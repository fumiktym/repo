package ktymBackup;

import java.io.File;
import java.util.Date;

public class FileTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		File f = new File("J:/tmp/IP55_32Jpn.exe");
		System.out.println("exists="+(f.exists() ? "true" : "false"));
		System.out.println("dir="+(f.isDirectory() ? "true" : "false"));
		Date d = new Date(f.lastModified());
		System.out.println("last modified="+d.toString());
	}

}
