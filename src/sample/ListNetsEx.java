package sample;
import io.Logging;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Collections;
import java.util.Enumeration;
public class ListNetsEx {
    public static void main(String args[]) throws SocketException,ParseException {
        Enumeration<NetworkInterface> nets=NetworkInterface.getNetworkInterfaces();
        for(NetworkInterface netint:Collections.list(nets)) displayInterfaceInformation(netint);
        String localHost=null;
        try {
            localHost=InetAddress.getLocalHost().getHostAddress();
            Logging.mainLogger.info(String.valueOf(localHost));
        } catch(java.net.UnknownHostException ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage(),0);
        }
    }
    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        Logging.mainLogger.info("Display name: "+netint.getDisplayName());
        Logging.mainLogger.info("Hardware address: "+Arrays.toString(netint.getHardwareAddress()));
    }
}