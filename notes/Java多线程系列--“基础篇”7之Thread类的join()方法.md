**作用：**
```
    主线程(生成子线程的线程)等待子线程（生成的线程）的终止。也就是在子线程调用了join()方法后面的代码，只有等到子线程结束了才能执行。
线程状态变化：
主线程：运行 --> 等待/超时等待(取决于调用的是join()还是join(long millis))  
       调用join方法的子线程执行完后会 等待/超时等待 --> 运行/阻塞(对象被锁)
子线程：就绪 --> 运行 -->终止
```
**注意：**
```
    主线程不会释放已经持有的对象锁。
```

**那么join()到底是暂停了哪些线程？是不是暂停了所有的线程呢？**

**首先给出结论：**
```
    t.join()方法只会使主线程(或者说调用t.join()的线程)进入等待池并等待t线程执行完毕后才会被唤醒。并不影响同一时刻处在运行状态的其他线程。
```
下面则是分析过程。

之前对于join()方法只是了解它能够使得t.join()中的t优先执行，当t执行完后才会执行其他线程。能够使得线程之间的并行执行变成串行执行。

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadJoin implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            log.info(""+i);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        log.info(Thread.currentThread().getName()+"线程启动了");
        Runnable r = new ThreadJoin();
        Thread t = new Thread(r,"线程一");
        Runnable r1 = new ThreadJoin();
        Thread t1 = new Thread(r1,"线程二");
        t.start();
        /**
         * join的意思是使得放弃当前线程的执行，并返回对应的线程，例如下面代码的意思就是：
         * 程序在main线程中调用t线程的join方法，则main线程放弃cpu控制权，并返回t线程继续执行直到线程t执行完毕
         * 所以结果是t线程执行完后，才到主线程执行，相当于在main线程中同步t线程，t执行完了，main线程才有执行的机会
         */
        t1.start();
        log.info(Thread.currentThread().getName()+"结束了");
    }
}

```

执行结果

```cpp hljs
22:10:37.544 [main] INFO cn.itcast.methods.ThreadJoin - main线程启动了
22:10:37.548 [main] INFO cn.itcast.methods.ThreadJoin - main结束了
22:10:37.548 [线程一] INFO cn.itcast.methods.ThreadJoin - 0
22:10:37.548 [线程一] INFO cn.itcast.methods.ThreadJoin - 1
22:10:37.548 [线程二] INFO cn.itcast.methods.ThreadJoin - 0
22:10:37.548 [线程二] INFO cn.itcast.methods.ThreadJoin - 1
22:10:37.548 [线程一] INFO cn.itcast.methods.ThreadJoin - 2
22:10:37.548 [线程二] INFO cn.itcast.methods.ThreadJoin - 2
22:10:37.548 [线程一] INFO cn.itcast.methods.ThreadJoin - 3
22:10:37.548 [线程二] INFO cn.itcast.methods.ThreadJoin - 3
22:10:37.548 [线程一] INFO cn.itcast.methods.ThreadJoin - 4
22:10:37.548 [线程二] INFO cn.itcast.methods.ThreadJoin - 4
```

可以看出主线程先执行完成，然后线程一和线程二是交替执行的。

而在其中加入join()方法后

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadJoin implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            log.info(""+i);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        log.info(Thread.currentThread().getName()+"线程启动了");
        Runnable r = new ThreadJoin();
        Thread t = new Thread(r,"线程一");
        Runnable r1 = new ThreadJoin();
        Thread t1 = new Thread(r1,"线程二");
        t.start();
        /**
         * join的意思是使得放弃当前线程的执行，并返回对应的线程，例如下面代码的意思就是：
         * 程序在main线程中调用t线程的join方法，则main线程放弃cpu控制权，并返回t线程继续执行直到线程t执行完毕
         * 所以结果是t线程执行完后，才到主线程执行，相当于在main线程中同步t线程，t执行完了，main线程才有执行的机会
         */
        t.join();
        t1.start();
        t1.join();
        log.info(Thread.currentThread().getName()+"结束了");
    }
}
```

