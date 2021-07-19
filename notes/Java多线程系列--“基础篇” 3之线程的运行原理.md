## 一、栈与栈帧

<font size="2">Java Virtual Machine Stacks （Java 虚拟机栈）</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;我们都知道 JVM 中由堆、栈、方法区所组成，其中栈内存是给谁用的呢？其实就是线程，每个线程启动后，虚拟机就会为其分配一块栈内存。</font>

<font color="red">**每个栈由多个栈帧（Frame）组成，对应着每次方法调用时所占用的内存**</font>

<font color="red">**每个线程只能有一个活动栈帧，对应着当前正在执行的那个方法**</font>

## 二、运行过程

```java
package cn.itcast.n3;

public class TestFrames {
    public static void main(String[] args) {
        method1(10);
    }

    static void method1(int x) {
        int y = x + 1;
        Object m = method2();
        System.out.printf(m.toString());
    }

    static Object method2() {
        Object n = new Object();
        return n;
    }
}
```

```
1.将编译好的字节码加载到jvm的方法区内存中
2.jvm启动一个main的主线程，cpu核心就准备运行主线程的代码了，给主线程分配自己的栈内存【args、局部变量、返回地址、所记录】，每个线程的栈里面还有个程序计数器
	程序计数器的作用:当cpu要执行哪行代码了，就去这个里面去要
3.把主方法的里面代码行放到程序计数器
4.主方法调用的是method1的方法，为method1分配栈内存，里面存储这个方法里面局部变量，返回地址，这些变量是分配内存时，会把空间预留好
5.将method1的第一行读到程序计数器让cpu执行
6.methode1下一行调用method2()方法，创建他的栈内存
7.把Object n = new Object()这行代码读取到计数器，在队中创建对象
8.method2()将返回地址给m，方法执行完就可以释放掉method2()的栈内存
9.一层层方法结束后，依次释放掉每个方法线程
```

