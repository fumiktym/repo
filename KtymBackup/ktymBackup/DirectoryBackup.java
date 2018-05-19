/*
 * Created on 2004/11/29
 *
 * To change the template for this generated file go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
package ktymBackup;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Vector;


/**
 * @author Fumi
 *
 * To change the template for this generated type comment go to
 * Window&gt;Preferences&gt;Java&gt;Code Generation&gt;Code and Comments
 */
public class DirectoryBackup {
	
	// constants
	final static String TIMESTAMPFILE=".kb.timestamp";
	final static String REMOVEDDIR=".kb.removed";
	final static String IGNORE_FILES_NAME = ".backup";
	final static String BACKUP_DIR_NAME = ".oldversions";
	
	final static long MACHINE_MILISEC_ERROR = 60000; // マシン間の時計誤差＝60秒までは許容
	// instance vars
	
	private File source;
	private File target;
	
	private int doneN = 0;
	private int copiedN = 0;
	private int checkedN = 0;
	
	//前回の実行日付時間
	private long prevStartTime;
	private long prevEndTime;
	//今回の実行スタート時間
	private long curStartTime;
	//実行日付ログ
	private List<String> logLines;
	
	public DirectoryBackup(File srcDir, File tgtDir) throws IOException {
		if(srcDir == null || !srcDir.isDirectory()){
			throw new IOException("(Internal)DirectoryBackup: source dir is illegal");
		}
		source = srcDir;
		if(tgtDir == null || !tgtDir.isDirectory()){
			throw new IOException("(Internal)DirectoryBackup: target dir is illegal");
		}
		target = tgtDir;
	}
	public void doBackup(int total) {
		doneN = 0;
		copiedN = 0;
		checkedN = 0;
		getPrevTime();
		backupDirs(source, target, total);
		if(!KtymBackup.quiet){
			KtymBackup.logFile.println("Visited directories: "+doneN);
			KtymBackup.logFile.println("Tested Files:        "+checkedN);
			KtymBackup.logFile.println("Copied Files:        "+copiedN);
		}
		setPrevTime();
	}
	
	public int calculateDirsN() throws IOException {
		return calcDirs(source, target);
	}
	
	public DirectoryBackup(File srcDir) throws IOException {
		if(srcDir == null || !srcDir.isDirectory()){
			throw new IOException("(Internal)DirectoryBackup: source dir is illegal");
		}
		source = srcDir;
		target = null;
	}
	private int calcDirs(File s, File d) throws IOException {
		if(d != null && s.equals(d)) throw new IOException("Directory '"+d.getPath()+"' is included in the source directory.");
		File[] files = s.listFiles();
		int n = 1;
		if(files == null) return n;
		for(int i = 0; i< files.length; i++) {
			if(files[i].isDirectory()) n += calcDirs(files[i], d);
		}
		return n;
	}
	
	private void backupDirs(File s, File t, int total) {
		if(KtymBackup.verbose)
			KtymBackup.logFile.println("About to "+(KtymBackup.test?"test":"backup")+" '"+s.getPath()+"'...");
		
		File[] files = s.listFiles();
		if(files == null) {
			doneN++;
			return;
		}
		Set<String> ignoreFiles = checkIgnoreFiles(files, null/* force */);
		int copiedFilesN = 0;
		int filesN = 0;
		int processDirsN = 0;
		int dirsN = 0;
		for(int i = 0; i < files.length; i++) {
			File file = files[i];
			try {
				if(file.isDirectory()){
					if(ignoreFiles.contains(file.getName().toUpperCase())){
						if(KtymBackup.verbose){
							KtymBackup.logFile.println("Directory '"+file.getName()+"' was ignored by "+IGNORE_FILES_NAME);
						}
					} else {
						if(backupDirectory(file, t, total))
							processDirsN++;
					}
					dirsN++;
				} else if(file.isFile()) {
					if(ignoreFiles.contains(file.getName().toUpperCase())){
						if(KtymBackup.verbose){
							KtymBackup.logFile.println("File '"+file.getName()+"' was ignored by "+IGNORE_FILES_NAME);
						}
					} else {
						if(backupFile(file, t)) {
							copiedFilesN++;
							copiedN++;
						}
					}
					checkedN++;
					filesN++;
				} else {
					throw new IOException("(Internal)DirectoryBackup::backupDirs - Dir member '"+file.getPath()+"' not directory nor files...");
				}
			} catch (IOException e) {
				e.printStackTrace();
				KtymBackup.logFile.println(e.getMessage()+" in '"+file.getPath()+"'");
				e.printStackTrace(KtymBackup.logFile);
			}
		}
		
		doneN++;
		if(KtymBackup.verbose)
			KtymBackup.logFile.println("Finished processing '"+s.getPath()+"' ("+(doneN*100/total)+"%)... #"+(KtymBackup.test?"tested-to-copy":"copied")+" files "+copiedFilesN+"/"+filesN+", #processed directories:"+processDirsN+"/"+dirsN);
		else if(!KtymBackup.quiet) {
			String str = "Finished - "+(doneN*100/total)+"% '"+s.getPath()+"' "+copiedFilesN+"/"+filesN+"                                               %";
			if(str.length() > 76) str = str.substring(0,76);
			System.out.print(str + "\015");
		}		
	}
	
	// filesにある.backup ファイルから読み込んだ同期対象からはずすファイル名のセットを返す。
	// ただし、"\"が前に着いたファイルは片方の.backup で指定されていても強制的に同期する。この対象を forceFilesにいれる。
	// forceFilesがnullなら強制同期のものを取得しない
	private Set<String> checkIgnoreFiles(File[] files, Set<String> forceFiles) {
		Set<String> ret = new HashSet<String>();
		Set<String> force = forceFiles;
		if(force == null) force = new HashSet<String>();
		File ignoreFile = null;
		for(int i = 0; i < files.length; i++ ){
			if(files[i].getName().equalsIgnoreCase(IGNORE_FILES_NAME)) {
				ignoreFile = files[i];
				break;
			}
			if(files[i].getName().startsWith(TIMESTAMPFILE)){
				ret.add(files[i].getName().toUpperCase());
			}
		}
		if(ignoreFile != null){
			try {
				BufferedReader reader = new BufferedReader(
					new FileReader(ignoreFile));
				String line;
				while((line = reader.readLine())!=null) {
					String aLine = line.trim().toUpperCase();
					if(aLine.startsWith("/")){
						aLine = aLine.substring(1);
						force.add(aLine);
					} else {
						ret.add(aLine);
					}
				}
				reader.close();
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				if(!KtymBackup.quiet) {
					KtymBackup.logFile.println(e.getMessage()+" in reading '"+ignoreFile.getPath()+"'");
					e.printStackTrace(KtymBackup.logFile);
				}
			} catch (IOException e) {
				e.printStackTrace();
				if(!KtymBackup.quiet) {
					KtymBackup.logFile.println(e.getMessage()+" in reading '"+ignoreFile.getPath()+"'");
					e.printStackTrace(KtymBackup.logFile);
				}
			}
		}
		ret.add(REMOVEDDIR.toUpperCase());
		ret.add(IGNORE_FILES_NAME.toUpperCase());
		ret.add(BACKUP_DIR_NAME.toUpperCase());
		// force にあるものがretにあれば結果から取り除く
		for(String forceFile : force){
			if(ret.contains(forceFile)) ret.remove(forceFile);
		}
		return ret;
	}
	private boolean backupDirectory(File dir, File tgtParent, int total) throws IOException {
		File tgtDir = searchName(dir.getName(), tgtParent);
		if(tgtDir == null) {
			if(!KtymBackup.quiet)
				KtymBackup.logFile.println("Warning: target dir '"+dir.getName()+"' does not exist in '"+tgtParent.getPath()+"'.");
			if(KtymBackup.test){
				KtymBackup.logFile.println("Mkdir '"+dir.getName()+"' in '"+tgtParent.getPath()+"'.");
				return false;
			}
			tgtDir = new File(tgtParent, dir.getName());
			tgtDir.mkdir();
			if(dir.isHidden())
				try {
					Runtime.getRuntime().exec("attrib +h \""+tgtDir.getPath()+"\"").waitFor();
				} catch (InterruptedException e) {
					throw new IOException(e.getMessage()+" during attrib +h "+tgtDir.getPath());
				} catch (IOException e) {
					throw new IOException(e.getMessage()+" during attrib +h "+tgtDir.getPath());
				} 
		}
		if(!tgtDir.isDirectory()){
			throw new IOException("about to backup dir '"+dir.getName()+"',"+
			                  "but the same name non-dir file exists in '"+tgtParent.getPath()+"'");
		}
		if(!dir.canRead()){
			throw new IOException("cannot read dir '"+dir.getPath()+"'.");
		}
		if(!tgtDir.canWrite()){
			throw new IOException("cannot write dir '"+tgtDir.getPath()+"'.");
		}
		String attr = getFileAttr(dir);
		String args ="";
		if(attr.indexOf("A")>=0) args += "+a ";
		if(attr.indexOf("S")>=0) args += "+S ";
		if(attr.indexOf("H")>=0) args += "+h ";
		if(args.length() > 0) {
			try {
				Runtime.getRuntime().exec("attrib "+args+"\""+tgtDir.getPath()+"\"").waitFor();
			} catch (InterruptedException e) {
				throw new IOException(e.getMessage()+" during attrib "+args+tgtDir.getPath());
			} catch (IOException e) {
				throw new IOException(e.getMessage()+" during attrib "+args+tgtDir.getPath());
			}
		} 
		
		backupDirs(dir, tgtDir, total);
		
		return true;
	}
	
