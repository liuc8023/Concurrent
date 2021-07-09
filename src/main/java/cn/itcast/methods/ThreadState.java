package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

@Slf4j
public class ThreadState {
    public static void main(String[] args) throws InterruptedException {
        ThreadState threadState=new ThreadState();
        Thread thread=new Thread(()->{
            synchronized (threadState){
                try {
                    for(int i=0;i<10;i++) {
                        if(i==0){
                            Thread.sleep(1000);
                        }else if(i==1){
                            threadState.wait();
                        }else if(i==2){
                            System.out.println("我复活了");
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        });
        Thread thread2=new Thread(()->{
            synchronized (threadState){
                threadState.notify();
            }
        });
        log.info("新建状态："+thread.getState());
        thread.start();
        log.info("等待运行状态："+thread.getState());
        Thread.sleep(500);
        log.info("计时等待状态："+thread.getState());
        Thread.sleep(1500);
        log.info("等待状态："+thread.getState());
        thread2.start();
        Thread.sleep(1000);
        log.info("终止状态："+thread.getState());
    }
}
