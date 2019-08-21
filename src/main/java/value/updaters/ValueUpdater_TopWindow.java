package value.updaters;

import com.fasterxml.jackson.annotation.JsonProperty;
import org.jcodec.common.Preconditions;
import tree.node.NodeQWOPBase;

import java.util.*;


public class ValueUpdater_TopWindow implements IValueUpdater {

    public final int windowSize;

    public enum Criteria {
        WORST, AVERAGE_OPTIMISTIC, AVERAGE_PESSIMISTIC
    }

    public Criteria windowScoringCriterion = Criteria.WORST;

    public ValueUpdater_TopWindow(@JsonProperty("windowSize") int windowSize) {
        Preconditions.checkArgument(windowSize > 0, "Window size should be at least 1.", windowSize);

        this.windowSize = windowSize;
    }

    @Override
    public float update(float valueUpdate, NodeQWOPBase<?> node) {
        if (node.getChildCount() == 0) {
            return valueUpdate;
        } else {
            List<NodeQWOPBase<?>> children = new ArrayList<>();
            node.applyToThis(n -> children.addAll(n.getChildren())); // Trick to get around type erasure.

            children.sort(Comparator.comparing(NodeQWOPBase::getAction)); // Sort by actions. Separated first by key,
            // then by duration, ascending.

            // Separate into lists of consecutive actions.
            List<List<NodeQWOPBase<?>>> clusters = separateClustersInSortedList(children);

            // Find best average cluster value. Starts with clusters at least the size of the specified window and
            // moves down if none are found.
            boolean foundAtLeastOne = false;
            float bestValue = -Float.MAX_VALUE;
            int effectiveWindowSize = windowSize;
            while (effectiveWindowSize > 0) {
                for (List<NodeQWOPBase<?>> cluster : clusters) {
                    if (cluster.size() >= effectiveWindowSize) {
                        for (int i = 0; i <= cluster.size() - effectiveWindowSize; i++) {
                            float value;
                            switch (windowScoringCriterion) {
                                case WORST:
                                    value = Float.MAX_VALUE;
                                    break;
                                case AVERAGE_OPTIMISTIC:
                                    value = 0f;
                                    break;
                                case AVERAGE_PESSIMISTIC:
                                    value = 0f;
                                    break;
                                default:
                                    throw new IllegalStateException("Unhandled window criterion.");
                            }

                            for (int j = i; j < i + effectiveWindowSize; j++) {
                                float nodeVal = cluster.get(j).getValue();
                                switch (windowScoringCriterion) {
                                    case WORST:
                                        if (nodeVal < value) {
                                            value = nodeVal;
                                        }
                                        break;
                                    case AVERAGE_OPTIMISTIC:
                                        value += nodeVal;
                                        break;
                                    case AVERAGE_PESSIMISTIC:
                                        value += nodeVal;
                                        break;
                                }
                            }

                            switch (windowScoringCriterion) {
                                case WORST:
                                    // value *= (float) effectiveWindowSize /  (float) windowSize;
                                    break;
                                case AVERAGE_OPTIMISTIC:
                                    value /= effectiveWindowSize;
                                    break;
                                case AVERAGE_PESSIMISTIC: // Average based on desired window size. If there isn't a
                                    // grouping of actions as big as the window, then missing elements get an
                                    // effective value of zero.
                                    value /= windowSize;
                                    break;
                            }

                            foundAtLeastOne = true;
                            if (value > bestValue) {
                                bestValue = value;
                            }
                        }
                    }
                }

                if (foundAtLeastOne) {
                    return bestValue;
                }
                effectiveWindowSize--;
            }
        }

        throw new IllegalStateException("Should never happen. Value updater did not find any clusters and did not " +
                "handle that case appropriately.");
    }

    @Override
    public IValueUpdater getCopy() {
        return new ValueUpdater_TopWindow(windowSize);
    }

    static List<List<NodeQWOPBase<?>>> separateClustersInSortedList(List<NodeQWOPBase<?>> nodes) {
        List<List<NodeQWOPBase<?>>> clusters = new ArrayList<>();
        int idx = 0;
        while (idx < nodes.size()) {
            List<NodeQWOPBase<?>> cluster = new ArrayList<>();
            NodeQWOPBase<?> sequenceBegin = nodes.get(idx);
            cluster.add(sequenceBegin);
            idx++;
            while (idx < nodes.size() && nodes.get(idx).getAction().getTimestepsTotal() == 1 + nodes.get(idx - 1).getAction().getTimestepsTotal()
                    && nodes.get(idx).getAction().peek() == nodes.get(idx - 1).getAction().peek()) {

                cluster.add(nodes.get(idx));
                idx++;
            }
            clusters.add(cluster);
        }
        return clusters;
    }
}
