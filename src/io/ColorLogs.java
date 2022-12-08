package io;
import static utilities.Utilities.toObjects;
import java.util.*;
import java.util.Map.Entry;
import utilities.Pair;
public class ColorLogs {
    //https://stackoverflow.com/questions/54909752/how-to-change-the-util-logging-logger-printing-colour-in-logging-properties/60434521#60434521
    //https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println/5762502#5762502
    public static String coloredString(String string,String escapeSequence) {
        if(escapeSequence!=null) System.out.println("color: "+Arrays.asList(toObjects(escapeSequence.getBytes())));
        else System.out.println("escape sequence is null!");
        return color_RESET+(escapeSequence!=null?escapeSequence:blackOrWhite)+string+color_RESET;
        // try removing trailig reset?
    }
    public static String containsKey(String string) {
        String foundKey=null;
        for(String key:map.keySet()) if(string.contains(key)) { foundKey=key; break; }
        return foundKey;
    }
    public static String escapeSequence(String string) {
        String key=containsKey(string);
        return key!=null?map.get(key):blackOrWhite;
    }
    private static boolean containsAGTPKey(String string) {
        for(String key:gtpKeys) if(string.contains(key)) return true;
        return false;
    }
    private static String escapeSequence2(String string) {
        String key=null;
        if(containsAGTPKey(string));
        // this is very broken.
        return key!=null?map.get(key):color_YELLOW;
    }
    public static String quote(String escapeSequence) {
        StringBuffer stringBuffer=new StringBuffer();
        for(int i=0;i<escapeSequence.length();++i) stringBuffer.append('\\').append(escapeSequence.charAt(i));
        return stringBuffer.toString();
    }
    public static String color(String string) {
        String normalized=string.trim().replaceAll(" +"," "); // what is this for?
        String[] words=normalized.split(" ");
        String escapeSequence=null;
        if(words.length>=2) {
            String target=words[1];
            escapeSequence=escapeSequence(target);
            if(escapeSequence==null) { System.out.println(words[1]+" escape sequence is null!"); } //else System.out.println("escape sequence is: "+quote(escapeSequence));
        }
        String coloredString=coloredString(string,escapeSequence);
        //coloredString+=quote(escapeSequence);
        return coloredString;
    }
    private static void makeMap() {
        Pair<String,String> audioPair=new Pair<>("audio",blackOrWhite);
        Pair<String,String> nothingPair=new Pair<>("nothing",blackOrWhite);
        Pair<String,String> mainPair=new Pair<>("main",blackOrWhite);
        Pair<String,String> gamePair=new Pair<>("game",color_RED);
        Pair<String,String> recorderPair=new Pair<>("recorder",color_PURPLE);
        Pair<String,String> blackPair=new Pair<>("black",color_GREEN);
        Pair<String,String> whitePair=new Pair<>("white",color_CYAN);
        Pair<String,String> serverPair=new Pair<>("server",color_BLUE);
        Pair<String,String> modelPair=new Pair<>("model",color_YELLOW);
        Pair<String,String>[] pairs=new Pair/*<String,String>*/[] {audioPair,nothingPair,mainPair,gamePair,recorderPair,
                blackPair,whitePair,serverPair,modelPair};
        for(Pair<String,String> pair:pairs) map.put(pair.first,pair.second);
    }
    public static void printMap() { for(String key:map.keySet()) System.out.println(key+":"+quote(map.get(key))); }
    public static void test() {
        boolean foo=true;
        if(foo) {
            System.out.println("color1:");
            System.out.println(ColorLogs.coloredString("a nothing xx",ColorLogs.blackOrWhite));
            System.out.println(ColorLogs.coloredString("a main xx",ColorLogs.blackOrWhite));
            System.out.println(ColorLogs.coloredString("a game xx",ColorLogs.color_RED));
            System.out.println(ColorLogs.coloredString("a recorder xx",ColorLogs.color_PURPLE));
            System.out.println(ColorLogs.coloredString("a black xx",ColorLogs.color_GREEN));
            System.out.println(ColorLogs.coloredString("a white xx",ColorLogs.color_CYAN));
            System.out.println(ColorLogs.coloredString("a server xx",ColorLogs.color_BLUE));
            System.out.println(ColorLogs.coloredString("a model xx",ColorLogs.color_YELLOW));
            System.out.println("||||||||||");
            System.out.println("color2");
            for(String string:strings) {
                System.out.println(string+" "+color(string));
                //System.out.println("reset: "+RESET);
            }
            System.out.println("color3");
            for(Entry<String,String> key:map.entrySet())
                System.out.println(key.getKey()+" "+quote(key.getValue())+" "+key.getValue());
            return;
        }
    }
    public static void main(String[] args) { test(); }
    static {
        //  System.out.println("start static init color logs");
    }
    public static final String color_RESET="\033[0m"; // Text Reset
    // Regular Colors
    public static final String color_BLACK="\033[0;30m"; // BLACK
    public static final String color_RED="\033[0;31m"; // RED
    public static final String color_GREEN="\033[0;32m"; // GREEN
    public static final String color_YELLOW="\033[0;33m"; // YELLOW
    public static final String color_BLUE="\033[0;34m"; // BLUE
    public static final String color_PURPLE="\033[0;35m"; // PURPLE
    public static final String color_CYAN="\033[0;36m"; // CYAN
    public static final String color_WHITE="\033[0;37m"; // WHITE
    // Bold
    public static final String color_BLACK_BOLD="\033[1;30m"; // BLACK
    public static final String color_RED_BOLD="\033[1;31m"; // RED
    public static final String color_GREEN_BOLD="\033[1;32m"; // GREEN
    public static final String color_YELLOW_BOLD="\033[1;33m"; // YELLOW
    public static final String color_BLUE_BOLD="\033[1;34m"; // BLUE
    public static final String color_PURPLE_BOLD="\033[1;35m"; // PURPLE
    public static final String color_CYAN_BOLD="\033[1;36m"; // CYAN
    public static final String color_WHITE_BOLD="\033[1;37m"; // WHITE
    // Underline
    public static final String color_BLACK_UNDERLINED="\033[4;30m"; // BLACK
    public static final String color_RED_UNDERLINED="\033[4;31m"; // RED
    public static final String color_GREEN_UNDERLINED="\033[4;32m"; // GREEN
    public static final String color_YELLOW_UNDERLINED="\033[4;33m"; // YELLOW
    public static final String color_BLUE_UNDERLINED="\033[4;34m"; // BLUE
    public static final String color_PURPLE_UNDERLINED="\033[4;35m"; // PURPLE
    public static final String color_CYAN_UNDERLINED="\033[4;36m"; // CYAN
    public static final String color_WHITE_UNDERLINED="\033[4;37m"; // WHITE
    // Background
    public static final String color_BLACK_BACKGROUND="\033[40m"; // BLACK
    public static final String color_RED_BACKGROUND="\033[41m"; // RED
    public static final String color_GREEN_BACKGROUND="\033[42m"; // GREEN
    public static final String color_YELLOW_BACKGROUND="\033[43m"; // YELLOW
    public static final String color_BLUE_BACKGROUND="\033[44m"; // BLUE
    public static final String color_PURPLE_BACKGROUND="\033[45m"; // PURPLE
    public static final String color_CYAN_BACKGROUND="\033[46m"; // CYAN
    public static final String color_WHITE_BACKGROUND="\033[47m"; // WHITE
    // High Intensity
    public static final String color_BLACK_BRIGHT="\033[0;90m"; // BLACK
    public static final String color_RED_BRIGHT="\033[0;91m"; // RED
    public static final String color_GREEN_BRIGHT="\033[0;92m"; // GREEN
    public static final String color_YELLOW_BRIGHT="\033[0;93m"; // YELLOW
    public static final String color_BLUE_BRIGHT="\033[0;94m"; // BLUE
    public static final String color_PURPLE_BRIGHT="\033[0;95m"; // PURPLE
    public static final String color_CYAN_BRIGHT="\033[0;96m"; // CYAN
    public static final String color_WHITE_BRIGHT="\033[0;97m"; // WHITE
    // Bold High Intensity
    public static final String color_BLACK_BOLD_BRIGHT="\033[1;90m"; // BLACK
    public static final String color_RED_BOLD_BRIGHT="\033[1;91m"; // RED
    public static final String color_GREEN_BOLD_BRIGHT="\033[1;92m"; // GREEN
    public static final String color_YELLOW_BOLD_BRIGHT="\033[1;93m";// YELLOW
    public static final String color_BLUE_BOLD_BRIGHT="\033[1;94m"; // BLUE
    public static final String color_PURPLE_BOLD_BRIGHT="\033[1;95m";// PURPLE
    public static final String color_CYAN_BOLD_BRIGHT="\033[1;96m"; // CYAN
    public static final String color_WHITE_BOLD_BRIGHT="\033[1;97m"; // WHITE
    // High Intensity backgrounds
    public static final String color_BLACK_BACKGROUND_BRIGHT="\033[0;100m";// BLACK
    public static final String color_RED_BACKGROUND_BRIGHT="\033[0;101m";// RED
    public static final String color_GREEN_BACKGROUND_BRIGHT="\033[0;102m";// GREEN
    public static final String color_YELLOW_BACKGROUND_BRIGHT="\033[0;103m";// YELLOW
    public static final String color_BLUE_BACKGROUND_BRIGHT="\033[0;104m";// BLUE
    public static final String color_PURPLE_BACKGROUND_BRIGHT="\033[0;105m"; // PURPLE
    public static final String color_CYAN_BACKGROUND_BRIGHT="\033[0;106m"; // CYAN
    public static final String color_WHITE_BACKGROUND_BRIGHT="\033[0;107m"; // WHITE
    //
    public static String blackOrWhite=color_WHITE; // hack for not knowing background color
    //public static final String[] keys=new String[]{"main","game","recorder","black","white","server","model"};
    //public static final Pair<String,String> model=new Pair<>("model",PURPLE);
    public static final Set<String> gtpKeys=Set.of("recorder","black","white","model");
    public static final String[] strings=new String[] {"a nothing","a main foo","a game foo","a recorder bar",
            "a black xxx","a white xxx","a server xxx","a model xxx",};
    static final Map<String,String> map=new LinkedHashMap<>();
    static {
        makeMap();
    }
    static boolean lightTheme=true;
}
