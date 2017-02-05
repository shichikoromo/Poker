package socket;

import gui.LoginForm;
import gui.PokerFrame;
import poker.GameState;
import poker.Player;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;


public class Client extends Thread {

    private Socket server;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private Scanner scanner = new Scanner(System.in);
    private LoginForm loginForm;
    private PokerFrame pokerFrame;
    private GameState gameState;
    private Player user;
    private Object lock = new Object();


    public Client() {
        loginForm = new LoginForm(this);
        Terminal.print("1. LoginForm: " + loginForm.toString());
        try {
            System.out.println("Trying to connect...");
            server = new Socket("localhost", 2533);
            out = new ObjectOutputStream(server.getOutputStream());
            in = new ObjectInputStream(server.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void main(String args[]) {
        new Client().start();
    }

    @Override
    public void run() {
        Terminal.print("\n\n 2. login()");
        login();
        Terminal.print("Login successful");
        loginForm.setLoginMessage();
        while (gameState == null) {
            Terminal.print(String.valueOf(gameState));
            getGameState();
        }
        Terminal.print(String.valueOf(gameState));
        loginForm.end();
        pokerFrame = new PokerFrame(user, gameState);

        Terminal.print("\n\n 3. prepare()");
        prepare();
        Terminal.print("prepare completed");
        pokerFrame.setVisible(true);

        Terminal.print("\n\n 4. preFlop()");
        Terminal.print("isPhaseEnd: " + gameState.isPhaseEnd);
        Terminal.print("currentPlayer: " + gameState.currentPlayer);
        while (!gameState.isPhaseEnd) {
            Terminal.print("setCurrentPlayerLabel()");
            pokerFrame.setCurrentPlayerLabel(gameState.currentPlayer);
            betPhase();
        }

        Terminal.print("\n\n 5.flop()");
    }

    private Player login() {
        try {
            Terminal.print("LoginForm readyToStart: " + String.valueOf(loginForm.readyToSend));
            while (!loginForm.readyToSend) {
                Thread.sleep(500);
            }
            user = loginForm.getCurrentUser();
            List<Object> message = new ArrayList();
            message.add(user.getUsername());
            message.add(user.getPassword());
            message.add(user.getWantToRegister());

            out.writeObject(message);
            Terminal.print("out.writeObject: " + message.toString());

            Boolean foo = null;
            while (foo == null) {
                foo = Boolean.valueOf(in.readObject().toString());
                Thread.sleep(500);
            }
            user.setLoginSuccessful(foo);
            return foo ? user : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    private synchronized void prepare() {
        Terminal.print("user: " + user);
        Terminal.print("gameState: " + gameState.round +".Round, "+gameState.cardPhase+", dealer "+gameState.dealer.getUsername()+", maxstake "+gameState.maxStake+", mainpot "+getSumOfMainPot(gameState.mainPot));
        pokerFrame.setRoundLabel(gameState.round);
        pokerFrame.setPhaseLabel(String.valueOf(gameState.cardPhase));
        pokerFrame.setDealerOrAllInIcon(gameState.dealer, false);
        pokerFrame.setPotOrBetLabels(gameState.maxStake, false);
        pokerFrame.setPotOrBetLabels(getSumOfMainPot(gameState.mainPot), true);

        for (Player player : gameState.players) {
            pokerFrame.setNameOrMoney(player, false, false);
            pokerFrame.setNameOrMoney(player, true, false);
            pokerFrame.setHandcardsPic(player, player.getHandCards());
        }
        pokerFrame.setPlayerVisiblity(user);

    }

    private int getSumOfMainPot(List<Integer> mainpot) {
        Terminal.print("mainpot: "+ gameState.mainPot.toString());
        int sumOfPot = 0;
        for (int i : mainpot) {
            sumOfPot += i;
        }
        Terminal.print("SumOfPot: " + String.valueOf(sumOfPot));
        return sumOfPot;
    }

    private void betPhase() {
        Terminal.print("betPhase()");
        getGameState();
        Terminal.print("gameState: " + gameState.toString());
        gameState.tryAction = -1;
        while (!gameState.currentPlayer.getActionSuccessful()) {
            pokerFrame.setCurrentPlayerLabel(gameState.currentPlayer);
            if (gameState.tryAction == 2) {
                Terminal.print("sendAction(bet)");
                sendAction(true);
            } else {
                Terminal.print("sendAction()");
                sendAction(false);
            }
        }
    }


    private void flop() {
        pokerFrame.setTablecardsPic(1);
    }

    private void turn() {
        pokerFrame.setTablecardsPic(2);
    }

    private void river() {
        pokerFrame.setTablecardsPic(3);
    }


    private GameState getGameState() {
        boolean foo = false;
        Terminal.print(String.valueOf(gameState));
        while (!foo) {
            try {
                gameState = (GameState) in.readObject();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Terminal.print(gameState.toString());
            foo = true;
            return gameState;
        }
        return null;
    }

    private Player sendAction(boolean bet) {
        try {
            while (gameState.tryAction < 0) {
                Thread.sleep(500);
                out.writeObject(gameState);
            }

            Boolean foo = null;
            while (foo == null) {
                foo = Boolean.valueOf(in.readObject().toString());
                Thread.sleep(500);
            }
            user.setActionSuccessful(foo);
            return foo ? user : null;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }

    }

    private void fold() {
        //  pokerFrame.isStrikeThrough();
    }
}
