package c_001_03_Ordering;

import java.io.IOException;

public class T03_ThisEscape {
    private int num = 8;
    public T03_ThisEscape(){
        new Thread(() -> System.out.println(this.num)).start();
    }

    public static void main(String[] args) throws IOException {
        new T03_ThisEscape();
        System.in.read();
    }
}
