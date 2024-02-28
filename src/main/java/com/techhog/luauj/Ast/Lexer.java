package com.techhog.luauj.Ast;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Vector;

import com.techhog.luauj.Ast.Ast.AstName;

public final class Lexer {
    private static String[] kReserved = {"and", "break", "do", "else", "elseif", "end", "false", "for", "function", "if", "in", "local", "nil", "not", "or",
    "repeat", "return", "then", "true", "until", "while"};

    public static final class Lexeme {
        public static final class Type {
            private static final Map<String, Type> NAME_TO_ITEM_MAP = new HashMap<>();
            private static final Map<Integer, Type> INDEX_TO_ITEM_MAP = new HashMap<>();
            private static final Map<Character, Type> CHAR_TO_ITEM_MAP = new HashMap<>();
            private static int last_index = 0;
            static {
                add("Eof", 0);
                for (char i = 1; i < 256; i++) {
                    add("char" + i, i);
                }
                add("Char_END", 256);

                add("Equal");
                add("LessEqual");
                add("GreaterEqual");
                add("NotEqual");
                add("Dot2");
                add("Dot3");
                add("SkinnyArrow");
                add("DoubleColon");

                add("InterpStringBegin");
                add("InterpStringMid");
                add("InterpStringEnd");
                // An interpolated string with no expressions (like `x`)
                add("InterpStringSimple");

                add("AddAssign");
                add("SubAssign");
                add("MulAssign");
                add("DivAssign");
                add("ModAssign");
                add("PowAssign");
                add("ConcatAssign");

                add("RawString");
                add("QuotedString");
                add("Number");
                add("Name");

                add("Comment");
                add("BlockComment");

                add("BrokenString");
                add("BrokenComment");
                add("BrokenUnicode");
                add("BrokenInterpDoubleBrace");

                add("Error");

                add("Reserved_BEGIN");
                add("ReservedAnd");
                add("ReservedBreak");
                add("ReservedDo");
                add("ReservedElse");
                add("ReservedElseif");
                add("ReservedEnd");
                add("ReservedFalse");
                add("ReservedFor");
                add("ReservedFunction");
                add("ReservedIf");
                add("ReservedIn");
                add("ReservedLocal");
                add("ReservedNil");
                add("ReservedNot");
                add("ReservedOr");
                add("ReservedRepeat");
                add("ReservedReturn");
                add("ReservedThen");
                add("ReservedTrue");
                add("ReservedUntil");
                add("ReservedWhile");
                add("Reserved_END");
            }
            
            public static final Type Eof = get("Eof");
            public static final Type Char_END = get("Char_END");

            public static final Type Equal = get("Equal");
            public static final Type LessEqual = get("LessEqual");
            public static final Type GreaterEqual = get("GreaterEqual");
            public static final Type NotEqual = get("NotEqual");
            public static final Type Dot2 = get("Dot2");
            public static final Type Dot3 = get("Dot3");
            public static final Type SkinnyArrow = get("SkinnyArrow");
            public static final Type DoubleColon = get("DoubleColon");

            public static final Type InterpStringBegin = get("InterpStringBegin");
            public static final Type InterpStringMid = get("InterpStringMid");
            public static final Type InterpStringEnd = get("InterpStringEnd");
            // An interpolated string with no expressions (like `x`)
            public static final Type InterpStringSimple = get("InterpStringSimple");

            public static final Type AddAssign = get("AddAssign");
            public static final Type SubAssign = get("SubAssign");
            public static final Type MulAssign = get("MulAssign");
            public static final Type DivAssign = get("DivAssign");
            public static final Type ModAssign = get("ModAssign");
            public static final Type PowAssign = get("PowAssign");
            public static final Type ConcatAssign = get("ConcatAssign");

            public static final Type RawString = get("RawString");
            public static final Type QuotedString = get("QuotedString");
            public static final Type Number = get("Number");
            public static final Type Name = get("Name");

            public static final Type Comment = get("Comment");
            public static final Type BlockComment = get("BlockComment");

            public static final Type BrokenString = get("BrokenString");
            public static final Type BrokenComment = get("BrokenComment");
            public static final Type BrokenUnicode = get("BrokenUnicode");
            public static final Type BrokenInterpDoubleBrace = get("BrokenInterpDoubleBrace");

            public static final Type Error = get("Error");

