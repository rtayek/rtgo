package model;

import java.io.IOException;
import java.io.Reader;
import java.io.StringReader;
import java.io.StringWriter;
import java.io.Writer;
import static io.IOs.noIndent;
import sgf.*;
import utilities.Utilities;
import static sgf.Parser.restoreSgf;

/**
 * Utilities for SGF round-tripping in tests.
 */
public final class ModelHelper {
    private ModelHelper() {}

    public static MNode modelRoundTrip(Reader reader, Writer writer) {
        StringBuffer sb = new StringBuffer();
        Utilities.fromReader(sb, reader);
        String expectedSgf = sb.toString(); // so we can compare
        SgfNode games = restoreSgf(new StringReader(expectedSgf));
        if (games == null) return null; // return empty node!
        if (games.right != null) System.out.println(" 2 more than one game!");

        MNode mNodes0 = MNode.toGeneralTree(games);
        Model model = new Model();
        model.setRoot(mNodes0);
        MNode mNodes = model.root();
        if (mNodes != null) {
            if (mNodes.children.size() > 1) {
                //System.out.println("more than one child: "+mNodes.children);
            }
            SgfNode sgfRoot = mNodes.toBinaryTree();
            SgfNode actual = sgfRoot.left;
            StringWriter hack = new StringWriter();
            actual.saveSgf(hack, noIndent);
            try {
                writer.write(hack.toString());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return mNodes;
    }

    public static MNode modelRoundTrip2(String expectedSgf, Writer writer) {
        SgfNode games = restoreSgf(new StringReader(expectedSgf));
        if (games == null) return null;
        if (games.right != null) System.out.println(" 2 more than one game!");
        games.saveSgf(new StringWriter(), noIndent);
        MNode mNodes0 = MNode.toGeneralTree(games);
        Model model = new Model();
        model.setRoot(mNodes0);
        MNode mNodes = model.root();
        String actualSgf = null;
        if (games != null) {
            SgfNode sgfRoot = mNodes.toBinaryTree();
            SgfNode actual = sgfRoot.left;
            StringWriter stringWriter = new StringWriter();
            actual.saveSgf(stringWriter, noIndent);
            actualSgf = stringWriter.toString();
        }
        if (actualSgf != null) try {
            writer.write(actualSgf);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return mNodes;
    }
}
