package socket;

import gui.LoginForm;
import gui.PokerFrame;
import poker.GameState;
import poker.Player;
import poker.Table;

import javax.imageio.IIOException;
import java.io.*;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

public class Server implements Messenger {

    private static int MAX_PLAYERS = 2;

    private Socket client;
    private ServerSocket server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Table table;
    private Player user;
    private LoginForm loginForm;
    private PokerFrame pokerFrame;
    private List<String> loginUsers = new ArrayList<>();
    private List<ClientDealer> clientDealers = new ArrayList<>();

    public Server() {
        table = new Table(this);

        try {
            server = new ServerSocket(2533);
            serve();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String[] args) {
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
                    clientDealers.add(clientDealer);
                    Terminal.print(clientDealers.toString());

                } else {
                    client = server.accept();

                    out = new ObjectOutputStream(client.getOutputStream());
                    in = new ObjectInputStream(client.getInputStream());

                    out.writeObject("Table is full");
                    Terminal.print("Table is full ");
                }
            }
        } catch (Exception e) {
            System.err.println(e.getMessage());
            e.printStackTrace(System.err);
        }
    }

    public void addLoginUsers(Player player) {
        loginUsers.add(player.getUsername());
    }

    public List<String> getLoginUsers() {
        return loginUsers;
    }

    public List<String> addPlayerToTable(Player player) {
        if (!isTableFull()) {
            table.addPlayer(player);
            return loginUsers;
        } else {
            return null;
        }
    }

    public Table getTable() {
        return table;
    }

    public boolean isTableFull() {
        if (table.getPlayers().size() >= MAX_PLAYERS) {
            return true;
        } else {
            return false;
        }
    }

    public void startGame() {
        Terminal.print("THE GAME BEGINS");
        table.startNextRound();
    }

    public int waitForAction(Player player) {
//        Terminal.print("wait for action");
//        for (ClientDealer clientDealer : clientDealers) {
//             clientDealer.waitForAction();
//        }
        return 0;
    }

    public void sendMessage(String message) {
        Terminal.print("send");
        for (ClientDealer clientDealer : clientDealers) {
            clientDealer.sendMessage(message);
        }
    }

    public void synchro(GameState gameState) {
        Terminal.print("Synchronize");
//        Terminal.print(clientDealers.toString());
//        Terminal.print(gameState.players.size()+gameState.players.toString());

        for (ClientDealer clientDealer : clientDealers) {
            clientDealer.sendGameState(gameState);
        }
    }
}
