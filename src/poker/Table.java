package poker;

import handChecker.HandChecker;
import handChecker.HandValue;
import handChecker.PokerCard;
import poker.exception.NotEnoughMoneyException;
import socket.Terminal;

import java.util.*;

public class Table {

    private CardDeck cardDeck = new CardDeck();
    private List<Player> players = new LinkedList<>();
    private List<Player> actualPlayers = new ArrayList<>();
    private List<Player> allInPlayers = new ArrayList<>();
    private List<Integer> mainPot = new ArrayList<>();
    private List<Foo> playerScores = new ArrayList<>();
    private List<Foo> winners = new ArrayList<>();
    private List<Foo> secondWinners = new ArrayList<>();
    private List<String> playerNames = new ArrayList<>();


    private Player dealer;
    private Player smallBlind;
    private Player bigBlind;
    private Player currentPlayer;
    private int minStake = 10;
    private int maxStake;
    private Random random = new Random();
    private Scanner scanner = new Scanner(System.in);

    private int round = 0;
    private boolean newPhase = true;
    private BetPhase betPhase;
    private CardPhase cardPhase;

    public List<Player> addPlayer(Player player) {
        players.add(player);
        playerNames.add(player.getName());
        return players;
    }

    public List<Player> removePlayer(Player player) {
        players.remove(player);
        playerNames.remove(player.getName());
        return players;
    }

    private void findNextDealer() {
        int nextDealerIndex;
        if (dealer == null) {
            nextDealerIndex = random.nextInt(actualPlayers.size());
        } else {
            nextDealerIndex = actualPlayers.indexOf(dealer) + 1;
        }
        dealer = getPlayerFromIndex(nextDealerIndex);

        if (actualPlayers.size() < 2) {
            writeMessageForAll("The Game cannot continue.");
        } else if (actualPlayers.size() == 2) {
            smallBlind = dealer;
            bigBlind = getPlayerFromIndex(nextDealerIndex + 1);
        } else {
            smallBlind = getPlayerFromIndex(nextDealerIndex + 1);
            bigBlind = getPlayerFromIndex(nextDealerIndex + 2);
        }
    }

    private Player getPlayerFromIndex(int i) {
        i = i % actualPlayers.size();
        return actualPlayers.get(i);
    }

    private Player getFirstPlayer() {
        Player firstPlayer;
        if (betPhase == BetPhase.PREFLOP) {
            firstPlayer = getNextPlayer(bigBlind);
        } else {
            firstPlayer = getNextPlayer(dealer);
        }
        return firstPlayer;
    }

    private Player getNextPlayer(Player previousPlayer) {
        int i = actualPlayers.indexOf(previousPlayer);
        Player nextPlayer = getPlayerFromIndex(i + 1);
        return nextPlayer;
    }

    public List<Player> getPlayers() {
        return players;
    }

    private void betPhase() {
        switch (betPhase) {
            case PREFLOP:
                try {
                    maxStake = minStake;
                    smallBlind.bet(minStake / 2);
                    bigBlind.bet(minStake);
                    mainPot.add((minStake / 2));
                    mainPot.add(minStake);

                    String message = "";
                    message += "\n" + "BET PHASE - PRE-FLOP\n";
                    message += "Small Blind " + smallBlind.getName() + " payed ¥" + minStake / 2 + "\n";
                    message += "  Big Blind " + bigBlind.getName() + " payed ¥" + minStake + "\n";
                    message += "\nmax stake: " + maxStake + "\n";
                    writeMessageForAll(message);

                } catch (NotEnoughMoneyException e) {
                    System.err.println(e.getMessage());
                }
                break;
            case POSTFLOP:
                newPhase = true;
                writeMessageForAll("\n" + "BET PHASE - POST FLOP");
                break;
            case POSTTURN:
                newPhase = true;
                writeMessageForAll("\n" + "BET PHASE - POST TURN");
                break;
            case SHOWDOWN:
                newPhase = true;
                writeMessageForAll("\n" + "BET PHASE - SHOW DOWN");
                break;
        }


        while (!checkIfPhaseEnds()) {
            boolean isOptionInvalid = true;
            int option = 0;
            if (currentPlayer.isAllIn()) {
                check();
                currentPlayer = getNextPlayer(currentPlayer);
            } else {
                while (isOptionInvalid) {
                    writeMessageForAll(currentPlayer.getName() + "'s turn...");

                    String message = "";
                    message += currentPlayer.getName() + " - What do you want to do?\n";
                    message += "\t(1) - fold\n";
                    message += "\t(2) - call\n";
                    message += "\t(3) - check\n";
                    message += "\t(4) - bet\n";
                    message += "\t(5) - raise\n";
                    message += "\t(6) - all in\n";

                    option = getInput(message);

                    if (1 <= option && option <= 6) {
                        isOptionInvalid = false;
                    } else {
                        writeMessage("Please choose a valid option (1 - 6).\n");
                    }
                }

                action(getAction(option));
            }
        }
    }

