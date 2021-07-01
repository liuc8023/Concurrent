package cn.itcast;

public class Test2 {
    public static void main(String[] args) {
        MyThread2 mt=new MyThread2();
        new Thread(mt).start();
        new Thread(mt).start();


    }
    static class MyThread2 implements Runnable{
        private int ticket = 5;
        @Override
        public void run(){
            while(true){
                System.out.println("Runnable ticket = " + ticket--);
                if(ticket < 0){
                    break;
                }
            }
        }
    }
}
