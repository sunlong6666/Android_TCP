package com.sunlong.github.client;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity
{

    private EditText et_ip,et_port,et_message;
    private Button bt_connect,bt_send;
    private TextView tv_showMessage;
    private MyClient client;
    private MyHandler handler;

    private String ip;//IP地址
    private String port;//端口号

    @Override
    protected void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        initView();

        bt_connect.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                //判断当前连接状态，如果正在连接，则断开当前连接
                if (MyHandler.CONNECT_STATUS)
                {
                    handler.sendEmptyMessage(MyHandler.CONNECT_BREAK);
                    client.closeAll();
                }
                else //如果未连接，则开始连接服务端
                {
                    ip = et_ip.getText().toString().trim();
                    port = et_port.getText().toString().trim();
                    if (!ip.equals("") && !port.equals(""))
                    {
                        bt_connect.setText("正在连接...");
                        new Thread(new Runnable()//开启新线程进行网络请求操作
                        {
                            @Override
                            public void run()
                            {
                                client.connect(ip,Integer.parseInt(port));
                            }
                        }).start();
                    }
                    else
                    {
                        Toast.makeText(MainActivity.this,"请输入ip地址或端口！",Toast.LENGTH_LONG).show();
                    }
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
                        client.writeServer(et_message.getText().toString());//开启新线程发送数据
                    }
                }).start();
            }
        });
    }

    /**
     * 实例化控件
     */
    private void initView()
    {
        et_ip = (EditText)findViewById(R.id.m_et_ip);
        et_port = (EditText)findViewById(R.id.m_et_port);
        et_message = (EditText)findViewById(R.id.m_et_message);
        bt_connect = (Button)findViewById(R.id.m_bt_connect);
        bt_send = (Button)findViewById(R.id.m_bt_send);
        tv_showMessage = (TextView)findViewById(R.id.m_tv_showMessage);
        tv_showMessage.setMovementMethod(ScrollingMovementMethod.getInstance());//允许TextView可以拖动
        handler = new MyHandler(tv_showMessage,bt_connect,bt_send,MainActivity.this);
        client = new MyClient(handler);
    }

    /**
     * 页面销毁时执行
     */
    @Override
    protected void onDestroy()
    {
        super.onDestroy();
        client.closeAll();
    }

}
