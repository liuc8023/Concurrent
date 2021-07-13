package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试线程优先级的继承性
 */
@Slf4j
public class ThreadPriorityExtends implements Runnable{
    @Override
    public void run() {
        ThreadPriorityExtends1 r1 = new ThreadPriorityExtends1();
        Thread t1 = new Thread(r1,"B");
        log.info(t1.getName()+"线程的优先级是:"+Thread.currentThread().getPriority());
    }
}

class ThreadPriorityExtends1 extends ThreadPriorityExtends{

}

@Slf4j
class Test{
    public static void main(String[] args) {
        log.info(Thread.currentThread().getName()+":"+Thread.currentThread().getPriority());
        ThreadPriorityExtends r = new ThreadPriorityExtends();
        Thread t = new Thread(r,"A");
        log.info(t.getName()+"修改线程优先级之前的优先级是:"+t.getPriority());
        t.setPriority(Thread.MAX_PRIORITY);
        log.info(t.getName()+"修改线程优先级之后的优先级是:"+t.getPriority());
        t.start();
    }
}
