package tree.catalan;
import static tree.catalan.Node.encode;
import java.io.*;
import java.util.*;
import utilities.Holder;
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
            map.put(nodes,strings);
            ++nodes;
        }
    }
    void run() throws IOException {
        Node.usingMap2=false;
        Node.map.clear();
        //if(file.exists()) file.delete();
        if(file.exists()) {
            loadMapFromFile();
            System.out.println("loaded map:"+map);
        } else {
            System.out.println("before: "+map);
            int nodes=0;
            Holder<Integer> data=new Holder<>(0);
            List<Node> trees=Node.allBinaryTrees(nodes,data);
            System.out.println(trees);
            String line=makeLineFromTrees(trees);
            lines.add(line);
            map.put(nodes,lines); // may not be put in map?
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
    public static void main(String[] args) throws IOException { new Generate().run(); }
    File file=new File("catalanumbers.txt");
    ArrayList<String> lines=new ArrayList<>();
    final SortedMap<Integer,ArrayList<String>> map=new TreeMap<>();
}
