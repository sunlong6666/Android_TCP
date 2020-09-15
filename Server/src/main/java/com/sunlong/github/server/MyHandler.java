package com.sunlong.github.server;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;

public class MyHandler extends Handler
{

    public static final int READ_DATA = 1;
    public static final int ALL_CLIENT = 99;
    public static final int SERVER_SUCCESS = 100;
    public static final int SERVER_BREAK = 300;
    public static final int SERVER_FAILED = 400;
    public static boolean CONNECT_STATUS = false;

    private Context context;
    private TextView tv_showClient,tv_showMessage;
    private Button bt_monitor,bt_send;
    MyHandler(Context context,TextView tv_showClient,TextView tv_showMessage,Button bt_monitor,Button bt_send)
    {
        this.context = context;
        this.tv_showClient = tv_showClient;
        this.tv_showMessage = tv_showMessage;
        this.bt_monitor = bt_monitor;
        this.bt_send = bt_send;
    }

    @Override
    public void handleMessage(@NonNull Message msg)
    {
        switch (msg.what)
        {
            case SERVER_SUCCESS:
                CONNECT_STATUS = true;
                bt_send.setEnabled(true);
                bt_send.setTextColor(Color.RED);
                bt_monitor.setText("停止");
                bt_monitor.setTextColor(Color.RED);
                tv_showClient.setText("");
                Toast.makeText(context,"服务端开启！",Toast.LENGTH_LONG).show();
                break;
            case SERVER_BREAK:
                CONNECT_STATUS = false;
                bt_send.setEnabled(false);
                bt_send.setTextColor(Color.rgb(165,165,165));
                bt_monitor.setText("开始监听");
                bt_monitor.setTextColor(Color.BLACK);
                tv_showClient.setText("");
                Toast.makeText(context,"服务端断开！",Toast.LENGTH_LONG).show();
                break;
            case SERVER_FAILED:
                CONNECT_STATUS = false;
                bt_send.setEnabled(false);
                bt_send.setTextColor(Color.rgb(165,165,165));
                bt_monitor.setText("开始监听");
                bt_monitor.setTextColor(Color.BLACK);
                tv_showClient.setText("");
                Toast.makeText(context,"服务端初始化失败！",Toast.LENGTH_LONG).show();
                break;
            case READ_DATA:
                tv_showMessage.append(msg.obj.toString() + "\n");
                break;
            case ALL_CLIENT:
                tv_showClient.setText("");
                tv_showClient.append(msg.obj.toString());
                break;
        }
    }

}
