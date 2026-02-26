package gui;
import java.util.List;
import javax.swing.*;
import gui.Spinners.OldSpinners;
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
            for(Object o:model.getList()) if(o.equals(value)) return true;
            return false;
        }
        @Override public boolean setValueInWidget(Object value) {
            if(value==null) { Logging.mainLogger.warning("null value for parameter: "+t); return false; }
            if(isValueInWidget(value)) {
                SpinnerListModel model=(SpinnerListModel)jSpinner.getModel();
                for(Object o:model.getList()) if(o.equals(value)) { model.setValue(value); return true; }
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
    @Override public void setValuesInWidgetsFromCurrentValues() {
        for(Option<?,?> option:options()) {
            Parameters parameter=(Parameters)option.t;
            Object value=parameter.currentValue();
            SpinnerOption<?,?> spinnerOption=(SpinnerOption<?,?>)option;
            boolean ok=spinnerOption.setValueInWidget(value);
            if(!ok) Logging.mainLogger.info("can not set spinner value for parameter: "+parameter+" to: "+value);
            option.setCurrentValue(value);
        }
    }
    private <R extends Comparable<R>> void addParameterSpinnerOption(Parameters parameter,List<R> values,int width) {
        new SpinnerOption<Parameters,R>(parameter,parameter.defaultValue,(Range<R>)null,OldSpinners.spinner(values,width),(String)null,(KeyStroke)null) {
            @Override public Object fromString(String string) { return parameter.fromString(string); }
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
