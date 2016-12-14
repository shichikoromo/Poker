package poker.pot;

import poker.Player;

import java.util.List;

public interface IPot {
    long getTotalStakes();

    void addStakes(Player player, int stakes);

    void removeStakes(Player player, int stakes);

    void getWinnings(List<Pot.PlayerScore> playerScores);
}
