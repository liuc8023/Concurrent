<font size="2">&ensp;&ensp;&ensp;&ensp;在 Java 语言中，线程的优先级范围是 1~10，值必须在 1~10，否则会出现异常；优先级的默认值为 5。优先级较高的线程会被优先执行，当执行完毕，才会轮到优先级较低的线程执行。如果优先级相同，那么就采用轮流执行的方式。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;可以使用 Thread 类中的 setPriority() 方法来设置线程的优先级。语法如下：
</font>

```java
public final void setPriority(int newPriority);
```
<font size="2">如果要获取当前线程的优先级，可以直接调用 getPriority() 方法。语法如下：
</font>

```java
public final int getPriority();
```
<font size="2">&ensp;&ensp;&ensp;&ensp;简单了解过优先级之后，下面通过一个简单的例子来演示如何使用优先级。
</font>
```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadPriority implements Runnable{
    @Override
    public void run() {
        log.info(Thread.currentThread().getName()+"-->"+Thread.currentThread().getPriority());
        Thread.yield();
    }

    public static void main(String[]args) throws InterruptedException {
        log.info(Thread.currentThread().getName()+"-->"+Thread.currentThread().getPriority());
        ThreadPriority r = new ThreadPriority();
        Thread t1=new Thread(r);
        Thread t2=new Thread(r);
        Thread t3=new Thread(r);
        Thread t4=new Thread(r);
        Thread t5=new Thread(r);
        Thread t6=new Thread(r);

        t1.setPriority(Thread.MAX_PRIORITY);
        t2.setPriority(Thread.MAX_PRIORITY);
        t3.setPriority(Thread.MAX_PRIORITY);
        t4.setPriority(Thread.MIN_PRIORITY);
        t5.setPriority(Thread.MIN_PRIORITY);
        t6.setPriority(Thread.MIN_PRIORITY);
        //设置优先级在启动前
        t1.start();
        t2.start();
        t3.start();
        t4.start();
        t5.start();
        t6.start();
    }
}
```
<font size="2">执行结果</font>
```
16:34:06.886 [main] INFO cn.itcast.methods.ThreadPriority - main-->5
16:34:06.889 [Thread-0] INFO cn.itcast.methods.ThreadPriority - Thread-0-->10
16:34:06.889 [Thread-1] INFO cn.itcast.methods.ThreadPriority - Thread-1-->10
16:34:06.889 [Thread-2] INFO cn.itcast.methods.ThreadPriority - Thread-2-->10
16:34:06.889 [Thread-3] INFO cn.itcast.methods.ThreadPriority - Thread-3-->1
16:34:06.889 [Thread-5] INFO cn.itcast.methods.ThreadPriority - Thread-5-->1
16:34:06.889 [Thread-4] INFO cn.itcast.methods.ThreadPriority - Thread-4-->1
```

#### 5.1、线程优先级的继承性
<font size="2">&ensp;&ensp;&ensp;&ensp;Java中的线程优先级具有继承特性，如A线程继承了B线程，那么A、B线程优先级一样。
</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试线程优先级的继承性
 */
@Slf4j
public class ThreadPriorityExtends implements Runnable{
    @Override
    public void run() {
        ThreadPriorityExtends1 r1 = new ThreadPriorityExtends1();
        Thread t1 = new Thread(r1,"B");
        log.info(t1.getName()+"线程的优先级是:"+Thread.currentThread().getPriority());
    }
}

class ThreadPriorityExtends1 extends ThreadPriorityExtends {

}

@Slf4j
class Test{
    public static void main(String[] args) {
        log.info(Thread.currentThread().getName()+":"+Thread.currentThread().getPriority());
        ThreadPriorityExtends r = new ThreadPriorityExtends();
        Thread t = new Thread(r,"A");
        log.info(t.getName()+"修改线程优先级之前的优先级是:"+t.getPriority());
        t.setPriority(Thread.MAX_PRIORITY);
        log.info(t.getName()+"修改线程优先级之后的优先级是:"+t.getPriority());
        t.start();
    }
}
```
<font size="2">执行结果</font>

```
17:21:46.691 [main] INFO cn.itcast.methods.Test - main:5
17:21:46.695 [main] INFO cn.itcast.methods.Test - A修改线程优先级之前的优先级是:5
17:21:46.695 [main] INFO cn.itcast.methods.Test - A修改线程优先级之后的优先级是:10
17:21:46.695 [A] INFO cn.itcast.methods.ThreadPriorityExtends - B线程的优先级是:10
```
<font size="2">&ensp;&ensp;&ensp;&ensp;从执行结果可以看出，当一个线程不设置优先级时，他的优先级就是默认的优先级5，所以当线程A的优先级改为10时，继承了A线程的B线程也相应的改变了优先级，由此可以得出结论，优先级具有继承性。
</font>

#### 5.2、线程优先级的规则性

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.security.SecureRandom;

/**
 * 测试线程优先级的规则性
 */
@Slf4j
public class ThreadPriorityRegularity implements Runnable{
    @Override
    public void run() {
        long beginTime = System.currentTimeMillis();
        long addResult = 0;
        for (int i = 0; i <10; i++) {
            for (int j = 0; j < 5000; j++) {
                SecureRandom random = new SecureRandom();
                random.nextInt();
                addResult = addResult+j;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info(Thread.currentThread().getName()+"线程执行时间"+(endTime-beginTime));
    }

    public static void main(String[] args) {
        for(int i=0;i<5;i++){
            ThreadPriorityRegularity r = new ThreadPriorityRegularity();
            Thread t = new Thread(r,"A");
            Thread t1 = new Thread(r,"B");
            t.setPriority(Thread.MAX_PRIORITY);
            t1.setPriority(Thread.MIN_PRIORITY);
            t.start();
            t1.start();
        }
    }
}
```
<font size="2">执行结果</font>

