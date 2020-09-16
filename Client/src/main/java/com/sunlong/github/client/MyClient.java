package com.sunlong.github.client;

import android.os.Message;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.io.IOException;

public class MyClient
{

    private boolean flag = false;//客户端连接状态
    private InputStream inputStream;//Socket输入流
    private OutputStream outputStream;//Socket输出流
    private Message message;
    private Socket socket;

    private MyHandler handler;
    MyClient(MyHandler handler)
    {
        this.handler = handler;
    }

    public void connect(String ip,int port)
    {
        try
        {
            socket = new Socket();
            InetSocketAddress netSocket = new InetSocketAddress(ip,port);
            socket.connect(netSocket,4000);//进行网络请求，请求时间超过4秒就报错
            inputStream = socket.getInputStream();
            outputStream = socket.getOutputStream();
            handler.sendEmptyMessage(MyHandler.CONNECT_SUCCESS);
            flag = true;
            readServer();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            flag = false;
            handler.sendEmptyMessage(MyHandler.CONNECT_FAILED);
        }
    }

    /**
     * 开始接收数据
     */
    private void readServer()
    {
        while (flag)
        {
            try
            {
                byte[] temByte = new byte[1024];
                int i = inputStream.read(temByte);
                if (i == -1)//i=-1代表服务端断开连接
                {
                    flag = false;
                    handler.sendEmptyMessage(MyHandler.CONNECT_BREAK);
                    closeAll();
                    return;
                }
                byte[] data = new byte[i];
                System.arraycopy(temByte, 0, data, 0, i);
                String readText = new String(data, "UTF-8");
                message = new Message();
                message.what = MyHandler.READ_DATA;
                message.obj = readText;
                handler.sendMessage(message);
            }
            catch (IOException e)
            {
                e.printStackTrace();
                flag = false;
                handler.sendEmptyMessage(MyHandler.CONNECT_BREAK);
            }
        }
    }

    /**
     * 向服务端发送消息
     * @param messageText 发送内容
     */
    public void writeServer(String messageText)
    {
        try
        {
            if (!flag || !MyHandler.CONNECT_STATUS)return;
            byte[] data = messageText.getBytes();
            outputStream.write(data);
            outputStream.flush();
            message = new Message();
            message.what = MyHandler.WRITE_DATA;
            message.obj = messageText;
            handler.sendMessage(message);
        }
        catch (IOException e)
        {
            e.printStackTrace();
            flag = false;
            handler.sendEmptyMessage(MyHandler.CONNECT_BREAK);
        }
    }

    /**
     * 关闭socket、输入流和输出流
     */
    public void closeAll()
    {
        try
        {
            flag = false;
            if (socket != null) socket.close();
            if (inputStream != null) inputStream.close();
            if (outputStream != null) outputStream.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

}
