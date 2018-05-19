package money;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public abstract class CsvReader {
	
	private List<String> trntype = new ArrayList<String>();
	private List<String> dtposted = new ArrayList<String>();
	private List<Integer> trnamt = new ArrayList<Integer>();
	private List<String> fitid = new ArrayList<String>();
	private List<String> name = new ArrayList<String>();
	private List<String> memo = new ArrayList<String>();
	
	private String dtstart;
	private String dtend;
	
	abstract public String getORG();
	public String getDTSTART() { return dtstart; }
	public String getDTEND() { return dtend; }
	abstract public String getCURDEF();
	abstract public String getACCTID();
	abstract public String getACCTTYPE();
	abstract public int getBALAMT();
	abstract public String getBANKID();
	abstract public String getBRANCHID();
	
	public int getSTMTTRN_N() { return dtposted.size(); }
	public String getTRNTYPE(int idx) { 
		if(idx < trntype.size())
			return trntype.get(idx); 
		else return "";
	}
	public String getDTPOSTED(int idx) { 
		if(idx < dtposted.size())
			return dtposted.get(idx); 
		else return "";
	}
	public int getTRNAMT(int idx) { 
		if(idx < trnamt.size())
			return trnamt.get(idx); 
		else return 0;
	}
	public String getFITID(int idx) { 
		if(idx < fitid.size())
			return fitid.get(idx); 
		else return "";
	}
	public String getNAME(int idx) { 
		String ret = "";
		if(idx < name.size())
			ret = name.get(idx); 
		if(ret.length()==0) ret = getMEMO(idx);
		return ret;
	}
	public String getMEMO(int idx) { 
		if(idx < memo.size())
			return memo.get(idx); 
		else return "";
	}

	protected void addTRN(String type, String dt, int amt, 
			String id, String nm, String mm ) {
		trntype.add(type);
		dtposted.add(dt);
		trnamt.add(amt);
		fitid.add(id);
		name.add(nm);
		memo.add(mm);
		if(dtstart == null || compareDT(dt, dtstart) < 0){
			dtstart = dt;
		}
		if(dtend == null || compareDT(dt, dtend) > 0){
			dtend = dt;
		}
	}
	private int compareDT(String dt1, String dt2){
		dt1 = stripDT(dt1);
		dt2 = stripDT(dt2);
		return dt1.compareTo(dt2);
	}
	private String stripDT(String dt) {
		int n = dt.indexOf("[");
		if(n > 0) return dt.substring(0,n);
		else return dt;
	}
	protected String getCsvString(String org){
		if(org.startsWith("\"")){
			if(!org.endsWith("\"")){
				System.err.println("ïsê≥Ç»Åhï∂éöóÒ - "+org);
				return org;
			}
			return org.substring(1,org.length()-1);
		}
		return org;
	}
	static private final Pattern quotedCommaPat = Pattern.compile("[\"][^\"]*[\"]");
	static private final Pattern quotedNumberPat = Pattern.compile("[\"][0-9,]+[0-9][\"]");
	protected String removeComma(String line) {
		Matcher m = quotedNumberPat.matcher(line);
		while(m.find()){
			line = line.substring(0,m.start())+
				line.substring(m.start()+1,m.end()-1).replaceAll("[,ÅA]","") +
				line.substring(m.end());
			m = quotedNumberPat.matcher(line);
		}
		m = quotedCommaPat.matcher(line);
		while(m.find()){
			line = line.substring(0,m.start())+
				line.substring(m.start(),m.end()).replaceAll(",","ÅA") +
				line.substring(m.end());
		}
		return line;
	}
}
