package winsat;

import java.awt.BorderLayout;
import java.awt.Container;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;

public class WinSAT extends JFrame {
	
	private final static String TITLE = "WinSAT";
	private final static String satPath = "C:/Windows/Performance/WinSAT/DataStore";
	
	private JLabel text0;
	private JLabel text1;
	private JLabel text2;
	private JLabel text3;
	private JLabel text4;
	private JLabel text5;
	private JLabel text6;
	
	private String systemScore;
	private String cpuScore;
	private String memoryScore;
	private String graphicsScore;
	private String gamingScore;
	private String diskScore;
			
	static public void main(String[] argv) {
		WinSAT frame = new WinSAT();
		frame.setBounds(100, 100, 350, 160);
	    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	    frame.setVisible(true);
	    
	   frame.start();
	}
	public WinSAT() {
		super(TITLE);
	}
	
	private void start() {
		Container pane = getContentPane();
		makeWindow(pane);
		
		display();
		
	}
	
	private void makeWindow(Container p){
		
		JPanel l = new JPanel();
		GridLayout layout = new GridLayout(0, 2);
	    l.setLayout(layout);
	    JLabel label = new JLabel("Date");
	    label.setSize(80, 16);
	    text0 = new JLabel("");
	    text0.setSize(80, 16);
	    l.add(label, BorderLayout.LINE_START);
	    l.add(text0, BorderLayout.WEST);
	    
	    label = new JLabel("System");
	    label.setSize(80, 16);
	    text1 = new JLabel("");
	    text1.setSize(40, 16);
	    l.add(label, BorderLayout.LINE_START);
	    l.add(text1, BorderLayout.WEST);
	    
	    label = new JLabel("CPU");
	    label.setSize(80, 16);
	    text2 = new JLabel("");
	    text2.setSize(40, 16);
	    l.add(label, BorderLayout.LINE_START);
	    l.add(text2, BorderLayout.WEST);
	    
	    label = new JLabel("Memory");
	    label.setSize(80, 16);
	    text3 = new JLabel("");
	    text3.setSize(40, 16);
	    l.add(label, BorderLayout.LINE_START);
	    l.add(text3, BorderLayout.WEST);
	    
	    label = new JLabel("Graphics");
	    label.setSize(80, 16);
	    text4 = new JLabel("");
	    text4.setSize(40, 16);
	    l.add(label, BorderLayout.LINE_START);
	    l.add(text4, BorderLayout.WEST);
	    
	    label = new JLabel("Gaming");
	    label.setSize(80, 16);
	    text5 = new JLabel("");
	    text5.setSize(40, 16);
	    l.add(label, BorderLayout.LINE_START);
	    l.add(text5, BorderLayout.WEST);
	    
	    label = new JLabel("Disk");
	    label.setSize(80, 16);
	    text6 = new JLabel("");
	    text6.setSize(40, 16);
	    l.add(label, BorderLayout.LINE_START);
	    l.add(text6, BorderLayout.WEST);
	    
	    label = new JLabel("");
	    label.setSize(80, 16);
	    JButton recalcBtn = new JButton("再計算");
	    final JFrame frm = this;
	    recalcBtn.addActionListener(
	    	      new ActionListener(){
	    	          public void actionPerformed(ActionEvent event){
	    	            JLabel msg = new JLabel("WinSAT formal を実行します。");
	    	            JOptionPane.showMessageDialog(frm, msg);
	    	            
	    	            try {
							Runtime.getRuntime().exec("C:\\Windows\\SYSTEM32\\WinSAT2.EXE",new String[]{"formal", "-restart", "clean"});
						} catch (IOException e) {
							msg = new JLabel("WinSAT formal 実行が異常終了しました\n"+e.getMessage()+
									"\n 'winsat formal -restart を実行してください");
		    	            JOptionPane.showMessageDialog(frm, msg);
		    	            return;
						}
	    	            
	    	            msg = new JLabel("WinSAT formal を実行が終了しました。。");
	    	            JOptionPane.showMessageDialog(frm, msg);
	    	            display();
	    	          }
	    	        }
	    	      );
	    recalcBtn.setSize(40, 16);
	    l.add(label, BorderLayout.LINE_START);
	    l.add(recalcBtn, BorderLayout.WEST);
	    
	    p.add(l, BorderLayout.CENTER);
	    
	}
	
