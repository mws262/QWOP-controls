package game.qwop;

import game.action.ActionQueue;
import game.qwop.IStateQWOP.ObjectName;
import game.state.IState;
import org.junit.Assert;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.*;

public class GameQWOPTest {

    @Test
    public void makeNewWorld() {
        float[] initState = GameQWOP.getInitialState().flattenState();

        GameQWOP game = new GameQWOP();
        float[] currState = game.getCurrentState().flattenState();
        for (int i = 0; i < initState.length; i++) {
            Assert.assertEquals(initState[i], currState[i], 1e-12);
        }
        game.step(true, false, true, false);
        currState = game.getCurrentState().flattenState();
        Assert.assertEquals(1, game.getTimestepsThisGame());

        float stateDiff = 0;
        for (int i = 0; i < initState.length; i++) {
            stateDiff += Math.abs(initState[i] - currState[i]);
        }
        Assert.assertTrue(stateDiff > 1e-10);

        game.holdKeysForTimesteps(100, false, false, true, false);

        Assert.assertTrue(game.isFailed());

        game.resetGame();
        Assert.assertEquals(0, game.getTimestepsThisGame());
        Assert.assertFalse(game.isFailed());
        currState = game.getCurrentState().flattenState(); // Should be back to the initial state now.
        for (int i = 0; i < initState.length; i++) {
            Assert.assertEquals(initState[i], currState[i], 1e-12);
        }
    }

    @Test
    public void stepGame() {
        // TODO bad casts here.
        // Hard to test against "ground truth." Mostly going to make sure it's error free and that there aren't any
        // huge logical problems.
        GameQWOP game = new GameQWOP();
        float bodyTh = ((StateQWOP) game.getCurrentState()).getStateVariableFromName(ObjectName.BODY).getTh();
        Assert.assertEquals(0, game.getTimestepsThisGame());

        game.step(true, true, false, false);
        float bodyThNext = ((StateQWOP)game.getCurrentState()).getStateVariableFromName(ObjectName.BODY).getTh();
        Assert.assertNotEquals(bodyTh, bodyThNext, 0.0); // States should change after step().
        bodyTh = bodyThNext;
        Assert.assertEquals(1, game.getTimestepsThisGame()); // Counter should have advanced.

        game.step(true, false, true, false);
        bodyThNext = ((StateQWOP)game.getCurrentState()).getStateVariableFromName(ObjectName.BODY).getTh();
        Assert.assertNotEquals(bodyTh, bodyThNext, 0.0);
        Assert.assertEquals(2, game.getTimestepsThisGame());
    }

    @Test
    public void stepGame1() {
        // TODO bad casts in here.
        GameQWOP game1 = new GameQWOP();
        GameQWOP game2 = new GameQWOP();

        game1.step(true, false, true, false);
        game2.step(CommandQWOP.QO);

        IState gameState1 = game1.getCurrentState();
        IState gameState2 = game2.getCurrentState();

        Assert.assertEquals(gameState1.getCenterX(), gameState2.getCenterX(), 1e-12);
        Assert.assertEquals(((StateQWOP) gameState1).getStateVariableFromName(ObjectName.RTHIGH).getTh(),
                ((StateQWOP) gameState2).getStateVariableFromName(ObjectName.RTHIGH).getTh(),
                1e-12);
        Assert.assertEquals(((StateQWOP) gameState1).getStateVariableFromName(ObjectName.LUARM).getY(),
                ((StateQWOP) gameState2).getStateVariableFromName(ObjectName.LUARM).getY(),
                1e-12);
    }

    @Test
    public void setState() {
        GameQWOP game1 = new GameQWOP();
        GameQWOP game2 = new GameQWOP();

        game1.holdKeysForTimesteps(10, true, false, true, false);

        StateQWOP gameState1 = game1.getCurrentState();
        game2.setState(gameState1);
        float[] gameState2f = game2.getCurrentState().flattenState();
        float[] gameState1f = gameState1.flattenState();

        for (int i = 0; i < gameState1f.length; i++) {
            Assert.assertEquals(gameState1f[i], gameState2f[i], 1e-12);
        }
    }

