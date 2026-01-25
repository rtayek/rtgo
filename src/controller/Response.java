package controller;
import static controller.GTPBackEnd.*;
import io.Logging;
public class Response { // message has the same member variables!
    private Response(String string) {
        String s="";
        if(string==null) {
            id=-1;
            okOrBad=badCharacter;
        } else if(string.length()==0) {
            id=-1;
            okOrBad=badCharacter;
        } else if(string.length()>=2) {
            okOrBad=string.charAt(0);
            string=string.substring(1);
            String[] tokens=GtpParsing.splitTokens(string);
            // this is going to break when sending sgf back!
            // it probably needs to be encoded in hex ascii.
            this.id=GtpParsing.parseId(tokens);
            s=GtpParsing.joinTokensFrom(tokens,1);
            // this breaks a lot
            //if(checkForTwoLineFeeds(s)) s=s.substring(0,s.length()-2);
        } else {
            okOrBad=badCharacter;
            id=-1;
            Logging.mainLogger.severe(string+" is too small!");
        }
        this.response=s;
    }
    public boolean isOk() { return okOrBad.equals(okCharacter); }
    public boolean isBad() { return okOrBad.equals(badCharacter); }
    @Override public String toString() {
        String string=""+okOrBad+" ";
        if(id!=-1) string+=id;
        string+=response;
        return string;
    }
    public static String removeTrailing(String string) {
        // use this to reduce size of printouts.
        // check for duplicate code.
        if(string!=null)
            while(string.endsWith("\n")||string.endsWith("\r")) string=string.substring(0,string.length()-1);
        return string;
    }
    public static boolean checkForTwoLineFeeds(String string) {
        if(string.endsWith(twoLineFeeds)) {
            Logging.mainLogger.fine(string);
            return true;
        } else Logging.mainLogger.warning("response does not end with two line feeds!");
        return false;
    }
    public static Response response(String string) {
        boolean ok=checkForTwoLineFeeds(string);
        // get rid of the two linefieeds
        if(string.endsWith("\n")) string=string.substring(0,string.length()-1);
        if(string.endsWith("\n")) string=string.substring(0,string.length()-1);
        return new Response(string);
    }
    // maybe get rid of the above and just use the below?
    public static Response[] responses(String string) {
        // looks like it can return multiple lines
        boolean ok=checkForTwoLineFeeds(string);
        String[] strings=string.split(twoLineFeeds);
        Response[] responses=new Response[strings.length];
        for(int i=0;i<responses.length;i++) responses[i]=new Response(strings[i]);
        return responses;
    }
    public final Integer id;
    private final Character okOrBad;
    public /*final*/ String response;
}
