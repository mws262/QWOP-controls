package game;

import java.util.Arrays;
import java.util.LinkedList;

public class GameUnifiedCaching extends GameUnified {

    private LinkedList<IState> cachedStates;

    // For delay embedding.
    public final int timestepDelay;
    public final int numDelayedStates;

    public GameUnifiedCaching(int timestepDelay, int numDelayedStates) {
        if (timestepDelay < 1) {
            throw new IllegalArgumentException("Timestep delay must be at least one. Was: " + timestepDelay);
        }
        this.timestepDelay = timestepDelay;
        this.numDelayedStates = numDelayedStates;
    }

    @Override
    public void makeNewWorld() {
        super.makeNewWorld();

        if (cachedStates == null) {
            cachedStates = new LinkedList<>();
        }
        cachedStates.clear();
        cachedStates.add(getInitialState());
    }

    @Override
    public void step(boolean q, boolean w, boolean o, boolean p) {
        super.step(q, w, o, p);
        cachedStates.addFirst(super.getCurrentState());
    }

    @Override
    public IState getCurrentState() {

        assert !cachedStates.isEmpty();

        IState[] states = new IState[numDelayedStates + 1];
        Arrays.fill(states, getInitialState());

        for (int i = 0; i < Integer.min(states.length, (cachedStates.size() + timestepDelay - 1) / timestepDelay); i++) {
            states[i] = cachedStates.get(timestepDelay * i);
        }

        return new StateDelayEmbedded(states);
    }
}