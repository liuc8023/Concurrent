package cn.itcast.n3;

public class frameTest {

    public static void main(String[] args) {
        //线程t1
        Thread t1 = new Thread(new Runnable() {
            @Override
            public void run() {
                method1(20);
            }
        });
        t1.start();
        //主线程
        method1(10);
    }

    static void method1(int x) {
        int y = x + 1;
        Object m = method2();
        System.out.printf(m.toString());
    }

    static Object method2() {
        Object n = new Object();
        return n;
    }
}
