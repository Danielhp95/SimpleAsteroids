package gvglink;

import core.player.AbstractMultiPlayer;
import evodef.EvoAlg;
import evodef.GameActionSpaceAdapterMulti;
import ga.SimpleRMHC;
import ntuple.SlidingMeanEDA;
import planetwar.GameState;
import teamZero.spinbattle.controllers.multiPlayer.treeReusageDiscountOLMCTS.Agent;
import tools.ElapsedCpuTimer;

/**
 *  This class illustrates using a link state in order to allow a GVGAI agent to
 *  play a non-GVGAI game via the link state.
 *
 *  The GeneralGameRunner is the class that brings to two players together to play the game.
 */

public class GeneralGameRunnerTest {
    public static void main(String[] args) {
        PlanetWarsLinkState linkState = new PlanetWarsLinkState();
        GeneralGameRunner runner = new GeneralGameRunner().setGame(linkState).setLength(200);

        AbstractMultiPlayer player1, player2;
        GameActionSpaceAdapterMulti.visual = false;

        GameState.includeBuffersInScore = false;

        int idPlayer1 = 0;
        int idPlayer2 = 1;

        ElapsedCpuTimer timer = new ElapsedCpuTimer();

        player1 = new Agent(linkState.copy(), timer, idPlayer1);

        // try the evolutionary players
        // GameActionSpaceAdapterMulti.visual = true;

        int nResamples = 1;
        EvoAlg evoAlg = new SimpleRMHC(nResamples);

        int nEvals = 200;
        EvoAlg evoAlg2 = new SlidingMeanEDA().setHistoryLength(20);


        teamZero.spinbattle.controllers.multiPlayer.ea.Agent evoAgent1 = new teamZero.spinbattle.controllers.multiPlayer.ea.Agent(linkState.copy(), timer, evoAlg, idPlayer1, nEvals);
        evoAgent1.sequenceLength = 10;
        // player1 = evoAgent1;


        teamZero.spinbattle.controllers.multiPlayer.ea.Agent evoAgent2 = new teamZero.spinbattle.controllers.multiPlayer.ea.Agent(linkState.copy(), timer, evoAlg, idPlayer2, nEvals);
        evoAgent2.sequenceLength = 10;
        // evoAgent2.setUseShiftBuffer(false);

        player2 = evoAgent2;


        // player2 = new Agent(linkState, timer, evoAlg2, idPlayer2, nEvals);
        // player2 = new Agent(linkState, timer, new SimpleRMHC(nResamples), idPlayer2, nEvals);

        // player1 = new Agent();
        // player1 = new Agent();
        // player2 = new controllers.multiPlayer.doNothing.Agent(linkState.copy(), timer, 1);


        runner.setPlayers(player1, player2);

        int nGames = 100;
        runner.playGames(nGames);
        System.out.println(runner.scores);


    }

}
