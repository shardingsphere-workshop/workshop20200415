## Overview

In this workshop, we will try to implement a database which compatible with MySQL protocol, storage the data in CSV file.

## Quick start

1. Run bootstrap.main in your IDE, which listening on 3307 port
2. mysql -h 127.0.0.1 -P3307 -uroot -proot
3. select * from t_order;

## Principle

when starting the server, csv files in /data directory will be loaded into `CSVLogicSchema`,
each csv file will be mapped to a database table, metadata will be extracted from the first line as below:
```
order_id:long,user_id:int,status:string
1000001,10,"init"
2000001,20,"init"
3000001,30,"init"
```
binary SQL command from mysql-client will be decoded as a plain SQL which continually parsed as SQLStatement using ANTLR, in the CSVQueryBackendHandler raw csv data
will be read into memory according to query grammar, then return to the client based on MySQL-protocol.

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