	private boolean backupFile(File file, File tgtParent) throws IOException {
		File tgtFile = searchName(file.getName(), tgtParent);
		boolean tgtFileExists = false;
		if(tgtFile == null || !tgtFile.exists()) {
			if(KtymBackup.verbose) KtymBackup.logFile.println("Target file '"+file.getName()+"' does not exist in '"+tgtParent.getPath()+"'.");
			if(KtymBackup.test) {
				KtymBackup.logFile.println("Tested to create a new file '"+file.getName()+"' in '"+tgtParent.getPath()+"'.");
				return true;
			}
			if(tgtFile == null)
				tgtFile = new File(tgtParent, file.getName());
			if(!tgtFile.createNewFile())
				throw new IOException("Cannot create a file '"+tgtFile.getPath()+"'");
			tgtFileExists = false;
		} else {
			tgtFileExists = true;
			if(!tgtFile.isFile()) {
				throw new IOException("Cannot copy a directory '"+tgtFile.getPath()+"' to a file."); 
			} else if(file.lastModified() < tgtFile.lastModified()-60000){
				if(KtymBackup.force){
					if(KtymBackup.verbose)
						KtymBackup.logFile.println("File '"+file.getPath()+"' is older than one in '"+tgtParent.getPath()+"'.");
				} else {
					if(!KtymBackup.quiet)
						KtymBackup.logFile.println("Warning: File '"+file.getPath()+"' is older than on in '"+tgtParent.getPath()+"', and was not copied.");
					return false;
				}
			} else if(file.lastModified() < tgtFile.lastModified()+60000){
				return false;
			}
		}
		if(KtymBackup.test){
			KtymBackup.logFile.println("Tested to copy file '"+file.getName()+"' to '"+tgtFile.getPath()+"'.");
			return true;
		}
		
		if(!tgtFile.canWrite()){
			try {
				Runtime.getRuntime().exec("attrib -r \""+tgtFile.getPath()+"\"").waitFor();
			} catch (InterruptedException e) {
				throw new IOException(e.getMessage()+" during attrib -r "+tgtFile.getPath());
			} catch (IOException e) {
				throw new IOException(e.getMessage()+" during attrib -r "+tgtFile.getPath());
			}
		}
		
		if(tgtFile.isHidden() || (tgtFileExists && getFileAttr(tgtFile).indexOf("S")>=0)) {
			try {
				Runtime.getRuntime().exec("attrib -h -s \""+tgtFile.getPath()+"\"").waitFor();
			} catch (InterruptedException e) {
				throw new IOException(e.getMessage()+" during attrib -h -s "+tgtFile.getPath());
			} catch (IOException e) {
				throw new IOException(e.getMessage()+" during attrib -h -s "+tgtFile.getPath());
			}
		}
		
		if(tgtFileExists 
		   && KtymBackup.verBackup
		   && file.length() < KtymBackup.verBackupSize) backupOldVersions(tgtFile);

		copyFile(file, tgtFile);
		
		return true;
	}
	
	private File searchName(String name, File parent){
		File[] files = parent.listFiles();
		if(files == null) return null;
		for(int i=0; i < files.length; i++){
			if(files[i].getName().toUpperCase().equals(name.toUpperCase())) return files[i];
		}
		return null;
	}
	
	private void backupOldVersions(File f) throws IOException {
		File dir = f.getParentFile();
		File backupDir = checkAndCreateOldVersinoDir(dir);
		File oldOne = null;
		int n = 0;
		int maxN = 0;
		int minN = Integer.MAX_VALUE;
		File[] files = backupDir.listFiles();
		String fname = f.getName().toUpperCase();
		for(int i = 0; i < files.length; i++) {
			String aName = files[i].getName().toUpperCase();
			if(aName.indexOf(fname)==0){
				try {
					int idx = Integer.parseInt(aName.substring(fname.length()+1));
					if (maxN < idx) maxN = idx;
					if (minN > idx) {
						minN = idx;
						oldOne = files[i];
					}
				} catch (NumberFormatException e) {
					continue;
				}
				n++;
			}
		}
		if(n > KtymBackup.verBackupLimit){
			if(oldOne != null) oldOne.delete();
		}
		File newBackupFile = new File(backupDir, f.getName()+"-"+(maxN+1));
		if(newBackupFile.exists()){
			throw new IOException("(Internal) old version backup already exists");
		}
		if(!newBackupFile.createNewFile()) {
			throw new IOException("(Internal) old version backup cannot be created");
		}

		OutputStream out = new FileOutputStream(newBackupFile);
		InputStream in = new FileInputStream(f);
		byte[] buf = new byte[4096];
		int blen = 0;
		do {
			blen = in.read(buf);
			if(blen > 0) out.write(buf, 0, blen);
		} while(blen > 0);
		out.flush();
		out.close();
		in.close();
		
		newBackupFile.setLastModified(f.lastModified());
	}
	
