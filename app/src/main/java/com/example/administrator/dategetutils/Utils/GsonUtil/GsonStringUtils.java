package com.example.administrator.dategetutils.Utils.GsonUtil;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 * 通过单例模式创建自定义解析工具，以及实例化gson
 * @author Aries
 *
 * @create 2015-11-3 上午10:05:01
 */
public class GsonStringUtils {

	public Gson gson;
	private static GsonStringUtils gsonUtil;

	private GsonStringUtils() {
		super();
		GsonBuilder gb = new GsonBuilder();
		gb.registerTypeAdapter(String.class, new StringConverter());
		gson = gb.create();
	}
	
	public static GsonStringUtils getInstance(){
		if (gsonUtil == null) {
			synchronized (GsonStringUtils.class) {
				if (gsonUtil == null) {
					gsonUtil = new GsonStringUtils();
				}
			}
		}
		return gsonUtil;
	}

}
