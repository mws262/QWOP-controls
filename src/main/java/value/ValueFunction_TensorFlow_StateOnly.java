package value;

import actions.Action;
import game.GameUnified;
import game.IGame;
import game.State;
import tree.NodeQWOP;
import tree.NodeQWOPBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.*;
import java.util.concurrent.*;

public class ValueFunction_TensorFlow_StateOnly extends ValueFunction_TensorFlow {

    private static final int STATE_SIZE = 72;
    private static final int VALUE_SIZE = 1;

    boolean multithread = true;
    private ExecutorService ex;
    private int numThreads = 9;
    List<Callable<EvaluationResult>> evaluations = new ArrayList<>();
    public EvaluationResult currentResult;

    public ValueFunction_TensorFlow_StateOnly(File file) throws FileNotFoundException {
        super(file);
        if (multithread) {
            ex = Executors.newFixedThreadPool(numThreads);
        }
    }

    public ValueFunction_TensorFlow_StateOnly(String fileName, List<Integer> hiddenLayerSizes,
                                              List<String> additionalArgs) throws FileNotFoundException {
        super(fileName, STATE_SIZE, VALUE_SIZE, hiddenLayerSizes, additionalArgs);
        if (multithread) {
            ex = Executors.newFixedThreadPool(numThreads);
        }
    }

