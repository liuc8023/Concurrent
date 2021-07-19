<font size="2">先上一段代码，运行结果是什么
</font>

```java
package cn.itcast;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadTest {
    public static void main(String[] args) {
        Thread t = new Thread(){
            @Override
            public void run(){
                sougo();
            }
        };
        t.run();
        log.info("Hello:"+Thread.currentThread().getName()+"线程");
    }
    static synchronized void sougo(){
        log.info("Sougo:"+Thread.currentThread().getName()+"线程");
    }
}
```

<font size="2">执行结果</font>
```
21:05:55.066 [main] INFO cn.itcast.ThreadTest - Sougo:main线程
21:05:55.068 [main] INFO cn.itcast.ThreadTest - Hello:main线程
```

<font size="2">如果修改为t.start(),结果是
</font>
```java
package cn.itcast;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadTest {
    public static void main(String[] args) {
        Thread t = new Thread(){
            @Override
            public void run(){
                sougo();
            }
        };
        t.start();
        log.info("Hello:"+Thread.currentThread().getName()+"线程");
    }
    static synchronized void sougo(){
        log.info("Sougo:"+Thread.currentThread().getName()+"线程");
    }
}
```
<font size="2">执行结果</font>
```
21:07:51.875 [main] INFO cn.itcast.ThreadTest - Hello:main线程
21:07:51.875 [Thread-0] INFO cn.itcast.ThreadTest - Sougo:Thread-0线程
```

**start**

```
	它的作用是启动一个新线程。
	通过start()方法来启动的新线程，处于就绪（可运行）状态，并没有运行，一旦得到cpu时间片，就开始执行相应线
程的run()方法，这里方法run()称为线程体，它包含了要执行的这个线程的内容，run方法运行结束，此线程随即终止。start()不能被重复调用。用start方
法来启动线程，真正实现了多线程运行，即无需等待某个线程的run方法体代码执行完毕就直接继续执行下面的代码。这里无需等待run方法执行完毕，即可继
续执行下面的代码，即进行了线程切换。
```

**run**

```
	run方法就和普通的成员方法一样，可以被重复调用。
	如果直接调用run方法，并不会启动新线程！程序中依然只有主线程这一个线程，其程序执行路径还是只有一条，还是要顺序执行，还是要等待run方法体
执行完毕后才可继续执行下面的代码，这样就没有达到多线程的目的。
```

<font size="2">&ensp;&ensp;&ensp;&ensp;总结：调用start方法方可启动线程，而run方法只是thread的一个普通方法调用，还是在主线程里</font>

1.  <font size="2">start方法可以启动一个线程，run方法不能</font>
2. <font size="2">start方法不能被重复调用，run方法可以</font>
3. <font size="2">start中的run方法可以不执行完就继续执行下面的代码，即进行了线程切换。直接调用run方法必须等待其代码全部执行完才能继续执行下面的代码</font>
4. <font size="2">start方法实现了多线程，run方法没有执行多线程</font>

