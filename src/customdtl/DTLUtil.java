package customdtl;

import java.io.File;

public class DTLUtil {
	public static boolean isFileExist(String filename) {
		File tmpDir = new File(filename);
		return tmpDir.exists();
	}
	
	public static void printAllFiles() { 
		File folder = new File("files");
		File[] listOfFiles = folder.listFiles();
	
	    for (int i = 0; i < listOfFiles.length; i++) {
	      if (listOfFiles[i].isFile()) {
	        System.out.println("- " + listOfFiles[i].getName());
	      } else if (listOfFiles[i].isDirectory()) {
	        System.out.println("Directory " + listOfFiles[i].getName());
	      }
	    }
	}
}