            public static final Type Reserved_BEGIN = get("Reserved_BEGIN");
            public static final Type ReservedAnd = get("ReservedAnd");
            public static final Type ReservedBreak = get("ReservedBreak");
            public static final Type ReservedDo = get("ReservedDo");
            public static final Type ReservedElse = get("ReservedElse");
            public static final Type ReservedElseif = get("ReservedElseif");
            public static final Type ReservedEnd = get("ReservedEnd");
            public static final Type ReservedFalse = get("ReservedFalse");
            public static final Type ReservedFor = get("ReservedFor");
            public static final Type ReservedFunction = get("ReservedFunction");
            public static final Type ReservedIf = get("ReservedIf");
            public static final Type ReservedIn = get("ReservedIn");
            public static final Type ReservedLocal = get("ReservedLocal");
            public static final Type ReservedNil = get("ReservedNil");
            public static final Type ReservedNot = get("ReservedNot");
            public static final Type ReservedOr = get("ReservedOr");
            public static final Type ReservedRepeat = get("ReservedRepeat");
            public static final Type ReservedReturn = get("ReservedReturn");
            public static final Type ReservedThen = get("ReservedThen");
            public static final Type ReservedTrue = get("ReservedTrue");
            public static final Type ReservedUntil = get("ReservedUntil");
            public static final Type ReservedWhile = get("ReservedWhile");
            public static final Type Reserved_END = get("Reserved_END");

            private static void add(String name, int index) {
                Type item = new Type(name, index);
                NAME_TO_ITEM_MAP.put(name, item);
                INDEX_TO_ITEM_MAP.put(index, item);

                last_index = index;
            }
            private static void add(String name) {
                add(name, last_index + 1);
            }
            private static void add(String name, int index, char ch) {
                Type item = new Type(name, index, ch);
                NAME_TO_ITEM_MAP.put(name, item);
                INDEX_TO_ITEM_MAP.put(index, item);
                CHAR_TO_ITEM_MAP.put(ch, item);

                last_index = index;
            }
            private static void add(String name, char ch) {
                add(name, last_index + 1, ch);
            }

            public final String name;
            public final int index;
            public char ch;

            public Type(String name_in, int index_in) {
                name = name_in;
                index = index_in;
            }
            public Type(String name_in, int index_in, char ch_in) {
                name = name_in;
                index = index_in;
                ch = ch_in;
            }

            public static final Type get(String name) {
                return NAME_TO_ITEM_MAP.get(name);
            }
            public static final Type get(int index) {
                return INDEX_TO_ITEM_MAP.get(index);
            }
            public static final Type get(char ch) {
                return CHAR_TO_ITEM_MAP.get(ch);
            }
        }

        public final Type type;
        public final Location location;
        public final int length; // TODO: possibly remove Lexeme.length

        public final Optional<String> data;
        public final Optional<String> name;
        public Optional<Integer> codepoint;

        public Lexeme(Location location_in, Type type_in) {
            location = location_in;
            type = type_in;
            length = 0;
            data = Optional.empty();
            name = Optional.empty();
            codepoint = Optional.empty();
        }

        public Lexeme(Location location_in, char character) {
            location = location_in;
            type = Type.get(character);
            length = 0;
            data = Optional.empty();
            name = Optional.empty();
            codepoint = Optional.empty();
        }

        public Lexeme(Location location_in, Type type_in, String data_in, int size_in) {
            location = location_in;
            type = type_in;
            length = size_in;
            data = Optional.of(data_in);
            name = Optional.empty();
            codepoint = Optional.empty();

            assert type == Type.RawString || type == Type.QuotedString || type == Type.InterpStringBegin || type == Type.InterpStringMid ||
                type == Type.InterpStringEnd || type == Type.InterpStringSimple || type == Type.BrokenInterpDoubleBrace || type == Type.Number ||
                type == Type.Comment || type == Type.BlockComment;
        }

        public Lexeme(Location location_in, Type type_in, String name_in) {
            location = location_in;
            type = type_in;
            length = 0;
            data = Optional.empty();
            name = Optional.of(name_in);
            codepoint = Optional.empty();

            assert type == Type.Name || (type.index > Type.Reserved_BEGIN.index && type.index < Type.Reserved_END.index);
        }

        public void setCodepoint(int codepoint_in) {
            assert codepoint.isEmpty();

            codepoint = Optional.of(codepoint_in);
        }

