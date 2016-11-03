package first;

import java.io.*;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;
import java.util.zip.ZipOutputStream;


/**
 * Created by danielhertzman-ericson on 2016-01-13.
 */
public class Test {
	
	private static final String PATH_SEP = "\\";
	 public static final int BUFFER = 2048;
	 private Test() {}
	 
	 
	 public static void zipFilesInPath(final String zipFileName, final String filePath) throws IOException {
		  final FileOutputStream dest = new FileOutputStream(zipFileName);
		  final ZipOutputStream out = new ZipOutputStream(new BufferedOutputStream(dest));
		  try {
		 
		   byte[] data = new byte[BUFFER];
		   final File folder = new File(filePath);
		   final List<String> files = Arrays.asList(folder.list());
		   for (String file : files) {
		    final FileInputStream fi = new FileInputStream(filePath + PATH_SEP + file);
		    final BufferedInputStream origin = new BufferedInputStream(fi, BUFFER);
		    out.putNextEntry(new ZipEntry(file));
		    int count;
		    while ((count = origin.read(data, 0, BUFFER)) != -1) {
		     out.write(data, 0, count);
		    }
		    origin.close();
		    fi.close();
		   }
		  } finally {
		   out.close();
		   dest.close();
		  }
		}
	 
	 public static void unzipFilesToPath(final String zipFileName, final String fileExtractPath) throws IOException {
		 
		  final FileInputStream fis = new FileInputStream(zipFileName);
		  final ZipInputStream zis = new ZipInputStream(new BufferedInputStream(fis));
		  try {
		   ZipEntry entry;
		 
		   while ((entry = zis.getNextEntry()) != null) {
		    int count;
		    byte[] data = new byte[BUFFER];
		    final FileOutputStream fos = new FileOutputStream(fileExtractPath + PATH_SEP + entry.getName());
		    final BufferedOutputStream dest = new BufferedOutputStream(fos, BUFFER);
		    while ((count = zis.read(data, 0, BUFFER)) != -1) {
		     dest.write(data, 0, count);
		    }
		    dest.flush();
		    dest.close();
		   }
		  } finally {
		   fis.close();
		   zis.close();
		  }
		 
		 }
}
