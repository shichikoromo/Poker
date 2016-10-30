import java.util.Arrays;

public class Card implements handChecker.PokerCard {

    private static final int HEX_SPADES = 0x1F0A1;
    private static final int HEX_HEARTS = 0x1F0B1;
    private static final int HEX_DIAMONDS = 0x1F0C1;
    private static final int HEX_CLUBS = 0x1F0D1;
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

    public String toUtf8Symbol() {
        Integer hex;

        switch (color) {
            case SPADES:
                hex = HEX_SPADES;
                break;
            case HEARTS:
                hex = HEX_HEARTS;
                break;
            case DIAMONDS:
                hex = HEX_DIAMONDS;
                break;
            case CLUBS:
                hex = HEX_CLUBS;
                break;
            default:
                throw new RuntimeException("Unable to evaluate color of this card");
        }

        int index = Arrays.asList(Value.values()).indexOf(value);
        hex += index;

        // 10 = JACK
        if (index > 10) {
            // skip the knight from the unicode cards symbols
            hex++;
        }

        return new String(Character.toChars(hex));
    }
}
