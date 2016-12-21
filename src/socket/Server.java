package socket;

import poker.Player;
import poker.Table;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.ServerSocket;
import java.net.Socket;

public class Server {

    public boolean _startGame;
    private Socket client;
    private ServerSocket server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Table table = new Table();

    public Server() {
        try {
            server = new ServerSocket(2533);
            serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) throws IOException {
        new Server();
    }

    public void serve() {
        try {
            while (true) {
                if (!isTableFull()) {
                    Terminal.print("Waiting for Connections...");

                    client = server.accept();
                    Terminal.print("Client accepted");

                    ClientDealer clientDealer = new ClientDealer(client, this);
                    clientDealer.start();

                } else {
                    client = server.accept();

                    out = new ObjectOutputStream(client.getOutputStream());
                    in = new ObjectInputStream(client.getInputStream());

                    out.writeObject("Table is full");
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public boolean addPlayerToTable(Player player) {

        if (!isTableFull()) {
            table.addPlayer(player);
            if (isTableFull()) {
                _startGame = true;
            }
            return true;
        } else {
            return false;
        }
    }

    public void askAction() {
        try {
            out = new ObjectOutputStream(client.getOutputStream());
            out.writeObject("Please choose an option");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Table getTable() {
        return table;
    }

    private boolean isTableFull() {
        if (table.getPlayers().size() >= 2) {
            return true;
        } else {
            return false;
        }
    }

    public void startGame() {
        Terminal.print("THE GAME BEGINS");
        table.startNextRound();
    }

}
