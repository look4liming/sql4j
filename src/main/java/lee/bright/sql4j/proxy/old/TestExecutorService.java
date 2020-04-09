package lee.bright.sql4j.proxy.old;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TestExecutorService {

	public static void main(String[] args) {
		ExecutorService pool = Executors.newFixedThreadPool(5);
		for (int i = 0; i < 10; i++) {
			Task t = new Task(i);
			pool.execute(t);
		}
	}
	
	private static class Task implements Runnable {
		
		private int i;
		
		public Task(int i) {
			this.i = i;
		}
	
		public void run() {
			System.out.println(i);
		}
		
	}

}