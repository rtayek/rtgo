package model;
import static org.junit.Assert.assertNotNull;
import java.io.InputStream;
import org.junit.Test;
import audio.Audio;
import utilities.TestSupport;
public class GetAudioResourceAsStreamTestCase extends TestSupport {
    @Test public void testFilename() { inputStream=clazz.getResourceAsStream(filename); assertNotNull(inputStream); }
    @Test public void testAbsolutePath() {
        inputStream=clazz.getResourceAsStream("/audio/"+filename);
        assertNotNull(inputStream);
    }
    String filename="stone.wav";
    Class<Audio> clazz=audio.Audio.class;
    InputStream inputStream;
}
