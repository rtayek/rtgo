package controller;
import io.IOs.Stopable;
interface GTPx extends Stopable {
	// does not appear to be used at present.
    boolean messageSansLinefeed(Character character,Integer id,String string);
    boolean message(Character character,int id,String string);
    boolean isWaitingForMove();
    boolean processCommand(String string);
    void run();
}
