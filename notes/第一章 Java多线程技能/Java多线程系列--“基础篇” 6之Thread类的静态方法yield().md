**作用：**

```   
    暂停当前正在执行的线程对象（及放弃当前拥有的cup资源），并执行其他线程。yield()做的是让当前运行线程回到可运行状态，以允许具有相同优
先级的其他线程获得运行机会。
线程状态变化：运行 --》 就绪
```

**注意：**

```
    使用yield()的目的是让相同优先级的线程之间能适当的轮转执行。但是，实际中无法保证yield()达到让步目的，因为让步的线程还有可能被线程
调度程序再次选中。
```

```
跟sleep()方法一样，会让出CPU的执行权，但不会释放锁。
跟sleep()方法不同的是：yield方法只能让拥有相同优先级的线程有获取CPU执行时间的机会。还有就是它并不能控制具体的交出CPU的时间，因为yield() 
方法只是使当前线程重新回到就绪态，所以执行 yield()的线程有可能在进入到可执行状态后马上又被执行。
```

<font size="2">&ensp;&ensp;&ensp;&ensp;对于很多初学 Java 线程的小伙伴们，很容易将 Thread 类里的 yield() 方法理解错误，或者理解得不够透彻，先看下源码：
</font>

```java
/**
     * A hint to the scheduler that the current thread is willing to yield
     * its current use of a processor. The scheduler is free to ignore this
     * hint.
     *
     * <p> Yield is a heuristic attempt to improve relative progression
     * between threads that would otherwise over-utilise a CPU. Its use
     * should be combined with detailed profiling and benchmarking to
     * ensure that it actually has the desired effect.
     *
     * <p> It is rarely appropriate to use this method. It may be useful
     * for debugging or testing purposes, where it may help to reproduce
     * bugs due to race conditions. It may also be useful when designing
     * concurrency control constructs such as the ones in the
     * {@link java.util.concurrent.locks} package.
     */
    public static native void yield();
```

<font size="2">&ensp;&ensp;&ensp;&ensp;是的，你没看错，Thread 类源码中定义的这个方法没有方法体，native 关键字修饰的方法表示原生态方法，方法对应的实现不在这个类文件里，而是在用其他语言（如 C 和 C++）实现的文件中。Java 语言本身不能对操作系统底层进行访问和操作（但可以通过 JNI 接口调用其他语言来实现对底层的访问）。所以，想要知道并理解 yield() 方法的具体实现过程，于初学者来说并没有必要。虽然“无代码无真相”，但采用大白话的解释或许更加通俗易懂吧。
</font>

#### 1、Java线程调度的一点背景

<font size="2">&ensp;&ensp;&ensp;&ensp;在各种各样的线程中，Java 虚拟机必须实现一个有优先权的、基于优先级的调度程序。这意味着 Java 程序中的每一个线程被分配到一定的优先权，使用定义好的范围内的一个正整数表示。优先级可以被开发者改变。即使线程已经运行了一定时间，Java 虚拟机也不会改变其优先级。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;优先级的值很重要，因为 Java 虚拟机和下层的操作系统之间的约定是操作系统必须选择有最高优先权的 Java 线程运行。所以我们说 Java 实现了一个基于优先权的调度程序。该调度程序使用一种有优先权的方式实现，这意味着当一个有更高优先权的线程到来时，无论低优先级的线程是否在运行，都会中断(抢占)它。这个约定对于操作系统来说并不总是这样，这意味着操作系统有时可能会选择运行一个更低优先级的线程。
</font>

#### 2、理解线程的优先权

<font size="2">&ensp;&ensp;&ensp;&ensp;接下来，理解线程优先级是多线程学习很重要的一步，尤其是了解 yield() 函数的工作过程：
</font>

```
1、记住当线程的优先级没有指定时，所有线程都携带普通优先级。
2、优先级可以用从 1 到 10 的范围指定。10 表示最高优先级，1 表示最低优先级，5 是普通优先级。
3、记住优先级最高的线程在执行时被给予优先。但是不能保证线程在启动时就进入运行状态。
4、与在线程池中等待运行机会的线程相比，当前正在运行的线程可能总是拥有更高的优先级。
5、由调度程序决定哪一个线程被执行。
6、t.setPriority() 用来设定线程的优先级。
7、记住在线程 start() 方法被调用之前，线程的优先级应该被设定。
8、你可以使用常量，如 MIN_PRIORITY，MAX_PRIORITY，NORM_PRIORITY 来设定优先级。
```

<font size="2">&ensp;&ensp;&ensp;&ensp;现在，我们对线程调度和线程优先级有一定理解了，进入主题。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;Thread.yield() 方法会使当前线程从执行状态（运行状态）变为可执行状态（就绪状态）。CPU 会从众多的可执行态里选择，也就是说，</font><font size="2"><font color="red">**当前也就是刚刚调用 yield() 方法的那个线程还是有可能会被再次继续执行。yield() 方法并不是让当前线程暂停，让出时间片去执行其他线程，而在下一次时间片内就一定不会执行了（当前线程只是转换为就绪状态，在下一个本该是自己的却让给其他线程的时间片内也可能再次继续被执行）。**</font></font>

