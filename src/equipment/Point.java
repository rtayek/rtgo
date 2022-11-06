package equipment;
public class Point extends java.awt.Point {
    public Point() { super(); }
    public Point(java.awt.Point point) { super(point); }
    public Point(int x,int y) { super(x,y); }
    @Override public String toString() { return "("+x+","+y+")"; }
    private static final long serialVersionUID=1L;
}
