package spinbattle.test;

import ggi.core.SimplePlayerInterface;
import spinbattle.core.ProximityMap;
import spinbattle.core.SpinGameState;
import spinbattle.params.Constants;
import spinbattle.params.SpinBattleParams;
import spinbattle.players.HeuristicLauncher;
import spinbattle.view.SpinBattleView;
import utilities.JEasyFrame;

public class AIVersusAIView {

    public static void main(String[] args) throws Exception {
        SpinBattleParams params = new SpinBattleParams();
        SpinGameState gameState = new SpinGameState().setParams(params).setPlanets();
        SpinBattleView view = new SpinBattleView().setParams(params).setGameState(gameState);
        
        
        HeuristicLauncher player1 = new HeuristicLauncher(); // change one of these
        HeuristicLauncher player2 = new HeuristicLauncher(); // to make it play against a new ai
        // just make sure the new ai has a makeTransits(state, constants) method.
        
        new JEasyFrame(view, "Spin Battle Game");
        while(!gameState.gameOver()) {
            gameState.next(null);
            gameState = (SpinGameState) gameState.copy();
            player1.makeTransits(gameState, Constants.playerOne);
            player2.makeTransits(gameState, Constants.playerTwo);
            view.setGameState((SpinGameState) gameState.copy());
            view.repaint();
            Thread.sleep(20);
        }
    }
}
