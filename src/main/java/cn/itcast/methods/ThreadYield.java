package cn.itcast.methods;

/**
 * 线程让行方法
 */
public class ThreadYield implements Runnable{
    @Override
    public void run() {
        // 循环打印
        for (int i = 0 ; i < 20 ; i++ ){
            // 线程让行
            Thread.yield();
            // 打印内容
            System.out.println(Thread.currentThread().getName()+"======threadYield======"+i);
        }
    }
    /**
     * 作用：暂停当前正在执行的线程对象（及放弃当前拥有的cup资源），并执行其他线程。yield()做的是让当前运行线程回到可运行状态，
     *      以允许具有相同优先级的其他线程获得运行机会。
     * 线程状态变化：运行 --》 就绪
     * 注意：使用yield()的目的是让相同优先级的线程之间能适当的轮转执行。
     *      但是，实际中无法保证yield()达到让步目的，因为让步的线程还有可能被线程调度程序再次选中。
     */
    public static void main(String[] args) {
        Runnable r = new ThreadYield();
        Thread t = new Thread(r,"线程一");
        t.start();
        Runnable r1 = new ThreadYield();
        Thread t1 = new Thread(r1,"线程二");
        t1.start();
    }
}
