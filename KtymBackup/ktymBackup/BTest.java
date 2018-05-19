package ktymBackup;

import java.io.File;
import java.text.DateFormat;

public class BTest {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		// TODO Auto-generated method stub
		File f = new File ("J:\\tmp\\test2");
		if(!f.exists()) System.out.println("not exist");
		else {
			System.out.println("lastModified="+DateFormat.getDateTimeInstance().format(f.lastModified()));
			System.out.println("hash="+f.hashCode());
		}
		File p = f.getParentFile();
		System.out.println("the above parent = "+((p!=null)?p.getPath():"null"));
		if(p != null) {
			p = p.getParentFile();
			System.out.println("the above parent = "+((p!=null)?p.getPath():"null"));
			if(p != null) {
				p = p.getParentFile();
				System.out.println("the above parent = "+((p!=null)?p.getPath():"null"));
			}
		}
	}

}
