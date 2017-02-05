package poker;

import handChecker.PokerCard;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

public class GameState implements Serializable {

    private static final long serialVersionUID = 1L;

    public int round;
    public Table.CardPhase cardPhase;
    public List<Player> players = new LinkedList<>();
    public Player dealer;

    public int maxStake;
    public List<Integer> mainPot = new ArrayList<>();

    public List<Table.PlayerHandValue> winners = new ArrayList<>();
    public List<Table.PlayerHandValue> secondWinners = new ArrayList<>();

    public List<String> winnerName = new ArrayList<>();

    public List<ArrayList> handCards = new ArrayList<>();

    public boolean isOptionInvalid;

    public  boolean isPhaseEnd = false;

    public Player currentPlayer;

    public int tryAction;
    public int tryStake;

    public List<PokerCard> openCards = new ArrayList<>();


}
