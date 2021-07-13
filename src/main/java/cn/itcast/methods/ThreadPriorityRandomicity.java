package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

import java.security.SecureRandom;

/**
 * 测试线程优先级的随机性
 */
@Slf4j
public class ThreadPriorityRandomicity implements Runnable{
    @Override
    public void run() {
        long beginTime = System.currentTimeMillis();
        long addResult = 0;
        for (int i = 0; i <10; i++) {
            for (int j = 0; j < 5000; j++) {
                SecureRandom random = new SecureRandom();
                random.nextInt();
                addResult = addResult+j;
            }
        }
        long endTime = System.currentTimeMillis();
        log.info(Thread.currentThread().getName()+"线程执行时间"+(endTime-beginTime));
    }

    public static void main(String[] args) {
        for(int i=0;i<5;i++){
            ThreadPriorityRandomicity r = new ThreadPriorityRandomicity();
            Thread t = new Thread(r,"A");
            Thread t1 = new Thread(r,"B");
            t.setPriority(6);
            t1.setPriority(5);
            t.start();
            t1.start();
        }
    }
}