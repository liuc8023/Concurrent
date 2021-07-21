package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * @description 测试stop方法暴力停止线程
 * @author liuc
 * @date 2021/7/20 15:25
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadStop implements Runnable{
    private int i = 0;
    @Override
    public void run() {
        try {
            while (true){
                i++;
                log.info("i="+i);
                TimeUnit.SECONDS.sleep(1);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        try {
            ThreadStop r = new ThreadStop();
            Thread t = new Thread(r);
            t.start();
            TimeUnit.SECONDS.sleep(8);
            t.stop();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
