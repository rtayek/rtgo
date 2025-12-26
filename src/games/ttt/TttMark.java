package games.ttt;
public enum TttMark { empty, x, o; public TttMark other() { return this==x?o:x; } }
