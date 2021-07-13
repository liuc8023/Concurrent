**作用：**
```    
    让当前线程停止执行，把CPU让给其他线程执行，但不会释放对象锁和监控的状态，也就是如果有Synchronized同步块，其他线程仍然不同访问共享
数据。到了指定时间后线程又会自动恢复运行状态。
线程状态变化：运行 --> 等待 
sleep时间到后线程自动由 等待 --> 就绪
```
**注意：**
```
    线程睡眠到期自动苏醒，并返回到可运行状态，不是运行状态。sleep()中指定的时间是线程不会运行的最短时间。因此，sleep()方法不能保证该
线程睡眠到期后就开始执行
```
#### 1、Thread.sleep(long millis)
```java
package cn.itcast.methods;

/**
 * 线程休眠方法
 */
public class ThreadSleep implements Runnable {

    @Override
    public void run() {
        // 循环打印
        for (int i = 0; i < 100; i++) {
            // 线程休眠
            try {
                Thread.sleep(i*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 打印内容
            System.out.println(Thread.currentThread().getId()+"======threadSleep======"+i);
        }
    }

    public static void main(String[] args) {
        /**
         * 线程常用方法 sleep()
         * 作用是让当前线程停止执行，把cpu让给其他线程执行，但不会释放对象锁和监控的状态，到了指定时间后线程又会自动恢复运行状态。
         *
         * 注意：线程睡眠到期自动苏醒，并返回到可运行状态，不是运行状态。sleep()中指定的时间是线程不会运行的最短时间。
         * 因此，sleep()方法不能保证该线程睡眠到期后就开始执行
         */
        Runnable r = new ThreadSleep();
        Thread t = new Thread(r);
        t.start();

        Runnable r1 = new ThreadSleep();
        Thread t1 = new Thread(r1);
        t1.start();
    }
}
```

#### 2、sleep霸占对象锁

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试sleep方法不释放锁
 */
@Slf4j
public class ThreadSleepDontReleaseLock implements Runnable{
    private static Object lock = new Object();

