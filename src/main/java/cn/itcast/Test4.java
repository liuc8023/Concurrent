package cn.itcast;

public class Test4 extends Thread {

    private int ticket = 1000000000;
    @Override
    public void run(){
        for(int i =0;i<1000000000;i++){
            synchronized (this){
                if(this.ticket>0){
                    try {
                        Thread.sleep(500);
                        System.out.println(Thread.currentThread().getName()+"卖票---->"+(this.ticket--));
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    public static void main(String[] arg){
        Test4 t1 = new Test4();
        new Thread(t1,"线程1").start();
        new Thread(t1,"线程2").start();
    }
}
