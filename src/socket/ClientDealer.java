package socket;

import poker.GameState;
import poker.Player;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ClientDealer extends Thread {

    private static int MAX_PLAYERS = 2;

    private Server server;
    private Socket client;
    private ObjectInputStream in;
    private ObjectOutputStream out;
    private boolean loginSuccessful = false;
    private boolean gamestarted = false;
    private boolean foo = false;
    private Player user;

    public ClientDealer(Socket client, Server server) {
        this.server = server;
        this.client = client;

        Terminal.print(server.toString() + client.toString());

        try {
            out = new ObjectOutputStream(client.getOutputStream());
            in = new ObjectInputStream(client.getInputStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        Terminal.print("new ClientDealer()");
    }

    public void run() {
        Terminal.print("\n\n1.login()");
        login();
        Terminal.print("login successful");

        while (!gamestarted) {
            try {
                sleep(500);
            }catch (InterruptedException e){
                e.printStackTrace();
            }
        }
        Terminal.print("\n\n2. waitForAction()");
        waitForAction();
    }

    private void login() {
        try {
            List userData = (ArrayList) in.readObject();
            String username = userData.get(0).toString();
            char[] password = (char[]) userData.get(1);
            boolean wantToRegister = Boolean.valueOf(userData.get(2).toString());
            Terminal.print("userdata: " + userData.toString());

            user = new Player(username, password, wantToRegister, username, 100);
            Terminal.print("user: " + user.toString());

            if (!server.isTableFull()) {
                Terminal.print("\n\n 1-1.entry()");
                entry(user);

                if (loginSuccessful) {
                    user.setLoginSuccessful(true);
                    server.addLoginUsers(user);
                    Terminal.print("loginusers: " + server.getLoginUsers().toString());
                    Terminal.print("addloginUser()");
                    boolean addedPlayerToTable = server.addPlayerToTable(user) != null;
                    Terminal.print("addPlayerToTable: " + addedPlayerToTable);


                    Terminal.print("isTableFull: " + String.valueOf(server.isTableFull()));

                    if (server.isTableFull()) {
                        Terminal.print("\n\n 1-2.startGame()");
                        server.startGame();
                        gamestarted = true;
                    }

                    if (!addedPlayerToTable) {
                        out.writeObject("Table is already full!");
                        Terminal.print("Table is full ");
                    }
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
    }


    private boolean checkBeforeReadFile(File file) {
        if (file.exists()) {
            if (file.exists() && file.isFile() && file.canRead()) {
                return true;
            }
        }
        return false;
    }

    public void writeFile(String file, List<Player> users) throws IOException {
        PrintWriter pw = null;
        try {
            pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
            for (Player user : users) {
                pw.println(user.getUsername());
                pw.println(user.getPassword());
            }
        } finally {
            if (pw != null) {
                pw.close();
            }
        }
    }

    public List<Player> readFile() throws IOException {
        List<Player> list = new ArrayList<>();
        BufferedReader br = null;
        try {
            File file = new File("/Users/N/Java/Poker/src/gui/userdata.txt");
            if (checkBeforeReadFile(file)) {
                br = new BufferedReader(new FileReader(file));
                String line;
                int i = 0;
                String str[] = {null, null};
                while ((line = br.readLine()) != null) {
                    str[i % 2] = line;
                    i++;
                    if (i % 2 == 0) {
                        list.add(new Player(str[0], str[1].toCharArray()));
                    }
                }
            }
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return list;
    }

    public void addUser(String file_name, List<Player> userList, String username, char[] password) throws IOException {
        Player user = new Player(username, password);
        userList.add(user);
        writeFile(file_name, userList);
    }

    public boolean searchUser(String username, char[] password) throws IOException {
        List<Player> userList = readFile();
        boolean userExists = false;
        for (Player user : userList) {
            String un = user.getUsername();
            char[] pw = user.getPassword();
            if (Objects.equals(username, un)) {
                if (Arrays.equals(password, pw)) {
                    userExists = true;
                    break;
                }
            }
        }
        if (userExists) {
            return true;
        } else {
            return false;
        }
    }

    private void showUserList(ArrayList<Player> userList) {
        for (Player user : userList) {
            System.out.println("Username：" + user.getUsername());
            System.out.println("Password：" + user.getPassword().toString());
        }
    }

    public boolean registerUser(String username, char[] password) {
        String file_name = "/Users/N/Java/Poker/src/gui/userdata.txt";
        try {
            List<Player> list = readFile();
            addUser(file_name, list, username, password);
            System.out.println(username + ", " + password.toString() + " logged in!");
            return true;
        } catch (Exception e) {
            System.out.println(e);
            return false;
        }
    }

    private void setLoginSuccessful(boolean successful) {
        loginSuccessful = successful;
        if (successful) {
            try {
                out.writeObject(successful);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private void entry(Player user) throws IOException {
        boolean userExists = searchUser(user.getUsername(), user.getPassword());

        if (user.getWantToRegister() && !userExists) {
            boolean registrationSuccessful = registerUser(user.getUsername(), user.getPassword());
            setLoginSuccessful(registrationSuccessful);
        } else {
            setLoginSuccessful(userExists);
        }
    }


    private Object message;

    public void waitForMessage() {
        try {
            while (true) {
                if (message != null) {
                    Terminal.print("message: " + String.valueOf(message));
                    out.writeObject(message);
                    message = null;

                }
                sleep(500);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public int waitForAction() {
        int action = -1;
        Terminal.print("action: " + action);
        try {
            while (action <= 0) {
                action = Integer.getInteger(in.readObject().toString());
                Terminal.print("action: " + action);
            }
            return action;
        } catch (Exception e) {
            e.printStackTrace();
            return -1;
        }
    }

    public void sendMessage(String message) {
        this.message = message;
    }

    public void sendGameState(GameState gameState) {
        //       this.message = gameState;
        Terminal.print("    gameState: " +String.valueOf(gameState));
        try {
            out.writeObject(gameState);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}