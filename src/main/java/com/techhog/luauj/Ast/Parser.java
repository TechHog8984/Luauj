package com.techhog.luauj.Ast;

import java.util.HashMap;
import java.util.Optional;
import java.util.Vector;

import com.techhog.luauj.Ast.Ast.AstArray;
import com.techhog.luauj.Ast.Ast.AstDeclaredClassProp;
import com.techhog.luauj.Ast.Ast.AstExpr;
import com.techhog.luauj.Ast.Ast.AstExprTable;
import com.techhog.luauj.Ast.Ast.AstGenericType;
import com.techhog.luauj.Ast.Ast.AstGenericTypePack;
import com.techhog.luauj.Ast.Ast.AstLocal;
import com.techhog.luauj.Ast.Ast.AstName;
import com.techhog.luauj.Ast.Ast.AstStat;
import com.techhog.luauj.Ast.Ast.AstTableProp;
import com.techhog.luauj.Ast.Ast.AstType;
import com.techhog.luauj.Ast.Ast.AstTypeOrPack;
import com.techhog.luauj.Ast.Lexer.AstNameTable;
import com.techhog.luauj.Ast.Lexer.Lexeme;
import com.techhog.luauj.Ast.ParseResult.Comment;
import com.techhog.luauj.Ast.ParseResult.HotComment;
import com.techhog.luauj.Ast.ParseResult.ParseError;

public final class Parser {
    public static final class MatchLexeme {
        public final Lexeme.Type type;
        public final Position position;

        public MatchLexeme(Lexeme l) {
            type = l.type;
            position = l.location.begin;
        }
    }

    public static final class Function {
        public boolean vararg;
        public final int loop_depth;

        public Function(boolean vararg_in, int loop_depth_in) {
            vararg = vararg_in;
            loop_depth = loop_depth_in;
        }

        public Function() {
            vararg = false;
            loop_depth = 0;
        }
    }

    public static final class Local {
        public final Optional<AstLocal> local;
        public final int offset;

        public Local(AstLocal local_in, int offset_in) {
            local = Optional.of(local_in);
            offset = offset_in;
        }
        public Local() {
            local = Optional.empty();
            offset = 0;
        }
    }

    public static final class Name {
        public final AstName name;
        public final Location location;

        public Name(AstName name_in, Location location_in) {
            name = name_in;
            location = location_in;
        }
    }

    public static final class Binding {
        public final Name name;
        public final Optional<AstType> annotation;

        public Binding(Name name_in, AstType annotation_in) {
            name = name_in;
            annotation = Optional.of(annotation_in);
        }
        public Binding(Name name_in) {
            name = name_in;
            annotation = Optional.empty();
        }
    }

    private final ParseOptions options;

    private final Lexer lexer;

    private final Vector<Comment> comment_locations = new Vector<>();
    private final Vector<HotComment> hotcomments = new Vector<>();

    private boolean hotcomment_header = true;

    private final int recursion_counter;

    private final AstName name_self;
    private final AstName name_number;
    private final AstName name_error;
    private final AstName name_nil;

    private final MatchLexeme end_mismatch_suspect;

    private final Vector<Function> function_stack = new Vector<>();

    private final HashMap<AstName, AstLocal> local_map;
    private final Vector<AstLocal> local_stack = new Vector<>();

    private final Vector<ParseError> parse_errors = new Vector<>();

    private final Vector<Integer> match_recovery_stop_on_token = new Vector<>();

