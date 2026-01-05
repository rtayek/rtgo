package model;
import static org.junit.Assert.assertNotNull;
import java.io.InputStream;
import org.junit.Rule;
import org.junit.Test;
import audio.Audio;
import utilities.MyTestWatcher;
public class GetAudioResourceAsStreamTestCase {
    @Rule public MyTestWatcher watcher=new MyTestWatcher(getClass());
    @Test public void testFilename() { inputStream=clazz.getResourceAsStream(filename); assertNotNull(inputStream); }
    @Test public void testAbsolutePath() {
        inputStream=clazz.getResourceAsStream("/audio/"+filename);
        assertNotNull(inputStream);
    }
    String filename="stone.wav";
    Class<Audio> clazz=audio.Audio.class;
    InputStream inputStream;
}
