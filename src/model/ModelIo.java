package model;

import static io.IOs.noIndent;
import static io.Logging.parserLogger;
import static sgf.Parser.parentheses;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;
import java.io.Writer;

import com.tayek.util.io.FileIO;
import com.tayek.util.io.Indent;

import io.Logging;
import sgf.MNode;
import sgf.Parser;
import sgf.SgfIo.MNodeSaveMode;
import sgf.SgfNode;

/**
 * Consolidated save/restore operations shared across model and SGF code.
 */
public final class ModelIo {
    private ModelIo() {}

    // Independent operations
    public static void restoreModel(Model model,Reader reader) {
        MNode games=MNode.restoreMNodes(reader);
        Logging.mainLogger.info("restored root"+games);
        model.setRoot(games);
    }

    public static boolean saveModel(Model model,Writer writer) {
        return MNode.saveMNodes(writer,rootForSave(model),new Indent(SgfNode.options.indent));
    }

    public static SgfNode restoreSGF(Reader reader) {
        if(reader==null) return null;
        return new Parser().parse(reader);
    }

    public static String saveSgfToString(SgfNode node,Indent indent) {
        if(node==null) return null;
        StringWriter writer=new StringWriter();
        node.saveSgf(writer,indent);
        return writer.toString();
    }

    // Dependent operations
    public static SgfNode restoreAndSaveSGF(Reader reader,Writer writer) {
        if(reader==null) return null;
        SgfNode games=restoreSGF(reader);
        if(games!=null) games.saveSgf(writer,noIndent);
        String actual=writer.toString();
        int p=parentheses(actual);
        if(p!=0) Logging.mainLogger.info("actual parentheses count: "+p);
        return games;
    }

    public static boolean roundTripTwice(Reader reader) {
        Writer writer=new StringWriter();
        restoreAndSaveSGF(reader,writer);
        String expected=writer.toString();
        writer=new StringWriter();
        restoreAndSaveSGF(FileIO.toReader(expected),writer);
        String actual=writer.toString();
        if(!actual.equals(expected)) {
            parserLogger.severe(actual+"!="+reader);
            return false;
        }
        return true;
    }

    public static MNode mNodeRoundTrip(Reader reader,Writer writer,MNodeSaveMode saveMode) {
        MNode root=MNode.restoreMNodes(reader);
        if(saveMode==MNodeSaveMode.direct) {
            String actual=MNode.saveMNodesDirectly(root);
            try {
                writer.write(actual);
            } catch(IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            boolean ok=MNode.saveMNodes(writer,root,noIndent);
            if(!ok) Logging.mainLogger.info("not ok!");
        }
        return root;
    }

    public static MNode modelRoundTrip(String expectedSgf,Writer writer,ModelHelper.ModelSaveMode saveMode) {
        if(expectedSgf==null) return null;
        SgfNode games=restoreSGF(FileIO.toReader(expectedSgf));
        if(games==null) return null;
        if(games.right!=null) Logging.mainLogger.info(" 2 more than one game!");
        if(saveMode==ModelHelper.ModelSaveMode.sgfNodeChecked) {
            saveSgfToString(games,noIndent);
        }
        MNode mNodes0=MNode.toGeneralTree(games);
        Model model=new Model();
        model.setRoot(mNodes0);
        MNode mNodes=model.root();
        if(mNodes==null) return null;
        String actualSgf=saveFromModelRoot(mNodes,saveMode);
        if(actualSgf!=null) try {
            writer.write(actualSgf);
        } catch(IOException e) {
            e.printStackTrace();
        }
        return mNodes;
    }

    public static MNode modelRoundTrip(Reader reader,Writer writer,ModelHelper.ModelSaveMode saveMode) {
        if(reader==null) return null;
        StringBuffer sb=new StringBuffer();
        FileIO.fromReader(sb,reader);
        return modelRoundTrip(sb.toString(),writer,saveMode);
    }

    public static MNode modelRoundTrip(Reader reader,Writer writer) {
        return modelRoundTrip(reader,writer,ModelHelper.ModelSaveMode.sgfNode);
    }

    public static MNode modelRoundTrip2(String expectedSgf,Writer writer) {
        return modelRoundTrip(expectedSgf,writer,ModelHelper.ModelSaveMode.sgfNodeChecked);
    }

    private static MNode rootForSave(Model model) {
        MNode root=model.root();
        if(ModelTreeOps.isSentinel(root)) {
            Logging.mainLogger.info("root has RT");
            return root;
        }
        MNode wrapped=new MNode(null);
        wrapped.children().add(root);
        Logging.mainLogger.info("added null root");
        return wrapped;
    }

    private static String saveFromModelRoot(MNode root,ModelHelper.ModelSaveMode saveMode) {
        if(saveMode==ModelHelper.ModelSaveMode.direct) {
            return MNode.saveMNodesDirectly(root);
        }
        SgfNode sgfRoot=root.toBinaryTree();
        SgfNode actual=sgfRoot.left;
        return saveSgfToString(actual,noIndent);
    }
}
