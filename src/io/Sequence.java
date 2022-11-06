package io;
import java.util.*;
import server.NamedThreadGroup;
enum Sequence {
    reset("\033[0m"), //
    black("\033[0;30m"), //
    red("\033[0;31m"), //
    green("\033[0;32m"), //
    yellow("\033[0;33m"), //
    blue("\033[0;34m"), //
    purple("\033[0;35m"), //
    cyan("\033[0;36m"), //
    white("\033[0;37m"), //
    black_bold("\033[1;30m"), //
    red_bold("\033[1;31m"), //
    green_bold("\033[1;32m"), //
    yellow_bold("\033[1;33m"), //
    blue_bold("\033[1;34m"), //
    purple_bold("\033[1;35m"), //
    cyan_bold("\033[1;36m"), //
    white_bold("\033[1;37m"), //
    black_underlined("\033[4;30m"), //
    red_underlined("\033[4;31m"), //
    green_underlined("\033[4;32m"), //
    yellow_underlined("\033[4;33m"), //
    blue_underlined("\033[4;34m"), //
    purple_underlined("\033[4;35m"), //
    cyan_underlined("\033[4;36m"), //
    white_underlined("\033[4;37m"), //
    black_background("\033[40m"), //
    red_background("\033[41m"), //
    green_background("\033[42m"), //
    yellow_background("\033[43m"), //
    blue_background("\033[44m"), //
    purple_background("\033[45m"), //
    cyan_background("\033[46m"), //
    white_background("\033[47m"), //
    black_bright("\033[0;90m"), //
    red_bright("\033[0;91m"), //
    green_bright("\033[0;92m"), //
    yellow_bright("\033[0;93m"), //
    blue_bright("\033[0;94m"), //
    purple_bright("\033[0;95m"), //
    cyan_bright("\033[0;96m"), //
    white_bright("\033[0;97m"), //
    black_bold_bright("\033[1;90m"), //
    red_bold_bright("\033[1;91m"), //
    green_bold_bright("\033[1;92m"), //
    yellow_bold_bright("\033[1;93m"), //
    blue_bold_bright("\033[1;94m"), //
    purple_bold_bright("\033[1;95m"), //
    cyan_bold_bright("\033[1;96m"), //
    white_bold_bright("\033[1;97m"), //
    black_background_bright("\033[0;100m"), //
    red_background_bright("\033[0;101m"), //
    green_background_bright("\033[0;102m"), //
    yellow_background_bright("\033[0;103m"), //
    blue_background_bright("\033[0;104m"), //
    purple_background_bright("\033[0;105m"), //
    cyan_background_bright("\033[0;106m"), //
    white_background_bright("\033[0;107m"), //
    ;
    Sequence(String sequence) { this.sequence=sequence; } //
    public static String color(String string,String name) {
        String coloredString=string;
        Sequence sequence=containsAKey(name);
        if(sequence!=null) coloredString=sequence.coloredString(string);
        return coloredString;
    }
    static Sequence containsAKey(String string) {
        // should restrict looking for key to second word.
        // may have spaces, make a lisy
        SortedMap<Integer,String> indexToKey2=new TreeMap<>();
        for(String key:map.keySet()) if(string.contains(key)) { indexToKey2.put(string.indexOf(key),key); }
        if(indexToKey2.size()>0) {
            if(indexToKey2.size()>1) System.out.println(indexToKey2);
            String key=indexToKey2.get(indexToKey2.firstKey());
            return map.get(key);
        } else return blackOrWhite;
    }
    public String coloredString(String string) {
        return reset.sequence+sequence+string+reset.sequence;
        // try removing trailig reset?
    }
    static String color(Sequence sequence) { return sequence.coloredString(ColorLogs.quote(sequence.sequence)); }
    public static void main(String[] args) {
        System.out.println("first: "+Init.first);
        System.out.println("foreground"+" "+ColorLogs.quote(blackOrWhite.sequence)+" "+color(blackOrWhite));
        for(String key:map.keySet()) {
            Sequence sequence=map.get(key);
            System.out.println(key+":"+ColorLogs.quote(blackOrWhite.sequence)+' '+color(sequence));
        }
        System.out.println("foreground"+" "+ColorLogs.quote(blackOrWhite.sequence)+" "+color(blackOrWhite));
    }
    final String sequence;
    static Sequence blackOrWhite=white; // depends on eclipse theme or console foreground.
    static Map<String,Sequence> map=new LinkedHashMap<>();
    static Sequence[] colorsForNamedThread=new Sequence[] {red,blue,purple,green,cyan,yellow};
    static {
        for(String name:NamedThreadGroup.nameToColorIndex.keySet())
            map.put(name,colorsForNamedThread[NamedThreadGroup.nameToColorIndex.get(name)]);
        //map.put("nothing",blackOrWhite);
        //map.put("main",blackOrWhite);
    }
}
