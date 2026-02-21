package gui;
import java.awt.Dimension;
import java.awt.Font;
import java.util.List;
import javax.swing.JSpinner;
import javax.swing.SpinnerListModel;
import javax.swing.JTextField;
public class Spinners {
    static class OldSpinners {
        static JSpinner spinner(List<?> values,int width) {
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
    }
}
