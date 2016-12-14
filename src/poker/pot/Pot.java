package poker.pot;

import handChecker.HandValue;
import poker.Player;

import java.util.HashMap;
import java.util.List;

public class Pot implements IPot {

    int totalStakes = 0;
    HashMap<Player, HandValue> playerScores;

    public long getTotalStakes() {
        return totalStakes;
    }

    @Override
    public void addStakes(Player player, int stakes) {
        if (stakes > 0) {
            totalStakes += stakes;
        }
    }

    @Override
    public void removeStakes(Player player, int stakes) {
        if (stakes > totalStakes) {
            stakes = totalStakes;
        }
        totalStakes -= stakes;
    }

    @Override
    public void getWinnings(List<PlayerScore> playerScores) {
        boolean same = true;
//        Map<Player,Integer> playerWinnings = new HashMap<>();
//        Collections.sort(playerScores);
//        PlayerScore maxScore = playerScores.get(0);
//
//        // main
//        for (int i = 0; same && i < playerScores.size(); i++) {
//            PlayerScore currentPlayerScore = playerScores.get(i);
//            if (maxScore.handValue == currentPlayerScore.handValue){
//                playerWinnings.put(currentPlayerScore.player,0);
//                playerScores.remove(currentPlayerScore);
//                // main split pot
//            } else {
//
//            }
//        }
//
//        playerWinnings.size();
//
//        if (){ // isallin?
//            // side pot
//        }
    }

    class PlayerScore implements Comparable<PlayerScore> {

        public Player player;
        public HandValue handValue;
        public boolean isAllIn;

        public PlayerScore(Player player, HandValue handValue) {
            this(player, handValue, false);
        }

        public PlayerScore(Player player, HandValue handValue, boolean isAllIn) {
            this.player = player;
            this.handValue = handValue;
            this.isAllIn = isAllIn;
        }

        @Override
        public int compareTo(PlayerScore o) {
            return o.handValue.compareTo(this.handValue);
        }

    }
}
