package com.techhog.luauj.Ast;

public class Position {
    public int line, column;

    public Position(int l, int c) {
        line = l;
        column = c;
    }

    public boolean equals(Position rhs) {
        return column == rhs.column && line == rhs.line;
    }
    public boolean lessthan(Position rhs) {
        return (line == rhs.line) ? (column < rhs.column) : line < rhs.line;
    }
    public boolean lessthanequals(Position rhs) {
        return lessthan(rhs) || equals(rhs);
    }
    public boolean greaterthan(Position rhs) {
        return (line == rhs.line) ? (column > rhs.column) : line > rhs.line;
    }
    public boolean greaterthanequals(Position rhs) {
        return greaterthan(rhs) || equals(rhs);
    }
    public void shift(Position start, Position old_end, Position new_end) {
        if (greaterthanequals(start)) {
            if (line > old_end.line) {
                line += (new_end.line - old_end.line);
            } else {
                line = new_end.line;
                column += (new_end.column - old_end.column);
            }
        }
    }
}