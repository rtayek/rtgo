package gui;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.UIManager.LookAndFeelInfo;
import javax.swing.filechooser.FileNameExtensionFilter;
import utilities.MainGui;
public class Plaf extends MainGui {
    public static String getLookAndFeelClassName(String nameSnippet) {
        LookAndFeelInfo[] plafs=UIManager.getInstalledLookAndFeels();
        for(LookAndFeelInfo info:plafs) { if(info.getName().contains(nameSnippet)) { return info.getClassName(); } }
        return null;
    }
    void foo() {
        try {
            JFileChooser fileChoser=new JFileChooser(lastLoadDirectory!=null?lastLoadDirectory:new File("."));
            fileChoser.setFileFilter(new FileNameExtensionFilter("SGF file","sgf"));
            if(fileChoser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
                File file=fileChoser.getSelectedFile();
                System.out.println(file);
            }
        } catch(Exception ex) {
            ex.printStackTrace();
            Toast.toast(ex.toString());
        }
    }
    public static void setPlaf(LookAndFeelInfo plaf_) {
        System.out.println(plaf_);
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
        System.out.println(Arrays.asList(plafs));
        if(true) { run(plafs[2]); return; }
        for(LookAndFeelInfo plaf_:plafs) {
            System.out.println(plaf_);
            run(plaf_);
        }
    }
    transient File lastLoadDirectory,lastSaveDirectory,lastOpenFile;
}
