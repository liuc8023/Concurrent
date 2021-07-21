package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

/**
 * @description 测试suspend与resume方法
 * @author liuc
 * @date 2021/7/21 9:36
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadSuspendResume implements Runnable{
    private int i = 0;
    @Override
    public void run() {
        try {
            while (true) {
                i++;
                TimeUnit.SECONDS.sleep(1);
                log.info("i="+i);
                //当i等于10的时候，使用异常法停止线程
                while (i == 10) {
                    //使用异常法停止线程
                    throw new InterruptedException();
                }
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ThreadSuspendResume r = new ThreadSuspendResume();
            Thread t = new Thread(r);
            t.start();
            TimeUnit.SECONDS.sleep(5);
            t.suspend();
            log.info("suspend time1:"+System.currentTimeMillis());
            TimeUnit.SECONDS.sleep(5);
            t.resume();
            log.info("resume time2:"+System.currentTimeMillis());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