	private File checkAndCreateOldVersinoDir(File dir) {
		File target = new File(dir, BACKUP_DIR_NAME);
		if(target.exists()) return target;
		target.mkdir();
		return target;
	}
	public void doRestore(int total) {
		doneN = 0;
		copiedN = 0;
		checkedN = 0;
		restoreDirs(source, target, total);
		if(!KtymBackup.quiet){
			KtymBackup.logFile.println("Visited directories: "+doneN);
			KtymBackup.logFile.println("Tested Files:        "+checkedN);
			KtymBackup.logFile.println("Restored Files:      "+copiedN);
		}
	}
	private void restoreDirs(File s, File t, int total) {
		if(KtymBackup.verbose)
			KtymBackup.logFile.println("About to "+(KtymBackup.test?"test-restore":"restore")+" '"+s.getPath()+"'...");
		
		File[] files = t.listFiles();
		if(files == null) {
			doneN++;
			return;
		}
		Set<String> ignoreFiles = checkIgnoreFiles(files, null /* force　”/～”で強制的にバックアップするもの　＝処理なし*/);
		int copiedFilesN = 0;
		int filesN = 0;
		int processDirsN = 0;
		int dirsN = 0;
		for(int i = 0; i < files.length; i++) {
			File file = files[i];
			try {
				if(file.isDirectory()){
					if(ignoreFiles.contains(file.getName().toUpperCase())){
						if(KtymBackup.verbose){
							KtymBackup.logFile.println("Directory '"+file.getName()+"' was ignored by "+IGNORE_FILES_NAME);
						}
					} else {
						dirsN++;
						if(file.getName().toUpperCase().equals(BACKUP_DIR_NAME.toUpperCase()))
							continue;
						if(restoreDirectory(s, file, total)){
							processDirsN++;
						}
					}
				} else if(file.isFile()) {
					if(restoreFile(s, file)) {
						copiedFilesN++;
						copiedN++;
					}
					checkedN++;
					filesN++;
				} else {
					throw new IOException("(Internal)DirectoryBackup::restoreDirs - Dir members not directory nor files...");
				}
			} catch (IOException e) {
				e.printStackTrace();
				KtymBackup.logFile.println(e.getMessage()+" in '"+file.getPath()+"'");
				e.printStackTrace(KtymBackup.logFile);
			}
		}
		
		doneN++;
		if(KtymBackup.verbose)
			KtymBackup.logFile.println("Finished processing '"+t.getPath()+"' ("+(doneN*100/total)+"%)... #"+(KtymBackup.test?"tested-to-copy":"copied")+" files "+copiedFilesN+"/"+filesN+", #processed directories:"+processDirsN+"/"+dirsN);
		else if(!KtymBackup.quiet) {
			String str = "Finished - "+(doneN*100/total)+"% '"+t.getPath()+"' "+copiedFilesN+"/"+filesN+"                                               %";
			if(str.length() > 76) str = str.substring(0,76);
			System.out.print(str + "\015");
		}		
	}
	private boolean restoreDirectory(File srcParent, File dir, int total) throws IOException {
		File srcDir = searchName(dir.getName(), srcParent);
		if(srcDir == null) {
			if(!KtymBackup.quiet)
				KtymBackup.logFile.println("Warning: backuped dir '"+dir.getName()+"' does not exist in '"+srcParent.getPath()+"'.");
			if(KtymBackup.test){
				KtymBackup.logFile.println("Mkdir '"+dir.getName()+"' in '"+srcParent.getPath()+"'.");
				return false;
			}
			srcDir = new File(srcParent, dir.getName());
			srcDir.mkdir();
			if(dir.isHidden())
				try {
					Runtime.getRuntime().exec("attrib +h \""+srcDir.getPath()+"\"").waitFor();
				} catch (InterruptedException e) {
					throw new IOException(e.getMessage()+" in attrib +h "+srcDir.getPath());
				} catch (IOException e) {
					throw new IOException(e.getMessage()+" in attrib +h "+srcDir.getPath());
				} 
		}
		if(!srcDir.isDirectory()){
			throw new IOException("about to restore dir '"+dir.getName()+"',"+
							  "but the same name non-dir file exists in '"+srcParent.getPath()+"'");
		}
		if(!dir.canRead()){
			throw new IOException("cannot read dir '"+dir.getPath()+"'.");
		}
		String attr = getFileAttr(dir);
		String args ="";
		if(attr.indexOf("A")>=0) args += "+a ";
		if(attr.indexOf("S")>=0) args += "+S ";
		if(attr.indexOf("H")>=0) args += "+h ";
		if(args.length() > 0) {
			try {
				Runtime.getRuntime().exec("attrib "+args+"\""+srcDir.getPath()+"\"").waitFor();
			} catch (InterruptedException e) {
				throw new IOException(e.getMessage()+" in attrib "+args+srcDir.getPath());
			} catch (IOException e) {
				throw new IOException(e.getMessage()+" in attrib "+args+srcDir.getPath());
			}
		} 
		
		restoreDirs(srcDir, dir, total);
		
		return true;
	}
	private boolean restoreFile(File srcParent, File file) throws IOException {
		File srcFile = searchName(file.getName(), srcParent);
		if(srcFile == null || !srcFile.exists()) {
			if(KtymBackup.verbose) KtymBackup.logFile.println("Original file '"+file.getName()+"' does not exist in '"+srcParent.getPath()+"'.");
			if(KtymBackup.test) {
				KtymBackup.logFile.println("Tested to create a new file '"+file.getName()+"' in '"+srcParent.getPath()+"'.");
				return true;
			}
			if(srcFile == null)
				srcFile = new File(srcParent, file.getName());
			if(!srcFile.createNewFile())
				throw new IOException("Cannot create a file '"+srcFile.getPath()+"'");
		} else {
			if(!srcFile.isFile()) {
				throw new IOException("Cannot copy a directory '"+srcFile.getPath()+"' to a file."); 
			} else if(srcFile.lastModified()+60000 > file.lastModified()){
				
				if(file.lastModified() > srcFile.lastModified()-60000){
					// same timestamp within +/- 1 min.
					return false;
				} else {
					// the original file is newer than the backup file
					if(KtymBackup.force) {
					   	if(!KtymBackup.quiet) {
					   		KtymBackup.logFile.println("Warning: Origianl file '"+srcFile.getPath()+"' is newer than the backup file, but due to force option, the backup file is restored...");
					   	}
					} else {
						if(!KtymBackup.quiet) {
							KtymBackup.logFile.println("Warning: Original file '"+srcFile.getPath()+"' is newer than the backup file, so was not restored.");
						}
						return false;
					}
				}
			}
		}
		if(KtymBackup.test){
			KtymBackup.logFile.println("Tested to restore file '"+file.getName()+"' to '"+srcFile.getPath()+"'.");
			return true;
		}
		
		if(!srcFile.canWrite()){
			try {
				Runtime.getRuntime().exec("attrib -r \""+srcFile.getPath()+"\"").waitFor();
			} catch (InterruptedException e) {
				throw new IOException(e.getMessage()+" in attrib -r "+srcFile.getPath());
			} catch (IOException e) {
				throw new IOException(e.getMessage()+" in attrib -r "+srcFile.getPath());
			}
		}
		
		if(srcFile.isHidden() || getFileAttr(srcFile).indexOf("S") >= 0){
			try {
				Runtime.getRuntime().exec("attrib -h -s \""+srcFile.getPath()+"\"").waitFor();
			} catch (InterruptedException e) {
				throw new IOException(e.getMessage()+" in attrib -h -s "+srcFile.getPath());
			} catch (IOException e) {
				throw new IOException(e.getMessage()+" in attrib -h -s "+srcFile.getPath());
			}
		}
		
		copyFile(file, srcFile);
		
		return true;
	}
	private String getFileAttr(File f) throws IOException {
		String ret = "";
		Process proc = null;
		try {
			proc = Runtime.getRuntime().exec("attrib \"" + f.getPath()+"\"");
		} catch (IOException e) {
			throw new IOException(e.getMessage()+" in attrib "+f.getPath());
		}
		if(proc == null) return "";
		try {
			InputStream ins = proc.getInputStream();
			byte buf[] = new byte[8];
			int n = ins.read(buf);
			for(int i =0; i < n; i++)
				if(buf[i] == 'A') ret += "A";
				else if(buf[i] == 'S') ret += "S";
				else if(buf[i] == 'H') ret += "H";
				else if(buf[i] == 'R') ret += "R";
			ins.close();
			proc.waitFor();
		} catch (IOException e) {
			throw new IOException(e.getMessage()+" in reading the result of attrib "+f.getPath());
		} catch (InterruptedException e) {
			throw new IOException(e.getMessage()+" in reading the result of attrib "+f.getPath());
		}
		return ret;
	}
	public void doClean(int total) {
		doneN = 0;
		copiedN = 0;
		checkedN = 0;
		cleanDirs(source, total);
		if(!KtymBackup.quiet){
			KtymBackup.logFile.println("Visited directories: "+doneN);
			KtymBackup.logFile.println("Tested Directories:  "+checkedN);
			KtymBackup.logFile.println("Cleaned Directories: "+copiedN);
		}
	}
	private void cleanDirs(File s, int total) {
		if(KtymBackup.verbose)
			KtymBackup.logFile.println("About to "+(KtymBackup.test?"test-clean":"clean")+" '"+s.getPath()+"'...");
		
		File[] files = s.listFiles();
		if(files == null) {
			doneN++;
			return;
		}
		for(int i = 0; i < files.length; i++) {
			File file = files[i];
			try {
				if(file.isDirectory()){
					if(file.exists() && file.getName().equals(BACKUP_DIR_NAME)){
						checkedN++;
						File[] deleteFiles = file.listFiles();
						boolean childDeleteSuccess = true;
						if(deleteFiles != null) {
							for(int j = 0; j < deleteFiles.length; j++){
								if(deleteFiles[j].exists()){
									if(deleteFiles[j].isFile()){
										if(KtymBackup.test){
											continue;
										}
										if(!deleteFiles[j].delete()){
											KtymBackup.logFile.println("Error: file '"+deleteFiles[j].getPath()+"' cannot be deleted.");
											childDeleteSuccess = false;
										}
									} else {
										KtymBackup.logFile.println("Error: '"+deleteFiles[j].getPath()+"' is not a file and cannot be deleted.");
										childDeleteSuccess = false;
									}
								}
							}
						}
						if(childDeleteSuccess) {
							if(KtymBackup.test) {
								KtymBackup.logFile.println("Clean test - about to delete '"+file.getPath()+"'");
								copiedN++;
							} else if(!file.delete()){
								KtymBackup.logFile.println("Error: '"+file.getPath()+"' cannot be deleted.");
							} else {
								copiedN++;
							}
						} else {
							KtymBackup.logFile.println("Error: '"+file.getPath()+"' is not empty and cannot be deleted.");
						}
					}
					
					cleanDirs(file, total);
					
				}
			} catch (Exception e) {
				e.printStackTrace();
				KtymBackup.logFile.println(e.getMessage()+" in '"+file.getName()+"'");
				e.printStackTrace(KtymBackup.logFile);
			}
		}
		
		doneN++;
		if(KtymBackup.verbose)
			KtymBackup.logFile.println("Finished processing '"+s.getPath()+"' ("+(doneN*100/total)+"%)...");
		else if(!KtymBackup.quiet) {
			String str = "Finished - "+(doneN*100/total)+"% '"+s.getPath()+"'                                                %";
			if(str.length() > 76) str = str.substring(0,76);
			System.out.print(str + "\015");
		}		
	}
	///////////////////////// Sync routines ///////////////////////////
	public void doSync(int total) {
		doneN = 0;
		copiedN = 0;
		checkedN = 0;
		getPrevTime();
		try {
			syncDirs(source, target, total);
		} catch (NetworkException e) {
			KtymBackup.logFile.println("Abort by network exception - "+e.getMessage());
			e.printStackTrace();
			e.printStackTrace(KtymBackup.logFile);
		}
		if(!KtymBackup.quiet){
			KtymBackup.logFile.println("Visited directories: "+doneN);
			KtymBackup.logFile.println("Tested Files:        "+checkedN);
			KtymBackup.logFile.println("Sync'ed Files:      "+copiedN);
		}
		setPrevTime();
	}
	private void syncDirs(File s, File t, int total) throws NetworkException {
		if(s == null || t == null) {
			KtymBackup.logFile.println("syncDirs source or target File is null");
			return;
		}
		if(KtymBackup.verbose)
			KtymBackup.logFile.println("About to "+(KtymBackup.test?"test-sync":"sync")+" '"+s.getPath()+"'...");
		
		File[] srcFiles = s.listFiles();
		File[] dstFiles = t.listFiles();
		if(srcFiles == null) {
			srcFiles = new File[0];
		}
		if(dstFiles == null) {
			dstFiles = new File[0];
		}
		Set<String> forceFiles = new HashSet<String>();
		Set<String> ignoreSrcFiles = checkIgnoreFiles(srcFiles, forceFiles);
		Set<String> ignoreDstFiles = checkIgnoreFiles(dstFiles, null /* force 霎滂ｽ｡髫包ｿｽ*/);
		int copiedFilesN = 0;
		int filesN = 0;
		int processDirsN = 0;
		int dirsN = 0;
		//　ソースにあるファイルを処理
		for(int i = 0; i < srcFiles.length; i++) {
			File srcFile = srcFiles[i];
			File dstFile = searchName(srcFile.getName(), t);
			if(dstFile == null) {
				dstFile = new File(t, srcFile.getName());
			}
			
			// check ignore files　（ソースまたはディスティネーションで ignore files のものは無視する。ただし、forceにはいっているものはその限りでない）
			if(!(forceFiles.contains(srcFile.getName().toUpperCase())) && (
					ignoreSrcFiles.contains(srcFile.getName().toUpperCase()) ||
					ignoreDstFiles.contains(dstFile.getName().toUpperCase())
				)){
				if(KtymBackup.verbose){
					KtymBackup.logFile.println("Directory '"+srcFile.getName()+"' was ignored by "+IGNORE_FILES_NAME);
				}
				continue;
			}
			
			//ソースとディスティネーションでファイル・ディレクトリの種別が一致しない場合の処理
			if(dstFile.exists() && 
					((srcFile.isDirectory() && dstFile.isFile()) ||
					(srcFile.isFile() && dstFile.isDirectory()))){
				// srcFile が前回バックアップ後に作成・変更された場合
				// && dstFile が前回バックアップ前に作成・変更された場合
				//   -> dstFileを削除して copy
				//
				if(srcFile.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR && dstFile.lastModified() <= prevStartTime-MACHINE_MILISEC_ERROR){
					if(dstFile.isDirectory()){
						if(!removeDir(dstFile)){
							KtymBackup.logFile.println("cannot delete old dst dir '"+dstFile.getName()+"' due to change to file...ignored");
							continue;
						}
						try {
							if(!dstFile.createNewFile()){
								KtymBackup.logFile.println("cannot create new dst file '"+dstFile.getName()+"' due to change to file...ignored");
								continue;
							}
						} catch (IOException e) {
							KtymBackup.logFile.println("cannot create new dst file '"+dstFile.getName()+"' due to change to file (IOException)...ignored");
							continue;
						}
					} else {
						if(!removeFile(dstFile)){
							KtymBackup.logFile.println("cannot delete old dst file '"+dstFile.getName()+"' due to change to directory...ignored");
							continue;
						}
						if(!dstFile.mkdir()){
							KtymBackup.logFile.println("cannot create new dst directory '"+dstFile.getName()+"' due to change to directory...ignored");
							continue;
						}
					}
				}
				// srcFile　が前回バックアップ前に作成・変更された場合
				// && dstFile が前回バックアップ後に作成・変更された場合
				//   -> src dirを削除してtarget から copy
				//
				else if(srcFile.lastModified() <= prevStartTime-MACHINE_MILISEC_ERROR && dstFile.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR){
					if(srcFile.isDirectory()){
						if(!removeDir(srcFile)){
							KtymBackup.logFile.println("cannot delete old src dir '"+srcFile.getName()+"' due to change to file...ignored");
							continue;
						}
						try {
							if(!srcFile.createNewFile()){
								KtymBackup.logFile.println("cannot create new src file '"+srcFile.getName()+"' due to change to file...ignored");
								continue;
							}
						} catch (IOException e) {
							KtymBackup.logFile.println("cannot create new src file '"+srcFile.getName()+"' due to change to file (IOException)...ignored");
							continue;
						}
					} else {
						if(!removeFile(srcFile)){
							KtymBackup.logFile.println("cannot delete old src file '"+srcFile.getName()+"' due to change to directory...ignored");
							continue;
						}
						if(!srcFile.mkdir()){
							KtymBackup.logFile.println("cannot create new src directory '"+srcFile.getName()+"' due to change to directory...ignored");
							continue;
						}
					}
				} 
				// srcFileの親ディレクトリが前回バックアップ後に更新されていない
				// && dstFileの親ディレクトリが前回バックアップ後に更新されている
				//   --> srcFile を削除し、dstFileからコピー
				else if(s.lastModified()<= prevStartTime-MACHINE_MILISEC_ERROR  && t.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR){
					if(srcFile.isDirectory()){
						if(!removeDir(srcFile)){
							KtymBackup.logFile.println("cannot delete old src dir '"+srcFile.getName()+"' due to change to file...ignored");
							continue;
						}
						try {
							if(!srcFile.createNewFile()){
								KtymBackup.logFile.println("cannot create new src file '"+srcFile.getName()+"' due to change to file...ignored");
								continue;
							}
						} catch (IOException e) {
							KtymBackup.logFile.println("cannot create new src file '"+srcFile.getName()+"' due to change to file (IOException)...ignored");
							continue;
						}
					} else {
						if(!removeFile(srcFile)){
							KtymBackup.logFile.println("cannot delete old src file '"+srcFile.getName()+"' due to change to directory...ignored");
							continue;
						}
						if(!srcFile.mkdir()){
							KtymBackup.logFile.println("cannot create new src directory '"+srcFile.getName()+"' due to change to directory...ignored");
							continue;
						}
					}
				}
				// srcFileの親ディレクトリが前回バックアップ後に更新されている
				// && dstFileの親ディレクトリが前回バックアップ後に更新されていない
				//   --> dstFile を削除し、srcFileからコピー
				else if(s.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR && t.lastModified() <= prevStartTime-MACHINE_MILISEC_ERROR){
					if(dstFile.isDirectory()){
						if(!removeDir(dstFile)){
							KtymBackup.logFile.println("cannot delete old dst dir '"+dstFile.getName()+"' due to change to file...ignored");
							continue;
						}
						try {
							if(!dstFile.createNewFile()){
								KtymBackup.logFile.println("cannot create new dst file '"+dstFile.getName()+"' due to change to file...ignored");
								continue;
							}
						} catch (IOException e) {
							KtymBackup.logFile.println("cannot create new dst file '"+dstFile.getName()+"' due to change to file (IOException)...ignored");
							continue;
						}
					} else {
						if(!removeFile(dstFile)){
							KtymBackup.logFile.println("cannot delete old dst file '"+dstFile.getName()+"' due to change to directory...ignored");
							continue;
						}
						if(!dstFile.mkdir()){
							KtymBackup.logFile.println("cannot create new dst directory '"+dstFile.getName()+"' due to change to directory...ignored");
							continue;
						}
					}
				}
				
				// srcFileの親ディレクトリが前回バックアップ後に更新されている
				// && dstFileの親ディレクトリが前回バックアップ後に更新されている
				//    --> 同時に異なるマシンで変更が行われたので無視する。警告をだす。
				else if(s.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR && t.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR){
					KtymBackup.logFile.println("newly created srcFile '"+srcFile.getName()+"' is a directory, but the cooresponding dstFile is not a directory and also newly created...ignored");
					continue;
				}
				
				// srcFileの親ディレクトリが前回バックアップ後に更新されていない
				// && dstFileの親ディレクトリが前回バックアップ後に更新されていない
				//   src: Adir -> Bdir -> Cdir
				//   dst: Ddir -> Bdir -> Cfile
				//   のとき、dst で DdirをAdirにmoveした場合などに相当。
				//   --> 先祖のディレクトリを比較して上記と同様に処理
				else {
					File ss = s.getParentFile();
					File tt = t.getParentFile();
					while (ss != null && tt != null && 
						ss.lastModified()<= prevStartTime-MACHINE_MILISEC_ERROR  && tt.lastModified() <= prevStartTime-MACHINE_MILISEC_ERROR){
							ss = ss.getParentFile();
							tt = tt.getParentFile();
						}
					if(ss == null || tt == null) {
						KtymBackup.logFile.println("srcFile '"+srcFile.getName()+"' is a directory, but the cooresponding dstFile is not a directory and also the parent directories have no update information...ignored");
						continue;
					}
					// 先祖ディレクトリに更新情報が見つからない。（トップレベルのディレクトリまでタイムスタンプが更新されていない)
					else if(ss.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR && tt.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR){
						KtymBackup.logFile.println("newly created srcFile '"+srcFile.getName()+"' is a directory, but the cooresponding dstFile is not a directory and also newly created...ignored");
						continue;
					}
					
					// srcFileの先祖ディレクトリが前回バックアップ後に更新されていない
					// && dstFileの先祖ディレクトリが前回バックアップ後に更新されている
					//   --> srcFile を削除し、dstFileからコピー
					else if(ss.lastModified() <= prevStartTime-MACHINE_MILISEC_ERROR && tt.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR){
						if(srcFile.isDirectory()){
							if(!removeDir(srcFile)){
								KtymBackup.logFile.println("cannot delete old src dir '"+srcFile.getName()+"' due to change to file...ignored");
								continue;
							}
							try {
								if(!srcFile.createNewFile()){
									KtymBackup.logFile.println("cannot create new src file '"+srcFile.getName()+"' due to change to file...ignored");
									continue;
								}
							} catch (IOException e) {
								KtymBackup.logFile.println("cannot create new src file '"+srcFile.getName()+"' due to change to file (IOException)...ignored");
								continue;
							}
						} else {
							if(!removeFile(srcFile)){
								KtymBackup.logFile.println("cannot delete old src file '"+srcFile.getName()+"' due to change to directory...ignored");
								continue;
							}
							if(!srcFile.mkdir()){
								KtymBackup.logFile.println("cannot create new src directory '"+srcFile.getName()+"' due to change to directory...ignored");
								continue;
							}
						}
					}
					// srcFileの先祖ディレクトリが前回バックアップ後に更新されている
					// && dstFileの先祖ディレクトリが前回バックアップ後に更新されていない
					//   --> dstFile を削除し、srcFileからコピー
					else if(ss.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR && tt.lastModified() <= prevStartTime-MACHINE_MILISEC_ERROR){
						if(dstFile.isDirectory()){
							if(!removeDir(dstFile)){
								KtymBackup.logFile.println("cannot delete old dst dir '"+dstFile.getName()+"' due to change to file...ignored");
								continue;
							}
							try {
								if(!dstFile.createNewFile()){
									KtymBackup.logFile.println("cannot create new dst file '"+dstFile.getName()+"' due to change to file...ignored");
									continue;
								}
							} catch (IOException e) {
								KtymBackup.logFile.println("cannot create new dst file '"+dstFile.getName()+"' due to change to file (IOException)...ignored");
								continue;
							}
						} else {
							if(!removeFile(dstFile)){
								KtymBackup.logFile.println("cannot delete old dst file '"+dstFile.getName()+"' due to change to directory...ignored");
								continue;
							}
							if(!dstFile.mkdir()){
								KtymBackup.logFile.println("cannot create new dst directory '"+dstFile.getName()+"' due to change to directory...ignored");
								continue;
							}
						}
					}
					else {
						// ここにはこないはず！
						KtymBackup.logFile.println("ERROR!!!");
						continue;
					}
				}
			}
			try {
				if(srcFile.isDirectory()){
					
					dirsN++;
					if(srcFile.getName().equalsIgnoreCase(BACKUP_DIR_NAME))
						continue;
					if(!dstFile.exists()){
						// srcFile が前回バックアップ後に更新されている場合は dstFile作成
						if(srcFile.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR){
							dstFile.mkdir();
						} else {
							// 前回バックアップ後にターゲットが削除された
							//  --> srcFile を削除
							if(!removeDir(srcFile)){
								KtymBackup.logFile.println("cannot delete old src dir '"+srcFile.getName()+"' due to change to file...ignored");
								
							} else {
								KtymBackup.logFile.println("deleted old src dir '"+srcFile.getName()+"' due to target removal");
								
							}
							continue;
						}
					}
					if(syncDirectory(s, srcFile, dstFile, t, total)){
						processDirsN++;
					}
				} else if(srcFile.isFile()) {
					if(syncFile(s, srcFile, dstFile, t)) {
						copiedFilesN++;
						copiedN++;
					}
					checkedN++;
					filesN++;
				} else {
					throw new IOException("(Internal)DirectoryBackup::syncDirs - Dir members not directory nor files...");
				}
			} catch (NetworkException e){
				throw e;
			} catch (IOException e) {
				e.printStackTrace();
				KtymBackup.logFile.println(e.getMessage()+" in sync'ing '"+srcFile.getPath()+"' and '"+dstFile.getPath()+"'");
				e.printStackTrace(KtymBackup.logFile);
				if(e.getMessage().contains("ネットワーク")){
					throw new NetworkException(e.getMessage());
				}
			}
		}
		
		// ターゲットにあるがソースにないものだけをサーチ
		for(int i = 0; i < dstFiles.length; i++) {
			File dstFile = dstFiles[i];
			
			// check ignore files
			if(ignoreDstFiles.contains(dstFile.getName())){
				if(KtymBackup.verbose){
					KtymBackup.logFile.println("Directory '"+dstFile.getName()+"' was ignored by "+IGNORE_FILES_NAME);
				}
				continue;
			}
			
			File srcFile = searchName(dstFile.getName(), s);
			if(srcFile == null) {
				srcFile = new File(s, dstFile.getName());
				try {
					if(dstFile.isDirectory()){
						// 前回バックアップ後にターゲット(dstFile) が変更されていない
						//   --> srcFileが削除されたと考えて、dstFileを削除
						if(dstFile.lastModified() <= prevStartTime-MACHINE_MILISEC_ERROR){
							// 間違って削除されるので中止
							//  下記ファイルの場合例を参照
							KtymBackup.logFile.println("Determined but not executed (temp) - as Dst dir '"+dstFile.getName()+"' was deleted due to remove of srcFile");
							/*
							if(!removeDir(dstFile)){
								KtymBackup.logFile.println("cannot delete dst dir '"+dstFile.getName()+"' by remove of srcFile");
							} else {
								if(KtymBackup.verbose){
									KtymBackup.logFile.println("Dst dir '"+dstFile.getName()+"' was deleted due to remove of srcFile");
								}
							}
							*/
							continue;
						}
						dirsN++;
						if(dstFile.getName().toUpperCase().equals(BACKUP_DIR_NAME.toUpperCase()))
							continue;
						srcFile.mkdir();
						if(syncDirectory(s, srcFile, dstFile, t, total)){
							processDirsN++;
						}
					} else if(dstFile.isFile()) {
						// 前回バックアップ後にターゲット(dstFile) が変更されていない
						//   --> srcFileが削除されたと考えて、dstFileを削除
						if(dstFile.lastModified() <= prevStartTime-MACHINE_MILISEC_ERROR && getFileAttr(dstFile).indexOf("A") < 0){
							
							// TODO ディスティネーションしかないものを間違って削除することがおおいので、とりあえず中止
							// ログにのこす
							//  machine B
							//      srcFile作成　：　TIME T0
							//  machine A sync　：　TIME　T1 > T0
							//       (srcFile なし)   --->    （dstFile なし）
							//  machine B sync　：　TIME　T2 ( > T1)
							//       srcFile あり   --->    dstFile 作成 (作成時刻T0）
							//  machine A sync : TIME T3 ( > T2 > T1)
							//       (srcFile なし） --->    dstFile あり
							//  ===> dstFile 作成時刻(=T0) < 前回SYNC時刻 (=T1)なので間違って削除！
							//  
							KtymBackup.logFile.println("Determined but not executed (temp) - as Dst file '"+dstFile.getName()+"' was deleted due to remove of srcFile");
							/*
							if(!removeFile(dstFile)){
								KtymBackup.logFile.println("cannot delete dst file '"+dstFile.getName()+"' by remove of srcFile");
							} else {
								if(KtymBackup.verbose){
									KtymBackup.logFile.println("Dst file '"+dstFile.getName()+"' was deleted due to remove of srcFile");
								}
							}
							*/
							continue;
						}
						if(syncFile(s, srcFile, dstFile, t)) {
							copiedFilesN++;
							copiedN++;
						}
						checkedN++;
						filesN++;
					}
				} catch (NetworkException e) {
					throw e;
				} catch (IOException e) {
					e.printStackTrace();
					KtymBackup.logFile.println(e.getMessage()+" in sync'ing '"+srcFile.getPath()+"' and '"+dstFile.getPath()+"'");
					e.printStackTrace(KtymBackup.logFile);
					if(e.getMessage().contains("ネットワーク")){
						throw new NetworkException(e.getMessage());
					}
				}
			}
		}
		
		doneN++;
		if(KtymBackup.verbose)
			KtymBackup.logFile.println("Finished processing '"+t.getPath()+"' ("+(doneN*100/total)+"%)... #"+(KtymBackup.test?"tested-to-copy":"copied")+" files "+copiedFilesN+"/"+filesN+", #processed directories:"+processDirsN+"/"+dirsN);
		else if(!KtymBackup.quiet) {
			String str = "Finished - "+(doneN*100/total)+"% '"+t.getPath()+"' "+copiedFilesN+"/"+filesN+"                                               %";
			if(str.length() > 76) str = str.substring(0,76);
			System.out.print(str + "\015");
		}		
	}
	private boolean removeFile(File file) {
		File bkupDir = new File(file.getParentFile(), REMOVEDDIR);
		if(!bkupDir.exists()){
			bkupDir.mkdir();
			try {
				Runtime.getRuntime().exec("attrib +h \""+bkupDir.getPath()+"\"").waitFor();
			} catch (InterruptedException e) {
				KtymBackup.logFile.println(e.getMessage()+" during attrib +h "+bkupDir.getPath());
				return false;
			} catch (IOException e) {
				KtymBackup.logFile.println(e.getMessage()+" during attrib +h "+bkupDir.getPath());
				return false;
			} 
		} else if(bkupDir.isFile()){
			KtymBackup.logFile.println("Dir '"+bkupDir.getPath()+"' exists as file");
			if(!bkupDir.delete()){
				KtymBackup.logFile.println("cannot delete file '"+bkupDir.getPath()+"' for backup dir for removing '"+file.getPath()+"' -- ignored");
				return false;
			}
			bkupDir.mkdir();
		} else if(!bkupDir.isDirectory()){
			KtymBackup.logFile.println("cannot setup backup dir '"+bkupDir.getPath()+"' for backup dir for removing '"+file.getPath()+"' -- ignored");
			return false;
		}
		File bkupFile = new File(bkupDir, file.getName());
		try {
			copyFile(file, bkupFile);
		} catch (IOException e) {
			KtymBackup.logFile.println("IOExceptino during backup file '"+bkupDir.getPath()+"' for backup dir for removing '"+file.getPath()+"' -- ignored");
			return false;
		}
		return file.delete();
	}
	private boolean removeDir(File dir) {
		File bkupDir = new File(dir.getParentFile(), REMOVEDDIR);
		if(!bkupDir.exists()){
			bkupDir.mkdir();
			try {
				Runtime.getRuntime().exec("attrib +h \""+bkupDir.getPath()+"\"").waitFor();
			} catch (InterruptedException e) {
				KtymBackup.logFile.println(e.getMessage()+" during attrib +h "+bkupDir.getPath());
				return false;
			} catch (IOException e) {
				KtymBackup.logFile.println(e.getMessage()+" during attrib +h "+bkupDir.getPath());
				return false;
			} 
		} else if(bkupDir.isFile()){
			KtymBackup.logFile.println("Dir '"+bkupDir.getPath()+"' exists as file");
			if(!bkupDir.delete()){
				KtymBackup.logFile.println("cannot delete file '"+bkupDir.getPath()+"' for backup dir for removing '"+dir.getPath()+"' -- ignored");
				return false;
			}
			bkupDir.mkdir();
		} else if(!bkupDir.isDirectory()){
			KtymBackup.logFile.println("cannot setup backup dir '"+bkupDir.getPath()+"' for backup dir for removing '"+dir.getPath()+"' -- ignored");
			return false;
		}
		if(! removeDir(dir, bkupDir) ) return false;
		return dir.delete();
	}
	
