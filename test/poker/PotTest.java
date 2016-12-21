package poker;

import handChecker.HandValue;
import org.junit.Before;
import org.junit.Test;
import poker.pot.IPot;
import poker.pot.Pot;

import java.util.HashMap;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class PotTest {

    IPot pot;
    Player player1;
    Player player2;
    HashMap<Player, HandValue> playerScores;

    @Before
    public void setUp() throws Exception {
        pot = new Pot();

        //player1 = new Player("Player 1", 1000);
        //player2 = new Player("Player 2", 1000);
    }

    @Test
    public void potExists() {
        assertNotNull(pot);
        assertEquals(0, pot.getTotalStakes());
    }

    @Test
    public void addNegativeStakes() {
        pot.addStakes(player1, -5);
        assertEquals(0, pot.getTotalStakes());
    }

    @Test
    public void addStakes() {
        pot.addStakes(player1, 135);
        assertEquals(135, pot.getTotalStakes());
    }

    @Test
    public void removeStakes() {
        pot.addStakes(player1, 100);
        pot.removeStakes(player1, 100);
        assertEquals(0, pot.getTotalStakes());
    }

    @Test
    public void getWinning() {

    }

}
