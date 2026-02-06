package equipment;
public enum Stone {
    vacant,black,white,edge /* edge means off the board */;
    static char[] x=new char[] {' ','x','o','@'};
    public Character toCharacter() { return x[ordinal()]; }
    public Stone otherColor() {
        switch(this) {
            case black:
                return white;
            case white:
                return black;
            default:
                throw new RuntimeException("bad stone enum");
        }
    }
}
