package com.example.administrator.dategetutils.Interface;

import com.example.administrator.dategetutils.resultbean.erroinfo;

public abstract class Callback<T> {
	
//	public void doinbackground();
	public  void  Finish(T t)
	{
		if(t instanceof  erroinfo)
		{
			Error((erroinfo)t);
		}
		else
		{
			finish(t);
		}

	}

	public abstract void finish(T t);
	public abstract void Error(erroinfo info);
	public abstract  void networkerro();


}
