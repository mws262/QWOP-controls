package tflowtools;

import org.tensorflow.*;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

/**
 * A wrapper for creating, training, and evaluating TensorFlow models. Normally the syntax is a pain in Java, so this
 * just keeps me from having to remember all of it.
 *
 * @author matt
 */
public class TrainableNetwork {

    /**
     * Networks are not created in Java. Java calls a python script defined here to create the network.
     */
    public static final String pythonGraphCreatorScript = "python/java_value_function/create_generic_graph.py";

    /**
     * Default location from which to save/load Tensorflow models.
     */
    public static String graphPath = "src/main/resources/tflow_models/";

    /**
     * Default location from which to save/load Tensorflow checkpoint files.
     */
    public String checkpointPath = "src/main/resources/tflow_models/checkpoints";

    /**
     * .pb file defining the structure (but not yet weights) of the network.
     */
    public final File graphDefinition;

    /**
     * Loaded Tensorflow graph definition.
     */
    private final Graph graph;

    /**
     * Loaded Tensorflow session.
     */
    private final Session session;

    /**
     * Create a new wrapper for an existing Tensorflow graph.
     *
     * @param graphDefinition Graph definition file.
     */
    public TrainableNetwork(File graphDefinition) {
        this.graphDefinition = graphDefinition;

        // Begin loading the structure of the model as defined by the graph file.
        byte[] graphDef = null;
        try {
            graphDef = Files.readAllBytes(graphDefinition.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Put the graph together and potentially load a previous checkpoint.
        graph = new Graph();
        session = new Session(graph);
        graph.importGraphDef(graphDef);

        // Initialize
        session.runner().addTarget("init").run(); // TODO could be removed if it proves too slow and we only care
        // about loading rather than initializing from scratch.
    }

    /**
     * Run training on a set of data.
     *
     * @param inputs One or more sets of inputs. First dimension is the example number. Second dimension is the size of
     *               each example.
     * @param desiredOutputs Outputs which we want inputs to produce.
     * @param steps Number of training steps (some form of gradient descent) to use on this set of inputs.
     * @return The loss of the last step performed (smaller is better).
     */
    public float trainingStep(float[][] inputs, float[][] desiredOutputs, int steps) {
        Tensor<Float> input = Tensors.create(inputs);
        Tensor<Float> value_out = Tensors.create(desiredOutputs);
        Tensor<Float> output = null;
        for (int i = 0; i < steps; i++) {
            output = session.runner().feed("input", input).feed("output_target", value_out).addTarget("train").fetch(
                    "loss").run().get(0).expect(Float.class);
        }
        assert output != null;
        return output.floatValue();
    }

    /**
     * Save training progress (i.e. weights and biases) to specific checkpoint file. Will still be saved to the
     * checkpoint directory. Do not include file extension.
     */
    public void saveCheckpoint(String checkpointName) {
        Tensor<String> checkpointTensor = Tensors.create(Paths.get(checkpointPath, checkpointName).toString());
        session.runner().feed("save/Const", checkpointTensor).addTarget("save/control_dependency").run();
    }

    /**
     * Load a checkpoint file. Must match the graph loaded. Name does not need to include path or file extension.
     * @param checkpointName Name of the checkpoint to load.
     */
    public void loadCheckpoint(String checkpointName) {
        Tensor<String> checkpointTensor = Tensors.create(Paths.get(checkpointPath, checkpointName).toString());
        session.runner().feed("save/Const", checkpointTensor).addTarget("save/restore_all").run();
    }

    /**
     * Evaluate one or more inputs to the net. Does not perform any training.
     * @param inputs One or more inputs to feed in. For a single example, the first dimension should be 1.
     * @return The evaluated values of the inputs.
     */
    public float[][] evaluateInput(float[][] inputs) {
        Tensor<Float> inputTensor = Tensors.create(inputs);
        Tensor<Float> outputTensor =
                session.runner().feed("input", inputTensor).fetch("output").run().get(0).expect(Float.class);

        long[] outputShape = outputTensor.shape();
        float[][] output = new float[(int) outputShape[0]][(int) outputShape[1]];
        outputTensor.copyTo(output);
        return output;
    }

    /**
     * Print all operations in the graph for debugging.
     */
    public void printGraphOperations() {
        Iterator<Operation> iter = graph.operations();
        while (iter.hasNext()) {
            System.out.println(iter.next().name());
        }
    }

    /**
     * Create a new fully-connected neural network structure. This calls a python script to make the net.
     *
     * @param graphName      Name of the graph file (without path or file extension).
     * @param layerSizes     List of layer sizes. Make sure that the first and last layers match the inputs and desired
     *                       output sizes.
     * @param additionalArgs Additional inputs defined by the python script. Make sure that the list is separated out
     *                       by each word in the command line.
     * @return A new TrainableNetwork based on the specifications.
     */
    public static TrainableNetwork makeNewNetwork(String graphName, List<Integer> layerSizes,
                                                  List<String> additionalArgs) {
        // Add all command line arguments for the python script to a list.
        List<String> commandList = new ArrayList<>();
        commandList.add("python");
        commandList.add(pythonGraphCreatorScript);
        commandList.add("--layers");
        commandList.addAll(layerSizes.stream().map(String::valueOf).collect(Collectors.toList()));
        commandList.add("--savepath");
        commandList.add(graphPath + graphName + ".pb");
        commandList.addAll(additionalArgs);

        // Make and run the command line process.
        ProcessBuilder pb = new ProcessBuilder(commandList.toArray(new String[commandList.size()]));
        pb.redirectOutput(ProcessBuilder.Redirect.INHERIT); // Makes sure that error messages and outputs go to console.
        pb.redirectError(ProcessBuilder.Redirect.INHERIT);

        try {
            Process p = pb.start();
            p.waitFor(); // Make sure it has finished before moving on.
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
        }

        // Send the newly-created graph file to a new TrainableNetwork object.
        File graphFile = new File(graphPath + graphName + ".pb");
        assert graphFile.exists();

        return new TrainableNetwork(graphFile);
    }

    public static TrainableNetwork makeNewNetwork(String graphName, List<Integer> layerSizes) {
        return makeNewNetwork(graphName, layerSizes, new ArrayList<>());
    }
}
