package spinbattle.test;

import agents.dummy.RandomAgent;
import agents.evo.EvoAgent;
import core.player.AbstractMultiPlayer;
import ggi.agents.EvoAgentFactory;
import ggi.core.SimplePlayerInterface;
import gvglink.SpinBattleLinkState;
import logger.sample.DefaultLogger;
import planetwar.GVGAIWrapper;
import spinbattle.actuator.SourceTargetActuator;
import spinbattle.core.FalseModelAdapter;
import spinbattle.core.SpinGameState;
import spinbattle.log.BasicLogger;
import spinbattle.params.Constants;
import spinbattle.params.SpinBattleParams;
import spinbattle.players.HeuristicLauncher;
import spinbattle.ui.MouseSlingController;
import spinbattle.view.SpinBattleView;
import tools.ElapsedCpuTimer;
import utilities.JEasyFrame;

import java.util.Random;

public class HumanSlingVersusEvoAgent {

    public static void main(String[] args) throws Exception {
        // to always get the same initial game
        SpinBattleParams.random = new Random(8);
        SpinBattleParams params = new SpinBattleParams();

        params.maxTicks = 5000;
        params.gravitationalFieldConstant *= 1;
        params.transitSpeed *= 1;
        SpinGameState gameState = new SpinGameState().setParams(params).setPlanets();
        gameState.actuators[0] = new SourceTargetActuator().setPlayerId(0);
        gameState.actuators[1] = new SourceTargetActuator().setPlayerId(1);
        BasicLogger basicLogger = new BasicLogger();
        // gameState.setLogger(new DefaultLogger());
        SpinBattleView view = new SpinBattleView().setParams(params).setGameState(gameState);
        String title = "Spin Battle Game" ;
        JEasyFrame frame = new JEasyFrame(view, title + ": Waiting for Graphics");
        frame.setLocation(400, 100);
        // MouseSlingController mouseSlingController = new MouseSlingController();
        // mouseSlingController.setGameState(gameState).setPlayerId(Constants.playerOne);
        // view.addMouseListener(mouseSlingController);
        waitUntilReady(view);

        // SimplePlayerInterface evoAgent = new EvoAgentFactory().getAgent().setVisual();
        // SpinBattleParams falseParams = new SpinBattleParams();
        // falseParams.transitSpeed = 0;
        // falseParams.gravitationalFieldConstant = 0;
        // evoAgent = new FalseModelAdapter().setParams(falseParams).setPlayer(evoAgent);

        SimplePlayerInterface mctsAgent1 = getMCTSAgent(gameState, Constants.playerOne, false);
        SimplePlayerInterface mctsAgent2 =  getMCTSAgent(gameState, Constants.playerTwo, false);

        int[] actions = new int[2];


        for (int i=0; i<=10000 && !gameState.isTerminal(); i++) {
            int action_1 = mctsAgent1.getAction(gameState.copy(), 1);
            int action_2 = mctsAgent2.getAction(gameState.copy(), 2);
            System.out.println("P1: " + action_1 + "\tP2: " + action_2);
            actions[0] = action_1;
            actions[1] = action_2;
            gameState.next(actions);
            // mouseSlingController.update();
            // launcher.makeTransits(gameState, Constants.playerOne);
            view.setGameState((SpinGameState) gameState.copy());
            view.repaint();
            frame.setTitle(title + " : " + i); //  + " : " + CaveView.getTitle());
            Thread.sleep(50);
        }
        System.out.println(gameState.isTerminal());
    }

    static void waitUntilReady(SpinBattleView view) throws Exception {
        int i = 0;
        while (view.nPaints == 0) {
            // System.out.println(i++ + " : " + CaveView.nPaints);
            Thread.sleep(50);
        }
    }

    static GVGAIWrapper getMCTSAgent(SpinGameState gameState, int playerId, boolean reuseTree){
        ElapsedCpuTimer timer = new ElapsedCpuTimer();
        SpinBattleLinkState linkState = new SpinBattleLinkState(gameState);
        AbstractMultiPlayer agent;
        if (reuseTree) {
            agent = new controllers.multiPlayer.treeReusageDiscountOLMCTS.discountOLMCTS.Agent(linkState.copy(), timer, playerId);
        } else {
            agent = new controllers.multiPlayer.discountOLMCTS.Agent(linkState.copy(), timer, playerId);
        }

        GVGAIWrapper wrapper = new GVGAIWrapper().setAgent(agent);
        return wrapper;
    }
}