        public String toString() {
            if (type == Type.Equal)
                return "<eof>";

            else if (type == Type.Equal)
                return "'=='";

            else if (type == Type.LessEqual)
                return "'<='";

            else if (type == Type.GreaterEqual)
                return "'>='";

            else if (type == Type.NotEqual)
                return "'~='";

            else if (type == Type.Dot2)
                return "'..'";

            else if (type == Type.Dot3)
                return "'...'";

            else if (type == Type.SkinnyArrow)
                return "'->'";

            else if (type == Type.DoubleColon)
                return "'::'";

            else if (type == Type.AddAssign)
                return "'+='";

            else if (type == Type.SubAssign)
                return "'-='";

            else if (type == Type.MulAssign)
                return "'*='";

            else if (type == Type.DivAssign)
                return "'/='";

            else if (type == Type.ModAssign)
                return "'%='";

            else if (type == Type.PowAssign)
                return "'^='";

            else if (type == Type.ConcatAssign)
                return "'..='";

            else if (type == Type.RawString ||
                    type == Type.QuotedString)
                return data.isPresent() ? String.format("\"%s\"", data.get()) : "string";

            else if (type == Type.InterpStringBegin)
                return data.isPresent() ? String.format("`%s{", data.get()) : "the beginning of an interpolated string";

            else if (type == Type.InterpStringMid)
                return data.isPresent() ? String.format("}%s{", data.get()) : "the middle of an interpolated string";

            else if (type == Type.InterpStringEnd)
                return data.isPresent() ? String.format("}%s`", data.get()) : "the end of an interpolated string";

            else if (type == Type.InterpStringSimple)
                return data.isPresent() ? String.format("`%s`", data.get()) : "interpolated string";

            else if (type == Type.Number)
                return data.isPresent() ? String.format("'%s'", data.get()) : "number";

            else if (type == Type.Name)
                return name.isPresent() ? String.format("'%s'", name.get()) : "identifier";

            else if (type == Type.Comment)
                return "comment";

            else if (type == Type.BrokenString)
                return "malformed string";

            else if (type == Type.BrokenComment)
                return "unfinished comment";

            else if (type == Type.BrokenInterpDoubleBrace)
                return "'{{', which is invalid (did you mean '\\{'?)";

            else if (type == Type.BrokenUnicode){
                if (codepoint.isPresent()) {
                    final Optional<String> confusable = Confusables.findConfusable(codepoint.get());
                    if (confusable.isPresent())
                        return String.format("Unicode character U+%x (did you mean '%s'?)", codepoint.get(), confusable.get());

                    return String.format("Unicode character U+%x", codepoint);
                } else {
                    return "invalid UTF-8 sequence";
                }
            }

            else {
                if (type.index < Type.Char_END.index)
                    return String.format("'%c'", type.ch);
                else if (type.index > Type.Reserved_BEGIN.index && type.index < Type.Reserved_END.index)
                    return String.format("'%s'", kReserved[type.index - Type.Reserved_BEGIN.index - 1]);
                else
                    return "<unknown>";
            }
        }
    }

    public static final class AstNameTable {
        private static final class Entry {
            public final AstName value;
            public final int length; // TODO: possibly remove AstNameTable.Entry.length
            public final Lexeme.Type type;

            public Entry(AstName value_in, int length_in, Lexeme.Type type_in) {
                value = value_in;
                length = length_in;
                type = type_in;
            }

            public boolean equals(Entry other) {
                return length == other.length && value.equals(other.value);
            }
        }
        private final List<Entry> data = new ArrayList<>(); // TODO: use fill or whatever it is to preallocate 128 entries (I think)

        public AstNameTable() {
            assert kReserved.length == (Lexeme.Type.Reserved_END.index - Lexeme.Type.Reserved_BEGIN.index - 1);

            for (int i = Lexeme.Type.Reserved_BEGIN.index; i < Lexeme.Type.Reserved_END.index - 1; ++i) {
                addStatic(kReserved[i - Lexeme.Type.Reserved_BEGIN.index], Lexeme.Type.get(i + 1));
            }
        }

        private boolean contains(Entry entry) {
            for (Entry e : data) {
                if (entry.equals(e))
                    return true;
            }
            return false;
        }
        private Optional<Entry> findEntry(Entry entry) {
            for (Entry e : data) {
                if (entry.equals(e))
                    return Optional.of(e);
            }
            return Optional.empty();
        }
        public AstName addStatic(String name, Lexeme.Type type) {
            Entry entry = new Entry(new AstName(name), name.length(), type);

            assert !contains(entry);
            data.add(entry);

            return entry.value;
        }
        public AstName addStatic(String name) {
            return addStatic(name, Lexeme.Type.Name);
        }

        public Pair<AstName, Lexeme.Type> getOrAddWithType(String name, int length) {
            final Entry entry = new Entry(new AstName(name), length, Lexeme.Type.Name);
            final Optional<Entry> entry_optional = findEntry(entry);
            if (entry_optional.isPresent())
                return new Pair<>(entry_optional.get().value, entry_optional.get().type);

            return new Pair<>(entry.value, entry.type);
        }

        public Pair<AstName, Lexeme.Type> getWithType(String name, int length) {
            final Optional<Entry> entry_optional = findEntry(new Entry(new AstName(name), length, Lexeme.Type.Eof));
            if (entry_optional.isPresent())
                return new Pair<>(entry_optional.get().value, entry_optional.get().type);

            return new Pair<>(new AstName(), Lexeme.Type.Name);
        }

        public AstName getOrAdd(String name) {
            return getOrAddWithType(name, name.length()).first;
        }
        public AstName get(String name) {
            return getWithType(name, name.length()).first;
        }
    }