```
14:10:55.727 [A] INFO cn.itcast.methods.ThreadPriorityRegularity - A线程执行时间1529
14:10:55.896 [A] INFO cn.itcast.methods.ThreadPriorityRegularity - A线程执行时间1714
14:10:55.912 [A] INFO cn.itcast.methods.ThreadPriorityRegularity - A线程执行时间1730
14:10:55.949 [A] INFO cn.itcast.methods.ThreadPriorityRegularity - A线程执行时间1775
14:10:55.996 [A] INFO cn.itcast.methods.ThreadPriorityRegularity - A线程执行时间1814
14:10:56.096 [B] INFO cn.itcast.methods.ThreadPriorityRegularity - B线程执行时间1912
14:10:56.128 [B] INFO cn.itcast.methods.ThreadPriorityRegularity - B线程执行时间1946
14:10:56.150 [B] INFO cn.itcast.methods.ThreadPriorityRegularity - B线程执行时间1966
14:10:56.150 [B] INFO cn.itcast.methods.ThreadPriorityRegularity - B线程执行时间1966
14:10:56.165 [B] INFO cn.itcast.methods.ThreadPriorityRegularity - B线程执行时间1981
```
<font size="2">&ensp;&ensp;&ensp;&ensp;每当线程调度器有机会选择新线程时，它首先选择具有较高优先级的线程，这是优先级的**规则性**。所以一般来说，高优先级的进程大部分先执行完，但不代表高优先级的进程全部先执行完。例如虽然设置了优先级，但启动线程start()有先后顺序等影响了线程的执行顺序。
当两个线程的优先级差别很大，比如A线程为10，B线程为1，此时谁先执行完与启动顺序无关。因为A线程的优先级较B线程来说很高，所以即使A线程后启动也会在B线程之前执行完。
</font>

#### 5.3、线程优先级的随机性
<font size="2">&ensp;&ensp;&ensp;&ensp;如果两个线程的优先级很接近，比如A优先级为6，B优先级为5，那么谁先执行完就受优先级和启动顺序的共同影响，表现出随机性。
</font>

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.security.SecureRandom;

/**
 * 测试线程优先级的随机性
 */
@Slf4j
public class ThreadPriorityRandomicity implements Runnable{
    @Override
    public void run() {
        long beginTime = System.currentTimeMillis();
        long addResult = 0;
        for (int i = 0; i <10; i++) {
            for (int j = 0; j < 5000; j++) {
                SecureRandom random = new SecureRandom();
                random.nextInt();
                addResult = addResult+j;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info(Thread.currentThread().getName()+"线程执行时间"+(endTime-beginTime));
    }

    public static void main(String[] args) {
        for(int i=0;i<5;i++){
            ThreadPriorityRandomicity r = new ThreadPriorityRandomicity();
            Thread t = new Thread(r,"A");
            Thread t1 = new Thread(r,"B");
            t.setPriority(6);
            t1.setPriority(5);
            t.start();
            t1.start();
        }
    }
}
```
<font size="2">执行结果</font>

```
14:27:08.614 [A] INFO cn.itcast.methods.ThreadPriorityRandomicity - A线程执行时间1817
14:27:08.645 [B] INFO cn.itcast.methods.ThreadPriorityRandomicity - B线程执行时间1848
14:27:08.661 [A] INFO cn.itcast.methods.ThreadPriorityRandomicity - A线程执行时间1864
14:27:08.661 [B] INFO cn.itcast.methods.ThreadPriorityRandomicity - B线程执行时间1864
14:27:08.676 [B] INFO cn.itcast.methods.ThreadPriorityRandomicity - B线程执行时间1879
14:27:08.683 [B] INFO cn.itcast.methods.ThreadPriorityRandomicity - B线程执行时间1886
14:27:08.683 [B] INFO cn.itcast.methods.ThreadPriorityRandomicity - B线程执行时间1886
14:27:08.683 [A] INFO cn.itcast.methods.ThreadPriorityRandomicity - A线程执行时间1886
14:27:08.714 [A] INFO cn.itcast.methods.ThreadPriorityRandomicity - A线程执行时间1917
14:27:08.714 [A] INFO cn.itcast.methods.ThreadPriorityRandomicity - A线程执行时间1917
```
**注意**

```
    初级程序员常常过度使用线程优先级，不要将程序构建为功能的正确性依赖于优先级。 如果确实要使用优先级，应该避免初学者常犯的一个错误。如果
有几个高优先级的线程没有进入非活动状态，低优先级的线程可能永远也不能执行。每当调度器决定运行一个新线程时,首先会在具有高优先级的线程中进行
选择,尽管这样会使低优先级的线程完全饿死。
```