    private final Vector<AstStat> scratch_stat = new Vector<>();
    private final Vector<AstArray<Character>> scratch_string = new Vector<>();
    private final Vector<AstExpr> scratch_expr = new Vector<>();
    private final Vector<AstExpr> scratch_expr_aux = new Vector<>();
    private final Vector<AstName> scratch_name = new Vector<>();
    private final Vector<AstName> scratch_pack_name = new Vector<>();
    private final Vector<Binding> scratch_binding = new Vector<>();
    private final Vector<AstLocal> scratch_local = new Vector<>();
    private final Vector<AstTableProp> scratch_table_type_props = new Vector<>();
    private final Vector<AstType> scratch_type = new Vector<>();
    private final Vector<AstTypeOrPack> scratch_type_or_pack = new Vector<>();
    private final Vector<AstDeclaredClassProp> scratch_declared_class_props = new Vector<>();
    private final Vector<AstExprTable.Item> scratch_item = new Vector<>();
    private final Vector<Pair<AstName, Location>> scratch_arg_name = new Vector<>();
    private final Vector<AstGenericType> scratch_generic_types = new Vector<>();
    private final Vector<AstGenericTypePack> scratch_generic_type_packs = new Vector<>();
    private final Vector<Optional<Pair<AstName, Location>>> scratch_opt_arg_name = new Vector<>();
    private final StringBuilder scratch_data = new StringBuilder();

    public Parser(String buffer_in, int buffer_size_in, AstNameTable names, ParseOptions options_in) {
        options = options_in;
        lexer = new Lexer(buffer_in, buffer_size_in, names);
        recursion_counter = 0;
        end_mismatch_suspect = new MatchLexeme(new Lexeme(new Location(), Lexeme.Type.Eof));
        local_map = new HashMap<>();

        Function top = new Function();
        top.vararg = true;

        function_stack.ensureCapacity(8);
        function_stack.add(top);

        name_self = names.addStatic("self");
        name_number = names.addStatic("number");
        name_error = names.addStatic(ParseResult.kParseNameError);
        name_nil = names.getOrAdd("nil"); // nil is a reserved keyword

        match_recovery_stop_on_token.setSize(Lexeme.Type.Reserved_END.index);
        for (int i = 0; i < Lexeme.Type.Reserved_END.index; i++) {
            match_recovery_stop_on_token.setElementAt(0, i);
        }
        match_recovery_stop_on_token.setElementAt(1, Lexeme.Type.Eof.index);

        // required for lookahead() to work across a comment boundary and for nextLexeme() to work when captureComments is false
        lexer.setSkipComments(true);

        // read first lexeme (any hot comments get .header = true)
        assert hotcomment_header;
        nextLexeme();

        // all hot comments parsed after the first non-comment lexeme are special in that they don't affect type checking / linting mode
        hotcomment_header = false;

        // preallocate some buffers that are very likely to grow anyway; this works around std::vector's inefficient growth policy for small arrays
        local_stack.ensureCapacity(16);
        scratch_stat.ensureCapacity(16);
        scratch_expr.ensureCapacity(16);
        scratch_local.ensureCapacity(16);
        scratch_binding.ensureCapacity(16);
    }

    public void nextLexeme() {
        Lexeme.Type type = lexer.next( false, true).type;

        while (type == Lexeme.Type.BrokenComment || type == Lexeme.Type.Comment || type == Lexeme.Type.BlockComment) {
            final Lexeme lexeme = lexer.current();

            if (options.capture_comments)
                comment_locations.add(new Comment(lexeme.type, lexeme.location));

            // Subtlety: Broken comments are weird because we record them as comments AND pass them to the parser as a lexeme.
            // The parser will turn this into a proper syntax error.
            if (lexeme.type == Lexeme.Type.BrokenComment)
                return;

            // Comments starting with ! are called "hot comments" and contain directives for type checking / linting / compiling
            if (lexeme.type == Lexeme.Type.Comment && lexeme.length > 0 && lexeme.data.get().charAt(0) == '!') {
                final String text = lexeme.data.get();

                int end = lexeme.length;
                while (end > 0 && Lexer.isSpace(text.charAt(end - 1))) {
                    --end;
                }

                hotcomments.add(new HotComment(hotcomment_header, lexeme.location, text.substring(1, end)));
            }

            type = lexer.next(false, false).type;
        }
    }
}