    public static boolean isReserved(String word) {
        for (int i = Lexeme.Type.Reserved_BEGIN.index; i < Lexeme.Type.Reserved_END.index - 1; ++i) {
            if (word.equals(kReserved[i - Lexeme.Type.Reserved_BEGIN.index]))
                return true;
        }
        return false;
    }

    // works as a pointer to a String
    public static final class StringHolder {
        public String string;
        public StringHolder(String string_in) {
            string = string_in;
        }
    }
    public static int toUtf8(StringHolder holder, int code) {
        final int result;
        StringBuilder builder = new StringBuilder(holder.string);
        // U+0000..U+007F
        if (code < 0x80) {
            // data[0] = char(code);
            builder.setCharAt(0, (char) code);
            result = 1;
        }
        // U+0080..U+07FF
        else if (code < 0x800) {
            // data[0] = char(0xC0 | (code >> 6));
            // data[1] = char(0x80 | (code & 0x3F));
            builder.setCharAt(0, (char) (0xC0 | (code >> 6)));
            builder.setCharAt(1, (char) (0x80 | (code & 0x3F)));
            result = 2;
        }
        // U+0800..U+FFFF
        else if (code < 0x10000) {
            // data[0] = char(0xE0 | (code >> 12));
            // data[1] = char(0x80 | ((code >> 6) & 0x3F));
            // data[2] = char(0x80 | (code & 0x3F));
            builder.setCharAt(0, (char) (0xE0 | (code >> 12)));
            builder.setCharAt(1, (char) (0x80 | ((code >> 6) & 0x3F)));
            builder.setCharAt(2, (char) (0x80 | (code & 0x3F)));
            result = 3;
        }
        // U+10000..U+10FFFF
        else if (code < 0x110000) {
            // data[0] = char(0xF0 | (code >> 18));
            // data[1] = char(0x80 | ((code >> 12) & 0x3F));
            // data[2] = char(0x80 | ((code >> 6) & 0x3F));
            // data[3] = char(0x80 | (code & 0x3F));
            builder.setCharAt(0, (char) (0xF0 | (code >> 18)));
            builder.setCharAt(1, (char) (0x80 | ((code >> 12) & 0x3F)));
            builder.setCharAt(2, (char) (0x80 | ((code >> 6) & 0x3F)));
            builder.setCharAt(3, (char) (0x80 | (code & 0x3F)));
            result = 4;
        } else  { 
            return 0;
        }

        holder.string = builder.toString();
        return result;
    }

