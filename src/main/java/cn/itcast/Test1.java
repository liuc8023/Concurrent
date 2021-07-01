package cn.itcast;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Test1 {

    public static void main(String[] args) {
        //创建线程对象
        Thread t = new Thread("t1") {
            @Override
            public void run() {
                //要执行的任务
                log.info("running");
            }
        };
        //启动线程
        t.start();
        log.info("running");
    }
}
