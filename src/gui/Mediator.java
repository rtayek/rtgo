package gui;
import java.awt.*;
import java.awt.event.*;
import java.io.File;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import audio.Audio;
import audio.Audio.Sound;
import equipment.Board;
import gui.ButtonsABC.ButtonWithEnum;
import gui.EastPanels.*;
import gui.SouthPanels.*;
import gui.SpinnerOptions.SpinnerOption;
import gui.Spinners.NewSpinners.SpinnersABC.SpinnerWithAnEnum;
import gui.Spinners.OldSpinners;
import gui.TopPanels.*;
import gui.WestPanels.WestPanel;
import io.*;
import model.*;
import model.Event;
import model.Move;
import model.OptionsABC.Option;
import sgf.*;
class Mediator implements Observer,ActionListener {
    // anyway to disconnect this from the model?
    // like for web presentation?
    Mediator(Model model,Main main,TextView textView) {
        System.out.println("start mediator init.");
        if(model==null) {
            model=new Model(); // just for testing?
            //throw new RuntimeException("oops");
        }
        this.model=model;
        //connector=new Connector(model);
        // looks like this is the case where we do not connect!
        // at least right away
        // maybe we can roll up a back end each time
        // and put the connect in the constructor?
        this.main=main;
        this.textView=textView;
        GraphicsDevice gd=GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice();
        int width=gd.getDisplayMode().getWidth();
        int height=gd.getDisplayMode().getHeight();
        boardHeightInPixels=.5*height;
        Logging.mainLogger.info(model.name+" width="+width+", height="+height+", pixels="+boardHeightInPixels);
        JMenuBar jMenuBar=createMenuBar();
        if(main.isApplet()) main.applet().setJMenuBar(jMenuBar);
        else main.frame().setJMenuBar(jMenuBar);
        BorderLayout borderLayout=(BorderLayout)main.getLayout();
        Component old=borderLayout.getLayoutComponent(BorderLayout.PAGE_START);
        // old=null; fixed the left panel problem. but it's probably not the cause
        if(old!=null) { System.out.println("removing: "+old.getName()); main.remove(old); main.validate(); }
        if(useNewTopPanel) {
            topPanel=null;
            newTopPanel=new NewTopPanel(this);
            // these get added here.
            // the oldest parameter stuff gets added the new top panel constructor.
            if(useNewSpinners) {
                if(useSpinnerOptions) {
                    System.out.println("using spinner options.");
                    newTopPanel.spinnerParameterOptions.setValuesInWidgetsFromCurrentValues();
                    for(Option button:newTopPanel.spinnerParameterOptions.options()) {
                        ((SpinnerOption)button).jSpinner.addChangeListener(TopPanel.changeListener);
                        newTopPanel.add(((SpinnerOption)button).jSpinner);
                    }
                } else {
                    System.out.println("using new spinners.");
                    //newTopPanel.spinners.initializeParameters(Parameters.propertiesFilename);
                    newTopPanel.spinners.setValuesInWidgetsFromCurrentValues();
                    // button should be widget or something
                    for(SpinnerWithAnEnum button:newTopPanel.spinners.buttons()) {
                        button.jSpinner.addChangeListener(TopPanel.changeListener);
                        newTopPanel.add(button.jSpinner);
                    }
                }
            } else {
                System.out.println("using old spinners.");
                OldSpinners.staticStValuesInWidgetsFromCurrentValues();
                for(Parameters parameter:OldSpinners.map.keySet()) {
                    OldSpinners spinner=OldSpinners.map.get(parameter);
                    spinner.jSpinner.addChangeListener(TopPanel.changeListener);
                    newTopPanel.add(spinner.jSpinner);
                }
            }
            newTopPanel.setBackground(Color.green);
            main.add(newTopPanel,BorderLayout.PAGE_START);
            newTopPanel.buttons.enableAll(this);
        } else {
            topPanel=new TopPanel(this);
            newTopPanel=null;
            OldSpinners.staticStValuesInWidgetsFromCurrentValues();
            // these get added here.
            // the newer stuff gets added in the mediator
            for(Parameters parameter:OldSpinners.map.keySet()) {
                OldSpinners spinner=OldSpinners.map.get(parameter);
                spinner.jSpinner.addChangeListener(TopPanel.changeListener); // maybe add after inited from parameters?
                topPanel.add(spinner.jSpinner);
            }
            topPanel.setBackground(Color.green);
            main.add(topPanel,BorderLayout.PAGE_START);
            TopPanel.Buttons.enableAll(this);
        }
        if(useNewSouthPanel) {
            newSouthPanel=new NewSouthPanel(this);
            southPanel=null;
            newSouthPanel.setBackground(Color.green);
            main.add(newSouthPanel,BorderLayout.PAGE_END);
            newSouthPanel.buttons.enableAll(this);
        } else {
            newSouthPanel=null;
            southPanel=new SouthPanel(this);
            southPanel.setBackground(Color.green);
            main.add(southPanel,BorderLayout.PAGE_END);
            SouthPanel.Buttons.enableAll(this);
        }
        if(useNwwEastPanel) {
            eastPanel=null;
            newEastPanel=new NewEastPanel(this);
            newEastPanel.setBackground(Color.pink);
            main.add(newEastPanel,BorderLayout.LINE_END);
            newEastPanel.buttons.enableAll(this);
        } else {
            eastPanel=new EastPanel(this);
            newEastPanel=null;
            eastPanel.setBackground(Color.pink);
            main.add(eastPanel,BorderLayout.LINE_END);
            EastPanel.Buttons.enableAll(this);
            //old=borderLayout.getLayoutComponent(BorderLayout.LINE_START);
            //if(old!=null) { mainGui.remove(old); mainGui.validate(); }
        }
        if(!useNewWestPanel) {
            westPanel=new WestPanels.WestPanel(this);
            newWestPanel=null;
            westPanel.setBackground(Color.yellow);
            main.add(westPanel,BorderLayout.LINE_START);
            WestPanel.ConnectButtons.enableAll(this);
            System.out.println("old west panel)");
            //Main.listComponentsIn(westPanel,null,true);
        } else {
            westPanel=null;
            newWestPanel=new WestPanels.NewWestPanel(this);
            newWestPanel.setBackground(Color.yellow);
            main.add(newWestPanel,BorderLayout.LINE_START);
            newWestPanel.buttons.enableAll(this);
            // above is wrong. must be myt enums
            //System.out.println("west panel)");
            //Main.listComponentsIn(westPanel,null,true);
        }
        //status.setBorder(BorderFactory.createLineBorder(Color.black));
        extra=new JPanel(); // near the bottom
        extra.setAlignmentX(Component.CENTER_ALIGNMENT); // fixes alignment problem
        extra.setName("extrta panel");
        //extra.setBorder(BorderFactory.createLineBorder(Color.black));
        extra.setLayout(new BoxLayout(extra,BoxLayout.Y_AXIS));
        extra.add(status);
        status.setName("status");
        //lastMove.setBorder(BorderFactory.createLineBorder(Color.black));
        extra.add(lastMove);
        extra.add(sgfProperties);
        initializeGamePanel(); // in lieu of a splash screen
        // the above is causing problems when the gui is not completely
        // initialized!
        Mediator.this.model.addObserver(this);
        // model.addObserver(new Text(model));
        // model.notify(Event.start,"new mediator");
        if(false) main.addKeyListener(new KeyListener() {
            @Override public void keyTyped(KeyEvent arg0) {}
            @Override public void keyReleased(KeyEvent arg0) {}
            @Override public void keyPressed(KeyEvent arg0) {
                Logging.mainLogger.info(Mediator.this.model.name+" "+this+" key listener "+arg0);
                if(arg0.getKeyCode()==KeyEvent.VK_DELETE) {
                    Logging.mainLogger.info(Mediator.this.model.name+" "+"unmove from keyPressed() in "+this);
                    Mediator.this.model.delete();
                }
            }
        });
        System.out.println("end mediator init.");
    }
    public JMenuBar createMenuBar() {
        JMenuBar menuBar=new JMenuBar();
        JMenu menu=new JMenu("File");
        menu.setMnemonic(KeyEvent.VK_F);
        menu.getAccessibleContext().setAccessibleDescription("File menu");
        menuBar.add(menu);
        JMenuItem menuItem=new JMenuItem("Open ...",KeyEvent.VK_O);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_O,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Open file dialog");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem=new JMenuItem("Save ...",KeyEvent.VK_S);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_S,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Save file dialog");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu=new JMenu("Game");
        menu.setMnemonic(KeyEvent.VK_G);
        menu.getAccessibleContext().setAccessibleDescription("File menu");
        menuBar.add(menu);
        menuItem=new JMenuItem("New Game",KeyEvent.VK_N);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_N,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Start a new game");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu.addSeparator();
        menuItem=new JMenuItem("Pass",KeyEvent.VK_P);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_P,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Pass");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu.addSeparator();
        menuItem=new JMenuItem("Resign",KeyEvent.VK_R);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_R,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Resign");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu=new JMenu("View");
        menu.setMnemonic(KeyEvent.VK_V);
        menu.getAccessibleContext().setAccessibleDescription("View menu");
        menuBar.add(menu);
        menuItem=new JMenuItem("Tree view",KeyEvent.VK_T);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_T,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Tree view");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuItem=new JMenuItem("Console view",KeyEvent.VK_C);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_C,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("Console view");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menu=new JMenu("Help");
        menu.setMnemonic(KeyEvent.VK_H);
        menu.getAccessibleContext().setAccessibleDescription("Help menu");
        menuItem=new JMenuItem("About",KeyEvent.VK_A);
        menuItem.setAccelerator(KeyStroke.getKeyStroke(KeyEvent.VK_A,ActionEvent.ALT_MASK));
        menuItem.getAccessibleContext().setAccessibleDescription("About");
        menuItem.addActionListener(this);
        menu.add(menuItem);
        menuBar.add(menu);
        return menuBar;
    }
    @Override public void actionPerformed(ActionEvent e) {
        Logging.mainLogger.info(model.name+" "+"action performed: "+e);
        if(e.getActionCommand().equals("Open ...")) {
            try {
                JFileChooser fileChoser=new JFileChooser(lastLoadDirectory!=null?lastLoadDirectory:new File("."));
                fileChoser.setFileFilter(new FileNameExtensionFilter("SGF file","sgf"));
                if(fileChoser.showOpenDialog(null)==JFileChooser.APPROVE_OPTION) {
                    File file=fileChoser.getSelectedFile();
                    model.restore(IO.toReader(file));
                    // maybe here is where i can check for my root node?
                    // it would be in the first child.
                    String comment="from: "+file;
                    MNode root=model.root();
                    List<String> list=Arrays.asList(new String[] {comment});
                    SgfProperty property=new SgfProperty(P.C,list);
                    root.properties.add(0,property);
                    lastLoadDirectory=file.getParentFile();
                    lastOpenFile=file;
                }
            } catch(Exception ex) {
                ex.printStackTrace();
                Toast.toast(ex.toString());
            }
        } else if(e.getActionCommand().equals("Save ...")) {
            try {
                JFileChooser fileChoser=new JFileChooser(lastSaveDirectory!=null?lastSaveDirectory:new File("."));
                fileChoser.setFileFilter(new FileNameExtensionFilter("SGF file","sgf"));
                // fileChoser.s
                if(fileChoser.showSaveDialog(null)==JFileChooser.APPROVE_OPTION) {
                    File file=fileChoser.getSelectedFile();
                    //file=Model.insureExtension(file,Model.desiredExtension);
                    if(!model.save(IO.toWriter(file)))
                        Logging.mainLogger.warning(model.name+" "+"can not save to: "+file);
                    lastSaveDirectory=file.getParentFile();
                }
            } catch(Exception ex) {
                ex.printStackTrace();
                Toast.toast(ex.toString());
            }
        } else if(e.getActionCommand().equals("New Game")) {
            // get values from spinners
            // no, that is what we used to do
            // now let's set them in parameters
            // and get the values from them
            // sort of
            // since we are loading parameters,
            // we should set the spinners from the parameters.
            // 7/15/21 spinners are set in initialization.
            model.setRootFromParameters();
            Audio.play(Sound.challenge);
        } else if(e.getActionCommand().equals("Pass")) model.move(Move.blackPass);
        // maybe need to check turn here?
        // yes, the are both for black.
        // but pass() and resign() use turn().
        else if(e.getActionCommand().equals("Resign")) model.move(Move.blackResign);
        // and here also?
        else if(e.getActionCommand().equals("Tree view")) {
            if(myTreeView==null) {
                myTreeView=new TreeView(null,model); // try old to see why we did this?
                // old way seems to work.
                // new way does not find ancestors!
                model.addObserver(myTreeView);
            } else {
                model.deleteObserver(myTreeView);
                myTreeView.frame.dispose();
                myTreeView=null;
            }
            // try to fix unselected root
            model.setChangedAndNotify(Event.newTree);
        } else if(e.getActionCommand().equals("Console view")) {
            if(textView!=null) textView.frame.setVisible(!textView.frame.isVisible());
        } else if(e.getActionCommand().equals("About")) Toast.toast("Topological GO (alpha)");
    }
    @Override public void update(Observable observable,Object hint) {
        Logging.mainLogger.fine(model.name+" "+observable.getClass().getName()+", hint: "+hint);
        if(observable instanceof Model) {
            Model model=(Model)observable;
            if(model==this.model) {
                Logging.mainLogger.fine(model.name+" "+"hint: "+hint);
                if(model.board()!=null) initializeGamePanel(); // gross overkill
                else Logging.mainLogger.fine(model.name+" "+"board is null, skipping initializeGamePanel()");
                if(hint instanceof Event||hint instanceof Event.Hint) {
                    Event event=null;
                    if(hint instanceof Event) event=(Event)hint;
                    else if(hint instanceof Event.Hint) { Event.Hint h=(Event.Hint)hint; event=h.event; }
                    switch(event) {
                        case newTree:
                        case nodeChanged:
                        case illegalMove:
                            if(!useNewTopPanel) TopPanel.Buttons.enableAll(this);
                            else newTopPanel.buttons.enableAll(this);
                            // this will all need to be changed.
                            if(!useNewSouthPanel) {
                                SouthPanel.Buttons.scroll.abstractButton.setEnabled(false);
                                SouthPanel.Buttons.scroll.abstractButton.setSelected(false);
                                SouthPanel.Buttons.enableAll(this);
                            } else {
                                ButtonWithEnum scroll=newSouthPanel.buttons.get(SouthPanels.MyEnums.scroll);
                                scroll.abstractButton.setEnabled(false);
                                scroll.abstractButton.setSelected(useNewSouthPanel);
                                newSouthPanel.buttons.enableAll(this);
                            }
                            break;
                        default:
                            Logging.mainLogger.warning(model.name+" "+"unhandled event hint: "+hint);
                    }
                } else Logging.mainLogger.fine(model.name+" "+"hint="+hint);
                if(useNewWestPanel) {
                    //newWestPanel.setActionMap(l); // this may be what i am trying to do with buttons with an enum?
                    newWestPanel.setPlayerColor();
                } else westPanel.setPlayerColor();
                if(gamePanel!=null) {
                    Logging.mainLogger.fine(model.name+" "+"request repaint");
                    gamePanel.repaint();
                } else Logging.mainLogger.warning(model.name+" "+"no game panel to update");
            } else throw new RuntimeException("not our model!");
        } else throw new RuntimeException("not a model!");
    }
    private void addGamePanels() {
        JPanel panel=new JPanel();
        panel.setName("game panel container");
        panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
        gamePanel=new GamePanel(boardHeightInPixels,Mediator.this);
        gamePanel.setName("game panel");
        panel.add(gamePanel);
        panel.add(extra);
        main.add(panel/*gamePanel*/,BorderLayout.CENTER);
        if(!main.isApplet()) main.frame().pack();
        else; // https://community.oracle.com/thread/1294964
    }
    void initializeGamePanel() { // what is this doing?
        boolean tryNew=true;
        if(tryNew) {
            if(gamePanel==null) addGamePanels();
            Board b=model.board();
            if(b==null) model.setBoard(b=Board.factory.create());
            Logging.mainLogger.fine("board is null: "+(b==null));
            Logging.mainLogger.fine(model.name+" "+b.depth());
            Logging.mainLogger.fine(model.name+" "+b.id());
            int id=model.board().id();
            Logging.mainLogger
            .info(model.name+" "+"re initializing game panel with board "+id+" "+model.board().topology());
            if(true) SwingUtilities.invokeLater(new Runnable() {
                @Override public void run() {
                    gamePanel.initialize(boardHeightInPixels);
                    main.validate();
                    if(!main.isApplet()) main.frame().pack();
                    else; // https://community.oracle.com/thread/1294964
                    Logging.mainLogger.fine(model.name+" "+"requesting repaint");
                    gamePanel.repaint();
                }
            });
            else {
                gamePanel.initialize(boardHeightInPixels);
                main.validate();
                if(!main.isApplet()) main.frame().pack();
                else; // https://community.oracle.com/thread/1294964
                Logging.mainLogger.fine(model.name+" "+"requesting repaint");
                gamePanel.repaint();
            }
        } else {
            Logging.mainLogger.info(model.name+" "+"replacing game panel");
            if(gamePanel!=null) { System.out.println("removing: "+gamePanel.getName()); main.remove(gamePanel); }
            addGamePanels();
            main.validate();
            if(!main.isApplet()) main.frame().pack();
            else; // https://community.oracle.com/thread/1294964
            gamePanel.repaint();
        }
    }
    public static void main(String[] args) { //
    }
    final Main main;
    final Model model;
    TreeView myTreeView;
    final TextView textView;
    GamePanel gamePanel;
    final JPanel extra; // maybe does not need to be a field.
    final JLabel status=new JLabel(" ");
    final JLabel lastMove=new JLabel(" ");
    //final JLabel sgfProperties=new JLabel(" ");
    final JTextArea sgfProperties=new JTextArea();
    final TopPanels.TopPanel topPanel;
    final TopPanels.NewTopPanel newTopPanel;
    final SouthPanels.SouthPanel southPanel;
    final SouthPanels.NewSouthPanel newSouthPanel;
    final EastPanels.EastPanel eastPanel;
    final EastPanels.NewEastPanel newEastPanel;
    final WestPanels.WestPanel westPanel;
    final WestPanels.NewWestPanel newWestPanel;
    static boolean useSpinnerOptions=true;
    static boolean useNewSpinners=true;
    static boolean useNewTopPanel=true;
    static boolean useNewSouthPanel=true;
    static boolean useNewWestPanel=true;
    static boolean useNwwEastPanel=true;
    double boardHeightInPixels=18*40*3/4.;
    //
    transient File lastLoadDirectory,lastSaveDirectory,lastOpenFile;
}
