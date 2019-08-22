package tree.node;

import com.google.common.util.concurrent.AtomicDouble;
import game.action.Action;
import game.action.ActionQueue;
import data.SavableSingleGame;
import game.IGameInternal;
import game.action.Command;
import game.state.IState;
import value.updaters.IValueUpdater;
import value.updaters.ValueUpdater_HardSet;

import java.util.Collection;
import java.util.Collections;
import java.util.List;

/**
 * Most basic QWOP node. Mainly just a linked container for states, game.action, and an estimated value of the node..
 * Also inherits very basic tree functionality from {@link NodeGenericBase}. Can also handle some node importing.
 *
 * @param <N> This is essentially a recursive class parameterization. Read about f-bounded polymorphism. When using
 *            this class as an input argument, usually specify as the wildcard (?) to indicate that the class can be
 *            any inheriting implementation of this class.
 */
public abstract class NodeQWOPBase<N extends NodeQWOPBase<N, C>, C extends Command<?>> extends NodeGenericBase<N> {

    /**
     * Action taking the game from the state of the parent to this node's state.
     */
    private final Action<C> action;

    /**
     * State arrived at when taking this node's action from the parent node's state.
     */
    private final IState state;

    /**
     * A value associated with this node. This will depend greatly on the update strategy used.
     * @see IValueUpdater
     */
    private AtomicDouble value = new AtomicDouble(0);

    /**
     * Number of times that the value has been update (or in many cases, number of times the node has been "visited")
     * . This is updated automatically whenever {@link NodeQWOPBase#updateValue(float, IValueUpdater)} is called.
     */
    private int updateCount;


    public NodeQWOPBase(IState rootState) {
        super();
        action = null;
        state = rootState;
    }

    NodeQWOPBase(N parent, Action<C> action, IState state) {
        super(parent);
        this.action = action;
        this.state = state;
        // Check to make sure this node isn't already there in the parent's nodes.
        for (N parentChildren : parent.getChildren()) {
            if (parentChildren.getAction() == action) {
                throw new IllegalArgumentException("Tried to add a duplicate action node at depth " + getTreeDepth() + ". Action " +
                        "was: " + action.toString() + ".");
            }
        }
    }

    /**
     * Make a new child. Both parent and child have references to each other.
     * @param action Action taking the runner from this state to the state of the child we are creating.
     * @param state State arrived at when taking the specified action to the new node.
     * @return A new child node.
     */
    public abstract N addDoublyLinkedChild(Action<C> action, IState state);

    /**
     * Make a new child. The child has a reference to the parent, but the parent does not know about the child. This
     * is useful for doing action rollouts that we don't want to be a permanent part of the tree.
     * @param action Action taking the runner from this state to the state of the child we are creating.
     * @param state State arrived at when taking the specified action to the new node.
     * @return A new child node.
     */
    public abstract N addBackwardsLinkedChild(Action<C> action, IState state);

    public IState getState() { return state; }

    public Action<C> getAction() { return action; }

    public synchronized List<Action<C>> getSequence(List<Action<C>> actionList) {
        if (getTreeDepth() <= 0)
            throw new IndexOutOfBoundsException("Cannot get a sequence at the root node, since it has no game.action " +
                    "leading up to it.");
        actionList.clear();
        recurseUpTreeInclusiveNoRoot(n -> actionList.add(n.getAction()));
        Collections.reverse(actionList);
        return actionList;
    }