    private int getInput(String question) {
        return currentPlayer.askForInteger(question);
    }

    private boolean isInteger(String s) {
        try {
            Integer.parseInt(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    private boolean checkIfPhaseEnds() {
        if (newPhase) {
            newPhase = false;
            return false;
        }
        for (Player player : actualPlayers) {
            if (player.getActualStake() < maxStake) {
                return false;
            }
        }
        return true;

    }

    private void action(Action action) {
        switch (action) {
            case FOLD:
                fold();
                // skip finding next player
                return;
            case CALL:
                call();
                break;
            case CHECK:
                check();
                break;
            case BET:
            case RAISE:
                bet();
                break;
            case ALL_IN:
                allIn();
                break;
        }

        currentPlayer = getNextPlayer(currentPlayer);
    }

    private Action getAction(int option) {
        return Action.values()[option - 1];
    }

    private void fold() {
        Player nextPlayer = getNextPlayer(currentPlayer);
        writeMessageForAll(currentPlayer.getName() + " chose fold." + "\n");
        actualPlayers.remove(currentPlayer);
        currentPlayer = nextPlayer;
        checkIfRoundEnds();
    }

    public void call() {
        int stake;
        if (currentPlayer.getActualStake() == maxStake) {
            writeMessage("Action invalid. You have to bet:");
            bet();
        } else {
            //TODO es funktioniert nicht richtig
            stake = maxStake - currentPlayer.getActualStake();
            currentPlayer.setActualStake(stake);
            betForCurrentPlayer(stake);
            writeMessageForAll(currentPlayer.getName() + " chose call. He/she payed ¥" + stake + "\n");
        }
    }

    public boolean check() {
        // do nothing
        writeMessageForAll(currentPlayer.getName() + " chose check." + "\n");
        return true;
    }

    public boolean bet() {
        boolean isBetSuccessful = false;
        int tries = 3;
        int stake = 0;
        while (!isBetSuccessful && tries > 0) {
            String message = currentPlayer.getName() + " - How much do you want to bet? (" + tries + " tries left)";
            stake = getInput(message);

            isBetSuccessful = betForCurrentPlayer(stake);
            tries--;
        }

        if (isBetSuccessful) {
            maxStake = stake;
            writeMessageForAll(currentPlayer.getName() + " chose bet/raise. Actual max stake: " + maxStake + "\n");

        }
        return isBetSuccessful;
    }

    private boolean betForCurrentPlayer(int stake) {
        boolean isBetSuccessful = true;
        if (stake < maxStake) {
            writeMessage("Your stake has to be at least " + maxStake);
            isBetSuccessful = false;
        } else {
            try {
                currentPlayer.bet(stake);
                mainPot.add(stake);
            } catch (NotEnoughMoneyException e) {
                writeMessage("You don't have enough money. ");
                isBetSuccessful = false;
            }
        }
        return isBetSuccessful;
    }

    private void allIn() {
        writeMessageForAll(currentPlayer.getName() + "chose all in." + "\n");
        currentPlayer.allIn();
        allInPlayers.add(currentPlayer);
        mainPot.add(currentPlayer.getAllInMoney());
        maxStake = currentPlayer.getAllInMoney();
    }

    private void cardPhase() {
        switch (cardPhase) {
            case PREFLOP:
                writeMessageForAll("\n" + "CARD PHASE - PRE-FLOP");
                int i = 0;
                for (Player player : actualPlayers) {
                    player.getHandCards().clear();
                    player.addCards(cardDeck.draw(2));
                    handCardsMessage(i);
                    i++;
                }
                break;
            case FLOP:
                writeMessageForAll("\n" + "CARD PHASE - FLOP");
                writeMessageForAll(cardDeck.flop() + "\n");
                break;
            case TURN:
                writeMessageForAll("\n" + "CARD PHASE - TURN");
                writeMessageForAll(cardDeck.turn() + "\n");
                break;
            case RIVER:
                writeMessageForAll("\n" + "CARD PHASE - RIVER");
                writeMessageForAll(cardDeck.river() + "\n");
                break;
        }
    }

    private void handCardsMessage(int i) {
        currentPlayer = players.get(i);
        writeMessage("Player " + currentPlayer.getName() + "'s Handcards: " + currentPlayer.getHandCards());
    }

    private int getSumOfMainPot() {
        int sum = 0;
        for (int i : mainPot) {
            sum += i;
        }
        if (allInPlayers.contains(winners)) {
            sum -= getSumOfSidePot();
        }
        return sum;
    }

    private int getSumOfSidePot() {
        int sum;
        int number = 0;

        for (Player player : allInPlayers) {
            if (player.getAllInMoney() > winners.get(0).getPlayer().getAllInMoney()) {
                number++;
            }
        }
        sum = winners.get(0).getPlayer().getAllInMoney() * number;
        return sum;
    }

    private int splitPot(int pot) {
        int splitSum = pot / (winners.size() + 1);
        return splitSum;
    }

    private List<Foo> findWinner() {
        HandChecker handChecker = new HandChecker();
        List<PokerCard> playerCards = new ArrayList<>();
        HandValue currentHandValue;

        if (actualPlayers.size() <= 1) {
            winners.add(new Foo(actualPlayers.get(0), null));
        } else {
            for (Player player : actualPlayers) {
                playerCards.clear();
                playerCards.addAll(player.getHandCards());
                playerCards.addAll(cardDeck.getOpenCards());
                currentHandValue = handChecker.check(playerCards);
                playerScores.add(new Foo(player, currentHandValue));
            }
            Collections.sort(playerScores);

            for (int i = 1; i <= playerScores.size(); i++) {
                int value = playerScores.get(playerScores.size() - 1).compareTo(playerScores.get(playerScores.size() - i));
                if (value == 0) {
                    winners.add(playerScores.get(playerScores.size() - i));
                }
            }
        }
        return winners;
    }

    private boolean isWinnerAllin() {
        for (Player player : allInPlayers) {
            if (winners.contains(player)) {
                return true;
            }
        }
        return false;
    }

    private List<Foo> findSecondWinner() {
        if (winners.get(0).getPlayer().isAllIn() && allInPlayers.size() > 1) {
            secondWinners.add(playerScores.get(playerScores.size() - 2));
            for (int i = 1; i <= playerScores.size() - 1; i++) {
                int value = playerScores.get(playerScores.size() - 2).compareTo(playerScores.get(playerScores.size() - i));
                if (value == 0) {
                    secondWinners.add(playerScores.get(playerScores.size() - i));
                }
            }
        }
        return secondWinners;
    }

    private void checkIfRoundEnds() {
        if (actualPlayers.size() <= 1) {
            endRound();
        }
    }

    private void endRound() {
        int pot = 0;

        findWinner();

        if (!isWinnerAllin()) {
            if (winners.size() == 1) {
                pot = getSumOfMainPot();
                winners.get(0).getPlayer().addMoney(pot);
            } else if (winners.size() > 1) {
                pot = splitPot(getSumOfMainPot());
                for (Foo winner : winners) {
                    winner.getPlayer().addMoney(pot);
                }
            } else {
                // error
            }
            writeMessageForAll("\n" + winners + " is/are winner/s and got ¥" + pot + ". " + "\n");

        } else if (allInPlayers.size() == 1) {
            pot = getSumOfSidePot();
            winners.get(0).getPlayer().addMoney(pot);
            writeMessage("\n" + winners.get(0).getPlayer() + " is winner. He/She got ¥" + pot + ". " + "\n");
        } else {
            findSecondWinner();
            winners.get(0).getPlayer().addMoney(getSumOfMainPot());
            writeMessageForAll("\n" + winners.get(0).getPlayer() + " is winner. He/She got ¥" + pot + ". " + "\n");
            if (secondWinners.size() == 1) {
                pot = getSumOfSidePot();
                secondWinners.get(0).getPlayer().addMoney(pot);
            } else if (secondWinners.size() >= 2) {
                pot = splitPot(getSumOfSidePot());
                for (Foo secondWinner : secondWinners) {
                    secondWinner.getPlayer().addMoney(pot);
                }
            }
            writeMessageForAll(secondWinners + "is/are secondwinner/s and got ¥" + pot + ".");
        }


        for (int i = 0; i < players.size(); i++) {
            Player player = players.get(i);
            if (player.getMoney() < minStake) {
                removePlayer(player);
                writeMessageForAll(player.getName() + " has not enough money to continue.");
            }
        }

        if (!checkIfGameEnds()) {
            writeMessageForAll(round + ". round ended. Next round beginns." + "\n");
            startNextRound();
        } else {
            endGame();
        }
    }

    private boolean checkIfGameEnds() {
        if (players.size() <= 1) {
            endGame();
            return true;
        } else {
            return false;
        }
    }

    private void endGame() {
        writeMessageForAll("The Game is end. " + winners + " is/are winner/s.");
    }

    public void startRoundMessage() {
        String message = "";

        message += "\n" + round + ". ROUND" + "\n";
        message += "Players: " + playerNames + "\n";

        message += "\n" + "     Dealer: " + dealer.getName() + "\n";
        message += "Small-Blind: " + smallBlind.getName() + "\n";
        message += "  Big-Blind: " + bigBlind.getName() + "\n";

        writeMessageForAll(message);
    }


    public void startNextRound() {
        round++;
        cardDeck.prepare();
        winners.clear();
        secondWinners.clear();

        betPhase = BetPhase.PREFLOP;
        cardPhase = CardPhase.PREFLOP;

        actualPlayers.clear();
        actualPlayers.addAll(players);

        findNextDealer();

        startRoundMessage();
        Terminal.print("Players: " + players + "\n");

        currentPlayer = getFirstPlayer();

        cardPhase();
        betPhase();

        cardPhase = CardPhase.FLOP;
        cardPhase();

        betPhase = BetPhase.POSTFLOP;
        betPhase();

        cardPhase = CardPhase.TURN;
        cardPhase();

        betPhase = BetPhase.POSTTURN;
        betPhase();

        cardPhase = CardPhase.RIVER;
        cardPhase();

        betPhase = BetPhase.SHOWDOWN;
        betPhase();

        endRound();
    }


    private void writeMessage(String message) {
        Terminal.print(message);
        currentPlayer.say(message);
    }

    public void writeMessageForAll(String message) {
        Terminal.print(message);
        for (Player player : players) {
            player.say(message);
        }
    }

    @Override
    public String toString() {
        String string = "";
        String string2 = "";
        for (Player player : players) {
            string += player;
        }
        string2 = "D: " + dealer + "S: " + smallBlind + "B: " + bigBlind;
        return string + string2 + "";
    }

    enum BetPhase {
        PREFLOP,
        POSTFLOP,
        POSTTURN,
        SHOWDOWN
    }


    enum Action {
        FOLD,
        CALL,
        CHECK,
        BET,
        RAISE,
        ALL_IN
    }

    enum CardPhase {
        PREFLOP,
        FLOP,
        TURN,
        RIVER
    }

    class Foo implements Comparable<Foo> {

        public Player player;
        public HandValue handValue;

        public Foo(Player player, HandValue handValue) {
            this.player = player;
            this.handValue = handValue;
        }

        public Player getPlayer() {
            return player;
        }

        @Override
        public int compareTo(Foo o) {
            return this.handValue.compareTo(o.handValue);
        }

    }


}
