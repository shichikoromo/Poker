package poker;

import handChecker.HandChecker;
import handChecker.HandValue;
import handChecker.PokerCard;
import poker.exception.NotEnoughMoneyException;
import socket.Messenger;
import socket.Terminal;

import java.io.Serializable;
import java.util.*;

public class Table extends Thread {

    private CardDeck cardDeck = new CardDeck();
    private List<Player> actualPlayers = new ArrayList<>();
    private List<Player> allInPlayers = new ArrayList<>();
    private List<PlayerHandValue> playerScores = new ArrayList<>();

    private List<String> playerNames = new ArrayList<>();
    private GameState gameState = new GameState();

    private Player smallBlind;
    private Player bigBlind;
    private int minStake = 10;
    private Random random = new Random();

    private boolean newPhase = true;
    private boolean firstBet;
    private BetPhase betPhase;

    private Messenger messenger;

    public Table(Messenger messenger) {
        this.messenger = messenger;
    }

    public List<Player> addPlayer(Player player) {
        gameState.players.add(player);
        playerNames.add(player.getName());
        return gameState.players;
    }

    public List<Player> removePlayer(Player player) {
        gameState.players.remove(player);
        playerNames.remove(player.getName());
        return gameState.players;
    }

    private void findNextDealer() {
        Terminal.print("findNextDealer()");
        int nextDealerIndex;
        if (gameState.dealer == null) {
            nextDealerIndex = random.nextInt(actualPlayers.size());
        } else {
            nextDealerIndex = actualPlayers.indexOf(gameState.dealer) + 1;
        }
        gameState.dealer = getPlayerFromIndex(nextDealerIndex);

        if (actualPlayers.size() < 2) {
            Terminal.print("actualPlayers.size() < 2");
            // writeMessageForAll("The Game cannot continue.");
        } else if (actualPlayers.size() == 2) {
            Terminal.print("actualPlayers.size() == 2");
            smallBlind = gameState.dealer;
            bigBlind = getPlayerFromIndex(nextDealerIndex + 1);
        } else {
            Terminal.print("actualPlayers.size() > 2");
            smallBlind = getPlayerFromIndex(nextDealerIndex + 1);
            bigBlind = getPlayerFromIndex(nextDealerIndex + 2);
        }
    }

    private Player getPlayerFromIndex(int i) {
        Terminal.print("actualplayers.size: " + actualPlayers.size());
        i = i % actualPlayers.size();
        Terminal.print("next player: " + actualPlayers.get(i));
        return actualPlayers.get(i);
    }

    private Player getFirstPlayer() {
        Player firstPlayer;
        if (betPhase == BetPhase.PREFLOP) {
            firstPlayer = getNextPlayer(bigBlind);
        } else {
            firstPlayer = getNextPlayer(gameState.dealer);
        }
        return firstPlayer;
    }

    private Player getNextPlayer(Player previousPlayer) {
        int i = actualPlayers.indexOf(previousPlayer);
        Player nextPlayer = getPlayerFromIndex(i + 1);
        return nextPlayer;
    }

    public List<Player> getPlayers() {
        return gameState.players;
    }

    private void betPhase() {
        switch (betPhase) {
            case PREFLOP:
                try {
                    gameState.maxStake = minStake;
                    smallBlind.bet(minStake / 2);
                    bigBlind.bet(minStake);
                    gameState.mainPot.add((minStake / 2));
                    gameState.mainPot.add(minStake);
                    Terminal.print("PREFLOP:" + "maxstake " + gameState.maxStake + " SB bet" + smallBlind.getActualStake() + " BB bet " + bigBlind.getActualStake() + " mainpot " + gameState.mainPot);
                } catch (NotEnoughMoneyException e) {
                    System.err.println(e.getMessage());
                }
                messenger.synchro(getGameState());
                break;
            case POSTFLOP:
                newPhase = true;
                // writeMessageForAll("BET PHASE - POST FLOP");
                break;
            case POSTTURN:
                newPhase = true;
                // writeMessageForAll("BET PHASE - POST TURN");
                break;
            case SHOWDOWN:
                newPhase = true;
                //  writeMessageForAll("BET PHASE - SHOW DOWN");
                break;
        }

        while (!checkIfPhaseEnds()) {
            gameState.isOptionInvalid = true;
            int option = 0;
            if (gameState.currentPlayer.isAllIn()) {
                gameState.currentPlayer = getNextPlayer(gameState.currentPlayer);
                option = 1;
            } else {
                while (gameState.isOptionInvalid) {
                    if (gameState.tryAction >= 0) {
                        option = gameState.tryAction;
                    } else {
                        gameState.isOptionInvalid = false;
                    }
                }
            }

            action(getAction(option));
            messenger.synchro(gameState);
        }
//
//        while (true) {
//            try {
//                Thread.sleep(500);
//            } catch (InterruptedException e) {
//                e.printStackTrace();
//            }
//        }

//        gameState.isPhaseEnd = true;
    }


