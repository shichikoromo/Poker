package socket;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;

public class ClientDealer extends Thread {

    private Server server;
    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    public ClientDealer(Socket client, Server server) {
        this.client = client;
        this.server = server;

        try {
            in = new ObjectInputStream(client.getInputStream());
            out = new ObjectOutputStream(client.getOutputStream());
        } catch (IOException e) {

        }

    }

    public void run() {
        while (true) {
            try {
                Message message = (Message) in.readObject();

                switch (message.getTitle()) {
                    case "Player registered":
                        System.out.println("Player successfully registered");
                }

            } catch (ClassNotFoundException | IOException e) {
            }
        }
    }
}