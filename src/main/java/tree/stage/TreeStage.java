package tree.stage;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import tree.TreeWorker;
import tree.node.NodeQWOPBase;
import tree.node.NodeQWOPExplorableBase;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * If we want to switch and change between different tree stage goals.
 *
 * @author matt
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = TreeStage_FixedGames.class, name = "fixed_games"),
        @JsonSubTypes.Type(value = TreeStage_MaxDepth.class, name = "max_depth"),
        @JsonSubTypes.Type(value = TreeStage_MinDepth.class, name = "min_depth"),
        @JsonSubTypes.Type(value = TreeStage_SearchForever.class, name = "search_forever")
})
public abstract class TreeStage implements Runnable {

    /** **/
    private NodeQWOPExplorableBase<?> stageRoot;

    /**
     * Each stage gets its own workers to avoid contamination. Probably could combine later if necessary.
     */
    private final List<TreeWorker> workers = new ArrayList<>();

    /**
     * Number of TreeWorkers to be used.
     */
    protected int numWorkers;

    /**
     * Is the managing thread of this stage running?
     */
    private volatile boolean running = true;

    /**
     * Does this stage block the main thread until done?
     */
    private boolean blocking = true;

    private final Object lock = new Object();

    private Logger logger = LogManager.getLogger(TreeStage.class);

    public void initialize(List<TreeWorker> treeWorkers, NodeQWOPExplorableBase<?> stageRoot) {
        numWorkers = treeWorkers.size();
        if (numWorkers < 1)
            throw new RuntimeException("Tried to assign a tree stage an invalid number of workers: " + numWorkers);

        workers.addAll(treeWorkers);
        this.stageRoot = stageRoot;

        for (TreeWorker tw : treeWorkers) {
            tw.setRoot(stageRoot);
            tw.startWorker();
        }

        running = true;
        Thread stageThread = new Thread(this);
        stageThread.start();

        if (blocking) {
            // This blocks the goals thread until this stage is done.
            synchronized (lock) {
                while (running) {
                    try {
                        lock.wait();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    }

    @Override
    public void run() {
        // Monitor the progress of this stage's workers.
        while (running) {
            if (checkTerminationConditions()) {
                terminate();
                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Externally check if this stage has wrapped up yet.
     */
    @JsonIgnore
    public boolean isFinished() {
        return !running;
    }

    /**
     * Query the stage for its final results.
     */
    @JsonIgnore
    public abstract List<NodeQWOPBase<?>> getResults();

    /**
     * Check through the tree for termination conditions.
     */
    public abstract boolean checkTerminationConditions();

    /**
     * Terminate this stage, destroying the workers and their threads in the process.
     */
    public void terminate() {
        running = false;
        logger.info("Terminate called on a stage.");

        // Reports any results and kills the worker.
        workers.forEach(tw -> tw.terminateStageComplete(getRootNode().getRoot(), getResults()));

        // Stop the monitoring thread and let the goals thread continue.
        synchronized (lock) {
            lock.notify();
        }
    }

    /**
     * Check if workers are running.
     */
    public boolean areWorkersRunning() {
        synchronized (workers) {
            Iterator<TreeWorker> iterator = workers.iterator();
            if (!iterator.hasNext())
                return true; // It's stupid if the termination condition gets caught before any threads have a chance to get going.
            while (iterator.hasNext()) {
                if (iterator.next().isRunning()) {
                    return true;
                }
            }
            return false;
        }
    }

    /**
     * Get the root node that this stage is operating from. It cannot change from an external caller's perspective,
     * so no set method.
     */
    @JsonIgnore
    public NodeQWOPExplorableBase<?> getRootNode() {
        return stageRoot;
    }

    @JsonIgnore
    public int getNumberOfWorkers() {
        return numWorkers;
    }
}
