package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @description 测试先停止再遇到sleep
 * @author liuc
 * @date 2021/7/20 13:51
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadInterruptSleep implements Runnable{
    @Override
    public void run() {
        try {
            for (int i = 0; i < 10000; i++) {
                log.info("i="+(i+1));
            }
            log.info("run begin");
            TimeUnit.MINUTES.sleep(20);
            log.info("run end");
        } catch (InterruptedException e) {
            log.error("先停止，在遇到了sleep！进入catch！"+Thread.currentThread().isInterrupted());
            e.printStackTrace();
        }
        log.info("线程结束了");
    }

    public static void main(String[] args) {
        ThreadInterruptSleep r = new ThreadInterruptSleep();
        Thread t = new Thread(r);
        t.start();
        t.interrupt();
        log.info("end");
    }
}
