package controller;
import java.util.EnumSet;
public enum Failure {
    syntax_error,unacceptable_size,illegal_move,cannot_undo;
    // maybe always allow syntax error and then maybe there is just one
    // more?
    // so we do not need a set?
    // should add unknown command message?
    static final EnumSet<Failure> set0=EnumSet.noneOf(Failure.class);
    static {
        set0.add(Failure.syntax_error);
    }
    static final EnumSet<Failure> set1=EnumSet.noneOf(Failure.class);
    static {
        set1.add(Failure.syntax_error);
        set1.add(Failure.unacceptable_size);
    }
    static final EnumSet<Failure> set3=EnumSet.noneOf(Failure.class);
    static {
        set3.add(Failure.syntax_error);
        set3.add(Failure.illegal_move);
    }
    public String toString2() {
        String string="";
        for(int i=0;i<name().length();i++) if(name().charAt(i)=='_') string+=" ";
        else string+=name().charAt(i);
        return string;
    }
}
