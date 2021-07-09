package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
/**
 * 测试yield方法不释放锁
 */
@Slf4j
public class ThreadYieldDontReleaseLock implements Runnable{
    private static final Lock lock = new ReentrantLock();

    @Override
    public void run() {
        String threadName = Thread.currentThread().getName();
        lock.lock();
        try {
            log.info("线程{}获取到了锁", threadName);
            for (int i = 0 ; i < 10 ; i++ ){
                // 打印内容
                log.info(Thread.currentThread().getName()+"======threadYield======"+i);
                // 当i为5时，该线程就会把CPU时间让掉，让其他或者自己的线程执行（也就是谁先抢到谁执行）
                if (i == 5) {
                    log.info(Thread.currentThread().getName()+"线程让行");
                    // 线程让行
                    Thread.yield();
                }
            }
            log.info("线程{}已经苏醒", threadName);
        } finally {
            lock.unlock();
        }
    }

    public static void main(String[] args) {
        ThreadYieldDontReleaseLock r = new ThreadYieldDontReleaseLock();
        Thread t = new Thread(r,"A");
        Thread t1 = new Thread(r,"B");
        t.start();
        t1.start();
    }
}
