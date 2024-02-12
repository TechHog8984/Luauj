package com.techhog.luauj.Ast;

import java.util.HashMap;
import java.util.Map;

public class Lexer {
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

    }
}
