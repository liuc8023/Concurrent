### 2.1 创建和运行线程

#### 方法一 直接使用Thread

```java
package cn.itcast;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test1 {

    public static void main(String[] args) {
        //创建线程对象
        Thread t = new Thread("t1") {
            @Override
            public void run() {
                //要执行的任务
                log.info("running");
            }
        };
        //启动线程
        t.start();
        log.info("running");
    }
}

```

#### 方法二 使用Runnable配合Thread

```
把【线程】和【任务】（要执行的代码）分开

* Thread 代表线程
* Runnable 代表可运行的任务（线程要执行的代码）
```

```java
package cn.itcast;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunnableTest {
    public static void main(String[] args) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //要执行的任务
                log.debug("running");
            }
        };
        //创建线程对象
        Thread t = new Thread(r,"T2");
        //启动线程
        t.start();;
    }
}

````

<font size="2">Java8 以后可以使用lambda表达式精简代码</font>

```java
package cn.itcast;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunnableLambdaTest {
    public static void main(String[] args) {
        test1();
        test2();
    }

    public static void test1(){
        Runnable r = () ->{log.debug("running");};
        Thread t = new Thread(r,"test1");
        t.start();
    }

    public static void test2(){
        Thread t = new Thread(() ->{log.debug("running");},"test2");
        t.start();
    }
}

````

##### 原理之Thread与Runnable的关系

<font size="2">&ensp;&ensp;&ensp;&ensp;在实际工作中，我们很可能习惯性地选择Runnable或Thread之一直接使用，根本没在意二者的区别，但在面试中很多自以为是的菜货面试官会经常而且非常严肃的问出：请你解释下Runnable或Thread的区别？尤其是新手就容易上当，不知如何回答，就胡乱编一通。鄙人今天告诉你们这二者本身就没有本质区别，就是接口和类的区别。问出这个问题的面试官本身就是个二流子！如果非要说区别，请看如下：</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;Runnable的实现方式是实现其接口即可
Thread的实现方式是继承其类
Runnable接口支持多继承，但基本上用不到
Thread实现了Runnable接口并进行了扩展，而Thread和Runnable的实质是实现的关系，不是同类东西，所以Runnable或Thread本身没有可比性。
网络上流传的最大的一个错误结论：</font><font size ="2" color="red">**Runnable更容易可以实现多个线程间的资源共享，而Thread不可以！**</font> <font size="2">这是一个二笔的结论！网络得出此结论的例子如下：
</font>

```java
package cn.itcast;

public class Test {
    public static void main(String[] args) {
        new MyThread().start();
        new MyThread().start();

    }

    static class MyThread extends Thread{
        private int ticket = 5;
        @Override
        public void run(){
            while(true){
                System.out.println("Thread ticket = " + ticket--);
                if(ticket < 0){
                    break;
                }
            }
        }
    }
}
````

<font size="2">执行结果</font>

```
Thread ticket = 5
Thread ticket = 4
Thread ticket = 3
Thread ticket = 2
Thread ticket = 4
Thread ticket = 1
Thread ticket = 0
Thread ticket = 3
Thread ticket = 2
Thread ticket = 1
Thread ticket = 0
```

<font size="2">&ensp;&ensp;&ensp;&ensp;很显然，总共5张票但卖了10张。这就像两个售票员再卖同一张票，原因稍后分析。现在看看使用runnable的结果：
</font>

```java
package cn.itcast;

public class Test2 {
    public static void main(String[] args) {
        MyThread2 mt=new MyThread2();
        new Thread(mt).start();
        new Thread(mt).start();


    }
    static class MyThread2 implements Runnable{
        private int ticket = 5;
        @Override
        public void run(){
            while(true){
                System.out.println("Runnable ticket = " + ticket--);
                if(ticket < 0){
                    break;
                }
            }
        }
    }
}

````

<font size="2">执行结果</font>

```
Runnable ticket = 5
Runnable ticket = 4
Runnable ticket = 3
Runnable ticket = 2
Runnable ticket = 1
Runnable ticket = 0
```

<font size="2">&ensp;&ensp;&ensp;&ensp;嗯，嗯，大多数人都会认为结果正确了，而且会非常郑重的得出：Runnable更容易可以实现多个线程间的资源共享，而Thread不可以！ 真的是这样吗？大错特错！
Test这个例子结果多卖一倍票的原因根本不是因为Runnable和Thread的区别，看其中的如下两行代码：
</font>

```java
new MyThread().start();
new MyThread().start();
````

<font size="2">&ensp;&ensp;&ensp;&ensp;例子中，创建了两个MyThread对象，每个对象都有自己的ticket成员变量，当然会多卖1倍。如果把ticket定义为static类型，就离正确结果有近了一步（因为是多线程同时访问一个变量会有同步问题，加上锁才是最终正确的代码）。
现在看Test例子中，如下代码：
</font>

```java
MyThread2 mt=new MyThread2();
new Thread(mt).start();
new Thread(mt).start(); 
````

<font size="2">&ensp;&ensp;&ensp;&ensp;只创建了一个Runnable对象，肯定只卖一倍票（但也会有多线程同步问题，同样需要加锁），根本不是Runnable和Thread的区别造成的。再来看一个使用Thread方式的正确例子：
</font>

