package money.type;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import money.CsvReader;

public class UfjReader extends CsvReader {
	
	private int balamt = 0;
	
	static public boolean isUfj(String file){
		return file.startsWith("1156176_");
	}
	
	public UfjReader(String path) {
		
		try {
			FileInputStream is = new FileInputStream(path);
			BufferedReader rdr = new BufferedReader(new InputStreamReader(is, "SJIS"));
			String line;
			int lineno = 0;
			while((line = rdr.readLine()) != null) {
				lineno++;
				if(lineno < 2) continue; // �ŏ��̍s�̓^�C�g���Ȃ̂Ŗ���
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
		if(field.length < 6){
			System.err.println("field ��������Ȃ� - "+line);
			return;
		}
		String type = "OTHER";
		String id = "0";
		String dt = convertDT(getCsvString(field[0]));
		String memo = getCsvString(field[1]);
		String name = getCsvString(field[2]);
		int amt = 0;
		String field3 = getCsvString(field[3]);
		if(field3.length() > 0){
			try {
				amt = -Integer.parseInt(field3);
			} catch (NumberFormatException e){
				System.err.println("�x�����z�̐������s��"+field3);
			}
		}
		String field4 = getCsvString(field[4]);
		if(field4.length() > 0){
			if(amt != 0){
				System.err.println("�x�����z�Ɨa������z�̗����w��"+field3+"<->"+field4);
			}
			try {
				amt = Integer.parseInt(field4);
			} catch (NumberFormatException e){
				System.err.println("�a������z�̐������s��"+field4);
			}
		}
		String field5 = getCsvString(field[5]);
		if(field5.length() > 0){
			try {
				balamt = -Integer.parseInt(field5);
			} catch (NumberFormatException e){
				System.err.println("���������c���̐������s��"+field5);
			}
		}
		
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
		return "�O�H�����t�e�i��s";
	}

	@Override
	public String getCURDEF() {
		return "JPY";
	}

	@Override
	public String getACCTID() {
		return "1156176";
	}

	@Override
	public int getBALAMT() {
		return balamt;
	}

	@Override
	public String getACCTTYPE() {
		// TODO Auto-generated method stub
		return "SAVINGS";
	}

	@Override
	public String getBANKID() {
		// TODO Auto-generated method stub
		return "0005";
	}

	@Override
	public String getBRANCHID() {
		// TODO Auto-generated method stub
		return "379";
	}

	

}