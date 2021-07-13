package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadDaemon implements Runnable{
    @Override
    public void run() {
        log.info("running...");
    }

    public static void main(String[] args) {
        //isDaemon 判断线程是否为守护线程，如果返回true，表示该线程为守护线程，否则为用户线程
        log.info(Thread.currentThread().getName()+"线程是否为守护线程："+Thread.currentThread().isDaemon());
        ThreadDaemon r = new ThreadDaemon();
        Thread t = new Thread(r);
        log.info(t.getName()+"线程是否为守护线程："+t.isDaemon());
        t.start();
        t.setDaemon(true);
        log.info(t.getName()+"线程是否为守护线程："+t.isDaemon());

    }
}
