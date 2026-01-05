package audio;
import java.io.*;
import javax.sound.sampled.*;
import io.Logging;
import model.Model.Where;
import server.NamedThreadGroup;
public class Audio implements Runnable {
    public enum Sound { challenge, stone, atari, capture, pass, illegal; }
    private Audio(String filename) { this.filename=filename; }
    static InputStream getResourceAsStream(String filename) { return Audio.class.getResourceAsStream(filename); }
    static AudioInputStream getAudioInputSream(String filename) throws UnsupportedAudioFileException,IOException {
        AudioInputStream inputStream=AudioSystem
                .getAudioInputStream(new BufferedInputStream(getResourceAsStream(filename)));
        return inputStream;
    }
    @Override public void run() {
        started=true;
        completed=false;
        try {
            Clip clip=AudioSystem.getClip();
            AudioInputStream inputStream=getAudioInputSream(filename);
            if(inputStream!=null) {
                clip.open(inputStream);
                FloatControl gainControl=(FloatControl)clip.getControl(FloatControl.Type.MASTER_GAIN);
                gainControl.setValue(+6.0f); // ?
                clip.start();
                while(clip.getMicrosecondLength()!=clip.getMicrosecondPosition()) Thread.sleep(10);
                Logging.mainLogger.info("audio"+" "+" null input stream!");
            }
            completed=true;
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
