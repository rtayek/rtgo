package controller;
import static controller.GTPBackEnd.*;
import java.util.Arrays;
import io.Logging;
public class Message {
    Message(String string) {
        String stripped=strip(string);
        if(stripped!=null&&!stripped.isEmpty()) {
            String[] tokens=stripped.split(" ");
            try {
                id=Integer.parseInt(tokens[0]);
            } catch(NumberFormatException e) {} // usually throws
            String[] old=tokens;
            if(id!=-1) {
                tokens=new String[old.length-1];
                System.arraycopy(old,1,tokens,0,tokens.length);
            } else {
                tokens=new String[old.length];
                System.arraycopy(old,0,tokens,0,tokens.length);
            }
            if(tokens.length>=1) {
                command=Command.from(tokens[0]);
            } else Logging.mainLogger.warning(""+" "+"gtp: command seems to be a number: "+string);
            // model.mumble(command);
            // use arguments field to check number of arguments?
            arguments=tokens;
            int hack=stripped.indexOf(Command.tgo_receive_sgf.name());
            // i don't think we need hack any more.
            if(tokens[0].equals(Command.tgo_receive_sgf.name())) { // spaces in the causes many tokens.
                arguments=new String[2];
                arguments[0]=tokens[0];
                arguments[1]=stripped.substring(hack+Command.tgo_receive_sgf.name().length());
                arguments[1]=strip(arguments[1]);
                // now we gave problems with a bunch of linefeeds
                Logging.mainLogger.info(this.toString());
            }
        }
    }
    @Override public String toString() { return id+" "+command+" "+Arrays.asList(arguments); }
    static String strip(String string) {
        String stripped="";
        String trimmed=string.trim();
        for(int i=0;i<trimmed.length();i++) { // remove most control characters
            Character c=trimmed.charAt(i);
            if(Character.isISOControl(c)) { // check the spec on this
                if(c.equals(lineFeed)||c.equals(tab)) stripped+=c;
            } else stripped+=c;
        }
        trimmed=stripped;
        stripped="";
        for(int i=0;i<trimmed.length();i++) { // remove comment
            Character c=trimmed.charAt(i);
            if(c.equals('#')) break;
            else stripped+=c;
        }
        trimmed=stripped.trim();
        stripped="";
        for(int i=0;i<trimmed.length();i++) { // change tab to space
            Character c=trimmed.charAt(i);
            if(c.equals('\t')) stripped+=' ';
            else stripped+=c;
        }
        return stripped.trim();
    }
    public static void main(String[] args) {}
    Integer id=-1;
    Command command; // maybe this class belong in command?
    String[] arguments;
    public static final String defaultQuit=Command.quit.name();
}
