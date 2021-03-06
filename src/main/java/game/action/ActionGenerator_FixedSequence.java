package game.action;

import com.fasterxml.jackson.annotation.JsonGetter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonUnwrapped;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.KeyDeserializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import com.fasterxml.jackson.databind.ser.std.StdKeySerializer;
import distributions.Distribution;
import distributions.Distribution_Normal;
import game.qwop.CommandQWOP;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import tree.node.NodeGameExplorableBase;

import java.io.IOException;
import java.util.*;
import java.util.stream.IntStream;

/**
 * Assigns potential command choices to nodes. This fixed sequence version has an {@link ActionList} for
 * each subsequent {@link Action} choice, and cycles through these choices. This is a more general version of the fixed
 * nil/nil - WO - nil/nil - QP sequence I've used for quite awhile. Also, command "exceptions" can be specified to
 * override the normal ActionList in the cycle. This can be useful for defining a different set of game.command for the
 * first few steps when the runner is getting moving.
 *
 * @author matt
 */
@SuppressWarnings("Duplicates")
public class ActionGenerator_FixedSequence<C extends Command<?>> implements IActionGenerator<C> {

    /**
     * These are the possible game.command which are generated in a cycle.
     **/
    private List<ActionList<C>> repeatedActions;

    /**
     * These game.command are exceptions which will override the repeated game.command. Key is the
     * tree depth this applies to. Value is the set of Actions. The keySequence booleans
     * are still obeyed. Usually exceptions are at the beginning of a sequence.
     */
    @JsonSerialize(keyUsing = ExceptionSerializer.class) // Take care of the fact that the keys are integers, which
    // throws off XML.
    @JsonDeserialize(keyUsing = ExceptionDeserializer.class)
    private final Map<Integer, ActionList<C>> actionExceptions;

    /**
     * How many game.command are in one repeated cycle.
     **/
    private final int cycleLength;

    /**
     * This command generator will produce the nil-WO-nil-QP sequence. actionRepeats will be chosen in repeating order
     * like 1234,1234,1234, or 123,123,123, etc. Note that this starts from tree root EVEN if there are command
     * exceptions. However, command exceptions for specified tree depths will be chosen over the repeating game.command if
     * they are assigned.
     *
     * @param repeatedActions  An array of {@link ActionList}, with one for every choice in the sequence. Each
     *                         subsequent choice (increasing depth in the tree) cycles through the command sets and
     *                         wraps when at the end of the ActionSets.
     * @param actionExceptions A {@link Map} of integer indexes to {@link ActionList}. When the sequences reach a tree
     *                         depth specified in the map, it will use the ActionList corresponding to this index
     *                         rather than the normal ActionList in the cycle.
     */
    public ActionGenerator_FixedSequence(@NotNull @JsonProperty("repeatedActions") List<ActionList<C>> repeatedActions,
                                         @Nullable @JsonProperty("actionExceptions") Map<Integer, ActionList<C>> actionExceptions) {

        if (repeatedActions.size() == 0) {
            throw new IllegalArgumentException("There must be at least 1 repeated command. The array was empty.");
        }

        cycleLength = repeatedActions.size();

        this.repeatedActions = repeatedActions;
        this.actionExceptions = actionExceptions;
    }

    public ActionGenerator_FixedSequence(List<ActionList<C>> repeatedActions) {
        this(repeatedActions, null);
    }


    @SafeVarargs
    public ActionGenerator_FixedSequence(Map<Integer, ActionList<C>> actionExceptions,
                                         ActionList<C> ... repeatedActions) {
        this(Arrays.asList(repeatedActions), actionExceptions);
    }

    @SafeVarargs
    public ActionGenerator_FixedSequence(ActionList<C> ... repeatedActions) {
        this(null, repeatedActions);
    }

    @JsonIgnore
    @Override
    public ActionList<C> getPotentialChildActionSet(NodeGameExplorableBase<?, C, ?> parentNode) {
        int actionDepth = parentNode.getTreeDepth();

        // Check if this is an exception case.
        if (actionExceptions != null && actionExceptions.containsKey(actionDepth))
            return actionExceptions.get(actionDepth).getCopy();

        // Otherwise, pick based on cycle.
        return repeatedActions.get(actionDepth % cycleLength).getCopy();
    }

