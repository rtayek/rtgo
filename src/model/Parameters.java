package model;
import io.Logging;
import java.util.*;
import com.tayek.util.core.EnumProperties;
import com.tayek.util.io.PropertiesIO;
import equipment.Board;
import equipment.Board.*;
import model.Model.Role;
// matbe we can salvage some of this suff
public enum Parameters { // properties
    // same problem with enums that have buttons
    // write new code with spinners that have an enum.
    // done - see new paramter spinner class.
    width(Board.standard),depth(Board.standard),shape(Shape.normal) {
        @Override public Shape fromString(String string) { return Shape.valueOf(string); }
    },
    topology(Topology.normal) {
        @Override public Topology fromString(String string) { return Topology.valueOf(string); }
    },
    band(4),role(Role.anything) {
        @Override public Role fromString(String string) { return Role.valueOf(string); }
    };
    Parameters(Object defaultValue) { this.defaultValue=currentValue=defaultValue; }
    public void reset() { currentValue=defaultValue; }
    public Object fromString(String string) { return Integer.valueOf(string); } // maybe should be double?
    public Object currentValue() { return currentValue; }
    @Override public String toString() { return name()+"="+currentValue+"("+defaultValue+")"; }
    // also old change
    public static void change(Parameters parameter,Object value) {
        //Logging.mainLogger.info(parameter.name()+" changed from: "+parameter.currentValue()+"+ to: "+value);
        parameter.currentValue=value; // fromn spinner
        storeCurrentValuesInPropertiesFile(propertiesFilename);
    }
    public static void resetAll() { for(Parameters parameter:Parameters.values()) parameter.reset(); }
    static void setCurrentValuesFromProperties(Properties properties) {
        EnumProperties.apply(properties,values(),(parameter,property)->parameter.currentValue=parameter.fromString(property),
                (parameter)->Logging.mainLogger.info("can not find property: "+parameter.name()+" in "+properties));
    }
    static void setPropertiesFromCurrentValues(Properties properties) {
        EnumProperties.putInto(properties,values(),Parameters::currentValue);
    }
    static Properties currentValuesAsProperties() {
        return EnumProperties.toProperties(Parameters.values(),Parameters::currentValue);
    }
    static void setCurrentValuesFromPropertiesFile() {
        setCurrentValuesFromPropertiesFile(propertiesFilename);
    }
    static void setCurrentValuesFromPropertiesFile(String filename) {
        Properties properties=PropertiesIO.loadOrCreatePropertiesFile(currentValuesAsProperties(),filename);
        setCurrentValuesFromProperties(properties);
    }
    static void storeCurrentValuesInPropertiesFile(String filename) {
        PropertiesIO.writePropertiesFile(currentValuesAsProperties(),filename);
    }
    static void initializeParameters(String filename) {
        setCurrentValuesFromPropertiesFile(filename);
    }
    public static void main(String argv[]) {
        for(Parameters parameters:Parameters.values()) Logging.mainLogger.info(String.valueOf(parameters));
    }
    // they all seem to be new game parameters
    // investigate what this means. look around new game in model.
    // 7/11/2021 yes, and we don't seem to be using them
    // maybe keep the current values from the spinners in here?
    // no, they need to be in the new spinners.
    public final Object defaultValue;
    private Object currentValue;
    public static final String propertiesFilename="tgo.properties";
    static {
        //Init.Main.main(null);
        Logging.mainLogger.info("static init in parameters class.");
        initializeParameters(propertiesFilename); // move?
    }
    // these are the static values for the model lists?.
    // yes.
    // this is what the new spinners need.
    public static final List<Integer> bands=Collections
            .unmodifiableList(Arrays.asList(new Integer[] {0,1,2,3,4,5,6,7}));
    public static final List<Integer> sizes=Collections
            .unmodifiableList(Arrays.asList(new Integer[] {1,2,3,5,7,9,11,13,15,17,19,21,23,25,37,51}));
    public static final List<Topology> topologies=Collections.unmodifiableList(Arrays.asList(Topology.values()));
    public static final List<Shape> shapes=Collections.unmodifiableList(Arrays.asList(Shape.values()));
    public static final List<Role> roles=Collections.unmodifiableList(Arrays.asList(Role.values()));
}
