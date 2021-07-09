package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 展示线程sleep的时候不释放synchronized的monitor，等sleep时间到了以后，正常结束后才释放锁
 */
@Slf4j
public class ThreadSleepDontReleaseMonitor implements Runnable{
    @Override
    public void run() {
        syn();
    }

    private synchronized void syn(){
        log.info(Thread.currentThread().getName()+"线程获取到了同步锁");
        try {
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info(Thread.currentThread().getName()+"线程醒了，退出同步代码块");
    }
    public static void main(String[] args) {
        ThreadSleepDontReleaseMonitor r = new ThreadSleepDontReleaseMonitor();
        Thread t = new Thread(r,"A");
        Thread t1 = new Thread(r,"B");
        t.start();
        t1.start();
    }
}