    @JsonProperty
    @Override
    public Set<Action<C>> getAllPossibleActions() {
        Set<Action<C>> allActions = new HashSet<>();
        for (ActionList<C> as : repeatedActions) {
            allActions.addAll(as);
        }
        if (actionExceptions != null) {
            for (ActionList<C> as : actionExceptions.values()) {
                allActions.addAll(as);
            }
        }
        return allActions;
    }

    @JsonGetter("repeatedActions")
    public List<ActionList<C>> getRepeatedActions() {
        return new ArrayList<>(repeatedActions);
    }

    @JsonUnwrapped
    public Map<Integer, ActionList<C>> getActionExceptions() {
        return actionExceptions;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ActionGenerator_FixedSequence<?> that = (ActionGenerator_FixedSequence<?>) o;
        return cycleLength == that.cycleLength &&
                repeatedActions.equals(that.repeatedActions) &&
                Objects.equals(actionExceptions, that.actionExceptions);
    }

    @Override
    public int hashCode() {
        return Objects.hash(repeatedActions, actionExceptions, cycleLength);
    }

    /**
     * Assign the correct generator of game.command based on the baseline options and exceptions.
     * Will assign a broader set of options for "recovery" at the specified starting depth.
     * Pass -1 to disable this.
     */
    public static ActionGenerator_FixedSequence<CommandQWOP> makeDefaultGenerator(int recoveryExceptionStart) {
        /* Space of allowed game.command to sample */
        //Distribution<Action> uniform_dist = new Distribution_Equal();

        /* Repeated command 1 -- no keys pressed. */
        Distribution<Action<CommandQWOP>> dist1 = new Distribution_Normal<>(10f, 2f);
        ActionList<CommandQWOP> actionList1 = ActionList.makeActionList(IntStream.range(1, 25).toArray(), CommandQWOP.NONE, dist1);

        /*  Repeated command 2 -- W-O pressed */
        Distribution<Action<CommandQWOP>> dist2 = new Distribution_Normal<>(39f, 3f);
        ActionList<CommandQWOP> actionList2 = ActionList.makeActionList(IntStream.range(20, 60).toArray(), CommandQWOP.WO, dist2);

        /* Repeated command 3 -- No keys pressed. */
        Distribution<Action<CommandQWOP>> dist3 = new Distribution_Normal<>(10f, 2f);
        ActionList<CommandQWOP> actionList3 = ActionList.makeActionList(IntStream.range(1, 25).toArray(), CommandQWOP.NONE, dist3);

        /*  Repeated command 4 -- Q-P pressed */
        Distribution<Action<CommandQWOP>> dist4 = new Distribution_Normal<>(39f, 3f);
        ActionList<CommandQWOP> actionList4 = ActionList.makeActionList(IntStream.range(20, 60).toArray(), CommandQWOP.QP, dist4);

        List<ActionList<CommandQWOP>> repeatedActions = new ArrayList<>();
        repeatedActions.add(actionList1);
        repeatedActions.add(actionList2);
        repeatedActions.add(actionList3);
        repeatedActions.add(actionList4);

        /////// Action Exceptions for starting up. ////////
        /* Repeated command exceptions 1 -- no keys pressed. */
        Distribution<Action<CommandQWOP>> distE1 = new Distribution_Normal<>(5f, 1f);
        ActionList<CommandQWOP> actionListE1 = ActionList.makeActionList(IntStream.range(1, 25).toArray(),
                CommandQWOP.NONE, distE1);

        /*  Repeated command exceptions 2 -- W-O pressed */
        Distribution<Action<CommandQWOP>> distE2 = new Distribution_Normal<>(34f, 2f);
        ActionList<CommandQWOP> actionListE2 = ActionList.makeActionList(IntStream.range(30, 50).toArray(),
                CommandQWOP.WO, distE2);

        /*  Repeated command exceptions 3 -- no keys pressed. */
        Distribution<Action<CommandQWOP>> distE3 = new Distribution_Normal<>(5, 2f);
        ActionList<CommandQWOP> actionListE3 = ActionList.makeActionList(IntStream.range(1, 20).toArray(),
                CommandQWOP.NONE, distE3);

        /*  Repeated command exceptions 4 -- Q-P pressed */
        Distribution<Action<CommandQWOP>> distE4 = new Distribution_Normal<>(25f, 2f);
        ActionList<CommandQWOP> actionListE4 = ActionList.makeActionList(IntStream.range(15, 30).toArray(),
                CommandQWOP.QP, distE4);

        /////// Action Exceptions for recovery. ////////
        /*  Repeated command 1 and 3 -- Nothing pressed */
        Distribution<Action<CommandQWOP>> distFalseFalse = new Distribution_Normal<>(10f, 2f);
        ActionList<CommandQWOP> actionListFalseFalse = ActionList.makeActionList(IntStream.range(1, 50).toArray(),
                CommandQWOP.NONE, distFalseFalse);

        /*  Repeated command 2 -- W-O pressed */
        Distribution<Action<CommandQWOP>> distWO = new Distribution_Normal<>(39f, 3f);
        ActionList<CommandQWOP> actionListWO = ActionList.makeActionList(IntStream.range(1, 70).toArray(),
                CommandQWOP.WO, distWO);

        /*  Repeated command 4 -- Q-P pressed */
        Distribution<Action<CommandQWOP>> distQP = new Distribution_Normal<>(39f, 3f);
        ActionList<CommandQWOP> actionListQP = ActionList.makeActionList(IntStream.range(1, 70).toArray(), CommandQWOP.QP, distQP);

        Map<Integer, ActionList<CommandQWOP>> actionExceptions = new HashMap<>();
        actionExceptions.put(0, actionListE1);
        actionExceptions.put(1, actionListE2);
        actionExceptions.put(2, actionListE3);
        actionExceptions.put(3, actionListE4);

        // Put the recovery exceptions in the right spot.
        if (recoveryExceptionStart >= 0) {
            for (int i = 0; i < 4; i++) {
                int sequencePos = (recoveryExceptionStart + i) % 4;

                switch (sequencePos) {
                    case 0:
                        actionExceptions.put(recoveryExceptionStart + i, actionListFalseFalse);
                        break;
                    case 1:
                        actionExceptions.put(recoveryExceptionStart + i, actionListWO);
                        break;
                    case 2:
                        actionExceptions.put(recoveryExceptionStart + i, actionListFalseFalse);
                        break;
                    case 3:
                        actionExceptions.put(recoveryExceptionStart + i, actionListQP);
                        break;
                    default:
                        throw new IllegalStateException("unknown sequence position.");
                }
            }
        }
        // Define the specific way that these allowed game.command are assigned as potential options for nodes.
        return new ActionGenerator_FixedSequence<>(repeatedActions, actionExceptions);
    }