	private boolean removeDir(File dir, File bkupDir){
		File newBkupDir = new File(bkupDir, dir.getName());
		if(!newBkupDir.exists()){
			newBkupDir.mkdir();
		} else if(newBkupDir.isFile()){
			KtymBackup.logFile.println("Dir '"+newBkupDir.getPath()+"' exists as file");
			if(!newBkupDir.delete()){
				KtymBackup.logFile.println("cannot delete file '"+newBkupDir.getPath()+"' for backup dir for removing '"+dir.getPath()+"' -- ignored");
				return false;
			}
			newBkupDir.mkdir();
		} else if(!newBkupDir.isDirectory()){
			KtymBackup.logFile.println("cannot setup backup dir '"+newBkupDir.getPath()+"' for backup dir for removing '"+dir.getPath()+"' -- ignored");
			return false;
		} else if(!dir.canRead()){
			KtymBackup.logFile.println("cannot read backup dir '"+dir.getPath()+"' -- ignored");
			return false;
		}
		// remove all members
		File[] listFiles = dir.listFiles();
		if(listFiles == null) return false;
		for(File child : listFiles){
			if(child.exists()){
				if(child.isDirectory()){
					
					if(!removeDir(child, newBkupDir)) return false;

				} else if(child.isFile()){
					File bkupFile = new File(newBkupDir, child.getName());
					if(bkupFile.exists()){
						if(bkupFile.isDirectory()){
							KtymBackup.logFile.println("backup file '"+bkupFile.getPath()+"' exists as file for remove dir -- ignored");
							//return false;
							continue;
						}
					}
					try {
						copyFile(child, bkupFile);
					} catch (IOException e) {
						KtymBackup.logFile.println("failed to do backup file '"+bkupFile.getPath()+"' for remove");
						e.printStackTrace();
					}
					
				} else return false;
				
				if(!child.delete()) return false;
				
			}
		}
		return true;
	}
	private boolean syncDirectory(File srcParent, File srcDir, File dstDir, File dstParent, int total) throws IOException {

		if(srcDir == null && dstDir == null) {
			throw new IOException("(Internal) syncDirectory srcDir==null && dstDir==null --- break");
		}
		if(srcDir == null) {
			if(!KtymBackup.quiet)
				KtymBackup.logFile.println("Warning: sync src dir '"+dstDir.getName()+"' does not exist in '"+srcParent.getPath()+"'.");
			if(KtymBackup.test){
				KtymBackup.logFile.println("Mkdir '"+dstDir.getName()+"' in '"+srcParent.getPath()+"'.");
				return false;
			}
			srcDir = new File(srcParent, dstDir.getName());
			srcDir.mkdir();
			if(srcDir.isHidden())
				try {
					Runtime.getRuntime().exec("attrib +h \""+srcDir.getPath()+"\"").waitFor();
				} catch (InterruptedException e) {
					throw new IOException(e.getMessage()+" in attrib +h "+srcDir.getPath());
				} catch (IOException e) {
					throw new IOException(e.getMessage()+" in attrib +h "+srcDir.getPath());
				} 
		}
		if(!srcDir.isDirectory()){
			throw new IOException("about to sync src dir '"+srcDir.getName()+"',"+
							  "but the same name non-dir file exists in '"+srcParent.getPath()+"'");
		}
		if(dstDir == null) {
			if(!KtymBackup.quiet)
				KtymBackup.logFile.println("Warning: sync dst dir '"+srcDir.getName()+"' does not exist in '"+dstParent.getPath()+"'.");
			if(KtymBackup.test){
				KtymBackup.logFile.println("Mkdir '"+srcDir.getName()+"' in '"+dstParent.getPath()+"'.");
				return false;
			}
			dstDir = new File(dstParent, srcDir.getName());
			dstDir.mkdir();
			if(dstDir.isHidden())
				try {
					Runtime.getRuntime().exec("attrib +h \""+dstDir.getPath()+"\"").waitFor();
				} catch (InterruptedException e) {
					throw new IOException(e.getMessage()+" in attrib +h "+dstDir.getPath());
				} catch (IOException e) {
					throw new IOException(e.getMessage()+" in attrib +h "+dstDir.getPath());
				} 
		}
		if(!dstDir.isDirectory()){
			throw new IOException("about to sync dst dir '"+dstDir.getName()+"',"+
							  "but the same name non-dir file exists in '"+dstParent.getPath()+"'");
		}
		if(!srcDir.canRead()||!dstDir.canRead()){
			throw new IOException("cannot read dir '"+srcDir.getPath()+"' or '"+dstDir.getPath()+"'.");
		}
		String attr = getFileAttr(srcDir);
		String args ="-a ";
		//if(attr.indexOf("A")>=0) args += "+a ";
		if(attr.indexOf("S")>=0) args += "+S ";
		if(attr.indexOf("H")>=0) args += "+h ";
		if(args.length() > 0) {
			try {
				Runtime.getRuntime().exec("attrib "+args+"\""+srcDir.getPath()+"\"").waitFor();
			} catch (InterruptedException e) {
				throw new IOException(e.getMessage()+" in attrib "+args+srcDir.getPath());
			} catch (IOException e) {
				throw new IOException(e.getMessage()+" in attrib "+args+srcDir.getPath());
			}
		} 
		attr = getFileAttr(dstDir);
		args ="-a ";
		//if(attr.indexOf("A")>=0) args += "+a ";
		if(attr.indexOf("S")>=0) args += "+S ";
		if(attr.indexOf("H")>=0) args += "+h ";
		if(args.length() > 0) {
			try {
				Runtime.getRuntime().exec("attrib "+args+"\""+dstDir.getPath()+"\"").waitFor();
			} catch (InterruptedException e) {
				throw new IOException(e.getMessage()+" in attrib "+args+dstDir.getPath());
			} catch (IOException e) {
				throw new IOException(e.getMessage()+" in attrib "+args+dstDir.getPath());
			}
		} 
		
		syncDirs(srcDir, dstDir, total);
		
		return true;
	}
	private boolean syncFile(File srcParent, File srcFile, File dstFile, File dstParent) throws IOException {
		if(srcFile == null && dstFile == null) {
			KtymBackup.logFile.println("(Internal) syncFile --- srcFile == null && dstFile == null");
			return false;
		}
		if(srcFile == null) {
			srcFile = new File(srcParent, dstFile.getName());
		}
		if(dstFile == null) {
			dstFile = new File(dstParent, srcFile.getName());
		}
		if(!srcFile.exists()) {
			if(KtymBackup.verbose) KtymBackup.logFile.println("Original file '"+srcFile.getName()+"' does not exist in '"+srcParent.getPath()+"'.");
			// 前回バックアップより dstFile が新しければ srcFile を作成
			if(dstFile.lastModified() >= prevEndTime+MACHINE_MILISEC_ERROR  || getFileAttr(dstFile).indexOf("A")>=0){
				if(KtymBackup.test) {
					KtymBackup.logFile.println("Tested to create a new file '"+srcFile.getName()+"' in '"+srcParent.getPath()+"'.");
					return true;
				}
				if(!srcFile.createNewFile())
					throw new IOException("Cannot create a file '"+srcFile.getPath()+"'");
				copyFile(dstFile, srcFile);
			}
			// 前回バックアップより dstFile が古く、かつ、AflagがONでなければ dstFile を削除
			else if (getFileAttr(dstFile).indexOf("A")<0){	
				if(!dstFile.canWrite()){
					try {
						Runtime.getRuntime().exec("attrib -r \""+dstFile.getPath()+"\"").waitFor();
					} catch (InterruptedException e) {
						throw new IOException(e.getMessage()+" during attrib -r "+dstFile.getPath());
					} catch (IOException e) {
						throw new IOException(e.getMessage()+" during attrib -r "+dstFile.getPath());
					}
				}
				
				if(dstFile.isHidden() || getFileAttr(dstFile).indexOf("S")>=0) {
					try {
						Runtime.getRuntime().exec("attrib -h -s \""+dstFile.getPath()+"\"").waitFor();
					} catch (InterruptedException e) {
						throw new IOException(e.getMessage()+" during attrib -h -s "+dstFile.getPath());
					} catch (IOException e) {
						throw new IOException(e.getMessage()+" during attrib -h -s "+dstFile.getPath());
					}
				}
				if(!removeFile(dstFile)){
					return false;
				}
			}
			return true;
		}
		if(!dstFile.exists()) {
			if(KtymBackup.verbose) KtymBackup.logFile.println("Original file '"+dstFile.getName()+"' does not exist in '"+dstParent.getPath()+"'.");
			if(KtymBackup.test) {
				KtymBackup.logFile.println("Tested to create a new file '"+dstFile.getName()+"' in '"+dstParent.getPath()+"'.");
				return true;
			}
			// 前回バックアップより srcFile が新しければ dstFile を作成
			if(srcFile.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR || getFileAttr(srcFile).indexOf("A")>=0){
				if(!dstFile.createNewFile())
					throw new IOException("Cannot create a file '"+dstFile.getPath()+"'");
				copyFile(srcFile, dstFile);
			}
			// 前回バックアップより srcFile が古く、かつ、AflagがONでなければsrcFile を削除
			else if (getFileAttr(srcFile).indexOf("A")<0){
				
				if(!srcFile.canWrite()){
					try {
						Runtime.getRuntime().exec("attrib -r \""+srcFile.getPath()+"\"").waitFor();
					} catch (InterruptedException e) {
						throw new IOException(e.getMessage()+" during attrib -r "+srcFile.getPath());
					} catch (IOException e) {
						throw new IOException(e.getMessage()+" during attrib -r "+srcFile.getPath());
					}
				}
				
				if(srcFile.isHidden() || getFileAttr(srcFile).indexOf("S")>=0) {
					try {
						Runtime.getRuntime().exec("attrib -h -s \""+srcFile.getPath()+"\"").waitFor();
					} catch (InterruptedException e) {
						throw new IOException(e.getMessage()+" during attrib -h -s "+srcFile.getPath());
					} catch (IOException e) {
						throw new IOException(e.getMessage()+" during attrib -h -s "+srcFile.getPath());
					}
				}
				if(!removeFile(srcFile)){
					return false;
				}
			}

			return true;
		}
		if(!srcFile.isFile() || !dstFile.isFile()){
			throw new IOException("Cannot sync a directory '"+srcFile.getPath()+"' or '"+dstFile.getPath()+"'."); 
		}
		if(srcFile.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR){
			if(dstFile.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR){
				if(prevEndTime == 0){
					if(srcFile.lastModified() > dstFile.lastModified()+MACHINE_MILISEC_ERROR){
						if(!removeFile(dstFile)) return false;
						copyFile(srcFile, dstFile);
						return true;
					} else if(srcFile.lastModified() < dstFile.lastModified()-MACHINE_MILISEC_ERROR){
						if(!removeFile(srcFile)) return false;
						copyFile(dstFile, srcFile);
						return true;
					}
					return false;
				} else {
					KtymBackup.logFile.println("Src file '"+srcFile.getPath()+"' and dst file '"+dstFile.getPath()+"' were NOT modified after previous sync... ignored.");
					return false;
				}
			} else if(dstFile.lastModified() < prevStartTime-MACHINE_MILISEC_ERROR){
				if(!removeFile(dstFile)) return false;
				copyFile(srcFile, dstFile);
				return true;
			}
		} else {
			if(dstFile.lastModified() > prevEndTime+MACHINE_MILISEC_ERROR){
				if(!removeFile(srcFile)) return false;
				copyFile(dstFile, srcFile);
				return true;
			} else {
				if(KtymBackup.verbose)
					KtymBackup.logFile.println("Src file '"+srcFile.getPath()+"' and dst file '"+dstFile.getPath()+"' were NOT modified after previous sync... ignored.");
				return false;
			}
		}
		return true;
	}
	
