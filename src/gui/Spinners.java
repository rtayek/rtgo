package gui;
import java.awt.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.event.*;
import equipment.Board.Shape;
import equipment.Board.Topology;
import io.Logging;
import model.*;
import model.Interfaces.*;
import com.tayek.util.io.PropertiesIO;
public class Spinners {
    public static class NewSpinners {
        public static class SpinnersABC implements Persistance,Widgets {
            // does this need to be abstract>
            public class SpinnerWithAnEnum<T extends Enum<T>> implements Widget {
                public SpinnerWithAnEnum(Object defaultValue,T t,JSpinner jSpinner,String tooltipText,
                        KeyStroke keyStroke) {
                    // this looks like a good candiate to use option as a base class.
                    this.defaultValue=currentValue=defaultValue;
                    this.t=t;
                    this.jSpinner=jSpinner;
                    jSpinner.setName(t.name());
                    this.tooltipText=t.name();
                    if(tooltipText!=null) jSpinner.setToolTipText(tooltipText);
                    this.keyStroke=keyStroke; // may not be relavent?
                    SpinnersABC.this.map.put(t,this);
                }
                public SpinnerWithAnEnum(Object defaultValue,T t,JSpinner jSpinner,String tooltipText) {
                    this(defaultValue,t,jSpinner,tooltipText,(KeyStroke)null);
                }
                public SpinnerWithAnEnum(Object defaultValue,T t,String tooltipText) {
                    this(defaultValue,t,new JSpinner(),tooltipText);
                }
                public SpinnerWithAnEnum(Object defaultValue,T t,KeyStroke keyStroke) {
                    this(defaultValue,t,(String)null);
                }
                // some of this may not be right!
                public SpinnerWithAnEnum(Object defaultValue,T t) { this(defaultValue,t,(String)null); } // was ambiguous
                public SpinnerWithAnEnum(Object defaultValue,T t,JSpinner jSpinner) {
                    this(defaultValue,t,jSpinner,null);
                }
                Object fromString(String string) { return Integer.valueOf(string); } // maybe should be double?
                // needs to be overwritten
                private T[] values() { // just for this enum constant.
                    Class<T> clazz=t.getDeclaringClass();
                    return clazz.getEnumConstants();
                }
                //
                public void enableButton(boolean enable) { jSpinner.setEnabled(enable); }
                public boolean isEnabled() { return jSpinner.isEnabled(); }
                @Override public boolean isValueInWidget(Object value) {
                    SpinnerListModel model=(SpinnerListModel)jSpinner.getModel();
                    for(Object o:model.getList()) if(o.equals(value)) return true;
                    return false;
                }
                @Override public boolean setValueInWisget(Object value) { // in the gui?
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
                    if(t instanceof Parameters) {
                        Parameters parameter=(Parameters)t;
                        rc=isValueInWidget(currentValue);
                        if(rc) setValueInWisget(currentValue); // maybe wrong object??
                        // maybe need to set the spinner in the spinner with an enum?
                        // using the old parameters class
                    }
                    return rc;
                }
                public final T t; // is an instance of  parameters
                public final String tooltipText;
                final JSpinner jSpinner;
                public final KeyStroke keyStroke;
                public final Object defaultValue;
                Object currentValue; // make stuff like this private!
            } // end of inner class
            public SpinnerWithAnEnum<?> get(Enum<?> e) { return map.get(e); }
            public Set<Enum<?>> enums() { return map.keySet(); }
            public Collection<SpinnerWithAnEnum<?>> buttons() { return map.values(); }
            public void enableAll(Mediator mediator) { // default behavious is to enable all of the buttons.
                for(SpinnerWithAnEnum<?> button:buttons()) button.jSpinner.setEnabled(true);
            }
            public Enum<?> valueOf(String name) { for(Enum<?> e:enums()) if(e.name().equals(name)) return e; return null; }
            @Override public void setValuesInWidgetsFromCurrentValues() { // maybe bogus?
                // no, move above to new spinnes (not spinner optiomns).
                for(SpinnerWithAnEnum<?> button:buttons()) {
                    Logging.mainLogger.info(String.valueOf(button.t));
                    boolean ok=button.setValueInWidgetFromCurrentValue();
                    if(!ok) Logging.mainLogger.info("not ok!");
                }
            }
            // make sure these below work correctly (like parameters)
            // then see if we can push them up!
            // options has these 2 methods:
            @Override public void setCurrentValuesFromProperties(Properties properties) {
                for(SpinnerWithAnEnum<?> button:buttons()) if(properties.containsKey(button.t.name()))
                    button.currentValue=button.fromString(properties.getProperty(button.t.name()));
                else Logging.mainLogger.info("can not find property: "+button.t.name()+" in "+properties);
            }
            @Override public void setPropertiesFromCurrentValues(Properties properties) {
                for(SpinnerWithAnEnum<?> parameter:buttons())
                    properties.setProperty(parameter.t.name(),parameter.currentValue.toString());
            }
            Properties currentValuesAsProperties() {
                Properties properties=new Properties();
                setPropertiesFromCurrentValues(properties);
                return properties;
            }
            @Override public void loadCurrentValuesFromPropertiesFile(String propertiesFilename) {
                Properties properties=PropertiesIO.loadOrCreatePropertiesFile(currentValuesAsProperties(),propertiesFilename);
                Logging.mainLogger.info("loaded: "+properties);
                setCurrentValuesFromProperties(properties);
            }
            @Override public void storeCurrentValuesInPropertiesFile(String filename) {
                PropertiesIO.writePropertiesFile(currentValuesAsProperties(),filename);
            }
            @Override public void initializeParameters(String filename) {
                loadCurrentValuesFromPropertiesFile(filename);
            }
            @Override public String toString() {
                LinkedHashMap<String,Object> map=new LinkedHashMap<>();
                for(Enum<?> e:enums()) map.put(e.getClass().getName()+"."+e.name(),get(e).currentValue);
                return map.toString();
            }
            final Map<Enum<?>,SpinnerWithAnEnum<?>> map=new LinkedHashMap<>();
        }
        public static class ParameterSpinners extends SpinnersABC {
            ParameterSpinners() { initializeParameters(Parameters.propertiesFilename); }
            @Override public void enableAll(Mediator mediator) {}
            // new game works after change.
            // running Main.main(0 does not.
            // so we need to initialize earlier?
            // how much of the above code can we push up into the abc or the options class?
            {
                new SpinnerWithAnEnum<Parameters>(Parameters.topology.defaultValue,Parameters.topology,
                        OldSpinners.spinner(Parameters.topologies,150)) {
                    @Override Object fromString(String string) { return Topology.valueOf(string); }
                };
                new SpinnerWithAnEnum<Parameters>(Parameters.shape.defaultValue,Parameters.shape,
                        OldSpinners.spinner(Parameters.shapes,100)) {
                    @Override Object fromString(String string) { return Shape.valueOf(string); }
                };
                new SpinnerWithAnEnum<Parameters>(Parameters.width.defaultValue,Parameters.width,
                        OldSpinners.spinner(Parameters.sizes,50));
                new SpinnerWithAnEnum<Parameters>(Parameters.depth.defaultValue,Parameters.depth,
                        OldSpinners.spinner(Parameters.sizes,50));
                new SpinnerWithAnEnum<Parameters>(Parameters.band.defaultValue,Parameters.band,
                        OldSpinners.spinner(Parameters.bands,30));
                new SpinnerWithAnEnum<Parameters>(Parameters.role.defaultValue,Parameters.role,
                        OldSpinners.spinner(Parameters.roles,100)) {
                    @Override Object fromString(String string) { return Model.Role.valueOf(string); }
                };
            }
        }
    }
    static class OldSpinners implements Widgets {
        // this has-an enum.
        // maybe new top panel can use this?
        OldSpinners(Parameters parameter,JSpinner jSpinner) {
            this.parameter=parameter;
            this.jSpinner=jSpinner;
            jSpinner.setName(parameter.name());
            this.tooltipText=parameter.name();
            if(tooltipText!=null) jSpinner.setToolTipText(tooltipText);
            // this.defaultValue=defaultValue; // get value from parameters?
            // setValue(defaultValue);
        }
        boolean isValueInWidgetrr(Object value) {
            SpinnerListModel model=(SpinnerListModel)jSpinner.getModel();
            for(Object o:model.getList()) if(o.equals(value)) return true;
            return false;
        }
        boolean setValueInWidgetFromCurrentValue() {
            boolean rc=isValueInWidgetrr(parameter.currentValue());
            if(rc) setValueInWidget(parameter.currentValue()); // maybe wrong object??
            return rc;
        }
        boolean setValueInWidget(Object value) {
            if(value==null) { Logging.mainLogger.warning("null value for parameter: "+parameter); return false; }
            if(isValueInWidgetrr(value)) {
                SpinnerListModel model=(SpinnerListModel)jSpinner.getModel();
                for(Object o:model.getList()) if(o.equals(value)) { model.setValue(value); return true; }
            }
            Logging.mainLogger.info(""+" "+"can not set spinner to: "+value);
            return false;
        }
        @Override public void setValuesInWidgetsFromCurrentValues() { staticStValuesInWidgetsFromCurrentValues(); }
        public static void staticStValuesInWidgetsFromCurrentValues() {
            for(OldSpinners spinner:map.values()) spinner.setValueInWidgetFromCurrentValue();
        }
        static JSpinner spinner(List<?> values,int width) {
            SpinnerListModel model=new SpinnerListModel(values);
            if(false) model.addChangeListener(new ChangeListener() {
                @Override public void stateChanged(ChangeEvent e) {
                    Logging.mainLogger.warning("state change: "+e+" ignored in model: "+model);
                }
            });
            JSpinner jSpinner=new JSpinner(model);
            Dimension d=jSpinner.getPreferredSize();
            d.width=width;
            jSpinner.setPreferredSize(d);
            // modify the JTextField appearance
            JSpinner.ListEditor editor=new JSpinner.ListEditor(jSpinner);
            JTextField tf=editor.getTextField();
            tf.setHorizontalAlignment(JTextField.CENTER);
            tf.setFont(new Font("lucida sans regular",Font.PLAIN,16));
            tf.setEditable(false);
            jSpinner.setEditor(editor);
            return jSpinner;
        }
        final Parameters parameter;
        final JSpinner jSpinner;
        String tooltipText;
        static final Map<Parameters,OldSpinners> map=new LinkedHashMap<>(Parameters.values().length);
        static {
            map.put(Parameters.topology,
                    new OldSpinners(Parameters.topology,OldSpinners.spinner(Parameters.topologies,150)));
            map.put(Parameters.shape,new OldSpinners(Parameters.shape,OldSpinners.spinner(Parameters.shapes,100)));
            map.put(Parameters.width,new OldSpinners(Parameters.width,OldSpinners.spinner(Parameters.sizes,60)));
            map.put(Parameters.depth,new OldSpinners(Parameters.depth,OldSpinners.spinner(Parameters.sizes,60)));
            map.put(Parameters.band,new OldSpinners(Parameters.band,OldSpinners.spinner(Parameters.bands,30)));
            map.put(Parameters.role,new OldSpinners(Parameters.role,OldSpinners.spinner(Parameters.roles,100)));
            if(map.size()!=Parameters.values().length)
                Logging.mainLogger.severe("different number of paramaters and spinners!");
        }
    }
}
