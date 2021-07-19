### 1、守护线程和用户线程的区别
<font size="2">Java 提供了两种类型的线程：守护线程 和 用户线程
</font>
```
- 用户线程:是高优先级线程。JVM 会在终止之前等待任何用户线程完成其任务。例如：程序的主线程，连接网络的子线程，我们平常创建的普通线程。
- 守护线程:是低优先级线程。其唯一作用是为用户线程提供服务。例如 gc，finalizer 等
```
<font size="2">&ensp;&ensp;&ensp;&ensp;由于守护线程的作用是为用户线程提供服务，并且仅在用户线程运行时才需要，因此一旦所有用户线程完成执行，JVM 就会终止。也就是说 守护线程不会阻止 JVM 退出。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;这也是为什么通常存在于守护线程中的无限循环不会导致问题，因为任何代码（包括 finally 块 ）都不会在所有用户线程完成执行后执行。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;这也是为什么我们并不推荐 在守护线程中执行 I/O 任务 。因为可能导致无法正确关闭资源。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;但是，守护线程并不是 100% 不能阻止 JVM 退出的。守护线程中设计不良的代码可能会阻止 JVM 退出。例如，在正在运行的守护线程上调用Thread.join() 可以阻止应用程序的关闭。
</font>

### 2、守护线程能用来做什么？
<font size="2">&ensp;&ensp;&ensp;&ensp;常见的做法，就是将守护线程用于后台支持任务，比如垃圾回收、释放未使用对象的内存、从缓存中删除不需要的条目。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;咦，按照这个解释，那么大多数 JVM 线程都是守护线程。
</font>

### 3、如何创建守护线程 ？
<font size="2">&ensp;&ensp;&ensp;&ensp;守护线程也是一个线程，因此它的创建和启动其实和普通线程没什么区别。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;要将普通线程设置为守护线程，方法很简单，只需要调用 Thread.setDaemon() 方法即可。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;例如下面这段代码，假设我们继承 Thread 类创建了一个新类 NewThread 。那么我们就可以创建这个类的实例并设置为守护线程
</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadDaemon implements Runnable{
    @Override
    public void run() {
        log.info("running...");
    }

    public static void main(String[] args) {
        //isDaemon 判断线程是否为守护线程，如果返回true，表示该线程为守护线程，否则为用户线程
        log.info(Thread.currentThread().getName()+"线程是否为守护线程："+Thread.currentThread().isDaemon());
        ThreadDaemon r = new ThreadDaemon();
        Thread t = new Thread(r);
        log.info(t.getName()+"线程是否为守护线程："+t.isDaemon());
        t.setDaemon(true);
        log.info(t.getName()+"线程是否为守护线程："+t.isDaemon());
        t.start();
    }
}
```
<font size="2">执行结果</font>

```
16:33:24.420 [main] INFO cn.itcast.methods.ThreadDaemon - main线程是否为守护线程：false
16:33:24.420 [main] INFO cn.itcast.methods.ThreadDaemon - Thread-0线程是否为守护线程：false
16:33:24.420 [main] INFO cn.itcast.methods.ThreadDaemon - Thread-0线程是否为守护线程：true
16:33:24.420 [Thread-0] INFO cn.itcast.methods.ThreadDaemon - running...
```
<font size="2">
&ensp;&ensp;&ensp;&ensp;在 Java 语言中，线程的状态是自动继承的。任何线程都会继承创建它的线程的守护程序状态。怎么理解呢？
</font>

```
    如果一个线程是普通线程（ 用户线程） ，那么它创建的子线程默认也是普通线程（ 用户线程 ）。
    如果一个线程是守护线程，那么它创建的子线程默认也是守护线程。
```

<font size="2">&ensp;&ensp;&ensp;&ensp;因此，我们可以推演出： 由于主线程是用户线程，因此在 main() 方法内创建的任何线程默认为用户线程。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;需要注意的是调用 setDaemon() 方法的时机，该方法只能在创建 Thread 对象并且在启动线程前调用。在线程运行时尝试调用 setDaemon() 将抛出 IllegalThreadStateException 异常。
</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadDaemon implements Runnable{
    @Override
    public void run() {
        log.info("running...");
    }

    public static void main(String[] args) {
        //isDaemon 判断线程是否为守护线程，如果返回true，表示该线程为守护线程，否则为用户线程
        log.info(Thread.currentThread().getName()+"线程是否为守护线程："+Thread.currentThread().isDaemon());
        ThreadDaemon r = new ThreadDaemon();
        Thread t = new Thread(r);
        log.info(t.getName()+"线程是否为守护线程："+t.isDaemon());
        t.start();
        t.setDaemon(true);
        log.info(t.getName()+"线程是否为守护线程："+t.isDaemon());

    }
}
```
<font size="2">执行结果</font>

