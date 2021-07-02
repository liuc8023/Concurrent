package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadJoinTest implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            log.info(""+i);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        log.info(Thread.currentThread().getName()+" start");
        Runnable r = new ThreadJoinTest();
        Thread t = new Thread(r,"线程一");
        Runnable r1 = new ThreadJoinTest();
        Thread t1 = new Thread(r1,"线程二");
        Runnable r2 = new ThreadJoinTest();
        Thread t2 = new Thread(r2,"线程三");
        log.info("t start");
        t.start();
        log.info("t end");
        log.info("t1 start");
        t1.start();
        log.info("t1 end");
        t.join();
        log.info("t2 start");
        t2.start();
        log.info("t2 end");
        log.info(Thread.currentThread().getName()+" end");
    }
}
