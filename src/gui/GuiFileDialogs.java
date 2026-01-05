package gui;
import java.awt.Component;
import java.io.File;
import javax.swing.JFileChooser;
import javax.swing.filechooser.FileNameExtensionFilter;
final class GuiFileDialogs {
    record FileSelection(File file,File directory) {}
    private GuiFileDialogs() {}
    static FileSelection chooseOpenSgf(Component parent,File startDir) {
        JFileChooser chooser=createChooser(startDir);
        if(chooser.showOpenDialog(parent)==JFileChooser.APPROVE_OPTION) {
            File file=chooser.getSelectedFile();
            return new FileSelection(file,file.getParentFile());
        }
        return null;
    }
    static FileSelection chooseSaveSgf(Component parent,File startDir) {
        JFileChooser chooser=createChooser(startDir);
        if(chooser.showSaveDialog(parent)==JFileChooser.APPROVE_OPTION) {
            File file=chooser.getSelectedFile();
            return new FileSelection(file,file.getParentFile());
        }
        return null;
    }
    private static JFileChooser createChooser(File startDir) {
        JFileChooser chooser=new JFileChooser(startDir!=null?startDir:new File("."));
        chooser.setFileFilter(new FileNameExtensionFilter("SGF file","sgf"));
        return chooser;
    }
}
