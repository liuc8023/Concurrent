<font size="2">&ensp;&ensp;&ensp;&ensp;isAlive方法用于检查线程是否还活着，它是一个native方法，但不是静态方法，也就是说它必须被线程的实例所调用。</font>

<font size="2">&ensp;&ensp;&ensp;&ensp;其实大家可以思考一下它为什么不是静态方法，因为静态方法一般都是作用于当前正在执行的线程，既然是“当前正在执行”，那必然是Alive的，所以作为静态方法调用并没有意义。</font>

```java
/**
 * Tests if this thread is alive. A thread is alive if it has
 * been started and has not yet died.
 *
 * @return  <code>true</code> if this thread is alive;
 *          <code>false</code> otherwise.
 */
 public final native boolean isAlive();
```

```java
package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试isAlive方法
 */
@Slf4j
public class ThreadIsAlive implements Runnable{
    @Override
    public void run() {
        log.info(Thread.currentThread().getName()+"线程运行时，线程是否是活着的？"+Thread.currentThread().isAlive());
    }

    public static void main(String[] args) {
        ThreadIsAlive r = new ThreadIsAlive();
        Thread t = new Thread(r);
        log.info(t.getName()+"线程启动前，线程是否是活着的？"+t.isAlive());
        t.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info(t.getName()+"线程执行完成之后，线程是否是活着的？"+t.isAlive());
    }
}
```
<font size="2">执行结果</font>

```
20:59:17.684 [main] INFO cn.itcast.methods.ThreadIsAlive - Thread-0线程启动前，线程是否是活着的？false
20:59:17.694 [Thread-0] INFO cn.itcast.methods.ThreadIsAlive - Thread-0线程运行时，线程是否是活着的？true
20:59:18.699 [main] INFO cn.itcast.methods.ThreadIsAlive - Thread-0线程执行完成之后，线程是否是活着的？false
```