<font size="2">&ensp;&ensp;&ensp;&ensp;很多人将 yield 翻译成线程让步。顾名思义，就是说当一个线程使用了这个方法之后，它就会把自己CPU执行的一段时间片让给自己或者其它的线程运行。
</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 线程让行方法
 */
@Slf4j
public class ThreadYield implements Runnable{
    @Override
    public void run() {
        // 循环打印
        for (int i = 0 ; i < 10 ; i++ ){
            // 打印内容
            log.info(Thread.currentThread().getName()+"======threadYield======"+i);
            // 当i为5时，该线程就会把CPU时间让掉，让其他或者自己的线程执行（也就是谁先抢到谁执行）
            if (i == 5) {
                log.info(Thread.currentThread().getName()+"线程让行");
                // 线程让行
                Thread.yield();
            }

        }
    }
    /**
     * 作用：暂停当前正在执行的线程对象（及放弃当前拥有的cup资源），并执行其他线程。yield()做的是让当前运行线程回到可运行状态，
     *      以允许具有相同优先级的其他线程获得运行机会。
     * 线程状态变化：运行 --》 就绪
     * 注意：使用yield()的目的是让相同优先级的线程之间能适当的轮转执行。
     *      但是，实际中无法保证yield()达到让步目的，因为让步的线程还有可能被线程调度程序再次选中。
     */
    public static void main(String[] args) {
        Runnable r = new ThreadYield();
        Thread t = new Thread(r,"线程一");
        Runnable r1 = new ThreadYield();
        Thread t1 = new Thread(r1,"线程二");
        t.start();
        t1.start();
    }
}
```

<font size="2">执行结果</font>

```
16:06:38.380 [线程二] INFO cn.itcast.methods.ThreadYield - 线程二======threadYield======0
16:06:38.380 [线程一] INFO cn.itcast.methods.ThreadYield - 线程一======threadYield======0
16:06:38.383 [线程二] INFO cn.itcast.methods.ThreadYield - 线程二======threadYield======1
16:06:38.384 [线程一] INFO cn.itcast.methods.ThreadYield - 线程一======threadYield======1
16:06:38.384 [线程二] INFO cn.itcast.methods.ThreadYield - 线程二======threadYield======2
16:06:38.384 [线程一] INFO cn.itcast.methods.ThreadYield - 线程一======threadYield======2
16:06:38.384 [线程二] INFO cn.itcast.methods.ThreadYield - 线程二======threadYield======3
16:06:38.384 [线程一] INFO cn.itcast.methods.ThreadYield - 线程一======threadYield======3
16:06:38.384 [线程二] INFO cn.itcast.methods.ThreadYield - 线程二======threadYield======4
16:06:38.384 [线程一] INFO cn.itcast.methods.ThreadYield - 线程一======threadYield======4
16:06:38.384 [线程二] INFO cn.itcast.methods.ThreadYield - 线程二======threadYield======5
16:06:38.384 [线程一] INFO cn.itcast.methods.ThreadYield - 线程一======threadYield======5
16:06:38.384 [线程二] INFO cn.itcast.methods.ThreadYield - 线程二线程让行
16:06:38.384 [线程一] INFO cn.itcast.methods.ThreadYield - 线程一线程让行
16:06:38.384 [线程二] INFO cn.itcast.methods.ThreadYield - 线程二======threadYield======6
16:06:38.384 [线程二] INFO cn.itcast.methods.ThreadYield - 线程二======threadYield======7
16:06:38.384 [线程二] INFO cn.itcast.methods.ThreadYield - 线程二======threadYield======8
16:06:38.384 [线程二] INFO cn.itcast.methods.ThreadYield - 线程二======threadYield======9
16:06:38.384 [线程一] INFO cn.itcast.methods.ThreadYield - 线程一======threadYield======6
16:06:38.384 [线程一] INFO cn.itcast.methods.ThreadYield - 线程一======threadYield======7
16:06:38.384 [线程一] INFO cn.itcast.methods.ThreadYield - 线程一======threadYield======8
16:06:38.384 [线程一] INFO cn.itcast.methods.ThreadYield - 线程一======threadYield======9
```

<font size="2">&ensp;&ensp;&ensp;&ensp;当线程调用Thread.yield()方法时,会给线程调度器一个当前线程愿意让出CPU使用的暗示,但是线程调度器可能会忽略这个暗示，重新调用了该线程。无论线程调度器是否接受这个示意,都不会改变该线程的锁的所有权。
</font>

#### 3、yield霸占同步代码块（同步锁）

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 展示线程yield的时候不释放synchronized的monitor，等sleep时间到了以后，正常结束后才释放锁
 */
@Slf4j
public class ThreadYieldDontReleaseMonitor implements Runnable{
    @Override
    public void run() {
        syn();
    }

    private synchronized void syn(){
        for (int i = 0 ; i < 10 ; i++ ){
            // 打印内容
            log.info(Thread.currentThread().getName()+"======threadYield======"+i);
            // 当i为5时，该线程就会把CPU时间让掉，让其他或者自己的线程执行（也就是谁先抢到谁执行）
            if (i == 5) {
                log.info(Thread.currentThread().getName()+"线程让行");
                // 线程让行
                Thread.yield();
            }

        }
    }
    public static void main(String[] args) {
        ThreadYieldDontReleaseMonitor r = new ThreadYieldDontReleaseMonitor();
        Thread t = new Thread(r,"A");
        Thread t1 = new Thread(r,"B");
        t.start();
        t1.start();
    }
}
```

