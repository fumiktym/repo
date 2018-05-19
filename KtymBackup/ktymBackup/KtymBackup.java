/*
 * Created on 2004/11/29
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ktymBackup;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintStream;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;


/**
 * @author Fumi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class KtymBackup {
	
	// options
	static boolean helpOption = false;
	static boolean verbose = false;
	static boolean test = false;
	static boolean force = false;
	static boolean quiet = false;
	static PrintStream  logFile = System.out;
	static boolean verBackup = true;
	static long    verBackupSize = 10 * 1024 * 1024; // 100M
	static int     verBackupLimit = 3;
	
	static String command = null;

	public static void main(String[] args) {
		int i = analyzeCommandAndOptions(args);
		if(helpOption || command == null ||
		   args.length == i){
		    	logFile.println("Usage: java KtymBackup [-h|-help]");
		    	logFile.println("   -- Display this help.");
		    	logFile.println("Usage: java KtymBackup [commands|options] top-dir target-dir");
				logFile.println("   -- Backup top-dir and below directories into target-dir.");
		    	logFile.println("           top-dir - absolute path. e.g. \\\\server\\share\\... OR C:\\...");
				logFile.println("           target-dir - absolute path. e.g. \\\\server\\share\\... OR C:\\...");
				logFile.println("   commands...");
				logFile.println("      backup    - backup top-dir to target-dir");
				logFile.println("      restore   - restore top-dir from target-dir");
				logFile.println("      sync      - sync(backup & restore) top-dir from target-dir");
				logFile.println("      clean     - clean .oldversions directories in top-dir");
				logFile.println("   options...");
				logFile.println("      -v        - verbose.");
				logFile.println("      -t        - not execute. display only.");
				logFile.println("      -f        - even when target is new, force write");
				logFile.println("      -q        - quiet");
				logFile.println("      -l <path> - specify log file path");
				logFile.println("      -n        - no backup old versions");
				logFile.println("      -s <size> - if larger than <size>, no backup old versions. <size>= <n>, <n>K, <n>M, <n>G");
		    	logFile.println("      -g <generations> - limit #<old versions> to <generations>");
		    	System.exit(0);
		}
		if(command.equals("backup")){
			if(i+2 < args.length) {
				if(verbose)
					logFile.println("Warning: Excessive parameters -- "+args[i+2]);
					
			}
			DateFormat format = new SimpleDateFormat();
			try {
				File topDir = analyzeAbsDir(args[i]);
				File targetDir = analyzeAbsDir(args[i+1]);
				
				Date start = new Date(System.currentTimeMillis());
				DirectoryBackup backupObj = new DirectoryBackup(topDir, targetDir);
				if(verbose)
					logFile.println("Checking specified directories...");
				int n = backupObj.calculateDirsN();
				if(verbose) {
					logFile.println((test?"Test-backup":"Backup")+" start from '"+args[i]+"' to '"+args[i+1]+"' (expected "+n+" directories)...");
				}
				backupObj.doBackup(n);
				if(verbose) logFile.println("Completely done "+(test?"test-backup":"backup")+"...");
				Date end = new Date(System.currentTimeMillis());
				if(!KtymBackup.quiet) {
					logFile.println("Start Time: "+format.format(start));
					logFile.println("End   Time: "+format.format(end));
				}				
			} catch (IOException e) {
				e.printStackTrace();
				String mes = "'"+args[i]+"', '"+args[i+1]+"' - "+e.getMessage();
				logFile.println("Error: IO exception - "+mes);
				System.err.println("Error: IO exception - "+mes);
				if(logFile != System.out) logFile.close();
				System.exit(1);
			}
		} else if(command.equals("restore")){
			if(i+2 < args.length) {
				if(verbose)
					logFile.println("Warning: Excessive parameters -- "+args[i+2]);
					
			}
			DateFormat format = new SimpleDateFormat();
			try {
				File topDir = analyzeAbsDir(args[i]);
				File targetDir = analyzeAbsDir(args[i+1]);
				
				Date start = new Date(System.currentTimeMillis());
				DirectoryBackup backupObj = new DirectoryBackup(topDir, targetDir);
				if(verbose)
					logFile.println("Checking specified directories...");
				int n = backupObj.calculateDirsN();
				if(verbose) {
					logFile.println((test?"Test-restore":"Restore")+" start from '"+args[i]+"' to '"+args[i+1]+"' (expected "+n+" directories)...");
				}
				backupObj.doRestore(n);
				if(verbose) logFile.println("Completely done "+(test?"test-restore":"restore")+"...");
				Date end = new Date(System.currentTimeMillis());
				if(!KtymBackup.quiet) {
					logFile.println("Start Time: "+format.format(start));
					logFile.println("End   Time: "+format.format(end));
				}				
			} catch (IOException e) {
				e.printStackTrace();
				logFile.println("Error: IO exception - "+e.getMessage());
				System.err.println("Error: IO exception - "+e.getMessage());
				if(logFile != System.out) logFile.close();
				System.exit(1);
			}
		} else if(command.equals("sync")){
			if(i+2 < args.length) {
				if(verbose)
					logFile.println("Warning: Excessive parameters -- "+args[i+2]);
					
			}
			DateFormat format = new SimpleDateFormat();
			try {
				File topDir = analyzeAbsDir(args[i]);
				File targetDir = analyzeAbsDir(args[i+1]);
				
				Date start = new Date(System.currentTimeMillis());
				DirectoryBackup backupObj = new DirectoryBackup(topDir, targetDir);
				if(verbose)
					logFile.println("Checking specified directories...");
				int n = backupObj.calculateDirsN();
				if(verbose) {
					logFile.println((test?"Test-sync":"Sync")+" start '"+args[i]+"' with '"+args[i+1]+"' (expected "+n+" directories)...");
				}
				backupObj.doSync(n);
				if(verbose) logFile.println("Completely done "+(test?"test-sync":"sync")+"...");
				Date end = new Date(System.currentTimeMillis());
				if(!KtymBackup.quiet) {
					logFile.println("Start Time: "+format.format(start));
					logFile.println("End   Time: "+format.format(end));
				}				
			} catch (IOException e) {
				e.printStackTrace();
				String mes = "'"+args[i]+"', '"+args[i+1]+"' - "+e.getMessage();
				logFile.println("Error: IO exception - "+mes);
				System.err.println("Error: IO exception - "+mes);
				if(logFile != System.out) logFile.close();
				System.exit(1);
			}
		} else 
			if(command.equals("clean")){
				if(i+1 < args.length) {
					if(verbose)
						logFile.println("Warning: Excessive parameters -- "+args[i+1]);
							
				}
				DateFormat format = new SimpleDateFormat();
				try {
					File topDir = analyzeAbsDir(args[i]);
						
					Date start = new Date(System.currentTimeMillis());
					DirectoryBackup backupObj = new DirectoryBackup(topDir);
					if(verbose)
						logFile.println("Checking specified directories...");
					int n = backupObj.calculateDirsN();
					if(verbose) {
						logFile.println((test?"Test-clean":"Clean")+" start from '"+args[i]+"' (expected "+n+" directories)...");
					}
					backupObj.doClean(n);
					if(verbose) logFile.println("Completely done "+(test?"test-clean":"clean")+"...");
					Date end = new Date(System.currentTimeMillis());
					if(!KtymBackup.quiet) {
						logFile.println("Start Time: "+format.format(start));
						logFile.println("End   Time: "+format.format(end));
					}				
				} catch (IOException e) {
					e.printStackTrace();
					logFile.println("Error: IO exception - "+e.getMessage());
					System.err.println("Error: IO exception - "+e.getMessage());
					if(logFile != System.out) logFile.close();
					System.exit(1);
				}
			}
		
		if(logFile != System.out) logFile.close();
	}
	
	private static int analyzeCommandAndOptions(String[] args){
		int i;
		for(i = 0; i < args.length; i++){
			if(args[i] != null && args[i].length() > 0 && args[i].charAt(0)=='-'){
				if(args[i].length() == 1) break;
				else {
					String option = args[i].substring(1);
					if(option.equals("h") || option.equals("help")) helpOption = true;
					else if(option.equals("v")) {
						verbose = true;
						quiet = false;
					} 
					else if(option.equals("t")) test = true;
					else if(option.equals("f")) {
						force = true;
					} 
					else if(option.equals("q")) {
						quiet = true;
						verbose = false;
					} 
					else if(option.equals("l")){
						String path = (option.length() > 1)?option.substring(1):null;
						if(path == null) {
							if(args.length > i+1) {
								path = args[++i];
							} else {
								System.err.println("No log file path specified");
								continue;
							}
						}
						try {
							PrintStream ps = new PrintStream(new FileOutputStream(path));
							logFile = ps;
						} catch (FileNotFoundException e) {
							System.err.println("Log file path '"+path+"' does not exist");
							continue;
						}
					}
					else if(option.equals("n")){
						verBackup = false;
					}
					else if(option.equals("s")) {
						verBackup = true;
						String sizeStr = (option.length() > 1)?option.substring(1):null;
						if(sizeStr == null) {
							if(args.length > i+1) {
								sizeStr = args[++i];
							} else {
								continue;
							}
						}
						long n = 1;
						char lc = sizeStr.charAt(sizeStr.length()-1);
						switch(lc) {
							case 'G': case 'g': n = 1024;
							case 'M': case 'm': n *= 1024;
							case 'K': case 'k': n *= 1024;
								sizeStr = sizeStr.substring(0,sizeStr.length()-1);
								break;
						}
						try {
							n *= Integer.parseInt(sizeStr);
							verBackupSize = n;
						} catch(NumberFormatException e) {
							logFile.println("-s option - illegal number format '-"+option+"'");
						}
					}
					else if(option.equals("g")) {
						verBackup = true;
						String nStr = (option.length() > 1)?option.substring(1):null;
						if(nStr == null) {
							if(args.length > i+1) {
								nStr = args[++i];
							} else {
								continue;
							}
						}
						try {
							int n = Integer.parseInt(nStr);
							verBackupLimit = n;
						} catch(NumberFormatException e) {
							logFile.println("-g option - illegal number format '-"+option+"'");
						}
					}
					else {
						if(!quiet)
							logFile.println("Warning: Unknown option '-"+option+"'");
					} 
				}
			} else if(args[i].equals("backup")) command = args[i];
			else if(args[i].equals("restore")) command = args[i];
			else if(args[i].equals("sync")) command = args[i];
			else if(args[i].equals("clean")) command = args[i];
			else break;
		}
		return i;
	}
	
	private static File analyzeAbsDir(String str) throws IOException {
		if(str.indexOf("\\\\")==0 || str.charAt(1)==':'){
			File file = new File(str);
			if(!file.exists())
				throw new IOException("Specified path not exist.");
			if(!file.isDirectory())
				throw new IOException("Specified path is not directory");
			return file; // no error
		} else throw new IOException("Need absolute path specified. e.g. '\\\\server\\share...' or 'C:\\...");
	}
}
