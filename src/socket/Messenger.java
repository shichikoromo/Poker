package socket;

import poker.GameState;
import poker.Player;

public interface Messenger {

    int waitForAction(Player player) ;

    void sendMessage(String message);

    void synchro(GameState gameState);
}
