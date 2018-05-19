package test.thread;

public class ThreadTest1 implements Runnable{
	
	private void test1() {
		double i = 0;
		while(true) {
			i = i * 1.001;
		}
	}
	
	public void run() {
		test1();
	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		ThreadTest1 tests[] = {new ThreadTest1(),new ThreadTest1(),new ThreadTest1(),new ThreadTest1()};
		for(ThreadTest1 t1 : tests) {
			Thread t = new Thread(t1);
			System.out.println("Tread started...");
			t.start();
			
		}
		System.out.println("main thread end.");
	}

}
