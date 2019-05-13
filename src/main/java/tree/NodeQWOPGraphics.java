package tree;

import actions.Action;
import actions.ActionGenerator_Null;
import actions.IActionGenerator;
import game.State;

public class NodeQWOPGraphics extends NodeQWOPGraphicsBase<NodeQWOPGraphics> {

    public NodeQWOPGraphics(State rootState, IActionGenerator actionGenerator) {
        super(rootState, actionGenerator);
    }
    public NodeQWOPGraphics(State rootState) {
        super(rootState);
    }

    public NodeQWOPGraphics(NodeQWOPGraphics parent, Action action, State state, IActionGenerator actionGenerator) {
        super(parent, action, state, actionGenerator);
    }

    @Override
    protected NodeQWOPGraphics getThis() {
        return this;
    }

    @Override
    public NodeQWOPGraphics addDoublyLinkedChild(Action action, State state) {
        NodeQWOPGraphics child = new NodeQWOPGraphics(this, action, state, actionGenerator);
        addToChildList(child);
        return child;
    }

    @Override
    public NodeQWOPGraphics addBackwardsLinkedChild(Action action, State state) {
        return new NodeQWOPGraphics(this, action, state, actionGenerator);
    }

    @Override
    public NodeQWOPGraphics addDoublyLinkedChild(Action action, State state, IActionGenerator actionGenerator) {
        NodeQWOPGraphics child = new NodeQWOPGraphics(this, action, state, actionGenerator);
        addToChildList(child);
        return child;
    }

    @Override
    public NodeQWOPGraphics addBackwardsLinkedChild(Action action, State state, IActionGenerator actionGenerator) {
        return new NodeQWOPGraphics(this, action, state, actionGenerator);
    }
}
