
public class Card implements handChecker.PokerCard {

    private Color color;
    private Value value;

    public Card(Color color, Value value) {
        this.color = color;
        this.value = value;
    }

    @Override
    public Color getColor() {
        return color;
    }

    @Override
    public Value getValue() {
        return value;
    }

    @Override
    public String toString() {
        return color + " " + value;
    }
}
