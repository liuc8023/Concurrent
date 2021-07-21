package cn.itcast.methods;
import lombok.extern.slf4j.Slf4j;

/**
 * @description 测试stop()与java.lang.ThreadDeath异常的方式停止线程
 * @author liuc
 * @date 2021/7/20 19:47
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadStopThreadDeath implements Runnable{
    @Override
    public void run() {
        try{
            Thread.currentThread().stop();
        } catch (ThreadDeath e) {
            log.info("进入catch！");
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ThreadStopThreadDeath r = new ThreadStopThreadDeath();
        Thread t = new Thread(r);
        t.start();
    }
}