    public static boolean fixupQuotedString(StringHolder holder) {
        if (holder.string.isEmpty() || holder.string.indexOf('\\') == -1) {
            return true;
        }

        StringBuilder builder = new StringBuilder(holder.string);

        int length = holder.string.length();
        int write = 0;

        for (int i = 0; i < length;) {
            if (builder.charAt(i) != '\\') {
                builder.setCharAt(write++, builder.charAt(i));
                i++;
                continue;
            }

            if (i + 1 == length) {
                holder.string = builder.toString();
                return false;
            }

            final char escape = builder.charAt(i + 1);
            i += 2; // skip \e

            switch (escape) {
                case '\n':
                    builder.setCharAt(write++, '\n');
                    break;

                case '\r':
                    builder.setCharAt(write++, '\n');
                    if (i < length && builder.charAt(i) == '\n')
                        i++;
                    break;

                case 0:
                    holder.string = builder.toString();
                    return false;

                case 'x': {
                    // hex escape codes are exactly 2 hex digits long
                    if (i + 2 > length) {
                        holder.string = builder.toString();
                        return false;
                    }

                    int code = 0;

                    for (int j = 0; j < 2; ++j) {
                        char ch = builder.charAt(i + j);
                        if (!isHexDigit(ch)) {
                            holder.string = builder.toString();
                            return false;
                        }

                        // use or trick to convert to lower case
                        code = 16 * code + (isDigit(ch) ? ch - '0' : (ch | ' ') - 'a' + 10);
                    }

                    builder.setCharAt(write++, (char) code);
                    i += 2;
                    break;
                }

                case 'z': {
                    while (i < length && isSpace(builder.charAt(i)))
                        i++;
                    break;
                }

                case 'u': {
                    // unicode escape codes are at least 3 characters including braces
                    if (i + 3 > length) {
                        holder.string = builder.toString();
                        return false;
                    }

                    if (builder.charAt(i) != '{') {
                        holder.string = builder.toString();
                        return false;
                    }
                    i++;
                    if (builder.charAt(i) != '}') {
                        holder.string = builder.toString();
                        return false;
                    }

                    int code = 0;

                    for (int j = 0; j < 16; ++j) {
                        if (i == length) {
                            holder.string = builder.toString();
                            return false;
                        }

                        final char ch = builder.charAt(i);

                        if (ch == '}')
                            break;

                        if (!isHexDigit(ch)) {
                            holder.string = builder.toString();
                            return false;
                        }

                        // use or trick to convert to lower case
                        code = 16 * code + (isDigit(ch) ? ch - '0' : (ch | ' ') - 'a' + 10);
                        i++;
                    }

                    if (i == length || builder.charAt(i) != '}') {
                        holder.string = builder.toString();
                        return false;
                    }
                    i++;

                    holder.string = builder.toString();
                    final int utf8 = toUtf8(holder, code);
                    if (utf8 == 0) {
                        return false;
                    }

                    write += utf8;
                    break;
                }

                default: {
                    if (isDigit(escape)) {
                        int code = escape - '0';

                        for (int j = 0; j < 2; ++j) {
                            if (i == length || !isDigit(builder.charAt(i)))
                                break;

                            code = 10 * code + (builder.charAt(i) - '0');
                            i++;
                        }

                        if (code > 0xff) {
                            holder.string = builder.toString();
                            return false;
                        }

                        builder.setCharAt(write++, (char) code);
                    } else {
                        builder.setCharAt(write++, unescape(escape));
                    }
                }
            }
        }

        assert write <= length;

        holder.string = builder.toString();
        return true;
    }
    public static void fixupMultilineString(StringHolder holder) {
        if (holder.string.isEmpty())
            return;

        // Lua rules for multiline strings are as follows:
        // - standalone \r, \r\n, \n\r and \n are all considered newlines
        // - first newline in the multiline string is skipped
        // - all other newlines are normalized to \n

        // Since our lexer just treats \n as newlines, we apply a simplified set of rules that is sufficient to get normalized newlines for Windows/Unix:
        // - \r\n and \n are considered newlines
        // - first newline is skipped
        // - newlines are normalized to \n

        // This makes the string parsing behavior consistent with general lexing behavior - a standalone \r isn't considered a new line from the line
        // tracking perspective

        StringBuilder builder = new StringBuilder(holder.string);

        // skip leading newline
        if (builder.charAt(0) == '\r' && builder.charAt(1) == '\n')
            builder.delete(0, 2);
        else if (builder.charAt(0) == '\n')
            builder.delete(0, 1);

        // parse the rest of the string, converting newlines as we go
        int a = builder.length();
        while (a > 0) {
            if (builder.charAt(0) == '\r' && builder.charAt(1) == '\n') {
                builder.delete(0, 1);
                a -= 2;
            } else { // note: this handles \n by just writing it without changes
                a -= 1;
            }
        }

        holder.string = builder.toString();
    }

    public static boolean isSpace(char ch) {
        return ch == ' ' || ch == '\t' || ch == '\r' || ch == '\n' || ch == 11 || ch == '\f';
    }

    public static int unsigned(int a) {
        if (a < 0)
            return (int) Math.pow(2, 32);

        return a;
    }
    public static boolean isAlpha(char ch) {
        // use or trick to convert to lower case and unsigned comparison to do range check
        return unsigned((ch | ' ') - 'a') < 26;
    }

    public static boolean isDigit(char ch) {
        return unsigned(ch - '0') < 10;
    }

    public static boolean isHexDigit(char ch) {
        // use or trick to convert to lower case and unsigned comparison to do range check
        return unsigned(ch - '0') < 10 || unsigned((ch | ' ') - 'a') < 6;
    }

    public static boolean isNewLine(char ch) {
        return ch == '\n';
    }

    public static char unescape(char ch) {
        switch (ch) {
            case 'a':
                return 7;
            case 'b':
                return '\b';
            case 'f':
                return '\f';
            case 'n':
                return '\n';
            case 'r':
                return '\r';
            case 't':
                return '\t';
            case 'v':
                return 11;

            default:
                return ch;
        }
    }

    private static enum BraceType {
        InterpolatedString,
        Normal
    }

    private final String buffer;
    private final int buffer_size;

    private int offset;

    private int line;
    private int line_offset;

    private Lexeme lexeme;

    private Location previous_location;

    private final AstNameTable names;

    private boolean skip_comments;
    private boolean read_names;

    private final Vector<BraceType> brace_stack = new Vector<>();

    public Lexer(String buffer_in, int buffer_size_in, AstNameTable names_in) {
        buffer = buffer_in;
        buffer_size = buffer_size_in;
        offset = 0;
        line = 0;
        line_offset = 0;
        lexeme = new Lexeme(new Location(new Position(0,0), 0), Lexeme.Type.Eof);
        names = names_in;
        skip_comments = false;
        read_names = true;
    }

    public void setSkipComments(boolean skip) {
        skip_comments = skip;
    }
    public void setReadNames(boolean read) {
        read_names = read;
    }

