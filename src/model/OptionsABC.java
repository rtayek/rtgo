package model;
import static utilities.Utilities.*;
import java.io.File;
import java.util.*;
import model.Interfaces.Persistance;
import utilities.Range;
public class OptionsABC implements Persistance { // an instance of options.
    public class Option<T extends Enum<T>,R> { // an instance of one option.
        // should this be abstract?
        // no!
        // change T or N or Name?
        // names are from the enums.
        // values can be any primitive objects (Integer, Double ... ).
        // or from a single set of enum constants
        public Option(T t,Object defaultValue,Range<R> range) {
            // Rabge<T> above is wrongg.
            this.t=t;
            this.defaultValue=currentValue=defaultValue;
            this.range=range;
            map.put(t,this);
        }
        public Option(T t,Object defaultValue) { this(t,defaultValue,null); }
        public Object fromString(String string) { return Integer.valueOf(string); } // maybe should be double?
        // above needs to be overwritten for special cases.
        private Enum<?>[] values() { // just for this enum constant.
            // this wants to get the values for this option if it's values are enums.
            // so this is not a T!
            @SuppressWarnings("unchecked")
            Class<T> clazz=(Class<T>)t.getDeclaringClass();
            // this will not work.
            // we need to get the enums fro the default value?
            // do not forget to do this!
            return clazz.getEnumConstants();
        }
        public void reset() { currentValue=defaultValue; }
        public Object setCurrentValue(Object newValue) {
            Object previousValue=currentValue;
            currentValue=newValue;
            return previousValue;
        }
        public Object currentValue() { return currentValue; }
        @Override public String toString() { return t.name()+"="+currentValue+"("+defaultValue+")"; }
        public final T t; // just to give it a name
        private Object currentValue;
        public final Object defaultValue;
        public final Range<R> range;
        // subclass to get option with a widget.
        // or ?
    } // end of inner option class.
    @SuppressWarnings("unchecked") public <T extends Enum<T>,R> Option<T,R> get(T name) {
        return (Option<T,R>)map.get(name);
    }
    public Option<?,?> getOption(Enum<?> name) { return map.get(name); }
    public Set<Enum<?>> enums() { return map.keySet(); }
    public Collection<Option<?,?>> options() { return map.values(); }
    public Enum<?> valueOf(String name) { for(Enum<?> e:enums()) if(e.name().equals(name)) return e; return null; }
    @Override public void setCurrentValuesFromProperties(Properties properties) {
        for(Option<?,?> option:options()) if(properties.containsKey(option.t.name())) {
            option.currentValue=option.fromString(properties.getProperty(option.t.name()));
        } else System.out.println("can not find option: "+option.t.name()+" in "+properties);
    }
    @Override public void setPropertiesFromCurrentValues(Properties properties) {
        for(Option<?,?> option:options()) properties.setProperty(option.t.name(),option.currentValue.toString());
    }
    @Override public void loadCurrentValuesFromPropertiesFile(String propertiesFilename) {
        Properties properties=new Properties();
        load(properties,propertiesFilename);
        System.out.println(properties);
        setCurrentValuesFromProperties(properties);
    }
    @Override public void storeCurrentValuesInPropertiesFile(String filename) {
        Properties properties=new Properties();
        setPropertiesFromCurrentValues(properties);
        store(properties,filename);
    }
    @Override public void initializeParameters(String filename) {
        // can we move this to base class?
        // we are in the base class
        File file=new File(filename);
        if(file.exists()) loadCurrentValuesFromPropertiesFile(filename);
        else storeCurrentValuesInPropertiesFile(filename);
    }
    @Override public String toString() {
        LinkedHashMap<String,Object> map=new LinkedHashMap<>();
        for(Enum<?> e:enums()) map.put(e.getClass().getSimpleName()+"."+e.name(),getOption(e).currentValue);
        return map.toString();
    }
    public String toLongString() {
        LinkedHashMap<String,Object> map=new LinkedHashMap<>();
        for(Enum<?> e:enums()) map.put('\n'+e.getClass().getSimpleName()+"."+e.name(),getOption(e).currentValue);
        return map.toString();
    }
    public static <T extends Enum<T>> T fromString(Class<T> clazz,String string) {
        // move to utilities?
        // put with other from strings?
        if(clazz!=null&&string!=null) {
            try {
                return Enum.valueOf(clazz,string);
            } catch(IllegalArgumentException e) {}
        }
        return null;
    }
    LinkedHashMap<Enum<?>,Option<?,?>> map=new LinkedHashMap<>();
    public static void main(String[] args) {
        //
    }
    // examples of options.
    // some used by tests.
    public enum Names { letter, color, size, percent; }
    // the above are just names for each option in the group.
    // these below could be like the above or
    // they could be values for the option.
    // try to give examples of both or some tests for both.
    public enum Letter { a, b, c; }
    public enum Color { red, green, blue; }
    public enum Frog { fred; }
    public static class Options0 extends OptionsABC {
        {
            new OptionsABC.Option<Frog,Integer>(Frog.fred,Integer.valueOf(42)) {
                @Override public Integer fromString(String string) { return Integer.valueOf(string); }
            };
        }
    }
    public static class Options1 extends OptionsABC {
        {
            new OptionsABC.Option<Letter,Integer>(Letter.a,Integer.valueOf(42)) {
                @Override public Integer fromString(String string) { return Integer.valueOf(string); }
            };
            new OptionsABC.Option<Color,Double>(Color.red,Float.valueOf(.5f)) {
                @Override public Double fromString(String string) { return Double.valueOf(string); }
            };
        }
    }
    public static class Options2 extends OptionsABC {
        // the above are the values for the options
        {
            new OptionsABC.Option<Names,Letter>(Names.letter,Letter.a) {
                @Override public Letter fromString(String string) { return Letter.valueOf(string); }
            };
            new OptionsABC.Option<Names,Color>(Names.color,Color.green) {
                @Override public Color fromString(String string) { return Color.valueOf(string); }
            };
            new OptionsABC.Option<Names,Integer>(Names.size,Integer.valueOf(42)) {
                @Override public Integer fromString(String string) { return Integer.valueOf(string); }
            };
            new OptionsABC.Option<Names,Double>(Names.percent,Double.valueOf(12.34)) {
                @Override public Double fromString(String string) { return Double.valueOf(string); }
            };
        }
    }
}