<font size="2">执行结果</font>

```
16:40:29.364 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - A======threadYield======0
16:40:29.366 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - A======threadYield======1
16:40:29.366 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - A======threadYield======2
16:40:29.366 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - A======threadYield======3
16:40:29.366 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - A======threadYield======4
16:40:29.366 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - A======threadYield======5
16:40:29.366 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - A======threadYield======6
16:40:29.366 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - A======threadYield======7
16:40:29.366 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - A======threadYield======8
16:40:29.366 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - A======threadYield======9
16:40:29.366 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - B======threadYield======0
16:40:29.366 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - B======threadYield======1
16:40:29.366 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - B======threadYield======2
16:40:29.367 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - B======threadYield======3
16:40:29.367 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - B======threadYield======4
16:40:29.367 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - B======threadYield======5
16:40:29.367 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - B======threadYield======6
16:40:29.367 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - B======threadYield======7
16:40:29.367 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - B======threadYield======8
16:40:29.367 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseMonitor - B======threadYield======9
```

<font size="2">&ensp;&ensp;&ensp;&ensp;从执行结果可以看出，线程调用了yield方法之后并未释放同步锁</font>

#### 4、yield霸占lock

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 测试yield方法不释放锁
 */
@Slf4j
public class ThreadYieldDontReleaseLock implements Runnable{
    private static final Lock lock = new ReentrantLock();

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        lock.lock();
        try {
            log.info("线程{}获取到了锁", threadName);
            for (int i = 0 ; i < 10 ; i++ ){
                // 打印内容
                log.info(Thread.currentThread().getName()+"======threadYield======"+i);
                // 当i为5时，该线程就会把CPU时间让掉，让其他或者自己的线程执行（也就是谁先抢到谁执行）
                if (i == 5) {
                    log.info(Thread.currentThread().getName()+"线程让行");
                    // 线程让行
                    Thread.yield();
                }
            }
            log.info("线程{}已经苏醒", threadName);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ThreadYieldDontReleaseLock r = new ThreadYieldDontReleaseLock();
        Thread t = new Thread(r,"A");
        Thread t1 = new Thread(r,"B");
        t.start();
        t1.start();
    }
}
```

<font size="2">执行结果</font>

```
17:03:25.665 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - 线程A获取到了锁17:03:25.670 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - A======threadYield======017:03:25.670 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - A======threadYield======117:03:25.670 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - A======threadYield======217:03:25.670 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - A======threadYield======317:03:25.670 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - A======threadYield======417:03:25.670 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - A======threadYield======517:03:25.670 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - A线程让行17:03:25.670 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - A======threadYield======617:03:25.670 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - A======threadYield======717:03:25.670 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - A======threadYield======817:03:25.670 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - A======threadYield======917:03:25.670 [A] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - 线程A已经苏醒17:03:25.670 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - 线程B获取到了锁17:03:25.670 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - B======threadYield======017:03:25.670 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - B======threadYield======117:03:25.670 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - B======threadYield======217:03:25.670 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - B======threadYield======317:03:25.670 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - B======threadYield======417:03:25.670 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - B======threadYield======517:03:25.670 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - B线程让行17:03:25.670 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - B======threadYield======617:03:25.670 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - B======threadYield======717:03:25.670 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - B======threadYield======817:03:25.670 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - B======threadYield======917:03:25.670 [B] INFO cn.itcast.methods.ThreadYieldDontReleaseLock - 线程B已经苏醒
```

<font size="2">&ensp;&ensp;&ensp;&ensp;从执行结果可以看出，线程调用了yield方法之后并未释放lock锁</font>

**总结**

```
1、yield 是Thread一个静态的原生（native）方法。2、yield 告诉当前正在执行的线程把运行机会交给线程池中拥有相同优先级的线程。3、yield 不能保证使得当前正在运行的线程迅速转换到可运行的状态。4、它仅能使一个线程从运行状态转到可运行状态，而不是等待或阻塞状态。5、yield 并不会释放任何资源锁，只有当访问带锁资源的方法执行结束之后才会释放锁资源
```