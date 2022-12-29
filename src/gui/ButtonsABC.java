package gui;
import java.util.*;
import javax.swing.*;
public abstract class ButtonsABC {
    // may not want to be abstract
    // make sure buttons are stay in order
    // this may be usefull to keeo track of buttons
    // but perhasps not a collection of options (or something like that).
    public class ButtonWithEnum<T extends Enum<T>> /*extends AbstractButton */ {
        // maybe not extending abstract button may help with spinners?
        // these do not have any associated value.
        // the only thin associated with this is an enum (T t).
        // so option is not suitable for a base class.
        public ButtonWithEnum(T t,AbstractButton abstractButton,String tooltipText,KeyStroke keyStroke) {
            this.t=t;
            this.abstractButton=abstractButton;
            abstractButton.setName(t.name());
            abstractButton.setText(t.name());
            this.tooltipText=tooltipText;
            if(tooltipText!=null) abstractButton.setToolTipText(tooltipText);
            this.keyStroke=keyStroke;
            ButtonsABC.this.map.put(t,this);
        }
        public ButtonWithEnum(T t,AbstractButton abstractButton,String tooltipText) {
            this(t,abstractButton,tooltipText,null);
        }
        public ButtonWithEnum(T t,String tooltipText) { this(t,new JButton(),tooltipText); }
        public ButtonWithEnum(T t,KeyStroke keyStroke) { this(t,(String)null); }
        // some of these may not be correct!
        public ButtonWithEnum(T t) { this(t,(String)null); }
        public ButtonWithEnum(T t,AbstractButton abstractButton) { this(t,abstractButton,null); }
        private T[] values() { // just for this enum constant.
            Class<T> clazz=t.getDeclaringClass();
            return clazz.getEnumConstants();
        }
        public void enableButton(boolean enable) { abstractButton.setEnabled(enable); }
        public boolean isEnabled() { return abstractButton.isEnabled(); }
        public final T t;
        public final String tooltipText;
        public final AbstractButton abstractButton;
        public final KeyStroke keyStroke;
    } // end of inner class
    public ButtonWithEnum get(Enum e) { return map.get(e); }
    public Set<Enum> enums() { return map.keySet(); }
    public Collection<ButtonWithEnum> buttons() { return map.values(); }
    public void enableAll(Mediator mediator) { // default behavious is to enable all of the buttons.
        for(ButtonWithEnum button:buttons()) button.abstractButton.setEnabled(true);
    }
    public Enum valueOf(String name) { for(Enum e:enums()) if(e.name().equals(name)) return e; return null; }
    public void add(Class<?> clazz) {
        Object[] enums=clazz.getEnumConstants();
        if(enums!=null&&enums.length>0) for(Object e:enums) new ButtonWithEnum((Enum)e);
        else throw new RuntimeException(clazz+" has no enums!");
    }
    private final Map<Enum,ButtonWithEnum> map=new LinkedHashMap<>();
    // class for a button and an enum constant.
}