执行结果

```cpp hljs
22:11:56.930 [main] INFO cn.itcast.methods.ThreadJoin - main线程启动了
22:11:56.933 [线程一] INFO cn.itcast.methods.ThreadJoin - 0
22:11:56.933 [线程一] INFO cn.itcast.methods.ThreadJoin - 1
22:11:56.933 [线程一] INFO cn.itcast.methods.ThreadJoin - 2
22:11:56.933 [线程一] INFO cn.itcast.methods.ThreadJoin - 3
22:11:56.933 [线程一] INFO cn.itcast.methods.ThreadJoin - 4
22:11:56.934 [线程二] INFO cn.itcast.methods.ThreadJoin - 0
22:11:56.934 [线程二] INFO cn.itcast.methods.ThreadJoin - 1
22:11:56.934 [线程二] INFO cn.itcast.methods.ThreadJoin - 2
22:11:56.934 [线程二] INFO cn.itcast.methods.ThreadJoin - 3
22:11:56.934 [线程二] INFO cn.itcast.methods.ThreadJoin - 4
22:11:56.934 [main] INFO cn.itcast.methods.ThreadJoin - main结束了
```

显然，使用t.join()之后，线程二需要等线程一执行完毕之后才能执行。需要注意的是，t.join()需要等t.start()执行之后执行才有效果，此外，如果t.join()放在t1.start()之后的话，仍然会是交替执行，然而并不是没有效果，这点困扰了我很久，也没在别的博客里看到过。

为了深入理解，我们先看一下join()的源码。

```java
/**
 * Waits at most {@code millis} milliseconds for this thread to
 * die. A timeout of {@code 0} means to wait forever.
 *
 * <p> This implementation uses a loop of {@code this.wait} calls
 * conditioned on {@code this.isAlive}. As a thread terminates the
 * {@code this.notifyAll} method is invoked. It is recommended that
 * applications not use {@code wait}, {@code notify}, or
 * {@code notifyAll} on {@code Thread} instances.
 *
 * @param  millis
 *         the time to wait in milliseconds
 *
 * @throws  IllegalArgumentException
 *          if the value of {@code millis} is negative
 *
 * @throws  InterruptedException
 *          if any thread has interrupted the current thread. The
 *          <i>interrupted status</i> of the current thread is
 *          cleared when this exception is thrown.
 */
public final synchronized void join(long millis)
    throws InterruptedException {
    long base = System.currentTimeMillis();
    long now = 0;

    if (millis < 0) {
        throw new IllegalArgumentException("timeout value is negative");
    }

    if (millis == 0) {
        while (isAlive()) {
            wait(0);
        }
    } else {
        while (isAlive()) {
            long delay = millis - now;
            if (delay <= 0) {
                break;
            }
            wait(delay);
            now = System.currentTimeMillis() - base;
        }
    }
}
```

可以看出，join()方法的底层是利用wait()方法实现的。可以看出，join方法是一个同步方法，当主线程调用t.join()方法时，主线程先获得了t对象的锁，随后进入方法，调用了t对象的wait()方法，使主线程进入了t对象的等待池，此时，线程一则还在执行，并且随后的t1.start()还没被执行，因此，线程二也还没开始。等到线程一执行完毕之后，主线程继续执行，走到了t1.start()，线程二才会开始执行，所以就看到了上面的执行结果。

看到上面的解释，你是否有疑问？

**问题：**
```
	虽然t.join()被调用的地方是发生在“main主线程”中，但是t.join()是通过“子线程t”去调用的join()。那么，join()方法中的isAlive()应该
是判断“子线程t”是不是Alive状态；对应的wait(0)也应该是“让子线程s”等待才对。但如果是这样的话，t.join()的作用怎么可能是“让主线程等待，
直到子线程t完成为止”呢，应该是让"子线程等待才对(因为调用子线程对象t的wait方法嘛)"？
```
**答案：**
```
    wait()的作用是让“当前线程”等待，而这里的“当前线程”是指当前在CPU上运行的线程。所以，虽然是调用子线程的wait()方法，但是它是通过
“主线程”去调用的；所以，休眠的是主线程，而不是“子线程”！
```