    @Override
    public Action getMaximizingAction(NodeQWOPBase<?> currentNode) {
        State fullState = currentNode.getState();
        Objects.requireNonNull(fullState);

        List<Callable<EvaluationResult>> evaluations = new ArrayList<>();
        List<EvaluationResult> evalResults = new ArrayList<>();
        evaluations.add( // No Keys
                getCallable(fullState, currentNode, Action.Keys.none, 1, 10));
        evaluations.add( // QP
                getCallable(fullState, currentNode, Action.Keys.qp, 2, 45));
        evaluations.add( // WO
                getCallable(fullState, currentNode, Action.Keys.wo, 2, 45));
        evaluations.add( // Q
                getCallable(fullState, currentNode, Action.Keys.q, 2, 5));
        evaluations.add( // 2
                getCallable(fullState, currentNode, Action.Keys.w, 2, 5));
        evaluations.add( // O
                getCallable(fullState, currentNode, Action.Keys.o, 2, 5));
        evaluations.add( // P
                getCallable(fullState, currentNode, Action.Keys.p, 2, 5));
        // Off keys -- dunno if these are ever helpful.
        evaluations.add( // QO
                getCallable(fullState, currentNode, Action.Keys.qo, 2, 5));
        evaluations.add( // WP
                getCallable(fullState, currentNode, Action.Keys.wp, 2, 5));

        if (multithread) { // Multi-thread
            List<Future<EvaluationResult>> allResults;
            try {
                allResults = ex.invokeAll(evaluations);
                for (Future<EvaluationResult> future : allResults) {
                    evalResults.add(future.get()); // Each blocks until the thread is done.
                }
            }catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else { // Single thread
            try {
                for (Callable<EvaluationResult> eval : evaluations) {
                    evalResults.add(eval.call());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        EvaluationResult evalResult = evalResults.stream().max(EvaluationResult::compareTo).get();
        currentResult = evalResult;
        return new Action(evalResult.timestep, evalResult.keys);
    }


    private Callable<EvaluationResult> getCallable(byte[] gameStartingState, NodeQWOPBase<?> startingNode,
                                                   Action.Keys keys, int minDuration, int maxDuration) {
        boolean[] buttons = Action.keysToBooleans(keys);

        return () -> {
            GameUnified gameLocal = GameUnified.restoreFullState(gameStartingState);
            EvaluationResult bestResult = new EvaluationResult();
            for (int i = minDuration; i < maxDuration; i++) {
                gameLocal.step(buttons);
                State st = gameLocal.getCurrentState();
                NodeQWOPBase<?> nextNode = startingNode.addBackwardsLinkedChild(new Action(i, buttons), st);
                float val = evaluate(nextNode);
                if (val > bestResult.value) {
                    bestResult.value = val;
                    bestResult.timestep = i;
                    bestResult.keys = keys;
                }
            }
            return bestResult;
        };
    }

    @SuppressWarnings("Duplicates")
    private Callable<EvaluationResult> getCallable(State gameStartingState, NodeQWOPBase<?> startingNode,
                                                   Action.Keys keys, int minDuration, int maxDuration) {
        boolean[] buttons = Action.keysToBooleans(keys);

        return () -> {
            GameUnified gameLocal = new GameUnified();
            gameLocal.iterations = 15;
//            gameLocal.useWarmStarting = false;
            gameLocal.makeNewWorld();

            gameLocal.setState(gameStartingState);
            EvaluationResult bestResult = new EvaluationResult();
            //System.out.println("end " + keys.toString());
            float val1 = 0;
            float val2 = 0;
            float val3 = 0;
            for (int i = 1; i <= maxDuration; i++) {
//                gameLocal.applyBodyImpulse(-0.0005f, 0.0012f);
                if (i > 2) {
                    gameLocal.iterations = 5;
                }
                gameLocal.step(buttons);
                State st = gameLocal.getCurrentState();
                NodeQWOPBase<?> nextNode = startingNode.addBackwardsLinkedChild(new Action(i, buttons), st);
                val1 = val2;
                val2 = val3;
                val3 = evaluate(nextNode);
                if (i == 1) {
                   // val1 = val3;
                    val2 = val3 * 4f/4f;
                }
                if (i > 1 && i > minDuration) {
//                System.out.println(val);
//                    float sum = val1;
//                    if (val2 < sum) {
//                        sum = val2;
//                    }
//                    if (val3 < sum) {
//                        sum = val3;
//                    }
                    float sum = (val1 + val2 + val3)/3f;
//                    float sum = val2;
                    if (sum > bestResult.value) {
                        bestResult.value = sum;
                        bestResult.timestep = i - 1; //1; // TODO TEMP 2 for hardware. 1 is correct otherwise.
                        bestResult.keys = keys;
                        bestResult.state = st;
                    }
                }
            }
            return bestResult;
        };
    }

    private Action getBestActionFromEvaluationResults(List<EvaluationResult> results) {
        EvaluationResult evalResult = results.stream().max(EvaluationResult::compareTo).get();
        return new Action(evalResult.timestep, evalResult.keys);
    }

    @SuppressWarnings("Duplicates")
    @Override
    public Action getMaximizingAction(NodeQWOPBase<?> currentNode, IGame realGame) {
//        byte[] fullState = realGame.getFullState(); // This one has perfect state recall.
        State fullState = currentNode.getState();
        Objects.requireNonNull(fullState);

        List<Callable<EvaluationResult>> evaluations = new ArrayList<>();
        List<EvaluationResult> evalResults = new ArrayList<>();
        evaluations.add( // No Keys
                getCallable(fullState, currentNode, Action.Keys.none, 1, 15));
        evaluations.add( // QP
                getCallable(fullState, currentNode, Action.Keys.qp, 2, 55));
        evaluations.add( // WO
                getCallable(fullState, currentNode, Action.Keys.wo, 2, 55));
        evaluations.add( // Q
                getCallable(fullState, currentNode, Action.Keys.q, 2, 5));
        evaluations.add( // W
                getCallable(fullState, currentNode, Action.Keys.w, 2, 5));
        evaluations.add( // O
                getCallable(fullState, currentNode, Action.Keys.o, 2, 5));
        evaluations.add( // P
                getCallable(fullState, currentNode, Action.Keys.p, 2, 5));

        // Off keys -- dunno if these are ever helpful.
        evaluations.add( // QO
                getCallable(fullState, currentNode, Action.Keys.qo, 2, 5));
        evaluations.add( // WP
                getCallable(fullState, currentNode, Action.Keys.wp, 2, 5));

        if (multithread) { // Multi-thread
            List<Future<EvaluationResult>> allResults;
            try {
                allResults = ex.invokeAll(evaluations);
                for (Future<EvaluationResult> future : allResults) {
                    evalResults.add(future.get()); // Each blocks until the thread is done.
                }
            }catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        } else { // Single thread
            try {
                for (Callable<EvaluationResult> eval : evaluations) {
                    evalResults.add(eval.call());
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return getBestActionFromEvaluationResults(evalResults);
    }

    @Override
    float[] assembleInputFromNode(NodeQWOPBase<?> node) {
        return stateStats.standardizeState(node.getState());
    }

    @Override
    float[] assembleOutputFromNode(NodeQWOPBase<?> node) {
        return new float[]{node.value.getValue()};
    }

    /**
     * Result from evaluating the value over some futures.
     */
    public static class EvaluationResult implements Comparable<EvaluationResult> {

        /**
         * Value after the specified action.
         */
        public float value = -Float.MAX_VALUE;

        /**
         * Duration in timesteps of the Action producing this result.
         */
        public int timestep = -1;

        /**
         * Keys pressed for the Action producing this result.
         */
        public Action.Keys keys;

        public State state;

        @Override
        public int compareTo(EvaluationResult o) {
            return Float.compare(this.value, o.value);
        }
    }
}
