import handChecker.PokerCard;

import java.util.List;

public class Poker {
    public static void main(String[] args) {

        CardDeck cardDeck = new CardDeck();
        System.out.println(cardDeck.toString());

        Player player = new Player("PlayerA", 2000, 0);
        System.out.println(player);
        player.setBet(10);
        System.out.println(player + "\n");

        List<PokerCard> cards = cardDeck.drawTwo();
        System.out.println(cards.toString());
    }
}