    @Override
    public void run() {
        synchronized (lock){
            try {
                log.info(Thread.currentThread().getName()+"休眠10秒不放弃锁");
                Thread.sleep(10000);
                log.info(Thread.currentThread().getName()+"休眠10秒醒来");
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ThreadSleepDontReleaseLock r = new ThreadSleepDontReleaseLock();
        Thread t = new Thread(r,"A");
        ThreadSleepDontReleaseLock r1 = new ThreadSleepDontReleaseLock();
        Thread t1 = new Thread(r1,"B");
        t.start();
        t1.start();
    }
}
```
不管执行多少次，都是先A输出再B输出 或者先B输出再A输出，不会出现交叉输出的状况，
由于A获取到锁以后，即便是sleep也不会释放锁，因B获取不到锁，也就没法执行。

执行结果：
```
16:58:38.864 [A] INFO cn.itcast.methods.ThreadSleepDontReleaseLock - A休眠10秒不放弃锁
16:58:48.871 [A] INFO cn.itcast.methods.ThreadSleepDontReleaseLock - A休眠10秒醒来
16:58:48.871 [B] INFO cn.itcast.methods.ThreadSleepDontReleaseLock - B休眠10秒不放弃锁
16:58:58.874 [B] INFO cn.itcast.methods.ThreadSleepDontReleaseLock - B休眠10秒醒来
```
或者
```
17:03:58.093 [B] INFO cn.itcast.methods.ThreadSleepDontReleaseLock - B休眠10秒不放弃锁
17:04:08.108 [B] INFO cn.itcast.methods.ThreadSleepDontReleaseLock - B休眠10秒醒来
17:04:08.108 [A] INFO cn.itcast.methods.ThreadSleepDontReleaseLock - A休眠10秒不放弃锁
17:04:18.113 [A] INFO cn.itcast.methods.ThreadSleepDontReleaseLock - A休眠10秒醒来
```
**注意：**
```
    若我们注释掉 synchronized (lock) 后再次执行该程序，A和B是可以相互切换执行的，原因是：在没有同步锁的情况下，当一个线程进入“休眠
（阻塞）状态“时，会放弃CPU的执行权，另一个线程就会获取CPU执行权。
```
#### 3、sleep霸占同步代码块（同步锁）
```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 展示线程sleep的时候不释放synchronized的monitor，等sleep时间到了以后，正常结束后才释放锁
 */
@Slf4j
public class ThreadSleepDontReleaseMonitor implements Runnable{
    @Override
    public void run() {
        syn();
    }

    private synchronized void syn(){
        log.info(Thread.currentThread().getName()+"线程获取到了同步锁");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info(Thread.currentThread().getName()+"线程醒了，退出同步代码块");
    }
    public static void main(String[] args) {
        ThreadSleepDontReleaseMonitor r = new ThreadSleepDontReleaseMonitor();
        Thread t = new Thread(r,"A");
        Thread t1 = new Thread(r,"B");
        t.start();
        t1.start();
    }
}
```
执行结果
```
20:44:29.357 [A] INFO cn.itcast.methods.ThreadSleepDontReleaseMonitor - A线程获取到了同步锁
20:44:34.360 [A] INFO cn.itcast.methods.ThreadSleepDontReleaseMonitor - A线程醒了，退出同步代码块
20:44:34.360 [B] INFO cn.itcast.methods.ThreadSleepDontReleaseMonitor - B线程获取到了同步锁
20:44:39.376 [B] INFO cn.itcast.methods.ThreadSleepDontReleaseMonitor - B线程醒了，退出同步代码块
```

#### 4、sleep霸占lock
```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试sleep方法不释放对象锁
 */
@Slf4j
public class ThreadSleepDontReleaseLock implements Runnable{
    private static final Lock lock = new ReentrantLock();
    
    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        lock.lock();
        try {
            log.info("线程{}获取到了锁", threadName);
            Thread.sleep(5000);
            log.info("线程{}已经苏醒", threadName);
        } catch (InterruptedException e) {
            e.printStackTrace();
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ThreadSleepDontReleaseLock r = new ThreadSleepDontReleaseLock();
        Thread t = new Thread(r,"A");
        Thread t1 = new Thread(r,"B");
        t.start();
        t1.start();
    }
}
```
执行结果
```
20:51:40.122 [A] INFO cn.itcast.methods.ThreadSleepDontReleaseLock - 线程A获取到了锁
20:51:50.246 [A] INFO cn.itcast.methods.ThreadSleepDontReleaseLock - 线程A已经苏醒
20:51:50.246 [B] INFO cn.itcast.methods.ThreadSleepDontReleaseLock - 线程B获取到了锁
20:52:00.258 [B] INFO cn.itcast.methods.ThreadSleepDontReleaseLock - 线程B已经苏醒
```
由上面三个示例可以看出sleep 特点：不释放锁，不管是什么锁（包括 synchronized 和 lock），都不释放，我就一直睡觉，有什么锁就存着，直到sleep时间结束，才会将锁释放了。

#### 5、响应中断
sleep 方法响应中断
- 抛出 InterruptedException
- 清除中断状态
```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * 线程每隔1秒钟输出当前时间，运行时被中断
 */
@Slf4j
public class ThreadSleepInterrupted implements Runnable{

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            log.info("当前时间："+System.currentTimeMillis());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("我被中断了！");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ThreadSleepInterrupted r = new ThreadSleepInterrupted();
        Thread t = new Thread(r);
        t.start();
        try {
            Thread.sleep(6500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.interrupt();
    }
}
```
执行结果
```
21:26:46.399 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupted - 当前时间：1625750806390
21:26:47.411 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupted - 当前时间：1625750807411
21:26:48.424 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupted - 当前时间：1625750808424
21:26:49.427 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupted - 当前时间：1625750809427
21:26:50.435 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupted - 当前时间：1625750810435
21:26:51.435 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupted - 当前时间：1625750811435
21:26:52.442 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupted - 当前时间：1625750812442
我被中断了！
java.lang.InterruptedException: sleep interrupted
	at java.lang.Thread.sleep(Native Method)
	at java.lang.Thread.sleep(Thread.java:340)
	at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
	at cn.itcast.methods.ThreadSleepInterrupted.run(ThreadSleepInterrupted.java:14)
	at java.lang.Thread.run(Thread.java:748)
21:26:53.016 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupted - 当前时间：1625750813016
21:26:54.018 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupted - 当前时间：1625750814018
21:26:55.026 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupted - 当前时间：1625750815026
```
响应了中断，然后继续执行

sleep 方法可以让线程进入 waiting 状态，并且不占用 cpu 资源，但是不会释放锁，直到规定时间后再执行，休眠期间如果被中断，会抛出异常并清除中断状态