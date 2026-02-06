package iox;
import io.Logging;
import com.tayek.util.core.Et;
public enum Init3 {
    first;
    Init3() { //
        Logging.mainLogger.info(String.valueOf(et));
        if(!once) {
            try {
                // initializ hear
                once=true;
            } catch(Exception e) {
                Logging.mainLogger.info(String.valueOf(e));
                System.exit(1);
            }
        }
    }
    public boolean once=false;
    public final Et et=new Et();
}
