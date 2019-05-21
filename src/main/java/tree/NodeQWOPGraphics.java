package tree;

import actions.Action;
import actions.IActionGenerator;
import game.State;

/**
 * Concrete implementation of the QWOP tree node which contains additional information for graphical drawing (colors,
 * positions, etc.). Most of the functionality is found in {@link NodeQWOPGraphicsBase}.
 *
 * If this amount of functionality is not needed:
 * See {@link NodeQWOPExplorable} for just tree building functionality.
 * See {@link NodeQWOP} for just state and action storage.
 * See {@link NodeGeneric} for basic tree functionality without any QWOP specifics.
 *
 *
 * @author matt
 */
public class NodeQWOPGraphics extends NodeQWOPGraphicsBase<NodeQWOPGraphics> {

    /**
     * @see NodeQWOPGraphicsBase#NodeQWOPGraphicsBase(State)
     */
    public NodeQWOPGraphics(State rootState, IActionGenerator actionGenerator) {
        super(rootState, actionGenerator);
    }

    /**
     * @see NodeQWOPGraphicsBase#NodeQWOPGraphicsBase(State, IActionGenerator)
     */
    public NodeQWOPGraphics(State rootState) {
        super(rootState);
    }

    /**
     * @see NodeQWOPGraphicsBase#NodeQWOPGraphicsBase(NodeQWOPGraphicsBase, Action, State, IActionGenerator, boolean)
     */
    private NodeQWOPGraphics(NodeQWOPGraphics parent, Action action, State state, IActionGenerator actionGenerator,
                             boolean doublyLinked) {
        super(parent, action, state, actionGenerator, doublyLinked);
    }

    @Override
    protected NodeQWOPGraphics getThis() {
        return this;
    }

    @Override
    public NodeQWOPGraphics addDoublyLinkedChild(Action action, State state) {
        return addDoublyLinkedChild(action, state, actionGenerator);
    }

    @Override
    public NodeQWOPGraphics addBackwardsLinkedChild(Action action, State state) {
        return addBackwardsLinkedChild(action, state, actionGenerator);
    }

    @Override
    public NodeQWOPGraphics addDoublyLinkedChild(Action action, State state, IActionGenerator actionGenerator) {
        return new NodeQWOPGraphics(this, action, state, actionGenerator, true);
    }

    @Override
    public NodeQWOPGraphics addBackwardsLinkedChild(Action action, State state, IActionGenerator actionGenerator) {
        return new NodeQWOPGraphics(this, action, state, actionGenerator, false);
    }
}