    @Test
    public void getFailureStatus() {
        GameQWOP game = new GameQWOP();
        Assert.assertFalse(game.isFailed());
        game.holdKeysForTimesteps(100, false, false, true, false);
        Assert.assertTrue(game.isFailed());
    }

    @Test
    public void isRightFootDown() {
        GameQWOP game = new GameQWOP();
        Assert.assertFalse(game.isRightFootDown());
        game.step(false,false,false,false);
        Assert.assertTrue(game.isRightFootDown());
    }

    @Test
    public void isLeftFootDown() {
        GameQWOP game = new GameQWOP();
        Assert.assertFalse(game.isLeftFootDown());
        game.step(false,false,false,false);
        Assert.assertTrue(game.isLeftFootDown());
    }

    @Test
    public void getInitialState() {
        float[] initState = GameQWOP.getInitialState().flattenState();

        GameQWOP game = new GameQWOP();
        game.step(true,true,true,true);
        float[] initStateAgain = GameQWOP.getInitialState().flattenState(); // Make sure a second call gets the same
        // thing, even after the game has stepped a bit.

        for (int i = 0; i < initState.length; i++) {
            Assert.assertEquals(initState[i], initStateAgain[i], 1e-12);
        }
        Assert.assertEquals(initState.length, initStateAgain.length);
    }

    @Test
    public void holdKeysForTimesteps() {
        int numTs = 7;
        GameQWOP game1 = new GameQWOP();
        GameQWOP game2 = new GameQWOP();

        game1.holdKeysForTimesteps(numTs, false, true, false, false);
        for (int i = 0; i < numTs; i++) {
            game2.step(false, true, false, false);
        }

        Assert.assertArrayEquals(game2.getCurrentState().flattenState(), game1.getCurrentState().flattenState(),
                1e-15f);
    }

    @Test
    public void getTimestepsSimulatedThisGame() {
        GameQWOP game1 = new GameQWOP();
        GameQWOP game2 = new GameQWOP();

        Assert.assertEquals(0, game1.getTimestepsThisGame());
        Assert.assertEquals(0, game2.getTimestepsThisGame());

        game1.step(false, true, false, true);
        Assert.assertEquals(1, game1.getTimestepsThisGame());
        Assert.assertEquals(0, game2.getTimestepsThisGame());
        game2.step(true, false, false, false);
        Assert.assertEquals(1, game2.getTimestepsThisGame());

        game2.holdKeysForTimesteps(5, true, true, true, true);

        Assert.assertEquals(6, game2.getTimestepsThisGame());
        Assert.assertEquals(1, game1.getTimestepsThisGame());

    }

    @Test
    public void adjustRealQWOPStateToSimState() { //TODO need to fix the actual method first.
    }

    // Revisions to the game have occurred and the save is no longer a valid test. Once the game changes have
    // solidified, I should TODO add another

//    /**
//     * This test uses some saved data, with states at every timestep to make sure that the simulation can still
//     * reproduce it when fed the same commands. This alerts us if any changes have altered the behavior of GameThreadSafe
//     * without our knowledge.
//     */
//    @Test
//    public void testForAccidentalChanges() {
//        File exampleRunFile = new File("src/test/resources/saved_data_examples/example_run.TFRecord");
//        List<SequenceExample> dataSeries = null;
//        try {
//            dataSeries = TFRecordDataParsers.loadSequencesFromTFRecord(exampleRunFile);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//
//        Assert.assertEquals(1, Objects.requireNonNull(dataSeries).size()); // This example data should contain one run.
//        StateQWOP[] loadedStates = TFRecordDataParsers.getStatesFromLoadedSequence(dataSeries.get(0));
//        List<Action> loadedActions = TFRecordDataParsers.getActionsFromLoadedSequence(dataSeries.get(0));
//
//        GameQWOP game = new GameQWOP();
//        ActionQueue actionQueue = new ActionQueue();
//        actionQueue.addSequence(loadedActions);
//
//        int count = 0;
//        while (!actionQueue.isEmpty()) {
//            float[] stSim = game.getCurrentState().flattenState();
//            float[] stLoad = loadedStates[count].flattenState();
//
//            for (int i = 0; i < stSim.length; i++) {
//                Assert.assertEquals(stLoad[i], stSim[i], 1e-10);
//            }
//
//            boolean[] command = actionQueue.pollCommand();
//            game.step(command);
//            count++;
//        }
//    }

