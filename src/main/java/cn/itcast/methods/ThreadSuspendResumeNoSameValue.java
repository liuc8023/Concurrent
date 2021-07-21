package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * @description 测试suspend与resume方法的缺点，数据不同步的问题
 * @author liuc
 * @date 2021/7/21 16:47
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadSuspendResumeNoSameValue implements Runnable{
    final SuspendResumeNoSameValue obj = new SuspendResumeNoSameValue();
    @Override
    public void run() {
        if (Thread.currentThread().getName().equals("A")) {
            log.info(Thread.currentThread().getName()+"线程修改值");
            obj.setValue("hh","eeee");
        }
        if (Thread.currentThread().getName().equals("B")) {
            obj.printUserNamePassWord();
        }
    }

    public static void main(String[] args) {
        try {
            ThreadSuspendResumeNoSameValue r = new ThreadSuspendResumeNoSameValue();
            Thread t = new Thread(r,"A");
            t.start();
            TimeUnit.SECONDS.sleep(2);
            Thread t1 = new Thread(r,"B");
            t1.start();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

@Slf4j
class SuspendResumeNoSameValue{
    private String userName = "1";
    private String passWord = "11";
    public void setValue(String u,String p){
        this.userName = u;
        if (Thread.currentThread().getName().equals("A")) {
            log.info("暂停线程A");
            Thread.currentThread().suspend();
        }
        this.passWord = p;
    }
    public void printUserNamePassWord() {
        log.info("userName = {},passWord = {}",userName,passWord);
    }
}
