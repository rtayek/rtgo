package sgf;
import static io.Constants.lineSeparator;
import java.util.logging.*;
public class ASimpleFormatter extends SimpleFormatter {
    @Override public synchronized String format(LogRecord record) {
        String s=super.format(record);
        int index=s.indexOf(lineSeparator);
        if(index!=-1) s=s.substring(0,index)+'\t'+s.substring(index+lineSeparator.length());
        return s;
    }
}