![1588225202003172222274681802531979.png](http://jutibolg.oss-cn-shenzhen.aliyuncs.com/409/1624257824000/02227f793ef8437e940ed4b8b3a0382a.png)

## 三、多线程执行逻辑

```java
public class frameTest {
 
    public static void main(String[] args) {
        //线程t1
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                method1(20);
            }
        });
        t1.start();
        //主线程
        method1(10);
    }
 
    static void method1(int x) {
        int y = x + 1;
        Object m = method2();
        System.out.printf(m.toString());
    }
 
    static Object method2() {
        Object n = new Object();
        return n;
    }
}
```

![1588225202003172230178501308041811.png](http://jutibolg.oss-cn-shenzhen.aliyuncs.com/409/1624257824000/9f9552cb60b141c6bc9c4b6dbc206544.png)

![1588225202003172236583191837242489.png](http://jutibolg.oss-cn-shenzhen.aliyuncs.com/409/1624257824000/4be25ca5d84c4a9c92b5358906f55b9f.png)

## 四、线程上下文切换（Thread Context Switch）
### 4.1、CPU时间片

    CPU时间片即CPU分配给每个线程的执行时间段，称作它的时间片。CPU时间片一般为几十毫秒(ms)。

### 4.2、什么是上下文切换

<font size="2">&ensp;&ensp;&ensp;&ensp;CPU通过时间片段的算法来循环执行线程任务，而循环执行即每个线程允许运行的时间后的切换，而这种循环的切换使各个程序从表面上看是同时进行的。而切换时会保存之前的线程任务状态，当切换到该线程任务的时候，会重新加载该线程的任务状态。而这个从保存到加载的过程称之为上下文切换。
</font>
```
    若当前线程还在运行而时间片结束后，CPU将被剥夺并分配给另一个线程。
    若线程在时间片结束前阻塞或结束，CPU进行线程切换。而不会造成CPU资源浪费。
```
&ensp;&ensp;&ensp;
<font size="2"><font color="red">**因为以下一些原因导致 cpu 不再执行当前的线程，转而执行另一个线程的代码**</font></font>

```
- 线程的 cpu 时间片用完
- 垃圾回收
- 有更高优先级的线程需要运行
- 线程自己调用了 sleep、yield、wait、join、park、synchronized、lock 等方法
```

**原理：**

```
- 当 Context Switch 发生时，需要由操作系统保存当前线程的状态，并恢复另一个线程的状态，Java 中对应的概念
  就是程序计数器（Program Counter Register），它的作用是记住下一条 jvm 指令的执行地址，是线程私有的
- 状态包括程序计数器、虚拟机栈中每个栈帧的信息，如局部变量、操作数栈、返回地址等
- Context Switch 频繁发生会影响性能
  
```



### 4.3、上下文切换造成的影响

<font size="2">&ensp;&ensp;&ensp;&ensp;我们可以通过对比串联执行和并发执行进行对比。</font>

```java
    private static final long count = 1000000;

    public static void main(String[] args) throws Exception {
        concurrency();
        series();
    }
    /**
     * 并发执行
     * @throws Exception
     */
    private static void concurrency() throws Exception {
        long start = System.currentTimeMillis();
        //创建线程执行a+=
        Thread thread = new Thread(new Runnable() {
            public void run() {
                int a = 0;
                for (int i = 0; i < count; i++) {
                    a += 1;
                }
            }
        });
        //启动线程执行
        thread.start();
        //使用主线程执行b--;
        int b = 0;
        for (long i = 0; i < count; i++) {
            b--;
        }
        //合并线程，统计时间
        thread.join();
        long time = System.currentTimeMillis() - start;
        System.out.println("Concurrency：" + time + "ms, b = " + b);
    }
    /**
     * 串联执行
     */
    private static void series() {
        long start = System.currentTimeMillis();
        int a = 0;
        for (long i = 0; i < count; i++) {
            a += 1;
        }
        int b = 0;
        for (int i = 0; i < count; i++) {
            b--;
        }
        long time = System.currentTimeMillis() - start;
        System.out.println("Serial：" + time + "ms, b = " + b + ", a = " + a);
    }
```
<font size="2">&ensp;&ensp;&ensp;&ensp;通过修改循环次数,对比串行运行和并发运行的时间测试结果：</font>

| 循环次数 | 并发执行时间 | 串联执行时间 |
| -------- | ------------ | ------------ |
| 一百万   | 2ms          | 4ms          |
| 十万     | 2ms          | 2ms          |
| 一万     | 1ms          | 0ms          |

<font size="2">&ensp;&ensp;&ensp;&ensp;通过数据的对比我们可以看出。在一万以下的循环次数时，串联的执行速度比并发的执行速度块。是因为线程上下文切换导致额外的开销。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;在Linux系统下可以使用vmstat命令来查看上下文切换的次数，如果要查看上下文切换的时长，可以利用Lmbench3，这是一个性能分析工具。
</font>

### 4.4、如何减少上下文切换导致额外的开销

<font size="2">&ensp;&ensp;&ensp;&ensp;减少上下文切换次数便可以提高多线程的运行效率。减少上下文切换的方法有无锁并发编程、CAS算法、避免创建过多的线程和使用协程。</font>

```
- 无锁并发编程.当任何特定的运算被阻塞的时候，所有CPU可以继续处理其他的运算。换种方式说，在无锁系统中，当给定线程被其他线程阻塞的时候，所有CPU可以不停的继续处理其他工作。无锁算法大大增加系统整体的吞吐量，因为它只偶尔会增加一定的交易延迟。大部分高端数据库系统是基于无锁算法而构造的，以满足不同级别。

- CAS算法。Java提供了一套原子性操作的数据类型（java.util.concurrent.atomic包下），使用CAS算法来更新数据，不需要加锁。如:AtomicInteger、AtomicLong等。

- 避免创建过多的线程。如任务量少时，尽可能减少创建线程。对于某个时间段任务量很大的这种情况，我们可以通过线程池来管理线程的数量，避免创建过多线程。

- 协程：即协作式程序，其思想是，一系列互相依赖的协程间依次使用CPU，每次只有一个协程工作，而其他协程处于休眠状态。如：JAVA中使用wait和notify来达到线程之间的协同工作。
```

