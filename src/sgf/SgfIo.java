package sgf;
import java.io.File;
import java.util.List;
import io.Logging;
/** Reusable SGF/MNode I/O helpers shared by source and tests. */
public final class SgfIo {
	public enum MNodeSaveMode {
		standard,direct
	}
	private SgfIo() {}
	public static String loadExpectedSgf(Object key) {
		if(key==null) throw new RuntimeException("key: "+key+" is nul!");
		String sgf=Parser.getSgfData(key);
		if(sgf==null) return null;
		int p=Parser.parentheses(sgf);
		if(p!=0) {
			Logging.mainLogger.info(" bad parentheses: "+p);
			throw new RuntimeException(key+" bad parentheses: "+p);
		}
		return sgf;
	}
	public static String prepareExpectedSgf(Object key,String sgf) {
		String normalized=prepareSgf(sgf);
		if(normalized!=null) {
			if(SgfNode.options.removeLineFeed) if(normalized.contains("\n)")) {
				Logging.mainLogger.info("lf badness");
				System.exit(0);
			}
			if(SgfNode.SgfOptions.containsQuotedControlCharacters(key,normalized)) {
				Logging.mainLogger.info(key+" contains quoted control characters.");
				normalized=SgfNode.SgfOptions.removeQuotedControlCharacters(normalized);
			}
		}
		return normalized;
	}
	public static String prepareSgf(String sgf) {
		return sgf!=null?SgfNode.options.prepareSgf(sgf):null;
	}
	public static void logBadParentheses(String sgf,Object key,String label) {
		if(sgf==null) return;
		int p=Parser.parentheses(sgf);
		if(p!=0) Logging.mainLogger.info(key+" "+label+" bad parentheses: "+p);
	}
	public static File firstExistingFile(File... files) {
		if(files==null) return null;
		for(File file:files)
			if(file!=null&&file.exists()) return file;
		return null;
	}
	public static File[] filesInDir(String dir,String... filenames) {
		File[] files=new File[filenames.length];
		for(int i=0;i<filenames.length;i++)
			files[i]=new File(dir,filenames[i]);
		return files;
	}
	public static SgfNode nodeWithProperty(P id,String value) {
		SgfNode node=new SgfNode();
		node.add(new SgfProperty(id,List.of(value)));
		return node;
	}
}
