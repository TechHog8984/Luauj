package com.techhog.luauj.Ast;

import java.util.HashMap;
import java.util.Optional;

import com.techhog.luauj.Ast.Ast.AstArray;
import com.techhog.luauj.Ast.Ast.AstExpr;
import com.techhog.luauj.Ast.Ast.AstExprError;
import com.techhog.luauj.Ast.Ast.AstLocal;
import com.techhog.luauj.Ast.Ast.AstName;
import com.techhog.luauj.Ast.Ast.AstNode;
import com.techhog.luauj.Ast.Ast.AstStat;
import com.techhog.luauj.Ast.Ast.AstStatBlock;
import com.techhog.luauj.Ast.Ast.AstStatError;
import com.techhog.luauj.Ast.Ast.AstType;
import com.techhog.luauj.Ast.Ast.AstTypeError;
import com.techhog.luauj.Ast.Ast.AstTypeFunction;
import com.techhog.luauj.Ast.Ast.AstTypeIntersection;
import com.techhog.luauj.Ast.Ast.AstTypeList;
import com.techhog.luauj.Ast.Ast.AstTypePack;
import com.techhog.luauj.Ast.Ast.AstTypePackExplicit;
import com.techhog.luauj.Ast.Ast.AstTypePackGeneric;
import com.techhog.luauj.Ast.Ast.AstTypePackVariadic;
import com.techhog.luauj.Ast.Ast.AstTypeReference;
import com.techhog.luauj.Ast.Ast.AstTypeSingletonBool;
import com.techhog.luauj.Ast.Ast.AstTypeSingletonString;
import com.techhog.luauj.Ast.Ast.AstTypeTable;
import com.techhog.luauj.Ast.Ast.AstTypeTypeof;
import com.techhog.luauj.Ast.Ast.AstTypeUnion;
import com.techhog.luauj.Ast.Ast.AstVisitor;
import com.techhog.luauj.Ast.Lexer.Lexeme;

public class Ast {
    private static void visitTypeList(AstVisitor visitor, AstTypeList list) {
        for (AstType type : list.types.data) {
            type.visit(visitor);
        }

        if (list.tail_type.isPresent())
            list.tail_type.get().visit(visitor);
    }

    public static class AstName {
        public final String value;

        public AstName() {
            value = null;
        }
        public AstName(String value_in){
            value = value_in;
        }

        public boolean equals(AstName rhs) {
            return value == rhs.value;
        }
        public boolean equals(String rhs) {
            return value == rhs;
        }
        public boolean lessthan(AstName rhs) {
            if (value == null || rhs.value == null) return false;
            // maybe broken strcmp implementation
            for (char a : value.toCharArray()) {
                for (char b : rhs.value.toCharArray()) {
                    if (a != b) {
                        return ((int) a) > ((int) b);
                    }
                }
            }
            return false;
        }
    }

    public static class AstVisitor {
        public AstVisitor() {}
    
        public boolean visit(AstNode a) {
            return true;
        }
    
        public boolean visit(AstExpr node)
        {
            return visit((AstNode) node);
        }
    
        public boolean visit(AstExprGroup node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprConstantNil node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprConstantBool node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprConstantNumber node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprConstantString node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprLocal node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprGlobal node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprVarargs node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprCall node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprIndexName node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprIndexExpr node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprFunction node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprTable node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprUnary node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprBinary node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprTypeAssertion node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprIfElse node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprInterpString node)
        {
            return visit((AstExpr) node);
        }
        public boolean visit(AstExprError node)
        {
            return visit((AstExpr) node);
        }
    
        public boolean visit(AstStat node)
        {
            return visit((AstNode) node);
        }
    
        public boolean visit(AstStatBlock node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatIf node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatWhile node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatRepeat node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatBreak node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatContinue node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatReturn node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatExpr node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatLocal node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatFor node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatForIn node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatAssign node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatCompoundAssign node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatFunction node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatLocalFunction node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatTypeAlias node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatDeclareFunction node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatDeclareGlobal node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatDeclareClass node)
        {
            return visit((AstStat) node);
        }
        public boolean visit(AstStatError node)
        {
            return visit((AstStat) node);
        }
    
        // By default visiting type annotations is disabled; override this in your visitor if you need to!
        public boolean visit(AstType node)
        {
            return false;
        }
    
