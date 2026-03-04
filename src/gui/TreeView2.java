package gui;
import static io.Init.first;
import java.io.File;
import java.io.StringReader;
import javax.swing.JFrame;
import model.Model;
import model.ModelIo;
import sgf.Parser;
import utilities.MyJApplet;
import com.tayek.util.io.FileIO;
@SuppressWarnings("serial") public class TreeView2 extends TreeView {
	// why did i make this?
	public TreeView2(MyJApplet applet,Model model) {
		super(applet,model);
	}
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
	public static TreeView2 simple2() {
		Model model=new Model();
		TreeView2 myTreeView=new TreeView2(null,model);
		myTreeView.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		model.addObserver(myTreeView);
		if(true) ModelIo.restoreModel(model,FileIO.toReader(new File(Parser.sgfPath,"ff4_ex.sgf")));
		else {
			StringReader stringReader=new StringReader("(;C[frog])");
			ModelIo.restoreModel(model,stringReader);
		}
		//System.out.println(model);
		//System.out.println(model.currentNode());
		//System.out.println(model.currentNode().children().size()+" children.");
		return myTreeView;
	}
	public static void main(String[] args) {
		first.twice(); // do this first in all main programs!
		TreeView myTreeView=TreeView2.simple2();
	}
}
