# Android_TCP

Android_TCP:Client and Server.

#2020/9/18：经反复使用真机、模拟器测试，代码是没有问题的，不能连接的大部分原因是因为局域网ip地址不正确的原因；
            模拟器的ip地址不是路由器分配ip的地址，所以当模拟器作为Server，Client使用Server获取的ip地址进行连接时是无法连接的；
            所以要么Client和Server同时使用真机并且在同一局域网下；要么Server使用真机，Client使用模拟器；Server不能用模拟器。
            
Server:服务端尽量使用真机测试
![Server](https://github.com/sunlong6666/Android_TCP/blob/master/picture/TCPserver.jpg)

Client:可以同时开启多个客户端连接Server
![Client](https://github.com/sunlong6666/Android_TCP/blob/master/picture/TCPclient.png)
