package io;
import io.Logging;
public class AnsiColor {
    // this does not work very well
    //https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println/5762502#5762502
    public static void main(String[] args) {
        Logging.mainLogger.info("\\033[XXm");
        //print "\033[%dm%d\t\t\033[%dm%d" % (i,i,i+60,i+60);
        for(int i=20;i<37+1;++i) { Logging.mainLogger.info("\033["+i+"m"+i+"\t\t\033["+(i+60)+"m"+(i+60)); }
        Logging.mainLogger.info("\033[39m\\033[49m - Reset colour");
        Logging.mainLogger.info("\\033[2K - Clear Line");
        Logging.mainLogger.info("\\033[<L>;<C>H OR \\033[<L>;<C>f puts the cursor at line L and column C.");
        Logging.mainLogger.info("\\033[<N>A Move the cursor up N lines");
        Logging.mainLogger.info("\\033[<N>B Move the cursor down N lines");
        Logging.mainLogger.info("\\033[<N>C Move the cursor forward N columns");
        Logging.mainLogger.info("\\033[<N>D Move the cursor backward N columns");
        Logging.mainLogger.info("\\033[2J Clear the screen, move to (0,0)");
        Logging.mainLogger.info("\\033[K Erase to end of line");
        Logging.mainLogger.info("\\033[s Save cursor position");
        Logging.mainLogger.info("\\033[u Restore cursor position");
        Logging.mainLogger.info(" ");
        Logging.mainLogger.info("\\033[4m  Underline on");
        Logging.mainLogger.info("\\033[24m Underline off");
        Logging.mainLogger.info("\\033[1m  Bold on");
        Logging.mainLogger.info("\\033[21m Bold off");
    }
}
