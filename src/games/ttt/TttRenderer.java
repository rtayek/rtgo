package games.ttt;

/**
 * Simple text renderer for tic-tac-toe state.
 */
public final class TttRenderer {
    private TttRenderer() {}

    public static String render(TttState state) {
        StringBuilder sb=new StringBuilder();
        sb.append("TicTacToe ").append(state.spec().width()).append("x").append(state.spec().height())
          .append(" win=").append(state.spec().winLength()).append("\n");
        sb.append("toPlay=").append(state.toPlay()).append(" outcome=").append(state.outcome()).append("\n\n");

        for(int y=0;y<state.spec().height();y++) {
            for(int x=0;x<state.spec().width();x++) {
                sb.append(cellChar(state.at(x,y)));
                if(x+1<state.spec().width()) sb.append(' ');
            }
            sb.append('\n');
        }
        return sb.toString();
    }

    private static char cellChar(TttMark mark) {
        return switch(mark) {
            case empty -> '.';
            case x -> 'X';
            case o -> 'O';
        };
    }
}