    public Location previousLocation() {
        return previous_location;
    }

    public Lexeme next() {
        return next(skip_comments, true);
    }
    public Lexeme next(boolean skip_comments, boolean update_previous_location) {
        // in skipComments mode we reject valid comments
        do {
            // consume whitespace before the token
            while (isSpace(peekch()))
                consume();

            if (update_previous_location)
                previous_location = lexeme.location;

            lexeme = readNext();
            update_previous_location = false;
        } while (skip_comments && (lexeme.type == Lexeme.Type.Comment || lexeme.type == Lexeme.Type.BlockComment));

        return lexeme;
    }
    public void nextline() {
        while (peekch() != 0 && peekch() != '\r' && !isNewLine(peekch()))
            consume();

        next();
    }

    public Lexeme lookAhead() {
        final int current_offset = offset;
        final int current_line = line;
        final int current_line_offset = line_offset;
        Lexeme current_lexeme = lexeme;
        Location current_previous_location = previous_location;
    
        Lexeme result = next();

        offset = current_offset;
        line = current_line;
        line_offset = current_line_offset;
        lexeme = current_lexeme;
        previous_location = current_previous_location;

        return result;
    }

    public Lexeme current() {
        return lexeme;
    }

    private char peekch() {
        return (offset < buffer_size) ? buffer.charAt(offset) : 0;
    }
    private char peekch(int lookahead) {
        return (offset + lookahead < buffer_size) ? buffer.charAt(offset + lookahead) : 0;
    }

    private Position position() {
        return new Position(line, offset - line_offset);
    }

    private void consume() {
        if (isNewLine(buffer.charAt(offset))) {
            line++;
            line_offset = offset + 1;
        }

        offset++;
    }

    private Lexeme readCommentBody() {
        final Position start = position();

        assert peekch(0) == '-' && peekch(1) == '-';
        consume();
        consume();

        final int start_offset = offset;

        if (peekch() == '[') {
            int sep = skipLongSeparator();

            if (sep >= 0)
                return readLongString(start, sep, Lexeme.Type.BlockComment, Lexeme.Type.BrokenComment);
        }

        // fall back to single-line comment
        while (peekch() != 0 && peekch() != '\r' && !isNewLine(peekch()))
            consume();

        return new Lexeme(new Location(start, position()), Lexeme.Type.Comment, buffer.subSequence(start_offset, offset).toString(), offset - start_offset);
    }

        // Given a sequence [===[ or ]===], returns:
    // 1. number of equal signs (or 0 if none present) between the brackets
    // 2. -1 if this is not a long comment/string separator
    // 3. -N if this is a malformed separator
    // Does *not* consume the closing brace.
    private int skipLongSeparator() {
        final char start = peekch();

        assert start == '[' || start == ']';
        consume();

        int count = 0;

        while (peekch() == '=') {
            consume();
            count++;
        }

        return (start == peekch()) ? count : (-count) - 1;
    }

    private Lexeme readLongString(Position start, int sep, Lexeme.Type ok, Lexeme.Type broken) {
        // skip (second) [
        assert peekch() == '[';
        consume();

        final int start_offset = offset;

        while (peekch() != 0) {
            if (peekch() == ']') {
                if (skipLongSeparator() == sep) {
                    assert peekch() == ']';
                    consume(); // skip (second) ]

                    final int end_offset = offset - sep - 2;
                    assert end_offset >= start_offset;

                    return new Lexeme(new Location(start, position()), ok, buffer.subSequence(start_offset, end_offset).toString(), end_offset - start_offset);
                }
            } else {
                consume();
            }
        }

        return new Lexeme(new Location(start, position()), broken);
    }

    private void readBackslashInString() {
        assert peekch() == '\\';
        consume();
        switch (peekch()) {
            case '\r':
                consume();
                if (peekch() == '\n')
                    consume();
                break;

            case 0:
                break;

            case 'z':
                consume();
                while (isSpace(peekch()))
                    consume();
                break;

            default:
                consume();
        }
    }

    private Lexeme readQuotedString() {
        final Position start = position();

        char delimiter = peekch();
        assert delimiter == '\'' || delimiter == '"';
        consume();

        final int start_offset = offset;

        while (peekch() != delimiter) {
            switch (peekch()) {
                case 0:
                case '\r':
                case '\n':
                    return new Lexeme(new Location(start, position()), Lexeme.Type.BrokenString);

                case '\\':
                    readBackslashInString();
                    break;

                default:
                    consume();
            }
        }

        consume();

        return new Lexeme(new Location(start, position()), Lexeme.Type.QuotedString, buffer.subSequence(start_offset, offset - 1).toString(), offset - start_offset - 1);
    }

