package cn.itcast;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class RunnableLambdaTest {
    public static void main(String[] args) {
        test1();
        test2();
    }

    public static void test1(){
        Runnable r = () ->{log.debug("running");};
        Thread t = new Thread(r,"test1");
        t.start();
    }

    public static void test2(){
        Thread t = new Thread(() ->{log.debug("running");},"test2");
        t.start();
    }
}
