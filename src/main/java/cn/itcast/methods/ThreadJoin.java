package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadJoin implements Runnable{
    @Override
    public void run() {
        for (int i = 0; i < 5; i++) {
            log.info(""+i);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        log.info(Thread.currentThread().getName()+"线程启动了");
        Runnable r = new ThreadJoin();
        Thread t = new Thread(r,"线程一");
        Runnable r1 = new ThreadJoin();
        Thread t1 = new Thread(r1,"线程二");
        t.start();
        /**
         * join的意思是使得放弃当前线程的执行，并返回对应的线程，例如下面代码的意思就是：
         * 程序在main线程中调用t线程的join方法，则main线程放弃cpu控制权，并返回t线程继续执行直到线程t执行完毕
         * 所以结果是t线程执行完后，才到主线程执行，相当于在main线程中同步t线程，t执行完了，main线程才有执行的机会
         */
        t.join();
        t1.start();
        t1.join();
        log.info(Thread.currentThread().getName()+"结束了");
    }
}
