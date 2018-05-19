package test.thread;

import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.atomic.AtomicInteger;

public class NQueenByExecutor implements Callable<Void> {
	
	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int n = 16;
		Date t = new Date();
		long start = t.getTime();
		System.out.println("parallel executor start");
		try {
			System.out.println(nqueenp(n, 4, 64));
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (ExecutionException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
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

	int _n, _i, _u, _l, _r, _dl;
	
	TaskCounter tcount;
	AtomicInteger result;
	ExecutorService _es;
	
	public NQueenByExecutor(int n, int i, int u, int l, int r, int dl,
			AtomicInteger resultx, TaskCounter tcountx, ExecutorService es) {
		_n = n; _i = i; _u = u; _l = l; _r = r; _dl = dl; result = resultx;
		tcount = tcountx;
		_es = es;
	}
	
	@Override
	public Void call() throws Exception {
		nqueen(_n, _i, _u, _l, _r, _dl, result, tcount, _es);
		return null;
	}
	public void nqueen(int n , int i, int u, int l, int r, int dl, AtomicInteger result,
			TaskCounter tcount, ExecutorService es) {
		if(i == n) {
			result.getAndAdd(1);
			tcount.down();
			//System.out.println("i==n end");
			return;
		}
		if(i >= dl) {
			result.getAndAdd(serial_nqueen(n, i, u, l, r));
			tcount.down();
			//System.out.println("serial end");
			return;
		}
		int nl = (l << 1) | 1;
		int nr = ((r | (1 << n)) >> 1);
		int p = u & nl & nr;
		while( p != 0) {
			int lb = (-p)&p;
			p ^= lb;
			NQueenByExecutor tsk = new NQueenByExecutor(n, i+1, u^lb, nl^lb, nr^lb, dl, 
					result, tcount, es);
			tcount.up();
			es.submit(tsk);
		}
		tcount.down();
		//System.out.println("parallel end");
	}
	
	static int nqueenp(int n, int dl, int nw) throws InterruptedException, ExecutionException {
		ExecutorService es = Executors.newFixedThreadPool(nw);
		AtomicInteger result = new AtomicInteger();
		TaskCounter tcount = new TaskCounter();
		int b = (1 << n) - 1;
		NQueenByExecutor tsk = new NQueenByExecutor(n, 0, b, b, b, dl, result, tcount, es);
		tcount.up();
		es.submit(tsk);
		tcount.waitZero();
		es.shutdown();
		return result.get();
	}
	static int nqueens(int n) {
		int b = (1 << n) - 1;
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


}
