package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @description 测试suspend的调用导致println()函数的独占的问题
 * @author liuc
 * @date 2021/7/21 14:53
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadSuspendPrintln implements Runnable{
    private int i = 0;
    @Override
    public void run() {
        while (true) {
            i++;
            System.out.println(i);
        }
    }

    public static void main(String[] args) {
        try {
            ThreadSuspendPrintln r = new ThreadSuspendPrintln();
            Thread t = new Thread(r);
            t.start();
            TimeUnit.MILLISECONDS.sleep(50);
            t.suspend();
            log.info("main end");
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
