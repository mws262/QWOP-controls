package flashqwop;

import game.qwop.StateQWOP;

/**
 * New state information is sent from the real QWOP game every timestep. If anyone needs to listen in for these
 * states, they should implement this.
 */
public interface IFlashStateListener {
    void stateReceived(int timestep, StateQWOP state);
}
