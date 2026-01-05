package gui;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import javax.swing.*;
import javax.swing.event.*;
import javax.swing.tree.*;
import io.*;
import model.Event;
import model.Model;
import model.ModelIo;
import sgf.MNode;
import utilities.*;
@SuppressWarnings("serial") public class TreeView extends MainGui implements Observer,TreeSelectionListener,ActionListener {
	public TreeView(MyJApplet applet,Model model) {
		super(applet);
		Logging.mainLogger.info(model.name+" "+"constructed");
		this.model=model;
	}
	@Override public String title() {
		return "SGF Tree View";
	}
	TreePath find(Object object,DefaultMutableTreeNode root) {
		Enumeration<TreeNode/*DefaultMutableTreeNode*/> e=root.depthFirstEnumeration();
		while(e.hasMoreElements()) {
			TreeNode /*DefaultMutableTreeNode*/ node=e.nextElement();
			Object useObject=((DefaultMutableTreeNode)node).getUserObject();
			if(useObject!=null) {
				if(((DefaultMutableTreeNode)node).getUserObject().equals(object)) return new TreePath(((DefaultMutableTreeNode)node).getPath());
			}
		}
		return null;
	}
	@Override public void update(Observable observable,Object hint) {
		Logging.mainLogger.fine(model.name+" , hint: "+hint);
		if(observable instanceof Model) {
			Model model=(Model)observable;
			if(model==this.model) {
				if(hint instanceof Event||hint instanceof Event.Hint) {
					Event event=null;
					if(hint instanceof Event) event=(Event)hint;
					else if(hint instanceof Event.Hint) {
						Event.Hint h=(Event.Hint)hint;
						event=h.event;
					}
					Logging.mainLogger.warning("event: "+event);
					switch(event) {
						case newTree: // fix up display
							repplaceTree(model);
							if(false) {
								// below is duplicated from change
								// just a quick hack to get the root selected.
								MNode current=model.currentNode();
								DefaultMutableTreeNode root=(DefaultMutableTreeNode)tree.getModel().getRoot();
								TreePath treePath=find(current,root);
								tree.setSelectionPath(treePath);
								tree.scrollPathToVisible(treePath);
								// but it does not work when starting the client
								// or restoring from a file.
							}
							break;
						case nodeChanged: // in model
							// this is called for every move?
							// try to not do this
							repplaceTree(model); // this maybe causing the out
													// of memory errors with
													// kages
							MNode current=model.currentNode();
							DefaultMutableTreeNode root=(DefaultMutableTreeNode)tree.getModel().getRoot();
							TreePath treePath=find(current,root);
							tree.setSelectionPath(treePath);
							tree.scrollPathToVisible(treePath);
							break;
						default:
							Logging.mainLogger.info(model.name+" "+"unhandled event hint: "+hint);
					}
				} else Logging.mainLogger.info(model.name+" "+"hint="+hint);
			} else throw new RuntimeException("not our model!");
		} else throw new RuntimeException("not a model!");
	}
	private void repplaceTree(Model model) {
		Logging.mainLogger.fine("start replacing tree");
		tree=from(model.root());
		if(tree!=null) if(treeView!=null) {
			treeView.setViewportView(tree);
			treeView.validate();
			frame.setResizable(true);
			frame.setSize(new Dimension(640,480));
			Logging.mainLogger.fine("end replacing tree");
		} else Logging.mainLogger.info(model.name+" "+"tree view is null!");
		else Logging.mainLogger.info(model.name+" "+"tree is null!");
	}
	private void expandAllNodes(JTree tree,int startingIndex,int rowCount) {
		for(int i=startingIndex;i<rowCount;++i) {
			tree.expandRow(i);
		}
		if(tree.getRowCount()!=rowCount) {
			expandAllNodes(tree,rowCount,tree.getRowCount());
		}
	}
	// this is what i should do if the user clicks on a move we already have.
	@Override public void valueChanged(TreeSelectionEvent e) {
		DefaultMutableTreeNode node=(DefaultMutableTreeNode)tree.getLastSelectedPathComponent();
		if(node!=null) {
			boolean ok=model.goToMNode((MNode)node.getUserObject());
			if(!ok) Logging.mainLogger.warning("go to node fails!");
		} else Logging.mainLogger.warning("node null for: "+e);
	}
	@Override public void actionPerformed(ActionEvent e) {
		if(e.getActionCommand().equals("Open ...")) {
			GuiFileDialogs.FileSelection selection=GuiFileDialogs.chooseOpenSgf(frame(),lastLoadDirectory);
			if(selection!=null) {
				ModelIo.restore(model,selection.file());
				lastLoadDirectory=selection.directory();
			}
		}
	}
	JTree from(MNode newRoot) {
		DefaultMutableTreeNode top=new DefaultMutableTreeNode(newRoot);
		addChildren(top,newRoot);
		// Create a tree that allows one selection at a time.
		JTree tree=new JTree(top);
		tree.setCellRenderer(new NodeRenderer());
		final Font currentFont=tree.getFont();
		final Font bigFont=new Font(currentFont.getName(),currentFont.getStyle(),currentFont.getSize()+10);
		tree.setFont(bigFont);
		tree.getSelectionModel().setSelectionMode(TreeSelectionModel.SINGLE_TREE_SELECTION);
		// Listen for when the selection changes.
		tree.addTreeSelectionListener(this);
		expandAllNodes(tree,0,tree.getRowCount());
		return tree;
	}
	void addChildren(DefaultMutableTreeNode treeNode,MNode node) {
		if(node!=null) for(MNode child:node.children()) {
			DefaultMutableTreeNode childTreeNode=new DefaultMutableTreeNode(child);
			treeNode.add(childTreeNode);
			addChildren(childTreeNode,child);
		}
		else Logging.mainLogger.info(model.name+" "+"node is null in add children!");
	}
	static final class NodeRenderer extends DefaultTreeCellRenderer {
		@Override public Component getTreeCellRendererComponent(JTree tree,Object value,boolean selected,boolean expanded,
				boolean leaf,int row,boolean hasFocus) {
			Component component=super.getTreeCellRendererComponent(tree,value,selected,expanded,leaf,row,hasFocus);
			if(value instanceof DefaultMutableTreeNode) {
				DefaultMutableTreeNode node=(DefaultMutableTreeNode)value;
				Object userObject=node.getUserObject();
				if(userObject instanceof MNode) {
					int level=node.getLevel()+1;
					setText(userObject.toString()+" "+level);
				}
			}
			return component;
		}
	}
	@Override public void addContent() {
		setMenuBar();
		setLayout(new GridLayout(1,0));
		tree=from(model.root());
		treeView=new JScrollPane(tree);
		Dimension minimumSize=new Dimension(100,50);
		treeView.setMinimumSize(minimumSize);
		int n=getComponentCount();
		Logging.mainLogger.info(model.name+" "+n+" components before first add");
		lastAdded=add(treeView);
		n=getComponentCount();
		Logging.mainLogger.info(model.name+" "+n+" components after first add");
		frame.setResizable(true);
		frame.setSize(new Dimension(640,480));
	}
	void setMenuBar() {
		JMenuBar menuBar=new JMenuBar();
		JMenu menu=new JMenu("File");
		menu.setMnemonic(KeyEvent.VK_F);
		menu.getAccessibleContext().setAccessibleDescription("Open a file");
		menuBar.add(menu);
		JMenuItem menuItem=new JMenuItem("Open ...",KeyEvent.VK_O);
		menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.ALT_MASK));
		menuItem.getAccessibleContext().setAccessibleDescription("This doesn't really do anything");
		menu.add(menuItem);
		menuItem.addActionListener(this);
		frame.setJMenuBar(menuBar);
	}
	public static void main(String[] args) {
		TreeView myTreeView=TreeView.simple();
	}
	public static TreeView simple() {
		Model model=new Model();
		TreeView myTreeView=new TreeView(null,model);
		myTreeView.frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
		model.addObserver(myTreeView);
		ModelIo.restore(model,new File("sgf/ff4_ex.sgf"));
		return myTreeView;
	}
	public final Model model;
	JTree tree;
	JScrollPane treeView;
	Component lastAdded;
	File lastLoadDirectory;
	// File file=new File("Simple.sgf");
}
