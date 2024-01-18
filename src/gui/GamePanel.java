package gui;
import java.awt.*;
import java.awt.Dialog.*;
import java.awt.datatransfer.Clipboard;
import java.awt.event.*;
import java.awt.font.TextAttribute;
import java.awt.geom.Point2D;
import java.awt.im.InputMethodHighlight;
import java.awt.image.*;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import javax.swing.*;
import audio.Audio;
import audio.Audio.Sound;
import equipment.*;
import equipment.Board.Topology;
import equipment.Point;
import io.Logging;
import model.Model;
import model.Model.*;
import sgf.*;
class Line {
    Line(Point p1,Point p2,double width) { this.p1=p1; this.p2=p2; this.width=width; }
    public void paint(Graphics g) {
        // Graphics2D g2=(Graphics2D)g;
        g.drawLine(p1.x,p1.y,p2.x,p2.y);
    }
    final Point p1,p2;
    final double width;
    static final double lineThickness=1/454.5;
}
// mm inch
// Board width 424.2 16 23/32 1.4
// Board length 454.5 17 29/32 1.5
// Board thickness 151.5 5 31/32 0.
// Line spacing width-wise 22 7/8 7.26
// Line spacing length-wise 23.7 15/16 7.82
// Line thickness 1 1/32 0.3
// Star point marker diameter 4 5/32 1.2
// Stone diameter 22.5 29/32 7.5
@SuppressWarnings("serial") class SgfNodePanel extends JPanel { // not used yet
    SgfNodePanel(Mediator mediator) { this.mediator=mediator; }
    @Override public void paintComponent(Graphics g) { super.paintComponent(g); g.drawString("some board",10,10); }
    final Mediator mediator;
}
class MyShowingListener implements HierarchyListener {
    private JComponent component;
    public MyShowingListener(JComponent jc) { component=jc; }
    @Override public void hierarchyChanged(HierarchyEvent e) {
        if((e.getChangeFlags()&HierarchyEvent.SHOWING_CHANGED)>0&&component.isShowing()) {
            System.out.println("hierarchy changed");
            Logging.mainLogger.config("Showing: "+e.paramString());
        }
    }
}
public class GamePanel extends JPanel {
    GamePanel(double boardHeightInPixels,Mediator mediator) {
        System.out.println("wnter game panel constructor");
        // need to make this so i don't have to roll up a new one every time a
        // move is made.
        // maybe just put the code below into an init() method?
        this.mediator=mediator;
        Logging.mainLogger.info("line width in pixels: "+boardHeightInPixels*Line.lineThickness);
        initialize(boardHeightInPixels);
        addMouseListener(new MouseListener() {
            @Override public void mouseReleased(MouseEvent e) { releases++; }
            @Override public void mousePressed(MouseEvent e) { presses++; }
            @Override public void mouseExited(MouseEvent e) { setCursor(cursor); }
            @Override public void mouseEntered(MouseEvent e) { cursor=getCursor(); setCursor(blackCursor); }
            @Override public void mouseClicked(MouseEvent e) { clicks++; processClick(e); }
            @SuppressWarnings("unused") int presses,releases,clicks;
        });
        addKeyListener(new KeyListener() {
            @Override public void keyTyped(KeyEvent arg0) {}
            @Override public void keyReleased(KeyEvent arg0) {}
            @Override public void keyPressed(KeyEvent arg0) {
                Logging.mainLogger.info(mediator.model.name+" "+this+" key listener "+arg0);
                if(arg0.getKeyCode()==KeyEvent.VK_DELETE) { mediator.model.delete(); }
            }
        });
        if(false) KeyboardFocusManager.getCurrentKeyboardFocusManager().addKeyEventDispatcher(new KeyEventDispatcher() {
            @Override public boolean dispatchKeyEvent(KeyEvent arg0) {
                if(arg0.getID()==KeyEvent.KEY_PRESSED) switch(arg0.getKeyCode()) {
                    case KeyEvent.VK_DELETE:
                        Logging.mainLogger.info(mediator.model.name+" "+"delete from dispatchKeyEvent in "+this);
                        // Model.mumble("unmove from dispatchKeyEvent in
                        // "+this);
                        // Move move=mediator.model.unmove();
                        return true;
                    default:
                        Toast.toast("key is umimplemented: "+arg0);
                        return true;
                }
                else return true;
            }
        });
        addHierarchyListener(new MyShowingListener(this));
        System.out.println("end game panel init");
    }
    public void cursor(Stone who) {
        switch(who) {
            case black: // should be black stone
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                break;
            case white: // should be white stone
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                break;
            case edge: // should be x
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                break;
            case vacant: // should be x
                setCursor(new Cursor(Cursor.HAND_CURSOR));
                break;
        }
    }
    private MNode isAlreadyInTree(Point closest) {
        MNode found=null;
        MNode node=mediator.model.currentNode();
        String target=Coordinates.toSgfCoordinates(closest,board.depth());
        for(MNode child:node.children) for(SgfProperty property:child.sgfProperties)
            if(mediator.model.turn().equals(Stone.black)&&property.p().equals(P.B)
                    ||mediator.model.turn().equals(Stone.white)&&property.p().equals(P.W)) {
                        System.out.println("found child move property: "+property);
                        if(property.list().get(0).equals(target)) { // assume
                            // first
                            // and
                            // only?
                            System.out.println("found move! "+property);
                            found=child;
                            break;
                        }
                    }
        return found;
    }
    void processClick(MouseEvent e) {
        long t0=System.nanoTime();
        if(SwingUtilities.isLeftMouseButton(e)) {
            if(board==null) { Logging.mainLogger.info(mediator.model.name+" "+"ignoring left click"); return; }
            Point2D.Float point=toBoardCoordinates(new Point(e.getPoint()),board.depth());
            Logging.mainLogger.info(mediator.model.name+" "+"click="+point);
            Point closest=closest(point);
            Model.Where where=Model.Where.isPoint(board,mediator.model.band(),point,closest);
            Model.Role role=mediator.model.role();
            Stone playerColor=null;
            // if(mode.equals(Model.Mode.playBlack)) playerColor=Stone.black;
            // else if(mode.equals(Model.Mode.playWhite))
            // playerColor=Stone.white;
            // if(mode.equals(Model.Mode.anything)||playerColor.equals(mediator.model.turn()))
            // {
            // lets check to see if we already have this move as a child.
            MNode found=isAlreadyInTree(closest);
            if(found!=null) {
                Logging.mainLogger.info(mediator.model.name+" "+"found child node, goig to it.");
                boolean ok=mediator.model.goToMNode(found);
                if(!ok) System.out.println("go to node fails!");
            } else {
                // String move=Coordinates.toGtpCoordinateSystem(closest,
                // board.depth());
                // Model.Move move=new Model.MoveImpl(closest);
                // need to check role and legality here!
                boolean ok=mediator.model.checkAction(role,What.move);
                if(!ok) { System.out.println("not ok: "+role+" move"); Toast.toast("Move is no ok!"); }
                MoveResult wasLegal=mediator.model.moveAndPlaySound(mediator.model.turn(),closest);
                if(wasLegal==MoveResult.legal&&mediator.model.inAtari()) Audio.play(Sound.atari);
            }
            // } else {
            // Logging.logger.info(mediator.model.name+" "+"it's not your
            // turn!");
            // }
        } else if(SwingUtilities.isRightMouseButton(e)) {
            Point2D.Float point=toBoardCoordinates(new Point(e.getPoint()),board.depth());
            Point closest=closest(point);
            if(board.isOnBoard(closest)) {
                double distance=point.distance(closest);
                if(distance<Model.epsilon) { // see what he clicked on
                    Stone stone=board.at(closest.x,closest.y);
                    Block block=Block.find(board,closest);
                    Logging.mainLogger.info(mediator.model.name+" "+"you clicked on: "+block);
                    if(stone.equals(Stone.black)||stone.equals(Stone.white)) Toast.toast("you clicked on: "+block);
                    else Toast.toast("you clicked on a vacant or non board square: "+closest);
                    // BlockImpl g=new BlockImpl(b,i,j,processed);
                } else Logging.mainLogger.info(mediator.model.name+" "+"not close enough");
            } else Logging.mainLogger.info(mediator.model.name+" "+"off the board");
        } else if(SwingUtilities.isMiddleMouseButton(e)) {
            Logging.mainLogger.info(mediator.model.name+" "+"middle mouse button");
        }
        long dt=System.nanoTime()-t0;
        Logging.mainLogger.info("dt="+dt/1_000_000.+" ms.");
    }
    public static Point closest(Point2D.Float point) {
        Point closest=new Point(Math.round(point.x),Math.round(point.y));
        return closest;
    }
    private double line(Point p1,Point p2,double x) { return p1.y+(p2.y-p1.y)/(p2.x-p1.x)*x; }
    private void addLine(int x1,int y1,int x2,int y2) {
        lines.add(new Line(new Point(x1,y1),new Point(x2,y2),lineWidth));
    }
    private void addLinesForRotatedBoard(final int x0,final int y0) {
        // draw diagonal board
        Point pNorth=new Point(x0+dx*(board.width()-1)/2,y0);
        Point pSouth=new Point(x0+dx*(board.width()-1)/2,y0+dy*(board.depth()-1));
        Point pEast=new Point(x0+dx*(board.width()-1),y0+dy*(board.depth()-1)/2);
        Point pWest=new Point(x0,y0+dy*(board.depth()-1)/2);
        lines.add(new Line(pNorth,pEast,lineWidth));
        lines.add(new Line(pEast,pSouth,lineWidth));
        lines.add(new Line(pSouth,pWest,lineWidth));
        lines.add(new Line(pWest,pNorth,lineWidth));
        double x1=pNorth.x,y1=pNorth.y;
        double x2=pEast.x,y2=pEast.y;
        Function<Double,Double> l1=x->y1+(y2-y1)/(x2-x1)*x;
        for(int y=0;y<board.depth();y++) {
            int half=board.width()/2;
            int indent=half-y;
            int z2=half+y; // width-y? even with an even width? i.e. width=18
            if(y>half) { indent=-indent; z2=board.width()-1-indent; }
            addLine(x0+indent*dx,y0+y*dy,x0+z2*dx,y0+y*dy);
        }
        for(int x=0;x<board.width();x++) {
            int half=board.depth()/2;
            int indent=half-x;
            int z2=half+x; // width-y? even with an even width? i.e. width=18
            if(x>half) { indent=-indent; z2=board.width()-1-indent; }
            addLine(x0+x*dx,y0+indent*dy,x0+x*dx,y0+z2*dy);
        }
    }
    private void normalLines(final int x0,final int y0) {
        for(int y=0;y<board.depth();y++) addLine(x0,y0+y*dy,x0+(board.width()-1)*dx,y0+y*dy);
        for(int x=0;x<board.width();x++) addLine(x0+x*dx,y0,x0+x*dx,y0+(board.depth()-1)*dy);
    }
    void initialize(double boardHeightInPixels) {
        if(mediator==null) {
            System.out.println("game panel initialize board is null!");
            Logging.mainLogger.warning("mediator is null!");
            return;
        }
        board=mediator.model.board(); // maybe the board should be a parameter?
        if(board==null) {
            System.out.println("game panel initialize board is null!");
            Logging.mainLogger.warning("board is null!");
            return;
        }
        Logging.mainLogger.config("board type is: "+board.topology()+", band: "+mediator.model.band());
        lines.clear();
        // fix this so it paints in a sane manner if the board is null!
        boardDepth=(int)Math.round(boardHeightInPixels);
        boardWidth=(int)Math.round(boardHeightInPixels*aspectRatio);
        lineWidth=boardDepth*Line.lineThickness;
        jitter=Jitter.get(board.width(),board.depth());
        int n=Math.max(board.width(),board.depth());
        int dx_=boardWidth/(n-1);
        int dy_=boardDepth/(n-1);
        // Model.mumble("(dx,dy)=("+dx_+","+dy_+")");
        if(board.topology()==Topology.torus) {
            dx_=(int)Math.round(torusAmount*dx_);
            dy_=(int)Math.round(torusAmount*dy_);
        }
        // Model.mumble("(dx,dy)=("+dx_+","+dy_+")");
        dx=dx_;
        dy=dy_;
        Logging.mainLogger.info("dx: "+dx+", dy: "+dy);
        int x0_=dx*(1+(n-board.width())/2);
        int y0_=dy*(1+(n-board.depth())/2);
        // Model.mumble("(x0,y0)=("+x0_+","+y0_+")");
        if(board.topology()==Topology.torus) { // some problem here with band=7 and
            // width=depth=11
            x0_+=(1-torusAmount)*boardWidth/2;
            y0_+=(1-torusAmount)*boardDepth/2;
            // x0_+=2*dx;
            // y0_+=2*dy;
        }
        // band=board.type().equals(Board.Type.torus)?(int)ControlPanel.Spinners.bands.jSpinner.getModel().getValue():0;
        // Model.mumble("(x0,y0)=("+x0_+","+y0_+")");
        final int x0=x0_;
        final int y0=y0_;
        pUpperLeft=new Point(x0,y0);
        pLowerLeft=new Point(x0,y0+dy*(board.depth()-1));
        pUpperRight=new Point(x0+dx*(board.width()-1),y0);
        pLowerRight=new Point(x0+dx*(board.width()-1),y0+dy*(board.depth()-1));
        dp=new Point(dx,dy);
        switch(board.topology()) {
            case torus:
                int band=mediator.model.band();
                for(int y=-band;y<board.depth()+band;y++)
                    addLine(x0-band*dx,y0+y*dy,x0+(board.width()-1+band)*dx,y0+y*dy);
                for(int x=-band;x<board.width()+band;x++)
                    addLine(x0+x*dx,y0-band*dy,x0+x*dx,y0+(board.depth()-1+band)*dy);
                break;
            case horizontalCylinder:
            case verticalCylinder:
            case normal:
                normalLines(x0,y0);
                break;
            case diamond:
                addLinesForRotatedBoard(x0,y0);
                break;
            default:
                throw new RuntimeException("bad ropology");
            // break;
        }
        black=blackStone(dx,dy,getBackground());
        System.out.println("blac: "+black);
        white=whiteStone(dx,dy,getBackground());
        edge=edgeStone(dx,dy,getBackground());
        Dimension d=new Dimension(boardWidth+2*dx,boardDepth+2*dy);
        boolean setSize=true;
        if(setSize) {
            setSize(d);
            setPreferredSize(d); // this is the one that breaks when it is
            // too big!
            setMaximumSize(d);
            setMinimumSize(d);
        }
        // setOpaque(true);
        Color color=new Color(0xffa500);
        setBackground(color);
        Point point=new Point();
        Toolkit toolkit=Toolkit.getDefaultToolkit();
        System.out.println(black);
        if(black!=null) blackCursor=toolkit.createCustomCursor(black,point,"black");
        else System.err.println("black stone imahe is null!");
        if(white!=null) whiteCursor=toolkit.createCustomCursor(white,point,"white");
        else System.err.println("white stone imahe is null!");
    }
    Point2D.Float toBoardCoordinates(Point screen,int depth) {
        return Coordinates.toBoardCoordinates(screen,pUpperLeft,dp,depth);
    }
    Point toScreenCoordinates(Point board,int depth) {
        return Coordinates.toScreenCoordinates(board,pUpperLeft,dp,depth);
    }
    private Image edgeStone(int width,int height,Color color) {
        width=width+1; // should be a little larger?
        height=height+1; // should be a little larger?
        Image img=createImage(width,height);
        if(img==null) return img;
        Graphics g=img.getGraphics();
        g.setColor(color);
        g.fillRect(0,0,width,height);
        g.setColor(getBackground()/* Color.yellow */);
        g.fillOval(0,0,width-1,height-1);
        g.drawOval(0,0,width-1,height-1);
        // these two are different from
        // either g.fillOval(-1, -1, width+1, height+1);
        // or g.fillOval(0, 0, width-1, height-1);
        return img;
    }
    private Image blackStone(int width,int height,Color color) {
        width=width+1; // should be a little larger?
        height=height+1; // should be a little larger?
        Image img=createImage(width,height);
        if(img==null) { Logging.mainLogger.info(mediator.model.name+" "+"img is null"); return img; }
        Graphics g=img.getGraphics();
        g.setColor(color);
        g.fillRect(0,0,width,height);
        g.setColor(Color.black);
        g.fillOval(0,0,width-1,height-1);
        g.drawOval(0,0,width-1,height-1);
        // these two are different from
        // either g.fillOval(-1, -1, width+1, height+1);
        // or g.fillOval(0, 0, width-1, height-1);
        g.setColor(Color.white);
        g.drawArc(width/5,height/5,width*3/5,height*3/5,-20,-60);
        return img;
    }
    private Image whiteStone(int width,int height,Color color) {
        Image img=createImage(width,height);
        if(img==null) return img;
        Graphics g=img.getGraphics();
        g.setColor(color);
        g.fillRect(0,0,width,height);
        g.setColor(Color.white);
        g.fillOval(0,0,width-1,height-1);
        g.setColor(Color.black);
        g.drawOval(0,0,width-1,height-1);
        g.drawArc(width/5,height/5,width*3/5,height*3/5,-20,-60);
        return img;
    }
    private void paintMoveNumber(Graphics g,Point screen) {
        Point p=new Point(screen);
        String string=""+""+mediator.model.moves();
        int n=string.length();
        p.x+=-n*(dx/6);
        p.y+=dy/6;
        g.drawString(string,p.x,p.y);
    }
    private void paintStone_(Graphics g,Stone stone,int index,int atX,int atY,boolean paintMoveNumber) {
        // paint stone from (x,y) on board at (atX,atY) on screen
        Point screen=toScreenCoordinates(new Point(atX,atY),board.depth());
        Color old=g.getColor();
        int extra=(int)Math.round(dx*1.1);
        extra=1;
        int jX=jitter.xJitter(index),jY=jitter.yJitter(index);
        try {
            switch(stone) {
                case black: // no wonder it's null the first time.
                    if(black!=null) g.drawImage(black,screen.x-dx/2+jX,screen.y-dy/2+jY,dx+extra,dy+extra,null);
                    else {
                        g.setColor(Color.black);
                        g.fillOval(screen.x-dx/2+jX,screen.y-dy/2+jY,dx+extra,dy+extra);
                    }
                    if(paintMoveNumber) { g.setColor(Color.white); paintMoveNumber(g,screen); }
                    break;
                case white:
                    if(white!=null) g.drawImage(white,screen.x-dx/2+jX,screen.y-dy/2+jY,dx+extra,dy+extra,null);
                    else {
                        g.setColor(Color.white);
                        g.fillOval(screen.x-dx/2+jX,screen.y-dy/2+jY,dx+extra,dy+extra);
                    }
                    if(paintMoveNumber) { g.setColor(Color.black); paintMoveNumber(g,screen); }
                    break;
                case vacant: // not sure we ever get here
                    break;
                case edge:
                    g.setColor(getBackground());
                    if(edge!=null) {
                        g.drawImage(edge,screen.x-dx/2,screen.y-dy/2,dx,dy,null);
                    } else g.fillOval(screen.x-dx/2,screen.y-dy/2,dx,dx);
                    g.fillRect(screen.x-dx/4,screen.y-dy+1,dx/2,dy/2);
                    g.fillRect(screen.x-dx/4,screen.y+dy/2-1,dx/2,dy/2);
                    g.fillRect(screen.x-dx+1,screen.y-dy/4,dx/2,dy/2);
                    g.fillRect(screen.x+dx/2-1,screen.y-dy/4,dx/2,dy/2);
                    break;
                default:
                    break;
            }
        } catch(ArrayIndexOutOfBoundsException e) {
            Logging.mainLogger.info(mediator.model.name+" "+"throws "+e);
        } finally {
            g.setColor(old);
        }
    }
    private void drawCircleByCenter(Graphics g,Point point,int radius,Color color) {
        Color old=g.getColor();
        g.setColor(color);
        g.drawOval(point.x-radius,point.y-radius,2*radius,2*radius);
        g.setColor(old);
    }
    private void paintBandStone(Graphics g,Stone stone,int index,int atX,int atY,boolean paintMoveNumber) {
        if(board.isOnBoard(atX,atY)) {
            Logging.mainLogger.info(mediator.model.name+" "+"band stone is on board!");
            return;
        }
        if(!Model.Where.inABand(board,mediator.model.band(),atX,atY)) {
            Logging.mainLogger.info(mediator.model.name+" "+"stone at: "+atX+","+atY+" is not in a band!");
            return;
        }
        paintStone_(g,stone,index,atX,atY,paintMoveNumber);
        // Point point=new Point(atX,atY);
        // Point screen=toScreenCoordinates2(point,board.depth());
        // drawCircleByCenter(g,screen,(dx+dx/2)/2,Color.red);
        // g.drawRect(screen.x-dx/2,screen.y-dy/2,10,10);
    }
    private void paintBand(Graphics g,int x,int y,Stone stone,boolean paintMoveNumber) {
        int band=mediator.model.band();
        int index=board.index(x,y);
        if(0<=y&&y<band) {
            paintBandStone(g,stone,index,x,y+board.depth(),paintMoveNumber);
            if(0<=x&&x<band) paintBandStone(g,stone,index,x+board.width(),y+board.depth(),paintMoveNumber);
            if(board.width()-band<=x&&x<board.width())
                paintBandStone(g,stone,index,x-board.width(),y+board.depth(),paintMoveNumber);
        }
        if(board.depth()-band<=y&&y<board.depth()) {
            paintBandStone(g,stone,index,x,y-board.depth(),paintMoveNumber);
            if(0<=x&&x<band) paintBandStone(g,stone,index,x+board.width(),y-board.depth(),paintMoveNumber);
            if(board.width()-band<=x&&x<board.width())
                paintBandStone(g,stone,index,x-board.width(),y-board.depth(),paintMoveNumber);
        }
        if(0<=x&&x<band) paintBandStone(g,stone,index,x+board.width(),y,paintMoveNumber);
        if(board.width()-band<=x&&x<board.width()) paintBandStone(g,stone,index,x-board.width(),y,paintMoveNumber);
    }
    private void paintStone(Graphics g,int x,int y) {
        int xO=mediator.model.offsetX(x),yO=mediator.model.offsetY(y);
        xO=board.moduloWidth(xO);
        yO=board.moduloDepth(yO);
        Stone stone=board.at(x,y);
        if(!stone.equals(Stone.vacant)) {
            int index=board.index(x,y);
            // look at the infos from these guys later.
            paintStone_(g,stone,index,xO,yO,new Point(x,y).equals(mediator.model.lastMoveGTP()));
            if(board.topology().equals(Topology.torus))
                paintBand(g,xO,yO,stone,new Point(x,y).equals(mediator.model.lastMoveGTP()));
        }
    }
    @Override public void paintComponent(Graphics g) {
        super.paintComponent(g);
        if(board==null) {
            System.out.println("board is null in paint component.");
            Logging.mainLogger.warning("board is null!");
            g.drawString("no board",10,10);
        } else {
            System.out.println("board is not null in paint component.");
            Color color=g.getColor();
            for(Line line:lines) line.paint(g);
            if(board.starPoints()!=null) for(int i=0;i<board.starPoints().size();i++) {
                Point point=board.starPoints().get(i);
                if(!board.at(point).equals(Stone.edge))
                    g.fillOval(pUpperLeft.x+point.x*dx-dx-dx/8,pUpperLeft.y+point.y*dy-dy-dy/8,dx/4,dy/4);
            }
            // edge stones need to be painted first
            for(int y=0;y<board.depth();y++)
                for(int x=0;x<board.width();x++) if(board.at(x,y).equals(Stone.edge)) paintStone(g,x,y);
            for(int y=0;y<board.depth();y++)
                for(int x=0;x<board.width();x++) if(!board.at(x,y).equals(Stone.edge)) paintStone(g,x,y);
            Graphics2D g2=(Graphics2D)g;
            if(board.topology().equals(Topology.torus)) {
                Stroke old=g2.getStroke();
                g2.setStroke(new BasicStroke(4));
                g.setColor(Color.pink);
                g2.drawRect(pUpperLeft.x-dx/2,pUpperLeft.y-dy/2,board.width()*dx,board.depth()*dy);
                g2.setStroke(old);
            }
            g.setColor(color);
            int x=pLowerLeft.x,y=pLowerLeft.y;
            x+=dx;
            y+=dy/2;
            if(board.topology().equals(Topology.torus)) { y+=mediator.model.band()*dy+dy/2; x-=5*dx; }
            drawCoordinates(g);
            if(true) {
                String string=""+mediator.model.prisoners(Stone.black)+" captured black stones"+", "
                        +mediator.model.prisoners(Stone.white)+" captured white stones"+", "+mediator.model.komi()
                        +" komi"+", "+mediator.model.moves()+" moves.";
                mediator.status.setText(string);
                mediator.lastMove.setText("last move: "+mediator.model.lastMove()+"\\nfoo\\nbar");
                MNode currentNode=mediator.model.currentNode();
                if(currentNode!=null) {
                    String properties=currentNode.toString();
                    properties+="\nfoo\nbar";
                    mediator.sgfProperties.setText(properties);
                } else mediator.sgfProperties.setText("current node is null!");
            } else drawStatus(g,x,y);
        }
    }
    private void drawCoordinates(Graphics g) {
        double fontSize=.5*dy*Toolkit.getDefaultToolkit().getScreenResolution()/72.0;
        Font old=g.getFont();
        Font font=new Font(Font.SERIF,Font.BOLD,(int)(fontSize+1)/2);
        g.setFont(font);
        FontMetrics fm=g.getFontMetrics(font);
        int lineHeight=fm.getHeight();
        int band=mediator.model.band(); // can this change????
        int bandDx=board.topology().equals(Topology.torus)?(dx*band):0;
        int bandDy=board.topology().equals(Topology.torus)?(dy*band):0;
        int xPad=dx/4;
        for(int x=0;x<board.width();x++) {
            Point point=new Point(x,0);
            String gtp=Coordinates.toGtpCoordinateSystem(point,board.width(),board.depth());
            String letter=gtp.substring(0,1);
            if(board.topology().equals(Topology.torus)) if(letter.charAt(0)>'I') letter=""+(char)(letter.charAt(0)-1);
            // is the above correct?
            int sw=fm.stringWidth(letter); // string width
            g.drawString(letter,pUpperLeft.x+x*dx-(sw+1)/2,pUpperLeft.y-bandDy-fm.getDescent());
            g.drawString(letter,pUpperLeft.x+x*dx-(sw+1)/2,pUpperLeft.y+fm.getAscent()+(board.depth()-1)*dy+bandDy);
        }
        for(int y=0;y<board.depth();y++) {
            Point point=new Point(0,y);
            String gtp=Coordinates.toGtpCoordinateSystem(point,board.width(),board.depth());
            String number=gtp.substring(1);
            int sw=fm.stringWidth(number); // string width
            g.drawString(number,pLowerLeft.x-sw-bandDx-xPad,pLowerLeft.y-y*dy+lineHeight/4);
            g.drawString(number,pLowerLeft.x+bandDx+(board.width()-1)*dx+xPad,pLowerLeft.y-y*dy+lineHeight/4);
        }
        g.setFont(old);
    }
    private void drawStatus(Graphics g,int x,int y) {
        Font old=g.getFont();
        g.setFont(new Font("TimesRoman",Font.PLAIN,20));
        // make this a status line in the model?
        String string=""+mediator.model.prisoners(Stone.black)+" captured black stones"+", "
                +mediator.model.prisoners(Stone.white)+" captured white stones"+", "+mediator.model.komi()+" komi"+", "
                +mediator.model.moves()+" moves.";
        g.drawString(string,x,y);
        g.setFont(old);
    }
    private final Mediator mediator;
    private /* final */ Board board;
    private int boardWidth,boardDepth;
    private double lineWidth;
    // private Point closest; // closest point that he clicked on.
    private double torusAmount=.4; // size of real board
    // @SuppressWarnings("unused")
    private Point pUpperLeft,pLowerLeft,pUpperRight,pLowerRight;
    private int dx,dy;
    private Point dp;
    private final Set<Line> lines=new LinkedHashSet<Line>();
    private Image black,white,edge;
    // toggle cursor only over vacant!
    private Jitter jitter;
    private Cursor cursor,blackCursor,whiteCursor;
    static final long serialVersionUID=1L;
    public static final double aspectRatio=434.2/454.5;
    static double starPointDiameter=4/454.5;
}
