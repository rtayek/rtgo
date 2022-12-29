package gui;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import equipment.Point;
public class Jitter {
    static class Zero extends Jitter {
        Zero() { super(0,0); }
        @Override int xJitter(int index) { return 0; }
        @Override int yJitter(int index) { return 0; }
    }
    Jitter(int width,int depth) {
        xJitters=new int[width*depth];
        yJitters=new int[width*depth];
        for(int i=0;i<width*depth;i++) {
            xJitters[i]=random.nextInt(amount)-amount/2;
            yJitters[i]=random.nextInt(amount)-amount/2;
        }
    }
    int xJitter(int index) { return xJitters[index]; }
    int yJitter(int index) { return yJitters[index]; }
    public static void main(String[] args) {}
    final int[] xJitters,yJitters;
    static Map<Point,Jitter> jitters=new LinkedHashMap<>();
    {}
    static Jitter get(int width,int depth) {
        if(width==0&&depth==0) return zero;
        Point point=new Point(width,depth);
        Jitter jitter=jitters.get(point);
        if(jitter==null) { jitter=new Jitter(point.x,point.y); jitters.put(point,jitter); }
        return jitter;
    }
    static Random random=new Random(1234);
    static final int amount=5;
    static final Zero zero=new Zero();
}
