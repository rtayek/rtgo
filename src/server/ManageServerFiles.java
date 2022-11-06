package server;
import java.io.File;
import io.Logging;
public class ManageServerFiles {
    public static void deleteServerSGFFiles() {
        for(String filename:serverGames.list()) {
            Logging.mainLogger.info("deleting: "+filename);
            boolean ok=new File(serverGames,filename).delete();
            if(!ok) Logging.mainLogger.severe(filename+" was not deleted!");
        }
    }
    public static void main(String[] args) { deleteServerSGFFiles(); }
    public static final File serverGames=new File("serverGames");
}
