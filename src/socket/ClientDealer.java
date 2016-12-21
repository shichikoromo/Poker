package socket;

import poker.Player;

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
        this.server = server;
        this.client = client;

        try {
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        try {
            String name = in.readObject().toString();
            int money = Integer.parseInt(in.readObject().toString());

            Player player = new Player(name, money, in, out);
            boolean canAddPlayerToTable = server.addPlayerToTable(player);
            if (canAddPlayerToTable) {
                Terminal.print("Player " + name + " (¥ " + money + ") has successfully registered" + "\n");
                out.writeObject("Welcome " + name + " !");
                if (server._startGame) {
                    server.startGame();
                    server._startGame = false;
                }
            } else {
                out.writeObject("Table is already full !");
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}