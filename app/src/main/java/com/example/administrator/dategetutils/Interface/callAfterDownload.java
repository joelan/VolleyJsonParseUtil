package com.example.administrator.dategetutils.Interface;

import android.util.Log;

import com.example.administrator.dategetutils.Utils.ThreadPoolExecutorUtil;
import com.example.administrator.dategetutils.resultbean.erroinfo;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.FutureTask;


public abstract class callAfterDownload<T>{

	T calsstype;
	 String TAG="callAfterDownload";
	class Dointhread implements Callable<Boolean>
	{
		public Dointhread( T t) {
			// TODO Auto-generated constructor stub
			calsstype=t;
		}

		public Boolean call() throws Exception {
			// TODO Auto-generated method stub
			if(calsstype instanceof  erroinfo)
			{
				Error((erroinfo)calsstype);
			}
			else {
				Task(calsstype);
			}

			return true;
		}
		
	}
	
	class Dointhreadnetwork implements Callable<Boolean>
	{

		@Override
		public Boolean call() throws Exception {
			// TODO Auto-generated method stub
			networkerror();

			return true;
		}
		
	}
	//这两个函数判定一下是否为null
    public abstract void Task(T t);
	//这两个函数判定一下是否为null
	public abstract  void Error(erroinfo info);

	 public abstract  void networkerror();
	 
	 public  void SubmitTask(T t)
	 {
		 ExecutorService exec= ThreadPoolExecutorUtil.getThreadPool();
		 Dointhread subCalc = new Dointhread(t);
          FutureTask<Boolean> task = new FutureTask<Boolean>(subCalc);   
     Log.i(TAG, "submitTask");
         // tasks.add(task);   
          if (!exec.isShutdown()) {   
              exec.submit(task);
              Log.i(TAG, "submitTasksubmit");
          }   
		 
	 }
	
	 public void networktask()
	 {
		 ExecutorService exec= ThreadPoolExecutorUtil.getThreadPool();
	     Dointhreadnetwork subCalc2 = new Dointhreadnetwork();   
         FutureTask<Boolean> task2 = new FutureTask<Boolean>(subCalc2);   
         Log.i(TAG, "networkTask");
         if (!exec.isShutdown()) {   
             Log.i(TAG, "submitnetworkTask");
             exec.submit(task2);
         }   
	 }
	

}
