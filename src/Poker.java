public class Poker {
    public static void main(String[] args) {

        CardDeck cardDeck = new CardDeck();
        cardDeck.shuffle();
        System.out.println(cardDeck.toString());
    }
}
