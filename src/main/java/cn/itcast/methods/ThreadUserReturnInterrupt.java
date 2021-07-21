package cn.itcast.methods;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @description 测试使用interrupt()与return结合的方式停止线程
 * @author liuc
 * @date 2021/7/20 21:07
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadUserReturnInterrupt implements Runnable{
    @Override
    public void run() {
        while (true) {
            if (Thread.currentThread().isInterrupted()) {
                log.info(Thread.currentThread().getName()+"线程停止了");
                return;
            }
            log.info("time="+System.currentTimeMillis());
        }
    }

    public static void main(String[] args) {
        try {
            ThreadUserReturnInterrupt r = new ThreadUserReturnInterrupt();
            Thread t = new Thread(r);
            t.start();
            TimeUnit.MILLISECONDS.sleep(10);
            t.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
