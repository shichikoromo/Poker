package gui;

import handChecker.PokerCard;
import poker.Card;
import poker.GameState;
import poker.Player;
import poker.Table;
import socket.Terminal;

import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.TreeMap;

public class PokerFrame extends Thread {
    private JFrame frame;
    private JPanel mainPanel;
    private JPanel overPanel;
    private JPanel middlePanel;
    private JPanel middlePanel02;
    private JPanel cardsPanel;
    private JPanel buttonPanel;
    private JPanel underPanel;
    private JPanel playerPanel01;
    private JPanel playerPanel02;
    private JPanel playerPanel03;
    private JPanel playerPanel04;
    private JButton foldButton;
    private JButton callCheckButton;
    private JButton betRaiseButton;
    private JButton allInButton;
    private JLabel playerName01;
    private JLabel playerName02;
    private JLabel playerName03;
    private JLabel playerName04;
    private JLabel moneyLabel01;
    private JLabel moneyLabel02;
    private JLabel moneyLabel04;
    private JLabel moneyLabel03;
    private JLabel actionLabel01;
    private JLabel actionLabel02;
    private JLabel actionLabel04;
    private JLabel actionLabel03;
    private JLabel dealerLabel01;
    private JLabel dealerLabel02;
    private JLabel dealerLabel03;
    private JLabel dealerLabel04;
    private JLabel allInLabel01;
    private JLabel allInLabel02;
    private JLabel allInLabel03;
    private JLabel allInLabel04;
    private JLabel playerCards01_01;
    private JLabel playerCards01_02;
    private JLabel playerCards02_01;
    private JLabel playerCards02_02;
    private JLabel playerCards03_01;
    private JLabel playerCards03_02;
    private JLabel playerCards04_01;
    private JLabel playerCards04_02;
    private JLabel tableCard01;
    private JLabel tableCard02;
    private JLabel tableCard03;
    private JLabel tableCard04;
    private JLabel tableCard05;
    private JLabel leer;
    private JLabel roundLabel;
    private JLabel phaseLabel;
    private JLabel potLabel;
    private JLabel betLabel;
    private JLabel winnerLabel01;
    private JLabel winnerLabel02;
    private JLabel winnerLabel03;
    private JLabel winnerLabel04;
    private JTextField howMuchField;
    private JPanel waitingPanel;
    private JPanel gamePanel;
    private JLabel currentPlayerLabel01;
    private JLabel currentPlayerLabel02;
    private JLabel currentPlayerLabel03;
    private JLabel currentPlayerLabel04;
    private JTextArea alert;
    private boolean readyToPlay = false;
    private Player player;
    private GameState gameState;
    private Table table;

    private ImageIcon dealerIcon;
    private ImageIcon allInIcon;

    public int action;

    public PokerFrame(Player player, GameState gameState) {
        this.player = player;
        this.gameState = gameState;

        String frameName = "Poker - Player " + player.getUsername();
        frame = new JFrame(frameName);
        Container contentpane = frame.getContentPane();
        frame.getContentPane().setPreferredSize(new Dimension(720, 450));
        contentpane.add(mainPanel);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        setGame();
        initListeners();
    }

