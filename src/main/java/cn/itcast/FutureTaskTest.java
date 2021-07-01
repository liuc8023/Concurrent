package cn.itcast;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Slf4j
public class FutureTaskTest {
    public static void main(String[] args) throws ExecutionException, InterruptedException {
        //创建任务对象
        FutureTask<Integer> task = new FutureTask<Integer>(new Callable<Integer>() {
            @Override
            public Integer call() throws Exception {
                log.debug("running");
                Thread.sleep(1000);
                return 1;
            }
        });
        Thread t = new Thread(task);
        //启动线程
        t.start();
        //获取task执行完毕的结果
        Integer result = task.get();
        log.info("线程返回值："+result);
    }
}
