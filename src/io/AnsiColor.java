package io;
public class AnsiColor {
    // this does not work very well
    //https://stackoverflow.com/questions/5762491/how-to-print-color-in-console-using-system-out-println/5762502#5762502
    public static void main(String[] args) {
        System.out.println("\\033[XXm");
        //print "\033[%dm%d\t\t\033[%dm%d" % (i,i,i+60,i+60);
        for(int i=20;i<37+1;++i) { System.out.println("\033["+i+"m"+i+"\t\t\033["+(i+60)+"m"+(i+60)); }
        System.out.println("\033[39m\\033[49m - Reset colour");
        System.out.println("\\033[2K - Clear Line");
        System.out.println("\\033[<L>;<C>H OR \\033[<L>;<C>f puts the cursor at line L and column C.");
        System.out.println("\\033[<N>A Move the cursor up N lines");
        System.out.println("\\033[<N>B Move the cursor down N lines");
        System.out.println("\\033[<N>C Move the cursor forward N columns");
        System.out.println("\\033[<N>D Move the cursor backward N columns");
        System.out.println("\\033[2J Clear the screen, move to (0,0)");
        System.out.println("\\033[K Erase to end of line");
        System.out.println("\\033[s Save cursor position");
        System.out.println("\\033[u Restore cursor position");
        System.out.println(" ");
        System.out.println("\\033[4m  Underline on");
        System.out.println("\\033[24m Underline off");
        System.out.println("\\033[1m  Bold on");
        System.out.println("\\033[21m Bold off");
    }
}
