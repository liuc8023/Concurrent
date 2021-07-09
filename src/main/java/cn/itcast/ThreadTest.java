package cn.itcast;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadTest {
    public static void main(String[] args) {
        Thread t = new Thread(){
            @Override
            public void run(){
                sougo();
            }
        };
        t.start();
        log.info("Hello:"+Thread.currentThread().getName()+"线程");
    }
    static synchronized void sougo(){
        log.info("Sougo:"+Thread.currentThread().getName()+"线程");
    }
}
