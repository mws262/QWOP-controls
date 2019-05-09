package tree;

import actions.Action;
import game.State;

import java.util.List;

/**
 * Most basic QWOP node. Just does states and actions.
 * See {@link NodeGenericBase} for more information about the bizarre generics.
 *
 * @param <N>
 */
public abstract class NodeQWOPBase<N extends NodeQWOPBase<N>> extends NodeGenericBase<N> {

    private final Action action;

    private final State state;

    public NodeQWOPBase(State rootState) {
        super();
        action = null;
        state = rootState;
    }

    public NodeQWOPBase(N parent, Action action, State state) {
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
        getParent().addChild(getThis());
    }

    public State getState() { return state; }

    public Action getAction() { return action; }

    public synchronized List<Action> getSequence(List<Action> actionList) {
        if (getTreeDepth() == 0)
            throw new IndexOutOfBoundsException("Cannot get a sequence at the root node, since it has no actions " +
                    "leading up to it.");
        actionList.clear();
        N current = getThis();
        actionList.add(getTreeDepth() - 1, current.getAction());
        for (int i = getTreeDepth() - 2; i >= 0; i--) {
            current = current.getParent();
            actionList.add(i, current.getAction());
        }
        return actionList;
    }

    public abstract N addChild(Action action, State state);
}
