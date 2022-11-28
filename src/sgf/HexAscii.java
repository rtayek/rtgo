package sgf;
import java.util.*;

public class HexAscii {
    static char encode(byte nybble) {
        if(0<=nybble&&nybble<16) return ascii[nybble];
        throw new RuntimeException(nybble+" nybble is out of range!");
    }
    static byte decode(char c) {
        if('0'<=c&&c<='9') return (byte)(c-'0');
        else if('a'<=c&&c<='f') return (byte)(c-'a'+10);
        else {
            if(c=='\n') throw new RuntimeException("it's a line feed!");
            else throw new RuntimeException(Integer.valueOf(c)+" it's not a line feed!");
            //throw new RuntimeException("'"+c+"' is not in [0-9a-f]");
        }
    }
    public static String encodeFast(byte[] bytes) {
        StringBuffer stringBuffer=new StringBuffer();
        for(byte bite:bytes) stringBuffer.append(legal[bite]);
        return stringBuffer.toString();
    }
    public static String encode(byte[] bytes) {
        StringBuffer stringBuffer=new StringBuffer();
        for(byte bite:bytes) {
            char c1=encode((byte)(bite>>4&0x0f));
            char c2=encode((byte)(bite&0x0f));
            String twoCharacters=""+c1+c2;
            if(twoCharacters.contains("\n"))
                System.out.println("encoded a line feed!");
            if(!legalSet.contains(twoCharacters)) throw new RuntimeException();
            stringBuffer.append(twoCharacters);
        }
        return stringBuffer.toString();
    }
    public static String encode(String string) { return encode(string.getBytes()); }
    public static byte[] decode(String string) {
        byte[] bytes=new byte[string.length()/2];
        byte[] chars=string.getBytes();
        for(int i=0;i<chars.length/2;++i) {
            byte nybble1=decode((char)chars[2*i]);
            byte nybble2=decode((char)chars[2*i+1]);
            byte both=(byte)(16*nybble1+nybble2);
            bytes[i]=both;
        }
        return bytes;
    }
    public static String decodeToString(String string) { return new String(decode(string)); }
    static void generateLegalHexAscii() {
        for(int bite=0;bite<256;++bite) {
            byte[] expected=new byte[] {(byte)bite};
            String string=HexAscii.encode(expected);
            byte[] actual=HexAscii.decode(string);
            String string2=HexAscii.encode(actual);
            if(!string.equals(string2)) System.out.println(string+"!="+string2);
            System.out.print('"'+string+"\",");
        }
        System.out.println();
    }
    public static void main(String[] args) {
        //generateLegalHexAscii();
        System.out.println(legalSet);
        String expected="(;)(;)";
        System.out.println(expected);
        String encoded=encode(expected);
        System.out.println(encoded);
        String decoded=decodeToString(encoded);
        System.out.println(decoded);
    }
    public static final char[] ascii=new char[] {'0','1','2','3','4','5','6','7','8','9','a','b','c','d','e','f'};
    public static String[] legal=new String[] {"00","01","02","03","04","05","06","07","08","09","0a","0b","0c","0d",
            "0e","0f","10","11","12","13","14","15","16","17","18","19","1a","1b","1c","1d","1e","1f","20","21","22",
            "23","24","25","26","27","28","29","2a","2b","2c","2d","2e","2f","30","31","32","33","34","35","36","37",
            "38","39","3a","3b","3c","3d","3e","3f","40","41","42","43","44","45","46","47","48","49","4a","4b","4c",
            "4d","4e","4f","50","51","52","53","54","55","56","57","58","59","5a","5b","5c","5d","5e","5f","60","61",
            "62","63","64","65","66","67","68","69","6a","6b","6c","6d","6e","6f","70","71","72","73","74","75","76",
            "77","78","79","7a","7b","7c","7d","7e","7f","80","81","82","83","84","85","86","87","88","89","8a","8b",
            "8c","8d","8e","8f","90","91","92","93","94","95","96","97","98","99","9a","9b","9c","9d","9e","9f","a0",
            "a1","a2","a3","a4","a5","a6","a7","a8","a9","aa","ab","ac","ad","ae","af","b0","b1","b2","b3","b4","b5",
            "b6","b7","b8","b9","ba","bb","bc","bd","be","bf","c0","c1","c2","c3","c4","c5","c6","c7","c8","c9","ca",
            "cb","cc","cd","ce","cf","d0","d1","d2","d3","d4","d5","d6","d7","d8","d9","da","db","dc","dd","de","df",
            "e0","e1","e2","e3","e4","e5","e6","e7","e8","e9","ea","eb","ec","ed","ee","ef","f0","f1","f2","f3","f4",
            "f5","f6","f7","f8","f9","fa","fb","fc","fd","fe","ff",};
    public static final SortedSet<String> legalSet=new TreeSet<>(Arrays.asList(legal));
}
