package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

/**
 * 测试让main线程终止
 */
@Slf4j
public class ThreadInterrupted2{

    public static void main(String[] args) {
        log.info("给线程{}打终止标志",Thread.currentThread().getName());
        Thread.currentThread().interrupt();
        log.info("线程{}是否已经停止 1？={}",Thread.currentThread().getName(),Thread.interrupted());
        log.info("线程{}是否已经停止 2？={}",Thread.currentThread().getName(),Thread.interrupted());
        log.info("end!");
    }
}