此外，对于join()的位置和作用的关系，我们可以用下面的例子来分析

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadJoinTest implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            log.info(""+i);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        log.info(Thread.currentThread().getName()+" start");
        Runnable r = new ThreadJoinTest();
        Thread t = new Thread(r,"线程一");
        Runnable r1 = new ThreadJoinTest();
        Thread t1 = new Thread(r1,"线程二");
        Runnable r2 = new ThreadJoinTest();
        Thread t2 = new Thread(r2,"线程三");
        log.info("t start");
        t.start();
        log.info("t end");
        log.info("t1 start");
        t1.start();
        log.info("t1 end");
        t.join();
        log.info("t2 start");
        t2.start();
        log.info("t2 end");
        log.info(Thread.currentThread().getName()+" end");
    }
}
```

执行结果

```cpp hljs
00:04:41.466 [main] INFO cn.itcast.methods.ThreadJoinTest - main start
00:04:41.471 [main] INFO cn.itcast.methods.ThreadJoinTest - t start
00:04:41.471 [main] INFO cn.itcast.methods.ThreadJoinTest - t end
00:04:41.471 [main] INFO cn.itcast.methods.ThreadJoinTest - t1 start
00:04:41.471 [main] INFO cn.itcast.methods.ThreadJoinTest - t1 end
00:04:41.472 [线程一] INFO cn.itcast.methods.ThreadJoinTest - 0
00:04:41.472 [线程一] INFO cn.itcast.methods.ThreadJoinTest - 1
00:04:41.472 [线程一] INFO cn.itcast.methods.ThreadJoinTest - 2
00:04:41.472 [线程一] INFO cn.itcast.methods.ThreadJoinTest - 3
00:04:41.472 [线程一] INFO cn.itcast.methods.ThreadJoinTest - 4
00:04:41.472 [main] INFO cn.itcast.methods.ThreadJoinTest - t2 start
00:04:41.472 [main] INFO cn.itcast.methods.ThreadJoinTest - t2 end
00:04:41.472 [main] INFO cn.itcast.methods.ThreadJoinTest - main end
00:04:41.473 [线程二] INFO cn.itcast.methods.ThreadJoinTest - 0
00:04:41.473 [线程二] INFO cn.itcast.methods.ThreadJoinTest - 1
00:04:41.473 [线程二] INFO cn.itcast.methods.ThreadJoinTest - 2
00:04:41.473 [线程二] INFO cn.itcast.methods.ThreadJoinTest - 3
00:04:41.473 [线程二] INFO cn.itcast.methods.ThreadJoinTest - 4
00:04:41.473 [线程三] INFO cn.itcast.methods.ThreadJoinTest - 0
00:04:41.473 [线程三] INFO cn.itcast.methods.ThreadJoinTest - 1
00:04:41.473 [线程三] INFO cn.itcast.methods.ThreadJoinTest - 2
00:04:41.473 [线程三] INFO cn.itcast.methods.ThreadJoinTest - 3
00:04:41.473 [线程三] INFO cn.itcast.methods.ThreadJoinTest - 4
```

多次实验可以看出，主线程在t.join()方法处停止，并需要等待线程一执行完毕后才会执行t2.start()，然而，并不影响二线程的执行。因此，可以得出结论，t.join()方法只会使主线程进入等待池并等待t线程执行完毕后才会被唤醒。并不影响同一时刻处在运行状态的其他线程。

PS:join源码中，只会调用wait方法，并没有在结束时调用notify，这是因为线程在die的时候会自动调用自身的notifyAll方法，来释放所有的资源和锁。