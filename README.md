## DNS域名解析

<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585219432199.png" alt="1585219432199" style="zoom:50%;" />

<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585219393537.png" alt="1585219393537" style="zoom:50%;" />

> 浏览器向DNS客户端发起请求->DNS向根域名DNS服务器发送请求->根域名服务器将负责该域名解析的顶级域名DNS服务器信息发送给DNS客户端->DNS客户端向顶级域名发送请求
>
> 以递归的形式进行



**小结**： 递归是用户只向本地[DNS服务器](https://www.baidu.com/s?wd=DNS服务器&tn=44039180_cpr&fenlei=mv6quAkxTZn0IZRqIHckPjm4nH00T1YLuhPbm1PbmHTYmWc3PjRv0ZwV5Hcvrjm3rH6sPfKWUMw85HfYnjn4nH6sgvPsT6KdThsqpZwYTjCEQLGCpyw9Uz4Bmy-bIi4WUvYETgN-TLwGUv3En1Dkn16krjDL)发出请求，然后等待肯定或否定答案。而迭代是本地服务器向根[DNS服务器](https://www.baidu.com/s?wd=DNS服务器&tn=44039180_cpr&fenlei=mv6quAkxTZn0IZRqIHckPjm4nH00T1YLuhPbm1PbmHTYmWc3PjRv0ZwV5Hcvrjm3rH6sPfKWUMw85HfYnjn4nH6sgvPsT6KdThsqpZwYTjCEQLGCpyw9Uz4Bmy-bIi4WUvYETgN-TLwGUv3En1Dkn16krjDL)发出请求，而根[DNS服务器](https://www.baidu.com/s?wd=DNS服务器&tn=44039180_cpr&fenlei=mv6quAkxTZn0IZRqIHckPjm4nH00T1YLuhPbm1PbmHTYmWc3PjRv0ZwV5Hcvrjm3rH6sPfKWUMw85HfYnjn4nH6sgvPsT6KdThsqpZwYTjCEQLGCpyw9Uz4Bmy-bIi4WUvYETgN-TLwGUv3En1Dkn16krjDL)只是给出下一级DNS服务器的地址，然后本地DNS服务器再向下一级DNS发送查询请求直至得到最终答案。 





## 数据包

<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585222751494.png" alt="1585222751494" style="zoom:50%;" />

> 应用层数据包格式

## 网络编程概述

网络编程的实质其实就是进程之间的通信。

<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585222923408.png" alt="1585222923408" style="zoom:33%;" />

例如：Java对象经过序列化转换成流之后再进行反序列化

### java.io

<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585223059296.png" alt="1585223059296" style="zoom:50%;" />

> 其实字符流和字节流底层都是使用的字节来实现的，只是字符流更偏向于可读性，所以讲字节装换成为了字符

#### Java.io 之字符流



<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585224145409.png" alt="1585224145409" style="zoom: 50%;" />



<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585224188058.png" alt="1585224188058" style="zoom: 50%;" />

> 这些Reader是简历在之前的Reader之上的（装饰器模式），InputStreamReader就是字符流和字节流的连通，对应的就是这些Writer

#### Java.io 之字节流

<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585224511770.png" alt="1585224511770" style="zoom:50%;" />

<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585224581387.png" alt="1585224581387" style="zoom:50%;" />

> 也是和字符流的类似



 DataInputStrean和DataOutputStream主要适用于对基本数据类型的一个处理



#### 装饰器模式

![5719672](C:%5CUsers%5Cdengg%5CDesktop%5CIdeaProjects%5Cdesign_patterns%5Csrc%5Cmain%5Cjava%5Cstructure_patterns%5Cdecorator%5C.README_images%5C5719672.png)



### Socket的概述

Socket也是一种数据源

<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585226941570.png" alt="1585226941570" style="zoom:50%;" />

<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585227115244.png" alt="1585227115244" style="zoom:50%;" />

Unix中的Socket是什么？

- Unix系统中一切皆是文件
- 文件描述符是已打开文件的索引
- 每一个进程都会维护一个文件描述符表





### 同步异步阻塞非阻塞IO

基本概念没什么好讲的



### 网络通信中的线程池

如果是一个请求的话就需要创建一个线程的话，线程的创建和销毁都有较大的性能的消耗，

对此，可以通过创建线程池来解决这个问题，但是线程池的参数需要设置的很合理。



- 继承ExecutorService接口

<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585228158295.png" alt="1585228158295" style="zoom:33%;" />



- 使用Executors

<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585228200258.png" alt="1585228200258" style="zoom:33%;" />

通过Executors实际上还是构造一个ThreadPoolExecutor对象（继承自ExecutorService接口)

```
public ThreadPoolExecutor(int corePoolSize,
                              int maximumPoolSize,
                              long keepAliveTime,
                              TimeUnit unit,
                              BlockingQueue<Runnable> workQueue,
                              ThreadFactory threadFactory) {
        this(corePoolSize, maximumPoolSize, keepAliveTime, unit, workQueue,
             threadFactory, defaultHandler);
    }
```



## Socket和ServerSocket

ServerSocket：

![1585269551153](C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585269551153.png)

## BIO编程模型

<img src="C:%5CUsers%5Cdengg%5CAppData%5CRoaming%5CTypora%5Ctypora-user-images%5C1585275140940.png" alt="1585275140940" style="zoom:33%;" />

每次有请求过来的时候，ServerSocket的Acceptor（用主线程去实现）就会阻塞住，这样就不能同时响应多个请求，解决的方案就是<u>创建多个Handler -- 真正和客户端进行数据交换的线程</u>，用多个线程去响应请求 。



同时客户端也需要两个线程，一个线程用于获取消息，另外一个线程用于发送消息，等待控制台的输入操作

