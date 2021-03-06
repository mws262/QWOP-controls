package value;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import com.google.common.collect.Iterables;
import game.action.Command;
import game.state.IState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;
import tflowtools.TrainableNetwork;
import tree.node.NodeGameBase;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ValueFunction_TensorFlow_StateOnly.class, name = "tflow_state_only"),
        @JsonSubTypes.Type(value = ValueFunction_TensorFlow_StateOnlyLoaded.class, name = "tflow_state_only_loaded"),
})
public abstract class ValueFunction_TensorFlow<C extends Command<?>, S extends IState> implements IValueFunction<C, S>,
        AutoCloseable {

    /**
     * Input layer size.
     */
    public final int inputSize;

    /**
     * Output layer size.
     */
    public final int outputSize;

    /**
     * TensorFlow neural network used to predict value.
     */
    @JsonIgnore
    public TrainableNetwork network;

    public final String fileName;
    public final List<Integer> hiddenLayerSizes;
    public List<String> additionalNetworkArgs;
    final boolean tensorboardLogging;
    /**
     * Number of nodes to run through training in one shot.
     */
    private int trainingBatchSize = 1000;

    /**
     * How many training steps to take per batch of the update data.
     */
    private int trainingStepsPerBatch = 1;

    /**
     * Number of training epochs completed since the creation of this object.
     */
    private int epochCount = 0;

    /**
     * Number of training batches completed since the start of the last update.
     */
    private int batchCount;

    /**
     * Sum of losses during one update. Exists for logging.
     */
    private float lossSum;

    private static final Logger logger = LogManager.getLogger(ValueFunction_TensorFlow.class);

    /**
     * Probability of keeping a hidden layer output during training (dropout).
     */
    @JsonProperty
    public final float keepProbability;

    /**
     * Constructor which also creates a new TensorFlow model.
     * @param fileName Name of the file to be created.
     * @param inputSize Defines the input dimension of the net.
     * @param outputSize 1D size of the output layer.
     * @param hiddenLayerSizes Size of the fully-connected interior layers.
     * @param additionalNetworkArgs Additional arguments used when creating the net (see {@link TrainableNetwork}.
     * @throws FileNotFoundException Occurs when the file is not created successfully.
     */
    ValueFunction_TensorFlow(@JsonProperty("fileName") String fileName,
                             @JsonProperty("inputSize") int inputSize,
                             @JsonProperty("outputSize") int outputSize,
                             @JsonProperty("hiddenLayerSizes") List<Integer> hiddenLayerSizes,
                             @JsonProperty("additionalNetworkArgs") List<String> additionalNetworkArgs,
                             @JsonProperty("activeCheckpoint") String activeCheckpoint,
                             @JsonProperty("keepProbability") float keepProbability,
                             @JsonProperty("tensorboardLogging") boolean tensorboardLogging)throws IOException {
        logger.info("Making a new network for the value function.");
        this.inputSize = inputSize;
        this.outputSize = outputSize;
        this.fileName = fileName;
        this.hiddenLayerSizes = hiddenLayerSizes;
        this.keepProbability = keepProbability;
        this.tensorboardLogging = tensorboardLogging;
        // Supplement the hidden layer sizes with the input and output sizes.
        List<Integer> allLayerSizes = new ArrayList<>(hiddenLayerSizes);
        allLayerSizes.add(0, inputSize);
        allLayerSizes.add(outputSize);
        this.additionalNetworkArgs = additionalNetworkArgs;

        network = TrainableNetwork.makeNewNetwork(fileName, allLayerSizes, additionalNetworkArgs, tensorboardLogging);
        // Load checkpoint if provided.
        if (activeCheckpoint != null && !activeCheckpoint.isEmpty()) {
            loadCheckpoint(activeCheckpoint);
        }
    }

    /**
     * Constructor which uses existing model.
     * @param existingModel A .pb file referring to an existing model.
     * @throws FileNotFoundException Occurs when the specified model file is not found.
     */
    ValueFunction_TensorFlow(File existingModel, float keepProbability, boolean tensorboardLogging) throws FileNotFoundException {
        logger.info("Loading existing network for the value function.");
        this.tensorboardLogging = tensorboardLogging;
        fileName = existingModel.getPath();
        network = new TrainableNetwork(existingModel, tensorboardLogging);
        int[] layerSizes = network.getLayerSizes();
        hiddenLayerSizes = new ArrayList<>();
        for (int i = 1; i < layerSizes.length - 1; i++) {
            hiddenLayerSizes.add(layerSizes[i]);
        }
        inputSize = layerSizes[0];
        outputSize = layerSizes[layerSizes.length - 1];
        this.keepProbability = keepProbability;
    }

    /**
     * Existing network. It will not be validated. Mostly for use when copying.
     * @param network Existing value network.
     */
    @SuppressWarnings("unused")
    ValueFunction_TensorFlow(TrainableNetwork network, float keepProbability) {
        logger.info("Using provided network for the value function.");
        this.fileName = network.getGraphDefinitionFile().getPath();
        this.tensorboardLogging = network.useTensorboard;
        int[] layerSizes = network.getLayerSizes();
        assert layerSizes.length >= 2;
        inputSize = layerSizes[0];
        hiddenLayerSizes = new ArrayList<>();
        outputSize = layerSizes[layerSizes.length - 1];
        for (int i = 1; i < layerSizes.length - 1; i++) {
            hiddenLayerSizes.add(layerSizes[i]);
        }
        this.network = network;
        this.keepProbability = keepProbability;
    }

    /**
     * Load a checkpoint file for the neural network. This should be in the checkpoints directory, and should not
     * include any file extensions.
     * @param checkpointName Name of the checkpoint to load.
     */
    public void loadCheckpoint(String checkpointName) throws IOException {
        assert !checkpointName.isEmpty();
        network.loadCheckpoint(checkpointName);
    }

    /**
     * Save a checkpoint file for the neural networks (basically all weights and biases). Directory automatically
     * chosen.
     * @param checkpointName Name of the checkpoint file. Do not include file extension or directory.
     * @return A list of checkpoint with that name.
     */
    public List<File> saveCheckpoint(String checkpointName) throws IOException {
        assert !checkpointName.isEmpty();
        return network.saveCheckpoint(checkpointName);
    }

    /**
     * Get the file containing the tensorflow definition of the neural network.
     * @return The .pb file holding the graph definition. Does not include weights.
     */
    @SuppressWarnings("unused")
    @JsonIgnore
    public File getGraphDefinitionFile() {
        return network.getGraphDefinitionFile();
    }

    @JsonProperty("activeCheckpoint")
    public String getActiveCheckpoint() {
        return network.getActiveCheckpoint();
    }

    /**
     * Set the number of examples fed in per batch during training.
     * @param batchSize Size of each batch. In number of examples.
     */
    public void setTrainingBatchSize(int batchSize) {
        logger.info("Training batch size set to " + batchSize + " samples.");
        trainingBatchSize = batchSize;
    }

    /**
     * Set the number of training iterations per batch.
     * @param stepsPerBatch Number of training steps taken per batch fed in.
     */
    public void setTrainingStepsPerBatch(int stepsPerBatch) {
        logger.info("Training iterations per batch set to " + stepsPerBatch + ".");
        trainingStepsPerBatch = stepsPerBatch;
    }

    @Override
    public void update(List<? extends NodeGameBase<?, C, S>> nodes) {
        assert trainingBatchSize > 0;

        batchCount = 0;
        lossSum = 0f;

        float[][] trainingInput = new float[trainingBatchSize][inputSize];
        float[][] trainingOutput = new float[trainingBatchSize][outputSize];

        long startTime = System.currentTimeMillis();

        logger.info("Beginning value function update containing " + nodes.size() + " samples divided into batches of " + trainingBatchSize);

        // Divide into batches.
        Iterables.partition(nodes, trainingBatchSize).forEach(batch -> {

            // Iterate through the nodes in the batch.
            for (int i = 0; i < batch.size(); i++) {
                NodeGameBase<?, C, S> n = batch.get(i);

                // Don't include root node since it doesn't have a parent.
                if (n.getParent() == null) {
                    continue;
                }
                // Get state.
                float[] input = assembleInputFromNode(n);
                assert input.length == inputSize;

                float[] output = assembleOutputFromNode(n);
                assert output.length == outputSize;

                trainingInput[i] = input;
                trainingOutput[i] = output;
            }
            lossSum += network.trainingStep(trainingInput, trainingOutput, keepProbability, trainingStepsPerBatch);
            batchCount++;
        });

        String logMsg = String.format("Update complete. Epoch: %d. Batches this epoch: %d. Average loss: %.4f. Total " +
                        "time elapsed: %.1f sec.",
                epochCount,
                batchCount,
                lossSum / (float) batchCount,
                (System.currentTimeMillis() - startTime) * 0.001f);
        logger.info(logMsg);
        epochCount++;
    }

    @Override
    public float evaluate(@NotNull NodeGameBase<?, C, S> node) {
        float[][] input = new float[1][inputSize];
        input[0] = assembleInputFromNode(node);
        float[][] result = network.evaluateInput(input);
        return result[0][0];
    }

    abstract float[] assembleInputFromNode(NodeGameBase<?, C, S> node);

    abstract float[] assembleOutputFromNode(NodeGameBase<?, C, S> node);

    @Override
    public void close() {
        network.close();
    }
}
