import io.Logging;
public class Empty {
    public static void main(String[] args) {
        Logging.deleteLogfiles();
        System.out.println("Hello \u001b[1;31mred\u001b[0m world!");
    }
}
