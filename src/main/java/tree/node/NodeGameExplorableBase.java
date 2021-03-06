package tree.node;

import distributions.Distribution;
import game.action.*;
import game.state.IState;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Expands on basic QWOP data storage ({@link NodeGameBase}) and tree functionality ({@link NodeGenericBase}) to provide
 * basic tree exploration functions, like the concepts of untested game.command, fully-explored nodes, and branches locked
 * for exploration in a multi-threaded scenario.
 *
 * @param <N> This is essentially a recursive class parameterization. Read about f-bounded polymorphism. When using
 *            this class as an input argument, usually specify as the wildcard (?) to indicate that the class can be
 *            any inheriting implementation of this class.
 *
 */
public abstract class NodeGameExplorableBase<
        N extends NodeGameExplorableBase<N, C, S>,
        C extends Command<?>,
        S extends IState> extends NodeGameBase<N, C, S> {

    /**
     * Actions which could be executed from this node's state to make new children. These are assigned at the
     * creation of the node by a {@link IActionGenerator}.
     */
    private ActionList<C> untriedActions;

    /**
     * Are there any untried things below this node? This is not necessarily a TERMINAL node, it is simply a node
     * past which there are no potential child game.command to try.
     */
    private final AtomicBoolean fullyExplored = new AtomicBoolean(false);

    /**
     * If one TreeWorker is expanding from this leaf node (if it is one), then no other worker should try to
     * simultaneously expand from here too.
     */
    private final AtomicBoolean locked = new AtomicBoolean(false);

    /**
     * Action generator used to make the set of untried child game.command for this node. This is used during the creation
     * of this node and not again by this node. When a new child is created it will 'inherit' this generator by
     * default, unless another one is given.
     */
    final IActionGenerator<C> actionGenerator;

    private static final Logger logger = LogManager.getLogger(NodeGameExplorableBase.class);

    /**
     * Create a new root node. It will have potential child game.command assigned to it by the specified
     * {@link IActionGenerator}.
     *
     * @param rootState {@link IState} at this root node.
     * @param actionGenerator Used to generate untried child game.command to assign to root.
     */
    public NodeGameExplorableBase(@NotNull S rootState, @NotNull IActionGenerator<C> actionGenerator) {
        super(rootState);
        this.actionGenerator = actionGenerator;
        if (rootState.isFailed()) {
            untriedActions = ActionList.getEmptyList();
        } else {
            untriedActions = actionGenerator.getPotentialChildActionSet(this);
        }
        locked.set(false);
    }

    /**
     * Make a new root node. It will have no untried children assigned to it, and by default, all manually-added
     * children will also have no untried children assigned. For the normal version, please use
     * {@link NodeGameExplorableBase#NodeGameExplorableBase(IState, IActionGenerator)}.
     *
     * @param rootState {@link IState} at this root node.
     */
    public NodeGameExplorableBase(@NotNull S rootState) {
        this(rootState, new ActionGenerator_Null<>());
    }

    /**
     * Create a new node given a parent node, an command which takes the game from the parent to this node, and the
     * state at this node. The new node will be assigned potential child game.command based on the specified command
     * generator.
     *
     * For external users, it is much preferred to use
     * {@link NodeGameExplorableBase#addDoublyLinkedChild(Action, IState, IActionGenerator)}  or
     * {@link NodeGameExplorableBase#addBackwardsLinkedChild(Action, IState, IActionGenerator)}.
     *
     * @param parent Parent node to this newly created one. By default, the parent will not know about this node.
     * @param action Action taking the game's state from the parent node to this new node.
     * @param state StateQWOP at this new node.
     * @param actionGenerator Assigns the potential game.command to try to this new node.
     */
    NodeGameExplorableBase(N parent, Action<C> action, S state, IActionGenerator<C> actionGenerator,
                           boolean doublyLinked) {
        super(parent, action, state);
        this.actionGenerator = actionGenerator;
        if (doublyLinked)
            parent.addToChildList(getThis());
        if (state.isFailed()) {
            untriedActions = ActionList.getEmptyList();
            if (doublyLinked) {
                propagateFullyExploredStatusLite();
            } else {
                setFullyExploredStatus(true);
            }
        } else {
            untriedActions = actionGenerator.getPotentialChildActionSet(this);
        }
    }

    /**
     * Add a child node containing the {@link IState} achieved when executing the specified {@link Action}. New
     * untried game.command will be assigned to this new child based on the rules of the provided {@link IActionGenerator}.
     *
     * This is the "normal" child adder. The created child will have a reference to its parent (this), and this will
     * have the new child in its list of children.
     *
     * @see NodeGameExplorableBase#addBackwardsLinkedChild(Action, IState)
     * @see NodeGameExplorableBase#addDoublyLinkedChild(Action, IState, IActionGenerator)
     *
     * @param action Action which leads from this node to the child node.
     * @param state StateQWOP reached after taking the specified command from this node.
     * @param actionGenerator Generator which provides a new set of untried game.command to the child node.
     * @return A newly-created child node which has references to its parent (this), and this has references to it as
     * a child.
     */
    public abstract N addDoublyLinkedChild(Action<C> action, S state, IActionGenerator<C> actionGenerator);

    /**
     * Add a child node containing the {@link IState} achieved when executing the specified {@link Action}. New
     * untried game.command will be assigned to this new child based on the rules of the provided {@link IActionGenerator}.
     *
     * The newly created child will have a reference to its parent (this), but this node will not be aware of the
     * child, nor will the child command be removed from untried child game.command (if present). This is useful for
     * creating transient nodes that we don't want to become part of the tree permanently.
     *
     * @see NodeGameExplorableBase#addDoublyLinkedChild(Action, IState, IActionGenerator)
     * @see NodeGameExplorableBase#addBackwardsLinkedChild(Action, IState)
     *
     * @param action Action which leads from this node to the child node.
     * @param state StateQWOP reached after taking the specified command from this node.
     * @param actionGenerator Generator which provides a new set of untried game.command to the child node.
     * @return A newly-created child node which has a reference to its parent (this), but is unknown to the parent.
     */
    public abstract N addBackwardsLinkedChild(Action<C> action, S state, IActionGenerator<C> actionGenerator);

    /**
     * Get whether this node is marked as being fully-explored. This will occur if all potential descendents are also
     * fully-explored or failed, or this node itself is failed.
     * @return Whether this node is marked as fully-explored.
     */
    public boolean isFullyExplored() {
        return fullyExplored.get();
    }

    /**
     * Label this node as fully-explored. This should only happen when all potential descendents are exhausted or
     * failed. Best to not expose this method as the potential for bugs is too high.
     * @param fullyExploredStatus Whether this node is marked as fully-explored.
     */
    void setFullyExploredStatus(boolean fullyExploredStatus) {
        // Double check that conditions are met before marking.
        if (fullyExploredStatus) {
            assert getUntriedActionCount() == 0 || getState().isFailed();
            for (N child : getChildren()) {
                assert child.isFullyExplored();
            }
        }
        fullyExplored.set(fullyExploredStatus);
    }

    /**
     * Get the number of untried child game.command as assigned upon this node's creation by the {@link IActionGenerator}.
     * @return The number of potential child nodes, as determined by the command generator.
     */
    public int getUntriedActionCount() {
        if (untriedActions == null) {
            return 0;
        } else {
            return untriedActions.size();
        }
    }

    /**
     * Get one of the assigned potential child game.command. These are assigned by this node's {@link IActionGenerator}.
     * One way to sequentially get all the untried game.command would be to call <code>getUntriedActionByIndex(0)</code>.
     *
     * @see NodeGameExplorableBase#getUntriedActionRandom()
     * @see NodeGameExplorableBase#getUntriedActionCount() ()
     * @see NodeGameExplorableBase#getUntriedActionOnDistribution()
     * @see NodeGameExplorableBase#getUntriedActionListCopy()
     *
     * @param idx Index of the untried command to fetch.
     * @return An untried potential child command.
     */
    public Action<C> getUntriedActionByIndex(int idx) {
        return untriedActions.get(idx);
    }

    /**
     * Get a random untried potential command.
     * @see NodeGameExplorableBase#getUntriedActionByIndex(int)
     * @return An untried potential child command.
     */
    public Action<C> getUntriedActionRandom() {
        return untriedActions.getRandom();
    }

    /**
     * Get an untried potential command based on the rules of the {@link Distribution} within the {@link ActionList}.
     * @see NodeGameExplorableBase#getUntriedActionByIndex(int)
     * @return An untried potential child command.
     */
    public Action<C> getUntriedActionOnDistribution() {
        return untriedActions.sampleDistribution();
    }

    /**
     * Remove untried child game.command and check for changes to the fully-explored status.
     */
    void clearUntriedActions() {
        untriedActions.clear();
        // Only mark this node as fully-explored if all child game.command are also full explored.
        for (N child: getChildren()) {
            if (!child.isFullyExplored())
                return;
        }
        setFullyExploredStatus(true);
        propagateFullyExploredStatusLite();
    }

    /**
     * Get all the remaining untried game.command assigned to this node. The game.command themselves are the originals, but the
     * list is a copy.
     * @return A copy of the list of untried game.command.
     */
    public List<Action<C>> getUntriedActionListCopy() {
        return new ArrayList<>(untriedActions);
    }

    /**
     * Ask the command generator assigned to this node to report all actions that could be assigned to children of
     * this node.
     * @return A list of potential actions from the state at this node.
     */
    public ActionList<C> getAllPossibleChildActions() {
        return actionGenerator.getPotentialChildActionSet(this);
    }

    /**
     * Get the command selection distribution being used by the untried command list. This distribution is used when
     * calling {@link NodeGameExplorableBase#getUntriedActionOnDistribution()}
     * @return The distribution used for untried command selection.
     */
    public Distribution<Action<C>> getActionDistribution() {
        return untriedActions.getSamplingDistribution();
    }


    @Override
    void addToChildList(N child) {
        super.addToChildList(child);
        boolean removed = untriedActions.remove(child.getAction()); // If the child being added corresponds to one of
        // the untried
        // game.command listed, then remove it. Otherwise untried game.command remains unaffected.
        if (!removed) {
            logger.warn("Added a doubly-linked child with an command that the parent didn't have. May be ok, but you " +
                    "should know why this is happening.");
        }
    }

    /**
     * Helper for node adding from file. Clears unchecked game.command from non-leaf nodes. Only does it for nodes which
     * are at less than or equal to depth maxDepth. Forces new building to happen further towards the boundaries of
     * the tree. Note that maxDepth is absolute tree depth, not relative to
     *
     * Will not remove potential child game.command:
     * 1. Which are at leaf nodes at any depth.
     * 2. That are less than or equal to maxDepth but NOT a descendent of node.
     * 3. Nodes which are at a total tree depth greater than maxDepth.
     *
     * @param node Starting node for stripping unchecked game.command.
     * @param maxDepth Maximum absolute tree depth that untried game.command will be stripped from.
     * @param <N> Type of node, inheriting from {@link NodeGameExplorableBase}, that this command is being applied to.
     */
    public static <N extends NodeGameExplorableBase<?, C, S>, C extends Command<?>, S extends IState> void stripUncheckedActionsExceptOnLeaves(N node, int maxDepth) {
        assert maxDepth >= 0;
        node.recurseDownTreeInclusive(n -> {
            if (n.getUntriedActionCount() != 0 && n.getTreeDepth() <= maxDepth && n.getChildCount() != 0)
                n.clearUntriedActions();
        });
    }

    /**
     * Change whether this node or any above it have become fully explored. Generally called from a leaf node which
     * has just been assigned fully-explored status, and we need to propagate the effects back up the tree.
     * <p>
     * This is the "lite" version because it assumes that all child nodes it encounters have correct fully-explored
     * statuses already assigned. This should be true during normal operation, but when a bunch of saved nodes are
     * imported, it is useful to do a complete check}.
     */
    void propagateFullyExploredStatusLite() {
        boolean flag = true; // Assume this node is fully-explored and negate if we find evidence that it is not.

        if (!getState().isFailed()) {
            if (untriedActions != null && !untriedActions.isEmpty()) {
                flag = false;
            }
            for (N child : getChildren()) {
                if (!child.isFullyExplored()) { // If any child is not fully explored, then this node isn't too.
                    flag = false;
                }
            }
        }
        if (flag) {
            setFullyExploredStatus(true);
            if (getTreeDepth() > 0) { // We already know this node is fully explored, check the parent.
                getParent().propagateFullyExploredStatusLite();
            }
        }
    }

    /**
     * Change whether this node or any above it have become fully explored. This is the complete version, which
     * resets any existing fully-explored tags from the descendants of this node before redoing all checks. Call from
     * root to re-label the whole tree. During normal tree-building, a
     * {@link NodeGameExplorable#propagateFullyExploredStatusLite()
     * lite check} should suffice and is more computationally efficient.
     * <p>
     * This should only be used when a bunch of nodes are imported at once and need to all be checked, or if we need
     * to validate correct behavior of some feature.
     **/
    @SuppressWarnings("unused")
    private void propagateFullyExploredComplete() {
        ArrayList<N> leaves = new ArrayList<>(5000);
        getLeaves(leaves);

        // Reset all existing exploration flags out there.
        for (N leaf : leaves) {
            N currNode = leaf;
            while (currNode.getTreeDepth() > getTreeDepth()) {
                currNode.setFullyExploredStatus(false);
                currNode = currNode.getParent();
            }
            currNode.setFullyExploredStatus(false);
        }

        for (N leaf : leaves) {
            leaf.propagateFullyExploredStatusLite();
        }
    }

    /**
     * Destroy a branch and try to free up its memory. Mark the trimmed branch as fully explored and propagate the
     * status. This method can be useful when the sampler or user decides that one branch is bad and wants to keep it
     * from being used later in sampling.
     */
    public void destroyNodesBelowAndCheckExplored() {
        destroyNodesBelow();
        propagateFullyExploredStatusLite();
    }

    /*
     * LOCKING AND UNLOCKING NODES:
     *
     * Due to multithreading, it is a major headache if several threads are sampling from the same node at the same
     * time. Hence, we lock nodes to prevent sampling by other threads while another one is working in that part of
     * the tree. In general, we try to be overzealous in locking. For broad trees, there is minimal blocking slowdown
     * . For narrow trees, however, we may get only minimal gains from multithreading.
     *
     * Cases:
     * 1. Select node to expand. It has only 1 untried option. We lock the node, and expand.
     * 2. Select node to expand. It has multiple options. We still lock the node for now. This could be changed.
     * 3. Select node to expand. It is now locked according to 1 and 2. This node's parent only has fully explored
     * children and locked children. For all practical purposes, this node is also out of play. Lock it too and
     * recurse up the tree until we reach a node with at least one unlocked and not fully explored child.
     * When unlocking, we should propagate fully-explored statuses back up the tree first, and then remove locks as
     * far up the tree as possible.
     */

    /**
     * Set a flag to indicate that the invoking TreeWorker has temporary exclusive rights to expand from this node.
     *
     * @return Whether the lock was successfully obtained. True means that the caller obtained the lock. False means
     * that someone else got to it first.
     */
    public synchronized boolean reserveExpansionRights() {
        assert !isFullyExplored();
        assert !getState().isFailed();

        if (locked.get()) { // Already owned by another worker.
            return false;
        } else {
            locked.set(true);

            // May need to add locks to nodes further up the tree towards root. For example, if calling
            // reserveExpansionRights locks the final available node of this node's parent, then the parent should be
            // locked off too. This effect can chain all the way up the tree towards the root.
            if (getTreeDepth() > 0) getParent().propagateLock();

            return true;
        }
    }

    /**
     * Set a flag to indicate that the invoking TreeWorker has released exclusive rights to expand from this node.
     */
    public synchronized void releaseExpansionRights() {
        locked.set(false); // Release the lock.
        // Unlocking this node may cause nodes further up the tree to become available.
        if ((getTreeDepth() > 0)) getParent().propagateUnlock();
    }

    /**
     * Locking one node may cause some parent nodes to become unavailable also. propagateLock will check as far up
     * the tree towards the root node as necessary.
     */
    synchronized void propagateLock() {
        if (getUntriedActionCount() > 0) // may 19 - New addition. I don't see why this isn't ok...
            return;
        // Lock this node unless we find evidence that we don't need to.
        for (N child : getChildren()) {
            if (!child.isLocked() && !child.isFullyExplored()) {
                return; // In this case, we don't need to continue locking things further up the tree.
            }
        }
        reserveExpansionRights();
    }

    /**
     * Releasing one node's lock may make others further up the tree towards the root node become available.
     */
    synchronized void propagateUnlock() {
        if (!isLocked()) return; // We've worked our way up to a node which is already not locked. No need to
        // propagate further.

        // A single free child means we can unlock this node.
        for (N child : getChildren()) {
            if (!child.isLocked()) {  // Does not need to stay locked.
                releaseExpansionRights();
                return;
            }
        }
    }

    /**
     * Determine whether any sampler has exclusive rights to sample from this node.
     *
     * @return Whether any worker has exclusive rights to expand from this node (true/false).
     */
    public boolean isLocked() {
        return locked.get();
    }

    /**
     * Simply a wrapper about the lock. This is useful for inheriting classes that want to override to perform some
     * other behavior when locking occurs.
     * @param isLocked true for lock, false for unlock.
     */
    void setLock(boolean isLocked) {
        locked.set(isLocked);
    }
}
