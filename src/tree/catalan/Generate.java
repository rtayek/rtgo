package tree.catalan;
import static tree.catalan.Node.*;
import java.io.*;
import java.util.*;
import java.util.function.Consumer;
import utilities.*;
public class Generate {
    void linesToFile() {}
    void read() throws IOException {
        BufferedReader r=new BufferedReader(new FileReader(file));
        for(String line=r.readLine();line!=null;line=r.readLine()) lines.add(line);
    }
    String makeLineFromTrees(List<Node> trees) {
        String line="";
        for(Node tree:trees) {
            String encoded=encode(tree);
            System.out.println(encoded);
            line+=encoded+" ";
        }
        return line;
    }
    void loadMapFromFile() throws IOException {
        read();
        System.out.println(lines.size()+" lines ");
        // build map from lines.
        // can't use trees, use binary strings and them numbers.
        Integer nodes=0;
        for(String line:lines) {
            String[] words=line.split(" ");
            List<String> w=Arrays.asList(words);
            ArrayList<String> strings=new ArrayList<>(w);
            map3.put(nodes,strings); // each line encodes the entire tree.
            ++nodes;
        }
    }
    void run() throws IOException {
        //if(file.exists()) file.delete();
        if(file.exists()) {
            loadMapFromFile();
            System.out.println("loaded map:"+map);
            // how to add entry?
            // we seem to need all of the nodes to generate the next set of tree.
            // but it seems like we could just decode the first level down?
        } else {
            System.out.println("before: "+map);
            int nodes=0;
            Holder<Integer> data=new Holder<>(0);
            List<Node> trees=Node.allBinaryTrees(nodes,data);
            System.out.println(trees);
            String line=makeLineFromTrees(trees);
            lines.add(line);
            map3.put(nodes,lines); // may not be put in map?
            System.out.println("after: "+map);
            System.out.println("lines: "+lines);
            BufferedWriter w=new BufferedWriter(new FileWriter(file));
            for(String line2:lines) w.write(line2+'\n');
            w.close();
            if(true) return;
            System.out.println();
            trees=Node.allBinaryTrees(1,data);
            System.out.println(trees);
            trees=Node.allBinaryTrees(2,data);
            System.out.println(trees);
        }
    }
    ArrayList<Node> allBinaryTrees_(final int nodes,Holder<Integer> data,int depth) {
        ArrayList<Node> trees=new ArrayList<>();
        boolean done=false;
        if(true/* force a null?*/&&nodes==0) {
            trees.add(null);
            done=true;
        } else {
            if(map.containsKey(nodes)) { trees=map.get(nodes); done=true; }
            //done=false; // test. seems to cause concurrent
            if(done) {
                if(verbose) for(Node tree:trees) System.out.println("\tgot: "+tree+" "+encode(tree));
                if(verbose) System.out.println("\tgot "+trees.size()+" trees.");
            } else {
                if(verbose) System.out.println("building trees with "+nodes+" nodes.");
                for(int i=0;i<nodes;i++) { // this will fall through if nodes=0!
                    //System.gc();
                    if(verbose) System.out.println("i: "+i);
                    //ArrayList<Node> lefts=allBinaryTrees_(i,data,depth);
                    // does not help!
                    //for(Node left:lefts) {
                    for(Node left:allBinaryTrees_(i,data,depth)) {
                        if(verbose) System.out.println("\tleft  i: "+i);
                        for(Node right:allBinaryTrees_(nodes-1-i,data,depth)) {
                            if(verbose) System.out.println("\tright nodes-i: "+(nodes-i));
                            //System.out.println("construct node for "+i+"/"+nodes+" keys: "+map.keySet());
                            ++data.t;
                            Node node=new Node(data.t,left,right);
                            if(verbose) System.out.println("created nod: "+node+" "+encode(node));
                            final List<Integer> datas=new ArrayList<>();
                            Consumer<Node> add=x->datas.add(x.data);
                            preOrder(node,add);
                            // not using datas?
                            //System.out.println("data values: "+datas);
                            trees.add(node);
                            String encoded=encode(node);
                            if(verbose) System.out.println(encoded+" "+node);
                            if(verbose) System.out.println("1 node: "+node);
                        }
                    }
                }
            } // at least one node
        }
        if(nodes==0) {
            //System.err.println("zero node, trees: "+trees);
        } else { // do this earlier
            ArrayList<String> encoded=new ArrayList<>();
            for(Node tree:trees) encoded.add(encode(tree));
            if(verbose) System.out.println("putting encoded trees: "+encoded);
            if(verbose) System.out.println("\tputting: "+trees.size()+" trees.");
            map.put(nodes,trees);
        }
        if(verbose) System.out.println("exit");
        return trees;
    }
    ArrayList<Node> allBinaryTrees(int nodes,Holder<Integer> data) { return allBinaryTrees_(nodes,data,0); }
    public static void main(String[] args) throws IOException {
        Generate generate=new Generate();
        //generate.run();
        Et et=new Et();
        for(int n=0;n<=12;++n) {
            et.reset();
            Holder<Integer> data=new Holder<>(0);
            ArrayList<Node> trees=generate.allBinaryTrees(n,data);
            System.out.println(n+" nodes+: "+trees.size()+" trees in "+et);
            System.gc();
        }
    }
    File file=new File("catalanumbers.txt");
    boolean useGet=false;
    ArrayList<String> lines=new ArrayList<>();
    final SortedMap<Integer,ArrayList<Node>> map=new TreeMap<>();
    static SortedMap<Integer,ArrayList<String>> map3=new TreeMap<>();
    static boolean verbose=false;
}