	private void copyFile(File srcFile, File dstFile) throws IOException {
		/*
		OutputStream out = new FileOutputStream(dstFile);
		InputStream in = new FileInputStream(srcFile);
		byte[] buf = new byte[4096];
		int n = 0;
		do {
			n = in.read(buf);
			if(n > 0) out.write(buf, 0, n);
		} while(n > 0);
		out.flush();
		out.close();
		in.close();
		
		dstFile.setLastModified(dstFile.lastModified());
		if(!srcFile.canWrite()) dstFile.setReadOnly();
		String attr = getFileAttr(srcFile);
		String args ="";
		if(attr.indexOf("A")>=0) args += "+a ";
		if(attr.indexOf("S")>=0) args += "+S ";
		if(attr.indexOf("H")>=0) args += "+h ";
		if(args.length() > 0) {
			try {
				Runtime.getRuntime().exec("attrib "+args+"\""+dstFile.getPath()+"\"").waitFor();
			} catch (InterruptedException e) {
				throw new IOException(e.getMessage()+" in attrib "+args+dstFile.getPath());
			} catch (IOException e) {
				throw new IOException(e.getMessage()+" in attrib "+args+dstFile.getPath());
			}
		} 
		*/
		if(srcFile.getName().equals(".kb.removed")){
			return;
		}
		try {
			//Runtime.getRuntime().exec("cmd /C copy /B/Y/L \""+srcFile.getPath()+"\" \""+dstFile.getPath()+"\"").waitFor();
			Runtime.getRuntime().exec("cmd /C copy /B/Y \""+srcFile.getPath()+"\" \""+dstFile.getPath()+"\"").waitFor();
			clearAFlag(srcFile);
			clearAFlag(dstFile);
		} catch (InterruptedException e) {
			throw new IOException(e.getMessage()+" in copy \""+srcFile.getPath()+"\" \""+dstFile.getPath()+"\"");
		} catch (IOException e) {
			throw new IOException(e.getMessage()+" in copy \""+srcFile.getPath()+"\" \""+dstFile.getPath()+"\"");
		}
	}
	
