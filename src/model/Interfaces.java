package model;
import java.util.Properties;
public interface Interfaces {
    interface Persistance {
        void setCurrentValuesFromProperties(Properties properties);
        // examine the differences between the implementation.
        // make sure to look at the static version in parameters.
        void setPropertiesFromCurrentValues(Properties properties);
        void loadCurrentValuesFromPropertiesFile(String propertiesFilename);
        void storeCurrentValuesInPropertiesFile(String filename);
        void initializeParameters(String filename);
    }
    interface Widget { //
        boolean isValueInWidget(Object value);
        boolean setValueInWisget(Object value);
        boolean setValueInWidgetFromCurrentValue();
    }
    interface Widgets { //
        void setValuesInWidgetsFromCurrentValues();
    }
}