```java
package cn.itcast;

public class Test3  extends Thread {

    private int ticket = 10;
    @Override
    public void run(){
        for(int i =0;i<10;i++){
            synchronized (this){
                if(this.ticket>0){
                    try {
                        Thread.sleep(100);
                        System.out.println(Thread.currentThread().getName()+"卖票---->"+(this.ticket--));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] arg){
        Test3 t1 = new Test3();
        new Thread(t1,"线程1").start();
        new Thread(t1,"线程2").start();
    }
}

```

<font size="2">执行结果</font>

```
线程1卖票---->10
线程1卖票---->9
线程1卖票---->8
线程2卖票---->7
线程1卖票---->6
线程1卖票---->5
线程2卖票---->4
线程2卖票---->3
线程1卖票---->2
线程1卖票---->1
```

<font size="2">&ensp;&ensp;&ensp;&ensp;上例中只创建了一个Thread对象（子类Test3）,效果和Runnable一样。synchronized这个关键字是必须的，否则会出现同步问题，篇幅太长本文不做讨论。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;上面讨论下来，Thread和Runnable没有根本的没区别，只是写法不同罢了，事实是Thread和Runnable没有本质的区别，这才是正确的结论，和自以为是的大神所说的Runnable更容易实现资源共享，没有半点关系！
现在看下Thread源码
</font>

```java
public class Thread implements Runnable {
    /* Make sure registerNatives is the first thing <clinit> does. */
    private static native void registerNatives();
    static {
        registerNatives();
    }

    private volatile String name;
    private int            priority;
    private Thread         threadQ;
    private long           eetop;
```

<font size="2">&ensp;&ensp;&ensp;&ensp;可以看出，Thread实现了Runnable接口，提供了更多的可用方法和成员而已。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;结论，Thread和Runnable的实质是继承关系，没有可比性。无论使用Runnable还是Thread，都会new Thread，然后执行run方法。用法上，如果有复杂的线程操作需求，那就选择继承Thread，如果只是简单的执行一个任务，那就实现runnable。
再遇到二笔面试官问Thread和Runnable的区别，你可以直接鄙视了！
</font>

#### 方法三 FutureTask配合Thread

<font size="2">&ensp;&ensp;&ensp;&ensp;FutureTask 能够接收Callable类型的参数，用来处理有返回结果的情况
</font>

```java
package cn.itcast;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Slf4j
public class FutureTaskTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //创建任务对象
        FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("running");
                Thread.sleep(1000);
                return 1;
            }
        });
        Thread t = new Thread(task);
        //启动线程
        t.start();
        //获取task执行完毕的结果
        Integer result = task.get();
        log.info("线程返回值："+result);
    }
}

````

<font size="2">执行结果</font>

```
10:07:47.175 [Thread-0] DEBUG cn.itcast.FutureTaskTest - running
10:07:48.188 [main] INFO cn.itcast.FutureTaskTest - 线程返回值：1

Process finished with exit code 0
```

**分享一道面试题**

<font size="2">请你告诉我启动线程的三种方式 ?</font>

<font size="2">你说第一个：new Thread().start()； 第二个: new Thread(Runnable).start() 这没问题 ；那第三个呢，要回答线程池也是用的这两种之一，他这么问有些吹毛求疵的意思，你就可以说通过线程池也可以启动一个新的线程 3:Executors.newCachedThreadPool()或者FutureTask + Callable
</font>

### 2.2 查看进程线程的方法

#### windows
```
* 任务管理器可以查看进程和线程数，也可以用来杀死进程
* tasklist 查看进程
* taskkill 杀死进程
```

#### linux
```
* ps -ef或ps -fe 查看所有进程
* ps -ef|grep java 表示查看所有进程里 CMD 是 java 的进程信息
* ps -aux | grep java 显示所有进程里 CMD 是 java 的进程的状态
* kill -9 [PID] 表示强迫进程立即停止  
```

#### Java
```
* jps 查看所有Java进程
* jstack [PID] 查看某个Java进程（PID）的所有线程的状态
```

<font size="2">将下面的测试类上传到linux服务器上：</font>

```java
public class Test4 extends Thread {

    private int ticket = 1000000000;
    @Override
    public void run(){
        for(int i =0;i<1000000000;i++){
            synchronized (this){
                if(this.ticket>0){
                    try {
                        Thread.sleep(500);
                        System.out.println(Thread.currentThread().getName()+"卖票---->"+(this.ticket--));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] arg){
        Test4 t1 = new Test4();
        new Thread(t1,"线程1").start();
        new Thread(t1,"线程2").start();
    }
}
````

![image.png](http://jutibolg.oss-cn-shenzhen.aliyuncs.com/409/1624288932000/8b92713f2dd3406d80d768584bb7bc5f.png)

<font size="2">编译测试类Test4.java</font>

`[root@oracle java]# javac Test4.java`

<font size="2">编译后生成Test4.class文件</font>

![image.png](http://jutibolg.oss-cn-shenzhen.aliyuncs.com/409/1624288932000/2f8016cff8794050968efa5db2ef9274.png)

<font size="2">运行测试类</font>

`[root@oracle java]# java Test4`

![image.png](http://jutibolg.oss-cn-shenzhen.aliyuncs.com/409/1624288932000/92cf555b627d43739ba85d2c6ebf47a1.png)

<font size="2">通过命令查看进程</font>

`[root@oracle java]# ps -ef|grep java`

![image.png](http://jutibolg.oss-cn-shenzhen.aliyuncs.com/409/1624288932000/35cc358b80a6472dbf2a07da535e158c.png)