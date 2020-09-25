package com.sunlong.github.server;

import androidx.appcompat.app.AppCompatActivity;
import android.content.Context;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import java.io.IOException;

public class MainActivity extends AppCompatActivity
{

    private EditText et_port,et_message;
    private Button bt_monitor,bt_send;
    private TextView tv_showClient,tv_showMessage,tv_ip;
    private MyHandler handler;
    private String port;//服务端监听的端口号
    private MyServer myServer;//创建服务端对象
    private ServerThread serverThread;//开启服务端对象

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initView();

        tv_ip.setText(GetLocalIP());//获取当前设备局域网IP地址并展示在界面

        bt_monitor.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //判断当前服务器状态，如果未开启就开始监听；
               if(!MyHandler.CONNECT_STATUS)
               {
                   port = et_port.getText().toString().trim();
                   myServer = new MyServer(handler);
                   if (!port.equals(""))
                   {
                       new Thread(new Runnable()//开启新线程进行网络监听
                       {
                           @Override
                           public void run()
                           {
                               try
                               {
                                   myServer.initServer(Integer.valueOf(port));//调用开始监听对象
                               }
                               catch (IOException e)
                               {
                                   //服务器创建失败时执行下面的操作
                                   e.printStackTrace();
                                   handler.sendEmptyMessage(MyHandler.SERVER_FAILED);
                               }
                           }
                       }).start();
                   }
                   else
                   {
                       Toast.makeText(MainActivity.this,"请输入端口！",Toast.LENGTH_LONG).show();
                   }
               }
               else
               {
                   //如果当前处于连接状态就停止监听
                   handler.sendEmptyMessage(MyHandler.SERVER_BREAK);
                   myServer.closeMyServer();
                   serverThread.closeAll();
                   MyServer.sockets.clear();
               }
            }
        });

        bt_send.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                new Thread(new Runnable()
                {
                    @Override
                    public void run()
                    {
                        serverThread.sendAll(et_message.getText().toString());//调用对象：发送消息给当前连接的所有客户端
                    }
                }).start();
                tv_showMessage.append("Server:"+et_message.getText().toString()+"\n");
            }
        });

    }

    /**
     * 实例化控件
     */
    private void initView()
    {
        et_port = (EditText)findViewById(R.id.m_et_port);
        et_message = (EditText)findViewById(R.id.m_et_message);
        bt_monitor = (Button)findViewById(R.id.m_bt_monitor);
        bt_send = (Button)findViewById(R.id.m_bt_send);
        tv_showClient = (TextView)findViewById(R.id.m_tv_showClient);
        tv_showMessage = (TextView)findViewById(R.id.m_tv_showMessage);
        tv_ip = (TextView)findViewById(R.id.m_tv_ipShow);
        handler = new MyHandler(MainActivity.this,tv_showClient,tv_showMessage,bt_monitor,bt_send);
        tv_showMessage.setMovementMethod(ScrollingMovementMethod.getInstance());//允许TextView可以拖动
        tv_showClient.setMovementMethod(ScrollingMovementMethod.getInstance());
        serverThread = new ServerThread();
    }

    /**
     * 返回当前设备的IP地址
     * @return 当前设备IP地址
     */
    private String GetLocalIP()
    {
        WifiManager wifiManager = (WifiManager)getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        int ipAddress = wifiInfo.getIpAddress();
        if (ipAddress == 0) tv_ip.setText("127.0.0.1");
        String ip = ((ipAddress & 0xff) + "." + (ipAddress >> 8 & 0xff) + "." + (ipAddress >> 16 & 0xff) + "." + (ipAddress >> 24 & 0xff));
        return ip;
    }

    /**
     * APP退出时执行的操作
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        handler.sendEmptyMessage(MyHandler.SERVER_BREAK);
        myServer.closeMyServer();
        serverThread.closeAll();
        MyServer.sockets.clear();
    }

}
