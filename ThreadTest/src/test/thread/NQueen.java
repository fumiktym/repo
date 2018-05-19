package test.thread;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Timer;

public class NQueen extends Thread {
	int _n, _i, _u, _l, _r, _dl;
	int result;
	public NQueen(int n , int i, int u, int l, int r, int dl) {
		_n = n; _i = i; _u = u; _l = l; _r = r; _dl = dl;
	}
	public void run() { result = nqueen(_n, _i, _u, _l, _r, _dl); }
	int joinx() {
		do {
			try { join(); } catch(InterruptedException e) { continue; }
		} while(false);
		return result;
	}
	public static void main(String[] arg) {
		int n = 16;
		Date t = new Date();
		long start = t.getTime();
		System.out.println("thread start");
		System.out.println(nqueenp(n, 2));
		t = new Date();
		long end = t.getTime();
		System.out.println("Time = "+(end-start)+" m sec.");
		t = new Date();
		start = t.getTime();
		System.out.println("serial start");
		System.out.println(nqueens(n));
		t = new Date();
		end = t.getTime();
		System.out.println("Time = "+(end-start)+" m sec.");
	}
	
	static int nqueens(int n){
		int b = (1<<n) - 1;
		return serial_nqueen(n, 0, b, b, b);
	}
	
	static int serial_nqueen(int n, int i, int u, int l, int r) {
		if(i == n) return 1;
		int nl = (l << 1) | 1;
		int nr = ((r | (1<<n)) >> 1);
		int p = u & nl & nr;
		int c = 0;
		while( p != 0 ) {
			int lb = (-p)&p;
			p ^= lb;
			c += serial_nqueen(n, i+1, u^lb, nl^lb, nr^lb);
		}
		return c;
	}

	static int nqueenp(int n, int dl) {
		int b = (1 << n) - 1;
		NQueen th = new NQueen(n, 0,b, b, b, dl);
		th.start();
		return th.joinx();
	}
	int nqueen(int n, int i, int u, int l, int r,int dl) {
		if(i == n) { return 1; }
		if(i >= dl) return serial_nqueen(n, i, u, l, r);
		int nl = (l << 1) | 1;
		int nr = ((r|(1<<n))>>1);
		int p = u&nl&nr;
		int c = 0;
		List<NQueen> threads = null;
		while(p != 0) {
			int lb = (-p)&p;
			p ^= lb;
			NQueen th = new NQueen(n, i+1, u^lb,nl^lb,nr^lb,dl);
			if(threads == null)
				threads = new ArrayList<NQueen>();
			th.start();
			threads.add(th);
		}
		if(threads != null){
			for(NQueen th : threads) c += th.joinx();
		}
		return c;
		
	}
}
