package goals.perturbation_analysis;

import game.action.Action;
import game.action.ActionQueue;
import game.action.perturbers.ActionPerturber_OffsetActionTransitions;
import game.qwop.*;
import game.state.transform.ITransform;

import javax.swing.*;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MAIN_PerturbationEarlyLate extends JFrame {

    public static void main(String[] args) {
        new MAIN_PerturbationEarlyLate().run();
    }

    public void run() {

        // Use a known sample sequence.
        final ActionQueue<CommandQWOP> actionQueue = ActionQueuesQWOP.makeLongQueue();

        List<Action<CommandQWOP>> baselineActions = actionQueue.getActionsInCurrentRun();

        GameQWOP gameUnperturbed = new GameQWOP();
        GameQWOP gameEarly = new GameQWOP();
        GameQWOP gameLate = new GameQWOP();


        List<StateQWOP> baselineStates = new ArrayList<>();
        while (!actionQueue.isEmpty()) {
            gameUnperturbed.step(actionQueue.pollCommand());
            baselineStates.add(gameUnperturbed.getCurrentState());
        }

//        ITransform<StateQWOP> tform = new Transform_PCA<>(new int[]{0});
        TestingTform tform = new TestingTform();
        TestingTform tformEarly = new TestingTform();
        TestingTform tformLate = new TestingTform();

        tform.updateTransform(baselineStates);
        tformEarly.updateTransform(baselineStates);
        tformLate.updateTransform(baselineStates);

//        List<float[]> tformedBaseline = tform.transform(baselineStates);

        for (int delayAmount = 1; delayAmount < 5; delayAmount++) {
            for (int i = 11; i < baselineActions.size(); i++) { // Skip the first actions.
                actionQueue.resetQueue();
                gameUnperturbed.resetGame();
                gameEarly.resetGame();
                gameLate.resetGame();

                // Create perturbers that will offset the start time of actions forward and backward at this specified
                // action index.
                Map<Integer, Integer> earlyPerturbationSchedule = new HashMap<>();
                earlyPerturbationSchedule.put(i, delayAmount);

                Map<Integer, Integer> latePerturbationSchedule = new HashMap<>();
                latePerturbationSchedule.put(i, -delayAmount);

                ActionPerturber_OffsetActionTransitions<CommandQWOP> perturbEarly1 =
                        new ActionPerturber_OffsetActionTransitions<>(earlyPerturbationSchedule);

                ActionPerturber_OffsetActionTransitions<CommandQWOP> perturbLate1 =
                        new ActionPerturber_OffsetActionTransitions<>(latePerturbationSchedule);

                ActionQueue<CommandQWOP> perturbedEarlyQueue = perturbEarly1.perturb(actionQueue);
                ActionQueue<CommandQWOP> perturbedLateQueue = perturbLate1.perturb(actionQueue);

                tform.reset();
                tformEarly.reset();
                tformLate.reset();

                new File("./tmp" + delayAmount).mkdirs();
                try (FileWriter stateWriter = new FileWriter("./tmp" + delayAmount + "/actionDev" + i + ".txt")) {
                    while (!actionQueue.isEmpty()) {

                        // Step game without perturbations
                        CommandQWOP commandUnperturbed = actionQueue.pollCommand();
                        gameUnperturbed.step(commandUnperturbed);

                        if (!gameEarly.isFailed()) {
                            CommandQWOP commandEarly = perturbedEarlyQueue.pollCommand();
                            gameEarly.step(commandEarly);
                        }

                        if (!gameLate.isFailed()) {
                            CommandQWOP commandLate = perturbedLateQueue.pollCommand();
                            gameLate.step(commandLate);
                        }

                        // Write the timestep number.
                        stateWriter.write((gameUnperturbed.getTimestepsThisGame()) * QWOPConstants.timestep + " ");

                        float[] transformBaseline = tform.transform(gameUnperturbed.getCurrentState());
                        float[] transformEarly = tformEarly.transform(gameEarly.getCurrentState());
                        float[] transformLate = tformLate.transform(gameLate.getCurrentState());

////                         TESTING TESTING TESTING
//                        StateQWOP stEarly = gameEarly.getCurrentState().subtract(gameUnperturbed.getCurrentState());
//                        stEarly = stEarly.multiply(stEarly);
//                        StateName stChosen = StateName.TH;
//                        double sumEarly =
//                                (Math.sqrt(stEarly.getStateVariableFromName(ObjectName.BODY).getStateByName(stChosen)) +
//                                Math.sqrt(stEarly.getStateVariableFromName(ObjectName.RFOOT).getStateByName(stChosen)) +
//                                Math.sqrt(stEarly.getStateVariableFromName(ObjectName.RCALF).getStateByName(stChosen)) +
//                                Math.sqrt(stEarly.getStateVariableFromName(ObjectName.RTHIGH).getStateByName(stChosen)) +
//                                Math.sqrt(stEarly.getStateVariableFromName(ObjectName.LFOOT).getStateByName(stChosen)) +
//                                Math.sqrt(stEarly.getStateVariableFromName(ObjectName.LCALF).getStateByName(stChosen)) +
//                                Math.sqrt(stEarly.getStateVariableFromName(ObjectName.LTHIGH).getStateByName(stChosen))) / 7.;
//
//
//                        StateQWOP stLate = gameLate.getCurrentState().subtract(gameUnperturbed.getCurrentState());
//                        stLate = stLate.multiply(stLate);
//                        double sumLate =
//                                (Math.sqrt(stLate.getStateVariableFromName(ObjectName.BODY).getStateByName(stChosen)) +
//                                        Math.sqrt(stLate.getStateVariableFromName(ObjectName.RFOOT).getStateByName(stChosen)) +
//                                        Math.sqrt(stLate.getStateVariableFromName(ObjectName.RCALF).getStateByName(stChosen)) +
//                                        Math.sqrt(stLate.getStateVariableFromName(ObjectName.RTHIGH).getStateByName(stChosen)) +
//                                        Math.sqrt(stLate.getStateVariableFromName(ObjectName.LFOOT).getStateByName(stChosen)) +
//                                        Math.sqrt(stLate.getStateVariableFromName(ObjectName.LCALF).getStateByName(stChosen)) +
//                                        Math.sqrt(stLate.getStateVariableFromName(ObjectName.LTHIGH).getStateByName(stChosen))) / 7.;


//                        // Write the nominal state.
//                        stateWriter.write((gameUnperturbed.isFailed() ? "NaN" :
//                                0) + " ");
//
//                        // Early state.
//                        stateWriter.write((gameEarly.isFailed() ? "NaN" :
//                                sumEarly) + " ");
//
//                        // Late state.
//                        stateWriter.write((gameLate.isFailed() ? "NaN" :
//                                sumLate) + " ");

                        // Write the nominal state.
                        stateWriter.write((gameUnperturbed.isFailed() ? "NaN" :
                                transformBaseline[0]) + " ");

                        // Early state.
                        stateWriter.write((gameEarly.isFailed() ? "NaN" :
                                transformEarly[0]) + " ");

                        // Late state.
                        stateWriter.write((gameLate.isFailed() ? "NaN" :
                                transformLate[0]) + " ");

                        stateWriter.write('\n');
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    class TestingTform implements ITransform<StateQWOP> {
        public void reset() {
            prevSt = null;
        }
        @Override
        public void updateTransform(List<StateQWOP> statesToUpdateFrom) {}

        @Override
        public List<float[]> transform(List<StateQWOP> originalStates) {
            return null;
        }

        StateQWOP prevSt;
        @Override
        public float[] transform(StateQWOP originalState) {
//
//            if (prevSt == null) {
//                prevSt = originalState;
//                return new float[]{0f};
//            } else {
//                float zmp = originalState.approxZMP(prevSt, QWOPConstants.timestep);
//                prevSt = originalState;
//                return new float[]{zmp};
//            }

                float xdiff =
                        (float) (originalState.calcCOM().x - 0*(originalState.body.getX() + Math.sin(originalState.body.getTh()) * QWOPConstants.torsoL / 2f));
                // .getX();
                float ydiff = 0 *
                        (float) (originalState.calcCOM().y - 0*(originalState.body.getY() - Math.cos(originalState.body.getTh()) * QWOPConstants.torsoL / 2f));
            // .getX();
                // .getY();
                return new float[]{originalState.calcCOM().x}; //(float) Math.sqrt(xdiff * xdiff + ydiff * ydiff)};

            // (float) (originalState.calcCOM().x - (originalState.body.getX() + Math.sin(originalState
            // .body.getTh()) * QWOPConstants.torsoL / 2f))};
            //
            //  -
            // (originalState.rfoot.getX() -
            // originalState.lfoot.getX()) / 2f};
        }

        @Override
        public List<float[]> untransform(List<float[]> transformedStates) { return null; }

        @Override
        public List<float[]> compressAndDecompress(List<StateQWOP> originalStates) { return null; }

        @Override
        public int getOutputSize() { return 1; }

        @Override
        public String getName() { return ""; }
    };
}
