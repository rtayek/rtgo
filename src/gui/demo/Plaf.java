package gui.demo;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import io.Logging;
import utilities.MainGui;
public class Plaf extends MainGui {
    public static String getLookAndFeelClassName(String nameSnippet) {
        LookAndFeelInfo[] plafs=UIManager.getInstalledLookAndFeels();
        for(LookAndFeelInfo info:plafs) { if(info.getName().contains(nameSnippet)) { return info.getClassName(); } }
        return null;
    }
    void foo() {
        try {
            JFileChooser chooser=new JFileChooser(lastLoadDirectory);
            int rc=chooser.showOpenDialog(null);
            if(rc==JFileChooser.APPROVE_OPTION) {
                lastOpenFile=chooser.getSelectedFile();
                lastLoadDirectory=lastOpenFile.getParentFile();
                Logging.mainLogger.info(String.valueOf(lastOpenFile));
            }
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }
    public static void setPlaf(LookAndFeelInfo plaf_) {
        Logging.mainLogger.info(String.valueOf(plaf_));
        try {
            UIManager.setLookAndFeel(plaf_.getClassName());
        } catch(ClassNotFoundException|InstantiationException|IllegalAccessException
                |UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }
    static void run(LookAndFeelInfo plaf_) {
        setPlaf(plaf_);
        Plaf plaf=new Plaf();
        plaf.foo();
        //plaf.frame().dispose();
    }
    public static void main(String[] args) {
        LookAndFeelInfo[] plafs=UIManager.getInstalledLookAndFeels();
        Logging.mainLogger.info(String.valueOf(Arrays.asList(plafs)));
        if(true) { run(plafs[2]); return; }
        for(LookAndFeelInfo plaf_:plafs) {
            Logging.mainLogger.info(String.valueOf(plaf_));
            run(plaf_);
        }
    }
    transient File lastLoadDirectory,lastSaveDirectory,lastOpenFile;
}