	private void display() {
		loadParams();
		text1.setText(systemScore);
		text2.setText(cpuScore);
		text3.setText(memoryScore);
		text4.setText(graphicsScore);
		text5.setText(gamingScore);
		text6.setText(diskScore);
	}
	
	private void loadParams() {
		File file = searchSATXMLPath();
		if(file == null) return;
		BufferedReader rdr = null;
		String WinSPRStr = "";
		try {
			rdr = new BufferedReader(new InputStreamReader(new FileInputStream(file), "UTF-16"));
			String line;
			
			while((line = rdr.readLine())!=null){
				int n = line.indexOf("<WinSPR>");
				if(n > 0){
					int n2 = line.indexOf("</WinSPR>");
					if(n2 > 0) {
						WinSPRStr = line.substring(n, n2);
						break;
					} else {
						WinSPRStr = line.substring(n);
					}
					continue;
				}
				n = line.indexOf("</WinSPR>");
				if(n > 0) {
					WinSPRStr = WinSPRStr + line.substring(0, n);
					break;
				}
				if(WinSPRStr.length() > 0) {
					WinSPRStr += line;
				}
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally {
			if(rdr != null) {
				try {
					rdr.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
		
		if(WinSPRStr.length() == 0) {
			JLabel msg = new JLabel("WinSAT XML ファイルに　<WinSPR>タグがありません");
	        JOptionPane.showMessageDialog(this, msg);
	        return;
		}
		
		parseWinSPR(WinSPRStr);
	}
	
	private File searchSATXMLPath() {
		File satDir = new File(satPath);
		text0.setText("none");
		if(!satDir.exists()){
			JLabel msg = new JLabel("'"+satPath+"が存在しません");
	        JOptionPane.showMessageDialog(this, msg);
	        return null;
		}
		if(!satDir.isDirectory()){
			JLabel msg = new JLabel("'"+satPath+"はディレクトリではありません");
	        JOptionPane.showMessageDialog(this, msg);
	        return null;
		}
		File satXMLFile = null;
		String date = "";
		for(File member : satDir.listFiles()) {
			String name = member.getName();
			if(name.contains("Formal.Assessment")&&
					name.endsWith(".WinSAT.xml")){
				String cur = name.substring(0,name.indexOf("Formal.Assessment")-1);
				if(date.length() == 0 || cur.compareTo(date) > 0){
					date = cur;
					satXMLFile = member;
				}
			}
		}
		if(satXMLFile == null) {
			JLabel msg = new JLabel("'"+satPath+"に WinSAT XMLファイルがありません");
	        JOptionPane.showMessageDialog(this, msg);
		}
		text0.setText(date);
		return satXMLFile;
	}
	
	private void parseWinSPR(String str) {
		systemScore = getXMLValue("SystemScore", str);
		cpuScore = getXMLValue("CpuScore", str);
		memoryScore = getXMLValue("MemoryScore", str);
		graphicsScore = getXMLValue("GraphicsScore", str);
		gamingScore = getXMLValue("GamingScore", str);
		diskScore = getXMLValue("DiskScore", str);
	}
	
	private String getXMLValue(String tag, String str) {
		int n = str.indexOf("<"+tag+">");
		if(n < 0) {
			JLabel msg = new JLabel("<WinSAT>タグ下に<"+tag+">がありません");
	        JOptionPane.showMessageDialog(this, msg);
	        return "";
		}
		int n2 = str.indexOf("</"+tag+">");
		if(n2 < 0) {
			JLabel msg = new JLabel("<WinSAT>タグ下に</"+tag+">がありません");
	        JOptionPane.showMessageDialog(this, msg);
	        return "";
		}
		return str.substring(n+tag.length()+2, n2);
	}
}
