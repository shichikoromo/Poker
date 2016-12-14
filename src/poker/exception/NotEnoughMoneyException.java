package poker.exception;

import poker.Player;

public class NotEnoughMoneyException extends Exception {

    public NotEnoughMoneyException(Player player) {
        super("poker.Player " + player.getName() + " has not enough money!");
    }
}
