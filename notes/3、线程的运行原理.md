## 一、栈与栈帧

Java Virtual Machine Stacks （Java 虚拟机栈）

我们都知道 JVM 中由堆、栈、方法区所组成，其中栈内存是给谁用的呢？其实就是线程，每个线程启动后，虚拟

机就会为其分配一块栈内存。

**每个栈由多个栈帧（Frame）组成，对应着每次方法调用时所占用的内存**

**每个线程只能有一个活动栈帧，对应着当前正在执行的那个方法**

## 二、运行过程

```cpp hljs
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

```cpp hljs
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

**因为以下一些原因导致 cpu 不再执行当前的线程，转而执行另一个线程的代码**

1.线程的 cpu 时间片用完

2.垃圾回收

3.有更高优先级的线程需要运行

4.线程自己调用了 sleep、yield、wait、join、park、synchronized、lock 等方法

**原理：**

1.当 Context Switch 发生时，需要由操作系统保存当前线程的状态，并恢复另一个线程的状态，Java 中对应的概念

就是程序计数器（Program Counter Register），它的作用是记住下一条 jvm 指令的执行地址，是线程私有的

2.状态包括程序计数器、虚拟机栈中每个栈帧的信息，如局部变量、操作数栈、返回地址等

3.Context Switch 频繁发生会影响性能

