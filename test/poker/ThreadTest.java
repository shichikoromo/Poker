package poker;

public class ThreadTest {

    public static void main(String... args) {
        System.out.println("main");

        for (int i = 0; i < 4; i++) {
            Foo foo = new Foo();
            foo.start();

            System.out.println("over");
        }

        for (int i = 0; i < 10; i++) {
            System.out.println("ThreadTest " + i);
        }
    }

}

class Foo extends Thread {

    @Override
    public void run() {
        for (int i = 0; i < 100; i++) {
            System.out.println(this.toString() + ": " + i);
        }
    }
}


