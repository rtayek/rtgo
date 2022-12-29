package utilities;
import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.*;
import io.Init;
public class FindMainPrograms {
    static class Triple {
        Triple(File file,String clazz,String main) {
            if(file==null) System.exit(1);
            if(clazz==null) System.exit(1);
            if(main==null) System.exit(1);
            this.file=file;
            this.clazz=clazz;
            this.main=main;
        }
        @Override public String toString() { return "Triple [file="+file+", clazz="+clazz+", main="+main+"]"; }
        final File file;
        final String clazz;
        final String main;
    }
    static void fillFilesRecursively(File file,List<File> resultFiles) {
        if(file.isFile()) {
            if(file.getName().endsWith(javaExtension)) resultFiles.add(file);
        } else for(File child:file.listFiles()) { fillFilesRecursively(child,resultFiles); }
    }
    private static Class<?> processTriple(String name,Triple triple) {
        String dotted=dotted(triple.file);
        //System.out.println(name+"|"+dotted);
        // dotted will be the class with the filename
        // how to get others?
        // compare name with filename?
        Class<?> clazz=null;
        try {
            clazz=Class.forName(dotted);
        } catch(ClassNotFoundException e) {
            //System.out.println("caught: "+e);
            //e.printStackTrace();
        }
        return clazz;
    }
    private static String dotted(File file) {
        // remove top level source folder (i.e. src, tst).
        // convert package/Program.java to package.Program
        Path path=Path.of(file.toString());
        int n=path.getNameCount();
        path=path.subpath(1,n);
        String string=path.toString();
        if(string.endsWith(javaExtension)) string=string.substring(0,string.length()-javaExtension.length());
        string=string.replaceAll("\\\\",".");
        string=string.replaceAll("/",".");
        return string;
    }
    private static Pair<String,String> getClassName(String string) {
        String found=null;
        String name=null;
        List<String> words=Arrays.asList(string.split(" "));
        for(String target:targets) { if(words.contains(target)) { found=target; break; } }
        if(found!=null) { int index=words.indexOf(found); name=words.get(index+1); }
        return new Pair<String,String>(found,name);
    }
    private static List<Triple> makeTRiplesFromFiles() throws IOException {
        List<File> files=new ArrayList<>();
        for(String folder:sourceFolders) { fillFilesRecursively(new File(folder),files); }
        System.err.println(files.size()+" java files.");
        List<String> lines=new ArrayList<>();
        List<Triple> mains=new ArrayList<>();
        List<Triple> different=new ArrayList<>();
        for(File file:files) {
            //System.out.println(file);
            String lastClass=null;
            lines=Files.readAllLines(file.toPath(),StandardCharsets.UTF_8);
            for(String line:lines) {
                //System.out.println(line);
                // use targets
                if(line.contains("class ")||line.contains("interface ")||line.contains("enum ")) {
                    Pair<String,String> pair=getClassName(line);
                    String name=pair.second;
                    if(name!=null&&Utilities.isValidName(name))
                    //&&Utilities.isValidJavaIdentifier(name))
                    {
                        lastClass=line;
                        //System.out.println("valid: "+name);
                    } else System.out.println("not valid identifier: "+name+" "+line);
                    //System.out.println(lastClass);
                }
                if(line.contains(target)) {
                    Triple triple=new Triple(file,lastClass,line);
                    Pair<String,String> pair=getClassName(triple.clazz);
                    if(pair.first==null||pair.second==null) {
                        if(pair.first==null) System.out.println("found is null.");
                        if(pair.second==null) System.out.println("name is null.");
                        System.err.println("excluding: "+triple);
                    } else {
                        if(triple.file.toString().contains(pair.second+javaExtension)) {
                            mains.add(triple);
                            //System.out.println(pair+" "+triple);
                            Class<?> clazz=processTriple(pair.second,triple);
                            if(clazz!=null) {
                                //System.out.println("forname found class: "+clazz);
                            } else {
                                System.out.println("can not find class!: "+pair+" "+triple);
                                System.exit(0);
                            }
                        } else {
                            different.add(triple);
                            //System.out.println("different: "+pair.second+"!="+triple.file);
                        }
                    }
                }
            }
        }
        System.err.println(mains.size()+" mains.");
        System.err.println(different.size()+" different.");
        System.out.println("differebt: ----------------------------");
        if(true) {
            for(Triple triple:different) {
                Pair<String,String> pair=getClassName(triple.clazz);
                if(!triple.file.toString().contains(pair.second+javaExtension))
                    System.out.println("still different: "+pair.second+"!="+triple.file);
                System.out.println(pair+" "+triple);
                Class<?> clazz=processTriple(pair.second,triple);
                System.out.println("class: "+clazz);
            }
        }
        return mains;
    }
    public static void main(String[] args) throws IOException {
        List<Triple> mains=makeTRiplesFromFiles();
        // pairs look like (... <class> ... ,xyzzy  ... <main(> ...)
        System.out.println("after makeMainsFromFiles().");
        if(true) return;
        // lets find the classes
    }
    static String javaExtension=".java";
    static String mainsFilename="mains2.txt";
    static String projectFolder="code4";
    static Set<String> sourceFolders=Set.of("src","tst"/*,"slow"*//*,"suites"*/);
    static Set<String> targets=Set.of("class","interface","enum");
    static String target="public static void main(";
    static {
        System.out.println(Init.first);
    }
}
