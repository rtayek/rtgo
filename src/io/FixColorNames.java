package io;
import io.Logging;
import java.io.IOException;
import java.nio.file.*;
import java.util.List;
public class FixColorNames { // generate ctor call for sequence enums.
    public static void main(String[] args) throws IOException {
        Path path=Path.of("src/io/ColorLogs.java");
        List<String> lines=Files.readAllLines(path);
        String target="color_";
        for(String line:lines) {
            //Logging.mainLogger.info(line);
            String l=line;
            if(line.contains("static final String")) if(line.contains(target)) {
                // Logging.mainLogger.info("line: "+l);
                int leftIndex=l.indexOf(target);
                int rightIndex=leftIndex+target.length();
                String left=l.substring(0,rightIndex);
                String right=l.substring(rightIndex);
                //Logging.mainLogger.info("left: "+left);
                //Logging.mainLogger.info("right:"+right);
                //Logging.mainLogger.info(left+"|"+right);
                String[] words=right.split("=");
                //Logging.mainLogger.info("1 "+words[0]);
                //Logging.mainLogger.info("2 "+words[1]);
                String[] words2=words[1].split("\";");
                //Logging.mainLogger.info("3 "+words2[0]+'"');
                //Logging.mainLogger.info("4 "+words2[1]);
                String name=/*target+*/words[0].toLowerCase();
                String sequence=words2[0]+'"';
                Logging.mainLogger.info(name+'('+sequence+"), //");
            }
        }
        //Logging.mainLogger.info(lines);
    }
}
