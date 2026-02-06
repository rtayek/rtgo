package io;
public class Indent {
    public Indent(final String string) { this.string=string; }
    public Indent(final Indent indent) { this(indent.string); }
    public void in() { ++indent; }
    public void out() { --indent; }
    public String indent() {
        if(string.isEmpty()) return "";
        stringBuffer.delete(0,stringBuffer.length());
        for(int i=0;i<indent;++i) stringBuffer.append(string);
        return stringBuffer.toString();
    }
    private int indent;
    final protected String string;
    private StringBuffer stringBuffer=new StringBuffer();
}
