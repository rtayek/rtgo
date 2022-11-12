package sgf;
public class SgfFiles {
    public static void main(String[] args) {
        int n=0;
        for(Object key:Parser.sgfDataKeySet()) System.out.println(n+++" "+key);
    }
}
