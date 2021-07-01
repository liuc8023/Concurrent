package cn.itcast.methods;

/**
 * 线程休眠方法
 */
public class ThreadSleep implements Runnable {

    @Override
    public void run() {
        // 循环打印
        for (int i = 0; i < 100; i++) {
            // 线程休眠
            try {
                Thread.sleep(i*10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            // 打印内容
            System.out.println(Thread.currentThread().getId()+"======threadSleep======"+i);
        }
    }

    public static void main(String[] args) {
        /**
         * 线程常用方法 sleep()
         * 作用是让当前线程停止执行，把cpu让给其他线程执行，但不会释放对象锁和监控的状态，到了指定时间后线程又会自动恢复运行状态。
         *
         * 注意：线程睡眠到期自动苏醒，并返回到可运行状态，不是运行状态。sleep()中指定的时间是线程不会运行的最短时间。
         * 因此，sleep()方法不能保证该线程睡眠到期后就开始执行
         */
        Runnable r = new ThreadSleep();
        Thread t = new Thread(r);
        t.start();

        Runnable r1 = new ThreadSleep();
        Thread t1 = new Thread(r1);
        t1.start();
    }
}