    public static ActionGenerator_FixedSequence<CommandQWOP> makeExtendedGenerator(int recoveryExceptionStart) {
        /* Space of allowed game.command to sample */
        //Distribution<Action> uniform_dist = new Distribution_Equal();

        /* Repeated command 1 -- no keys pressed. */
        Distribution<Action<CommandQWOP>> dist1 = new Distribution_Normal<>(10f, 2f);
        ActionList<CommandQWOP> actionList1 = ActionList.makeActionList(IntStream.range(2, 25).toArray(),
                CommandQWOP.NONE, dist1);

        /*  Repeated command 2 -- W-O pressed */
        Distribution<Action<CommandQWOP>> dist2 = new Distribution_Normal<>(25, 3f);
        ActionList<CommandQWOP> actionList2 = ActionList.makeActionList(IntStream.range(5, 45).toArray(), CommandQWOP.WO,
                dist2);

        /* Repeated command 3 -- NONE pressed */
        Distribution<Action<CommandQWOP>> dist3 = new Distribution_Normal<>(10f, 2f);
        ActionList<CommandQWOP> actionList3 = ActionList.makeActionList(IntStream.range(2, 25).toArray(),
                CommandQWOP.NONE, dist3);

        /*  Repeated command 4 -- Q-P pressed */
        Distribution<Action<CommandQWOP>> dist4 = new Distribution_Normal<>(25, 3f);
        ActionList<CommandQWOP> actionList4 = ActionList.makeActionList(IntStream.range(5, 45).toArray(), CommandQWOP.QP,
                dist4);


        /////// Action Exceptions for starting up. ////////
        /* Repeated command exceptions 1 -- no keys pressed. */
        Distribution<Action<CommandQWOP>> distE1 = new Distribution_Normal<>(5f, 1f);
        ActionList<CommandQWOP> actionListE1 = ActionList.makeActionList(IntStream.range(1, 35).toArray(),
                CommandQWOP.NONE, distE1);

        /*  Repeated command exceptions 2 -- W-O pressed */
        Distribution<Action<CommandQWOP>> distE2 = new Distribution_Normal<>(34f, 2f);
        ActionList<CommandQWOP> actionListE2 = ActionList.makeActionList(IntStream.range(5, 45).toArray(), CommandQWOP.WO, distE2);

        /*  Repeated command exceptions 3 -- no keys pressed. */
        Distribution<Action<CommandQWOP>> distE3 = new Distribution_Normal<>(24f, 2f);
        ActionList<CommandQWOP> actionListE3 = ActionList.makeActionList(IntStream.range(1, 35).toArray(),
                CommandQWOP.NONE, distE3);

        /*  Repeated command exceptions 4 -- Q-P pressed */
        Distribution<Action<CommandQWOP>> distE4 = new Distribution_Normal<>(49f, 2f);
        ActionList<CommandQWOP> actionListE4 = ActionList.makeActionList(IntStream.range(5, 45).toArray(), CommandQWOP.QP,
                distE4);

        /////// Action Exceptions for recovery. ////////
        /*  Repeated command 1 and 3 -- Nothing pressed */
        Distribution<Action<CommandQWOP>> distFalseFalse = new Distribution_Normal<>(10f, 2f);
        ActionList<CommandQWOP> actionListFalseFalse = ActionList.makeActionList(IntStream.range(1, 50).toArray(),
                CommandQWOP.NONE, distFalseFalse);

        /*  Repeated command 2 -- W-O pressed */
        Distribution<Action<CommandQWOP>> distWO = new Distribution_Normal<>(39f, 3f);
        ActionList<CommandQWOP> actionListWO = ActionList.makeActionList(IntStream.range(1, 70).toArray(), CommandQWOP.WO,
                distWO);

        /*  Repeated command 4 -- Q-P pressed */
        Distribution<Action<CommandQWOP>> distQP = new Distribution_Normal<>(39f, 3f);
        ActionList<CommandQWOP> actionListQP = ActionList.makeActionList(IntStream.range(1, 70).toArray(),
                CommandQWOP.QP, distQP);

        Map<Integer, ActionList<CommandQWOP>> actionExceptions = new HashMap<>();
        actionExceptions.put(0, actionListE1);
        actionExceptions.put(1, actionListE2);
        actionExceptions.put(2, actionListE3);
        actionExceptions.put(3, actionListE4);

        // Put the recovery exceptions in the right spot.
        if (recoveryExceptionStart >= 0) {
            for (int i = 0; i < 4; i++) {
                int sequencePos = (recoveryExceptionStart + i) % 4;

                switch (sequencePos) {
                    case 0:
                        actionExceptions.put(recoveryExceptionStart + i, actionListFalseFalse);
                        break;
                    case 1:
                        actionExceptions.put(recoveryExceptionStart + i, actionListWO);
                        break;
                    case 2:
                        actionExceptions.put(recoveryExceptionStart + i, actionListFalseFalse);
                        break;
                    case 3:
                        actionExceptions.put(recoveryExceptionStart + i, actionListQP);
                        break;
                    default:
                        throw new RuntimeException("unknown sequence position.");
                }
            }
        }
        // Define the specific way that these allowed game.command are assigned as potential options for nodes.
        return new ActionGenerator_FixedSequence<>(actionExceptions, actionList1, actionList2, actionList3,
                actionList4);
    }

    private static class ExceptionDeserializer extends KeyDeserializer {

        @Override
        public Object deserializeKey(String key, DeserializationContext ctxt) {
            return new Integer(key.replace("depth", ""));
        }
    }

    // XML doesn't like tags with only integers in them.
    private static class ExceptionSerializer extends StdKeySerializer {

        @Override
        public void serialize(Object value, JsonGenerator g, SerializerProvider provider) throws IOException {
            g.writeFieldName("depth" + value.toString());
        }

    }
}
