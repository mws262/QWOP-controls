package savers;

import com.google.protobuf.ByteString;
import data.TFRecordWriter;
import game.action.Action;
import game.qwop.CommandQWOP;
import game.qwop.IStateQWOP.ObjectName;
import game.qwop.StateQWOP;
import game.state.IState;
import game.state.StateVariable6D;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.tensorflow.example.*;
import tree.node.NodeGameBase;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class DataSaver_DenseTFRecord extends DataSaver_Dense<CommandQWOP, StateQWOP> {

    private static int id_max = 0;
    private int id;
    /**
     * Filename prefix. Goes in front of date.
     */
    public final String filePrefix = "denseTF";

    /**
     * File extension. Do not include dot before.
     */
    public final String fileExtension = "TFRecord";

    /**
     * If changed, will use this. Otherwise, a timestamp is used.
     */
    public String filenameOverride = "";

    /**
     * Games since last save.
     */
    private int saveCounter = 0;

    /**
     * List of sets of states and game.command for individual games awaiting file write.
     */
    private ArrayList<GameContainer> gameData = new ArrayList<>();

    private static final Logger logger = LogManager.getLogger(DataSaver_DenseTFRecord.class);

    public DataSaver_DenseTFRecord() {
        id = id_max++;
    }
    /**
     * Finalize data stored during a run and send to file. If not called, data will not be saved. Data will be sent
     * to file every {@link DataSaver_DenseTFRecord#getSaveInterval()} games.
     *
     * @param endNode Not required. Null may be given.
     */
    @Override
    public void reportGameEnding(NodeGameBase<?, CommandQWOP, StateQWOP> endNode) {
        saveCounter++;
        List<StateQWOP> states = new ArrayList<>();
        for (IState st : stateBuffer) {
            states.add((StateQWOP) st);
        }
        gameData.add(new GameContainer(actionBuffer, states));

        if (getSaveInterval() == saveCounter) {
            toFile();
            saveCounter = 0;
        }

        // Clear out for the next run to begin.
        stateBuffer.clear();
        actionBuffer.clear();
    }

    @Override
    public void finalizeSaverData() {
        if (getSaveInterval() == 0) {
            toFile(); // If save interval is unset, then a single save at the end of the stage will be done.
        }
    }

    public void toFile() {
        try {
            convertToProtobuf();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Reset until next file write interval is up.
        gameData.clear();
    }

    // NOTE: The following methods were borrowed and altered from my MAIN_ConvertDenseDataToTFRecord class.

    /**
     * Make a single feature representing the 6 state variables for a single body part at a single timestep. Append
     * to existing FeatureList for that body part.
     * @param bodyPart Body part that the data is being given for. Comes from {@link ObjectName}.
     */
    private static void makeFeature(StateQWOP state, ObjectName bodyPart, FeatureList.Builder listToAppendTo) {
        Feature.Builder feat = Feature.newBuilder();
        FloatList.Builder featVals = FloatList.newBuilder();
        for (StateVariable6D.StateName stateName : StateVariable6D.StateName.values()) { // Iterate over all 6 state variables.
            featVals.addValue(state.getStateVariableFromName(bodyPart).getStateByName(stateName));
        }
        feat.setFloatList(featVals.build());
        listToAppendTo.addFeature(feat.build());
    }

    /**
     * Make a time series for a single run of a single state variable as a
     * FeatureList. Add to the broader list of FeatureList for this run.
     */
    private static void makeStateFeatureList(ArrayList<StateQWOP> states, ObjectName bodyPart,
											 FeatureLists.Builder featLists) {
        FeatureList.Builder featList = FeatureList.newBuilder();
        for (StateQWOP st : states) { // Iterate through the timesteps in a single run.
            makeFeature(st, bodyPart, featList);
        }
        featLists.putFeatureList(bodyPart.toString(), featList.build()); // Add this feature to the broader list of
		// features.
    }

    private void convertToProtobuf() throws IOException {
        String fullFilename;
        if (Objects.equals(filenameOverride, "")) {
            fullFilename = fileLocation + IDataSaver.generateFileName(filePrefix + "_" + id, fileExtension);
        } else {
            fullFilename = fileLocation + "/" + filenameOverride + "_" + id + "." + fileExtension;
        }
        File file = new File(fullFilename);

        if (file.getParentFile().mkdirs()) {
            logger.debug("Directory structure created: " + file.getPath());
        }

        FileOutputStream stream = new FileOutputStream(file);

        // Iterate through all runs in a single file.
        for (GameContainer dat : gameData) {
            int actionPad = dat.states.size() - dat.actions.size(); // Make the dimensions match for coding convenience.
            if (actionPad != 1) {
                logger.warn("Dimensions of state is not 1 more than dimension of command as expected. Ignoring " +
						"this one.");
                continue;
            }
            SequenceExample.Builder seqEx = SequenceExample.newBuilder();
            FeatureLists.Builder featLists = FeatureLists.newBuilder(); // All features (states & game.command) in a
			// single run.

            // Pack up states
            for (ObjectName bodyPart : ObjectName.values()) { // Make feature lists for all the body
            	// parts and add to the overall list of feature lists.
                makeStateFeatureList(dat.states, bodyPart, featLists);
            }

            // Pack up game.command -- 3 different ways:
            // 1) a Keys pressed at individual timestep.
            // 1) b Key categories pressed at individual timestep i.e. WO, QP, __ are three categories, labeled one hot.
            // 2) Timesteps until transition for each timestep.
            // 3) Just the command sequence (shorter than number of timesteps)

            // 1) a Keys pressed at individual timestep. 0 or 1 in bytes for each key
            FeatureList.Builder keyFeatList = FeatureList.newBuilder();
            for (Action<CommandQWOP> act : dat.actions) {
                Feature.Builder keyFeat = Feature.newBuilder();
                BytesList.Builder keyDat = BytesList.newBuilder();
                byte[] keys = new byte[]{
                        act.peek().get()[0] ? (byte) (1) : (byte) (0),
                        act.peek().get()[1] ? (byte) (1) : (byte) (0),
                        act.peek().get()[2] ? (byte) (1) : (byte) (0),
                        act.peek().get()[3] ? (byte) (1) : (byte) (0)};
                keyDat.addValue(ByteString.copyFrom(keys));
                keyFeat.setBytesList(keyDat.build());
                keyFeatList.addFeature(keyFeat.build());
            }
            // Pad it by one.
            Feature.Builder keyFeat = Feature.newBuilder();
            BytesList.Builder keyDat = BytesList.newBuilder();
            byte[] keys = new byte[]{(byte) 0, (byte) 0, (byte) 0, (byte) 0};
            keyDat.addValue(ByteString.copyFrom(keys));
            keyFeat.setBytesList(keyDat.build());
            keyFeatList.addFeature(keyFeat.build());

            featLists.putFeatureList("PRESSED_KEYS", keyFeatList.build());

            // 1) b Key combinations categorized, one-hot.
            FeatureList.Builder keyCatFeatList = FeatureList.newBuilder();
            for (Action<CommandQWOP> act : dat.actions) {
                Feature.Builder keyCatFeat = Feature.newBuilder();
                BytesList.Builder keyCatDat = BytesList.newBuilder();
                byte[] keysCat = new byte[]{
                        act.peek().get()[1] && act.peek().get()[2] ? (byte) (1) : (byte) (0), // WO
                        act.peek().get()[0] && act.peek().get()[3] ? (byte) (1) : (byte) (0), // QP
                        !act.peek().get()[1] && !act.peek().get()[2] && !act.peek().get()[0] && !act.peek().get()[3] ?
                                (byte) (1) :
								(byte) (0)}; // Neither
                keyCatDat.addValue(ByteString.copyFrom(keysCat));
                keyCatFeat.setBytesList(keyCatDat.build());
                keyCatFeatList.addFeature(keyCatFeat.build());
            }
            // Pad it by one.
            Feature.Builder keyCatFeat = Feature.newBuilder();
            BytesList.Builder keyCatDat = BytesList.newBuilder();
            byte[] keysCat = new byte[]{(byte) 0, (byte) 0, (byte) 0};
            keyCatDat.addValue(ByteString.copyFrom(keysCat));
            keyCatFeat.setBytesList(keyCatDat.build());
            keyCatFeatList.addFeature(keyCatFeat.build());

            featLists.putFeatureList("PRESSED_KEYS_ONE_HOT", keyCatFeatList.build());


            // 2) Timesteps until transition for each timestep.
            FeatureList.Builder transitionTSList = FeatureList.newBuilder();
            for (int i = 0; i < dat.actions.size(); i++) {
                int action = dat.actions.get(i).getTimestepsTotal();

                while (action > 0 && i < dat.actions.size()) {
                    Feature.Builder transitionTSFeat = Feature.newBuilder();
                    BytesList.Builder transitionTS = BytesList.newBuilder();
                    transitionTS.addValue(ByteString.copyFrom(new byte[]{(byte) action}));
                    transitionTSFeat.setBytesList(transitionTS.build());
                    transitionTSList.addFeature(transitionTSFeat.build());

                    action--;
                    i++;
                }
            }
            // Pad by one.
            Feature.Builder transitionTSFeat = Feature.newBuilder();
            BytesList.Builder transitionTS = BytesList.newBuilder();
            transitionTS.addValue(ByteString.copyFrom(new byte[]{(byte) 0}));
            transitionTSFeat.setBytesList(transitionTS.build());
            transitionTSList.addFeature(transitionTSFeat.build());

            featLists.putFeatureList("TIME_TO_TRANSITION", transitionTSList.build());

            // 3) Just the command sequence (shorter than number of timesteps) -- bytestrings e.g. [15, 1, 0, 0, 1]
            FeatureList.Builder actionList = FeatureList.newBuilder();
            Action<CommandQWOP> prevAct = null;
            for (Action<CommandQWOP> act : dat.actions) {
                if (!act.equals(prevAct)) {
                    prevAct = act;
                    Feature.Builder sequenceFeat = Feature.newBuilder();
                    BytesList.Builder seqList = BytesList.newBuilder();

                    byte[] actionBytes = new byte[]{(byte) act.getTimestepsTotal(),
                            act.peek().get()[0] ? (byte) (1) : (byte) (0),
                            act.peek().get()[1] ? (byte) (1) : (byte) (0),
                            act.peek().get()[2] ? (byte) (1) : (byte) (0),
                            act.peek().get()[3] ? (byte) (1) : (byte) (0)};

                    seqList.addValue(ByteString.copyFrom(actionBytes));
                    sequenceFeat.setBytesList(seqList.build());
                    actionList.addFeature(sequenceFeat.build());
                }
            }
            featLists.putFeatureList("ACTIONS", actionList.build());

            // Adding the timesteps as the context for each sequence.
            Features.Builder contextFeats = Features.newBuilder();
            Feature.Builder timestepContext = Feature.newBuilder();
            Int64List.Builder tsList = Int64List.newBuilder();
            tsList.addValue(dat.states.size()); // TOTAL number of timesteps in this run.
            timestepContext.setInt64List(tsList.build());
            contextFeats.putFeature("TIMESTEPS", timestepContext.build());

            seqEx.setContext(contextFeats.build());

            seqEx.setFeatureLists(featLists.build());
            TFRecordWriter.writeToStream(seqEx.build().toByteArray(), stream);
        }
        stream.close();
    }

    /**
     * Just a holder for the states and game.command of individual games.
     * Just so we can add these combinations to another list.
     *
     * @author matt
     */
    private static class GameContainer {

        ArrayList<Action<CommandQWOP>> actions = new ArrayList<>();
        ArrayList<StateQWOP> states = new ArrayList<>();

        private GameContainer(List<Action<CommandQWOP>> actions, List<StateQWOP> states) {
            this.actions.addAll(actions);
            this.states.addAll(states);
        }
    }

    @Override
    public DataSaver_DenseTFRecord getCopy() {
        DataSaver_DenseTFRecord newSaver = new DataSaver_DenseTFRecord();
        newSaver.setSaveInterval(getSaveInterval());
        newSaver.setSavePath(fileLocation);
        return newSaver;
    }
}
