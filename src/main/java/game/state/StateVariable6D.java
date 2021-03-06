package game.state;

import com.fasterxml.jackson.annotation.JsonProperty;
import game.qwop.StateQWOP;
import org.apache.commons.lang.builder.EqualsBuilder;
import org.apache.commons.lang.builder.HashCodeBuilder;

import java.io.Serializable;
import java.util.List;

/**
 * Container for state values for a single body link at a single timestep.
 * <p>
 * These StateVariables are generally stored by {@link StateQWOP StateQWOP} to represent the full runner state.
 *
 * @author matt
 */
public class StateVariable6D implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Name of each state value (configurations and velocities).
     */
    public enum StateName {
        X, Y, TH, DX, DY, DTH
    }

    /**
     * Horizontal position of the body.
     */
    private final float x;

    /**
     * Vertical position of the body.
     */
    private final float y;

    /**
     * Counterclockwise angle of the body.
     */
    private final float th;

    /**
     * Horizontal velocity of the body.
     */
    private final float dx;

    /**
     * Vertical velocity of the body.
     */
    private final float dy;

    /**
     * Counterclockwise angular rate of the body.
     */
    private final float dth;

    /**
     * Make a new StateVariables holding the configuration and velocity information for a single runner link.
     *
     * @param x   Horizontal position of the body.
     * @param y   Vertical position of the body.
     * @param th  Counterclockwise angle of the body.
     * @param dx  Horizontal velocity of the body.
     * @param dy  Vertical velocity of the body.
     * @param dth Counterclockwise angular rate of the body.
     */
    public StateVariable6D(@JsonProperty("x") float x,
                           @JsonProperty("y") float y,
                           @JsonProperty("th") float th,
                           @JsonProperty("dx") float dx,
                           @JsonProperty("dy") float dy,
                           @JsonProperty("dth") float dth) {
        this.x = x;
        this.y = y;
        this.th = th;
        this.dx = dx;
        this.dy = dy;
        this.dth = dth;
    }

    /**
     * Make a new StateVariables holding the configuration and velocity information for a single runner link.
     *
     * @param stateVals List containing the 6 state values for a single link. Order should be x, y, th, dx, dy, dth.
     */
    public StateVariable6D(List<Float> stateVals) {
        if (stateVals.size() != 6)
            throw new IndexOutOfBoundsException("Tried to make a StateVariable6D with the wrong number of values.");

        x = stateVals.get(0);
        y = stateVals.get(1);
        th = stateVals.get(2);
        dx = stateVals.get(3);
        dy = stateVals.get(4);
        dth = stateVals.get(5);
    }

    public StateVariable6D(float[] stateVals) {
        if (stateVals.length != 6)
            throw new IndexOutOfBoundsException("Tried to make a StateVariable6D with the wrong number of values.");

        x = stateVals[0];
        y = stateVals[1];
        th = stateVals[2];
        dx = stateVals[3];
        dy = stateVals[4];
        dth = stateVals[5];
    }

    /**
     * Get the horizontal position of the body.
     *
     * @return Horizontal position of the body.
     */
    public float getX() {
        return x;
    }

    /**
     * Get the vertical position of the body.
     *
     * @return Vertical position of the body.
     */
    public float getY() {
        return y;
    }

    /**
     * Get the counterclockwise angle of the body.
     *
     * @return Counterclockwise angle of the body.
     */
    public float getTh() {
        return th;
    }

    /**
     * Get the horizontal velocity of the body.
     *
     * @return Horizontal velocity of the body.
     */
    public float getDx() {
        return dx;
    }

    /**
     * Get the vertical velocity of the body.
     *
     * @return Vertical velocity of the body.
     */
    public float getDy() {
        return dy;
    }

    /**
     * Get the counterclockwise angular rate of the body.
     *
     * @return Counterclockwise angular rate of the body.
     */
    public float getDth() {
        return dth;
    }


    public float getStateByName(StateName name) {
        float stateValue;
        switch(name) {
            case DTH:
                stateValue = getDth();
                break;
            case DX:
                stateValue = getDx();
                break;
            case DY:
                stateValue = getDy();
                break;
            case TH:
                stateValue = getTh();
                break;
            case X:
                stateValue = getX();
                break;
            case Y:
                stateValue = getY();
                break;
            default:
                throw new RuntimeException("Unknown object state queried.");
        }

        return stateValue;
    }

    public StateVariable6D add(StateVariable6D other) {
        return new StateVariable6D(getX() + other.getX(), getY() + other.getY(), getTh() + other.getTh(),
                getDx() + other.getDx(), getDy() + other.getDy(), getDth() + other.getDth());
    }
    public StateVariable6D subtract(StateVariable6D other) {
        return new StateVariable6D(getX() - other.getX(), getY() - other.getY(), getTh() - other.getTh(),
                getDx() - other.getDx(), getDy() - other.getDy(), getDth() - other.getDth());
    }
    public StateVariable6D multiply(StateVariable6D other) {
        return new StateVariable6D(getX() * other.getX(), getY() * other.getY(), getTh() * other.getTh(),
                getDx() * other.getDx(), getDy() * other.getDy(), getDth() * other.getDth());
    }
    public StateVariable6D divide(StateVariable6D other) {
        return new StateVariable6D(getX() / (other.getX() == 0 ? 1 : other.getX()),
                getY() / (other.getY() == 0 ? 1 : other.getY()),
                getTh() / (other.getTh() == 0 ? 1 : other.getTh()),
                getDx() / (other.getDx() == 0 ? 1 : other.getDx()),
                getDy() / (other.getDy() == 0 ? 1 : other.getDy()),
                getDth() / (other.getDth() == 0 ? 1 : other.getDth()));
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || obj.getClass() != this.getClass()) {
            return false;
        }

        StateVariable6D other = (StateVariable6D) obj;
        EqualsBuilder equalsBuilder = new EqualsBuilder();
        equalsBuilder.append(this.getX(), other.getX());
        equalsBuilder.append(this.getY(), other.getY());
        equalsBuilder.append(this.getTh(), other.getTh());
        equalsBuilder.append(this.getDx(), other.getDx());
        equalsBuilder.append(this.getDy(), other.getDy());
        equalsBuilder.append(this.getDth(), other.getDth());

        return equalsBuilder.isEquals();
    }

    @Override
    public int hashCode() {
        HashCodeBuilder hashCodeBuilder = new HashCodeBuilder();
        hashCodeBuilder.append(this.getX());
        hashCodeBuilder.append(this.getY());
        hashCodeBuilder.append(this.getTh());
        hashCodeBuilder.append(this.getDx());
        hashCodeBuilder.append(this.getDy());
        hashCodeBuilder.append(this.getDth());

        return hashCodeBuilder.toHashCode();
    }
}