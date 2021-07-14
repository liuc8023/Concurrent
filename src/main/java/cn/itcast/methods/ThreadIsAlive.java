package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试isAlive方法
 */
@Slf4j
public class ThreadIsAlive implements Runnable{
    @Override
    public void run() {
        log.info(Thread.currentThread().getName()+"线程运行时，线程是否是活着的？"+Thread.currentThread().isAlive());
    }

    public static void main(String[] args) {
        ThreadIsAlive r = new ThreadIsAlive();
        Thread t = new Thread(r);
        log.info(t.getName()+"线程启动前，线程是否是活着的？"+t.isAlive());
        t.start();
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        log.info(t.getName()+"线程执行完成之后，线程是否是活着的？"+t.isAlive());
    }
}
