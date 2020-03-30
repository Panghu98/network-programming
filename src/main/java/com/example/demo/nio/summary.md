1.使用buffer的传输效率是明显高于不适用buffer进行IO传输的效率

2.channel和buffer的读写是相反的，一个读，则另外一个写

3.在大文件传输的时候，NIO的效率相对比BIO更高

>随着JDK版本的升级，BIO的底层也存在使用NIO进行实现的
>BIO1.3，NIO1.4，AIO1.7
