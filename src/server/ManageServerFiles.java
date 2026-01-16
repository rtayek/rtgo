package server;
import java.io.File;
import io.Logging;
public class ManageServerFiles {
	public static void deleteServerSGFFiles() {
		if(serverGames.exists()) {
			for(String filename:serverGames.list()) {
				Logging.mainLogger.info("deleting server game: "+filename);
				boolean ok=new File(serverGames,filename).delete();
				if(!ok) Logging.mainLogger.severe(filename+" was not deleted!");
			}
		} else System.out.println("serverGames dir does not exist");
	}
	public static void main(String[] args) {
		deleteServerSGFFiles();
	}
	public static final File serverGames=new File("serverGames");
}
