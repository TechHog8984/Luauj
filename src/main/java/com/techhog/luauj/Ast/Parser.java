package com.techhog.luauj.Ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.NoSuchElementException;
import java.util.Optional;
import java.util.Vector;

import com.techhog.luauj.Ast.Ast.AstArray;
import com.techhog.luauj.Ast.Ast.AstDeclaredClassProp;
import com.techhog.luauj.Ast.Ast.AstExpr;
import com.techhog.luauj.Ast.Ast.AstExprConstantNumber;
import com.techhog.luauj.Ast.Ast.AstExprConstantString;
import com.techhog.luauj.Ast.Ast.AstExprError;
import com.techhog.luauj.Ast.Ast.AstExprTable;
import com.techhog.luauj.Ast.Ast.AstGenericType;
import com.techhog.luauj.Ast.Ast.AstGenericTypePack;
import com.techhog.luauj.Ast.Ast.AstLocal;
import com.techhog.luauj.Ast.Ast.AstName;
import com.techhog.luauj.Ast.Ast.AstStat;
import com.techhog.luauj.Ast.Ast.AstStatError;
import com.techhog.luauj.Ast.Ast.AstTableProp;
import com.techhog.luauj.Ast.Ast.AstType;
import com.techhog.luauj.Ast.Ast.AstTypeError;
import com.techhog.luauj.Ast.Ast.AstTypeOrPack;
import com.techhog.luauj.Ast.Ast.ConstantNumberParseResult;
import com.techhog.luauj.Ast.Lexer.AstNameTable;
import com.techhog.luauj.Ast.Lexer.Lexeme;
import com.techhog.luauj.Ast.Lexer.StringHolder;
import com.techhog.luauj.Ast.ParseResult.Comment;
import com.techhog.luauj.Ast.ParseResult.HotComment;
import com.techhog.luauj.Ast.ParseResult.ParseError;

public final class Parser {
    private static final int LuauRecursionLimit = 1000;
    private static final int LuauParseErrorLimit = 100;
    private static final String ERROR_INVALID_INTERP_DOUBLE_BRACE =  "Double braces are not permitted within interpolated strings. Did you mean '\\{'?";

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

    public static final class TempVector<T> {
        private final Vector<T> storage;
        private int offset;
        private int size_;

        public TempVector(Vector<T> storage_in) {
            storage = storage_in;
            offset = storage.size();
            size_ = 0;
        }

        public T get(int index) {
            if (index >= size_)
                throw new ArrayIndexOutOfBoundsException(index);

            return storage.get(offset + index);
        }

        public T firstElement() {
            if (size_ == 0)
                throw new NoSuchElementException();

            return storage.get(offset);
        }

        public T lastElement() {
            if (size_ == 0)
                throw new NoSuchElementException();

            return storage.lastElement();
        }

        public boolean isEmpty() {
            return size_ == 0;
        }

        public int size() {
            return size_;
        }

        public void add(T item) {
            assert storage.size() == offset + size_;
            storage.add(item);
            size_++;
        }
    }

    private static final class DoublePointer {
        public double value;

        public DoublePointer(double value_in) {
            value = value_in;
        }
    }

    private final ParseOptions options;

    private final Lexer lexer;

    private final Vector<Comment> comment_locations = new Vector<>();
    private final Vector<HotComment> hotcomments = new Vector<>();

    private boolean hotcomment_header = true;

    private int recursion_counter;

    private final AstName name_self;
    private final AstName name_number;
    private final AstName name_error;
    private final AstName name_nil;

    private MatchLexeme end_mismatch_suspect;

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

    private Optional<AstArray<Character>> parseCharArray() {
        assert lexer.current().type == Lexeme.Type.QuotedString || lexer.current().type == Lexeme.Type.RawString ||
            lexer.current().type == Lexeme.Type.InterpStringSimple;

        assignStringBuilder(scratch_data, lexer.current().data.get());

        final StringHolder holder = new StringHolder(scratch_data.toString());
        if (lexer.current().type == Lexeme.Type.QuotedString || lexer.current().type == Lexeme.Type.InterpStringSimple) {
            if (!Lexer.fixupQuotedString(holder)) {
                nextLexeme();
                return Optional.empty();
            }
            assignStringBuilder(scratch_data, holder.string);
        } else {
            Lexer.fixupMultilineString(holder);
            assignStringBuilder(scratch_data, holder.string);
        }

        final AstArray<Character> value = copy(scratch_data.toString());
        nextLexeme();

        return Optional.of(value);
    }

