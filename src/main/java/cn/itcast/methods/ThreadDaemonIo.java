package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * 完成文件输出的守护线程任务
 */
@Slf4j
public class ThreadDaemonIo implements Runnable{

    @Override
    public void run() {
        try {
            //守护线程阻塞1秒后运行
            Thread.sleep(1000);
            File f=new File("daemon.txt");
            FileOutputStream os = new FileOutputStream(f,true);
            os.write("daemon".getBytes());
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
        ThreadDaemonIo r = new ThreadDaemonIo();
        Thread t = new Thread(r);
        //设置守护线程
        t.setDaemon(true);
        t.start();
    }
}
