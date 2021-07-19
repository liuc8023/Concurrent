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
            log.info("run begin");
            TimeUnit.MINUTES.sleep(20);
            log.info("run end");
        } catch (InterruptedException e) {
            log.info("在沉睡中被停止！进入catch！"+Thread.currentThread().isInterrupted());
            e.printStackTrace();
        }
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
