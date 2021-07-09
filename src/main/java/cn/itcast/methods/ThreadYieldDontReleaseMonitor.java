package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 展示线程yield的时候不释放synchronized的monitor，等sleep时间到了以后，正常结束后才释放锁
 */
@Slf4j
public class ThreadYieldDontReleaseMonitor implements Runnable{
    @Override
    public void run() {
        syn();
    }

    private synchronized void syn(){
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
    }
    public static void main(String[] args) {
        ThreadYieldDontReleaseMonitor r = new ThreadYieldDontReleaseMonitor();
        Thread t = new Thread(r,"A");
        Thread t1 = new Thread(r,"B");
        t.start();
        t1.start();
    }
}