    /**
     * Add nodes based on saved action sequences. Has to re-simulate each to get the states. This should not add
     * redundant nodes if the desired nodes already exist.
     *
     * @param actions A collection of action arrays. These game.action should represent unfailing running sequences.
     * @param root The root node to add these runs under.
     * @param game Game instance used to simulate the given game.action and generate the state information needed to make
     *            the nodes.
     */
    public static <C extends Command<?>, N extends NodeQWOPBase<?, C>> void makeNodesFromActionSequences(Collection<Action<C>[]> actions, N root,
                                                                                IGameInternal<C> game) {
        IValueUpdater valueUpdater = new ValueUpdater_HardSet();
        ActionQueue<C> actQueue = new ActionQueue<>();
        for (Action<C>[] acts : actions) {
            game.makeNewWorld();
            NodeQWOPBase<?, C> currentNode = root;

            if (currentNode.getUpdateCount() == 0) {
                currentNode.updateValue(0, valueUpdater);
            }

            actQueue.clearAll();

            for (Action<C> act : acts) {
                act = act.getCopy();
                act.reset();

                // Simulate
                actQueue.addAction(act);
                while (!actQueue.isEmpty()) {
                    game.step(actQueue.pollCommand());
                }

                // If there is already a node for this action, use it.
                boolean foundExisting = false;

                for (NodeQWOPBase<?, C> child : currentNode.getChildren()) {
                    if (child.getAction().equals(act)) {
                        currentNode = child;
                        foundExisting = true;
                        break;
                    }
                }

                // Otherwise, make a new one.
                if (!foundExisting) {
                    currentNode = currentNode.addDoublyLinkedChild(act, game.getCurrentState());
                    currentNode.updateValue(0, valueUpdater); // It needs to be updated in some way. For now, just set all
                    // to zero. If update count remains at 0, bad things happen.
                }
            }
        }
    }

    /** Takes a list of runs and figures out the tree hierarchy without duplicate objects. Adds to an existing
     *  given root.
     *
     * @param runs Saved run information that will be used to create new tree nodes. This will not verify the given
     *             game.action against simulation, so if the states don't match the game.action, it will not be detected here.
     * @param existingRootToAddTo A root node to add the new tree nodes below.
     */
    public static synchronized <N extends NodeQWOPBase<N, C>, C extends Command<?>> void makeNodesFromRunInfo(Collection<SavableSingleGame> runs,
                                                                                     N existingRootToAddTo) {
        IValueUpdater valueUpdater = new ValueUpdater_HardSet();
        for (SavableSingleGame run : runs) { // Go through all runs, placing them in the tree.
            N currentNode = existingRootToAddTo;

            if (currentNode.getUpdateCount() == 0) {
                currentNode.updateValue(0, valueUpdater);
            }

            for (int i = 0; i < run.actions.length; i++) { // Iterate through individual game.action of this run,
                // travelling down the tree in the process.

                boolean foundExistingMatch = false;
                for (N child : currentNode.getChildren()) { // Look at each child of the currently investigated node.
                    if (child.getAction() == run.actions[i]) { // If there is already a node for the action we are trying
                        // to place, just use it.
                        currentNode = child;
                        foundExistingMatch = true;
                        break; // We found a match, move on.
                    }
                }
                // If this action is unique at this point in the tree, we need to add a new node there.
                if (!foundExistingMatch) {
                    currentNode = (N) currentNode.addDoublyLinkedChild(run.actions[i], run.states[i]);
                    currentNode.updateValue(0, valueUpdater); // It needs to be updated in some way. For now, just set all
                    // to zero. If update count remains at 0, bad things happen.
                }
            }
        }
    }

    /**
     * Get the total number of timesteps in the actions leading all the way from root to this node. TODO: this
     * doesn't reflect the timesteps quite right if this node is failed as the last few timesteps might not have been
     * executed.
     * @return Total action timesteps to this node.
     */
    public int getCumulativeTimesteps() {
        int timesteps = 0;
        N currentNode = getThis();
        while (currentNode.getTreeDepth() > 0) {
            timesteps += currentNode.getAction().getTimestepsTotal();
            currentNode = currentNode.getParent();
        }
        return timesteps;
    }

    /**
     * Update the value of this node based on a newly discovered value, and some update rule, {@link IValueUpdater}.
     * @param valueUpdate New value information received from a rollout or further up the tree.
     * @param updater The update rule used to change the value of this node.
     */
    public synchronized void updateValue(float valueUpdate, IValueUpdater updater) {
        value.set(updater.update(valueUpdate, this));
        updateCount++;
    }

    /**
     * Get the estimated value associated with this node.
     * @return The scalar value estimated for this node.
     */
    public float getValue() {
        return value.floatValue();
    }

    /**
     * Number of times this node's value has been updated, or number of times this node has been "visited."
     * @return Number of times this node has been visited.
     */
    public synchronized int getUpdateCount() {
        return updateCount;
    }
}
