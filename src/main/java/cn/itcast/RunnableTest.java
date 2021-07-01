package cn.itcast;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunnableTest {
    public static void main(String[] args) {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                //要执行的任务
                log.debug("running");
            }
        };
        //创建线程对象
        Thread t = new Thread(r,"T2");
        //启动线程
        t.start();;
    }
}
