package controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.IGameSerializable;
import game.action.Action;
import game.action.Command;
import tree.node.NodeQWOPExplorableBase;
import value.IValueFunction;

public class Controller_ValueFunction<C extends Command<?>> implements IController<C> {

    private final IValueFunction<C> valueFunction;

    public Controller_ValueFunction(@JsonProperty("valueFunction") IValueFunction<C> valueFunction) {
        this.valueFunction = valueFunction;
    }

    @Override
    public Action<C> policy(NodeQWOPExplorableBase<?, C> state) {
        return valueFunction.getMaximizingAction(state);
    }

    @Override
    public Action<C> policy(NodeQWOPExplorableBase<?, C> state, IGameSerializable<C> game) {
        return valueFunction.getMaximizingAction(state, game);
    }

    @Override
    @JsonIgnore
    public IController<C> getCopy() {
        return new Controller_ValueFunction<>(valueFunction.getCopy());
    }

    public IValueFunction<C> getValueFunction() {
        return valueFunction;
    }

    @Override
    public void close() {
        valueFunction.close();
    }
}
