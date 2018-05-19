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

public class GoldPointCardReader extends CsvReader {
	
	private int balamt = 0;
	
	static final private Pattern filePat = Pattern.compile("20[0-9][0-9](01|02|03|04|05|06|07|08|09|10|11|12)[.]csv");
	static public boolean isGoldPointCard(String file){
		Matcher m = filePat.matcher(file);
		return m.matches();
	}
	
	public GoldPointCardReader(String path) {
		
		try {
			FileInputStream is = new FileInputStream(path);
			BufferedReader rdr = new BufferedReader(new InputStreamReader(is, "SJIS"));
			String line;
			int lineno = 0;
			while((line = rdr.readLine()) != null) {
				lineno++;
				// if(lineno < 2) continue; // 最初の行はタイトルなので無視
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
		if(field.length < 7){
			System.err.println("field 数がたりない - "+line);
			return;
		}
		String type = "OTHER";
		String id = "0";
		String dt = convertDT(getCsvString(field[0]));
		String memo = getCsvString(field[2])+getCsvString(field[3])+getCsvString(field[4])+getCsvString(field[5]);
		String name = getCsvString(field[1]);
		int amt = 0;
		String field6 = getCsvString(field[6]);
		if(field6.length() > 0){
			try {
				amt = -Integer.parseInt(field6);
			} catch (NumberFormatException e){
				System.err.println("支払金額の数字が不正 '"+field6+"'");
			}
		} else {
			System.err.println("支払金額の数字が不正"+field6);
		}
		balamt = balamt + amt;
		
		addTRN(type, dt, amt, 
				id, name, memo );
	}
	private String convertDT(String org) {
		String numf[] = org.split("/");
		if(numf == null || numf.length == 0) return "";
		String dt = numf[0]; // 年 2xxx
		if(dt.length() == 0) {
			System.err.println("DATE 年が null -> 2012と仮定");
			dt = "2012";
		} else if(dt.length() == 1) {
			System.err.println("DATE 年が  1桁 -> 201+'"+dt+"'と仮定");
			dt = "201"+dt;
		} else if(dt.length() == 2) {
			System.err.println("DATE 年が  2桁 -> 20+'"+dt+"'と仮定");
			dt = "20"+dt;
		} else if(dt.length() != 4) {
			System.err.println("DATE 年が  3桁または 5桁以上-> '"+dt+"' 2012と仮定");
			dt = "2012";
		}
		if(numf.length > 1){ // 月
			String s = numf[1];
			if(s.length() == 1) s = "0"+s;
			dt = dt + s;
		} else {
			System.err.println("DATE 月がnull　@'01'と仮定");
			dt = dt + "01";
		}
		if(numf.length > 2){ // 日
			String s = numf[2];
			if(s.length() == 1) s = "0"+s;
			dt = dt + s;
		} else {
			System.err.println("DATE 日がnull　@'01'と仮定");
			dt = dt + "01";
		}
		dt = dt + "000000[+9:JST]";
		return dt;
	}

	@Override
	public String getORG() {
		return "ヨドバシ　@ゴールドポイントカード";
	}

	@Override
	public String getCURDEF() {
		return "JPY";
	}

	@Override
	public String getACCTID() {
		return "4980018719962837";
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
