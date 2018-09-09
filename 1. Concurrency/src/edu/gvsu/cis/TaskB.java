
package edu.gvsu.cis;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TaskB implements Runnable {

	private Counter cnt;

	public TaskB(Counter cnt)
	{
		this.cnt = cnt;
	}

	@Override
	public void run() {
		for(int i=0; i<1000000; i++) {
			cnt.safeIncrement();
//			if(i==999999){
//				System.out.println(String.format("execute thread: %s - %d",Thread.currentThread().getName(), i));
//			}
		}

	}

	/**
	 * @param args
	 */
	public static void main(String[] args) {
		int n[] = {2, 4, 8, 16, 32}; // thread number
		for (int threadn = 0; threadn < n.length; threadn++) {
			long startTime = System.currentTimeMillis();
			Thread threadArr[] = new Thread[n[threadn]]; //thread array
			Counter cnt = new Counter();
			ExecutorService executor = Executors.newFixedThreadPool(n[threadn]);
			for (int i = 0; i < n[threadn]; i++) {
				threadArr[i] = new Thread(new TaskB(cnt));
				executor.execute(threadArr[i]);
			}
			executor.shutdown();
			while (!executor.isTerminated()) {
//				System.out.println("Not all threads finished");
			}
			long endTime = System.currentTimeMillis();
			System.out.format(String.format("execute %d threads finished \n", n[threadn]));
			System.out.format("Final Counter: %d \n", cnt.getCounter());
			System.out.format("Time Cost: %d milliseconds \n", endTime - startTime);
		}
	}

}
