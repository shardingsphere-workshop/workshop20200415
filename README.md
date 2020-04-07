## 概览

在这个workshop中，我们会使用Netty去实现一个数据库，数据库的前端支持MySQL协议，后端使用CSV文件进行的存储。

## 快速入门

1. 启动Bootstrap.main
2. mysql -h 127.0.0.1 -P3307 -uroot -proot
3. mysql> select * from t_order;

## 实现原理

服务启动时，默认会加载`/data`目录下的csv文件，每个文件会映射成一个表，CSV文件中的第一行存储了表的元信息，例如：
```
order_id:long,user_id:int,status:string
1000001,10,"init"
2000001,20,"init"
3000001,30,"init"
```
服务启动后，会监听3307端口，可以通过MySQL客户端进行交互。服务端收到MySQL的命令后，将会执行下面的处理：
1. 根据MySQL协议读取ByteBuf中的payload数据（decode）
2. 解析payload，生成MySQLPacket，Packet中包含了SQL信息
3. 根据MySQLPacket的类型，调用CommandExecutor进行逻辑处理
4. CommandExecutor调用BackendHandler执行SQL的处理（**对CSV文件的读写在BackendHandler中进行**）
5. CommandExecutor将BackendHandler的执行结果包装为MySQLPacket后，写入ChannelHandler的pipeline中
6. 在pipeline中按照MySQL的协议返回数据给客户端（encode）

```
mysql> select * from t_order;
+----------+---------+--------+
| order_id | user_id | status |
+----------+---------+--------+
| 1000001  | 10      | init   |
| 2000001  | 20      | init   |
| 3000001  | 30      | init   |
+----------+---------+--------+
3 rows in set (0.05 sec)
```
