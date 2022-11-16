package tree.catalan;
import java.util.List;
import utilities.Holder;
public class Catalan {
    tree.catalan.Node x;
    public static final long c(final int n,final int r) { // binomial coefficient
        long i,p;
        if(r<0||n<0||r>n) p=0;
        else if(r==0) p=1;
        else if(r>n-r) p=c(n,n-r);
        else {
            for(p=1,i=n;i>=n-r+1;i--) p=p*i;
            p=p/f(r);
        }
        return (int)p;
    }
    static final long f(final int n) { // factorial
        long i,p;
        if(n<=1) p=1;
        else for(p=n,i=2;i<=n-1;i++) p=p*i;
        return(p);
    }
    //C_n       =       1/(n+1)(2n; n)
    public static long catalan2(int n) { return c(2*n,n)/(n+1); }
    public static long catalan(int n) { return f(2*n)/(f(n+1)*f(n)); }
    private static void print(Node node) {
        if(node!=null) {
            String string=node.encode();
            int foo=Integer.parseInt(string,2);
            System.out.print(foo);
            System.out.print(" "+string);
            System.out.println(" "+node.toXString());
        } else {
            System.out.print("0");
            String string="";
            System.out.print(" "+string);
            System.out.println(" "+"()");
        }
    }
    public static void main(String[] args) {
        // uses the Node class here in this package
        for(int nodes=0;nodes<5;++nodes) { // 15 takes a few minutes.
            Node.ids=0;
            Holder<Integer> data=new Holder<>(0);
            List<Node> ltrees=Node.allBinaryTrees(nodes,data);
            System.out.println(nodes+" nodes. has "+ltrees.size()+" trees\n");
            for(Node node:ltrees) {
                System.out.println("|||");
                print(node);
            }
            System.out.println("|||||||");
        }
    }
    public static long[] catalans=new long[] {1,1,2,5,14,42,132,429,1430,4862,16796,58786,208012,742900,2674440,9694845,
            35357670,129644790,477638700,1767263190,6564120420l,24466267020l,91482563640l,343059613650l,1289904147324l,
            4861946401452l};
}
