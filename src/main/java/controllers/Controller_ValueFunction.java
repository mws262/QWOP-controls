package controllers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import game.IGameSerializable;
import game.action.Action;
import game.action.Command;
import game.state.IState;
import tree.node.NodeGameExplorableBase;
import value.IValueFunction;

public class Controller_ValueFunction<C extends Command<?>, S extends IState, V extends IValueFunction<C, S>> implements IController<C, S> {

    private final V valueFunction;

    public Controller_ValueFunction(@JsonProperty("valueFunction") V valueFunction) {
        this.valueFunction = valueFunction;
    }

    @Override
    public Action<C> policy(NodeGameExplorableBase<?, C, S> state) {
        return valueFunction.getMaximizingAction(state);
    }

    @Override
    public Action<C> policy(NodeGameExplorableBase<?, C, S> state, IGameSerializable<C, S> game) {
        return valueFunction.getMaximizingAction(state, game);
    }

    @Override
    @JsonIgnore
    public IController<C, S> getCopy() {
        return new Controller_ValueFunction<>(valueFunction.getCopy());
    }

    public V getValueFunction() {
        return valueFunction;
    }

    @Override
    public void close() {
        valueFunction.close();
    }
}