    @Test
    public void getToSameEndAfterReload() {
        GameQWOP gameSingle = new GameQWOP();

        // Run through the full queue with no saving/loading
        ActionQueue<CommandQWOP> actions = ActionQueuesQWOP.makeShortQueue();
        while (!actions.isEmpty()) {
            gameSingle.step(actions.pollCommand());
        }
        IState stateEndNoLoad = gameSingle.getCurrentState();

        // Redo with save/load in the middle.
        gameSingle.resetGame();
        actions = ActionQueuesQWOP.makeShortQueue();
        for (int i = 0; i < 30; i++) {
            gameSingle.step(actions.pollCommand());
        }

        IState stateBeforeLoad = gameSingle.getCurrentState();

        // Save
        byte[] fullState = gameSingle.getSerializedState();

        // Step forward arbitrarily.
        gameSingle.holdKeysForTimesteps(10, true, false, false, true);

        // Load
        gameSingle = gameSingle.restoreSerializedState(fullState);
        IState stateAfterLoad = gameSingle.getCurrentState();

        // Make sure states at save and after load are equal.
        Assert.assertArrayEquals(stateBeforeLoad.flattenState(), stateAfterLoad.flattenState(), 1e-15f);

        // Finish the queue on the loaded game.
        while (!actions.isEmpty()) {
            gameSingle.step(actions.pollCommand());
        }

        IState stateEndAfterLoad = gameSingle.getCurrentState();

        Assert.assertArrayEquals(stateEndNoLoad.flattenState(), stateEndAfterLoad.flattenState(), 1e-15f);

    }

    @Test
    public void forkingGameAndContinuingToTheSameEnd() {
        // Make sure that a single game can create a restored copy such that both are consistent with each other, but
        // don't affect each other's results.
        GameQWOP game = new GameQWOP();

        game.holdKeysForTimesteps(10, false, true, true, false);

        IState stateAtSave = game.getCurrentState();
        byte[] gameSave = game.getSerializedState();

        game.holdKeysForTimesteps(10, true, false, false, true);
        IState stateAfter10 = game.getCurrentState();

        GameQWOP gameRestored = game.restoreSerializedState(gameSave);
        IState stateAtRestore = gameRestored.getCurrentState();
        gameRestored.holdKeysForTimesteps(10, true, false, false, true);

        IState stateReloadAfter10 = gameRestored.getCurrentState();

        Assert.assertArrayEquals(stateAtSave.flattenState(), stateAtRestore.flattenState(), 1e-15f);
        Assert.assertArrayEquals(stateAfter10.flattenState(), stateReloadAfter10.flattenState(), 1e-15f);

        for (int i = 0; i < 10; i++) {
            game.step(false, true, true, false);
            gameRestored.step(false, true, true, false);
        }

        Assert.assertArrayEquals(game.getCurrentState().flattenState(), gameRestored.getCurrentState().flattenState()
                , 1e-15f);
    }

    @Test
    public void branchingGameLoad() {
        // Also make sure that a bunch of things loading from the SAME thing are ok.
        GameQWOP game = new GameQWOP();
        game.holdKeysForTimesteps(10, false, true, true, false);

        byte[] gameSave = game.getSerializedState();

        Callable<IState> sim = () -> {
            GameQWOP gameForLoading = game.restoreSerializedState(gameSave);
            gameForLoading.holdKeysForTimesteps(10, false, true, false, true);
            return gameForLoading.getCurrentState();
        };

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Callable<IState>> sims = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            sims.add(sim);
        }