        public boolean visit(AstTypeReference node)
        {
            return visit((AstType) node);
        }
        public boolean visit(AstTypeTable node)
        {
            return visit((AstType) node);
        }
        public boolean visit(AstTypeFunction node)
        {
            return visit((AstType) node);
        }
        public boolean visit(AstTypeTypeof node)
        {
            return visit((AstType) node);
        }
        public boolean visit(AstTypeUnion node)
        {
            return visit((AstType) node);
        }
        public boolean visit(AstTypeIntersection node)
        {
            return visit((AstType) node);
        }
        public boolean visit(AstTypeSingletonBool node)
        {
            return visit((AstType) node);
        }
        public boolean visit(AstTypeSingletonString node)
        {
            return visit((AstType) node);
        }
        public boolean visit(AstTypeError node)
        {
            return visit((AstType) node);
        }
    
        public boolean visit(AstTypePack node)
        {
            return false;
        }
        public boolean visit(AstTypePackExplicit node)
        {
            return visit((AstTypePack) node);
        }
        public boolean visit(AstTypePackVariadic node)
        {
            return visit((AstTypePack) node);
        }
        public boolean visit(AstTypePackGeneric node)
        {
            return visit((AstTypePack) node);
        }
    };

    public static abstract class AstType extends AstNode {
        public AstType(int class_index, Location location) {
            super(class_index, location);
        }

        public Optional<AstType> asType() {
            return Optional.of(this);
        }
    }

    public static class AstLocal {
        public final AstName name;
        public final Location location;
        public final Optional<AstLocal> shadow;
        public final int function_depth;
        public final int loop_depth;

        public final Optional<AstType> annotation;

        public AstLocal(AstName name_in, Location location_in, Optional<AstLocal> shadow_in, int function_depth_in, int loop_depth_in, AstType annotation_in) {
            name = name_in;
            location = location_in;
            shadow = shadow_in;
            function_depth = function_depth_in;
            loop_depth = loop_depth_in;
            annotation = Optional.of(annotation_in);
        }

        public AstLocal(AstName name_in, Location location_in, Optional<AstLocal> shadow_in, int function_depth_in, int loop_depth_in) {
            name = name_in;
            location = location_in;
            shadow = shadow_in;
            function_depth = function_depth_in;
            loop_depth = loop_depth_in;
            annotation = Optional.empty();
        }
    }

    public static class AstArray<T> {
        public final T[] data;
        public final int size;

        public AstArray(T[] data_in) {
            data = data_in;
            size = data_in.length;
        }
    }

    public static class AstTypeList {
        public final AstArray<AstType> types;
        public final Optional<AstTypePack> tail_type;

        public AstTypeList(AstArray<AstType> types_in, AstTypePack tail_type_in) {
            types = types_in;
            tail_type = Optional.of(tail_type_in);
        }
        public AstTypeList(AstArray<AstType> types_in) {
            types = types_in;
            tail_type = Optional.empty();
        }
    }

    public static class AstGenericType {
        public final AstName name;
        public final Location location;
        public final Optional<AstType> default_value;

        public AstGenericType(AstName name_in, Location location_in, AstType default_value_in) {
            name = name_in;
            location = location_in;
            default_value = Optional.of(default_value_in);
        }
        public AstGenericType(AstName name_in, Location location_in) {
            name = name_in;
            location = location_in;
            default_value = Optional.empty();
        }
    }

    public static class AstGenericTypePack {
        public final AstName name;
        public final Location location;
        public final Optional<AstTypePack> default_value;

        public AstGenericTypePack(AstName name_in, Location location_in, AstTypePack default_value_in) {
            name = name_in;
            location = location_in;
            default_value = Optional.of(default_value_in);
        }
        public AstGenericTypePack(AstName name_in, Location location_in){
            name = name_in;
            location = location_in;
            default_value = Optional.empty();
        }
    }
    private static class AstRtti {
        private static int INDEX = 0;
        private static HashMap<Class<?>, Integer> CLASS_TO_INDEX_MAP = new HashMap();

        public static int get(Class<?> c) {
            if (!CLASS_TO_INDEX_MAP.containsKey(c)) {
                CLASS_TO_INDEX_MAP.put(c, ++AstRtti.INDEX);
            }
            return CLASS_TO_INDEX_MAP.get(c);
        }
    }

    public static abstract class AstNode {
        public final int class_index;
        public final Location location;
        public AstNode(int class_index_in, Location location_in) {
            class_index = class_index_in;
            location = location_in;
        }

