package com.example.administrator.dategetutils.Utils;

import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ThreadPoolExecutorUtil {
	
	 private static ExecutorService  exec=null;   
	public static ExecutorService  getThreadPool()
	{
		if(exec==null)
		{
		  BlockingQueue queue = new LinkedBlockingQueue();    
		     exec = new ThreadPoolExecutor(3, 3, 10, TimeUnit.SECONDS, queue); 
//		  exec=Executors.newCachedThreadPool();
		}
		
		  return exec;  
	}

}
