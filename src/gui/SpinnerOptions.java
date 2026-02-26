package gui;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.*;
import io.Logging;
import model.*;
import model.Interfaces.*;
import com.tayek.util.core.Range;
// we need a clas in between options abc and this class.
abstract class WidgetOptions extends OptionsABC implements Widgets {
	 public abstract class WidgetOption<T extends Enum<T>,R extends Comparable<R>>extends Option<T,R> implements Widget {
        public WidgetOption(T t,Object defaultValue,Range<R> range) { super(t,defaultValue,range); }
    }
}
public class SpinnerOptions extends WidgetOptions {
    public class SpinnerOption<T extends Enum<T>,R extends Comparable<R>>extends WidgetOption<T,R> {
        public SpinnerOption(T t,Object defaultValue,Range<R> range,JSpinner jSpinner,String tooltipText,
                KeyStroke keyStroke) {
            super(t,defaultValue,range);
            this.jSpinner=jSpinner;
            jSpinner.setName(t.name());
            this.tooltipText=tooltipText!=null?tooltipText:t.name();
            if(this.tooltipText!=null) jSpinner.setToolTipText(this.tooltipText);
            this.keyStroke=keyStroke;
        }
        // add the other ctors later!
        public void enableButton(boolean enable) { jSpinner.setEnabled(enable); }
        public boolean isEnabled() { return jSpinner.isEnabled(); }
        @Override public boolean isValueInWidget(Object value) { //ok - all the same
            SpinnerListModel model=(SpinnerListModel)jSpinner.getModel();
            return model.getList().contains(value);
        }
        @Override public boolean setValueInWidget(Object value) {
            if(value==null) { Logging.mainLogger.warning("null value for parameter: "+t); return false; }
            if(isValueInWidget(value)) {
                SpinnerListModel model=(SpinnerListModel)jSpinner.getModel();
                model.setValue(value);
                return true;
            }
            Logging.mainLogger.info(""+" "+"can not set spinner to: "+value);
            return false;
        }
        @Override public boolean setValueInWidgetFromCurrentValue() { // for this parameter
            boolean rc=false;
            rc=isValueInWidget(currentValue());
            if(rc) setValueInWidget(currentValue());
            // maybe need to set the spinner in the spinner with an enum?
            // using the old parameters class
            // not sure, look at this later perhaps?
            return rc;
        }
        final String tooltipText;
        final JSpinner jSpinner;
        final KeyStroke keyStroke;
    } // end of inner option class
    @Override public void setValuesInWidgetsFromCurrentValues() {
        for(Option<?,?> button:options()) {
            Logging.mainLogger.info(String.valueOf(button.t));
            boolean ok=((SpinnerOption<?,?>)button).setValueInWidgetFromCurrentValue();
            if(!ok) Logging.mainLogger.info("not ok!");
        }
    }
    public void enableAll(Mediator mediator) { // default behavious is to enable all of the buttons.
        for(Option<?,?> button:options()) ((SpinnerOption<?,?>)button).jSpinner.setEnabled(true);
        // maybe we can just cast to j component?
        // or move up to a base class?
    }
}
class SpinnerParameterOptions extends SpinnerOptions {
    // change name
    SpinnerParameterOptions() {}
    @Override public void enableAll(Mediator mediator) {}
    private static JSpinner spinner(List<?> values,int width) {
        SpinnerListModel model=new SpinnerListModel(values);
        JSpinner jSpinner=new JSpinner(model);
        Dimension d=jSpinner.getPreferredSize();
        d.width=width;
        jSpinner.setPreferredSize(d);
        JSpinner.ListEditor editor=new JSpinner.ListEditor(jSpinner);
        JTextField tf=editor.getTextField();
        tf.setHorizontalAlignment(JTextField.CENTER);
        tf.setFont(new Font("lucida sans regular",Font.PLAIN,16));
        tf.setEditable(false);
        jSpinner.setEditor(editor);
        return jSpinner;
    }
    private <R extends Comparable<R>> void addParameterSpinnerOption(Parameters parameter,List<R> values,int width) {
        new SpinnerOption<Parameters,R>(parameter,parameter.defaultValue,(Range<R>)null,spinner(values,width),(String)null,(KeyStroke)null) {
            @Override public Object fromString(String string) { return parameter.fromString(string); }
            @Override public Object currentValue() { return parameter.currentValue(); }
        };
    }
    // new game works after change.
    // running Main.main(0 does not.
    // so we need to initialize earlier?
    // how much of the above code can we push up into the abc or the options class?
    // the above is old.
    // 2/21/26 using codex to refactor.
    {
        addParameterSpinnerOption(Parameters.topology,Parameters.topologies,150);
        addParameterSpinnerOption(Parameters.shape,Parameters.shapes,100);
        addParameterSpinnerOption(Parameters.width,Parameters.sizes,50);
        addParameterSpinnerOption(Parameters.depth,Parameters.sizes,50);
        addParameterSpinnerOption(Parameters.band,Parameters.bands,30);
        addParameterSpinnerOption(Parameters.role,Parameters.roles,30);
    }
    public static void main(String[] args) {}
}
