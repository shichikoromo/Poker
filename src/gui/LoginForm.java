package gui;

import poker.Player;
import socket.Client;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class LoginForm extends Thread {
    private JFrame frame = new JFrame("Register/Login");
    private JPanel mainPanel;
    private JPanel namePanel;
    private JPanel passwordPanel;
    private JPanel buttonPanel;
    private JLabel titleLabel;
    private JLabel nameLabel;
    private JLabel passwordLabel;
    private JTextField nameField;
    private JPasswordField passwordField;
    private JButton registrationButton;
    private JButton loginButton;
    private JLabel alert;
    private Client client;
    private Player currentUser;
    public boolean readyToSend = false;

    public LoginForm(Client cl) {
        frame.setLocationRelativeTo(null);
        Container contentpane = frame.getContentPane();
        contentpane.add(mainPanel);
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);

        client = cl;

        initListeners();
    }

    private void initListeners() {
        registrationButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryLoginOrRegister(true);
            }
        });

        loginButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                tryLoginOrRegister(false);
            }
        });

        passwordField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                    tryLoginOrRegister(false);
                }
            }
        });
    }

    private void tryLoginOrRegister(boolean register) {

        String username = getUsername();
        char[] password = getPassword();

        if (checkInputs(username, password)) {
            Player user = new Player(username, password);
            setCurrentUser(user);

            if (register) {
                user.setWantToRegister(true);
                alert.setText("try to register...");
            } else {
                alert.setText("try to login...");
            }

            readyToSend = true;
        }
    }

    private String getUsername() {
        return nameField.getText();
    }

    private char[] getPassword() {
        return passwordField.getPassword();
    }

    private void setCurrentUser(Player user) {
        currentUser = user;
    }

    public Player getCurrentUser() {
        return currentUser;
    }

    private boolean checkInputs(String username, char[] password) {
        if (username.equals("")) {
            alert.setText("Username invalid");
            return false;
        } else if (password.length == 0) {
            alert.setText("Password invalid");
            return false;
        } else {
            return true;
        }
    }

    public void setLoginMessage() {
        if (currentUser.isLoginSuccessful()) {
            alert.setText("waiting for another players...");
            System.out.println(currentUser.getUsername() + ", " + currentUser.getPassword().toString() + " logged in");
        } else {
            alert.setText("registration/login failed");
        }
    }

    public void end() {
        frame.setVisible(false);
    }
}
