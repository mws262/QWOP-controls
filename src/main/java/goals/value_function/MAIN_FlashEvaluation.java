package goals.value_function;

import actions.Action;
import flashqwop.FlashGame;
import game.GameUnified;
import game.State;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.config.Configurator;
import org.jblas.util.Random;
import tree.NodeQWOP;
import tree.Utility;
import value.ValueFunction_TensorFlow;
import value.ValueFunction_TensorFlow_StateOnly;
import vision.VisionDataSaver;

import java.io.File;
import java.io.FileNotFoundException;

@SuppressWarnings("Duplicates")
public class MAIN_FlashEvaluation extends FlashGame {
    static{
        Utility.loadLoggerConfiguration();
    }
    private boolean imageCapture = false;
    private boolean addActionNoise = false;
    private float noiseProbability = 0.3f;


    private VisionDataSaver visionSaver;
    private int gameMonitorIndex = 0;
    private File captureDir = new File("vision_capture");

    // Net and execution parameters.
    String valueNetworkName = "small_net.pb"; // "deepnarrow_net.pb";
    String checkpointName = "small698"; //329"; // "med67";

    private static boolean hardware = false;

    Action[] prefix = new Action[]{
            new Action(7, Action.Keys.none),
//            new Action(40, Action.Keys.wo),
//            new Action(20, Action.Keys.qp),
//            new Action(1, Action.Keys.p),
//            new Action(19, Action.Keys.qp),
//            new Action(3, Action.Keys.wo),

    };

    private Logger logger = LogManager.getLogger(MAIN_FlashEvaluation.class);

    private ValueFunction_TensorFlow valueFunction = null;

    public MAIN_FlashEvaluation() {
        super(hardware); // Do hardware commands out?
        if (imageCapture) {
            visionSaver = new VisionDataSaver(captureDir, gameMonitorIndex);
            getServer().addStateListener(visionSaver);
        }

        loadController();

        // "Warming-up" the JVM. I think that this helps trigger JIT compilation of the correct stuff before time
        // sensitive things need to happen.
        logger.debug("Doing dummy controller evaluations as a hack to prime(?) the caches.");
        // Temporarily disable logging for the value function controller. It's not useful clogging the log with the
        // warm-up iterations.
        Configurator.setLevel(LogManager.getLogger(ValueFunction_TensorFlow_StateOnly.class).getName(), Level.OFF);
        for (int i = 0; i < 50; i++) {
            getControlAction(GameUnified.getInitialState()); // TODO make this better. The first controller evaluation ever
        }
        Configurator.setLevel(LogManager.getLogger(ValueFunction_TensorFlow_StateOnly.class).getName(), Level.INFO);
        logger.debug("Dummy evaluations complete.");

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        restart();
    }

    @Override
    public Action[] getActionSequenceFromBeginning() {
        return prefix;
    }

    @Override
    public Action getControlAction(State state) {
        Action action = valueFunction.getMaximizingAction(new NodeQWOP(state));
        if (addActionNoise && Random.nextFloat() < noiseProbability) {
            if (action.getTimestepsTotal() < 2 || Random.nextFloat() > 0.5f) {
                action = new Action(action.getTimestepsTotal() + 1, action.peek());
            } else {
                action = new Action(action.getTimestepsTotal() - 1, action.peek());
            }
        }
        return action;
    }

    @Override
    public void reportGameStatus(State state, boolean[] command, int timestep) {}

    private void loadController() {
        // Load a value function controller.
        try {
            valueFunction = new ValueFunction_TensorFlow_StateOnly(new File("src/main/resources/tflow_models" +
                    "/" + valueNetworkName)); // state_only.pb"));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        valueFunction.loadCheckpoint(checkpointName);//"small329"); // "small289"); // _after439");//273");//chk_after1");
    }

    public static void main(String[] args) {
        new MAIN_FlashEvaluation();
    }


}
