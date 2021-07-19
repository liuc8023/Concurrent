package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试interrupt
 */
@Slf4j
public class ThreadInterrupt implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 30000; i++) {
            log.info("i="+(i+1));
        }
    }

    public static void main(String[] args) {
        try {
            ThreadInterrupt r = new ThreadInterrupt();
            Thread t = new Thread(r);
            //启动线程
            t.start();
            //延时50毫秒
            Thread.sleep(50);
            //停止线程
            t.interrupt();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