    private AstExpr parseString() throws ParseError {
        final Location location = lexer.current().location;
        final Optional<AstArray<Character>> value = parseCharArray();
        if (value.isPresent())
            return new AstExprConstantString(location, value.get());
        else
            return reportExprError(location, new AstArray<AstExpr>(new AstExpr[] {}), "String literal contains malformed escape sequence");
    }

    private void assignStringBuilder(StringBuilder builder, String data) {
        builder.delete(0, builder.length()); // clear
        builder.append(data);
    }

    private AstExpr parseInterpString() {
        final TempVector<AstArray<Character>> strings = new TempVector<>(scratch_string);
        final TempVector<AstExpr> expressions = new TempVector<>(scratch_expr);

        final Location start_location = lexer.current().location;
        Location end_location;

        do {
            final Lexeme current_lexeme = lexer.current();
            assert current_lexeme.type == Lexeme.Type.InterpStringBegin || current_lexeme.type == Lexeme.Type.InterpStringMid ||
                current_lexeme.type == Lexeme.Type.InterpStringEnd || current_lexeme.type == Lexeme.Type.InterpStringSimple;

            end_location = current_lexeme.location;

            assignStringBuilder(scratch_data, current_lexeme.data.get());

            final StringHolder scratch_data_holder = new StringHolder(scratch_data.toString());
            if (!Lexer.fixupQuotedString(scratch_data_holder)) {
                nextLexeme();
                return reportExprError(new Location(start_location, end_location), new AstArray<AstExpr>(new AstExpr[] {}), "Interpolated string literal contains malformed escape sequence");
            }
            assignStringBuilder(scratch_data, scratch_data_holder.string);

            final AstArray<Character> chars = copy(scratch_data.toString());

            nextLexeme();

            strings.add(chars);

            if (current_lexeme.type == Lexeme.Type.InterpStringEnd || current_lexeme.type == Lexeme.Type.InterpStringSimple)
                break;

            boolean error_while_checking = false;

            Lexeme.Type current_type = lexer.current().type;
            if (current_type == Lexeme.Type.InterpStringMid || current_type == Lexeme.Type.InterpStringEnd) {
                error_while_checking = true;
                nextLexeme();
                expressions.add(reportExprError(end_location, new AstArray<>(new AstExpr[]{}), "Malformed interpolated string, expected expression inside '{}'"));
            } else if (current_type == Lexeme.Type.BrokenString) {
                error_while_checking = true;
                nextLexeme();
                expressions.add(reportExprError(end_location, new AstArray<>(new AstExpr[]{}), "Malformed interpolated string, did you forget to add a '`'?"));
            } else {
                expressions.add(parseExpr());
            }

            if (error_while_checking)
                break;

            current_type = lexer.current().type;

            if (current_type == Lexeme.Type.InterpStringBegin || current_type == Lexeme.Type.InterpStringMid || current_type == Lexeme.Type.InterpStringEnd) {
            } else if (current_type == Lexeme.Type.BrokenInterpDoubleBrace) {
                nextLexeme();
                return reportExprError(end_location, new AstArray<>(new AstExpr[]{}), ERROR_INVALID_INTERP_DOUBLE_BRACE);
            } else if (current_type == Lexeme.Type.BrokenString) {
                nextLexeme();
                return reportExprError(end_location, new AstArray<>(new AstExpr[]{}), "Malformed interpolated string, did you forget to add a '}'?");
            } else {
                return reportExprError(end_location, new AstArray<>(new AstExpr[]{}), "Malformed interpolated string, got %s", lexer.current().toString());
            }
        } while (true);

        final AstArray<AstArray<Character>> strings_array = copy(strings);
    }

    private AstExpr parseNumber() {
        final Location start = lexer.current().location;

        assignStringBuilder(scratch_data, lexer.current().data.get());

        // Remove all internal _ - they don't hold any meaning and this allows parsing code to just pass the string pointer to strtod et al
        // int index = scratch_data.toString().indexOf('_');
        int underscore_index;
        while (true) {
            underscore_index = scratch_data.toString().indexOf('_');
            if (underscore_index == -1)
                break;

            scratch_data.deleteCharAt(underscore_index);
        }

        final DoublePointer value = new DoublePointer(0);
        final ConstantNumberParseResult result = parseDouble(value, scratch_data);
        nextLexeme();

        if (result == ConstantNumberParseResult.Malformed)
            return reportExprError(start, new AstArray<>(new AstExpr[]{}), "Maliformed number");

        return new AstExprConstantNumber(start, value.value, result);
    }

