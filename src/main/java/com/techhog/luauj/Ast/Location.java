package com.techhog.luauj.Ast;

public final class Location {
    public Position begin, end;

    public Location() {
        begin = new Position(0, 0);
        end = new Position(0, 0);
    }
    public Location(Position begin_in, Position end_in) {
        begin = begin_in;
        end = end_in;
    }
    public Location(Position begin_in, int length) {
        begin = begin_in;
        end = new Position(begin_in.line, begin_in.column + length);
    }
    public Location(Location begin_in, Location end_in) {
        begin = begin_in.begin;
        end = end_in.end;
    }

    public boolean equals(Location rhs) {
        return begin.equals(rhs.begin) && end.equals(rhs.end);
    }
    public boolean encloses(Location l) {
        return begin.lessthanequals(l.begin) && end.greaterthanequals(l.end);
    }
    public boolean overlaps(Location l) {
        return (begin.lessthanequals(l.begin) && end.greaterthanequals(l.begin)) || (begin.lessthanequals(l.end) && end.greaterthanequals(l.end)) || (begin.greaterthanequals(l.begin) && end.lessthanequals(l.end));
    }
    public boolean contains(Position p) {
        return begin.lessthanequals(p) && p.lessthan(end);
    }
    public boolean containsClosed(Position p) {
        return begin.lessthanequals(p) && p.lessthanequals(end);
    }

    public void extend(Location other) {
        if (other.begin.lessthan(begin)) 
            begin = other.begin;
        if (other.end.greaterthan(end))
            end = other.end;
    }
    public void shift(Position start, Position old_end, Position new_end) {
        begin.shift(start, old_end, new_end);
        end.shift(start, old_end, new_end);
    }
}