    private Lexeme readInterpolatedStringBegin() {
        assert peekch() == '`';

        final Position start = position();
        consume();

        return readInterpolatedStringSection(start, Lexeme.Type.InterpStringBegin, Lexeme.Type.InterpStringSimple);
    }
    private Lexeme readInterpolatedStringSection(Position start, Lexeme.Type format_type, Lexeme.Type end_type) {
        final int start_offset = offset;

        while (peekch() != '`') {
            switch (peekch()) {
                case 0:
                case '\r':
                case '\n':
                    return new Lexeme(new Location(start, position()), Lexeme.Type.BrokenString);

                case '\\':
                    // Allow for \ u{}, which would otherwise be consumed by looking for { 
                    //            ^ space is because java parses unicode even in COMMENTS (???????)

                    if (peekch(1) == 'u' && peekch(2) == '{') {
                        consume(); // backslash
                        consume(); // u
                        consume(); // {
                        break;
                    }

                    readBackslashInString();
                    break;

                case '{':
                    brace_stack.add(BraceType.InterpolatedString);

                    if (peekch(1) == '{') {
                        // TODO: double check buffer.subSequence stuff
                        final Lexeme broken_double_brace = new Lexeme(
                            new Location(start, position()),
                            Lexeme.Type.BrokenInterpDoubleBrace,
                            buffer.subSequence(start_offset, offset).toString(),
                            offset - start_offset
                        );
                        consume();
                        consume();

                        return broken_double_brace;
                    }

                    consume();
                    // TODO: double check buffer.subSequence stuff
                    return new Lexeme(new Location(start, position()), format_type, buffer.subSequence(start_offset, offset - 1).toString(), offset - start_offset - 1);

                default:
                    consume();
            }
        }

        consume();

        // TODO: double check buffer.subSequence stuff
        return new Lexeme(new Location(start, position()), end_type, buffer.subSequence(start_offset, offset - 1).toString(), offset - start_offset - 1);
    }

    private Lexeme readNumber(Position start, int start_offset) {
        assert isDigit(peekch());

        // This function does not do the number parsing - it only skips a number-like pattern.
        // It uses the same logic as Lua stock lexer; the resulting string is later converted
        // to a number with proper verification.
        do {
            consume();
        } while (isDigit(peekch()) || peekch() == '.' || peekch() == '_');

        if (peekch() == 'e' || peekch() == 'E') {
            consume();

            if (peekch() == '+' || peekch() == '-')
                consume();
        }

        while (isAlpha(peekch()) || isDigit(peekch()) || peekch() == '_')
            consume();

        // TODO: double check buffer.subSequence stuff
        return new Lexeme(new Location(start, position()), Lexeme.Type.Number, buffer.subSequence(start_offset, offset).toString(), offset - start_offset);
    }

    private Pair<AstName, Lexeme.Type> readName() {
        assert isAlpha(peekch()) || peekch() == '_';

        final int start_offset = offset;

        do {
            consume();
        } while (isAlpha(peekch()) || isDigit(peekch()) || peekch() == '_');

        // TODO: double check buffer.subSequence stuff
        return read_names ? names.getOrAddWithType(buffer.subSequence(start_offset, offset).toString(), offset - start_offset)
                            : names.getWithType(buffer.subSequence(start_offset, offset).toString(), offset - start_offset);
    }

    private Lexeme readNext() {
        final Position start = position();

        switch (peekch()) {
            case 0:
                return new Lexeme(new Location(start, 0), Lexeme.Type.Eof);

            case '-':
                if (peekch(1) == '>') {
                    consume();
                    consume();
                    return new Lexeme(new Location(start, 2), Lexeme.Type.SkinnyArrow);
                } else if (peekch(1) == '=') {
                    consume();
                    consume();
                    return new Lexeme(new Location(start, 2), Lexeme.Type.SubAssign);
                } else if (peekch(1) == '-') {
                    return readCommentBody();
                } else {
                    consume();
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get('-'));
                }

            case '[':
                int sep = skipLongSeparator();

                if (sep >= 0) {
                    return readLongString(start, sep, Lexeme.Type.RawString, Lexeme.Type.BrokenString);
                } else if (sep == -1) {
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get('['));
                } else {
                    return new Lexeme(new Location(start, position()), Lexeme.Type.BrokenString);
                }

            case '{':
                consume();

                if (!brace_stack.isEmpty())
                    brace_stack.add(BraceType.Normal);

                return new Lexeme(new Location(start, 1), Lexeme.Type.get('{'));

            case '}':
                consume();

