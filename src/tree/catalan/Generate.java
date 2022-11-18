package tree.catalan;
import static utilities.Utilities.getFileAsListOfStrings;
import java.io.File;
import java.util.*;
import utilities.Holder;
public class Generate {
    public static void main(String[] args) {
        Node.usingMap2=false;
        List<String> lines;
        File file=new File("catalanumbers.txt");
        if(file.exists()) {
            lines=getFileAsListOfStrings(file);
        }
        else {
            lines=new ArrayList<>();
            Holder<Integer> data=new Holder<>(0);
            List<Node> trees=Node.allBinaryTrees(0,data);

            System.out.println(trees);
            trees=Node.allBinaryTrees(1,data);
            System.out.println(trees);
            trees=Node.allBinaryTrees(2,data);
            System.out.println(trees);

            
        }
        
    }
}
