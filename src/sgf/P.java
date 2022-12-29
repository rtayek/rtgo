package sgf;
import java.util.*;
abstract class P1 extends P {
    P1(String id,String ff,String description,String type,String value) { super(id,ff,description,type,value); }
    @Override public String toString() {
        return id; //?
    }
    void value(String value) { this.value=value; }
}
class Move extends P1 {
    Move(String id,String ff,String description,String type,String value) { super(id,ff,description,type,value); }
    //int x,y;
}
class Setup extends P1 {
    Setup(String id,String ff,String description,String type,String value) { super(id,ff,description,type,value); }
}
class Root extends P1 {
    Root(String id,String ff,String description,String type,String value) { super(id,ff,description,type,value); }
}
class GameInfo extends P1 {
    GameInfo(String id,String ff,String description,String type,String value) { super(id,ff,description,type,value); }
}
class NoType extends P1 {
    NoType(String id,String ff,String description,String type,String value) { super(id,ff,description,type,value); }
}
public abstract class P {
    P(String id,String ff,String description,String type,String value) {
        this.id=id;
        this.ff=ff;
        this.description=description;
        this.type=type;
        this.value=value;
    }
    public final String id,ff,description,type;
    String value;
    public static final P1 AB=new Setup("AB","1234","Add Black","setup","list of stone");
    public static final P1 AE=new Setup("AE","1234","Add Empty","setup","list of point");
    public static final P1 AN=new GameInfo("AN","--34","Annotation","game-info","simpletext");
    public static final P1 AP=new Root("AP","---4","Application","root","composed simpletext : simpletext");
    public static final P1 AR=new NoType("AR","---4","Arrow","-","list of composed point : point");
    public static final P1 AS=new NoType("AS","---4","Who adds stones","- (LOA)","simpletext");
    public static final P1 AW=new Setup("AW","1234","Add White","setup","list of stone");
    public static final P1 B=new Move("B","1234","Black","move","move");
    public static final P1 BL=new Move("BL","1234","Black time left","move","real");
    public static final P1 BM=new Move("BM","1234","Bad move","move","double");
    public static final P1 BR=new GameInfo("BR","1234","Black rank","game-info","simpletext");
    public static final P1 BS=new GameInfo("BS","123-","Black species","game-info","number");
    public static final P1 BT=new GameInfo("BT","--34","Black team","game-info","simpletext");
    public static final P1 C=new NoType("C","1234","Comment","-","text");
    public static final P1 CA=new Root("CA","---4","Charset","root","simpletext");
    public static final P1 CH=new NoType("CH","123-","Check mark","-","double");
    public static final P1 CP=new GameInfo("CP","--34","Copyright","game-info","simpletext");
    public static final P1 CR=new NoType("CR","--34","Circle","-","list of point");
    public static final P1 DD=new NoType("DD","---4","Dim points","- (inherit)","elist of point");
    public static final P1 DM=new NoType("DM","--34","Even position","-","double");
    public static final P1 DO=new Move("DO","--34","Doubtful","move","none");
    public static final P1 DT=new GameInfo("DT","1234","Date","game-info","simpletext");
    public static final P1 EL=new NoType("EL","12--","Eval. comp move","-","number");
    public static final P1 EV=new GameInfo("EV","1234","Event","game-info","simpletext");
    public static final P1 EX=new NoType("EX","12--","Expected move","-","move");
    public static final P1 FF=new Root("FF","-234","Fileformat","root","number (range: 1-4)");
    public static final P1 FG=new NoType("FG","1234","Figure","-","none | composed number : simpletext");
    public static final P1 GB=new NoType("GB","1234","Good for Black","-","double");
    public static final P1 GC=new GameInfo("GC","1234","Game comment","game-info","text");
    public static final P1 GM=new Root("GM","1234","Game","root","number (range: 1-5,7-16)");
    public static final P1 GN=new GameInfo("GN","1234","Game name","game-info","simpletext");
    public static final P1 GW=new NoType("GW","1234","Good for White","-","double");
    public static final P1 HA=new GameInfo("HA","1234","Handicap","game-info (Go)","number");
    public static final P1 HO=new NoType("HO","--34","Hotspot","-","double");
    public static final P1 ID=new GameInfo("ID","--3-","Game identifier","game-info","simpletext");
    public static final P1 IP=new GameInfo("IP","---4","Initial pos.","game-info (LOA)","simpletext");
    public static final P1 IT=new Move("IT","--34","Interesting","move","none");
    public static final P1 IY=new GameInfo("IY","---4","Invert Y-axis","game-info (LOA)","simpletext");
    public static final P1 KM=new GameInfo("KM","1234","Komi","game-info (Go)","real");
    public static final P1 KO=new Move("KO","--34","Ko","move","none");
    public static final P1 L=new NoType("L","12--","Letters","-","list of point");
    public static final P1 LB=new NoType("LB","--34","Label","-","list of composed point : simpletext");
    public static final P1 LN=new NoType("LN","--34","Line","-","list of composed point : point");
    public static final P1 LT=new NoType("LT","--3-","Lose on time","-","none");
    public static final P1 LZ=new GameInfo("LZ","---4","Leela Zero","game-info (Go)","simpletext");
    public static final P1 M=new NoType("M","12--","Simple markup","-","list of point");
    public static final P1 MA=new NoType("MA","--34","Mark with X","-","list of point");
    public static final P1 MN=new Move("MN","--34","Set move number","move","number");
    public static final P1 N=new NoType("N","1234","Nodename","-","simpletext");
    public static final P1 OB=new Move("OB","--34","OtStones Black","move","number");
    public static final P1 OM=new NoType("OM","--3-","Moves/overtime","-","number");
    public static final P1 ON=new GameInfo("ON","--34","Opening","game-info","simpletext");
    public static final P1 OP=new NoType("OP","--3-","Overtime length","-","real");
    public static final P1 OT=new GameInfo("OT","---4","Overtime","game-info","simpletext");
    public static final P1 OV=new NoType("OV","--3-","Operator overhead","-","real");
    public static final P1 OW=new Move("OW","--34","OtStones White","move","number");
    public static final P1 PB=new GameInfo("PB","1234","Player Black","game-info","simpletext");
    public static final P1 PC=new GameInfo("PC","1234","Place","game-info","simpletext");
    public static final P1 PL=new Setup("PL","1234","Player to play","setup","color");
    public static final P1 PM=new NoType("PM","---4","Print move mode","- (inherit)","number");
    public static final P1 PW=new GameInfo("PW","1234","Player White","game-info","simpletext");
    public static final P1 RE=new GameInfo("RE","1234","Result","game-info","simpletext");
    public static final P1 RG=new NoType("RG","123-","Region","- (Go)","list of point");
    public static final P1 RO=new GameInfo("RO","1234","Round","game-info","simpletext");
    public static final P1 RR=new GameInfo("RR","1234","Round","game-info","simpletext"); // what is this?
    public static final P1 RT=new Root("RT","1234","Tgo Root","Root","simpletext"); // my MNode root?
    public static final P1 RU=new GameInfo("RU","--34","Rules","game-info","simpletext");
    public static final P1 SC=new NoType("SC","123-","Secure stones","-","list of point");
    public static final P1 SE=new NoType("SE","--3-","Selftest moves","-","list of point");
    public static final P1 SE2=new NoType("SE2","---4","Markup","- (LOA)","point");
    public static final P1 SI=new NoType("SI","--3-","Sigma","-","double");
    public static final P1 SL=new NoType("SL","1234","Selected","-","list of point");
    public static final P1 SO=new GameInfo("SO","1234","Source","game-info","simpletext");
    public static final P1 SQ=new NoType("SQ","---4","Square","-","list of point");
    public static final P1 ST=new Root("ST","---4","Style","root","number (range: 0-3)");
    public static final P1 SU=new GameInfo("SU","---4","Setup type","game-info (LOA)","simpletext");
    public static final P1 SZ=new Root("SZ","1234","Size","root","(number | composed number : number)");
    public static final P1 TB=new NoType("TB","1234","Territory Black","- (Go)","elist of point");
    public static final P1 TC=new NoType("TC","--3-","Territory count","- (Go)","number");
    public static final P1 TE=new Move("TE","1234","Tesuji","move","double");
    public static final P1 TM=new GameInfo("TM","1234","Timelimit","game-info","real");
    public static final P1 TR=new NoType("TR","--34","Triangle","-","list of point");
    public static final P1 TW=new NoType("TW","1234","Territory White","- (Go)","elist of point");
    public static final P1 UC=new NoType("UC","--34","Unclear pos","-","double");
    public static final P1 US=new GameInfo("US","1234","User","game-info","simpletext");
    public static final P1 V=new NoType("V","1234","Value (score)","-","real");
    public static final P1 VW=new NoType("VW","1234","View","- (inherit)","elist of point");
    public static final P1 W=new Move("W","1234","White","move","move");
    public static final P1 WL=new Move("WL","1234","White time left","move","real");
    public static final P1 WR=new GameInfo("WR","1234","White rank","game-info","simpletext");
    public static final P1 WS=new GameInfo("WS","123-","White species","game-info","number");
    public static final P1 WT=new GameInfo("WT","--34","White team","game-info","simpletext");
    // custom property for resign
    public static final P1 ZB=new Move("ZB","1234","black resign","move","none");
    public static final P1 ZW=new Move("ZW","1234","White resign","move","none");
    private static final P[] ps= {AB,AE,AN,AP,AR,AS,AW,B,BL,BM,BR,BS,BT,C,CA,CH,CP,CR,DD,DM,DO,DT,EL,EV,EX,FF,FG,GB,GC,
            GM,GN,GW,HA,HO,ID,IP,IT,IY,KM,KO,L,LB,LN,LT,LZ,M,MA,MN,N,OB,OM,ON,OP,OT,OV,OW,PB,PC,PL,PM,PW,RE,RG,RO,RR,RT,
            RU,SC,SE,SE2,SI,SL,SO,SQ,ST,SU,SZ,TB,TC,TE,TM,TR,TW,UC,US,V,VW,W,WL,WR,WS,WT,ZB,ZW};
    public static SortedMap<String,P> idToP=new TreeMap<>();
    static {
        for(P p:ps) idToP.put(p.id,p);
    }
}
