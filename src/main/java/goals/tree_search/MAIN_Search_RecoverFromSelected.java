package goals.tree_search;

import controllers.Controller_Random;
import game.action.Action;
import data.SavableActionSequence;
import data.SavableFileIO;
import data.SparseDataToDenseTFRecord;
import game.qwop.GameQWOP;
import game.IGameInternal;
import game.action.ActionGenerator_FixedSequence;
import game.qwop.CommandQWOP;
import game.qwop.StateQWOP;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tree.node.NodeGameBase;
import tree.node.NodeGameGraphics;
import tree.node.evaluator.EvaluationFunction_Constant;
import tree.node.evaluator.EvaluationFunction_Distance;
import tree.sampler.ISampler;
import tree.sampler.Sampler_UCB;
import tree.TreeWorker;
import tree.Utility;
import tree.sampler.rollout.RolloutPolicyBase;
import tree.sampler.rollout.RolloutPolicy_DeltaScore;
import value.updaters.ValueUpdater_Average;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * Does the full search in 4 stages.
 * 1. get to steady running depth.
 * 2. expand some node with a bunch of deviations from steady.
 * 3. find recoveries for each deviation.
 * 4. convert sparsely saved data to dense and save to file.
 *
 * @author matt
 */
public class MAIN_Search_RecoverFromSelected extends SearchTemplate {

    private static final Logger logger = LogManager.getLogger(MAIN_Search_RecoverFromSelected.class);

    public MAIN_Search_RecoverFromSelected() {
        super(new File("src/main/resources/config/" + "search.config_selected"));
    }

    public static void main(String[] args) {
        MAIN_Search_RecoverFromSelected manager = new MAIN_Search_RecoverFromSelected();
        manager.doGames();
    }

    public void doGames() {
        // Load all parameters specific to this search.
//        Sampler_UCB.explorationMultiplier = Float.parseFloat(properties.getProperty("UCBExplorationMultiplier", "1"));

        // Stage 1 - recovering
        int getBackToSteadyDepth = Integer.parseInt(properties.getProperty("getBackToSteadyDepth", "18")); // Stage
		// terminates after any part of the tree reaches this depth.
        float maxWorkerFraction1 = Float.parseFloat(properties.getProperty("fractionOfMaxWorkers1", "1")); // Fraction
		// of the maximum workers used for this stage.
        int bailAfterXGames1 = Integer.parseInt(properties.getProperty("bailAfterXGames1", "1000000")); // Stop stage
		// 1 after this many games even if we don't reach the goal depth.
        String fileSuffix1 = properties.getProperty("fileSuffix1", "");

        String filename1 = "controller_recovery_" + fileSuffix1;

        // Stage 2 saving
        int trimStartBy;
        int trimEndBy = Integer.parseInt(properties.getProperty("trimEndBy", "4"));

        // For extending and fixing saved games from MAIN_Controlled
        // See which files need to be covered.
        SavableFileIO<SavableActionSequence<CommandQWOP>> actionSequenceLoader = new SavableFileIO<>();
        File[] actionFiles = getSaveLocation().listFiles();
        Arrays.sort(Objects.requireNonNull(actionFiles));
        ArrayUtils.reverse(actionFiles);

        IGameInternal<CommandQWOP, StateQWOP> game = new GameQWOP();

        for (File f : actionFiles) {
            if (f.getName().contains("SavableActionSequence") && f.getName().contains("6_08")) {
                logger.info("Processing file: " + f.getName());
                List<SavableActionSequence<CommandQWOP>> actionSeq = new ArrayList<>();
                actionSequenceLoader.loadObjectsToCollection(f, actionSeq);

                List<List<Action<CommandQWOP>>> acts =
                        actionSeq.stream().map(SavableActionSequence::getActions).collect(Collectors.toList());

                trimStartBy = acts.get(0).size();

                // Recreate the tree section.
                NodeGameGraphics<CommandQWOP, StateQWOP> root = new NodeGameGraphics<>(GameQWOP.getInitialState(),
                        ActionGenerator_FixedSequence.makeDefaultGenerator(trimStartBy));
                NodeGameBase.makeNodesFromActionSequences(acts, root, game);

                // Put it on the UI.
                NodeGameGraphics.pointsToDraw.clear();
                ui.clearRootNodes();
                ui.addRootNode(root);

                List<NodeGameGraphics<CommandQWOP, StateQWOP>> leafList = new ArrayList<>();
                root.getLeaves(leafList);

                // Expand the deviated spots and find recoveries.
                logger.info("Starting leaf expansion.");
                NodeGameGraphics previousLeaf = null;

                // Should only be 1 element in leafList now. Keeping the loop for the future however.
                for (NodeGameGraphics<CommandQWOP, StateQWOP> leaf : leafList) {
                    String name = filename1 + Utility.getTimestamp();
                    doBasicMaxDepthStage(leaf, name, getBackToSteadyDepth, maxWorkerFraction1, bailAfterXGames1);

                    // Save results
                    File[] files = getSaveLocation().listFiles();
                    for (File dfiles : Objects.requireNonNull(files)) {
                        if (dfiles.toString().toLowerCase().contains(name) && !dfiles.toString().toLowerCase().contains("unsuccessful")) {
                            SparseDataToDenseTFRecord converter =
									new SparseDataToDenseTFRecord(getSaveLocation().getAbsolutePath() + "/");
                            converter.trimFirst = trimStartBy;
                            converter.trimLast = trimEndBy;
                            List<File> toConvert = new ArrayList<>();
                            toConvert.add(dfiles);
                            converter.convert(toConvert, false);
                            logger.info("Converted to TFRecord.");
                            break;
                        }
                    }
                    // Turn off drawing for this one.
                    leaf.turnOffBranchDisplay();
                    //FIXME: Fix line below since some methods have become private.
//                    leaf.parent.children.remove(leaf);
                    if (previousLeaf != null) {
                        previousLeaf.destroyNodesBelowAndCheckExplored();
                    }
                    previousLeaf = leaf;

                    logger.info("Expanded leaf.");
                }
            }
            logger.info("Stage done.");
        }
    }

    @Override
    TreeWorker<CommandQWOP, StateQWOP> getTreeWorker() {
        ISampler<CommandQWOP, StateQWOP> sampler = new Sampler_UCB<>(
                new EvaluationFunction_Constant<>(0f),
                new RolloutPolicy_DeltaScore<>(
                        new EvaluationFunction_Distance<>(),
                        RolloutPolicyBase.getQWOPRolloutActionGenerator(),
                        new Controller_Random<>()),
                new ValueUpdater_Average<>(),
                5,
                1); // TODO hardcoded.
        return TreeWorker.makeStandardQWOPTreeWorker(sampler);
    }
}
