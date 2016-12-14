package poker;

import handChecker.PokerCard;
import poker.exception.NotEnoughMoneyException;

import java.util.ArrayList;
import java.util.List;

public class Player {
    private String name;
    private int money;
    private int actualStake;
    private int allInMoney;
    private boolean isAllIn;
    private List<PokerCard> handCards = new ArrayList<>();

    public Player(String name, int money) {
        this.name = name;
        this.money = money;
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
