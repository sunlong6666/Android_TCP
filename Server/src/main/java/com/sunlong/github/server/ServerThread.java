package com.sunlong.github.server;

import android.os.Message;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

public class ServerThread implements Runnable
{

    private String name;
    boolean flag = true;
    private InputStream inputStream;
    private OutputStream outputStream;
    private Message message;
    private Socket socket;
    private MyHandler handler;

    ServerThread(Socket socket,MyHandler handler)
    {
        this.socket = socket;
        this.handler = handler;
        name = socket.getInetAddress().getHostName();
        message = new Message();
        message.what = MyHandler.READ_DATA;
        message.obj = name+" 连接到客户端！";
        handler.sendMessage(message);
        showAllClient();
    }

    ServerThread()
    {

    }

    @Override
    public void run()
    {
        try
        {
            inputStream = socket.getInputStream();
            while (flag)
            {
                byte[] temByte = new byte[1024];
                int i = inputStream.read(temByte);
                if(i == -1)
                {
                    flag = false;
                    message = new Message();
                    message.what = MyHandler.READ_DATA;
                    message.obj = name+" 断开连接！";
                    handler.sendMessage(message);
                    sendAll(name+" 断开连接！");
                    //移除没连接上的客户端
                    synchronized (MyServer.sockets)
                    {
                        MyServer.sockets.remove(socket);//从集合里移除断开连接的客户端
                    }
                    socket.close();
                    inputStream.close();
                    showAllClient();
                    return;
                }
                byte[] data = new byte[i];
                System.arraycopy(temByte,0,data,0,i);
                String readTextString = new String(data,"UTF-8");
                message = new Message();
                message.what = MyHandler.READ_DATA;
                message.obj = name+":"+readTextString;
                handler.sendMessage(message);
                sendAll("("+this.name+"）: "+readTextString);
            }
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
    }

    /**
     * 向所有在线客户端发消息
     * @param msg
     * @throws IOException
     */
    public void sendAll(String msg)
    {
        synchronized (MyServer.sockets)
        {
            for (Socket socket : MyServer.sockets)
            {
                try
                {
                    outputStream = socket.getOutputStream();
                    byte[] data = msg.getBytes("UTF-8");
                    outputStream.write(data);
                    outputStream.flush();
                }
                catch (IOException e)
                {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 遍历当前所有
     */
    private void showAllClient()
    {
        StringBuffer nameAll = new StringBuffer();
        for (int i = 0; i < MyServer.sockets.size() ; i++)
        {
            nameAll.append( MyServer.sockets.get(i).getLocalSocketAddress().toString()+" ("+name+")"+"\n");
        }
        message = new Message();
        message.what = MyHandler.ALL_CLIENT;
        message.obj = nameAll;
        handler.sendMessage(message);
    }

    public void closeAll()
    {
        for (Socket s : MyServer.sockets)
        {
            try
            {
                flag = false;
                if (s != null) s.close();
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
            catch (IOException e)
            {
                e.printStackTrace();
            }
        }
    }

}
