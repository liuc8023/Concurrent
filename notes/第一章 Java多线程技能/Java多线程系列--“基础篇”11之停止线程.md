<font size="2">&ensp;&ensp;&ensp;&ensp;停止线程是在多线程开发中很重要的技术点，掌握此技术可以对线程的停止进行有效的处理。停止线程在 Java 语言中并不像 break 语句那样干脆，需要一些技巧性的处理。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;使用 Java 内置支持多线程的类设计多线程应用是很常见的事情，然而，多线程给开发人员带来了一些新的挑战，如果处理不好就会导致超出预期的行为并且难以定位错误。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;本节将讨论如何更好地停止一个线程。停止一个线程意味着在线程处理完任务之前停掉正在做的操作，也就是放弃当前的操作。虽然这看起来非常简单，但是必须做好防范措施，以便达到预期的效果。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;停止一个线程可以使用 Thread.stop() 方法，但最好不用它。虽然它确实可以停止一个正在运行的线程，但是这个方法是不安全的，而且已被弃用作废了，在将来的 Java 版本中，这个方法将不可用或不被支持。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;大多数停止一个线程的操作使用 Thread.interrupt() 方法，尽管方法的名称是“停止，中止”的意思，但这个方法不会终止一个正在运行的线程，还需要加入一个判断才可以完成线程的停止。关于此知识点在后面有专门的章节进行介绍。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;在 Java 中有以下 3 种方法可以终止正在运行的线程：</font>
```
- 使用退出标识，使线程正常退出，也就是当 run() 方法完成后线程终止。
- 使用 stop() 方法强行终止线程，但是不推荐使用这个方法，因为 stop() 和 suspend() 及 resume() 一样，都是作废过期的方法，使用它们可能产生
  不可预料的结果。
- 使用 interrupt() 方法中断线程。
```

### 停止不了的线程
<font size="2">&ensp;&ensp;&ensp;&ensp;interrupt() 方法的作用是用来停止线程，但 intermpt() 方法的使用效果并不像循环结构中 break 语句那样，可以马上停止循环。调用 intermpt() 方法仅仅是在当前线程中打了一个停止的标记，并不是真的停止线程。</font> 

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试interrupt
 */