    private String getOption() {
        String message = "";
        message += gameState.currentPlayer.getName() + " - What do you want to do? (Your money remains ¥" + gameState.currentPlayer.getMoney() + ")\n";
        message += "\t(1) - fold\n";
        if (!(gameState.currentPlayer.getActualStake() == gameState.maxStake)) {
            message += "\t(2) - call\n";
        } else {
            message += "\t\n";
        }
        message += "\t(3) - check\n";
        if (firstBet) {
            message += "\t(4) - bet\n";
        } else {
            message += "\t(4) - raise\n";
        }
        message += "\t(6) - all in";

        return message;
    }

//    private int getInput(String question) {
//        return currentPlayer.askForInteger(question);
//    }
//
//    private boolean isInteger(String s) {
//        try {
//            Integer.parseInt(s);
//            return true;
//        } catch (NumberFormatException e) {
//            return false;
//        }
//    }

    private boolean checkIfPhaseEnds() {
        if (newPhase) {
            newPhase = false;
            gameState.isPhaseEnd = true;
        }
        for (Player player : actualPlayers) {
            if (!player.isAllIn() && player.getActualStake() < gameState.maxStake) {
                gameState.isPhaseEnd = false;
            }
        }
        return gameState.isPhaseEnd;

    }

    private void action(Action action) {
        switch (action) {
            case FOLD:
                fold();
                // skip finding next player
                return;
            case CALL:
                if (gameState.currentPlayer.getActualStake() < gameState.maxStake) {
                    call();
                } else {
                    check();
                }
                break;
            case BET:
                bet();
                break;
            case ALL_IN:
                allIn();
                break;
        }

        gameState.currentPlayer = getNextPlayer(gameState.currentPlayer);
    }

    private Action getAction(int option) {
        return Action.values()[option];
    }

    private void fold() {
        Player nextPlayer = getNextPlayer(gameState.currentPlayer);
        // writeMessageForAll(currentPlayer.getName() + " chose fold.");
        actualPlayers.remove(gameState.currentPlayer);
        gameState.currentPlayer = nextPlayer;
        checkIfRoundEnds();

    }

    public void call() {
        int stake;
        if (gameState.currentPlayer.getActualStake() == gameState.maxStake) {
            // writeMessage("Action invalid. You have to firstBet:");
            check();
        } else {
            stake = gameState.maxStake - gameState.currentPlayer.getActualStake();
            if (stake > 0) {
                betForCurrentPlayer(gameState.maxStake);
                gameState.currentPlayer.setActualStake(gameState.maxStake);
            }
            //  writeMessageForAll(currentPlayer.getName() + " chose call.");
        }

    }

    public boolean check() {
        // do nothing
        return true;
    }

    public boolean bet() {
        boolean isBetSuccessful = false;
        int tries = 3;
        int stake;
        while (!isBetSuccessful && tries > 0) {
            String message = gameState.currentPlayer.getName() + " - How much do you want to firstBet? (" + tries + " tries left)";
            //stake = getInput(message);

            // isBetSuccessful = betForCurrentPlayer(stake);
            tries--;
        }

        if (isBetSuccessful) {
            int max = 0;
            for (int mainStake : gameState.mainPot) {
                max = Math.max(max, mainStake);
                gameState.maxStake = max;
            }
            // writeMessageForAll(currentPlayer.getName() + " chose firstBet/raise. Actual max stake: " + gameState.maxStake + "\n");

        }

        if (!firstBet) {
            firstBet = false;
        }

        return isBetSuccessful;
    }

    private boolean betForCurrentPlayer(int stake) {
        boolean isBetSuccessful = true;
        if (stake < gameState.maxStake) {
            // writeMessage("Your stake has to be at least " + gameState.maxStake);
            isBetSuccessful = false;
        } else {
            try {
                gameState.currentPlayer.bet(stake);
                gameState.mainPot.add(stake);
            } catch (NotEnoughMoneyException e) {
                //  writeMessage("You don't have enough money. ");
                isBetSuccessful = false;
            }
        }
        return isBetSuccessful;
    }

    private void allIn() {
        gameState.currentPlayer.allIn();
        allInPlayers.add(gameState.currentPlayer);
        gameState.mainPot.add(gameState.currentPlayer.getAllInMoney());
        gameState.maxStake = gameState.currentPlayer.getAllInMoney();
        // writeMessageForAll(currentPlayer.getName() + " chose all in. Actual max stake: " + gameState.maxStake);
    }

