import handChecker.PokerCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class CardDeck {

    private Stack<PokerCard> deck = new Stack<>();

    public CardDeck() {
        prepare();
    }

    private void prepare() {
        deck.clear();
        for (PokerCard.Color color : PokerCard.Color.values()) {
            for (PokerCard.Value value : PokerCard.Value.values()) {
                deck.push(new Card(color, value));
            }
        }
        shuffle();
    }

    private void shuffle() {
        Collections.shuffle(deck);
    }

    private PokerCard draw() {
        // PokerCard card = this.deck.pop();
        // return card;
        return deck.pop();
    }

    public List<PokerCard> drawTwo() {
        List<PokerCard> cards = new ArrayList<>();
        cards.add(draw());
        cards.add(draw());
        return cards;
    }

    public void burn() {
        draw();
    }

    @Override
    public String toString() {
        String string = "";
        for (PokerCard card : deck) {
            string += card + "\n";
        }
        return string;
    }
}
