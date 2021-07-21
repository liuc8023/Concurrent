<font size="2">&ensp;&ensp;&ensp;&ensp;暂停线程意味着此线程还可以恢复运行。在Java多线程中，可以使用suspend()方法暂停线程，使用resume()方法恢复线程的执行。</font>

### suspend与resume方法的使用
<font size="2">&ensp;&ensp;&ensp;&ensp;首先我们看一下suspend与resume的JDK源码</font>

```java
/**
 * Suspends this thread.
 * <p>
 * First, the <code>checkAccess</code> metho of this thread is called
 * with no arguments. This may result i throwing a
 * <code>SecurityException </code>(in th current thread).
 * <p>
 * If the thread is alive, it is suspende and makes no further
 * progress unless and until it is resumed.
 *
 * @exception  SecurityException  if th current thread cannot modify
 *               this thread.
 * @see #checkAccess
 * @deprecated   This method has bee deprecated, as it is
 *   inherently deadlock-prone.  If th target thread holds a lock on the
 *   monitor protecting a critical syste resource when it is suspended, no
 *   thread can access this resource unti the target thread is resumed. If
 *   the thread that would resume the targe thread attempts to lock this
 *   monitor prior to callin <code>resume</code>, deadlock results.  Such
 *   deadlocks typically manifest themselve as "frozen" processes.
 *   For more information, see
 *   <a href="{@docRoot}/../technotes/guide concurrency/threadPrimitiveDeprecation.htm >Why
 *   are Thread.stop, Thread.suspend an Thread.resume Deprecated?</a>.
 */
 @Deprecated
 public final void suspend() {
     checkAccess();
     suspend0();
 }
  
    
 /**
  * Resumes a suspended thread.
  * <p>
  * First, the <code>checkAccess</code> metho  of this thread is called
  * with no arguments. This may result i  throwing a
  * <code>SecurityException</code> (in th  current thread).
  * <p>
  * If the thread is alive but suspended, i  is resumed and is
  * permitted to make progress in it  execution.
  *
  * @exception  SecurityException  if th  current thread cannot modify this
  *               thread.
  * @see        #checkAccess
  * @see        #suspend()
  * @deprecated This method exists solely fo  use with {@link #suspend},
  *     which has been deprecated because i  is deadlock-prone.
  *     For more information, see
  *     <  href="{@docRoot}/../technotes/guides/concu  ency/threadPrimitiveDeprecation.html">Why
  *     are Thread.stop, Thread.suspend an  Thread.resume Deprecated?</a>.
  */
  @Deprecated
  public final void resume() {
      checkAccess();
      resume0();
  }
```

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * @description 测试suspend与resume方法
 * @author liuc
 * @date 2021/7/21 9:36
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadSuspendResume implements Runnable{
    private int i = 0;
    @Override
    public void run() {
        try {
            while (true) {
                i++;
                TimeUnit.SECONDS.sleep(1);
                log.info("i="+i);
                //当i等于10的时候，使用异常法停止线程
                while (i == 10) {
                    //使用异常法停止线程
                    throw new InterruptedException();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ThreadSuspendResume r = new ThreadSuspendResume();
            Thread t = new Thread(r);
            t.start();
            TimeUnit.SECONDS.sleep(5);
            t.suspend();
            log.info("suspend time1:"+System.currentTimeMillis());
            TimeUnit.SECONDS.sleep(5);
            t.resume();
            log.info("resume time2:"+System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
<font size="2">执行结果</font>

```
10:06:21.200 [Thread-0] INFO cn.itcast.methods.ThreadSuspendResume - i=1
10:06:22.203 [Thread-0] INFO cn.itcast.methods.ThreadSuspendResume - i=2
10:06:23.208 [Thread-0] INFO cn.itcast.methods.ThreadSuspendResume - i=3
10:06:24.211 [Thread-0] INFO cn.itcast.methods.ThreadSuspendResume - i=4
10:06:25.198 [main] INFO cn.itcast.methods.ThreadSuspendResume - suspend time1:1626833185198
10:06:30.214 [Thread-0] INFO cn.itcast.methods.ThreadSuspendResume - i=5
10:06:30.214 [main] INFO cn.itcast.methods.ThreadSuspendResume - resume time2:1626833190214
10:06:31.218 [Thread-0] INFO cn.itcast.methods.ThreadSuspendResume - i=6
10:06:32.221 [Thread-0] INFO cn.itcast.methods.ThreadSuspendResume - i=7
10:06:33.225 [Thread-0] INFO cn.itcast.methods.ThreadSuspendResume - i=8
10:06:34.230 [Thread-0] INFO cn.itcast.methods.ThreadSuspendResume - i=9
10:06:35.234 [Thread-0] INFO cn.itcast.methods.ThreadSuspendResume - i=10
java.lang.InterruptedException
	at cn.itcast.methods.ThreadSuspendResume.run(ThreadSuspendResume.java:27)
	at java.lang.Thread.run(Thread.java:748)

Process finished with exit code 0

```
<font size="2">&ensp;&ensp;&ensp;&ensp;从执行结果的打印时间来看，线程的确被暂停了，而且还可以恢复成运行的状态。</font>

### suspend与resume方法的缺点——独占
<font size="2">&ensp;&ensp;&ensp;&ensp;在使用suspend与resume方法时，如果使用不当，极易造成公共的同步对象的独占，使得其他线程无法访问公共同步对象。</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * @description 测试suspend与resume方法的缺点——独占的问题
 * @author liuc
 * @date 2021/7/21 11:15
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadSuspendResumeDealLock implements Runnable{
    final SuspendResumeDealLock obj = new SuspendResumeDealLock();
    @Override
    public void run() {
        log.info(Thread.currentThread().getName()+"线程进入了");
        obj.printString();
    }

    public static void main(String[] args) {
        try {
            ThreadSuspendResumeDealLock r = new ThreadSuspendResumeDealLock();
            Thread t = new Thread(r,"a");
            t.start();
            TimeUnit.SECONDS.sleep(1);
            Thread t1 = new Thread(r,"b");
            t1.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

@Slf4j
class SuspendResumeDealLock {
    public synchronized void printString () {
        log.info("begin");
        if (Thread.currentThread().getName().equals("a")) {
            log.info("a线程永远的suspend了！");
            Thread.currentThread().suspend();
        }
        log.info("end");
    }
}
```
<font size="2">执行结果</font>

```
11:25:56.346 [a] INFO cn.itcast.methods.ThreadSuspendResumeDealLock - a线程进入了
11:25:56.349 [a] INFO cn.itcast.methods.SuspendResumeDealLock - begin
11:25:56.349 [a] INFO cn.itcast.methods.SuspendResumeDealLock - a线程永远的suspend了！
11:25:57.347 [b] INFO cn.itcast.methods.ThreadSuspendResumeDealLock - b线程进入了
```
<font size="2">&ensp;&ensp;&ensp;&ensp;由执行结果可以看出，a线程独占并锁死了printString方法，a线程永远被suspend暂停了。</font>

### println独占问题

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @description 测试suspend的调用导致println()函数的独占的问题
 * @author liuc
 * @date 2021/7/21 14:53
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadSuspendPrintln implements Runnable{
    private int i = 0;
    @Override
    public void run() {
        while (true) {
            i++;
        }
    }

    public static void main(String[] args) {
        try {
            ThreadSuspendPrintln r = new ThreadSuspendPrintln();
            Thread t = new Thread(r);
            t.start();
            TimeUnit.SECONDS.sleep(2);
            t.suspend();
            log.info("main end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
<font size="2">执行结果</font>

```
14:59:46.511 [main] INFO cn.itcast.methods.ThreadSuspendPrintln - main end
```
<font size="2">如果将上面的线程更改如下</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @description 测试suspend的调用导致println()函数的独占的问题
 * @author liuc
 * @date 2021/7/21 14:53
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadSuspendPrintln implements Runnable{
    private int i = 0;
    @Override
    public void run() {
        while (true) {
            i++;
            System.out.println(i);
        }
    }

    public static void main(String[] args) {
        try {
            ThreadSuspendPrintln r = new ThreadSuspendPrintln();
            Thread t = new Thread(r);
            t.start();
            TimeUnit.SECONDS.sleep(2);
            t.suspend();
            log.info("main end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
```
<font size="2">执行结果</font>

```
...
9091
9092
9093
9094
9095
9096
9097
```
<font size="2">&ensp;&ensp;&ensp;&ensp;由执行结果可以看出，main主函数没有打印的main end，首先我们看一下println()方法的JDK源码 </font>

```java
/**
 * Prints an integer andthen terminate the line. This method behaves as
 * though it invokes<code>{@link#print(int)}</code> andthen
 * <code>{@link#println()}</code>.
 *
 * @param x  The<code>int</code> to beprinted.
 */
public void println(int x){
    synchronized (this) {
        print(x);
        newLine();
    }
}
```
<font size="2">&ensp;&ensp;&ensp;&ensp;因为println(int x)方法使用了synchronized锁锁住了当前对象，当使用println(int x)方法打印的时候，突然调用了suspend()方法，那么suspend()就会独占这把锁且不会主动释放当前锁，导致println方法一直处于“暂停状态”，而后面的程序无法正常执行，也就出现了上面的执行结果，数字正常打印但是main方法中的"main end"字符串没有打印的原因。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;虽然suspend()与resume()方法都是过期作废的方法，但还是有必要研究它过期作废的原因，这是很有意义的。</font>

### suspend与resume方法的缺点——不同步
<font size="2">&ensp;&ensp;&ensp;&ensp;在使用suspend与resume方法时也容易出现因为线程的暂停而导致数据不同步的情况。</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * @description 测试suspend与resume方法的缺点，数据不同步的问题
 * @author liuc
 * @date 2021/7/21 16:47
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadSuspendResumeNoSameValue implements Runnable{
    final SuspendResumeNoSameValue obj = new SuspendResumeNoSameValue();
    @Override
    public void run() {
        if (Thread.currentThread().getName().equals("A")) {
            log.info(Thread.currentThread().getName()+"线程修改值");
            obj.setValue("hh","eeee");
        }
        if (Thread.currentThread().getName().equals("B")) {
            obj.printUserNamePassWord();
        }
    }

    public static void main(String[] args) {
        try {
            ThreadSuspendResumeNoSameValue r = new ThreadSuspendResumeNoSameValue();
            Thread t = new Thread(r,"A");
            t.start();
            TimeUnit.SECONDS.sleep(2);
            Thread t1 = new Thread(r,"B");
            t1.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

@Slf4j
class SuspendResumeNoSameValue{
    private String userName = "1";
    private String passWord = "11";
    public void setValue(String u,String p){
        this.userName = u;
        if (Thread.currentThread().getName().equals("A")) {
            log.info("暂停线程A");
            Thread.currentThread().suspend();
        }
        this.passWord = p;
    }
    public void printUserNamePassWord() {
        log.info("userName = {},passWord = {}",userName,passWord);
    }
}
```
<font size="2">执行结果</font>

```
17:02:19.137 [A] INFO cn.itcast.methods.ThreadSuspendResumeNoSameValue - A线程修改值
17:02:19.139 [A] INFO cn.itcast.methods.SuspendResumeNoSameValue - 暂停线程A
17:02:21.132 [B] INFO cn.itcast.methods.SuspendResumeNoSameValue - userName = hh,passWord = 11
```
<font size="2">&ensp;&ensp;&ensp;&ensp;执行结果出现值不同步的情况，所以在程序中使用suspend()方法要格外注意。关于如何解决这些问题，请看后面的章节。</font>