    private void cardPhase() {
        switch (gameState.cardPhase) {
            case PREFLOP:
                List<PokerCard> handCards = new ArrayList<>();
                for (Player player : actualPlayers) {
                    player.getHandCards().clear();
                    handCards = cardDeck.draw(2);
                    player.addCards(handCards);
                    gameState.handCards.add((ArrayList) handCards);
                }
                break;
            case FLOP:
                Terminal.print("\n" + "CARD PHASE - FLOP");
                Terminal.print(cardDeck.flop() + "\n");
                gameState.openCards.addAll(cardDeck.flop());
                break;
            case TURN:
                Terminal.print("\n" + "CARD PHASE - TURN");
                Terminal.print(cardDeck.turn() + "\n");
                gameState.openCards.addAll(cardDeck.turn());
                break;
            case RIVER:
                Terminal.print("\n" + "CARD PHASE - RIVER");
                Terminal.print(cardDeck.river() + "\n");
                gameState.openCards.addAll(cardDeck.river());
                break;
        }
    }


    private int getSumOfMainPot() {
        int sum = 0;
        for (int i : gameState.mainPot) {
            sum += i;
        }
        if (allInPlayers.contains(gameState.winners)) {
            sum -= getSumOfSidePot();
        }
        return sum;
    }

    private int getSumOfSidePot() {
        int sum;
        int number = 0;

        for (Player player : allInPlayers) {
            if (player.getAllInMoney() > gameState.winners.get(0).getPlayer().getAllInMoney()) {
                number++;
            }
        }
        sum = gameState.winners.get(0).getPlayer().getAllInMoney() * number;
        return sum;
    }

    private int splitPot(int pot) {
        return pot / (gameState.winners.size() + 1);
    }

    private List<PlayerHandValue> findWinner() {
        HandChecker handChecker = new HandChecker();
        List<PokerCard> playerCards = new ArrayList<>();
        HandValue currentHandValue;

        if (actualPlayers.size() <= 1) {
            gameState.winners.add(new PlayerHandValue(actualPlayers.get(0), null));
        } else {
            for (Player player : actualPlayers) {
                playerCards.clear();
                playerCards.addAll(player.getHandCards());
                playerCards.addAll(cardDeck.getOpenCards());
                currentHandValue = handChecker.check(playerCards);
                playerScores.add(new PlayerHandValue(player, currentHandValue));
            }
            Collections.sort(playerScores);

            for (int i = 1; i <= playerScores.size(); i++) {
                int value = playerScores.get(playerScores.size() - 1).compareTo(playerScores.get(playerScores.size() - i));
                if (value == 0) {
                    gameState.winners.add(playerScores.get(playerScores.size() - i));
                }
            }
        }

        for (PlayerHandValue winner : gameState.winners) {
            gameState.winnerName.add(winner.getPlayer().getUsername());
        }
        return gameState.winners;
    }

    private boolean isWinnerAllin() {
        for (Player player : allInPlayers) {
            if (gameState.winners.contains(player)) {
                return true;
            }
        }
        return false;
    }

    private List<PlayerHandValue> findSecondWinner() {
        if (gameState.winners.get(0).getPlayer().isAllIn() && allInPlayers.size() > 1) {
            gameState.secondWinners.add(playerScores.get(playerScores.size() - 2));
            for (int i = 1; i <= playerScores.size() - 1; i++) {
                int value = playerScores.get(playerScores.size() - 2).compareTo(playerScores.get(playerScores.size() - i));
                if (value == 0) {
                    gameState.secondWinners.add(playerScores.get(playerScores.size() - i));
                }
            }
        }
        return gameState.secondWinners;
    }

    private void checkIfRoundEnds() {
        if (actualPlayers.size() <= 1) {
            // endRound();
        }
    }