        public abstract void visit(AstVisitor visitor);
        public Optional<AstExpr> asExpr() {
            return Optional.empty();
        }
        public Optional<AstStat> asStat() {
            return Optional.empty();
        }
        public Optional<AstType> asType() {
            return Optional.empty();
        }

        public  boolean is(Class<?> c) {
            return class_index == AstRtti.get(c);
        }
        public <T> Optional<T> as(Class<?> c) {
            return is(c) ? Optional.of((T) this) : Optional.empty();
        }
    }

    public static abstract class AstExpr extends AstNode {
        public AstExpr(int class_index, Location location) {
            super(class_index, location);
        }

        @Override
        public Optional<AstExpr> asExpr() {
            return Optional.of(this);
        }
    }

    public static abstract class AstStat extends AstNode {
        public boolean has_semicolon;

        public AstStat(int class_index, Location location, boolean has_semicolon_in) {
            super(class_index, location);
            has_semicolon = has_semicolon_in;
        }
        public AstStat(int class_index, Location location) {
            this(class_index, location, false);
        }
    }

    public static class AstExprGroup extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprGroup.class);
        }

        public final AstExpr expr;

        public AstExprGroup(Location location, AstExpr expr_in) {
            super(ClassIndex(), location);
            expr = expr_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this))
                expr.visit(visitor);
        }
    }

    public static class AstExprConstantNil extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprConstantNil.class);
        }

        public AstExprConstantNil(Location location) {
            super(ClassIndex(), location);
        }

        public void visit(AstVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class AstExprConstantBool extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprConstantBool.class);
        }

        public final boolean value;

        public AstExprConstantBool(Location location, boolean value_in) {
            super(ClassIndex(), location);
            value = value_in;
        }

        public void visit(AstVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static enum ConstantNumberParseResult {
        Ok,
        Malformed,
        BinOverflow,
        HexOverflow,
        DoublePrefix,
    };

    public static class AstExprConstantNumber extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprConstantNumber.class);
        }

        public final double value;
        public final ConstantNumberParseResult parse_result;

        public AstExprConstantNumber(Location location, double value_in, ConstantNumberParseResult parse_result_in) {
            super(ClassIndex(), location);
            value = value_in;
            parse_result = parse_result_in;
        }
        public AstExprConstantNumber(Location location, double value_in) {
            this(location, value_in, ConstantNumberParseResult.Ok);
        }

        public void visit(AstVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class AstExprConstantString extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprConstantString.class);
        }

        public final AstArray<Character> value;

        public AstExprConstantString(Location location, AstArray<Character> value_in) {
            super(ClassIndex(), location);
            value = value_in;
        }

        public void visit(AstVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class AstExprLocal extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprLocal.class);
        }

        public final AstLocal local;
        public final boolean upvalue;

        public AstExprLocal(Location location, AstLocal local_in, boolean upvalue_in) {
            super(ClassIndex(), location);
            local = local_in;
            upvalue = upvalue_in;
        }

        public void visit(AstVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class AstExprGlobal extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprGlobal.class);
        }

        public final AstName name;

        public AstExprGlobal(Location location, AstName name_in) {
            super(ClassIndex(), location);
            name = name_in;
        }

        public void visit(AstVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class AstExprVarargs extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprVarargs.class);
        }

        public AstExprVarargs(Location location) {
            super(ClassIndex(), location);
        }

        public void visit(AstVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class AstExprCall extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprCall.class);
        }

        public final AstExpr func;
        public final AstArray<AstExpr> args;
        public final boolean self;
        public final Location arg_location;

        public AstExprCall(Location location, AstExpr func_in, AstArray<AstExpr> args_in, boolean self_in, Location arg_location_in) {
            super(ClassIndex(), location);
            func = func_in;
            args = args_in;
            self = self_in;
            arg_location = arg_location_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                func.visit(visitor);

                for (AstExpr arg : args.data) {
                    arg.visit(visitor);
                }
            }
        }
    }

    public static class AstExprIndexName extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprIndexName.class);
        }

        public final AstExpr expr;
        public final AstName index;
        public final Location index_location;
        public final Position op_position;
        public final char op;

        public AstExprIndexName(Location location, AstExpr expr_in, AstName index_in, Location index_location_in, Position op_position_in, char op_in) {
            super(ClassIndex(), location);
            expr = expr_in;
            index = index_in;
            index_location = index_location_in;
            op_position = op_position_in;
            op = op_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this))
                expr.visit(visitor);
        }
    }

    public static class AstExprIndexExpr extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprIndexExpr.class);
        }

        public final AstExpr expr;
        public final AstExpr index;

        public AstExprIndexExpr(Location location, AstExpr expr_in, AstExpr index_in) {
            super(ClassIndex(), location);
            expr = expr_in;
            index = index_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this))
                expr.visit(visitor);
                index.visit(visitor);
        }
    }

    public static class AstExprFunction extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprFunction.class);
        }

        public final AstArray<AstGenericType> generics;
        public final AstArray<AstGenericTypePack> generic_packs;
        public final AstLocal self;
        public final AstArray<AstLocal> args;
        public final Optional<AstTypeList> return_annotation;
        public final boolean vararg = false;
        public final Location vararg_location;
        public final Optional<AstTypePack> vararg_annotation;

        public final AstStatBlock body;

        public final int function_depth;

        public final AstName debugname;

        public final boolean has_end = false;
        public final Optional<Location> arg_location;

        public AstExprFunction(Location location, AstArray<AstGenericType> generics_in, AstArray<AstGenericTypePack> generic_packs_in,
        AstLocal self_in, AstArray<AstLocal> args_in, boolean vararg_in, Location vararg_location_in, AstStatBlock body_in, int function_depth_in,
        AstName debugname_in, Optional<AstTypeList> return_annotation_in, Optional<AstTypePack> vararg_annotation_in, boolean has_end_in,
        Optional<Location> arg_location_in)
        {
            super(ClassIndex(), location);

            generics = generics_in;
            generic_packs = generic_packs_in;
            self = self_in;
            args = args_in;
            vararg = vararg_in;
            vararg_location = vararg_location_in;
            body = body_in;
            function_depth = function_depth_in;
            debugname = debugname_in;
            return_annotation = return_annotation_in;
            vararg_annotation = vararg_annotation_in;
            has_end = has_end_in;
            arg_location = arg_location_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                for (AstLocal arg : args.data) {
                    if (arg.annotation.isPresent())
                        arg.annotation.get().visit(visitor);
                }

                if (vararg_annotation.isPresent())
                    vararg_annotation.get().visit(visitor);

                if (return_annotation.isPresent())
                    visitTypeList(visitor, return_annotation.get());

                body.visit(visitor);
            }
        }
    }

    public static class AstExprTable extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprTable.class);
        }

        public static class Item {
            public static enum Kind {
                List,    // foo, in which case key is a nullptr
                Record,  // foo=bar, in which case key is a AstExprConstantString
                General, // [foo]=bar
            }

            public final Kind kind;

            public final Optional<AstExpr> key;
            public final AstExpr value;

            public Item(Kind kind_in, AstExpr key_in, AstExpr value_in) {
                kind = kind_in;
                key = Optional.of(key_in);
                value = value_in;
            }
            public Item(Kind kind_in, AstExpr value_in) {
                kind = kind_in;
                key = Optional.empty();
                value = value_in;
            }
        }

        public final AstArray<Item> items;

        public AstExprTable(Location location, AstArray<Item> items_in) {
            super(ClassIndex(), location);
            items = items_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                for (Item item : items.data) {
                    if (item.key.isPresent())
                        item.key.get().visit(visitor);

                    item.value.visit(visitor);
                }
            }
        }
    }

    public static class AstExprUnary extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprUnary.class);
        }
        public static enum Op {
            Not,
            Minus,
            Len
        };

        public final Op op;
        public final AstExpr expr;

        public AstExprUnary(Location location, Op op_in, AstExpr expr_in) {
            super(ClassIndex(), location);
            op = op_in;
            expr = expr_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this))
                expr.visit(visitor);
        }
    }

    public static String toString(AstExprUnary.Op op) {
        switch (op) {
            case Minus:
                return "-";
            case Not:
                return "not";
            case Len:
                return "#";
        }
        return "";
    }

    public static class AstExprBinary extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprBinary.class);
        }
        public static enum Op {
            Add,
            Sub,
            Mul,
            Div,
            Mod,
            Pow,
            Concat,
            CompareNe,
            CompareEq,
            CompareLt,
            CompareLe,
            CompareGt,
            CompareGe,
            And,
            Or
        };

        public final Op op;
        public final AstExpr left;
        public final AstExpr right;

        public AstExprBinary(Location location, Op op_in, AstExpr left_in, AstExpr right_in) {
            super(ClassIndex(), location);
            op = op_in;
            left = left_in;
            right = right_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this))
                left.visit(visitor);
                right.visit(visitor);
        }
    }

    public static String toString(AstExprBinary.Op op) {
        switch (op) {
            case Add:
                return "+";
            case Sub:
                return "-";
            case Mul:
                return "*";
            case Div:
                return "/";
            case Mod:
                return "%";
            case Pow:
                return "^";
            case Concat:
                return "..";
            case CompareNe:
                return "~=";
            case CompareEq:
                return "==";
            case CompareLt:
                return "<";
            case CompareLe:
                return "<=";
            case CompareGt:
                return ">";
            case CompareGe:
                return ">=";
            case And:
                return "and";
            case Or:
                return "or";
        }
        return "";
    }

    public static class AstExprTypeAssertion extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprTypeAssertion.class);
        }

        public final AstExpr expr;
        public final AstType annotation;

        public AstExprTypeAssertion(Location location, AstExpr expr_in, AstType annotation_in) {
            super(ClassIndex(), location);
            expr = expr_in;
            annotation = annotation_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                expr.visit(visitor);
                annotation.visit(visitor);
            }
        }
    }

    public static class AstExprIfElse extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprIfElse.class);
        }

        public final AstExpr condition;
        public final Optional<AstExpr> true_expr;
        public final Optional<AstExpr> false_expr;

        public AstExprIfElse(Location location, AstExpr condition_in, Optional<AstExpr> true_expr_in, Optional<AstExpr> false_expr_in) {
            super(ClassIndex(), location);
            condition = condition_in;
            true_expr = true_expr_in;
            false_expr = false_expr_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                condition.visit(visitor);

                if (true_expr.isPresent())
                    true_expr.get().visit(visitor);
                if (false_expr.isPresent())
                    false_expr.get().visit(visitor);
            }
        }
    }

    public static class AstExprInterpString extends AstExpr {
        public static int ClassIndex() {
            return AstRtti.get(AstExprInterpString.class);
        }

        public final AstArray<AstArray<Character>> strings;
        public final AstArray<AstExpr> expressions;

        public AstExprInterpString(Location location, AstArray<AstArray<Character>> strings_in, AstArray<AstExpr> expressions_in) {
            super(ClassIndex(), location);
            strings = strings_in;
            expressions = expressions_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                for (AstExpr expr : expressions.data) {
                    expr.visit(visitor);
                }
            }
        }
    }

    public static class AstStatBlock extends AstStat {
        public static int ClassIndex() {
            return AstRtti.get(AstStatBlock.class);
        }

        public final AstArray<AstStat> body;

        public AstStatBlock(Location location, AstArray<AstStat> body_in) {
            super(ClassIndex(), location);
            body = body_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                for (AstStat stat : body.data) {
                    stat.visit(visitor);
                }
            }
        }
    }

    public static class AstStatIf extends AstStat {
        public static int ClassIndex() {
            return AstRtti.get(AstStatIf.class);
        }

        public final AstExpr condition;
        public final AstStatBlock then_body;
        public final Optional<AstStat> else_body;

        public final Optional<Location> then_location;
        // Active for 'elseif' as well
        public final Optional<Location> else_location;

        public AstStatIf(Location location, AstExpr condition_in, AstStatBlock then_body_in, Optional<AstStat> else_body_in,
            Optional<Location> then_location_in, Optional<Location> else_location_in)
        {
            super(ClassIndex(), location);
            condition = condition_in;
            then_body = then_body_in;
            else_body = else_body_in;
            then_location = then_location_in;
            else_location = else_location_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                condition.visit(visitor);
                then_body.visit(visitor);

                if (else_body.isPresent())
                    else_body.get().visit(visitor);
            }
        }
    }

    public static class AstStatWhile extends AstStat {
        public static int ClassIndex() {
            return AstRtti.get(AstStatWhile.class);
        }

        public final AstExpr condition;
        public final AstStatBlock body;

        public final boolean has_do;
        public final Location do_location;

        public final boolean has_end;

        public AstStatWhile(Location location, AstExpr condition_in, AstStatBlock body_in, boolean has_do_in, Location do_location_in, boolean has_end_in) {
            super(ClassIndex(), location);
            condition = condition_in;
            body = body_in;
            has_do = has_do_in;
            do_location = do_location_in;
            has_end = has_end_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                condition.visit(visitor);
                body.visit(visitor);
            }
        }
    }

    public static class AstStatRepeat extends AstStat {
        public static int ClassIndex() {
            return AstRtti.get(AstStatRepeat.class);
        }

        public final AstExpr condition;
        public final AstStatBlock body;

        public final boolean has_until;

        public AstStatRepeat(Location location, AstExpr condition_in, AstStatBlock body_in, boolean has_until_in) {
            super(ClassIndex(), location);
            condition = condition_in;
            body = body_in;
            has_until = has_until_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                condition.visit(visitor);
                body.visit(visitor);
            }
        }
    }

    public static class AstStatBreak extends AstStat {
        public static int ClassIndex() {
            return AstRtti.get(AstStatBreak.class);
        }

        public AstStatBreak(Location location) {
            super(ClassIndex(), location);
        }

        public void visit(AstVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class AstStatContinue extends AstStat {
        public static int ClassIndex() {
            return AstRtti.get(AstStatContinue.class);
        }

        public AstStatContinue(Location location) {
            super(ClassIndex(), location);
        }

        public void visit(AstVisitor visitor) {
            visitor.visit(this);
        }
    }

    public static class AstStatReturn extends AstStat {
        public static int ClassIndex() {
            return AstRtti.get(AstStatReturn.class);
        }

        public final AstArray<AstExpr> list;

        public AstStatReturn(Location location, AstArray<AstExpr> list_in) {
            super(ClassIndex(), location);
            list = list_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                for (AstExpr expr : list.data) {
                    expr.visit(visitor);
                }
            }
        }
    }

    public static class AstStatExpr extends AstStat {
        public static int ClassIndex() {
            return AstRtti.get(AstStatExpr.class);
        }

        public final AstExpr expr;

        public AstStatExpr(Location location, AstExpr expr_in) {
            super(ClassIndex(), location);
            expr = expr_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this))
                expr.visit(visitor);
        }
    }

    public static class AstStatLocal extends AstStat {
        public static int ClassIndex() {
            return AstRtti.get(AstStatLocal.class);
        }

        public final AstArray<AstLocal> vars;
        public final AstArray<AstExpr> values;
        public final Optional<Location> equals_sign_location;

        public AstStatLocal(Location location, AstArray<AstLocal> vars_in, AstArray<AstExpr> values_in, Optional<Location> equals_sign_location_in) {
            super(ClassIndex(), location);
            vars = vars_in;
            values = values_in;
            equals_sign_location = equals_sign_location_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                for (AstLocal var : vars.data) {
                    if (var.annotation.isPresent())
                        var.annotation.get().visit(visitor);
                }

                for (AstExpr expr : values.data)
                    expr.visit(visitor);
            }
        }
    }

    public static class AstStatFor extends AstStat {
        public static int ClassIndex() {
            return AstRtti.get(AstStatFor.class);
        }

        public final AstLocal var;
        public final AstExpr from;
        public final AstExpr to;
        public final Optional<AstExpr> step;
        public final AstStatBlock body;

        public final boolean has_do;
        public final Location do_location;

        public final boolean has_end;

        public AstStatFor(Location location, AstLocal var_in, AstExpr from_in, AstExpr to_in, Optional<AstExpr> step_in, AstStatBlock body_in, 
            boolean has_do_in, Location do_location_in, boolean has_end_in)
        {
            super(ClassIndex(), location);

            var = var_in;
            from = from_in;
            to = to_in;
            step = step_in;
            body = body_in;
    
            has_do = has_do_in;
            do_location = do_location_in;
    
            has_end = has_end_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                if (var.annotation.isPresent())
                    var.annotation.get().visit(visitor);

                from.visit(visitor);
                to.visit(visitor);

                if (step.isPresent())
                    step.get().visit(visitor);

                body.visit(visitor);
            }
        }
    }

    public static class AstStatForIn extends AstStat {
        public static int ClassIndex() {
            return AstRtti.get(AstStatForIn.class);
        }

        public final AstArray<AstLocal> vars;
        public final AstArray<AstExpr> values;
        public final AstStatBlock body;

        public final boolean has_in;
        public final Location in_location;

        public final boolean has_do;
        public final Location do_location;

        public final boolean has_end;

        public AstStatForIn(Location location, AstArray<AstLocal> vars_in, AstArray<AstExpr> values_in, AstStatBlock body_in, 
            boolean has_in_in, Location in_location_in, boolean has_do_in, Location do_location_in, boolean has_end_in)
        {
            super(ClassIndex(), location);

            vars = vars_in;
            values = values_in;
            body = body_in;

            has_in = has_in_in;
            in_location = in_location_in;

            has_do = has_do_in;
            do_location = do_location_in;

            has_end = has_end_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                for (AstLocal var : vars.data) {
                    if (var.annotation.isPresent())
                        var.annotation.get().visit(visitor);
                }

                for (AstExpr expr : values.data)
                    expr.visit(visitor);

                body.visit(visitor);
            }
        }
    }

    public static class AstTypeOrPack {
        public Optional<AstType> type;
        public Optional<AstTypePack> type_pack;
    }
    public static class AstTypeReference extends AstType {
        public static int ClassIndex() {
            return AstRtti.get(AstTypeReference.class);
        }

        public final boolean has_parameter_list;
        public final Optional<AstName> prefix;
        public final AstName name;
        public final AstArray<AstTypeOrPack> parameters;

        public AstTypeReference(Location location_in, Optional<AstName> prefix_in, AstName name_in, Optional<Boolean> has_parameter_list_in, AstArray<AstTypeOrPack> parameters_in) {
            super(ClassIndex(), location_in);
            has_parameter_list = has_parameter_list_in.orElse(false);
            prefix = prefix_in;
            name = name_in;
            parameters = parameters_in;
        }
        public AstTypeReference(Location location_in, Optional<AstName> prefix_in, AstName name_in, Optional<Boolean> has_parameter_list_in) {
            this(location_in, prefix_in, name_in, has_parameter_list_in, new AstArray<>(new AstTypeOrPack[] {}));
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                for (AstTypeOrPack param : parameters.data) {
                    if (param.type.isPresent())
                        param.type.get().visit(visitor);
                    else
                        param.type_pack.get().visit(visitor);
                }
            }
        }
    }

    public static class AstTableProp {
        public final AstName name;
        public final Location location;
        public final AstType type;

        public AstTableProp(AstName name_in, Location location_in, AstType type_in) {
            name = name_in;
            location = location_in;
            type = type_in;
        }
    }
    public static class AstTableIndexer {
        public final AstType index_type;
        public final AstType result_type;
        public final Location location;

        public AstTableIndexer(AstType index_type_in, AstType result_type_in, Location location_in) {
            index_type = index_type_in;
            result_type = result_type_in;
            location = location_in;
        }
    }
    public static class AstTypeTable extends AstType {
        public static int ClassIndex() {
            return AstRtti.get(AstTypeTable.class);
        }

        public final AstArray<AstTableProp> props;
        public final Optional<AstTableIndexer> indexer;

        public AstTypeTable(Location location, AstArray<AstTableProp> props_in, Optional<AstTableIndexer> indexer_in) {
            super(ClassIndex(), location);
            props = props_in;
            indexer = indexer_in;
        }

        public void visit(AstVisitor visitor) {
            if (visitor.visit(this)) {
                for (AstTableProp prop : props) {
                    prop.type.visit(visitor);
                }

                if (indexer.isPresent()) {
                    AstTableIndexer i = indexer.get();
                    i.index_type.visit(visitor);
                    i.result_type.visit(visitor);
                }
            }
        }
    }
    public static class AstTypeFunction extends AstType {

    }
    public static class AstTypeTypeof extends AstType {

    }
    public static class AstTypeUnion extends AstType {
        
    }
    public static class AstTypeIntersection extends AstType {
        
    }
    public static class AstExprError extends AstType {
        
    }
    public static class AstStatError extends AstType {
        
    }
    public static class AstTypeError extends AstType {
        
    }
    public static class AstTypeSingletonBool extends AstType {
        
    }
    public static class AstTypeSingletonString extends AstType {
        
    }
    public static class AstTypePack extends AstNode {
        
    }
    public static class AstTypePackExplicit extends AstTypePack {

    }
    public static class AstTypePackVariadic extends AstTypePack {
        
    }
    public static class AstTypePackGeneric extends AstTypePack {
        
    }

    public static AstName getIdentifier(AstExpr node) {

    }
    public static Location getLocation(AstTypeList type_list) {

    }

    public static <T> Location getLocation(AstArray<T> array) {
        
    }

    // public static class AstNameTable {
    //     public final AstName addStatic(String name, Lexeme.Type type) {

    //     }
    // }
}
