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