    private void endRound() {
        int pot = 0;
        findWinner();
        if (!isWinnerAllin()) {
            String winnerString = "";
            if (gameState.winners.size() == 1) {
                pot = getSumOfMainPot();
                Player winner = gameState.winners.get(0).getPlayer();
                winner.addMoney(pot);
                winnerString += winner.getName();
            } else if (gameState.winners.size() > 1) {
                pot = splitPot(getSumOfMainPot());
                for (Iterator<PlayerHandValue> iterator = gameState.winners.iterator(); iterator.hasNext(); ) {
                    PlayerHandValue winner = iterator.next();
                    winner.getPlayer().addMoney(pot);
                    winnerString += winner.getPlayer().getName();
                    if (iterator.hasNext()) {
                        winnerString += ", ";
                    }
                }
            } else {
                // error
            }
            //writeMessageForAll("Winner " + winnerString + " got ¥" + pot + ". " + "\n");

        } else if (allInPlayers.size() == 1) {
            pot = getSumOfSidePot();
            gameState.winners.get(0).getPlayer().addMoney(pot);
            //writeMessage("\n" + winners.get(0).getPlayer().getName() + " is a winner and got ¥" + pot + ". " + "\n");
        } else {
            findSecondWinner();
            gameState.winners.get(0).getPlayer().addMoney(getSumOfMainPot());
            // writeMessageForAll("\n" + winners.get(0).getPlayer().getName() + " is a winner and got ¥" + pot + ". " + "\n");
            if (gameState.secondWinners.size() == 1) {
                pot = getSumOfSidePot();
                gameState.secondWinners.get(0).getPlayer().addMoney(pot);
            } else if (gameState.secondWinners.size() >= 2) {
                pot = splitPot(getSumOfSidePot());
                for (PlayerHandValue secondWinner : gameState.secondWinners) {
                    secondWinner.getPlayer().addMoney(pot);
                }
            }
            //writeMessageForAll(secondWinners + "is/are secondwinner/s and got ¥" + pot + ".");
        }


        for (int i = 0; i < gameState.players.size(); i++) {
            Player player = gameState.players.get(i);
            if (player.getMoney() < minStake) {
                //writeMessageForAll(player.getName() + " has not enough money to continue.");
                removePlayer(player);
            }
        }

        for (PlayerHandValue playerscore : playerScores) {
            Player player = playerscore.getPlayer();
            if (player.isAllIn() && !gameState.winners.contains(player)) {
                // writeMessageForAll("Player " + player.getName() + " went all in and did not won. Player must leave.");
                removePlayer(player);
            }
        }


        if (!checkIfGameEnds()) {
            //writeMessageForAll(gameState.round + ".round is over. Next round begins." + "\n");
            startNextRound();
        } else {
            endGame();
        }
    }

    private boolean checkIfGameEnds() {
        if (gameState.players.size() <= 1) {
            endGame();
            return true;
        } else {
            return false;
        }
    }

    private void endGame() {
        // writeMessageForAll("The Game is over.");

    }

//    public void startRoundMessage() {
//        String message = "";
//
//        message += "\n" + round + ". ROUND" + "\n";
//        message += "Players: " + playerNames + "\n";
//
//        message += "\n" + "     Dealer: " + dealer.getName() + "\n";
//        message += "Small Blind: " + smallBlind.getName() + "\n";
//        message += "  Big Blind: " + bigBlind.getName() + "\n";
//
//        writeMessageForAll(message);
//    }

    public void startNextRound() {
        gameState.round++;
        cardDeck.prepare();
        gameState.winners.clear();
        gameState.secondWinners.clear();
        gameState.maxStake = 0;

        betPhase = BetPhase.PREFLOP;
        gameState.cardPhase = CardPhase.PREFLOP;

        actualPlayers.clear();
        actualPlayers.addAll(gameState.players);

        findNextDealer();

        Terminal.print("Players: " + gameState.players + "\n");

        gameState.currentPlayer = getFirstPlayer();

        cardPhase();
        betPhase();

        gameState.cardPhase = CardPhase.FLOP;
        cardPhase();

        betPhase = BetPhase.POSTFLOP;
        betPhase();

        gameState.cardPhase = CardPhase.TURN;
        cardPhase();

        betPhase = BetPhase.POSTTURN;
        betPhase();

        gameState.cardPhase = CardPhase.RIVER;
        cardPhase();

        betPhase = BetPhase.SHOWDOWN;
        betPhase();

        endRound();
    }

    public GameState getGameState() {
        return gameState;
    }

    private void writeMessage(String message, Player player) {
        Terminal.print(message);
        //player.say(message);
    }

    private void writeMessage(String message) {
        Terminal.print(message);
        //currentPlayer.say(message);
    }

//    public void writeMessageForAll(String message) {
//        Terminal.print(message);
//        for (Player player : gameState.players) {
//            player.say(message);
//        }
//    }

    @Override
    public String toString() {
        String string = "";
        String string2 = "";
        string += "[";
        for (Player player : gameState.players) {
            string += player.getName();
        }
        string += "]";
        string2 = ", D: " + gameState.dealer.getName() + ", S: " + smallBlind.getName() + ", B: " + bigBlind.getName();
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
        BET,
        ALL_IN
    }

    enum CardPhase implements Serializable {
        PREFLOP,
        FLOP,
        TURN,
        RIVER
    }

    class PlayerHandValue implements Comparable<PlayerHandValue> {

        public Player player;
        public HandValue handValue;

        public PlayerHandValue(Player player, HandValue handValue) {
            this.player = player;
            this.handValue = handValue;
        }

        public Player getPlayer() {
            return player;
        }

        @Override
        public int compareTo(PlayerHandValue o) {
            return this.handValue.compareTo(o.handValue);
        }

    }
}
