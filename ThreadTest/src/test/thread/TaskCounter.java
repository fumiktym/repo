package test.thread;

import java.util.LinkedList;
import java.util.List;

public class TaskCounter {
	private Integer count;
	//private Thread mon;
	//private boolean alive;
	public TaskCounter() {
		count = new Integer(0);
	}
	@Override
	protected void finalize() throws Throwable {
		int ct;
		synchronized(count) {
			ct = count.intValue();
		}
		if(ct > 0) {
			waitZero();
		}
		super.finalize();
	}
	public void up() {
		
		synchronized(count) {
			count = new Integer(count.intValue()+1);
		}
	}
	public void down() {
		synchronized(count) {
			count = new Integer(count.intValue()-1);
		}
	}
	public void waitZero() {
		
		while(count.intValue() > 0){
			
		}
	}
	
}
