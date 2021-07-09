package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

/**
 * 测试sleep方法不释放锁
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
        ThreadSleepDontReleaseLock r1 = new ThreadSleepDontReleaseLock();
        Thread t1 = new Thread(r1,"B");
        t.start();
        t1.start();
    }
}
