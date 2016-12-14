package poker;

import handChecker.PokerCard;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Stack;

public class CardDeck {

    private Stack<PokerCard> deck = new Stack<>();
    private List<PokerCard> openCards = new ArrayList<>();

    public CardDeck() {
    }

    public void prepare() {
        deck.clear();
        openCards.clear();
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

    public List<PokerCard> draw(int amount) {
        List<PokerCard> cards = new ArrayList<>();
        for (int i = 1; i <= amount; i++) {
            cards.add(deck.pop());
        }
        return cards;
    }

    public void burn() {
        draw(1);
    }

    private List<PokerCard> drawOpenCards(int amount) {
        openCards.addAll(draw(amount));
        return openCards;
    }

    public List<PokerCard> flop() {
        burn();
        return drawOpenCards(3);
    }

    public List<PokerCard> turn() {
        burn();
        return drawOpenCards(1);
    }

    public List<PokerCard> river() {
        burn();
        return drawOpenCards(1);
    }

    public List<PokerCard> getOpenCards() {
        return openCards;
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
