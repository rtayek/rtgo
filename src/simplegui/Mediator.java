package simplegui;
import java.awt.BorderLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Observable;
import java.util.Observer;
import javax.swing.BoxLayout;
import javax.swing.JLabel;
import javax.swing.JPanel;
import io.Logging;
class Mediator implements Observer,ActionListener {
    // anyway to disconnect this from the model?
    // like for web presentation?
    Mediator(Model model,Main mainGui,JPanel center) {
        if(model==null) throw new RuntimeException("oops");
        this.model=model;
        // add a panel with wigets
        BorderLayout borderLayout=(BorderLayout)mainGui.getLayout();
        // remove the old ones. why?
        // don't know. but that was the reason the bottom went away!.
        // maybe we were trying to replace the bottom?
        /*
        Component old=borderLayout.getLayoutComponent(BorderLayout.PAGE_END);
        System.out.println(old);
        if(old!=null) {
            System.out.println("removing: "+old);mainGui.remove(old); mainGui.validate(); }
        else System.out.println("not removing.");
        */
        //center.add(new JLabel("center"),BorderLayout.CENTER);
        //center.add(new JLabel("center2"),BorderLayout.CENTER);
        addGamePanels(center);
        model.addObserver(this);
        // model.notify(Event.start,"new mediator");
    }
    @Override public void actionPerformed(ActionEvent e) {
        Logging.mainLogger.info(model+" "+"action performed: "+e);
        if(e.getActionCommand().equals("Open ...")) {} else if(e.getActionCommand().equals("Save ...")) {}
    }
    @Override public void update(Observable observable,Object hint) {
        Logging.mainLogger.info(model+" "+observable.getClass().getName()+", hint: "+hint);
        if(observable instanceof Model) {
            Model model=(Model)observable;
            if(model==this.model) {
                Logging.mainLogger.fine(model+" "+"hint: "+hint);
            } else Logging.mainLogger.fine(model+" "+"hint="+hint+" is not our model!");
        } else throw new RuntimeException("not a model!");
    }
    private void addGamePanels(JPanel center) {
        JPanel outerPanel=new JPanel();
        outerPanel.setLayout(new BoxLayout(outerPanel,BoxLayout.Y_AXIS));
        JPanel gamePanel=new JPanel();
        gamePanel.add(new JLabel("game"));
        outerPanel.add(gamePanel);
        outerPanel.add(new JLabel("in outer"));
        center.add(outerPanel,BorderLayout.CENTER);
        center.add(new ControlPanel(this));
    }
    void action(ControlPanel.Buttons button) {
        Logging.mainLogger.info(model+" "+"click: "+button.name());
        switch(button) {
            case humidity:
                System.out.println(button);
                model.changeHumidity(1);
                break;
            case temperature:
                System.out.println(button);
                model.changeTemperature(1);
                break;
            default:
                Logging.mainLogger.info(model+" "+button+" was not handled!");
        }
    }
    public static void main(String[] args) { Main.main(new String[] {}); } // just run main
    final Model model;
    final JLabel status=new JLabel(" ");
}
