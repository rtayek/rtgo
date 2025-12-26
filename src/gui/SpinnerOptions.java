package gui;
import javax.swing.*;
import equipment.Board;
import equipment.Board.*;
import gui.Spinners.OldSpinners;
import io.Logging;
import model.*;
import model.Interfaces.*;
import utilities.Range;
// we need a clas in between options abc and this class.
abstract class WidgetOptions extends OptionsABC implements Widgets {
    public abstract class WidgetOption<T extends Enum<T>,R>extends Option<T,R> implements Widget {
        public WidgetOption(T t,Object defaultValue,Range<R> range) { super(t,defaultValue,range); }
    }
    JComponent jComponent;
}
public class SpinnerOptions extends WidgetOptions {
    Class<Parameters> c=Parameters.class; // hack
    public class SpinnerOption<T extends Enum<T>,R>extends WidgetOption<T,R> {
        public SpinnerOption(T t,Object defaultValue,Range<R> range,JSpinner jSpinner,String tooltipText,
                KeyStroke keyStroke) {
            super(t,defaultValue,range);
            this.jSpinner=jSpinner;
            jSpinner.setName(t.name());
            this.tooltipText=t.name();
            if(tooltipText!=null) jSpinner.setToolTipText(tooltipText);
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
        @Override public boolean setValueInWisget(Object value) {
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
            if(rc) setValueInWisget(currentValue());
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
            System.out.println(button.t);
            boolean ok=((SpinnerOption<?,?>)button).setValueInWidgetFromCurrentValue();
            if(!ok) System.out.println("not ok!");
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
    SpinnerParameterOptions() {
        initializeParameters(Parameters.propertiesFilename);
        // fix this name!
    }
    @Override public void enableAll(Mediator mediator) {}
    // new game works after change.
    // running Main.main(0 does not.
    // so we need to initialize earlier?
    // how much of the above code can we push up into the abc or the options class?
    {
        new SpinnerOption<Parameters,Topology>(Parameters.topology,(Topology)Parameters.topology.defaultValue,
                (Range<Topology>)null,OldSpinners.spinner(Parameters.topologies,150),(String)null,(KeyStroke)null) {
            @Override public Object fromString(String string) { return Topology.valueOf(string); }
        };
        new SpinnerOption<Parameters,Shape>(Parameters.shape,(Shape)Parameters.shape.defaultValue,(Range<Shape>)null,
                OldSpinners.spinner(Parameters.shapes,100),(String)null,(KeyStroke)null) {
            @Override public Object fromString(String string) { return Shape.valueOf(string); }
        };
        new SpinnerOption<Parameters,Integer>(Parameters.width,(Integer)Board.standard,(Range<Integer>)null,
                OldSpinners.spinner(Parameters.sizes,50),(String)null,(KeyStroke)null);
        new SpinnerOption<Parameters,Integer>(Parameters.depth,(Integer)Board.standard,(Range<Integer>)null,
                OldSpinners.spinner(Parameters.sizes,50),(String)null,(KeyStroke)null);
        new SpinnerOption<Parameters,Integer>(Parameters.band,(Integer)Parameters.band.defaultValue,
                (Range<Integer>)null,OldSpinners.spinner(Parameters.bands,30),(String)null,(KeyStroke)null);
        new SpinnerOption<Parameters,Model.Role>(Parameters.role,(Model.Role)Parameters.role.defaultValue,
                (Range<Model.Role>)null,OldSpinners.spinner(Parameters.roles,30),(String)null,(KeyStroke)null) {
            @Override public Object fromString(String string) { return Model.Role.valueOf(string); }
        };
    }
    public static void main(String[] args) {}
}
