package audio;
import java.io.InputStream;
import com.tayek.util.audio.AudioClips;
import io.Logging;
import model.Model.Where;
import server.NamedThreadGroup;
public class Audio implements Runnable {
    public enum Sound { challenge, stone, atari, capture, pass, illegal; }
    private Audio(String filename) { this.filename=filename; }
    static InputStream getResourceAsStream(String filename) { return Audio.class.getResourceAsStream(filename); }
    @Override public void run() {
        started=true;
        completed=false;
        try {
            completed=AudioClips.playWav(Audio.class,filename,+6.0f,true);
            if(!completed) Logging.mainLogger.info("audio"+" "+" null input stream!");
        } catch(Exception e) {
            Logging.mainLogger.severe(this+" caught: "+e);
            completed=false;
        }
    }
    private static Audio play(String filename) {
        Logging.mainLogger.info("play "+filename);
        Audio audio=new Audio(filename);
        (audio.thread=NamedThreadGroup.createNamedThread(NamedThreadGroup.groupZero,audio,"audio")).start();
        return audio;
    }
    public static Audio play(Sound sound) {
        switch(sound) {
            case challenge:
                return play("gochlng.wav");
            case stone:
                return play("stone.wav");
            case atari:
                return play("goatari.wav");
            case capture:
                return play("gocaptb.wav");
            case pass:
                return play("gopass.wav");
            case illegal:
                return play("goillmv.wav");
            default:
                Logging.mainLogger.warning(""+" "+"default where!");
                return null;
        }
    }
    public static Audio play(Where where) {
        // don't need this unless we have different sounds for different moves that are not legal.
        switch(where) {
            case onBoard:
            case onVacant:
                return null;
            case hole:
            case occupied:
            case notCloseEnough:
            case notInBand:
            case offBoard:
                return play(Sound.illegal);
            default:
                Logging.mainLogger.warning(""+" "+"default where!");
                return null;
        }
    }
    public static void main(String[] args) throws InterruptedException {
        Logging.mainLogger.info("sounds");
        for(Sound sound:Sound.values()) {
            Logging.mainLogger.info(String.valueOf(sound));
            Audio audio=play(sound);
            if(audio!=null) audio.thread.join();
            else Logging.mainLogger.info("no sound for: "+sound);
        }
        Logging.mainLogger.info("wheres");
        for(Where where:Where.values()) {
            Logging.mainLogger.info(String.valueOf(where));
            Audio audio=play(where);
            if(audio!=null) audio.thread.join();
            else Logging.mainLogger.info("no sound for: "+where);
        }
    }
    final String filename;
    transient boolean started,completed;
    Thread thread;
}
