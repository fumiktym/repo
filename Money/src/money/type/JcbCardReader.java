package money.type;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import money.CsvReader;

public class JcbCardReader extends CsvReader {
	
	private int balamt = 0;
	
	static final private Pattern filePat = Pattern.compile("[jJ][cC][bB]20[0-9][0-9](01|02|03|04|05|06|07|08|09|10|11|12)[.]csv");
	static public boolean isJcbCard(String file){
		Matcher m = filePat.matcher(file);
		return m.matches();
	}
	
	public JcbCardReader(String path) {
		
		try {
			FileInputStream is = new FileInputStream(path);
			BufferedReader rdr = new BufferedReader(new InputStreamReader(is, "SJIS"));
			String line;
			int lineno = 0;
			while((line = rdr.readLine()) != null) {
				lineno++;
				line = removeComma(line);
				String[] field = line.split("[,]");
				readField(field, line);
			}
			rdr.close();
			is.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void readField(String[] field, String line){
		if(field.length < 4){
			System.err.println("field êîÇ™ÇΩÇËÇ»Ç¢ - "+line);
			return;
		}
		String type = "OTHER";
		String id = "0";
		String name = getCsvString(field[1]);
		if(name.length() == 0) return;
		String dt = convertDT(getCsvString(field[0]));
		String memo = getCsvString(field[2]);
		int amt = 0;
		String field6 = getCsvString(field[3]);
		if(field6.endsWith("â~"))
			field6 = field6.substring(0,field6.length()-1);
		if(field6.length() > 0){
			try {
				amt = -Integer.parseInt(field6);
			} catch (NumberFormatException e){
				System.err.println("éxï•ã‡äzÇÃêîéöÇ™ïsê≥ '"+field6+"'");
			}
		} else {
			System.err.println("éxï•ã‡äzÇÃêîéöÇ™ïsê≥"+field6);
		}
		balamt = balamt + amt;
		
		addTRN(type, dt, amt, 
				id, name, memo );
	}
	private String convertDT(String org) {
		String numf[] = org.split("/");
		if(numf == null || numf.length == 0) return "";
		String dt = numf[0]; // îN 2xxx
		if(dt.length() == 0) {
			System.err.println("DATE îNÇ™ null -> 2012Ç∆âºíË");
			dt = "2012";
		} else if(dt.length() == 1) {
			System.err.println("DATE îNÇ™  1åÖ -> 201+'"+dt+"'Ç∆âºíË");
			dt = "201"+dt;
		} else if(dt.length() == 2) {
			System.err.println("DATE îNÇ™  2åÖ -> 20+'"+dt+"'Ç∆âºíË");
			dt = "20"+dt;
		} else if(dt.length() != 4) {
			System.err.println("DATE îNÇ™  3åÖÇ‹ÇΩÇÕ 5åÖà»è„-> '"+dt+"' 2012Ç∆âºíË");
			dt = "2012";
		}
		if(numf.length > 1){ // åé
			String s = numf[1];
			if(s.length() == 1) s = "0"+s;
			dt = dt + s;
		} else {
			System.err.println("DATE åéÇ™nullÅ@@'01'Ç∆âºíË");
			dt = dt + "01";
		}
		if(numf.length > 2){ // ì˙
			String s = numf[2];
			if(s.length() == 1) s = "0"+s;
			dt = dt + s;
		} else {
			System.err.println("DATE ì˙Ç™nullÅ@@'01'Ç∆âºíË");
			dt = dt + "01";
		}
		dt = dt + "000000[+9:JST]";
		return dt;
	}

	@Override
	public String getORG() {
		return "ÇiÇbÇaÅ@@ÇnÇcÇ`ÇjÇxÇtÅ@ÇbÇ`ÇqÇc";
	}

	@Override
	public String getCURDEF() {
		return "JPY";
	}

	@Override
	public String getACCTID() {
		return "XXXXXXXXXXXXXXX1089";
	}

	@Override
	public int getBALAMT() {
		return balamt;
	}

	@Override
	public String getACCTTYPE() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBANKID() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getBRANCHID() {
		// TODO Auto-generated method stub
		return null;
	}

	

}