	private void clearAFlag(File file) throws IOException{
		try {
			Runtime.getRuntime().exec("attrib -a \""+file.getPath()+"\"").waitFor();
		} catch (InterruptedException e) {
			throw new IOException(e.getMessage()+" in attrib -a "+file.getPath());
		} catch (IOException e) {
			throw new IOException(e.getMessage()+" in attrib -a "+file.getPath());
		}
	}

	// common
	// prevTime load/save
	private File getPrevTimeFile() {
		// source '/' '.kb.timestamp." + target's hash
		return new File(source, TIMESTAMPFILE+"."+target.hashCode());
	}
	private void getPrevTime(){

		prevStartTime = 0L;
		prevEndTime = 0L;
		logLines =new Vector<String>();
		File pfile = getPrevTimeFile();
		if(!pfile.exists()) {
			return;
		}
		
		FileReader st = null;
		try {
			st = new FileReader(pfile);
			BufferedReader rdr = new BufferedReader(st);
			String line = rdr.readLine();
			String f[] = line.split("[,]");
			if(f.length >=1){
				prevStartTime = Long.parseLong(f[0]);
				if(f.length > 1)
					prevEndTime = Long.parseLong(f[1]);
				else prevEndTime = prevStartTime + 60000 * 60 * 4;
			}
			while((line = rdr.readLine())!=null){
				logLines.add(line);
			}
			rdr.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			KtymBackup.logFile.println(e.getMessage()+" for opening '"+pfile.getPath()+"'");
			e.printStackTrace(KtymBackup.logFile);
		} catch (IOException e) {
			e.printStackTrace();
			KtymBackup.logFile.println(e.getMessage()+" in reading '"+pfile.getPath()+"'");
			e.printStackTrace(KtymBackup.logFile);
		} catch (NumberFormatException e) {
			e.printStackTrace();
			KtymBackup.logFile.println(e.getMessage()+" in reading '"+pfile.getPath()+"'");
			e.printStackTrace(KtymBackup.logFile);
		} finally {
			try {
				if(st != null) st.close();
			} catch (IOException e) {
				e.printStackTrace();
				KtymBackup.logFile.println(e.getMessage()+" in closing '"+pfile.getPath()+"'");
				e.printStackTrace(KtymBackup.logFile);
			}
		}
		Date curDateObj = new Date();
		curStartTime = curDateObj.getTime();
	}
	private void setPrevTime() {
		File pfile = getPrevTimeFile();
		Date curDateObj = new Date();
		long curEndTime = curDateObj.getTime();
		logLines.add(DateFormat.getDateTimeInstance().format(curDateObj));
		if(pfile.exists()) {
			pfile.delete();
		}
		PrintWriter pw;
		try {
			pw = new PrintWriter(pfile);
			pw.println(""+curStartTime+","+curEndTime);
			for(String line : logLines){
				pw.println(line);
			}
			pw.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			KtymBackup.logFile.println(e.getMessage()+" for opening '"+pfile.getPath()+"'");
			e.printStackTrace(KtymBackup.logFile);
		}
	}
}
