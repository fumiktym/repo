package money;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.Calendar;
import java.util.Date;

import money.type.AeonCardReader;
import money.type.GoldPointCardReader;
import money.type.JcbCardReader;
import money.type.UfjReader;
import money.type.ViewCardReader;

public class OfxConverter {
	static public void main(String arg[]){
		if(arg.length == 0){
			arg = new String[]{
				"",
				"C:/Users/ï∂ïF/Downloads",
				"C:/Users/ï∂ïF/Downloads"
			};
		}
		if(arg.length != 3){
			System.err.println("Usage money.OfxConverter infile outfile");
			System.exit(1);
		}
		String type = arg[0];
		String iPath = arg[1];
		String oPath = arg[2];
		OfxConverter conv = new OfxConverter();
		conv.convert(type, iPath, oPath);
		
	}
	
	public OfxConverter() {
		
	}
	
	public void convert(String type, String iPath, String oPath){
		File dir = new File(iPath);
		if(dir.exists() && dir.isDirectory()){
			for(String file : dir.list()){
				if(!file.endsWith(".csv") && !file.endsWith(".CSV")){
					continue;
				}
				type = "";
				if(type.length() == 0 && UfjReader.isUfj(file)){
					type = "ufj";
				} else if(type.length() == 0 && GoldPointCardReader.isGoldPointCard(file)){
					type = "gold";
				} else if(type.length() == 0 && AeonCardReader.isAeonCard(file)){
					type = "aeon";
				} else if(type.length() == 0 && ViewCardReader.isViewCard(file)){
					type = "view";
				} else if(type.length() == 0 && JcbCardReader.isJcbCard(file)){
					type = "jcb";
				}
				if(type.length() == 0) continue;
				System.out.println("Process ("+type+") - "+iPath+"/"+file);
				convertOfx(type, iPath+"/"+file, oPath+"/"+file.substring(0,file.length()-4)+".ofx");
			}
		} else if(dir.exists()){
			convertOfx(type, iPath, oPath);
		} else {
			System.err.println("ì¸óÕ '"+iPath+"' Ç™ë∂ç›ÇµÇ»Ç¢ÅB");
		}
	}
	public void convertOfx(String type, String iPath, String oPath){
		CsvReader rdr = null;
		if(type.equals("ufj")){
			rdr = new UfjReader(iPath);
		} else if(type.equals("gold")){
			rdr = new GoldPointCardReader(iPath);
		} else if(type.equals("aeon")){
			rdr = new AeonCardReader(iPath);
		
		} else if(type.equals("view")){
			rdr = new ViewCardReader(iPath);
		} else if(type.equals("jcb")){
			rdr = new JcbCardReader(iPath);
				
		} else {
			System.err.println("CSV Reader Type Ç™ïsê≥ - "+type);
			return;
		}
		/*
		if(rdr == null) {
			System.err.println("CSV Reader èâä˙ê›íËÉGÉâÅ[ - type="+type+" path="+iPath);
			return;
		}
		*/
		
		try {
			PrintWriter wtr = new PrintWriter(oPath, "UTF-8");
			generateOfx(rdr, wtr);
			wtr.close();
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	private void generateOfx(CsvReader rdr, PrintWriter wtr) {
		generateHeader(wtr);
		wtr.println("<OFX>");
		generateSIGNONMSGSRSV1(rdr, wtr, "  ");
		if(rdr.getBANKID() == null)
			generateCREDITCARDMSGSRSV1(rdr, wtr, "  ");
		else generateBANKMSGSRSV1(rdr, wtr, "  ");
		wtr.println("</OFX>");
	}
	private void generateHeader(PrintWriter wtr) {
		wtr.println("OFXHEADER:100");
		wtr.println("DATA:OFXSGML");
		wtr.println("VERSION:102");
		wtr.println("SECURITY:NONE");
		wtr.println("ENCODING:UTF-8");
		wtr.println("CHARSET:CSUNICODE");
		wtr.println("COMPRESSION:NONE");
		wtr.println("OLDFILEUID:NONE");
		wtr.println("NEWFILEUID:NONE");
	}
	
	private void generateSIGNONMSGSRSV1(CsvReader rdr, PrintWriter wtr, String indent){
		wtr.println(indent+"<SIGNONMSGSRSV1>");
		wtr.println(indent+"  <SONRS>");
		wtr.println(indent+"    <STATUS>");
		wtr.println(indent+"      <CODE>0");
		wtr.println(indent+"      <SEVERITY>INFO");
		wtr.println(indent+"    </STATUS>");
		wtr.println(indent+"    <DTSERVER>"+getNowDT());
		wtr.println(indent+"    <LANGUAGE>JPN");
		wtr.println(indent+"    <FI>");
		wtr.println(indent+"      <ORG>"+rdr.getORG());
		wtr.println(indent+"    </FI>");
		wtr.println(indent+"  </SONRS>");
		wtr.println(indent+"</SIGNONMSGSRSV1>");
	}
	private void generateCREDITCARDMSGSRSV1(CsvReader rdr, PrintWriter wtr, String indent){
		wtr.println(indent+"<CREDITCARDMSGSRSV1>");
		wtr.println(indent+"  <CCSTMTTRNRS>");
		wtr.println(indent+"    <TRNUID>0");
		wtr.println(indent+"    <STATUS>");
		wtr.println(indent+"      <CODE>0");
		wtr.println(indent+"      <SEVERITY>INFO");
		wtr.println(indent+"    </STATUS>");
		wtr.println(indent+"    <CCSTMTRS>");
		wtr.println(indent+"      <CURDEF>"+rdr.getCURDEF());
		wtr.println(indent+"      <CCACCTFROM>");
		wtr.println(indent+"        <ACCTID>"+rdr.getACCTID());
		wtr.println(indent+"      </CCACCTFROM>");
		
		generateBANKTRANLIST(rdr, wtr, indent+"    ");
		
		wtr.println(indent+"      <LEDGERBAL>");
		wtr.println(indent+"        <BALAMT>0");
		wtr.println(indent+"        <DTASOF>"+getNowDT());
		wtr.println(indent+"      </LEDGERBAL>");
		wtr.println(indent+"    </CCSTMTRS>");
		wtr.println(indent+"  </CCSTMTTRNRS>");
		wtr.println(indent+"</CREDITCARDMSGSRSV1>");
	}
	private void generateBANKMSGSRSV1(CsvReader rdr, PrintWriter wtr, String indent){
		wtr.println(indent+"<BANKMSGSRSV1>");
		wtr.println(indent+"  <STMTTRNRS>");
		wtr.println(indent+"    <TRNUID>0");
		wtr.println(indent+"    <STATUS>");
		wtr.println(indent+"      <CODE>0");
		wtr.println(indent+"      <SEVERITY>INFO");
		wtr.println(indent+"    </STATUS>");
		wtr.println(indent+"    <STMTRS>");
		wtr.println(indent+"      <CURDEF>"+rdr.getCURDEF());
		wtr.println(indent+"      <BANKACCTFROM>");
		wtr.println(indent+"        <BANKID>"+rdr.getBANKID());
		wtr.println(indent+"        <BRANCHID>"+rdr.getBRANCHID());
		wtr.println(indent+"        <ACCTID>"+rdr.getACCTID());
		wtr.println(indent+"        <ACCTTYPE>"+rdr.getACCTTYPE());
		wtr.println(indent+"      </BANKACCTFROM>");
		
		generateBANKTRANLIST(rdr, wtr, indent+"    ");
		
		wtr.println(indent+"      <LEDGERBAL>");
		wtr.println(indent+"        <BALAMT>0");
		wtr.println(indent+"        <DTASOF>"+getNowDT());
		wtr.println(indent+"      </LEDGERBAL>");
		wtr.println(indent+"    </STMTRS>");
		wtr.println(indent+"  </STMTTRNRS>");
		wtr.println(indent+"</BANKMSGSRSV1>");
	}
	private void generateBANKTRANLIST(CsvReader rdr, PrintWriter wtr, String indent){
		wtr.println(indent+"<BANKTRANLIST>");
        wtr.println(indent+"  <DTSTART>"+rdr.getDTSTART());
        wtr.println(indent+"  <DTEND>"+rdr.getDTEND());
        
        for(int i = 0; i < rdr.getSTMTTRN_N(); i++){
        	generateSTMTTRN(rdr, wtr, indent+"  ", i);
        }

		wtr.println(indent+"</BANKTRANLIST>");

	}
	private void generateSTMTTRN(CsvReader rdr, PrintWriter wtr, String indent, int idx){
		wtr.println(indent+"<STMTTRN>");
		wtr.println(indent+"  <TRNTYPE>"+rdr.getTRNTYPE(idx));
		wtr.println(indent+"  <DTPOSTED>"+rdr.getDTPOSTED(idx));
		wtr.println(indent+"  <TRNAMT>"+rdr.getTRNAMT(idx));
		wtr.println(indent+"  <FITID>"+rdr.getFITID(idx));
		wtr.println(indent+"  <NAME>"+rdr.getNAME(idx));
		wtr.println(indent+"  <MEMO>"+rdr.getMEMO(idx));
		wtr.println(indent+"</STMTTRN>");
	}
	
	private String getNowDT() {
		Date dt = new Date();
		int y = Calendar.getInstance().get(Calendar.YEAR);
		int m = Calendar.getInstance().get(Calendar.MONTH)+1;
		int d = Calendar.getInstance().get(Calendar.DAY_OF_MONTH);
		int H = Calendar.getInstance().get(Calendar.HOUR_OF_DAY);
		int M = Calendar.getInstance().get(Calendar.MINUTE);
		int S = Calendar.getInstance().get(Calendar.SECOND);
		String ret = ""+y;
		if(m < 10) ret += "0"+m;
		else ret += m;
		if(d < 10) ret += "0"+d;
		else ret += d;
		if(H < 10) ret += "0"+H;
		else ret += H;
		if(M < 10) ret += "0"+M;
		else ret += M;
		if(S < 10) ret += "0"+S;
		else ret += S;
		ret += "[+9:JST]";
		return ret;
	}
}
