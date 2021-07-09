package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * 线程每隔1秒钟输出当前时间，运行时被中断
 */
@Slf4j
public class ThreadSleepInterrupted implements Runnable{

    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            log.info("当前时间："+System.currentTimeMillis());
            try {
                TimeUnit.SECONDS.sleep(1);
            } catch (InterruptedException e) {
                System.out.println("我被中断了！");
                e.printStackTrace();
            }
        }
    }

    public static void main(String[] args) {
        ThreadSleepInterrupted r = new ThreadSleepInterrupted();
        Thread t = new Thread(r);
        t.start();
        try {
            Thread.sleep(6500);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        t.interrupt();
    }
}
