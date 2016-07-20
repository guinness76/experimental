package org.experimental.synchronicity;

import java.lang.management.LockInfo;
import java.lang.management.ManagementFactory;
import java.lang.management.MonitorInfo;
import java.lang.management.ThreadInfo;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

/**
 * Shows how blocked threads and monitors are displayed in a thread dump.
 * @author Matt
 *
 */
public class MonitorTest implements Runnable{
	
	private static String sharedVar = "";
	private static TestLock sharedVarLock = new TestLock();
	private static TestLock secondLock = new TestLock();
	final String threadName;
	
	public static void main(String[] args) throws InterruptedException{
		
		Map<Thread, StackTraceElement[]> map = Thread.getAllStackTraces();
		System.out.println("At beginning of test, thread state is below");
		for(Thread thread: map.keySet()){
			System.out.printf("Thread %s: state=%s\n", thread.getName(), thread.getState());
		}
		
		ExecutorService executor = Executors.newFixedThreadPool(2);
		MonitorTest threadOne = new MonitorTest("threadOne");
		MonitorTest threadTwo = new MonitorTest("threadTwo");
		Future<?> future1 = executor.submit(threadOne);
		Future<?> future2 = executor.submit(threadTwo);
		
		Thread.sleep(1000);
		
		System.out.println("\nAfter child threads are started, sharedVar=" + sharedVar);
		
		ThreadInfo[] threads = ManagementFactory.getThreadMXBean().dumpAllThreads(true, true);

		for(ThreadInfo thread: threads){
			thread.getLockName();
			thread.getLockOwnerName();
			LockInfo lockInfo = thread.getLockInfo();
			MonitorInfo[] monitors = thread.getLockedMonitors();
			LockInfo[] synchronizers = thread.getLockedSynchronizers();
			System.out.printf("Thread %s[%d]: state=%s, lock owner id=%d, lock owner name='%s', lock name=%s\n",
					thread.getThreadName(), 
					thread.getThreadId(),
					thread.getThreadState(),
					thread.getLockOwnerId(),
					thread.getLockOwnerName(),
					thread.getLockName());
			
			for(StackTraceElement elm: thread.getStackTrace()){
				System.out.printf("\t%s\n", elm.toString());
			}
			
			for(MonitorInfo monitor: monitors){				
				System.out.printf("\tLocked monitor object id=%s, class name=%s\n", 
						monitor.getIdentityHashCode(),
						monitor.getClassName());
			}
			
//			for(LockInfo lock: synchronizers){
//				System.out.printf("\tLocked synchronizer object id=%s, class name=%s\n",
//						lock.getIdentityHashCode(),
//						lock.getClassName());
//			}
		}
		
		executor.shutdownNow();
	}
	
	/**
	 * Using a synchronized method like so will lock the entire class instance until the method exits. Another thread
	 * blocked here will list a lock name of 'java.lang.Class'.
	 * @param threadName
	 */
	public static synchronized void updateStaticVar(String threadName){
		Date now = new Date();
		sharedVar = now.toString();

		try {
			System.out.printf("sharedVar set to '%s', Will now sleep for %s\n", now.toString(), threadName);
			Thread.sleep(2000);
		} catch (InterruptedException e) {
			System.out.println("Interrupt called on " + threadName);
		}
		
	}
	
	public MonitorTest(String threadName) {
		this.threadName = threadName;
	}
	
	public void run(){
		updateVar();	// Uncomment to show how a specified object is displayed as the locked object.
//		updateStaticVar(threadName);	// Uncomment to show how java.lang.Class is displayed as the locked object.
	}
	
	public void updateVar(){
		
		/**
		 * Using a specific object as a lock makes it more obvious when a second thread is blocked. The second
		 * blocked thread will list the lock name as 'main.java.MonitorTest$TestLock'.
		 */
		synchronized(sharedVarLock){
			Date now = new Date();
			sharedVar = now.toString();
			
			/**
			 * Example of using more than one lock. Doesn't actually do anything here, but it does cause the first
			 * thread to list multiple monitors (in this case, the listed monitors are the TestLocks)
			 */
			synchronized(secondLock){
				try {
					System.out.printf("sharedVar set to '%s', Will now sleep for %s\n", now.toString(), threadName);
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					System.out.println("Interrupt called on " + this.threadName);
				}
			}
		}

	}
	
	public static class TestLock extends Object{
		
	}

}
