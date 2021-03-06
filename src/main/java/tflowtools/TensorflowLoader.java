package tflowtools;

import com.google.common.primitives.Floats;
import edu.umd.cs.findbugs.annotations.SuppressFBWarnings;
import game.state.IState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensorflow.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;

/**
 * Basic utilities and loaders for getting TensorFlow models in here and working. Users should extend this class.
 * Specifically applies to networks which take a game state and return a single list of numbers.
 *
 * @author matt
 */
public abstract class TensorflowLoader implements AutoCloseable {

    /**
     * Current TensorFlow session.
     */
    private final Session session;

    /**
     * Computational graph loaded from TensorFlow saves.
     */
    private final Graph graph;

    private static final Logger logger = LogManager.getLogger(TensorflowLoader.class);

    /**
     * Load the computational graph from a .pb file and also make a new session.
     *
     * @param pbFile    Name of the graph save file (usually *.pb), including the file extension.
     * @param directory Directory name containing the graph save file.
     */
    @SuppressFBWarnings(value = "NP_NULL_ON_SOME_PATH_EXCEPTION", justification = "Null pointer exception is caught.")
    public TensorflowLoader(String pbFile, String directory) {
        logger.debug("Tensorflow version: " + TensorFlow.version());
        byte[] graphDef = null;
        try {
            graphDef = Files.readAllBytes(Paths.get(directory, pbFile));
        } catch (IOException e) {
            e.printStackTrace();
        }

        if (graphDef == null)
            throw new NullPointerException("TensorFlow graph was not successfully loaded.");

        graph = new Graph();
        graph.importGraphDef(graphDef);
        session = new Session(graph);
    }

    /**
     * Simple prediction from the model where we give a state and expect a list of floats out.
     * Only applies when a single output (could be multi-element though) is required from the graph.
     *
     * @param state      StateQWOP to feed into the computational graph.
     * @param inputName  Name of the graph input to shove the state into.
     * @param outputName Name of the graph output to fetch.
     * @return List of values returned by the specified graph output.
     */
    protected List<Float> sisoFloatPrediction(IState state, String inputName, String outputName) {
        Tensor<Float> inputTensor = Tensor.create(flattenState(state), Float.class);
         List<Tensor<?>> output = session.runner().feed(inputName + ":0", inputTensor)
                        .fetch(outputName + ":0")
                        .run();
        Tensor<Float> result = output.get(0).expect(Float.class);
        long[] outputShape = result.shape();

        float[] reshapedResult = result.copyTo(new float[(int) outputShape[0]][(int) outputShape[1]])[0];

        inputTensor.close();
        result.close();
        return Floats.asList(reshapedResult);
    }


    /**
     * Print out all operations in the TensorFlow graph to help determine which ones we want to use. There can be an
     * incredible number, and sometimes TensorBoard is the best way to sort this out.
     */
    @SuppressWarnings("unused")
    public void printTensorflowGraphOperations() {
        Iterator<Operation> iter = graph.operations();
        while (iter.hasNext()) {
            logger.info(iter.next().name());
        }
    }

    /**
     * Get the current TensorFlow session.
     *
     * @return A TensorFlow session object.
     */
    public Session getSession() {
        return session;
    }

    /**
     * Get the TensorFlow computational graph (ie model).
     *
     * @return The TensorFlow graph object.
     */
    public Graph getGraph() {
        return graph;
    }

    /**
     * Make a StateQWOP object into a 72-element array the way TensorFlow wants it. This method also subtracts the torso
     * x-component out of all body parts.
     *
     * @param state Input state to flatten into an array.
     * @return A 1x72 element array containing all the concatenated state variable values, with the torso x-component
     * subtracted out.
     */
    public static float[][] flattenState(IState state) {
        float[][] flatState = new float[1][];
        flatState[0] = state.flattenState();

        return flatState;
    }

    @Override
    public void close() {
        session.close();
        graph.close();
    }
}
