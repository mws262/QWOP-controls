package game.action;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import tree.node.NodeGameExplorableBase;

import java.util.Set;

/**
 * An IActionGenerator determines which {@link ActionList} should be assigned to a node as potential
 * child nodes to explore.
 *
 * @author matt
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = ActionGenerator_FixedActions.class, name = "fixed_actions"),
        @JsonSubTypes.Type(value = ActionGenerator_FixedSequence.class, name = "fixed_sequence"),
        @JsonSubTypes.Type(value = ActionGenerator_UniformNoRepeats.class, name = "fixed_actions_no_repeats"),
        @JsonSubTypes.Type(value = ActionGenerator_Null.class, name = "null"),
        @JsonSubTypes.Type(value = ActionGenerator_QWOPNominal.class, name = "qwop_nominal"),
})
public interface IActionGenerator<C extends Command<?>> {
    /**
     * Get an {@link ActionList} of potential game.command to explore from a newly created node as its potential children.
     *
     * @param parentNode Node for which we want to pick potential child game.command.
     * @return A set of game.command to try as potential children.
     */
    @JsonIgnore
    ActionList<C> getPotentialChildActionSet(NodeGameExplorableBase<?, C, ?> parentNode);

    /**
     * Get a set of all possible game.command which this generator could return.
     * @return All possibly generated Actions.
     */
    @JsonIgnore
    Set<Action<C>> getAllPossibleActions();
}
