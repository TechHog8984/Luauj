package com.techhog.luauj.Ast;

import org.junit.jupiter.api.Test;

import com.techhog.luauj.Ast.Lexer.AstNameTable;
import com.techhog.luauj.Ast.Lexer.Lexeme;

public class LexerTests {
    private Lexer lex(final String test_input){
        final AstNameTable table = new AstNameTable();
        final Lexer lexer = new Lexer(test_input, test_input.length(), table);
        return lexer;
    }

    @Test
    public void broken_string_works() {
        final Lexeme lexeme = lex("[[").next();
        assert lexeme.type == Lexeme.Type.BrokenString;
        assert lexeme.location.equals(new Location(new Position(0, 0), new Position(0, 2)));
    }

    @Test
    public void broken_comment() {
        final Lexeme lexeme = lex("--[[  ").next();
        assert lexeme.type == Lexeme.Type.BrokenComment;
        assert lexeme.location.equals(new Location(new Position(0, 0), new Position(0, 6)));
    }

    @Test
    public void broken_comment_kept() {
        final Lexer lexer = lex("--[[  ");
        lexer.setSkipComments(true);
        assert lexer.next().type == Lexeme.Type.BrokenComment;
    }

    @Test
    public void comment_skipped() {
        final Lexer lexer = lex("--  ");
        lexer.setSkipComments(true);
        assert lexer.next().type == Lexeme.Type.Eof;
    }

    @Test
    public void multilineCommentWithLexemeInAndAfter() {
        final Lexer lexer = lex("--[[ function \n" +
                                "]] end");

        final Lexeme comment = lexer.next();
        final Lexeme end = lexer.next();

        assert comment.type == Lexeme.Type.BlockComment;
        assert comment.location.equals(new Location(new Position(0, 0), new Position(1, 2)));
        assert end.type == Lexeme.Type.ReservedEnd;
        assert end.location.equals(new Location(new Position(1, 3), new Position(1, 6)));
    }

    @Test
    public void testBrokenEscapeTolerant() {
        final String test_input = "'\\3729472897292378'";
        final Lexeme item = lex(test_input).next();

        assert item.type == Lexeme.Type.QuotedString;
        assert item.location.equals(
            new Location(
                new Position(0, 0),
                new Position(0, test_input.length())
            )
        );
    }

    @Test
    public void testBigDelimiters() {
        final Lexeme item = lex("--[===[\n" +
        "\n" +
        "\n" +
        "\n" +
        "]===]").next();

        assert item.type == Lexeme.Type.BlockComment;
        assert item.location.equals(new Location(new Position(0, 0), new Position(4, 5)));
    }

    @Test
    public void lookAhead() {
        final Lexer lexer = lex("foo --[[ comment ]] bar : nil end");
        lexer.setSkipComments(true);
        lexer.next(); // must call next() before reading data from lexer at least once

        assert lexer.current().type == Lexeme.Type.Name;
        assert lexer.current().name.orElse("").equals("foo");
        assert lexer.lookAhead().type == Lexeme.Type.Name;
        assert lexer.lookAhead().name.orElse("").equals("bar");

        lexer.next();

        assert lexer.current().type == Lexeme.Type.Name;
        assert lexer.current().name.orElse("").equals("bar");
        assert lexer.lookAhead().type.ch == ':';

        lexer.next();

        assert lexer.current().type.ch == ':';
        assert lexer.lookAhead().type == Lexeme.Type.ReservedNil;

        lexer.next();

        assert lexer.current().type == Lexeme.Type.ReservedNil;
        assert lexer.lookAhead().type == Lexeme.Type.ReservedEnd;

        lexer.next();

        assert lexer.current().type == Lexeme.Type.ReservedEnd;
        assert lexer.lookAhead().type == Lexeme.Type.Eof;

        lexer.next();

        assert lexer.current().type == Lexeme.Type.Eof;
        assert lexer.lookAhead().type == Lexeme.Type.Eof;
    }

    @Test
    public void string_interpolation_basic() {
        final Lexer lexer = lex("`foo {\"bar\"}`");

        final Lexeme interp_begin = lexer.next();
        assert interp_begin.type == Lexeme.Type.InterpStringBegin;

        final Lexeme quote = lexer.next();
        assert quote.type == Lexeme.Type.QuotedString;

        final Lexeme interp_end = lexer.next();
        assert interp_end.type == Lexeme.Type.InterpStringEnd;
    }

    @Test
    public void string_interpolation_full() {
        final Lexer lexer = lex("`foo {\"bar\"} {\"baz\"} end`");

        Lexeme interpBegin = lexer.next();
        assert interpBegin.type == Lexeme.Type.InterpStringBegin;
        assert interpBegin.toString().equals("`foo {");

        Lexeme quote1 = lexer.next();
        assert quote1.type == Lexeme.Type.QuotedString;
        assert quote1.toString().equals("\"bar\"");

        Lexeme interpMid = lexer.next();
        assert interpMid.type == Lexeme.Type.InterpStringMid;
        assert interpMid.toString().equals("} {");

        Lexeme quote2 = lexer.next();
        assert quote2.type == Lexeme.Type.QuotedString;
        assert quote2.toString().equals("\"baz\"");

        Lexeme interpEnd = lexer.next();
        assert interpEnd.type == Lexeme.Type.InterpStringEnd;
        assert interpEnd.toString().equals("} end`");
    }

    @Test
    public void string_interpolation_double_brace() {
        final Lexer lexer = lex("`foo{{bad}}bar`");

        final Lexeme broken_interp_begin = lexer.next();
        assert broken_interp_begin.type == Lexeme.Type.BrokenInterpDoubleBrace;
        assert broken_interp_begin.data.orElse("").equals("foo");

        assert lexer.next().type == Lexeme.Type.Name;

        final Lexeme interp_end = lexer.next();
        assert interp_end.type == Lexeme.Type.InterpStringEnd;
        assert interp_end.data.orElse("").equals("}bar");
    }

    @Test
    public void string_interpolation_double_but_unmatched_brace() {
        final Lexer lexer = lex("`{{oops}`, 1");

        assert lexer.next().type == Lexeme.Type.BrokenInterpDoubleBrace;
        assert lexer.next().type == Lexeme.Type.Name;
        assert lexer.next().type == Lexeme.Type.InterpStringEnd;
        assert lexer.next().type.ch == ',';
        assert lexer.next().type == Lexeme.Type.Number;
    }

    // TODO: this test fails :(
    // @Test
    // public void string_interpolation_unmatched_brace() {
    //     final Lexer lexer = lex("{" +
    //     "   `hello {\"world\"}" +
    //     "} -- this might be incorrectly parsed as a string");

    //     assert lexer.next().type.ch == '{';
    //     assert lexer.next().type == Lexeme.Type.InterpStringBegin;
    //     assert lexer.next().type == Lexeme.Type.QuotedString;
    //     assert lexer.next().type == Lexeme.Type.BrokenString;
    //     assert lexer.next().type.ch == '}';
    // }

    @Test
    public void string_interpolation_with_unicode_escape() {
        final Lexer lexer = lex("`\\u{1F41B}`");

        assert lexer.next().type == Lexeme.Type.InterpStringSimple;
        assert lexer.next().type == Lexeme.Type.Eof;
    }
}