    private AstLocal pushLocal(Binding binding) {
        final Name name = binding.name;
        AstLocal local = local_map.get(name.name);

        local = new AstLocal(name.name, name.location, Optional.of(local), function_stack.size() - 1, function_stack.lastElement().loop_depth, binding.annotation.get());

        local_stack.add(local);

        return local;
    }

    private int saveLocals() {
        return Lexer.unsigned(local_stack.size());
    }

    private void restoreLocals(int offset) {
        for (int i = local_stack.size(); i > offset; --i) {
            final AstLocal l = local_stack.get(i - 1);

            local_map.put(l.name, l.shadow.get());
        }

        local_stack.setSize(offset);
    }

    private boolean expectAndConsume(char value, String context) throws ParseError {
        return expectAndConsume(Lexeme.Type.get(value), context);
    }

    private boolean expectAndConsume(Lexeme.Type type, String context) throws ParseError {
        if (lexer.current().type != type) {
            expectAndConsumeFail(type, Optional.of(context));

            // check if this is an extra token and the expected token is next
            if (lexer.lookAhead().type == type) {
                // skip invalid and consume expected
                nextLexeme();
                nextLexeme();
            }

            return false;
        } else {
            nextLexeme();
            return true;
        }
    }

    private void expectAndConsumeFail(Lexeme.Type type, Optional<String> context) throws ParseError {
        final String type_string = new Lexeme(new Location(new Position(0, 0), 0), type).toString();
        final String current_lexeme_string = lexer.current().toString();

        if (context.isPresent())
            report(lexer.current().location, "Expected %s when parsing %s, got %s", type_string, context.get(), current_lexeme_string);
        else
            report(lexer.current().location, "Expected %s, got %s", type_string, current_lexeme_string);
    }

    private boolean expectMatchAndConsume(char value, MatchLexeme begin, boolean search_for_missing) throws ParseError {
        final Lexeme.Type type = Lexeme.Type.get(value);

        if (lexer.current().type != type) {
            expectMatchAndConsumeFail(type, begin, Optional.empty());

            return expectMatchAndConsumeRecover(value, begin, search_for_missing);
        } else {
            nextLexeme();

            return true;
        }
    }

    private boolean expectMatchAndConsumeRecover(char value, MatchLexeme begin, boolean search_for_missing) {
        final Lexeme.Type type = Lexeme.Type.get(value);

        if (search_for_missing) {
            // previous location is taken because 'current' lexeme is already the next token
            final int current_line = Lexer.unsigned(lexer.previousLocation().end.line);

            // search to the end of the line for expected token
            // we will also stop if we hit a token that can be handled by parsing function above the current one
            Lexeme.Type lexemeType = lexer.current().type;

            while (current_line == lexer.current().location.begin.line && lexemeType != type && match_recovery_stop_on_token.get(lexemeType.index) == 0) {
                nextLexeme();
                lexemeType = lexer.current().type;
            }

            if (lexemeType == type) {
                nextLexeme();

                return true;
            }
        } else {
            // check if this is an extra token and the expected token is next
            if (lexer.lookAhead().type == type) {
                // skip invalid and consume expected
                nextLexeme();
                nextLexeme();

                return true;
            }
        }

        return false;
    }

    private void expectMatchAndConsumeFail(Lexeme.Type type, MatchLexeme begin, Optional<String> extra) throws ParseError {
        final String type_string = new Lexeme(new Location(new Position(0, 0), 0), type).toString();
        final String match_string = new Lexeme(new Location(new Position(0, 0), 0), begin.type).toString();

        if (lexer.current().location.begin.line == begin.position.line)
            report(lexer.current().location, "Expected %s (to close %s at column %d), got %s%s", type_string, match_string,
                begin.position.column + 1, lexer.current().toString(), extra.orElse(""));
        else
            report(lexer.current().location, "Expected %s (to close %s at line %d), got %s%s", type_string, match_string,
                begin.position.line + 1, lexer.current().toString(), extra.orElse(""));
    }

    private boolean expectMatchEndAndConsume(Lexeme.Type type, MatchLexeme begin) throws ParseError {
        if (lexer.current().type != type) {
            expectMatchEndAndConsumeFail(type, begin);

            // check if this is an extra token and the expected token is next
            if (lexer.lookAhead().type == type) {
                // skip invalid and consume expected
                nextLexeme();
                nextLexeme();

                return true;
            }

            return false;
        } else {
            // If the token matches on a different line and a different column, it suggests misleading indentation
            // This can be used to pinpoint the problem location for a possible future *actual* mismatch
            if (lexer.current().location.begin.line != begin.position.line && lexer.current().location.begin.column != begin.position.column &&
            end_mismatch_suspect.position.line < begin.position.line) // Only replace the previous suspect with more recent suspects
                end_mismatch_suspect = begin;

            nextLexeme();

            return true;
        }
    }

