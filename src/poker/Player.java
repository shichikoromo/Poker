package poker;

import handChecker.PokerCard;
import poker.exception.NotEnoughMoneyException;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int money;
    private int actualStake;
    private int allInMoney;
    private boolean isAllIn;
    private List<PokerCard> handCards = new ArrayList<>();

    private ObjectInputStream in;
    private ObjectOutputStream out;

    public Player(String name, int money, ObjectInputStream in, ObjectOutputStream out) {
        this.name = name;
        this.money = money;

        this.in = in;
        this.out = out;
    }

    /**
     * write message to player
     */
    public void say(String message) {
        try {
            out.writeObject(message);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Object ask(String question) {
        say("QUESTION:\n" + question);
        Object result = null;
        try {
            result = in.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return result;
    }

    public String askForString(String question) {
        Object askStringObject = ask(question);
        if (askStringObject != null) {
            return askStringObject.toString();
        } else {
            return "";
        }
    }

    public int askForInteger(String question) throws NumberFormatException {
        String askIntegerString = askForString(question);
        return Integer.parseInt(askIntegerString);
    }

    public String getName() {
        return name;
    }

    public int getMoney() {
        return money;
    }

    public int getActualStake() {
        return actualStake;
    }

    public void setActualStake(int stake) {
        actualStake += stake;
    }

    public List<PokerCard> getHandCards() {
        return handCards;
    }

    public void addMoney(int money) {
        this.money += money;
    }

    public void addCards(List<PokerCard> cards) {
        handCards.addAll(cards);
    }

    public void bet(int stake) throws NotEnoughMoneyException {
        if (stake <= money) {
            money = money - stake;
            actualStake = stake;
        } else {
            throw new NotEnoughMoneyException(this);
        }
    }

    public void allIn() {
        try {
            setAllInMoney(money);
            setAllIn(true);
            bet(money);
        } catch (NotEnoughMoneyException e) {
            // should not happen
        }

    }

    public boolean isAllIn() {
        return isAllIn;
    }

    public void setAllIn(boolean allIn) {
        isAllIn = allIn;
    }

    public int getAllInMoney() {
        return allInMoney;
    }

    public void setAllInMoney(int amount) {
        allInMoney = amount;
    }

    @Override
    public String toString() {
        return name + " " + money;
    }
}
