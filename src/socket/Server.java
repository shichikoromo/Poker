package socket;

import poker.Table;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private ServerSocket server;
    private ObjectInputStream in;
    private ObjectOutputStream out;

    private List<ClientDealer> clients = new ArrayList<>();
    private Table table = new Table();

    public Server() {

        while (!isTableFull()) {

            try {

                server = new ServerSocket(3333);
                Socket client = server.accept();
                in = new ObjectInputStream(client.getInputStream());
                out = new ObjectOutputStream(client.getOutputStream());

                ClientDealer clientDealer = new ClientDealer(client, this);

                clientDealer.start();
                clients.add(clientDealer);

            } catch (IOException e) {
            }
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }

    public Table getTable() {
        return table;
    }

    private boolean isTableFull() {

        if (table.getPlayers().size() <= 1) {
            return false;
        } else if (table.getPlayers().size() == 3) {
            return true;
        } else {
            return false;
        }
        //TODO redundant??
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
