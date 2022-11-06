package mailinglist;
public class Go {
    enum Color {
        black,white,vacant,edge; // edge can be useful 
    }
    enum Direction { n, s, e, w, ne, nw, se, sw }
    class Neighbors {
        Neighbors(int width,int depth) {
            this.width=width;
            this.depth=depth;
            this.size=(width+2)*(depth+2);
            n=new int[size];
            s=new int[size];
            e=new int[size];
            w=new int[size];
            ne=new int[size];
            nw=new int[size];
            se=new int[size];
            sw=new int[size];
            // lots more init required, but it this is constant for any width and depth 
        }
        final int width,depth,size;
        int[] n,s,e,w,ne,nw,se,sw;
    }
    class Board {
        Board(Neighbors neighbors) { this.neighbors=neighbors; colors=new Color[neighbors.size]; }
        final Color[] colors;
        final Neighbors neighbors;
    }
}
