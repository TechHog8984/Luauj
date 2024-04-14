package com.techhog.luauj.Ast;

import java.util.Vector;

import com.techhog.luauj.Ast.Ast.AstStatBlock;
import com.techhog.luauj.Ast.Lexer.Lexeme;

public final class ParseResult {
    public static final String kParseNameError = "%error-id%";
    public static final class ParseError extends Exception {
        public static void raise(Location location, String format, Object... args) throws ParseError {
            throw new ParseError(location, String.format(format, args));
        }

        private final Location location;
        private final String message;

        public ParseError(Location location_in, String message_in) {
            location = location_in;
            message = message_in;
        }

        public String what() {
            return message;
        }

        public Location getLocation() {
            return location;
        }

        public String getMessage() {
            return message;
        }
    }

    public static final class ParseErrors {
        private final Vector<ParseError> errors;
        private final String message;

        public ParseErrors(Vector<ParseError> errors_in) {
            errors = errors_in;
            assert !errors.isEmpty();

            if (errors.size() == 1) {
                message = errors.firstElement().what();
            } else {
                message = errors.size() + " parse errors";
            }
        }

        public String what() {
            return message;
        }
        public Vector<ParseError> getErrors() {
            return errors;
        }
    }

    public static final class HotComment {
        public final boolean header;
        public final Location location;
        public final String content;

        public HotComment(boolean header_in, Location location_in, String content_in) {
            header = header_in;
            location = location_in;
            content = content_in;
        }
    }

    public static final class Comment {
        public final Lexeme.Type type; // Comment, BlockComment, or BrokenComment
        public final Location location;

        public Comment(Lexeme.Type type_in, Location location_in) {
            type = type_in;
            location = location_in;
        }
    }

    public final AstStatBlock root;
    public final int lines;

    public final Vector<HotComment> hotcomments;
    public final Vector<ParseError> errors;

    public final Vector<Comment> comment_locations;

    public ParseResult(AstStatBlock root_in, int lines_in, Vector<HotComment> hotcomments_in, Vector<ParseError> errors_in, Vector<Comment> comment_locations_in) {
        root = root_in;
        lines = lines_in;

        hotcomments = hotcomments_in;
        errors = errors_in;

        comment_locations = comment_locations_in;
    }
    public ParseResult(AstStatBlock root_in, Vector<HotComment> hotcomments_in, Vector<ParseError> errors_in, Vector<Comment> comment_locations_in) {
        this(root_in, 0, hotcomments_in, errors_in, comment_locations_in);
    }
}
