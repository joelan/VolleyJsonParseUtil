package com.example.administrator.dategetutils.Utils;

import android.app.Application;
import android.util.Log;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;

/**
 * Created by Administrator on 2015/12/6.
 */
public class App extends Application {

public static final String TAG="Application";
    @Override
    public void onCreate() {
        super.onCreate();

        requestQueue=getRequestQueue();

    }

    public static RequestQueue requestQueue;
    private RequestQueue getRequestQueue() {
        Log.e(TAG, "getRequestQueue-----------" + requestQueue);
        if (App.requestQueue == null) {
            synchronized (App.class) {
                if (App.requestQueue == null) {
                    App.requestQueue = Volley.newRequestQueue(this);
                }
            }
        }
        return requestQueue;
    }

}
