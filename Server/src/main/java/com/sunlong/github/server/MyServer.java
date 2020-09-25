package com.sunlong.github.server;

import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;

public class MyServer
{

    private boolean flag = true;
    private ServerSocket serverSocket;
    protected static ArrayList<Socket> sockets = new ArrayList<>();//当前在线客户端集合

    private MyHandler handler;
    MyServer(MyHandler handler)
    {
        this.handler = handler;
    }

    public void initServer(int port) throws IOException
    {
        serverSocket = new ServerSocket(port);
        handler.sendEmptyMessage(MyHandler.SERVER_SUCCESS);
        while (flag)
        {
            try
            {
                Socket socket = serverSocket.accept();
                synchronized (sockets)
                {
                    sockets.add(socket);//加入到集合
                }
                //开启线程创建服务器
                Thread thread = new Thread(new ServerThread(socket,handler));
                thread.start();
            }
            catch (Exception e)
            {
                e.printStackTrace();
                flag = false;
            }
        }
    }

    public void closeMyServer()
    {
        try
        {
            //关闭服务器
            serverSocket.close();
        }
        catch (IOException e)
        {
            e.printStackTrace();
            handler.sendEmptyMessage(MyHandler.SERVER_FAILED);
        }
    }

}
