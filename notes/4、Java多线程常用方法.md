## Java多线程常用方法

​		在Java中，线程使用Thread关键字来表示。所有线程对象，都必须来自Thread类或者Thread类子类的实例，所以掌握Thread类的常用方法就显得尤为重要。

### 方法一、start和run的区别

先上一段代码，运行结果是什么

![img](https://img2018.cnblogs.com/i-beta/1582675/201912/1582675-20191209110225141-681749077.png)

结果是

![img](https://img2018.cnblogs.com/i-beta/1582675/201912/1582675-20191209110431589-989254768.png)

如果修改为t.start(),结果是

![img](https://img2018.cnblogs.com/i-beta/1582675/201912/1582675-20191209111510482-581278206.png)

**start**

```
	它的作用是启动一个新线程。
	通过start()方法来启动的新线程，处于就绪（可运行）状态，并没有运行，一旦得到cpu时间片，就开始执行相应线程的run()方法，这里方法run()称为线程体，它包含了要执行的这个线程的内容，run方法运行结束，此线程随即终止。start()不能被重复调用。用start方法来启动线程，真正实现了多线程运行，即无需等待某个线程的run方法体代码执行完毕就直接继续执行下面的代码。这里无需等待run方法执行完毕，即可继续执行下面的代码，即进行了线程切换。
```

**run**

```
	run方法就和普通的成员方法一样，可以被重复调用。
	如果直接调用run方法，并不会启动新线程！程序中依然只有主线程这一个线程，其程序执行路径还是只有一条，还是要顺序执行，还是要等待run方法体执行完毕后才可继续执行下面的代码，这样就没有达到多线程的目的。
```

总结：调用start方法方可启动线程，而run方法只是thread的一个普通方法调用，还是在主线程里

1.  start方法可以启动一个线程，run方法不能
2. start方法不能被重复调用，run方法可以
3. start中的run方法可以不执行完就继续执行下面的代码，即进行了线程切换。直接调用run方法必须等待其代码全部执行完才能继续执行下面的代码
4. start方法实现了多线程，run方法没有执行多线程



### 方法二、Thread类的静态方法sleep()

**作用：**让当前线程停止执行，把CPU让给其他线程执行，但不会释放对象锁和监控的状态，也就是如果有Synchronized同步块，其他线程仍然不同访问共享数据。到了指定时间后线程又会自动恢复运行状态。
线程状态变化：运行 --》 等待 。 sleep时间到后线程自动由 等待 --》 就绪
**注意：**线程睡眠到期自动苏醒，并返回到可运行状态，不是运行状态。sleep()中指定的时间是线程不会运行的最短时间。因此，sleep()方法不能保证该线程睡眠到期后就开始执行

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
         * 作用是让当前线程停止执行，把cpu让给其他线程执行，但不会释放对象锁和监控的状态，到了指定时间后线程又会自动恢复运行状		   * 态。
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



### 方法三、Thread类的静态方法yield()

**作用：**暂停当前正在执行的线程对象（及放弃当前拥有的cup资源），并执行其他线程。yield()做的是让当前运行线程回到可运行状态，以允许具有相同优先级的其他线程获得运行机会。
线程状态变化：运行 --》 就绪
**注意：**使用yield()的目的是让相同优先级的线程之间能适当的轮转执行。但是，实际中无法保证yield()达到让步目的，因为让步的线程还有可能被线程调度程序再次选中。

```
跟sleep()方法一样，会让出CPU的执行权，但不会释放锁。
跟sleep()方法不同的是：yield方法只能让拥有相同优先级的线程有获取CPU执行时间的机会。还有就是它并不能控制具体的交出CPU的时间，因为yield() 方法只是使当前线程重新回到就绪态，所以执行 yield()的线程有可能在进入到可执行状态后马上又被执行。
```

```java
package cn.itcast.methods;

/**
 * 线程让行方法
 */
public class ThreadYield implements Runnable{
    @Override
    public void run() {
        // 循环打印
        for (int i = 0 ; i < 20 ; i++ ){
            // 线程让行
            Thread.yield();
            // 打印内容
            System.out.println(Thread.currentThread().getName()+"======threadYield======"+i);
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
        t.start();
        Runnable r1 = new ThreadYield();
        Thread t1 = new Thread(r1,"线程二");
        t1.start();
    }
}
```

执行结果

```cpp hljs
线程一======threadYield======0
线程二======threadYield======0
线程一======threadYield======1
线程二======threadYield======1
线程一======threadYield======2
线程二======threadYield======2
线程一======threadYield======3
线程二======threadYield======3
线程一======threadYield======4
线程二======threadYield======4
线程一======threadYield======5
线程二======threadYield======5
线程一======threadYield======6
线程二======threadYield======6
线程一======threadYield======7
线程二======threadYield======7
线程一======threadYield======8
线程二======threadYield======8
线程一======threadYield======9
线程二======threadYield======9
线程一======threadYield======10
线程二======threadYield======10
线程一======threadYield======11
线程二======threadYield======11
线程一======threadYield======12
线程二======threadYield======12
线程一======threadYield======13
线程二======threadYield======13
线程一======threadYield======14
线程二======threadYield======14
线程一======threadYield======15
线程二======threadYield======15
线程一======threadYield======16
线程二======threadYield======16
线程二======threadYield======17
线程一======threadYield======17
线程二======threadYield======18
线程一======threadYield======18
线程二======threadYield======19
线程一======threadYield======19
```

通过以上两个例子可以明显感觉到线程频繁切换会导致性能明显下降。因为进行线程上下文切换（Thread Context Switch）是需要时间的，切换的次数越多，消耗在切换上的时间越多，CPU真正花在程序执行上的时间越少。



### 方法四、Thread类的join()方法

**作用：**主线程(生成子线程的线程)等待子线程（生成的线程）的终止。也就是在子线程调用了join()方法后面的代码，只有等到子线程结束了才能执行。
线程状态变化：
主线程：运行 --》 等待/超时等待(取决于调用的是join()还是join(long millis))  调用join方法的子线程执行完后会 等待/超时等待 --》 运行/阻塞(对象被锁)
子线程：就绪 --》 运行 --》 终止
**注意：**主线程不会释放已经持有的对象锁。

**那么join()到底是暂停了哪些线程？是不是暂停了所有的线程呢？**

**首先给出结论：**t.join()方法只会使主线程(或者说调用t.join()的线程)进入等待池并等待t线程执行完毕后才会被唤醒。并不影响同一时刻处在运行状态的其他线程。

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

    public static void main(String[] args) {
        Runnable r = new ThreadJoin();
        Thread t = new Thread(r,"线程一");
        Runnable r1 = new ThreadJoin();
        Thread t1 = new Thread(r1,"线程二");
        t.start();
        t1.start();
    }
}
```

执行结果

```cpp hljs
22:00:02.145 [线程二] INFO cn.itcast.methods.ThreadJoin - 0
22:00:02.145 [线程一] INFO cn.itcast.methods.ThreadJoin - 0
22:00:02.149 [线程一] INFO cn.itcast.methods.ThreadJoin - 1
22:00:02.149 [线程二] INFO cn.itcast.methods.ThreadJoin - 1
22:00:02.149 [线程一] INFO cn.itcast.methods.ThreadJoin - 2
22:00:02.149 [线程二] INFO cn.itcast.methods.ThreadJoin - 2
22:00:02.149 [线程一] INFO cn.itcast.methods.ThreadJoin - 3
22:00:02.149 [线程二] INFO cn.itcast.methods.ThreadJoin - 3
22:00:02.149 [线程一] INFO cn.itcast.methods.ThreadJoin - 4
22:00:02.149 [线程二] INFO cn.itcast.methods.ThreadJoin - 4
```

可以看出线程一和线程二是交替执行的。

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
    }
}
```

执行结果

```cpp hljs
22:12:15.445 [线程一] INFO cn.itcast.methods.ThreadJoin - 0
22:12:15.448 [线程一] INFO cn.itcast.methods.ThreadJoin - 1
22:12:15.448 [线程一] INFO cn.itcast.methods.ThreadJoin - 2
22:12:15.448 [线程一] INFO cn.itcast.methods.ThreadJoin - 3
22:12:15.448 [线程一] INFO cn.itcast.methods.ThreadJoin - 4
22:12:15.449 [线程二] INFO cn.itcast.methods.ThreadJoin - 0
22:12:15.449 [线程二] INFO cn.itcast.methods.ThreadJoin - 1
22:12:15.449 [线程二] INFO cn.itcast.methods.ThreadJoin - 2
22:12:15.449 [线程二] INFO cn.itcast.methods.ThreadJoin - 3
22:12:15.449 [线程二] INFO cn.itcast.methods.ThreadJoin - 4
```

​		显然，使用t.join()之后，线程二需要等线程一执行完毕之后才能执行。需要注意的是，t.join()需要等t.start()执行之后执行才有效果，此外，如果t.join()放在t1.start()之后的话，仍然会是交替执行，然而并不是没有效果，这点困扰了我很久，也没在别的博客里看到过。

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

​		可以看出，join()方法的底层是利用wait()方法实现的。可以看出，join方法是一个同步方法，当主线程调用t.join()方法时，主线程先获得了t对象的锁，随后进入方法，调用了t对象的wait()方法，使主线程进入了t对象的等待池，此时，线程一则还在执行，并且随后的t1.start()还没被执行，因此，线程二也还没开始。等到线程一执行完毕之后，主线程继续执行，走到了t1.start()，线程二才会开始执行，所以就看到了上面的执行结果。

看到上面的解释，你是否有疑问？

**问题：**
	虽然t.join()被调用的地方是发生在“main主线程”中，但是t.join()是通过“子线程t”去调用的join()。那么，join()方法中的isAlive()应该是判断“子线程t”是不是Alive状态；对应的wait(0)也应该是“让子线程s”等待才对。但如果是这样的话，t.join()的作用怎么可能是“让主线程等待，直到子线程t完成为止”呢，应该是让"子线程等待才对(因为调用子线程对象t的wait方法嘛)"？

**答案：**wait()的作用是让“当前线程”等待，而这里的“当前线程”是指当前在CPU上运行的线程。所以，虽然是调用子线程的wait()方法，但是它是通过“主线程”去调用的；所以，休眠的是主线程，而不是“子线程”！



此外，对于join()的位置和作用的关系，我们可以用下面的例子来分析