                if (brace_stack.isEmpty())
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get('}'));

                final BraceType brace_stack_top = brace_stack.getLast();
                brace_stack.remove(brace_stack.size() - 1);

                if (brace_stack_top != BraceType.InterpolatedString)
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get('}'));

                return readInterpolatedStringSection(position(), Lexeme.Type.InterpStringMid, Lexeme.Type.InterpStringEnd);

            case '=':
                consume();

                if (peekch() == '=') {
                    consume();
                    return new Lexeme(new Location(start, 2), Lexeme.Type.Equal);
                } else
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get('='));

            case '<':
                consume();

                if (peekch() == '=') {
                    consume();
                    return new Lexeme(new Location(start, 2), Lexeme.Type.LessEqual);
                } else
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get('<'));

            case '>':
                consume();

                if (peekch() == '=') {
                    consume();
                    return new Lexeme(new Location(start, 2), Lexeme.Type.GreaterEqual);
                } else
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get('>'));

            case '~':
                consume();

                if (peekch() == '=') {
                    consume();
                    return new Lexeme(new Location(start, 2), Lexeme.Type.NotEqual);
                } else
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get('~'));

            case '"':
            case '\'':
                return readQuotedString();

            case '`':
                return readInterpolatedStringBegin();

            case '.':
                consume();

                if (peekch() == '.') {
                    consume();

                    if (peekch() == '.') {
                        consume();

                        return new Lexeme(new Location(start, 3), Lexeme.Type.Dot3);
                    }
                    else if (peekch() == '=') {
                        consume();

                        return new Lexeme(new Location(start, 3), Lexeme.Type.ConcatAssign);
                    }
                    else
                        return new Lexeme(new Location(start, 2), Lexeme.Type.Dot2);
                } else {
                    if (isDigit(peekch()))
                    {
                        return readNumber(start, offset - 1);
                    }
                    else
                        return new Lexeme(new Location(start, 1), '.');
                }

            case '+':
                consume();

                if (peekch() == '=') {
                    consume();
                    return new Lexeme(new Location(start, 2), Lexeme.Type.AddAssign);
                } else
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get('+'));

            case '/':
                consume();

                if (peekch() == '=') {
                    consume();
                    return new Lexeme(new Location(start, 2), Lexeme.Type.DivAssign);
                } else
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get('/'));

            case '*':
                consume();

                if (peekch() == '=') {
                    consume();
                    return new Lexeme(new Location(start, 2), Lexeme.Type.MulAssign);
                } else
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get('*'));

            case '%':
                consume();

                if (peekch() == '=') {
                    consume();
                    return new Lexeme(new Location(start, 2), Lexeme.Type.ModAssign);
                } else
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get('%'));

            case '^':
                consume();

                if (peekch() == '=') {
                    consume();
                    return new Lexeme(new Location(start, 2), Lexeme.Type.PowAssign);
                } else
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get('^'));

            case ':':
                consume();

                if (peekch() == ':') {
                    consume();
                    return new Lexeme(new Location(start, 2), Lexeme.Type.DoubleColon);
                } else 
                    return new Lexeme(new Location(start, 1), Lexeme.Type.get(':'));

            case '(':
            case ')':
            case ']':
            case ';':
            case ',':
            case '#':
            {
                char ch = peekch();
                consume();

                return new Lexeme(new Location(start, 1), Lexeme.Type.get(ch));
            }

            default:
                if (isDigit(peekch()))
                    return readNumber(start, offset);
                else if (isAlpha(peekch()) || peekch() == '_') {
                    final Pair<AstName, Lexeme.Type> name = readName();

                    return new Lexeme(new Location(start, position()), name.second, name.first.value);
                } else if ((peekch() & 0x80) != 0)
                    return readUtf8Error();
                else {
                    char ch = peekch();
                    consume();

                    return new Lexeme(new Location(start, 1), ch);
                }
        }
    }
    private Lexeme readUtf8Error() {
        final Position start = position();
        int codepoint = 0;
        int size = 0;

        if ((peekch() & 0b10000000) == 0b00000000) {
            size = 1;
            codepoint = peekch() & 0x7F;
        } else if ((peekch() & 0b11100000) == 0b11000000) { 
            size = 2;
            codepoint = peekch() & 0b11111;
        } else if ((peekch() & 0b11110000) == 0b11100000) {
            size = 3;
            codepoint = peekch() & 0b1111;
        } else if ((peekch() & 0b11111000) == 0b11110000) {
            size = 4;
            codepoint = peekch() & 0b111;
        } else {
            consume();
            return new Lexeme(new Location(start, position()), Lexeme.Type.BrokenUnicode);
        }

        consume();

        for (int i = 1; i < size; ++i) {
            if ((peekch() & 0b11000000) != 0b10000000)
                return new Lexeme(new Location(start, position()), Lexeme.Type.BrokenUnicode);

            codepoint = codepoint << 6;
            codepoint |= (peekch() & 0b00111111);
            consume();
        }

        final Lexeme result = new Lexeme(new Location(start, position()), Lexeme.Type.BrokenUnicode);
        result.setCodepoint(codepoint);
        return result;
    }
}
