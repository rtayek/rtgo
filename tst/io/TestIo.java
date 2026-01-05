package io;
import static org.junit.Assert.assertTrue;
import java.io.StringWriter;
import java.io.Writer;
public final class TestIo {
    @FunctionalInterface public interface WriterConsumer {
        void write(Writer writer);
    }
    @FunctionalInterface public interface WriterBoolean {
        boolean write(Writer writer);
    }
    private TestIo() {}
    public static String writeToString(WriterConsumer consumer) {
        StringWriter writer=new StringWriter();
        consumer.write(writer);
        return writer.toString();
    }
    public static String saveToString(String message,WriterBoolean consumer) {
        StringWriter writer=new StringWriter();
        boolean ok=consumer.write(writer);
        assertTrue(message,ok);
        return writer.toString();
    }
}
