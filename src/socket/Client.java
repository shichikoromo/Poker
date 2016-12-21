package socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {

    private Socket server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Scanner scanner = new Scanner(System.in);

    public Client() {

        try {

            System.out.println("Please enter your name:");
            String name = scanner.next();
            System.out.println("How much money do you have?:");
            Integer money = scanner.nextInt();

            Terminal.print("Player " + name + " has ¥" + money + "\n");

            System.out.println("Trying to connnect...");
            server = new Socket("localhost", 2533);

            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());

            out.writeObject(name);
            out.writeObject(money);

        } catch (Exception e) {
            System.out.println("Exception " + e.getMessage());
        }
    }

    public static void main(String args[]) throws IOException {
        new Client().start();
    }

    @Override
    public void run() {

        try {

            while (true) {
                String message = in.readObject().toString();

                if (message.contains("QUESTION:")) {
                    Terminal.printQuestion(message);
                    out.writeObject(scanner.next());
                } else {
                    Terminal.print(message);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }

    }
}
