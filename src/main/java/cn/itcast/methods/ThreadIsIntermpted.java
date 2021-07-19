package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试this.isInterrupted()
 */
@Slf4j
public class ThreadIsIntermpted implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 10; i++) {
            log.info("i=" + (i + 1));
            if (i == 3) {
                Thread.currentThread().interrupt();
                log.info("线程{}是否已经停止 1？={}",Thread.currentThread().getName(),
                        Thread.currentThread().isInterrupted());
                log.info("线程{}是否已经停止 2？={}",Thread.currentThread().getName(),
                        Thread.currentThread().isInterrupted());
            }
        }
    }

    public static void main(String[] args) {
        ThreadIsIntermpted r = new ThreadIsIntermpted();
        Thread t = new Thread(r);
        t.start();
    }
}
