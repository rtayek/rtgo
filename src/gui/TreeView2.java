package gui;
import java.io.StringReader;
import javax.swing.JFrame;
import javax.swing.tree.DefaultMutableTreeNode;
import io.Logging;
import model.Model;
import sgf.MNode;
import utilities.MyJApplet;
@SuppressWarnings("serial") public class TreeView2 extends TreeView { // why did i make
    // this?
    public TreeView2(MyJApplet applet,Model model) { super(applet,model); }
    /*
    void addChildren(DefaultMutableTreeNode treeNode,Node node) {
    if(node!=null) for(Node child:node.children) {
    DefaultMutableTreeNode childTreeNode=new DefaultMutableTreeNode(child);
    treeNode.add(childTreeNode);
    addChildren(childTreeNode,child);
    }
    else Logging.logger.info(model.name+" "+"node is null in add children!");
    }
    
    
     */
    @Override void addChildren(DefaultMutableTreeNode treeNode,MNode node) {
        if(node!=null) for(MNode child:node.children) {
            Node2 child2=new Node2(child,treeNode);
            DefaultMutableTreeNode childTreeNode=new DefaultMutableTreeNode(child2);
            treeNode.add(childTreeNode);
            addChildren(childTreeNode,child);
        }
        else Logging.mainLogger.info(model.name+" "+"node is null in add children!");
    }
    public static TreeView2 simple2() {
        Model model=new Model();
        TreeView2 myTreeView=new TreeView2(null,model);
        myTreeView.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        model.addObserver(myTreeView);
        //model.restore(IO.toReader(new File("sgf/ff4.sgf")));
        StringReader stringReader=new StringReader("(;C[frog])");
        model.restore(stringReader);
        return myTreeView;
    }
    public static void main(String[] args) { TreeView myTreeView=TreeView2.simple2(); }
}