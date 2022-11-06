package io;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
public class FixColorNames { // generate ctor call for sequence enums.
    public static void main(String[] args) throws IOException {
        Path path=Path.of("src/io/ColorLogs.java");
        List<String> lines=Files.readAllLines(path);
        String target="color_";
        for(String line:lines) {
            //System.out.println(line);
            String l=line;
            if(line.contains("static final String")) if(line.contains(target)) {
                // System.out.println("line: "+l);
                int leftIndex=l.indexOf(target);
                int rightIndex=leftIndex+target.length();
                String left=l.substring(0,rightIndex);
                String right=l.substring(rightIndex);
                //System.out.println("left: "+left);
                //System.out.println("right:"+right);
                //System.out.println(left+"|"+right);
                String[] words=right.split("=");
                //System.out.println("1 "+words[0]);
                //System.out.println("2 "+words[1]);
                String[] words2=words[1].split("\";");
                //System.out.println("3 "+words2[0]+'"');
                //System.out.println("4 "+words2[1]);
                String name=/*target+*/words[0].toLowerCase();
                String sequence=words2[0]+'"';
                System.out.println(name+'('+sequence+"), //");
            }
        }
        //System.out.println(lines);
    }
}
