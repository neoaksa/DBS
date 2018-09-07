
package edu.gvsu.cis;

public class TaskA implements Runnable {

	private Counter cnt;
	
	public TaskA(Counter cnt)
	{
		this.cnt = cnt;
	}

	@Override
	public void run() {
		for(int i=0; i<10000; i++) {
			cnt.increment();
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		Counter cnt = new Counter();
		Thread t1 = new Thread(new TaskA(cnt));
		Thread t2 = new Thread(new TaskA(cnt));
		t1.start();
		t2.start();
		try {
			t1.join();
			t2.join();
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Final Counter = " + cnt.getCounter());

	}

}