@Slf4j
public class ThreadInterrupt implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 30000; i++) {
            log.info("i="+(i+1));
        }
    }

    public static void main(String[] args) {
        try {
            ThreadInterrupt r = new ThreadInterrupt();
            Thread t = new Thread(r);
            //启动线程
            t.start();
            //延时50毫秒
            Thread.sleep(50);
            //停止线程
            t.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
<font size="2">执行结果</font>

```
i=1
i=2
...
i=29999
i=30000
```
<font size="2">从运行结果来看，调用interrupt方法并没有停止线程。那如何停止线程呢？请接着往下看。</font>

### 判断线程是不是停止状态
<font size="2">&ensp;&ensp;&ensp;&ensp;在介绍如何停止线程的知识点前，先来看一下如何判断线程的状态是不是停止的。在 Java 的 JDK 中，Thread.java 类里提供了两种方法。</font>

```java
Thread.interrupted()：测试当前线程是否已经中断。
this.islnterrupted()：测试线程是否已经中断。
```
<font size="2">这两个方法有什么差别呢？我们先看下JDK源码：</font>

```java
/**
     * Tests whether the current thread has been interrupted.  The
     * <i>interrupted status</i> of the thread is cleared by this method.  In
     * other words, if this method were to be called twice in succession, the
     * second call would return false (unless the current thread were
     * interrupted again, after the first call had cleared its interrupted
     * status and before the second call had examined it).
     *
     * <p>A thread interruption ignored because a thread was not alive
     * at the time of the interrupt will be reflected by this method
     * returning false.
     *
     * @return  <code>true</code> if the current thread has been interrupted;
     *          <code>false</code> otherwise.
     * @see #isInterrupted()
     * @revised 6.0
     */
    public static boolean interrupted() {
        return currentThread().isInterrupted(true);
    }
    
    /**
     * Tests whether this thread has been interrupted.  The <i>interrupted
     * status</i> of the thread is unaffected by this method.
     *
     * <p>A thread interruption ignored because a thread was not alive
     * at the time of the interrupt will be reflected by this method
     * returning false.
     *
     * @return  <code>true</code> if this thread has been interrupted;
     *          <code>false</code> otherwise.
     * @see     #interrupted()
     * @revised 6.0
     */
    public boolean isInterrupted() {
        return isInterrupted(false);
    }
    
    /**
     * Tests if some Thread has been interrupted.  The interrupted state
     * is reset or not based on the value of ClearInterrupted that is
     * passed.
     */
    private native boolean isInterrupted(boolean ClearInterrupted);
```

<font size="2">&ensp;&ensp;&ensp;&ensp;首先我们发现这个2个方法都调用了isInterrupted(boolean ClearInterrupted)这个方法，我们看这个方法的注解，它的意思是说如果一个线程已经被终止了，中断状态是否被重置取决于ClearInterrupted的值，即<font color="red">**ClearInterrupted为true时，中断状态会被重置，为false则不会被重置**</font>。
然后我们比较这2个方法的差别，结果就很明显了，可以看出，interrupted()是静态方法，所以我们用Thread.interrupted()方法表示，它调用的是currentThread().isInterrupted(true)方法，即说明是返回<font color="red">**当前线程**</font>的是否已经中断的状态值，而且有清理中断状态的机制。而isInterrupted()是一个实例方法，所以我们用this.isInterrupted()方法表示，它调用的是isInterrupted(false)方法，意思是返回线程是否已经中断的状态值，与Thread.interrupted()方法相反，它没有清理中断状态的机制。</font>

##### Thread.interrupted()
```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * 测试Thread.interrupted()
 */
@Slf4j
public class ThreadInterrupted implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 50000; i++) {
            log.info("i=" + (i + 1));
        }
    }

    public static void main(String[] args) {
        ThreadInterrupted r = new ThreadInterrupted();
        Thread t = new Thread(r);
        t.start();
        try {
            TimeUnit.SECONDS.sleep(1);
            log.info("给线程{}打终止标志",t.getName());
            t.interrupt();
            log.info("线程{}是否已经停止 1？={}",t.getName(),Thread.interrupted());
            log.info("线程{}是否已经停止 2？={}",t.getName(),Thread.interrupted());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("end!");
    }
}
```
<font size="2">执行结果</font>

```
...
15:27:25.969 [Thread-0] INFO cn.itcast.methods.ThreadInterrupted - i=49997
15:27:25.969 [Thread-0] INFO cn.itcast.methods.ThreadInterrupted - i=49998
15:27:25.969 [Thread-0] INFO cn.itcast.methods.ThreadInterrupted - i=49999
15:27:25.969 [Thread-0] INFO cn.itcast.methods.ThreadInterrupted - i=50000
15:27:26.697 [main] INFO cn.itcast.methods.ThreadInterrupted - 给线程Thread-0打终止标志
15:27:26.699 [main] INFO cn.itcast.methods.ThreadInterrupted - 线程Thread-0是否已经停止 1？=false
15:27:26.699 [main] INFO cn.itcast.methods.ThreadInterrupted - 线程Thread-0是否已经停止 2？=false
15:27:26.699 [main] INFO cn.itcast.methods.ThreadInterrupted - end!
```
<font size="2">&ensp;&ensp;&ensp;&ensp;从执行的结果来看，线程并未终止，这也就证明了interrupted()方法的解释：测试当前线程是否已经中断，**这个“当前线程”是main**，它从未中断过，所以2个结果都是false。 </font>

<font size="2">&ensp;&ensp;&ensp;&ensp;那如何让main线程终止呢？</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试让main线程终止
 */
@Slf4j
public class ThreadInterrupted2{

    public static void main(String[] args) {
        log.info("给线程{}打终止标志",Thread.currentThread().getName());
        Thread.currentThread().interrupt();
        log.info("线程{}是否已经停止 1？={}",Thread.currentThread().getName(),Thread.interrupted());
        log.info("线程{}是否已经停止 2？={}",Thread.currentThread().getName(),Thread.interrupted());
        log.info("end!");
    }
}
```
<font size="2">执行结果</font>

```
15:33:46.549 [main] INFO cn.itcast.methods.ThreadInterrupted2 - 给线程main打终止标志
15:33:46.553 [main] INFO cn.itcast.methods.ThreadInterrupted2 - 线程main是否已经停止 1？=true
15:33:46.553 [main] INFO cn.itcast.methods.ThreadInterrupted2 - 线程main是否已经停止 2？=false
15:33:46.553 [main] INFO cn.itcast.methods.ThreadInterrupted2 - end!
```
<font size="2">&ensp;&ensp;&ensp;&ensp;从执行结果来看，方法interrupted()的确能判断出当前线程是否是中断（停止）状态。第二个false是什么意思呢，这就验证了我们上面所说的中断状态清除的情况，就是说interrupted()会清除中断状态，如果连续2次调用该方法，则第二次会返回false值（在第一次调用已清除了中端状态之后，且第二次调用检验完中断状态之前，当前线程再次中断的情况除外）。
</font>

##### this.isInterrupted()
<font size="2">&ensp;&ensp;&ensp;&ensp;从jdk源码中的申明中可以看出 isIntermpted() 方法不是 static 的。</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试this.isInterrupted()
 */
@Slf4j
public class ThreadIsIntermpted implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            log.info("i=" + (i + 1));
            if (i == 3) {
                Thread.currentThread().interrupt();
                log.info("线程{}是否已经停止 1？={}",Thread.currentThread().getName(),
                        Thread.currentThread().isInterrupted());
                log.info("线程{}是否已经停止 2？={}",Thread.currentThread().getName(),
                        Thread.currentThread().isInterrupted());
            }
        }
    }

    public static void main(String[] args) {
        ThreadIsIntermpted r = new ThreadIsIntermpted();
        Thread t = new Thread(r);
        t.start();
    }
}

```
<font size="2">执行结果</font>

```
15:55:56.871 [Thread-0] INFO cn.itcast.methods.ThreadIsIntermpted - i=1
15:55:56.874 [Thread-0] INFO cn.itcast.methods.ThreadIsIntermpted - i=2
15:55:56.874 [Thread-0] INFO cn.itcast.methods.ThreadIsIntermpted - i=3
15:55:56.874 [Thread-0] INFO cn.itcast.methods.ThreadIsIntermpted - i=4
15:55:56.874 [Thread-0] INFO cn.itcast.methods.ThreadIsIntermpted - 线程Thread-0是否已经停止 1？=true
15:55:56.876 [Thread-0] INFO cn.itcast.methods.ThreadIsIntermpted - 线程Thread-0是否已经停止 2？=true
15:55:56.876 [Thread-0] INFO cn.itcast.methods.ThreadIsIntermpted - i=5
15:55:56.876 [Thread-0] INFO cn.itcast.methods.ThreadIsIntermpted - i=6
15:55:56.876 [Thread-0] INFO cn.itcast.methods.ThreadIsIntermpted - i=7
15:55:56.876 [Thread-0] INFO cn.itcast.methods.ThreadIsIntermpted - i=8
15:55:56.876 [Thread-0] INFO cn.itcast.methods.ThreadIsIntermpted - i=9
15:55:56.876 [Thread-0] INFO cn.itcast.methods.ThreadIsIntermpted - i=10
```
<font size="2">&ensp;&ensp;&ensp;&ensp;从结果看，2个值都为true，说明方法isInterrupted()并没有清除中断状态标志，和我们之前分析源码的结果一致。
</font>

**总结**

```
interrupted()是static方法，调用的时候要用Thread.interrupted()，而isInterrupted()是实例方法，调用时要用线程的实例调用；
Thread.interrupted()：测试当前线程是否已经是中断状态，执行后具有将状态标志清除为false的功能；
this.isInterrupted()：测试线程Thread对象是否已经是中断状态，但不清除状态标志。
```

### 能停止的线程——异常法
<font size="2">&ensp;&ensp;&ensp;&ensp;有了前面学习过的知识点，就可以通过在线程中用for语句来判断线程是否是停止状态，如果是停止状态，则后面的代码不再运行。</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试异常法暂停线程
 */
@Slf4j
public class ThreadInterruptedException implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 50000; i++) {
            //判断当前线程是否已经停止
            if (Thread.currentThread().isInterrupted()) {
                log.info("已经是停止状态了！我要退出了！");
                break;
            }
            log.info("i = "+(i+1));
        }
        log.info("我还是会执行的");
    }

    public static void main(String[] args) {
        try {
            ThreadInterruptedException r = new ThreadInterruptedException();
            Thread t = new Thread(r);
            t.start();
            Thread.sleep(100);
            t.interrupt();
        } catch (InterruptedException e) {
            log.info("main catch");
            e.printStackTrace();
        }
        log.info("end");
    }
}
```
<font size="2">执行结果</font>

```
...
20:36:06.490 [Thread-0] INFO cn.itcast.methods.ThreadInterruptedException - i = 15758
20:36:06.490 [Thread-0] INFO cn.itcast.methods.ThreadInterruptedException - i = 15759
20:36:06.490 [Thread-0] INFO cn.itcast.methods.ThreadInterruptedException - i = 15760
20:36:06.490 [Thread-0] INFO cn.itcast.methods.ThreadInterruptedException - i = 15761
20:36:06.490 [Thread-0] INFO cn.itcast.methods.ThreadInterruptedException - i = 15762
20:36:06.490 [Thread-0] INFO cn.itcast.methods.ThreadInterruptedException - 已经是停止状态了！我要退出了！
20:36:06.490 [main] INFO cn.itcast.methods.ThreadInterruptedException - end
20:36:06.509 [Thread-0] INFO cn.itcast.methods.ThreadInterruptedException - 我还是会执行的
```
<font size="2">&ensp;&ensp;&ensp;&ensp;上面的线程虽然停止了，但for循环外面还有执行语句，还是会继续运行的。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;那我们该如何解决语句继续运行的问题呢？
</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试异常法暂停线程
 */
@Slf4j
public class ThreadInterruptedException implements Runnable{
    @Override
    public void run() {
        try {
            for (int i = 0; i < 50000; i++) {
                //判断当前线程是否已经停止
                if (Thread.currentThread().isInterrupted()) {
                    log.info("已经是停止状态了！我要退出了！");
                    throw new InterruptedException();
                }
                log.info("i = "+(i+1));
            }
            log.info("我还是会执行的");
        } catch (InterruptedException e) {
            log.info("ThreadInterruptedException类run方法catch到的异常");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ThreadInterruptedException r = new ThreadInterruptedException();
            Thread t = new Thread(r);
            t.start();
            Thread.sleep(100);
            t.interrupt();
        } catch (InterruptedException e) {
            log.info("main catch");
            e.printStackTrace();
        }
        log.info("end");
    }
}
```
<font size="2">执行结果</font>

