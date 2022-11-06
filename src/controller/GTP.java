package controller;
import io.IO.Stopable;
 interface GTPx extends Stopable {
    boolean messageSansLinefeed(Character character,Integer id,String string);
    boolean message(Character character,int id,String string);
    boolean isWaitingForMove();
    boolean processCommand(String string);
    void run();
}