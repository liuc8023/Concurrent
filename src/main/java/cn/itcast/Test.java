package cn.itcast;

public class Test {
    public static void main(String[] args) {
        new MyThread().start();
        new MyThread().start();

    }

    static class MyThread extends Thread{
        private int ticket = 5;
        @Override
        public void run(){
            while(true){
                System.out.println("Thread ticket = " + ticket--);
                if(ticket < 0){
                    break;
                }
            }
        }
    }
}
