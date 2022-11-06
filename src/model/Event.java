package model;
public enum Event {
    newTree,nodeChanged,illegalMove,exception;
    public static class Hint {
        Hint(Event event,String string) { this.event=event; this.string=string; }
        @Override public String toString() { return event+":"+(string!=null?string:""); }
        public final Event event;
        public final String string;
    }
}
