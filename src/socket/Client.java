package socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.Scanner;

public class Client extends Thread {

    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Client() {

        Scanner scanner = new Scanner(System.in);

        try {
            client = new Socket("127.0.0.1", 3333);
            in = new ObjectInputStream(client.getInputStream());
            out = new ObjectOutputStream(client.getOutputStream());

            System.out.println("Please enter your name:");
            String name = scanner.next();

            System.out.println("How much money do you have?:");
            int money = scanner.nextInt();

            System.out.print("Player " + name + " has ¥ " + money);

            Player player = new Player(name, money);
            Message message = new Message("Player registered", player);
            writeMessage(message);

        } catch (IOException e) {

        }
    }

    public static void main(String args[]) throws IOException {
        new Client();
    }

    private void writeMessage(Message message) {
        try {
            out.writeObject(message);
            out.flush();
        } catch (IOException e) {
        }
    }

    @Override
    public void run() {
        while (true) {
            try {
                Message message = (Message) in.readObject();

                switch (message.getTitle()) {
                    case "":
                        break;

                }
            } catch (ClassNotFoundException | IOException e) {

            }
        }
    }

    class Player {
        public String name;
        public int money;

        public Player(String name, int money) {
            this.name = name;
            this.money = money;
        }

        public String getName() {
            return name;
        }

    }
}
