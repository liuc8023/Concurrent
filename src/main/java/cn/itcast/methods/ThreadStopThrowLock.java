package cn.itcast.methods;

import lombok.extern.slf4j.Slf4j;
import java.util.concurrent.TimeUnit;

/**
 * @description 测试stop()方法释放锁的不良后果
 * @author liuc
 * @date 2021/7/20 20:19
 * @since JDK1.8
 * @version V1.0
 */
@Slf4j
public class ThreadStopThrowLock implements Runnable{
    private User user;
    public ThreadStopThrowLock (User user){
        this.user = user;
    }
    @Override
    public void run() {
        user.printString("b","bb");
    }

    public static void main(String[] args) {
        try {
            User u = new User();
            ThreadStopThrowLock r = new ThreadStopThrowLock(u);
            Thread t = new Thread(r);
            t.start();
            TimeUnit.MILLISECONDS.sleep(500);
            t.stop();
            log.info(u.getUserName()+","+u.getPassword());
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}

class User {
    private String userName ="a";
    private String password = "aa";

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    synchronized public void printString(String userName,String password){
        try {
            this.userName = userName;
            TimeUnit.MINUTES.sleep(1);
            this.password = password;
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}