    private void initListeners() { //TODO SENTAKU HA ITIDO NOMI & ACTION MINA NI HYOUJI(CLIENT KARA?)
        foldButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (player.getUsername().equals(gameState.currentPlayer.getUsername())) {
                    gameState.tryAction = 0;
                    for (Player pl : gameState.players) {
                        setActionLabel(player, gameState.tryAction);
                    }
                }
            }
        });

        callCheckButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (player.getUsername().equals(gameState.currentPlayer.getUsername())) {
                    gameState.tryAction = 1;
                    for (Player pl : gameState.players) {
                        setActionLabel(player, gameState.tryAction);
                    }
                }
            }
        });

        betRaiseButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (player.getUsername().equals(gameState.currentPlayer.getUsername())) {
                    gameState.tryAction = 2;
                    for (Player pl : gameState.players) {
                        setActionLabel(player, gameState.tryAction);
                    }
                    howMuchField.setText("  How much?  ");
                    int i = 3;
                    alert.setText("tries remain: " + i);
                }
            }
        });

        allInButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                if (player.getUsername().equals(gameState.currentPlayer.getUsername())) {
                    gameState.tryAction = 3;
                    setActionLabel(player, gameState.tryAction);
                    setDealerOrAllInIcon(player, true);


                }
            }
        });

        howMuchField.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                super.mouseClicked(e);
                howMuchField.setText("");
            }
        });

        howMuchField.addKeyListener(new KeyAdapter() {
            @Override
            public void keyReleased(KeyEvent e) {
                if (KeyEvent.VK_ENTER == e.getKeyCode()) {
                    int stake = Integer.getInteger(howMuchField.getText());
                    if (stake < 0 && stake > 3) {
                        alert.setText("input invalid");
                    } else {
                        gameState.tryStake = stake;
                    }
                }
                howMuchField.setText("               ");
            }
        });

    }

    public static void main(String[] args) {
        new PokerFrame(null, null).setVisible(true);
    }

    public void setVisible(boolean foo) {
        frame.setVisible(foo);
    }

    private void setGame() {
        ImageIcon nullIcon = getCardPic(null);
        ImageIcon emptyIcon = new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/null.png");
        playerCards01_01.setIcon(nullIcon);
        playerCards01_02.setIcon(nullIcon);
        playerCards02_01.setIcon(nullIcon);
        playerCards02_02.setIcon(nullIcon);
        playerCards04_01.setIcon(nullIcon);
        playerCards04_02.setIcon(nullIcon);
        playerCards03_01.setIcon(nullIcon);
        playerCards03_02.setIcon(nullIcon);

        tableCard01.setIcon(emptyIcon);
        tableCard02.setIcon(emptyIcon);
        tableCard03.setIcon(emptyIcon);
        tableCard04.setIcon(emptyIcon);
        tableCard05.setIcon(emptyIcon);

        dealerIcon = new ImageIcon("/Users/N/Java/Poker/src/gui/img/dealerMarker.png");
        dealerLabel01.setIcon(dealerIcon);
        dealerLabel02.setIcon(dealerIcon);
        dealerLabel04.setIcon(dealerIcon);
        dealerLabel03.setIcon(dealerIcon);
        dealerLabel01.setVisible(false);
        dealerLabel02.setVisible(false);
        dealerLabel04.setVisible(false);
        dealerLabel03.setVisible(false);

        allInIcon = new ImageIcon("/Users/N/Java/Poker/src/gui/img/allInMarker.png");
        allInLabel01.setIcon(allInIcon);
        allInLabel02.setIcon(allInIcon);
        allInLabel04.setIcon(allInIcon);
        allInLabel03.setIcon(allInIcon);
        allInLabel01.setVisible(false);
        allInLabel02.setVisible(false);
        allInLabel04.setVisible(false);
        allInLabel03.setVisible(false);

        howMuchField.setText("                ");
    }

    public void setRoundLabel(int round) {
        String roundText = round + ". Round";
        roundLabel.setText(roundText);
    }

    public void setPhaseLabel(String phase) {
        phaseLabel.setText(phase);
    }

    public void setNameOrMoney(Player player, boolean ismoney, boolean fold) {
        String name = "Player " + player.getUsername();
        String money = "(" + player.getMoney() + ")";
        int index = getPlayerIndex(player);
        if (ismoney) {
            index += 4;
        }
        switch (index) {
            case 0:
                playerName01.setText(name);
                break;
            case 1:
                playerName02.setText(name);
                break;
            case 2:
                playerName04.setText(name);
                break;
            case 3:
                playerName03.setText(name);
                break;
            case 4:
                if (fold) {
                    moneyLabel01.setText("( - )");
                } else {
                    moneyLabel01.setText(money);
                }
                break;
            case 5:
                if (fold) {
                    moneyLabel02.setText("( - )");
                } else {
                    moneyLabel02.setText(money);
                }
                break;
            case 6:
                if (fold) {
                    moneyLabel03.setText("( - )");
                } else {
                    moneyLabel03.setText(money);
                }
                break;
            case 7:
                if (fold) {
                    moneyLabel04.setText("( - )");
                } else {
                    moneyLabel04.setText(money);
                }
                break;
        }
    }

    public void setDealerOrAllInIcon(Player player, boolean allIn) {
        int index = getPlayerIndex(player);
        if (allIn) {
            index += 4;
        }
        switch (index) {
            case 0:
                dealerLabel01.setVisible(true);
                break;
            case 1:
                dealerLabel02.setVisible(true);
                break;
            case 2:
                dealerLabel03.setVisible(true);
                break;
            case 3:
                dealerLabel04.setVisible(true);
                break;
            case 4:
                allInLabel01.setVisible(true);
                break;
            case 5:
                allInLabel02.setVisible(true);
                break;
            case 6:
                allInLabel03.setVisible(true);
                break;
            case 7:
                allInLabel04.setVisible(true);
                break;
        }
    }

    public void setCurrentPlayerLabel(Player player) {
        switch (getPlayerIndex(player)) {
            case 0:
                currentPlayerLabel01.setText("→");
                break;
            case 1:
                currentPlayerLabel02.setText("→");
                break;
            case 2:
                currentPlayerLabel03.setText("→");
                break;
            case 3:
                currentPlayerLabel04.setText("→");
                break;
        }
    }

    public void setPotOrBetLabels(int mainPot, boolean pot) {
        String amount = String.valueOf(mainPot);
        if (pot) {
            potLabel.setText(amount);
        } else {
            betLabel.setText(amount);
        }
    }

    public void setHandcardsPic(Player player, List<PokerCard> handcards) {
        Terminal.print(handcards.toString());
        ImageIcon nullIcon = getCardPic(null);
        Terminal.print("handcards: " + player.getUsername() + player.getHandCards());
        ImageIcon icon01 = getCardPic(player.getHandCards().get(0));
        ImageIcon icon02 = getCardPic(player.getHandCards().get(1));
        switch (getPlayerIndex(player)) {
            case 0:
                playerCards01_01.setIcon(icon01);
                playerCards01_02.setIcon(icon02);
                break;
            case 1:
                playerCards02_01.setIcon(icon01);
                playerCards02_02.setIcon(icon02);
                break;
            case 2:
                playerCards03_01.setIcon(icon01);
                playerCards03_02.setIcon(icon02);
                break;
            case 3:
                playerCards04_01.setIcon(icon01);
                playerCards04_02.setIcon(icon02);
                break;
        }
    }


    public void setActionLabel(Player player, int action) {
        int index = getPlayerIndex(player);
        String actionString = getAction(action);
        String money = "(" + player.getMoney() + ")";
        if (action < 0) {
            index += 4;
        }
        switch (index) {
            case 0:
                actionLabel01.setText(actionString);
                break;
            case 1:
                actionLabel02.setText(actionString);
                break;
            case 2:
                actionLabel03.setText(actionString);
                break;
            case 3:
                actionLabel04.setText(actionString);
                break;
            case 4:
                moneyLabel01.setText(money);
                break;
            case 5:
                moneyLabel02.setText(money);
                break;
            case 6:
                moneyLabel03.setText(money);
                break;
            case 7:
                moneyLabel04.setText(money);
                break;
        }
    }

    public String getAction(int action) {
        switch (action) {
            case 0:
                return "    FOLD    ";
            case 1:
                return "CALL/CHECK";
            case 2:
                return " BET/RAISE ";
            case 3:
                return "   ALL IN   ";
        }
        return "";
    }

    public void setPlayerVisiblity(Player player) {
        Terminal.print("setPlayerVisiblity()");
        Terminal.print("player: " + player.toString());
        ImageIcon nullIcon = getCardPic(null);
        int index = getPlayerIndex(player);
        Terminal.print("index: " + String.valueOf(index));
        switch (index) {
            case 0:
                Terminal.print("case 0");
                playerCards02_01.setIcon(nullIcon);
                playerCards02_02.setIcon(nullIcon);
                playerCards04_01.setIcon(nullIcon);
                playerCards04_02.setIcon(nullIcon);
                playerCards03_01.setIcon(nullIcon);
                playerCards03_02.setIcon(nullIcon);
                moneyLabel02.setVisible(false);
                moneyLabel03.setVisible(false);
                moneyLabel04.setVisible(false);
                break;
            case 1:
                Terminal.print("case 1");
                playerCards01_01.setIcon(nullIcon);
                playerCards01_02.setIcon(nullIcon);
                playerCards04_01.setIcon(nullIcon);
                playerCards04_02.setIcon(nullIcon);
                playerCards03_01.setIcon(nullIcon);
                playerCards03_02.setIcon(nullIcon);
                moneyLabel01.setVisible(false);
                moneyLabel03.setVisible(false);
                moneyLabel04.setVisible(false);
                break;
            case 2:
                Terminal.print("case 2");
                playerCards01_01.setIcon(nullIcon);
                playerCards01_02.setIcon(nullIcon);
                playerCards02_01.setIcon(nullIcon);
                playerCards02_02.setIcon(nullIcon);
                playerCards04_01.setIcon(nullIcon);
                playerCards04_02.setIcon(nullIcon);
                moneyLabel01.setVisible(false);
                moneyLabel02.setVisible(false);
                moneyLabel04.setVisible(false);
                break;
            case 3:
                Terminal.print("case 3");
                playerCards01_01.setIcon(nullIcon);
                playerCards01_02.setIcon(nullIcon);
                playerCards02_01.setIcon(nullIcon);
                playerCards02_02.setIcon(nullIcon);
                playerCards03_01.setIcon(nullIcon);
                playerCards03_02.setIcon(nullIcon);
                moneyLabel02.setVisible(false);
                moneyLabel03.setVisible(false);
                moneyLabel01.setVisible(false);
                break;
        }
    }

    public void setTablecardsPic(int phase) {
        Terminal.print("opencards: " + gameState.openCards);
        switch (phase) {
            case 1:
                ImageIcon icon01 = getCardPic(gameState.openCards.get(0));
                ImageIcon icon02 = getCardPic(gameState.openCards.get(1));
                ImageIcon icon03 = getCardPic(gameState.openCards.get(2));
                tableCard01.setIcon(icon01);
                tableCard02.setIcon(icon02);
                tableCard03.setIcon(icon03);
                break;
            case 2:
                ImageIcon icon04 = getCardPic(gameState.openCards.get(3));
                tableCard04.setIcon(icon04);
                break;
            case 3:
                ImageIcon icon05 = getCardPic(gameState.openCards.get(4));
                tableCard05.setIcon(icon05);
                break;
        }
    }

    public void setWinnerLabel() {
        for (String name : gameState.winnerName) {
            for (Player player : gameState.players) {
                if (Objects.equals(name, player.getUsername())) {
                    switch (getPlayerIndex(player)) {
                        case 0:
                            winnerLabel01.setText("Winner!");
                            break;
                        case 1:
                            winnerLabel02.setText("Winner!");
                            break;
                        case 2:
                            winnerLabel03.setText("Winner!");
                            break;
                        case 3:
                            winnerLabel04.setText("Winner!");
                            break;

                    }
                }
            }
        }
    }

    private List<Player> getPlayers() {
        return gameState.players;
    }

    public int getPlayerIndex(Player player) {
        for (int i = 0; i < getPlayers().size(); i++) {
            Player otherPlayer = getPlayers().get(i);
            if (otherPlayer.getUsername().equals(player.getUsername())) {
                return gameState.players.indexOf(otherPlayer);
            }
        }
        return -1;
    }

    private ImageIcon getCardPic(PokerCard card) {
        if (card == null) {
            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/unknownCard01.png");
        } else {
            switch (card.getColor()) {
                case HEARTS:
                    switch (card.getValue()) {
                        case TWO:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/HEARTS/2.png");
                        case THREE:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/HEARTS/3.png");
                        case FOUR:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/HEARTS/4.png");
                        case FIVE:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/HEARTS/5.png");
                        case SIX:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/HEARTS/6.png");
                        case SEVEN:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/HEARTS/7.png");
                        case EIGHT:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/HEARTS/8.png");
                        case NINE:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/HEARTS/9.png");
                        case TEN:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/HEARTS/10.png");
                        case JACK:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/HEARTS/11.png");
                        case QUEEN:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/HEARTS/12.png");
                        case KING:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/HEARTS/13.png");
                        case ASS:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/HEARTS/14.png");
                    }
                    break;
                case CLUBS:
                    switch (card.getValue()) {
                        case TWO:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/CLUBS/2.png");
                        case THREE:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/CLUBS/3.png");
                        case FOUR:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/CLUBS/4.png");
                        case FIVE:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/CLUBS/5.png");
                        case SIX:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/CLUBS/6.png");
                        case SEVEN:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/CLUBS/7.png");
                        case EIGHT:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/CLUBS/8.png");
                        case NINE:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/CLUBS/9.png");
                        case TEN:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/CLUBS/10.png");
                        case JACK:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/CLUBS/11.png");
                        case QUEEN:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/CLUBS/12.png");
                        case KING:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/CLUBS/13.png");
                        case ASS:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/CLUBS/14.png");
                    }
                    break;
                case DIAMONDS:
                    switch (card.getValue()) {
                        case TWO:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/DIAMONDS/2.png");
                        case THREE:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/DIAMONDS/3.png");
                        case FOUR:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/DIAMONDS/4.png");
                        case FIVE:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/DIAMONDS/5.png");
                        case SIX:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/DIAMONDS/6.png");
                        case SEVEN:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/DIAMONDS/7.png");
                        case EIGHT:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/DIAMONDS/8.png");
                        case NINE:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/DIAMONDS/9.png");
                        case TEN:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/DIAMONDS/10.png");
                        case JACK:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/DIAMONDS/11.png");
                        case QUEEN:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/DIAMONDS/12.png");
                        case KING:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/DIAMONDS/13.png");
                        case ASS:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/DIAMONDS/14.png");
                    }
                    break;
                case SPADES:
                    switch (card.getValue()) {
                        case TWO:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/SPADES/2.png");
                        case THREE:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/SPADES/3.png");
                        case FOUR:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/SPADES/4.png");
                        case FIVE:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/SPADES/5.png");
                        case SIX:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/SPADES/6.png");
                        case SEVEN:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/SPADES/7.png");
                        case EIGHT:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/SPADES/8.png");
                        case NINE:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/SPADES/9.png");
                        case TEN:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/SPADES/10.png");
                        case JACK:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/SPADES/11.png");
                        case QUEEN:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/SPADES/12.png");
                        case KING:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/SPADES/13.png");
                        case ASS:
                            return new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/SPADES/14.png");
                    }
                    break;
            }
        }
        return null;
    }


//    private ImageIcon getCardPic(PokerCard card) {
//        ImageIcon icon;
//        if (card == null) {
//            icon = new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/unknownCard01.png");
//        } else {
//            icon = new ImageIcon("/Users/N/Java/Poker/src/gui/img/Kartenbilder/" + card.getColor() + getCardValue(card.getValue()) + ".png");
//        }
//        return icon;
//    }
}