package sgf;
public enum P2 { // looks like the only thing i need this for is so i can switch
    // on it!
    // yes, p can not be an enum because it has subclassed.
    // if we replace the subclasses with type idtentifier
    // maybe we could get rid of this.
    // maybe not needed in current version of java.
    AB,AE,AN,AP,AR,AS,AW,B,BL,BM,BR,BS,BT,C,CA,CH,CP,CR,DD,DM,DO,DT,EL,EV,EX,FF,FG,GB,GC,GM,GN,GW,HA,HO,ID,IP,IT,IY,KM,KO,L,LB,LN,LT,LZ,M,MA,MN,N,OB,OM,ON,OP,OT,OV,OW,PB,PC,PL,PM,PW,RE,RG,RO,RR,RT,RU,SC,SE,SE2,SI,SL,SO,SQ,ST,SU,SZ,TB,TC,TE,TM,TR,TW,UC,US,V,VW,W,WL,WR,WS,WT,ZB,ZW;
    public enum Types { setup, info, root, move, none; }
    // put these above into P
    // then get rid of p2
    P2() {
        P p0=P.idToP.get(name());
        p=p0;
        type=type(p0);
        if(p0==null) System.out.println("can not find p for "+name());
    }
    public static P2 which(String id) { return valueOf(id); }
    static Types type(P p) {
        if(p instanceof Setup) return Types.setup;
        else if(p instanceof GameInfo) return Types.info;
        else if(p instanceof Root) return Types.root;
        else if(p instanceof Move) return Types.move;
        else if(p instanceof NoType) return Types.none;
        else throw new RuntimeException("unknown type for "+p);
    }
    public static void main(String[] arguments) {
        // time left BL and WL() and tesugi (TE) are move types!
        // i added these custom types.
        System.out.println(P2.valueOf("RT"));
        System.out.println(P2.valueOf("ZB"));
        System.out.println(P2.valueOf("ZW"));
    }
    final P p;
    final Types type;
}
