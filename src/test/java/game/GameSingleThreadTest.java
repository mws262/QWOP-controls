package game;

import actions.Action;
import actions.ActionQueue;
import org.junit.Assert;
import org.junit.Test;

import static org.junit.Assert.*;

public class GameSingleThreadTest {

    @Test
    public void stepGame() {
        GameLoader gameMulti = new GameLoader();
        GameSingleThread gameSingle = new GameSingleThread();

        ActionQueue actionQueue = getSampleActions();

        int count = 0;
        while (!actionQueue.isEmpty()) {
            boolean[] command = actionQueue.pollCommand();
            gameMulti.stepGame(command);
            gameSingle.stepGame(command[0], command[1], command[2], command[3]);

            float[] initStateMulti = gameMulti.getCurrentState().flattenState();
            float[] initStateSingle = gameSingle.getCurrentState().flattenState();
            //System.out.println(count);
            //Assert.assertArrayEquals(initStateMulti, initStateSingle, 1e-2f);
            System.out.println(initStateMulti[1] + "," + initStateSingle[1]);
            count++;
        }
    }

    @Test
    public void getInitialState() {
//        GameLoader gameMulti = new GameLoader();
//        GameSingleThread gameSingle = new GameSingleThread();
//
//        float[] initStateMulti = gameMulti.getInitialState().flattenState();
//        float[] initStateSingle = gameSingle.getInitialState().flattenState();
//
//        Assert.assertArrayEquals(initStateMulti, initStateSingle, 1e-12f);
    }

    private static ActionQueue getSampleActions() {
        // Ran MAIN_Search_LongRun to get these. 19 steps.
        ActionQueue actionQueue = new ActionQueue();
        actionQueue.addAction(new Action(1, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(34, new boolean[]{false, true, true, false}));
        actionQueue.addAction(new Action(19, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(45, new boolean[]{true, false, false, true}));

        actionQueue.addAction(new Action(10, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(38, new boolean[]{false, true, true, false}));
        actionQueue.addAction(new Action(5, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(31, new boolean[]{true, false, false, true}));

        actionQueue.addAction(new Action(21, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(21, new boolean[]{false, true, true, false}));
        actionQueue.addAction(new Action(14, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(35, new boolean[]{true, false, false, true}));

        actionQueue.addAction(new Action(10, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(23, new boolean[]{false, true, true, false}));
        actionQueue.addAction(new Action(20, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(23, new boolean[]{true, false, false, true}));

        actionQueue.addAction(new Action(13, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(20, new boolean[]{false, true, true, false}));
        actionQueue.addAction(new Action(24, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(22, new boolean[]{true, false, false, true}));

        actionQueue.addAction(new Action(18, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(23, new boolean[]{false, true, true, false}));
        actionQueue.addAction(new Action(20, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(24, new boolean[]{true, false, false, true}));

        actionQueue.addAction(new Action(21, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(20, new boolean[]{false, true, true, false}));
        actionQueue.addAction(new Action(19, new boolean[]{false, false, false, false}));
        actionQueue.addAction(new Action(21, new boolean[]{true, false, false, true}));

        actionQueue.addAction(new Action(16, new boolean[]{false, false, false, false}));

        return actionQueue;
    }
}