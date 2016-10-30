import handChecker.PokerCard;

import java.util.Collections;
import java.util.Stack;

public class CardDeck {

    private Stack<PokerCard> deck;

    public CardDeck() {
        deck = new Stack<>();
        for (PokerCard.Color color : PokerCard.Color.values()) {
            for (PokerCard.Value value : PokerCard.Value.values()) {
                deck.push(new Card(color, value));
            }
        }
    }

    public void shuffle() {
        Collections.shuffle(deck);
    }

    @Override
    public String toString() {
        String string = "";
        for (PokerCard card : deck) {
            string += ((Card) card).toUtf8Symbol() + "\n";
        }
        return string;
    }
}
