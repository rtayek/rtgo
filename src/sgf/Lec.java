package sgf;
import java.io.*;
import java.util.*;
import io.IO;
import utilities.Pair;
public class Lec {
    static int howSimilar(String previous,String string) {
        int n=Integer.MAX_VALUE;
        for(int i=0;i<Math.min(previous.length(),string.length());i++) if(previous.charAt(i)!=string.charAt(i)) {
            n=i/5;
            break;
        }
        return n;
    }
    static void moveSequences(StringBuffer stringBuffer,List<Pair<String,String>> pairs,File dir,String filename) {
        Pair<String,String> previous;
        SgfNode games=new Parser().parse(IO.toReader(new File(dir,filename)));
        stringBuffer.setLength(0);
        Traverser traverser=new Traverser(new SgfAcceptorImpl() {
            @Override public void accept(SgfNode node) {
                for(SgfProperty property:node.properties)
                    if(/*moves<10&&*/(property.p().equals(P.W)||property.p().equals(P.B))) {
                        stringBuffer.append(property.toString());
                        moves++;
                    }
            }
            int moves;
        });
        traverser.visitLeft(games); // only main line maybe?
        // System.out.println(" "+filename);
        Pair<String,String> pair=new Pair<>(filename,stringBuffer.toString());
        // System.out.println(pair.first+" "+pair.second);
        pairs.add(pair);
        previous=pair;
    }
    static void processMoveSequences(List<Pair<String,String>> pairs,Map<String,Set<Pair<String,String>>> similar) {
        Pair<String,String> previous;
        Collections.sort(pairs,new Comparator<Pair<String,String>>() {
            @Override public int compare(Pair<String,String> o1,Pair<String,String> o2) { // yuck!
                return o1.second.compareTo(o2.second);
            }
        });
        previous=null;
        for(Pair<String,String> pair:pairs) {
            if(previous!=null) {
                int n=howSimilar(previous.second,pair.second);
                System.out.println("equal up to: "+n+" moves.");
                System.out.println(pair.second);
            }
            previous=pair;
        }
        System.out.println(pairs.size()+" files.");
        pairs.forEach((Pair<String,String> pair)->System.out.println(pair.second+" "+pair.first));
        int requiredMoves=7;
        for(Pair<String,String> pair:pairs) if(similar.size()==0) {
            Set<Pair<String,String>> set=new LinkedHashSet<>();
            set.add(pair);
            similar.put(pair.second,set);
        } else {
            boolean added=false;
            for(String key:similar.keySet()) if(howSimilar(pair.second,key)>=requiredMoves) {
                similar.get(key).add(pair);
                added=true;
                break;
            }
            if(!added) {
                Set<Pair<String,String>> set=new LinkedHashSet<>();
                set.add(pair);
                similar.put(pair.second,set);
            }
        }
        System.out.println(similar.size()+" equivalence classes.");
        for(String key:similar.keySet()) System.out.print(similar.get(key).size()+" ");
        System.out.println();
    }
    static void moveSets(int max,StringBuffer stringBuffer,Parser parser,File dir,final Set<String> moves,
            Set<Pair<String,Set<String>>> pairs2,String filename) {
        SgfNode games=parser.parse(IO.toReader(new File(dir,filename)));
        stringBuffer.setLength(0);
        moves.clear();
        Traverser traverser=new Traverser(new SgfAcceptorImpl() {
            @Override public void accept(SgfNode node) {
                for(SgfProperty property:node.properties) if(i<max&&(property.p().equals(P.W)||property.p().equals(P.B))) {
                    moves.add(property.toString());
                    i++;
                }
            }
            int i;
        });
        traverser.visitLeft(games);
        Pair<String,Set<String>> pair=new Pair<>(filename,new LinkedHashSet<String>(moves));
        // System.out.println(pair.first+" "+pair.second);
        pairs2.add(pair);
    }
    public static void main(String args[]) {
        StringBuffer stringBuffer=new StringBuffer();
        List<Pair<String,String>> pairs=new ArrayList<>();
        Parser parser=new Parser();
        File dir=new File("D:/ray/sgf/lec/black");
        // some of the files have eric playing black
        Pair<String,String> previous=null;
        Map<String,Set<Pair<String,String>>> similar=new LinkedHashMap<>();
        Map<String,Set<Pair<String,String>>> equivalent=new LinkedHashMap<>();
        final Set<String> moves=new LinkedHashSet<String>();
        Set<Pair<String,Set<String>>> pairs2=new LinkedHashSet<>();
        int requiredMoves=6;
        final boolean reviewOnly=true;
        for(String filename:dir.list(new FilenameFilter() {
            @Override public boolean accept(File dir,String name) {
                if((!reviewOnly||name.startsWith("review"))&&name.endsWith(".sgf")) return true;
                return false;
            }
        })) {
            // moveSequences(stringBuffer,pairs,parser,dir,filename);
            moveSets(requiredMoves,stringBuffer,parser,dir,moves,pairs2,filename);
        }
        // processMoveSequences(pairs,similar);
        System.out.println(pairs2);
        Map<Set<String>,Set<String>> map=new LinkedHashMap<>();
        for(Pair<String,Set<String>> pair:pairs2)
            if(map.keySet().contains(pair.second)) map.get(pair.second).add(pair.first);
            else {
                Set<String> filenames=new TreeSet<>();
                filenames.add(pair.first);
                map.put(pair.second,filenames);
            }
        map.forEach((moves2,filenames)-> { // only works if
            // required==strings.length!
            System.out.println("map: "+filenames+" "+moves2);
        });
        String strings[]= {"B[qd]","W[oq]","B[dc]","W[oc]","B[co]","W[jj]"};
        // String strings[]= {"B[qd]"};
        Set<String> target=new LinkedHashSet<>();
        // ;
        for(int i=0;i<Math.min(strings.length,requiredMoves);i++) target.add(strings[i]);
        System.out.println("target: "+target);
        map.forEach((moves2,filenames)-> {
            if(moves2.equals(target)) {
                System.out.println("found: "+filenames.size()+": ");
                for(String filename:filenames) System.out.println(filename);
                System.out.print("found: "+filenames.size()+" "+filenames+" "+moves2);
            }
        });
    }
    // 1 1 1 1 1 1 2 1 1 3 1 1 1 4 2 1 2 1 1 1 2 2 2 139 8 1 1 4 2 12 1 1 2 1 1
    // 1 1 1 1 1 1 2 1 1 1 2 1 1 1 1 3 2 1 2 1 1 1 1 1 1 1 2 23 13 2 8 1 89 1 2
    // 8 1 1 1 3 2 12 1 1 2 1 1
}