    private void expectMatchEndAndConsumeFail(Lexeme.Type type, MatchLexeme begin) throws ParseError {
        if (end_mismatch_suspect.type != Lexeme.Type.Eof && end_mismatch_suspect.position.line > begin.position.line) {
            final String match_string = new Lexeme(new Location(new Position(0, 0), 0), end_mismatch_suspect.type).toString();
            final String suggestion = String.format("; did you forget to close %s at line %d?", match_string, end_mismatch_suspect.position.line + 1);

            expectMatchAndConsumeFail(type, begin, Optional.of(suggestion));
        } else {
            expectMatchAndConsumeFail(type, begin, Optional.empty());
        }
    }

    // private <T> AstArray<T> copy(T data, int size) {
    //     final AstArray<T> result = new AstArray<>();

    //     result.data = size > 0 ? ((T[]) new T[]{}) : null;
    // }

    // private AstArray<Character> copy(String data) {
    //     final AstArray<Character> result = copy(data, data.length() + 1);

    //     result.size = data.length();

    //     return result;
    // }

    @SuppressWarnings("unchecked")
    private <T> AstArray<T> copy(TempVector<T> data) {
        final ArrayList<T> array_list = new ArrayList<>();
        for (int i = 0; i < data.size(); i++) {
            array_list.add(data.get(i));
        }

        return new AstArray<>((T[]) array_list.toArray());
    }

    private AstArray<Character> copy(String data) {
        final ArrayList<Character> array_list = new ArrayList<>();
        final char[] ch = data.toCharArray();
        for (int i = 0; i < ch.length; i++) {
            array_list.add(ch[i]);
        }

        return new AstArray<Character>((Character[]) array_list.toArray());
    }

    private void incrementRecursionCounter(String context) throws ParseError {
        recursion_counter++;

        if (recursion_counter > Lexer.unsigned(LuauRecursionLimit))
            ParseError.raise(lexer.current().location, "Exceeded allowed recursion depth; simplify your %s to make the code compile", context);
    }

    @SuppressWarnings("unused")
    private void report(Location location, String format, Object... args) throws ParseError {
        // To reduce number of errors reported to user for incomplete statements, we skip multiple errors at the same location
        // For example, consider 'local a = (((b + ' where multiple tokens haven't been written yet

        if (!parse_errors.isEmpty() && location == parse_errors.lastElement().getLocation())
            return;

        String message = String.format(format, args);

        // when limited to a single error, behave as if the error recovery is disabled
        if (LuauParseErrorLimit == 1) {
            throw new ParseError(location, message);
        }

        parse_errors.add(new ParseError(location, message));

        if (parse_errors.size() >= Lexer.unsigned(LuauParseErrorLimit))
            ParseError.raise(location, "Reached error limit (%d)", LuauParseErrorLimit);
    }

    private void reportNameError(Optional<String> context) throws ParseError {
        if (context.isPresent())
            report(lexer.current().location, "Expected identifier when parsing %s, got %s", context.get(), lexer.current().toString());
        else
            report(lexer.current().location, "Expected identifier when parsing, got %s", lexer.current().toString());
    }

    private AstStatError reportStatError(Location location, AstArray<AstExpr> expressions, AstArray<AstStat> statements, String format, Object... args) throws ParseError {
        report(location, String.format(format, args));

        return new AstStatError(location, expressions, statements, Lexer.unsigned(parse_errors.size() - 1));
    }


    private AstExprError reportExprError(Location location, AstArray<AstExpr> expressions, String format, Object... args) throws ParseError {
        report(location, String.format(format, args));

        return new AstExprError(location, expressions, Lexer.unsigned(parse_errors.size() - 1));
    }

    private AstTypeError reportTypeError(Location location, AstArray<AstType> types, String format, Object... args) throws ParseError {
        report(location, String.format(format, args));

        return new AstTypeError(location, types, false, Lexer.unsigned(parse_errors.size() - 1));
    }

    private AstTypeError reportMissingTypeError(Location parse_error_location, Location ast_error_location, String format, Object... args) throws ParseError {
        report(parse_error_location, String.format(format, args));

        return new AstTypeError(ast_error_location, new AstArray<AstType>(new AstType[]{}), true, Lexer.unsigned(parse_errors.size() - 1));
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
