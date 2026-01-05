package sgf;
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
		System.out.println(staticSgfData.size()+" static sgf data keys:");
		objects.addAll(staticSgfData);

		Collection<Object> files=sgfFiles();
		System.out.println(files.size()+" sgf files.");
		objects.addAll(files);

		Set<File> badSgfFiles=Parser.badSgfFiles;
		System.out.println(badSgfFiles.size()+" bad sgf files.");
		objects.addAll(badSgfFiles);

		String sgfDir=Parser.sgfPath;
		Collection<Object> sgf=sgfFiles(sgfDir);
		System.out.println(sgf.size()+" in "+sgfDir);
		objects.addAll(sgf);
		
		String ogsDir=Parser.ogsPath;
		Collection<Object> ogs=sgfFiles(ogsDir);
		System.out.println(ogs.size()+" in "+ogsDir);
		objects.addAll(ogs);

		String strangeDir=Parser.strangePath;
		Collection<Object> strange=sgfFiles(strangeDir);
		System.out.println(strange.size()+" in "+strangeDir);
		objects.addAll(strange);

		// strangesgf
		System.out.println(objects.size()+" total objects:");

	}
}
