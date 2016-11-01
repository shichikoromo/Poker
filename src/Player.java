public class Player {
    private String name;
    private int money;
    private int bet;
    private Card card;


    public Player(String name, int money, int bet) {
        this.name = name;
        this.money = money;
        this.bet = bet;
    }

    public void setBet(int bet) {
        this.bet = bet;
    }


}