```
16:35:00.947 [main] INFO cn.itcast.methods.ThreadDaemon - main线程是否为守护线程：false
16:35:00.947 [main] INFO cn.itcast.methods.ThreadDaemon - Thread-0线程是否为守护线程：false
16:35:00.955 [Thread-0] INFO cn.itcast.methods.ThreadDaemon - running...
Exception in thread "main" java.lang.IllegalThreadStateException
	at java.lang.Thread.setDaemon(Thread.java:1359)
	at cn.itcast.methods.ThreadDaemon.main(ThreadDaemon.java:19)
```

### 守护线程注意点
```
- thread.setDaemon(true)必须在thread.start()之前设置，否则会跑出一个IllegalThreadStateException异常。你不能把正在运行的常规线程
设置为守护线程。
- 在Daemon线程中产生的新线程也是Daemon的。 
- 不要认为所有的应用都可以分配给Daemon来进行服务，比如读写操作或者计算逻辑。 
```
<font size="2">&ensp;&ensp;&ensp;&ensp;因为你不可能知道在所有的用户线程完成之前，守护线程是否已经完成了预期的服务任务。一旦用户线程退出了，可能大量数据还没有来得及读入或写出，计算任务也可能多次运行结果不一样。这对程序是毁灭性的。造成这个结果理由已经说过了：一旦所用户有线程离开了，虚拟机也就退出运行了。
</font>

**未设置守护线程**
```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 完成文件输出的守护线程任务
 */
@Slf4j
public class ThreadDaemonIo implements Runnable{

    @Override
    public void run() {
        try {
            //守护线程阻塞1秒后运行
            Thread.sleep(1000);
            File f=new File("daemon.txt");
            FileOutputStream os = new FileOutputStream(f,true);
            os.write("daemon".getBytes());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ThreadDaemonIo r = new ThreadDaemonIo();
        Thread t = new Thread(r);
        t.start();
    }
}
```
<font size="2">执行结果</font>

![20210713170720.png](http://jutibolg.oss-cn-shenzhen.aliyuncs.com/409/1626167357000/76d986ebbbda44019c139b99824cf2a6.png)

**设置守护线程**

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 完成文件输出的守护线程任务
 */
@Slf4j
public class ThreadDaemonIo implements Runnable{

    @Override
    public void run() {
        try {
            //守护线程阻塞1秒后运行
            Thread.sleep(1000);
            File f=new File("daemon.txt");
            FileOutputStream os = new FileOutputStream(f,true);
            os.write("daemon".getBytes());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ThreadDaemonIo r = new ThreadDaemonIo();
        Thread t = new Thread(r);
        //设置守护线程
        t.setDaemon(true);
        t.start();
    }
}
```
<font size="2">执行结果</font>
![20210713171410.png](http://jutibolg.oss-cn-shenzhen.aliyuncs.com/409/1626167357000/9192de27023c4e6fa8cecb82753488f6.png)
<font size="2">&ensp;&ensp;&ensp;&ensp;看到了吧，把输入输出逻辑包装进守护线程多么的可怕，文件没生成且字符串也没有写入指定文件。原因也很简单，直到主线程完成，守护线程仍处于1秒的阻塞状态。这个时候主线程很快就运行完了，虚拟机退出，守护线程停止服务，输出操作自然失败了。
</font>

### 总结
<font size="2">**定义：** 守护线程--也称“服务线程”，在没有用户线程可服务时会自动离开。
优先级：守护线程的优先级比较低，用于为系统中的其它对象和线程提供服务。</font>

<font size="2">**设置：** 通过setDaemon(true)来设置线程为“守护线程”；将一个用户线程设置为
守护线程的方式是在 线程对象创建 之前 用线程对象的setDaemon方法。</font>

<font size="2">**例如:** 垃圾回收线程就是一个经典的守护线程，当我们的程序中不再有任何运行的
Thread,程序就不会再产生垃圾，垃圾回收器也就无事可做，所以当垃圾回收线程是
JVM上仅剩的线程时，垃圾回收线程会自动离开。它始终在低级别的状态中运行，用于
实时监控和管理系统中的可回收资源。</font>

<font size="2">**生命周期：** 守护进程（Daemon）是运行在后台的一种特殊进程。它独立于控制终端并且
周期性地执行某种任务或等待处理某些发生的事件。也就是
说守护线程不依赖于终端，但是依赖于系统，与系统“同生共死”。那Java的守护线程是
什么样子的呢。当JVM中所有的线程都是守护线程的时候，JVM就可以退出了；如果还有一个
或以上的非守护线程则JVM不会退出。
</font>

