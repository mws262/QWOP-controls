package value.updaters;

import game.action.Command;
import tree.node.NodeQWOPBase;

/**
 * Node value estimate update rule. The value is always estimated to be the average of all update value seen. This is
 * default in many implementations of upper confidence bound for trees.
 *
 * @author matt
 */
public class ValueUpdater_Average<C extends Command<?>> implements IValueUpdater<C> {

    @Override
    public float update(float valueUpdate, NodeQWOPBase<?, C> node) {
        return (node.getValue() * node.getUpdateCount() + valueUpdate) / (node.getUpdateCount() + 1);
    }

    @Override
    public IValueUpdater<C> getCopy() {
        return new ValueUpdater_Average<>();
    }
}
