package sgf;
import static io.Logging.parserLogger;
import static sgf.SgfNode.sgfRoundTripTwice;
import java.io.*;
import java.util.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.logging.Level;
import io.*;
import sgf.combine.Combine;
// https://github.com/search?q=sgf4j
public class Parser {
    private Parser() {}
    private char read() throws IOException {
        int c=reader.read();
        if(c==-1) {
            if(endOfFile) throw new RuntimeException("line: "+line+" "+s.length()+" "+s+" eof!");
            endOfFile=true;
        }
        return((char)c);
    }
    private boolean isDelimiter(char c) {
        for(int i=0;i<delimiter.length;++i) if(delimiter[i]==c) { lastDelimiter=c; return true; }
        return false;
    }
    private boolean nextIsDelimiter() throws IOException {
        for(char c=read();;c=read()) if(Character.isWhitespace(c)||c=='\r');
        else {
            reader.unread(c);
            return isDelimiter(c);
        }
    }
    private String getToken() throws IOException {
        s.delete(0,s.length());
        for(boolean done=false;!done;) {
            char c=read();
            if(Character.isWhitespace(c)||c=='\r') {
                if(s.length()>0) done=true;
            } else if(isDelimiter(c)) {
                if(s.length()==0) s.append(c);
                else reader.unread(c);
                done=true;
            } else s.append(c);
        }
        parserLogger.finest("token="+s.toString());
        return s.toString();
    }
    private String getUntilClosingBracket() throws IOException {
        s.delete(0,s.length());
        boolean lastWasEscaped=false;
        for(char c=read();c!=endCValueCharacter||lastWasEscaped;c=read()) {
            if(lastWasEscaped) {
                lastWasEscaped=false;
                switch(c) {
                    case '\\':
                        s.append('\\').append('\\');
                        break;
                    case '\n':
                        line++;
                        break;
                    case '\r':
                        break;
                    default:
                        s.append('\\').append(c);
                }
            } else if(c=='\\') lastWasEscaped=true;
            else {
                lastWasEscaped=false;
                if(c=='\r');
                else s.append(c);
            }
        }
        reader.unread(endCValueCharacter);
        return s.toString();
    }
    private List<String> cValues() throws IOException {
        List<String> list=new LinkedList<>();
        for(;nextIsDelimiter()&&lastDelimiter==startCValueCharacter;) {
            String token=getToken();
            String value=getUntilClosingBracket();
            token=getToken();
            if(token.charAt(0)!=endCValueCharacter||token.length()!=1)
                throw new RuntimeException("missing "+endCValueCharacter);
            list.add(value);
        }
        return list;
    }
    private SgfProperty getProperty() throws IOException {
        String id=getToken();
        if(isDelimiter(id.charAt(0))) { reader.unread(id.charAt(0)); return null; }
        P p=P.idToP.get(id);
        if(p==null) {
            parserLogger.fine("can not find property with id="+id+", last delimiter is: "+lastDelimiter);
            return null;
        }
        indent.in();
        List<String> list=cValues();
        indent.out();
        return new SgfProperty(p,list);
    }
    private SgfNode node() throws IOException {
        SgfNode node=new SgfNode();
        while(!nextIsDelimiter()) {
            indent.in();
            SgfProperty property=getProperty();
            indent.out();
            if(property!=null) node.add(property);
            else parserLogger.info(indent.indent()+"null property");
        }
        if(node.properties==null) node.properties=new ArrayList<>(); /* maybe this is a mistake? */
        return(node);
    }
    private SgfNode sequence() throws IOException {
        SgfNode first=node();
        Logging.mainLogger.fine("first node: "+first);
        for(SgfNode node=first;nextIsDelimiter()&&lastDelimiter==startNodeCharacter;node=node.left) {
            getToken();
            node.left=node();
        }
        return first;
    }
    private String sequenceToString(SgfNode node) {
        // like main line?
        StringBuffer s=new StringBuffer();
        for(SgfNode n=node;n!=null;n=n.left) s.append(n);
        return s.toString();
    }
    private SgfNode parse() throws IOException { // recursive
        SgfNode sequence=null;
        if(!nextIsDelimiter()||lastDelimiter!=startNodeCharacter) {
            parserLogger.info("missing startNodeCharacter"+startNodeCharacter);
            return sequence;
        }
        SgfNode lastNode=null;
        boolean firstVariation=true;
        String token=getToken();
        if(!token.equals(";")) parserLogger.warning("token is not semicolon: "+token);
        sequence=sequence();
        parserLogger.fine("added sequence: "+sequenceToString(sequence));
        lastNode=sequence.lastDescendant();
        if(lastNode==null) throw new RuntimeException("node() returned null!");
        //parserLogger.info("added sequence: '"+sequenceToString(sequence)+'\'');
        while(nextIsDelimiter()&&lastDelimiter==startGameCharacter) {
            token=getToken();
            if(lastNode==null) throw new RuntimeException("last==null");
            indent.in();
            SgfNode lastDescendant=lastNode.lastDescendant();
            SgfNode game=parse(/* lastDescendant */); // recurse
            if(firstVariation) {
                if(lastDescendant.right!=null) throw new RuntimeException("lastDescendant.right!=null");
                lastDescendant.left=game; // add variation as left
            } else {
                if(lastNode.left==null) throw new RuntimeException("lastNode.left==null");
                lastNode.left.addSibling(game);
            }
            indent.out();
            firstVariation=false;
            if(!nextIsDelimiter()||lastDelimiter!=endGameCharacter)
                // probably should use parser exception
                throw new RuntimeException("missing endGameCharacter"+endGameCharacter);
            getToken();
        }
        return sequence;
    }
    public static SgfNode restoreSgf(Reader reader) { SgfNode games=new Parser().parse(reader); return games; }
    private SgfNode parse(Reader reader) {
        if(reader==null) return null;
        try {
            this.reader=new PushbackReader(reader);
            endOfFile=false;
            line=0;
            SgfNode first=null;
            main:while(!endOfFile) {
                while(!nextIsDelimiter()) {
                    if(endOfFile) break main;
                    String token=getToken();
                    parserLogger.fine("sgf parser skipping: "+token);
                    if(endOfFile) break main;
                }
                String token=getToken();
                if(token==null) break main;
                else if(token.equals(startGameString)) {
                    indent.in();
                    SgfNode game=parse();
                    indent.out();
                    if(first==null) first=game;
                    else {
                        parserLogger.info("more than one game!");
                        first.addSibling(game);
                    }
                }
            }
            if(first==null) parserLogger.warning("first is null! (no games)");
            else if(first.siblings()>0) parserLogger.info("returning more than one game!");
            return first;
        } catch(IOException e) {}
        parserLogger.severe("parser return null!");
        return null;
    }
    private static void combineAndCheckKogosJosekiDictionary() throws IOException,Exception { // this has been sorta replaced by code in the test case.
        Reader reader=IO.toReader(new File(Combine.pathToHere,"KogosJosekiDictionary.sgf"));
        boolean ok=sgfRoundTripTwice(reader);
        if(!ok) throw new Exception("test fails");
    }
    public static Collection<Object> sgfDataKeySet() { return new ArrayList<>(Parser.sgfData.keySet()); }
    private static void collectSgfFiles(Set<Object> objects,File[] files) {
        for(File file:files) if(file.isFile()) {
            if(file.exists()) {
                if(file.getName().endsWith(".sgf")) {
                    if(!file.getName().startsWith("KogosJosekiDictionary")) // exclude for now
                        //System.out.println("ok "+file);
                        if(!badSgfFiles.contains(file)) objects.add(file);
                } else System.out.println(file+" is not an sgf file!");
            } else System.out.println(file+" soes not exist!");
        } else if(file.isDirectory()) {
            collectSgfFiles(objects,file.listFiles());
        } else System.out.println("strange: "+file);
    }
    private static void collectSgfFiles(String dir,Set<Object> objects) {
        System.out.println(new File(dir));
        File[] files=new File(dir).listFiles();
        if(files!=null) System.out.println("has "+files.length+" files.");
        else System.out.println("files is null!");
        collectSgfFiles(objects,files);
    }
    public static Collection<Object> sgfFiles() { return sgfFiles("sgf"); }
    public static Collection<Object> sgfFiles(String dir) {
        Set<Object> objects=new LinkedHashSet<>();
        collectSgfFiles(dir,objects);
        return objects;
    }
    public static String getSgfData(Object key) {
        String sgf=null;
        if(key==null) throw new RuntimeException(key+" key is null!");
        if(key==null) return null;
        if(key instanceof String) sgf=sgfData.get(key);
        else if(key instanceof File) sgf=utilities.Utilities.fromFile((File)key);
        else {
            System.out.println(key+" is not a string or a file!");
            IO.stackTrace(10);
            //System.exit(1);
            throw new RuntimeException(key+" is not a string or a file!");
        }
        return sgf;
    }
    public static Set<Object> findMultipleGames(Set<Object> objects) {
        Set<Object> many=new LinkedHashSet<>();
        for(Object key:objects) {
            String sgf=getSgfData(key);
            int p=parentheses(sgf);
            if(p!=0) System.out.println("parentheses count: "+p);
            SgfNode games=restoreSgf(new StringReader(sgf));
            if(games!=null) if(games.right!=null) {
                many.add(key);
                System.out.println(key+" has more than one game: "+games.right);
            }
        }
        return many;
    }
    public static int parentheses(String string) { // parentheses
        int count=0;
        if(string!=null) for(Character c:string.toCharArray()) {
            if(c.equals('(')) ++count;
            else if(c.equals(')')) --count;
            if(count<0) System.out.println("count is negative!");
        }
        return count;
    }
    public static void main(String[] argument) throws Exception {
        System.out.println(Init.first);
        Logging.setLevels(Level.OFF);
        //System.out.println("main "+god.et);
        //combineAndCheckKogosJosekiDictionary();
        //System.out.println(sgfData);
        List<Object> objects=new ArrayList<>();
        objects.addAll(sgfDataKeySet());
        //objects.addAll(sgfFiles());
        for(Object key:objects) {
            System.out.println("key: "+key);
            String expectedSgf=getSgfData(key);
            expectedSgf=SgfNode.options.prepareSgf(expectedSgf);
            SgfNode games=expectedSgf!=null?restoreSgf(new StringReader(expectedSgf)):null;
            if(games!=null) if(games.right!=null) System.out.println(key+" right: "+games.right);
            //System.out.println(games);
        }
    }
    Indent indent=new Indent("  ");
    PushbackReader reader;
    StringBuffer s=new StringBuffer();
    char lastDelimiter;
    boolean endOfFile;
    int line;
    public static final char startGameCharacter='(',endGameCharacter=')',startNodeCharacter=';',
            startCValueCharacter='[',endCValueCharacter=']';
    public static final String startGameString=startGameCharacter+"";
    public static final char[] delimiter= {startGameCharacter,endGameCharacter,startNodeCharacter,startCValueCharacter,
            endCValueCharacter};
    static final Runtime runtime=Runtime.getRuntime();
    public static final String justASemicolon=";";
    public static final String justSomeSemicolons=";;;";
    public static final String empty="()";
    public static final String twoEmpty=empty+empty;
    public static final String twoEmptyWithLinefeed=empty+'\n'+empty;
    public static final String reallyEmpty="";
    public static Set<String> illegalSgfKeys=new LinkedHashSet<>();
    // maybe just a set?
    static { // not legal sgf
        illegalSgfKeys.add(justASemicolon);
        illegalSgfKeys.add(justSomeSemicolons);
        illegalSgfKeys.add(empty);
        illegalSgfKeys.add(twoEmpty);
        illegalSgfKeys.add(twoEmptyWithLinefeed);
        illegalSgfKeys.add(reallyEmpty);
    }
    public static final String sgfExamleFromRedBean="""
                    (;FF[4]C[root](;C[a];C[b](;C[c])
                    (;C[d];C[e]))
                    (;C[f](;C[g];C[h];C[i])
                    (;C[j])))
                    """; // removed traling line feed
    public static final String startOfGame="(;GM[1]FF[4]VW[]CA[UTF-8])";
    public static final String oneMoveAtA1NoHeader="(;B[as])";
    public static final String oneMoveAtA1="(;FF[4];B[as])";
    public static final String simplevariations="(;GM[1]FF[4]CA[UTF-8]AP[Many Faces of Go:12.024]SZ[19](;B[qd]BL[892]WL[900](;W[oc]BL[892]WL[881])(;W[od]BL[888]WL[881];B[oc]BL[883]WL[881]))(;B[pq]BL[892]WL[890]))";
    public static final String comments1="(;C[root];C[left1];C[left1.left2](;C[left1.right1])(;C[right1]))";
    public static final String consecutiveMoves="(;SZ[19];B[as];B[ar])";
    public static final String emptyWithSemicolon="(;)";
    public static final String twoEmptyWithSemicolon="""
                    (;)
                    (;)
                    """;
    public static final String noVariation="(;FF[4]GM[1]SZ[19];B[aa];W[bb];B[cc];W[dd];B[ad];W[bd])";
    public static final String simpleWithVariations="(;FF[4]C[root](;B[aa]C[a];C[b]W[bb](;C[c]B[cc])(;C[d]B[dd];C[e]W[ee]))(;C[f]B[ff](;C[g]W[gg];C[h]B[hh];C[i]W[ii])(;C[j]W[jj];)))";
    public static final String oneVariationAtMoveThree="(;FF[4]GM[1]SZ[19];B[aa];W[bb](;B[cc];W[dd];B[ad];W[bd])(;B[hh];W[hg]))";
    public static final String twoVariationsAtMoveThree="(;FF[4]GM[1]SZ[19];B[aa];W[bb](;B[cc]N[Var A];W[dd];B[ad];W[bd])(;B[hh]N[Var B];W[hg])(;B[gg]N[Var C];W[gh];B[hh];W[hg];B[kk]))";
    public static final String twoVariationsAtDifferentMoves="(;FF[4]GM[1]SZ[19];B[aa];W[bb](;B[cc];W[dd](;B[ad];W[bd])(;B[ee];W[ff]))(;B[hh];W[hg]))";
    public static final String variationOfAVariation="(;FF[4]GM[1]SZ[19];B[aa];W[bb](;B[cc]N[Var A];W[dd];B[ad];W[bd])(;B[hh]N[Var B];W[hg])(;B[gg]N[Var C];W[gh];B[hh](;W[hg]N[Var A];B[kk])(;W[kl]N[Var B])))";
    public static final String manyFacesTwoMovesAtA1AndR16="(;GM[1]FF[4]VW[]AP[Many Faces of Go:12.022]SZ[19]HA[0]ST[0]PB[ray]PW[ray]DT[2015-03-31]KM[7.5]RU[Chinese]BR[2 Dan]WR[2 Dan];B[as]BL[50]WL[60];W[qd]BL[1800]WL[1727])";
    public static final String manyFacesTwoMovesAtA1AndR16OnA9by9Board="(;GM[1]FF[4]VW[]AP[Many Faces of Go:12.022]SZ[9]HA[0]ST[0]PB[ray]PW[ray]DT[2015-04-12]KM[7.5]RU[Chinese]BR[2 Dan]WR[2 Dan];B[ai]BL[58]WL[60];W[gd]BL[29]WL[50])";
    public static final String newvariationsmf="""
                    (;
                    GM[1]FF[4]VW[]CA[UTF-8]AP[Many Faces of Go:12.024]
                    SZ[19]
                    HA[0]
                    ST[0]
                    PB[Opponent]
                    PW[Opponent]
                    DT[2022-03-28]
                    KM[6.5]
                    RU[Japanese]
                    BR[30 Kyu]
                    WR[30 Kyu]
                    ;B[qd]BL[895]WL[900]
                    (;W[od]BL[895]WL[897];B[oc]BL[891]WL[897];W[nc]BL[891]WL[896];B[pc]BL[890]WL[896]
                    (;W[nd]BL[890]WL[893];B[qf]BL[889]WL[893];W[jc]BL[889]WL[891])
                    (;W[md]BL[889]WL[891];B[pe]BL[886]WL[891];W[ic]BL[886]WL[888]))
                    (;W[oc]BL[880]WL[888];B[ld]BL[875]WL[888];W[of]BL[875]WL[886]
                    (;B[qg]BL[873]WL[886])
                    (;B[oe]BL[873]WL[883];W[ne]BL[873]WL[883];B[pe]BL[872]WL[883];W[nd]BL[872]WL[881]
                    ;B[nf]BL[870]WL[881];W[mf]BL[870]WL[880];B[ng]BL[869]WL[880];W[le]BL[869]WL[879];B[og]BL[867]WL[879]
                    ;W[kd]BL[867]WL[878])))
                    """;
    public static final String newvariationsmfflat="(;GM[1]FF[4]VW[]CA[UTF-8]AP[Many Faces of Go:12.024]SZ[19]HA[0]ST[0]PB[Opponent]PW[Opponent]DT[2022-03-28]KM[6.5]RU[Japanese]BR[30 Kyu]WR[30 Kyu];B[qd]BL[895]WL[900](;W[od]BL[895]WL[897];B[oc]BL[891]WL[897];W[nc]BL[891]WL[896];B[pc]BL[890]WL[896](;W[nd]BL[890]WL[893];B[qf]BL[889]WL[893];W[jc]BL[889]WL[891])(;W[md]BL[889]WL[891];B[pe]BL[886]WL[891];W[ic]BL[886]WL[888]))(;W[oc]BL[880]WL[888];B[ld]BL[875]WL[888];W[of]BL[875]WL[886](;B[qg]BL[873]WL[886])(;B[oe]BL[873]WL[883];W[ne]BL[873]WL[883];B[pe]BL[872]WL[883];W[nd]BL[872]WL[881];B[nf]BL[870]WL[881];W[mf]BL[870]WL[880];B[ng]BL[869]WL[880];W[le]BL[869]WL[879];B[og]BL[867]WL[879];W[kd]BL[867]WL[878])))";
    public static final String newvariationssmall="(;GM[1]FF[4](;B[qd]BL[897]WL[900];W[oc]BL[897]WL[886];B[ld]BL[878]WL[886])(;B[pd]BL[897]WL[891]))";
    public static final String smartgovariationsflat="(;GM[1]FF[4]SZ[19](;B[qd](;W[oc];B[ld])(;W[od];B[oc]))(;B[pd]))";
    public static final String twoGamesInOneFileFromSmartGo="""
                    (;GM[1]FF[4]SZ[19]AP[SmartGo:3.1.8]
                    PW[ray]
                    PB[SmartGo]
                    DT[2022-01-07]
                    KM[6.5]
                    RU[Simple]
                    TM[1800.0]OT[20 / 5]BL[1800.0]OM[20]OP[300.0];B[qd]V[0.0]BL[1799.6];W[oq]
                    ;B[dd]V[0.0]BL[1799.5];W[oc];B[dp]V[0.0]BL[1799.4])
                    (;GM[1]FF[4]SZ[19]AP[SmartGo:3.1.8]
                    PW[raz]
                    PB[SmartGo]
                    DT[2022-01-07]
                    KM[6.5]
                    RU[Simple]
                    TM[1800.0]OT[20 / 5]BL[1800.0]OM[20]OP[300.0];B[dq]V[0.0]BL[1799.6];W[oq]
                    ;B[dd]V[0.0]BL[1799.5];W[oc];B[dp]V[0.0]BL[1799.4])
                    """;
    public static final String twoverysmallgamesflat="(;B[as])\n(;B[at])"; // not flat anymore!
    public static final String twosmallgamesflat="(;FF[4];B[as])\n(;FF[4];B[at])"; // not flat anymore!
    public static final String smartgo4="(;GM[1])\n(;GM[2])\n(;GM[3])\n(;GM[4])";
    public static final String smartgo42="(;GM[1];B[as])\n(;GM[2];B[as])\n(;GM[3];B[as])\n(;GM[4];B[as])";
    public static final String smartgo43="(;GM[1];B[as];B[at])\n(;GM[2];B[as];B[at])\n(;GM[3];B[as];B[at])\n(;GM[4];B[as];B[at])";
    private final static Map<String,String> sgfData=new LinkedHashMap<>();
    private static void initializeMap() {
        sgfData.put("sgfExamleFromRedBean",sgfExamleFromRedBean);
        sgfData.put("startOfGame",startOfGame);
        sgfData.put("oneMoveAtA1NoHeader",oneMoveAtA1NoHeader);
        sgfData.put("oneMoveAtA1",oneMoveAtA1);
        sgfData.put("comments1",comments1);
        sgfData.put("simplevariations",simplevariations);
        sgfData.put("comments1",comments1);
        sgfData.put("consecutiveMoves",consecutiveMoves);
        sgfData.put("emptyWithSemicolon",emptyWithSemicolon);
        sgfData.put("twoEmptyWithSemicolon",twoEmptyWithSemicolon);
        sgfData.put("noVariation",noVariation);
        sgfData.put("simpleWithVariations",simpleWithVariations);
        sgfData.put("oneVariationAtMoveThree",oneVariationAtMoveThree);
        sgfData.put("twoVariationsAtMoveThree",twoVariationsAtMoveThree);
        sgfData.put("twoVariationsAtDifferentMoves",twoVariationsAtDifferentMoves);
        sgfData.put("variationOfAVariation",variationOfAVariation);
        sgfData.put("manyFacesTwoMovesAtA1AndR16",manyFacesTwoMovesAtA1AndR16);
        sgfData.put("manyFacesTwoMovesAtA1AndR16OnA9by9Board",manyFacesTwoMovesAtA1AndR16OnA9by9Board);
        sgfData.put("newvariationsmf",newvariationsmf);
        sgfData.put("newvariationsmfflat",newvariationsmfflat);
        sgfData.put("newvariationssmall",newvariationssmall);
        sgfData.put("smartgovariationsflat",smartgovariationsflat);
        sgfData.put("twoGamesInOneFileFromSmartGo",twoGamesInOneFileFromSmartGo);
        sgfData.put("twoverysmallgamesflat",twoverysmallgamesflat);
        sgfData.put("twosmallgamesflat",twosmallgamesflat);
        sgfData.put("smartgo4",smartgo4);
        sgfData.put("smartgo42",smartgo42);
        sgfData.put("smartgo43",smartgo43);
        //System.out.println(sgfData.keySet());
        //System.out.println(sgfData.size()+" sgf strings in parser map.");
    }
    static {
        //System.out.println("static parser init");
        //IO.stackTrace(20);
    }
    static final AtomicBoolean isInitialized=new AtomicBoolean();
    static { // this kind of thing needs to be done once!
        // running multiple instances is causing it to be run more than once.
        //synchronized(isInitialized) {
        synchronized(Parser.class) {
            boolean ok=isInitialized.compareAndSet(false,true);
            if(ok) initializeMap();
        }
    }
    private static final Set<File> badSgfFiles=new LinkedHashSet<>();
    static {
        badSgfFiles.add(new File("sgf/old/annotated/test1.sgf")); // missing endGameCharacter)
        badSgfFiles.add(new File("strangesgf/() not same count/missing ) at end.sgf"));
        badSgfFiles.add(new File("strangesgf/() not same count/mail header, missing ) at end.sgf"));
        badSgfFiles.add(new File("strangesgf/; missing/5er, some var., ';' after '(' missing.sgf"));
        badSgfFiles.add(new File("strangesgf/; missing/tabs, empty branch without ; .sgf"));
        badSgfFiles.add(
                new File("strangesgf/Comments/escaped and not escaped/10744 esc.Zeichen nicht esc - gefangen.sgf"));
        badSgfFiles.add(new File("strangesgf/Comments/escaped and not escaped/last ] escaped.sgf"));
        File exception=new File("strangesgf/exception (too many chars in comment and other IDs)");
        badSgfFiles.add(new File(exception,"damaged file, (too much example code), missing ]))).sgf"));
        badSgfFiles.add(
                new File(exception,"repaired damaged file 1,  comment = 63193 x b-slash (too much example code).sgf"));
        badSgfFiles.add(new File(
                "strangesgf/FileSize, too long Strings/11478 Code chopped apart - missing ]))), many b-slashes in unfinished C.sgf"));
        badSgfFiles.add(new File("strangesgf/hand work errors/2701 (;[yy;0].sgf"));
        badSgfFiles.add(new File("strangesgf/instant Ko take back/Ko direct recapture.sgf"));
        badSgfFiles.add(new File("strangesgf/legal/035 tabs instead of spaces.sgf"));
        badSgfFiles.add(new File("strangesgf/multi (collection files)/defect easy(SPLIT) cutTo4Problems.sgf"));
        badSgfFiles.add(new File("strangesgf/multi (collection files)/easy(SPLIT) cutTo4Problems.sgf"));
        badSgfFiles.add(new File("strangesgf/multi (collection files)/multiEasy.sgf"));
        badSgfFiles.add(new File("strangesgf/variations/5er, some vars, defect 1.sgf"));
    }
}
