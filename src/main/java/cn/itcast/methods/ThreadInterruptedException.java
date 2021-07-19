package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试异常法暂停线程
 */
@Slf4j
public class ThreadInterruptedException implements Runnable{
    @Override
    public void run() {
        try {
            for (int i = 0; i < 50000; i++) {
                //判断当前线程是否已经停止
                if (Thread.currentThread().isInterrupted()) {
                    log.info("已经是停止状态了！我要退出了！");
                    throw new InterruptedException();
                }
                log.info("i = "+(i+1));
            }
            log.info("我还是会执行的");
        } catch (InterruptedException e) {
            log.info("ThreadInterruptedException类run方法catch到的异常");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ThreadInterruptedException r = new ThreadInterruptedException();
            Thread t = new Thread(r);
            t.start();
            Thread.sleep(100);
            t.interrupt();
        } catch (InterruptedException e) {
            log.info("main catch");
            e.printStackTrace();
        }
        log.info("end");
    }
}