```
...
20:43:45.597 [Thread-0] INFO cn.itcast.methods.ThreadInterruptedException - i = 15955
20:43:45.597 [Thread-0] INFO cn.itcast.methods.ThreadInterruptedException - i = 15956
20:43:45.597 [Thread-0] INFO cn.itcast.methods.ThreadInterruptedException - i = 15957
20:43:45.597 [Thread-0] INFO cn.itcast.methods.ThreadInterruptedException - 已经是停止状态了！我要退出了！
20:43:45.597 [Thread-0] INFO cn.itcast.methods.ThreadInterruptedException - ThreadInterruptedException类run方法catch到的异常
20:43:45.597 [main] INFO cn.itcast.methods.ThreadInterruptedException - end
java.lang.InterruptedException
	at cn.itcast.methods.ThreadInterruptedException.run(ThreadInterruptedException.java:17)
	at java.lang.Thread.run(Thread.java:748)
```

<font size="2">&ensp;&ensp;&ensp;&ensp;由上面执行结果可以看出使用异常法可以正常结束线程。
</font>

### 在沉睡中停止
<font size="2">
如果线程在sleep()状态下停止线程，会是什么效果呢？
</font>


```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * 测试在睡眠中停止
 */
@Slf4j
public class ThreadSleepInterrupt implements Runnable{
    @Override
    public void run() {
        try {
            for (int i = 0; i < 10000; i++) {
                log.info("i="+(i+1));
            }
            log.info("run begin");
            TimeUnit.MINUTES.sleep(20);
            log.info("run end");
        } catch (InterruptedException e) {
            log.info("在沉睡中被停止！进入catch！"+Thread.currentThread().isInterrupted());
            e.printStackTrace();
        }
        log.info("线程结束了");
    }

    public static void main(String[] args) {
        try {
            ThreadSleepInterrupt r = new ThreadSleepInterrupt();
            Thread t = new Thread(r);
            t.start();
            TimeUnit.MILLISECONDS.sleep(200);
            t.interrupt();
        } catch (InterruptedException e) {
            log.info("main catch");
            e.printStackTrace();
        }
        log.info("end");
    }
}
```
<font size="2">执行结果</font>


