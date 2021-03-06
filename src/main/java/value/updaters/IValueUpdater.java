package value.updaters;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import game.action.Command;
import game.state.IState;
import tree.node.NodeGameBase;

/**
 * A rule for updating the estimated value of a {@link NodeGameBase} given a node and an update value.
 *
 * @author matt
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ValueUpdater_Average.class, name = "average"),
        @JsonSubTypes.Type(value = ValueUpdater_HardSet.class, name = "hard_set"),
        @JsonSubTypes.Type(value = ValueUpdater_StdDev.class, name = "stdev_above"),
        @JsonSubTypes.Type(value = ValueUpdater_TopNChildren.class, name = "top_children"),
        @JsonSubTypes.Type(value = ValueUpdater_TopWindow.class, name = "top_window")
})
public interface IValueUpdater<C extends Command<?>, S extends IState> {

    /**
     * Provide an updated value for a node.
     * @param valueUpdate An update value.
     * @param node Node to calculate an updated value for.
     * @return The provided node's updated value.
     */
    float update(float valueUpdate, NodeGameBase<?, C, S> node);

    @JsonIgnore
    IValueUpdater<C, S> getCopy();
}
