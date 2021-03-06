package tree.sampler;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonSubTypes;
import com.fasterxml.jackson.annotation.JsonTypeInfo;
import game.IGameInternal;
import game.action.Action;
import game.action.Command;
import game.state.IState;
import tree.node.NodeGameExplorableBase;

/**
 * Defines a strategy for sampling nodes.
 *
 * @author Matt
 */
@JsonTypeInfo(
        use = JsonTypeInfo.Id.NAME,
        property = "type")
@JsonSubTypes({
        @JsonSubTypes.Type(value = Sampler_Random.class, name = "random"),
        @JsonSubTypes.Type(value = Sampler_Greedy.class, name = "greedy"),
        @JsonSubTypes.Type(value = Sampler_UCB.class, name = "ucb"),
        @JsonSubTypes.Type(value = Sampler_Deterministic.class, name = "deterministic"),
        @JsonSubTypes.Type(value = Sampler_Distribution.class, name = "distribution"),
})
public interface ISampler<C extends Command<?>, S extends IState> extends AutoCloseable {

    /**
     * Decide a path through the existing tree to a place where a new node will be added. It is the tree policy's
     * responsibility to lock the Node multithreading is used.
     **/
    NodeGameExplorableBase<?, C, S> treePolicy(NodeGameExplorableBase<?, C, S> startNode);

    /**
     * Lets the sampler know that the previously requested game moves have occurred and the tree FSM is ready to do
     * more stuff.
     **/
    void treePolicyActionDone(NodeGameExplorableBase<?, C, S> currentNode);

    /**
     * Are we ready to switch from tree policy to expansion policy?
     **/
    boolean treePolicyGuard(NodeGameExplorableBase<?, C, S> currentNode);

    /**
     * Strategy for adding a single node at a depth of 1 greater than the given startNode.
     **/
    Action<C> expansionPolicy(NodeGameExplorableBase<?, C, S> startNode);

    /**
     * Lets the sampler know that the previously requested game moves have occurred and the tree FSM is ready to do
     * more stuff.
     **/
    void expansionPolicyActionDone(NodeGameExplorableBase<?, C, S> currentNode);

    /**
     * Are we ready to switch from expansion policy to rollout policy?
     **/
    boolean expansionPolicyGuard(NodeGameExplorableBase<?, C, S> currentNode);

    /**
     * Continued expansion which is NOT added to the tree as nodes. Only used for scoring as in UCB.
     **/
    void rolloutPolicy(NodeGameExplorableBase<?, C, S> startNode, IGameInternal<C, S> game);

    /**
     * Are we ready to switch from rollout policy to tree policy?
     **/
    boolean rolloutPolicyGuard(NodeGameExplorableBase<?, C, S> currentNode);

    /**
     * Copy this sampler and its settings. Each worker needs an individual copy.
     **/
    @JsonIgnore
    ISampler<C, S> getCopy();

    @Override
    void close();
}