```
...
14:10:25.645 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupt - i=9997
14:10:25.645 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupt - i=9998
14:10:25.645 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupt - i=9999
14:10:25.645 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupt - i=10000
14:10:25.645 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupt - run begin
14:10:25.713 [main] INFO cn.itcast.methods.ThreadSleepInterrupt - end
14:10:25.713 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupt - 在沉睡中被停止！进入catch！false
14:10:25.815 [Thread-0] INFO cn.itcast.methods.ThreadSleepInterrupt - 线程结束了
java.lang.InterruptedException: sleep interrupted
	at java.lang.Thread.sleep(Native Method)
	at java.lang.Thread.sleep(Thread.java:340)
	at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
	at cn.itcast.methods.ThreadSleepInterrupt.run(ThreadSleepInterrupt.java:18)
	at java.lang.Thread.run(Thread.java:748)
```

<font size="2">&ensp;&ensp;&ensp;&ensp;从上面的执行结果来看，如果在sleep状态下停止某一线程，会进入的catch语句，并且清除停止状态值，使之变成false。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;上面一个实验是先sleep，然后再调用interrupt()方法停止线程的，与之相反的操作在学习线程时也要注意。</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @description 测试先停止再遇到sleep
 * @author liuc
 * @date 2021/7/20 13:51
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadInterruptSleep implements Runnable{
    @Override
    public void run() {
        try {
            for (int i = 0; i < 10000; i++) {
                log.info("i="+(i+1));
            }
            log.info("run begin");
            TimeUnit.MINUTES.sleep(20);
            log.info("run end");
        } catch (InterruptedException e) {
            log.error("先停止，在遇到了sleep！进入catch！");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ThreadInterruptSleep r = new ThreadInterruptSleep();
        Thread t = new Thread(r);
        t.start();
        t.interrupt();
        log.info("end");
    }
}
```
<font size="2">执行结果</font>

```
14:12:09.698 [main] INFO cn.itcast.methods.ThreadInterruptSleep - end
...
14:12:09.777 [Thread-0] INFO cn.itcast.methods.ThreadInterruptSleep - i=9997
14:12:09.777 [Thread-0] INFO cn.itcast.methods.ThreadInterruptSleep - i=9998
14:12:09.777 [Thread-0] INFO cn.itcast.methods.ThreadInterruptSleep - i=9999
14:12:09.777 [Thread-0] INFO cn.itcast.methods.ThreadInterruptSleep - i=10000
14:12:09.777 [Thread-0] INFO cn.itcast.methods.ThreadInterruptSleep - run begin
14:12:09.778 [Thread-0] ERROR cn.itcast.methods.ThreadInterruptSleep - 先停止，在遇到了sleep！进入catch！false
14:12:09.778 [Thread-0] INFO cn.itcast.methods.ThreadInterruptSleep - 线程结束了
java.lang.InterruptedException: sleep interrupted
	at java.lang.Thread.sleep(Native Method)
	at java.lang.Thread.sleep(Thread.java:340)
	at java.util.concurrent.TimeUnit.sleep(TimeUnit.java:386)
	at cn.itcast.methods.ThreadInterruptSleep.run(ThreadInterruptSleep.java:23)
	at java.lang.Thread.run(Thread.java:748)
```

### 能停止的线程——暴力停止（不推荐）
<font size="2">&ensp;&ensp;&ensp;&ensp;使用stop()方法停止线程则是非常暴力的。首先我们看一下stop方法的JDK源码：</font>

```java
/**
  * Forces the thread to stop executing.
  * <p>
  * If there is a security manager installed, its <code>checkAccess</code>
  * method is called with <code>this</code>
  * as its argument. This may result in a
  * <code>SecurityException</code> being raised (in the current thread).
  * <p>
  * If this thread is different from the current thread (that is, the current
  * thread is trying to stop a thread other than itself), the
  * security manager's <code>checkPermission</code> method (with a
  * <code>RuntimePermission("stopThread")</c de> argument) is called in
  * addition.
  * Again, this may result in throwing a
  * <code>SecurityException</code> (in the current thread).
  * <p>
  * The thread represented by this thread is forced to stop whatever
  * it is doing abnormally and to throw a newly created
  * <code>ThreadDeath</code> object as an exception.
  * <p>
  * It is permitted to stop a thread that has not yet been started.
  * If the thread is eventually started, it immediately terminates.
  * <p>
  * An application should not normally try to catch
  * <code>ThreadDeath</code> unless it must  dosome extraordinary
  * cleanup operation (note that the throwing of
  * <code>ThreadDeath</code> causes <code>finally</code> clauses of
  * <code>try</code> statements to be  executedbefore the thread
  * officially dies).  If a  <code>catch</code>clause catches a
  * <code>ThreadDeath</code> object, it is important to rethrow the
  * object so that the thread actually dies.
  * <p>
  * The top-level error handler that reacts  tootherwise uncaught
  * exceptions does not print out a message  orotherwise notify the
  * application if the uncaught exception is an instance of
  * <code>ThreadDeath</code>.
  *
  * @exception  SecurityException  if the current thread cannot
  *               modify this thread.
  * @see        #interrupt()
  * @see        #checkAccess()
  * @see        #run()
  * @see        #start()
  * @see        ThreadDeath
  * @see        ThreadGroup#uncaughtException(Thread,Throw ble)
  * @see        SecurityManager#checkAccess(Thread)
  * @see        SecurityManager#checkPermission
  * @deprecated This method is inherently unsafe.  Stopping a thread with
  *       Thread.stop causes it to unlock all of the monitors that it
  *       has locked (as a natural  consequenceof the unchecked
  *       <code>ThreadDeath</code> exception propagating up the stack).  If
  *       any of the objects previously protected by these monitors were in
  *       an inconsistent state, the damaged objects become visible to
  *       other threads, potentially  resultingin arbitrary behavior.  Many
  *       uses of <code>stop</code> should be replaced by code that simply
  *       modifies some variable to indicate that the target thread should
  *       stop running.  The target thread should check this variable
  *       regularly, and return from its run method in an orderly fashion
  *       if the variable indicates that it  isto stop running.  If the
  *       target thread waits for long  periods(on a condition variable,
  *       for example), the <code>interrupt</code> method should be  usedto
  *       interrupt the wait.
  *       For more information, see
  *       <a href="{@docRoot}/../technotes/guides/concu rency/threadPrimitiveDeprecation.html">Why
  *       are Thread.stop, Thread.suspend and Thread.resume Deprecated?</a>.
  */
@Deprecated
public final void stop() {
    SecurityManager security =System.getSecurityManager();
    if (security != null) {
        checkAccess();
        if (this != Thread.currentThread()){
        security.checkPermission(SecuriyConstants.STOP_THREAD_PERMISSIN);
        }
    }
    // A zero status value corresponds to"NEW", it can't change to
    // not-NEW because we hold the lock.
    if (threadStatus != 0) {
        resume(); // Wake up thread if itwas suspended; no-op otherwise
    }
    // The VM can handle all thread states
    stop0(new ThreadDeath());
}
```
<font size="2">&ensp;&ensp;&ensp;&ensp;为啥JDK源码已经废弃了该方法？你可以参考下面两篇文章</font>

[为什么不能用stop停止线程](https://www.jianshu.com/p/5312ef02d5b5)

[为什么不要用stop方法停止线程?](https://zhuanlan.zhihu.com/p/111629972)

<font size="2">&ensp;&ensp;&ensp;&ensp;但作为学习我们还是需要了解一下的，请看下面的例子</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * @description 测试stop方法暴力停止线程
 * @author liuc
 * @date 2021/7/20 15:25
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadStop implements Runnable{
    private int i = 0;
    @Override
    public void run() {
        try {
            while (true){
                i++;
                log.info("i="+i);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ThreadStop r = new ThreadStop();
            Thread t = new Thread(r);
            t.start();
            TimeUnit.SECONDS.sleep(8);
            t.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```

<font size="2">执行结果</font>

```
19:31:16.227 [Thread-0] INFO cn.itcast.methods.ThreadStop - i=1
19:31:17.233 [Thread-0] INFO cn.itcast.methods.ThreadStop - i=2
19:31:18.243 [Thread-0] INFO cn.itcast.methods.ThreadStop - i=3
19:31:19.253 [Thread-0] INFO cn.itcast.methods.ThreadStop - i=4
19:31:20.258 [Thread-0] INFO cn.itcast.methods.ThreadStop - i=5
19:31:21.262 [Thread-0] INFO cn.itcast.methods.ThreadStop - i=6
19:31:22.269 [Thread-0] INFO cn.itcast.methods.ThreadStop - i=7
19:31:23.276 [Thread-0] INFO cn.itcast.methods.ThreadStop - i=8

Process finished with exit code 0
```
<font size="2">&ensp;&ensp;&ensp;&ensp;由执行结果可以看出，线程被暴力结束了。</font>

### 方法stop()与java.lang.ThreadDeath异常
<font size="2">&ensp;&ensp;&ensp;&ensp;调用stop()方法时会抛出java.lang.ThreadDeath异常，但在通常的情况下，此异常不需要显示的捕捉。</font>

```java
package cn.itcast.methods;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 测试stop()与java.lang.ThreadDeath异常的方式停止线程
 * @author liuc
 * @date 2021/7/20 19:47
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadStopThreadDeath implements Runnable{
    @Override
    public void run() {
        try{
            Thread.currentThread().stop();
        } catch (ThreadDeath e) {
            log.info("进入catch！");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ThreadStopThreadDeath r = new ThreadStopThreadDeath();
        Thread t = new Thread(r);
        t.start();
    }
}
```
<font size="2">执行结果</font>

```
19:52:24.744 [Thread-0] INFO cn.itcast.methods.ThreadStopThreadDeath - 进入catch！
java.lang.ThreadDeath
	at java.lang.Thread.stop(Thread.java:853)
	at cn.itcast.methods.ThreadStopThreadDeath.run(ThreadStopThreadDeath.java:16)
	at java.lang.Thread.run(Thread.java:748)
```

### 释放锁的不良后果
<font size="2">&ensp;&ensp;&ensp;&ensp;使用stop()方法会对锁定的对象进行了“解锁”，导致数据得不到同步的处理，出现数据不一致的问题。如果出现这样的情况，程序处理的数据就有可能遭到破坏，最终导致程序执行的流程错误，一定要特别注意。</font>

```
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * @description 测试stop()方法释放锁的不良后果
 * @author liuc
 * @date 2021/7/20 20:19
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadStopThrowLock implements Runnable{
    private User user;
    public ThreadStopThrowLock (User user){
        this.user = user;
    }
    @Override
    public void run() {
        user.printString("b","bb");
    }

    public static void main(String[] args) {
        try {
            User u = new User();
            ThreadStopThrowLock r = new ThreadStopThrowLock(u);
            Thread t = new Thread(r);
            t.start();
            TimeUnit.MILLISECONDS.sleep(500);
            t.stop();
            log.info(u.getUserName()+","+u.getPassword());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class User {
    private String userName ="a";
    private String password = "aa";

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    synchronized public void printString(String userName,String password){
        try {
            this.userName = userName;
            TimeUnit.MINUTES.sleep(1);
            this.password = password;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
<font size="2">执行结果</font>

```
20:39:30.317 [main] INFO cn.itcast.methods.ThreadStopThrowLock - b,aa

Process finished with exit code 0
```
<font size="2">&ensp;&ensp;&ensp;&ensp;由于stop()方法已经在JDK中被标明是“作废/过期”的方法，显然它在功能上具有缺陷，所以不建议在程序中使用stop()方法停止线程。</font>

### 使用return停止线程
<font size="2">&ensp;&ensp;&ensp;&ensp;将interrupt()方法与return结合使用也能实现停止线程的效果。</font>

```java
package cn.itcast.methods;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @description 测试使用interrupt()与return结合的方式停止线程
 * @author liuc
 * @date 2021/7/20 21:07
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadUserReturnInterrupt implements Runnable{
    @Override
    public void run() {
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                log.info(Thread.currentThread().getName()+"线程停止了");
                return;
            }
            log.info("time="+System.currentTimeMillis());
        }
    }

    public static void main(String[] args) {
        try {
            ThreadUserReturnInterrupt r = new ThreadUserReturnInterrupt();
            Thread t = new Thread(r);
            t.start();
            TimeUnit.MILLISECONDS.sleep(10);
            t.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
<font size="2">执行结果</font>

```
...
21:17:07.364 [Thread-0] INFO cn.itcast.methods.ThreadUserReturnInterrupt - time=1626787027364
21:17:07.364 [Thread-0] INFO cn.itcast.methods.ThreadUserReturnInterrupt - time=1626787027364
21:17:07.364 [Thread-0] INFO cn.itcast.methods.ThreadUserReturnInterrupt - time=1626787027364
21:17:07.364 [Thread-0] INFO cn.itcast.methods.ThreadUserReturnInterrupt - time=1626787027364
21:17:07.364 [Thread-0] INFO cn.itcast.methods.ThreadUserReturnInterrupt - time=1626787027364
21:17:07.365 [Thread-0] INFO cn.itcast.methods.ThreadUserReturnInterrupt - time=1626787027365
21:17:07.365 [Thread-0] INFO cn.itcast.methods.ThreadUserReturnInterrupt - Thread-0线程停止了
```

<font size="2" color ="red">&ensp;&ensp;&ensp;&ensp;综合上述的讲解，还是建议使用“异常法”的方式来实现线程的停止，因为在catch块中还可以将异常向上抛，使线程停止的事件得以传播。</font>

