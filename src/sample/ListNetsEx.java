package sample;
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
            System.out.println(localHost);
        } catch(java.net.UnknownHostException ex) {
            ex.printStackTrace();
            throw new ParseException(ex.getMessage(),0);
        }
    }
    static void displayInterfaceInformation(NetworkInterface netint) throws SocketException {
        System.out.println("Display name: "+netint.getDisplayName());
        System.out.println("Hardware address: "+Arrays.toString(netint.getHardwareAddress()));
    }
}