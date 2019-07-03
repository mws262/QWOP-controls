package game.action;

import com.fasterxml.jackson.annotation.JsonProperty;
import tree.node.NodeQWOPExplorableBase;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * ActionGenerator which is defined for a single ActionList which is always returned when queried.
 *
 * @author matt
 */
public class ActionGenerator_FixedActions implements IActionGenerator {

    private final ActionList actionList;

    public ActionGenerator_FixedActions(@JsonProperty("actionList") ActionList actionList) {
        this.actionList = actionList;
    }

    @Override
    public ActionList getPotentialChildActionSet(NodeQWOPExplorableBase<?> parentNode) {
        return actionList.getCopy();
    }

    @Override
    public Set<Action> getAllPossibleActions() {
        return new HashSet<>(actionList);
    }

    public ActionList getActionList() {
        return actionList;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionGenerator_FixedActions that = (ActionGenerator_FixedActions) o;
        return actionList.equals(that.actionList);
    }

    @Override
    public int hashCode() {
        return Objects.hash(actionList);
    }
}