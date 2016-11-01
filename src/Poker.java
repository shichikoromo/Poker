import handChecker.PokerCard;

import java.util.ArrayList;
import java.util.List;

public class Poker {
    public static void main(String[] args) {

        CardDeck cardDeck = new CardDeck();
        System.out.println(cardDeck.toString());

        List<Player> players = new ArrayList<>();

        players.add(new Player("(◉-◉)", 2000, 0));
        players.add(new Player("(\")-・〜", 2000, 0));
        players.add(new Player("P('')q", 2000, 0));

        for (Player player : players) {
            foo(player, cardDeck);
        }

        System.out.println("\n");

        System.out.println(cardDeck.flop());
        System.out.println(cardDeck.turn());
        System.out.println(cardDeck.river());


    }

    public static void foo(Player player, CardDeck cardDeck) {
        List<PokerCard> cards = cardDeck.drawTwo();
        System.out.println(player + "\t" + cards.toString());
    }
}
