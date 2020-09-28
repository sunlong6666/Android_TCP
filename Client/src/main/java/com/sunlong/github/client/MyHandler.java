package com.sunlong.github.client;

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

    public static final int READ_DATA = 1;//读取到消息
    public static final int WRITE_DATA = 99;//发送消息
    public static final int CONNECT_SUCCESS = 100;//连接成功
    public static final int CONNECT_BREAK = 300;//断开连接
    public static final int CONNECT_FAILED = 400;//连接失败
    public static boolean CONNECT_STATUS = false;//当前与服务端连接状态

    private TextView tv_show;
    private Button bt_connect,bt_send;
    private Context context;

    MyHandler(TextView tv_show, Button bt_connect, Button bt_send, Context context)
    {
        this.tv_show = tv_show;
        this.bt_connect = bt_connect;
        this.bt_send = bt_send;
        this.context = context;
    }

    @Override
    public void handleMessage(@NonNull Message msg)
    {
        switch (msg.what)
        {
            case CONNECT_SUCCESS:
                Toast.makeText(context,"连接成功！",Toast.LENGTH_LONG).show();
                CONNECT_STATUS = true;
                bt_connect.setText("断开连接");
                bt_connect.setTextColor(Color.RED);
                bt_send.setTextColor(Color.RED);
                bt_send.setEnabled(true);//发送按钮生效
                break;
            case CONNECT_BREAK:
                Toast.makeText(context,"连接断开！",Toast.LENGTH_LONG).show();
                CONNECT_STATUS = false;
                bt_connect.setText("连接");
                bt_connect.setTextColor(Color.BLACK);
                bt_send.setTextColor(Color.rgb(165,165,165));
                bt_send.setEnabled(false);//发送按钮失效
                break;
            case CONNECT_FAILED:
                Toast.makeText(context,"连接失败！",Toast.LENGTH_LONG).show();
                CONNECT_STATUS = false;
                bt_connect.setText("连接");
                bt_connect.setTextColor(Color.BLACK);
                bt_send.setTextColor(Color.rgb(165,165,165));
                bt_send.setEnabled(false);
                break;
            case READ_DATA:
                tv_show.append("Server：" + msg.obj.toString() + "\n");
                break;
            case WRITE_DATA:
                tv_show.append("Client：" +msg.obj.toString() + "\n");
                break;
        }
    }

}
