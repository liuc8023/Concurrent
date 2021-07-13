package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadWait implements Runnable{
    int sum = 0;
    @Override
    public void run() {
        log.info(Thread.currentThread().getName()+"线程启动时间："+System.currentTimeMillis());
//        synchronized (this) {
            for (int i = 0; i < 100; i++) {
                sum += i;
            }
            log.info(Thread.currentThread().getName()+":"+sum);
            //默认隐式自动通知持有当前对象的被阻塞的线程解锁
//            this.notify();
//        }
        log.info(Thread.currentThread().getName()+"线程结束时间："+System.currentTimeMillis());
    }

    public int getSum() {
        return sum;
    }

    public static void main(String[] args) {
        int a = 10;
        ThreadWait r = new ThreadWait();
        Thread t = new Thread(r);
        t.start();

        synchronized (t) {
            log.info(t.getName());
            try {
                t.wait();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        log.info(Thread.currentThread().getName()+":"+System.currentTimeMillis());
        log.info(Thread.currentThread().getName()+":"+(r.getSum()+a));
    }
}
