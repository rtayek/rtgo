package sgf;
import io.Logging;
import static sgf.Parser.getSgfData;
import static sgf.Parser.sgfDataKeySet;
import static sgf.Parser.sgfFiles;
import java.io.File;
import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Set;
public class SgfData {
	// keep track of all sgf data keys and files.
	// objects.addAll(sgfDataKeySet());
	// objects.addAll(sgfFiles());
	// expectedSgf=getSgfData(key); // gets the data for the key
	// collectSgfFiles in parser gets bad sgf files
	// sgfFiles(String dir)
	public static void main(String[] args) {
		Set<Object> objects=new LinkedHashSet<>();

		Collection<Object> staticSgfData=sgfDataKeySet();
		Logging.mainLogger.info(staticSgfData.size()+" static sgf data keys:");
		objects.addAll(staticSgfData);

		Collection<Object> files=sgfFiles();
		Logging.mainLogger.info(files.size()+" sgf files.");
		objects.addAll(files);

		Set<File> badSgfFiles=Parser.badSgfFiles;
		Logging.mainLogger.info(badSgfFiles.size()+" bad sgf files.");
		objects.addAll(badSgfFiles);

		String sgfDir=Parser.sgfPath;
		Collection<Object> sgf=sgfFiles(sgfDir);
		Logging.mainLogger.info(sgf.size()+" in "+sgfDir);
		objects.addAll(sgf);
		
		String ogsDir=Parser.ogsPath;
		Collection<Object> ogs=sgfFiles(ogsDir);
		Logging.mainLogger.info(ogs.size()+" in "+ogsDir);
		objects.addAll(ogs);

		String strangeDir=Parser.strangePath;
		Collection<Object> strange=sgfFiles(strangeDir);
		Logging.mainLogger.info(strange.size()+" in "+strangeDir);
		objects.addAll(strange);

		// strangesgf
		Logging.mainLogger.info(objects.size()+" total objects:");

	}
}