        List<Future<IState>> results = null;
        try {
            results = executorService.invokeAll(sims);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        float[] stateComparison = null;
        for (Future<IState> f : Objects.requireNonNull(results)) {
            try {
                IState s = f.get();
                if (stateComparison == null) {
                    stateComparison = s.flattenState();
                } else {
                    Assert.assertArrayEquals(stateComparison, s.flattenState(), 1e-15f);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }

    @Test
    public void simultaneousGameLoad() {

        // Make sure that a bunch of things loading and saving at the same time are ok.
        Callable<IState> sim = () -> {
            GameQWOP game = new GameQWOP();
            game.holdKeysForTimesteps(10, false, true, true, false);

            byte[] gameSave = game.getSerializedState();

            game.holdKeysForTimesteps(10, true, false, false, false);

            GameQWOP gameLoaded = game.restoreSerializedState(gameSave);

            gameLoaded.holdKeysForTimesteps(10, true, false, false, false);

            IState s = game.getCurrentState();
            float[] s1 = s.flattenState();
            float[] s2 = gameLoaded.getCurrentState().flattenState();
            Assert.assertArrayEquals(s1, s2, 1e-15f);

            return s;
        };

        ExecutorService executorService = Executors.newFixedThreadPool(5);
        List<Callable<IState>> sims = new ArrayList<>();
        for (int i = 0; i < 5; i++) {
            sims.add(sim);
        }

        List<Future<IState>> results = null;
        try {
            results = executorService.invokeAll(sims);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        float[] stateComparison = null;
        for (Future<IState> f : Objects.requireNonNull(results)) {
            try {
                IState s = f.get();
                if (stateComparison == null) {
                    stateComparison = s.flattenState();
                } else {
                    Assert.assertArrayEquals(stateComparison, s.flattenState(), 1e-15f);
                }
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
    }
//    private static ActionQueue getSampleActions() {
//        // Ran MAIN_Search_LongRun to get these. 19 steps.
//        ActionQueue actionQueue = new ActionQueue();
//        actionQueue.addAction(new Action(1, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(34, new boolean[]{false, true, true, false}));
//        actionQueue.addAction(new Action(19, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(45, new boolean[]{true, false, false, true}));
//
//        actionQueue.addAction(new Action(10, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(38, new boolean[]{false, true, true, false}));
//        actionQueue.addAction(new Action(5, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(31, new boolean[]{true, false, false, true}));
//
//        actionQueue.addAction(new Action(21, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(21, new boolean[]{false, true, true, false}));
//        actionQueue.addAction(new Action(14, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(35, new boolean[]{true, false, false, true}));
//
//        actionQueue.addAction(new Action(10, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(23, new boolean[]{false, true, true, false}));
//        actionQueue.addAction(new Action(20, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(23, new boolean[]{true, false, false, true}));
//
//        actionQueue.addAction(new Action(13, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(20, new boolean[]{false, true, true, false}));
//        actionQueue.addAction(new Action(24, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(22, new boolean[]{true, false, false, true}));
//
//        actionQueue.addAction(new Action(18, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(23, new boolean[]{false, true, true, false}));
//        actionQueue.addAction(new Action(20, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(24, new boolean[]{true, false, false, true}));
//
//        actionQueue.addAction(new Action(21, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(20, new boolean[]{false, true, true, false}));
//        actionQueue.addAction(new Action(19, new boolean[]{false, false, false, false}));
//        actionQueue.addAction(new Action(21, new boolean[]{true, false, false, true}));
//
//        actionQueue.addAction(new Action(16, new boolean[]{false, false, false, false}));
//
//        return actionQueue;
//    }
}
