package com.techhog.luauj.Ast;

public final class ParseOptions {
    public static enum Mode {
        NoCheck,    // Do not perform any inference
        Nonstrict,  // Unannotated symbols are any
        Strict,     // Unannotated symbols are inferred
        Definition, // Type definition module, has special parsing rules
    };

    public final boolean allow_type_annotations;
    public final boolean support_continue_statement;
    public final boolean allow_declaration_syntax;
    public final boolean capture_comments;

    public ParseOptions(boolean allow_type_annotations_in, boolean support_continue_statement_in, boolean allow_declaration_syntax_in, boolean capture_comments_in) {
        allow_type_annotations = allow_type_annotations_in;
        support_continue_statement = support_continue_statement_in;
        allow_declaration_syntax = allow_declaration_syntax_in;
        capture_comments = capture_comments_in;
    }
    public ParseOptions() {
        allow_type_annotations = true;
        support_continue_statement = true;
        allow_declaration_syntax = false;
        capture_comments = false;
    }
}
