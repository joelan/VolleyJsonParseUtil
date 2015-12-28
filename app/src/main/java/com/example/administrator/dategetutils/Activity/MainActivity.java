package com.example.administrator.dategetutils.Activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.example.administrator.dategetutils.Interface.Callback;
import com.example.administrator.dategetutils.R;
import com.example.administrator.dategetutils.Utils.PostDatatoServer;
import com.example.administrator.dategetutils.resultbean.erroinfo;
import com.example.administrator.dategetutils.resultbean.mymsg;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        GetDataFromServer();



    }

    /**
     * 获取服务器数据
     */
    private void GetDataFromServer() {
        PostDatatoServer post=new PostDatatoServer(this);
        /**
         * 结果操作在线程
         */
//        post.setCallad(new callAfterDownload<mymsg>() {
//
//            @Override
//            public void Task(final mymsg mymsg) {
//
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//
//                        if(mymsg!=null)
//                            Toast.makeText(MainActivity.this,mymsg.getDetail(),Toast.LENGTH_SHORT).show();
//                        else
//                        {
//                            Toast.makeText(MainActivity.this,"错误",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//
//            }
//
//            @Override
//            public void Error(final erroinfo info) {
//
//                MainActivity.this.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        if(info!=null)
//                            Toast.makeText(MainActivity.this,info.getError(),Toast.LENGTH_SHORT).show();
//                        else
//                        {
//                            Toast.makeText(MainActivity.this,"错误",Toast.LENGTH_SHORT).show();
//                        }
//                    }
//                });
//
//            }
//
//            @Override
//            public void networkerror() {
//
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();
//                    }
//                });
//
//
//            }
//
//        });


        /**
         * 返回类型为对象（list）的例子，结果的操作在UI进程
         */
        post.setcallbacklistener(new Callback<mymsg>() {

            //对象
            @Override
            public void finish(mymsg mymsg) {
                if (mymsg != null)
                    Toast.makeText(MainActivity.this, mymsg.getDetail(), Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(MainActivity.this, "错误", Toast.LENGTH_SHORT).show();
                }
            }

//列表
//            @Override
//            public void finish(ArrayList<mymsg> mymsgs) {
//
//                if(mymsgs!=null&&mymsgs.size()>0)
//                               {
//                            Toast.makeText(MainActivity.this,mymsgs.get(mymsgs.size()-1).getDetail(), Toast.LENGTH_SHORT).show();
//
//                                }
//                        else
//                        {
//                            Toast.makeText(MainActivity.this,"错误",Toast.LENGTH_SHORT).show();
//                        }
//
//
//            }

            @Override
            public void Error(erroinfo info) {
                if (info != null)
                    Toast.makeText(MainActivity.this, info.getError(), Toast.LENGTH_SHORT).show();
                else {
                    Toast.makeText(MainActivity.this, "错误", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void networkerro() {

                Toast.makeText(MainActivity.this, "网络错误", Toast.LENGTH_SHORT).show();

            }
        });

        //对象 线程解释
        post.postdataparcelinThread(null, "http://joemyapplication.duapp.com/", mymsg.class);


        //列表  线程解释
        //  post.postdataInThreadType(null, "http://joemyapplication.duapp.com/", new TypeToken<List<mymsg>>(){}.getType());


        //对象 UI进程解释
        post.postdata(null, "http://joemyapplication.duapp.com/", mymsg.class);


        //列表  UI进程解释
        //  post.postdata(null, "http://joemyapplication.duapp.com/", new TypeToken<List<mymsg>>(){}.getType());
    }
}
