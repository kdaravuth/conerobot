package conerobotprj.dv.lib;

import java.io.File;
import java.nio.file.FileSystems;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;

public class Utility {

	public static List<String> listFiles = new ArrayList<String>();

	public static void walk(String path) {

		File root = new File(path);
		File[] list = root.listFiles();

		if (list == null)
			return;

		for (File f : list) {
			if (f.isDirectory()) {
				walk(f.getPath());
				// System.out.println( "Dir:" + f.getAbsoluteFile() );
			} else {
				// System.out.println( "File:" + f.getAbsoluteFile());
				listFiles.add(f.getPath().toString());
			}
		}
	}

	public static String getBillfileName(String fullfilePath) {

		// Get the file separator
		return fullfilePath.split(Pattern.quote(FileSystems.getDefault().getSeparator()))[fullfilePath
				.split(Pattern.quote(FileSystems.getDefault().getSeparator())).length - 1];

	}
}
