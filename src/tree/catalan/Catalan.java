package tree.catalan;
import java.util.List;
import tree.catalan.G2.Node;
import utilities.Holder;
public class Catalan {
    Node x;
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
        G2 g2=new G2();
        for(int nodes=0;nodes<100;++nodes) {
            Node.ids=0;
            Holder<Integer> data=new Holder<>(0);
            List<Node> trees=g2.all(nodes,data);
            //System.out.println(trees);
            System.out.println(nodes+" nodes. has "+trees.size()+" trees.");
            if(trees.size()!=catalans[nodes]) {
                System.out.println(trees.size()+" trees.size()!=catalans["+nodes+"]");
            }
            for(Node node:trees) {
                //System.out.println("|||");
                //print(node);
            }
            //System.out.println("|||||||");
        }
        System.out.println(catalans.length+" catalans.");
    }
    public static long[] catalans=new long[] {1,1,2,5,14,42,132,429,1430,4862,16796,58786,208012,742900,2674440,9694845,
            35357670,129644790,477638700,1767263190,6564120420l,24466267020l,91482563640l,343059613650l,1289904147324l,
            4861946401452l};
}
