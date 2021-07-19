package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * 测试Thread.interrupted()
 */
@Slf4j
public class ThreadInterrupted implements Runnable {

    @Override
    public void run() {
        for (int i = 0; i < 50000; i++) {
            log.info("i=" + (i + 1));
        }
    }

    public static void main(String[] args) {
        ThreadInterrupted r = new ThreadInterrupted();
        Thread t = new Thread(r);
        t.start();
        try {
            TimeUnit.SECONDS.sleep(1);
            log.info("给线程{}打终止标志",t.getName());
            t.interrupt();
            log.info("线程{}是否已经停止 1？={}",t.getName(),Thread.interrupted());
            log.info("线程{}是否已经停止 2？={}",t.getName(),Thread.interrupted());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info("end!");
    }
}