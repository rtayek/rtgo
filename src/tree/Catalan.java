package tree;
import java.util.*;
import tree.G2.Generator;
public class Catalan {
    public static final long c(final int n,final int r) { // binomial coefficient
        long i,p;
        if(r<0||n<0||r>n) p=0;
        else if(r==0) p=1;
        else if(r>n-r) p=c(n,n-r);
        else {
            for(p=1,i=n;i>=n-r+1;--i) p=p*i;
            p=p/f(r);
        }
        // figure out why this fails first!
        return (int)p;
    }
    static final long f(final int n) { // factorial
        long i,p;
        if(n<=1) p=1;
        else for(p=n,i=2;i<=n-1;++i) p=p*i;
        return(p);
    }
    //C_n       =       1/(n+1)(2n; n)
    public static long catalan2(int n) { return c(2*n,n)/(n+1); }
    public static long catalan(int n) { return f(2*n)/(f(n+1)*f(n)); }
    public static void main(String[] args) {
        for(int nodes=0;nodes<=maxNodes;++nodes) {
            System.gc();
            System.out.println("nodes: "+nodes);
            Node.ids=0;
            Iterator<Long> iterator=new G2.Longs();
            List<Node<Long>> trees=Generator.one(nodes,iterator,true);
            //System.out.println("trees: "+trees);
            //System.out.println("trees2: "+trees2);
            //System.out.println(nodes+" nodes. has "+trees.size()+" trees.");
            //System.out.println(nodes+" nodes. has "+trees2.size()+" trees2.");
            if(trees.size()!=catalans[nodes]) {
                System.out.println(trees.size()+" trees.size()!=catalans["+nodes+"]");
            } else System.out.println(trees.size()+" trees.");
            int i=0;
            for(Node<Long> node:trees) {
                if(node!=null)
                    if(RedBean.encoded.equals(node.encoded)) System.out.println("node: "+nodes+", tree: "+i+": "+node);
                ++i;
            }
            //System.out.println("|||||||");
            System.out.println("end of nodes: "+nodes);
        }
        System.out.println(catalans.length+" catalans.");
    }
    static final int maxNodes=13;
    public static long[] catalans=new long[] {1,1,2,5,14,42,132,429,1430,4862,16796,58786,208012,742900,2674440,9694845,
            35357670,129644790,477638700,1767263190,6564120420l,24466267020l,91482563640l,343059613650l,1289904147324l,
            4861946401452l};
}
