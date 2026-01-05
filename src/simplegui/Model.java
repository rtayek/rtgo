package simplegui;
import io.Logging;
import java.util.Observable;
public class Model extends Observable {
    public Model() {}
    public void changeTemperature(double delta) { temperature+=delta; setChangedAndNotify("Temperature"); }
    public void changeHumidity(double delta) { humidity+=delta; setChangedAndNotify("Humidity"); }
    public void reset() { temperature=humidity=0; }
    public double getTemperature() { return temperature; }
    public void setTemperature(double temperature) { this.temperature=temperature; }
    public double getHumidity() { return humidity; }
    public void setHumidity(double humidity) { this.humidity=humidity; }
    public void setChangedAndNotify(Object object) { setChanged(); notifyObservers(object); }
    @Override public String toString() { return "Model [temperature="+temperature+", humidity="+humidity+"]"; }
    public static void main(String[] args) throws Exception {
        Model model=new Model();
        Logging.mainLogger.info(String.valueOf(model.toString()));
    }
    double temperature,humidity;
}