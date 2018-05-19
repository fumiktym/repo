package money.type;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.HashSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import money.CsvReader;

/**
 * view-
 * 2015/5/4 �ǉ� �u�����p����-
 * @author ���F
 *
 */
public class ViewCardReader extends CsvReader {
	
	private int balamt = 0;
	
	static final private Pattern filePat = Pattern.compile("(([vV][iI][eE][wW])|�����p����)[_\\-a-zA-Z0-9]*[.]csv");
	static public boolean isViewCard(String file){
		Matcher m = filePat.matcher(file);
		return m.matches();
	}
	
	public ViewCardReader(String path) {
		
		try {
			FileInputStream is = new FileInputStream(path);
			BufferedReader rdr = new BufferedReader(new InputStreamReader(is, "SJIS"));
			String line;
			int lineno = 0;
			boolean goriyo = false;
			int skip = 0;
			if(path.contains("�����p����")){
				goriyo = true;
				skip = 7;
			}
			while((line = rdr.readLine()) != null) {
				lineno++;
				if(lineno <= skip) continue; // �����p���ׂ̏ꍇ�A�ŏ���7�s�͖���
				line = removeComma(line);
				if(goriyo && line.startsWith("���ߓ�")) // �����p���ׂ̏ꍇ�A����ȍ~�̍s�͖���
					break;
				String[] field = line.split("[,]");
				if(field==null 
						|| field.length < 1 
						|| field[0]==null 
						|| field[0].trim().length()==0)
					continue;
				if(goriyo)
					readField_goriyo(field, line);
				else
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
		if(field.length < 5){
			System.err.println("field ��������Ȃ� - "+line);
			return;
		}
		String type = "OTHER";
		String id = "0";
		String dt = convertDT(getCsvString(field[0]));
		String memo = getCsvString(field[4]);
		String name = getCsvString(field[2]);
		int amt = -1;
		String field6 = getCsvString(field[3]);
		if(field6.length() == 0 ){
			field6 = getCsvString(field[4]);
			amt = 1;
		}
		if(field6.length() > 0 ){
			field6 = field6.replaceAll("[�A]", "");
			try {
				amt = amt * Integer.parseInt(field6);
			} catch (NumberFormatException e){
				System.err.println("�x�����z�̐������s�� '"+field6+"'");
			}
		} else {
			System.err.println("�x�����z�̐������s��"+field6);
		}
		balamt = balamt + amt;
		
		addTRN(type, dt, amt, 
				id, name, memo );
	}
	
	
	private HashSet<String> dateName = new HashSet<String>();
	
	private void readField_goriyo(String[] field, String line){
		if(field.length < 5){
			System.err.println("field ��������Ȃ� - "+line);
			return;
		}
		String type = "OTHER";
		String id = "0";
		String dt = convertDT(getCsvString(field[0])); // �����p�N����
		String memo = getCsvString(field[5]); // �x���敪�i�񐔁j
		String name = getCsvString(field[1]); // �����p�ӏ�
		String key = dt+":"+name;
		String name2 = name;
		int count = 2;
		if(dateName.contains(key)){
			name = name2 + count++;
			key = dt + ":" + name;
		}
		dateName.add(key);
		int amt = -1;
		String field6 = getCsvString(field[2]); // �����p�z
		if(field6.length() == 0 ){
			field6 = getCsvString(field[4]); // ���ߊz
			amt = 1;
		}
		if(field6.length() > 0 ){
			field6 = field6.replaceAll("[�A]", "");
			try {
				amt = amt * Integer.parseInt(field6);
			} catch (NumberFormatException e){
				System.err.println("�����p�z/���ߊz�̐������s�� '"+field6+"'");
			}
		} else {
			System.err.println("�����p�z/���ߊz���s��(�w��Ȃ�)"+field6);
		}
		balamt = balamt + amt;
		
		addTRN(type, dt, amt, 
				id, name, memo );
	}
	private String convertDT(String org) {
		String numf[] = org.split("/");
		if(numf == null || numf.length == 0) return "";
		String dt = numf[0]; // �N 2xxx
		if(dt.length() == 0) {
			System.err.println("DATE �N�� null -> 2012�Ɖ���");
			dt = "2012";
		} else if(dt.length() == 1) {
			System.err.println("DATE �N��  1�� -> 201+'"+dt+"'�Ɖ���");
			dt = "201"+dt;
		} else if(dt.length() == 2) {
			System.err.println("DATE �N��  2�� -> 20+'"+dt+"'�Ɖ���");
			dt = "20"+dt;
		} else if(dt.length() != 4) {
			System.err.println("DATE �N��  3���܂��� 5���ȏ�-> '"+dt+"' 2012�Ɖ���");
			dt = "2012";
		}
		if(numf.length > 1){ // ��
			String s = numf[1];
			if(s.length() == 1) s = "0"+s;
			dt = dt + s;
		} else {
			System.err.println("DATE ����null�@@'01'�Ɖ���");
			dt = dt + "01";
		}
		if(numf.length > 2){ // ��
			String s = numf[2];
			if(s.length() == 1) s = "0"+s;
			dt = dt + s;
		} else {
			System.err.println("DATE ����null�@@'01'�Ɖ���");
			dt = dt + "01";
		}
		dt = dt + "000000[+9:JST]";
		return dt;
	}

	@Override
	public String getORG() {
		return "JR�@@VIEW SUICA JCB�J�[�h";
	}

	@Override
	public String getCURDEF() {
		return "JPY";
	}

	@Override
	public String getACCTID() {
		return "viewaccount";
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
