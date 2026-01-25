package controller;
import java.util.Arrays;
import io.Logging;
public class Message {
    Message(String string) {
        String stripped=GtpParsing.strip(string);
        if(stripped!=null&&!stripped.isEmpty()) {
            GtpParsing.ParsedTokens parsed=GtpParsing.parseTokens(stripped);
            id=parsed.id;
            String[] tokens=parsed.tokens;
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
                arguments[1]=GtpParsing.strip(arguments[1]);
                // now we have problems with a bunch of line feeds
                Logging.mainLogger.info(String.valueOf(this.toString()));
            }
        }
    }
    @Override public String toString() { return id+" "+command+" "+Arrays.asList(arguments); }
    public static void main(String[] args) {}
    Integer id=-1;
    Command command; // maybe this class belong in command?
    String[] arguments;
    public static final String defaultQuit=Command.quit.name();
}
