package tree;

import actions.Action;
import actions.IActionGenerator;
import com.jogamp.opengl.GL2;
import com.jogamp.opengl.util.gl2.GLUT;
import game.State;
import value.updaters.IValueUpdater;

import java.awt.*;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Explorable QWOP node which provides no additional functionality beyond drawing capabilities. If you don't need this,
 * use {@link NodeQWOPExplorableBase} for memory efficiency.
 *
 * See {@link NodeGenericBase} for an explanation of the ridiculous generics. It's not as bad as it looks.
 *
 * @param <N>
 */
public abstract class NodeQWOPGraphicsBase<N extends NodeQWOPGraphicsBase<N>> extends NodeQWOPExplorableBase<N> {

    /* Node visibility flags
    Note that by default lines are drawn, but points are not. Overriding the line or point color will not make it
    visible, although this color will be preferred over the normal version until the overridden one is cleared (set
    to null).
     */

    /**
     * Should a dot be displayed at the position of this node?
     */
    public boolean displayPoint = false;

    /**
     * Should a line be drawn from this node's parent's position to this node?
     */
    public boolean displayLine = true;

    /**
     * Enables a text label over this node, if one is assigned.
     */
    public boolean displayLabel = false;

    /**
     * Sets locked nodes to have points drawn. This is useful for seeing what the workers are up to.
     */
    private static final boolean drawLockedNodes = true;

    /**
     * Node location in 3D space.
     */
    public float[] nodeLocation = new float[3]; // Location that this node appears on the tree visualization

    /**
     * Angle of the line between grandparent and parent. Determines the baseline angle that this node and its
     * siblings should go off at +/- some spread between the siblings.
     */
    float nodeAngle = 0; // Keep track of the angle that the line previous node to this node makes.

    /**
     * The arc that this node will share with its siblings. Must get narrower up the tree to accommodate the branching.
     */
    float sweepAngle = 2f * (float) Math.PI;

    /**
     * Length of the line connecting the parent node to this. This is calculated to be biggest near the root to
     * provide spacing for later on. It decays at greater tree depths to make lines less likely to collide with each
     * other.
     */
    private float edgeLength;

    /**
     * Determines whether very close lines/nodes will be drawn. Can greatly speed up UI for very dense trees.
     */
    private static final boolean limitDrawing = true;

    /**
     *
     */
    public static final Set<NodeQWOPGraphicsBase> pointsToDraw = ConcurrentHashMap.newKeySet(10000);


    /**
     * Square distance between the nearest neighbor below which the node and associated lines will not be drawn.
     */
    private static final float nodeDrawFilterDistSq = 0.1f;

    /**
     * If filtering of drawing nodes which are very close is enabled, this will indicate whether this node is one of
     * the ones which is invisible.
     */
    private boolean notDrawnForSpeed = false;

    /**
     * Default color of the point at the node's location, if point drawing is enabled. Any override color will take
     * precedence over this.
     */
    private float[] pointColorFloats = Color.green.getColorComponents(null);
    private float[] overridePointColorFloats;

    /**
     * Default color of the line from this node to its parent, if line drawing is enabled. Any override color will
     * take precedence over this.
     */
    private float[] lineColorFloats;
    private float[] overrideLineColorFloats;

    /**
     * Text label to overlaid at this node's position if label drawing is enabled.
     */
    private String nodeLabel;

    /**
     * Color that text labels will be drawn in, if label drawing is enabled for this node.
     */
    private float[] nodeLabelColor = Color.CYAN.getColorComponents(null);

    /**
     * Default brightness in HSV scale.
     */
    public static final float lineBrightnessDefault = 0.85f;

    /**
     * Brightness of this particular line. Usually changed to make one part of the tree stand out from the rest.
     */
    float lineBrightness = lineBrightnessDefault;

    /**
     * If we want to bring out a certain part of the tree so it doesn't hide under other parts, give it a small z
     * offset.
     */
    float nodeLocationZOffset = 0.4f;

    public NodeQWOPGraphicsBase(State rootState, IActionGenerator actionGenerator) {
        super(rootState, actionGenerator);
        setLineColor(getColorFromTreeDepth(getTreeDepth(), lineBrightness));
    }

    public NodeQWOPGraphicsBase(State rootState) {
        super(rootState);
    }

    NodeQWOPGraphicsBase(N parent, Action action, State state, IActionGenerator actionGenerator, boolean doublyLinked) {
        super(parent, action, state, actionGenerator, doublyLinked);

        setLineColor(getColorFromTreeDepth(getTreeDepth(), lineBrightness));
        edgeLength = 5.00f * (float) Math.pow(0.6947, 0.1903 * getTreeDepth()) + 1.5f;
        lineBrightness = parent.lineBrightness;
        nodeLocationZOffset = parent.nodeLocationZOffset;

        if (doublyLinked)
            calcNodePos();
    }

    /**
     * Draw the line connecting this node to its parent.
     */
    public void drawLine(GL2 gl) {
        if (nodeLocation != null && lineColorFloats != null && ((getTreeDepth() > 0) && displayLine && !notDrawnForSpeed)) { //
            // No lines for root.
            gl.glColor3fv((overrideLineColorFloats == null) ? lineColorFloats : overrideLineColorFloats, 0);
            gl.glVertex3d(getParent().nodeLocation[0], getParent().nodeLocation[1], getParent().nodeLocation[2] + nodeLocationZOffset);
            gl.glVertex3d(nodeLocation[0], nodeLocation[1], nodeLocation[2] + nodeLocationZOffset);
        }
    }

    /**
     * Draw the node point if enabled
     */
    public void drawPoint(GL2 gl) {
        if (nodeLocation != null && displayPoint && !notDrawnForSpeed) {
            gl.glColor3fv((overridePointColorFloats == null) ? pointColorFloats : overridePointColorFloats, 0);
            gl.glVertex3d(nodeLocation[0], nodeLocation[1], nodeLocation[2] + nodeLocationZOffset);
        }
    }

    /**
     * Draw a text string using GLUT. This string will go to a position in world coordinates, not screen coordinates.
     */
    public void drawLabel(GL2 gl, GLUT glut) {
        if (nodeLabel != null && displayLabel && !nodeLabel.isEmpty() && !notDrawnForSpeed) { // I think that the node label can be null
            // even if it is assigned as a object field on construction. Constructors chain in such a way that a child
            // could be added to its parent before node construction is complete.
            gl.glColor3fv(nodeLabelColor, 0);
            gl.glRasterPos3d(nodeLocation[0], nodeLocation[1], nodeLocation[2] + nodeLocationZOffset + 0.1f);
            glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, nodeLabel);
        }
    }

    /**
     * Draw all lines in the subtree below this node.
     *
     * @param gl OpenGL drawing object.
     */
    @SuppressWarnings("unused")
    public void drawLinesBelow(GL2 gl) {
        recurseDownTreeExclusive(n->{
            n.drawLine(gl);
            assert getTreeDepth() < n.getTreeDepth();
        });
    }

    /**
     * Draw all nodes in the subtree below this node.
     *
     * @param gl OpenGL drawing object.
     */
    @SuppressWarnings("unused")
    public void drawPointsBelow(GL2 gl) {
        recurseDownTreeInclusive(n -> n.drawPoint(gl));
    }

    /**
     * Color the node scaled by depth in the tree. Totally for gradient pleasantness.
     */
    public static Color getColorFromTreeDepth(float depth, float brightness) {
        return getColorFromScaledValue(depth, 10f, brightness);
    }

    public static Color getColorFromScaledValue(float val, float max, float brightness) {
        final float colorOffset = 0f;
        final float scaledDepth = val / max;
        final float H = scaledDepth * 0.38f + colorOffset;
        final float S = 0.8f;
        return Color.getHSBColor(H, S, brightness);
    }


    void setLineColor(Color color) {
        lineColorFloats = color.getColorComponents(null);
    }

    void setOverrideLineColor(Color color) {
        if (color == null) {
            overrideLineColorFloats = null;
        } else {
            overrideLineColorFloats = color.getColorComponents(null);
        }
    }

    public void setPointColor(Color color) {
        pointColorFloats = color.getColorComponents(null);
    }

    public void setOverridePointColor(Color color) {
        if (color == null) {
            overridePointColorFloats = null;
        } else {
            overridePointColorFloats = color.getColorComponents(null);
        }
    }

    void setLineBrightness(float brightness) {
        lineBrightness = brightness;
        setLineColor(getColorFromTreeDepth(getTreeDepth(), lineBrightness));
    }

    void calcNodePos() {
        //Angle of this current node -- parent node's angle - half the total sweep + some increment so that all will
        // span the required sweep.
        assert getTreeDepth() > 0;
        int possibleParentBranchSlots = getParent().getChildCount() + getParent().getUntriedActionCount();

        if (possibleParentBranchSlots > 1) { //Catch the div by 0

            int childNo = getIndexAccordingToParent();

            sweepAngle = (float) Math.max((getParent().sweepAngle / (possibleParentBranchSlots))
                    * (1 + (getTreeDepth() - 1) * 0.05f), 0.005);

            // This is to straighten out branches that are curving off to one side due to asymmetric expansion.
            // Acts like a controller to bring the angle towards the angle of the first node in this branch after
            // root.
            float angleAdj;
            N ancestor = getThis();
            while (ancestor.getTreeDepth() > 1) {
                ancestor = ancestor.getParent();
            }
            angleAdj = -0.2f * (getParent().nodeAngle - ancestor.nodeAngle);

            if (childNo == 0) {
                nodeAngle = getParent().nodeAngle + angleAdj;
            } else if (childNo % 2 == 0) {
                nodeAngle = getParent().nodeAngle + sweepAngle * childNo / 2 + angleAdj;
            } else {
                nodeAngle = getParent().nodeAngle - sweepAngle * (childNo + 1) / 2 + angleAdj;
            }
//            nodeAngle = getParent().nodeAngle + sweepAngle * (childNo - possibleParentBranchSlots/2f) + angleAdj;

        } else {
            sweepAngle = getParent().sweepAngle; //Only reduce the sweep angle if the parent one had more than one child.
            nodeAngle = getParent().nodeAngle;
        }

        nodeLocation[0] = (float) (getParent().nodeLocation[0] + edgeLength * Math.cos(nodeAngle));
        nodeLocation[1] = (float) (getParent().nodeLocation[1] + edgeLength * Math.sin(nodeAngle));
        nodeLocation[2] = 0f; // No out of plane stuff yet.

        // Since drawing speed is a UI bottleneck, we may want to filter out some of the points that are REALLY close.
        if (limitDrawing) {
            float xDiff;
            float yDiff;
            float zDiff;
            for (NodeQWOPGraphicsBase n : pointsToDraw) {
                if (n.getTreeDepth() != getTreeDepth()) { // Trying this as a speedup approach for the filtering.
                    // It's rarer for nodes of a different depth to get too close to others.
                    continue;
                }
                xDiff = n.nodeLocation[0] - nodeLocation[0];
                yDiff = n.nodeLocation[1] - nodeLocation[1];
                zDiff = n.nodeLocation[2] - nodeLocation[2];
                // Actually distance squared to avoid sqrt
                if ((xDiff * xDiff + yDiff * yDiff + zDiff * zDiff) < nodeDrawFilterDistSq) {
                    notDrawnForSpeed = true;
                    return; // Too close. Turn it off and get out.
                }
            }
            notDrawnForSpeed = false;
            pointsToDraw.add(this);
        }
    }

    public void reenableIfNotDrawnForSpeed() {
        if (notDrawnForSpeed) {
            setLineColor(getColorFromTreeDepth(getTreeDepth(), lineBrightness));
            notDrawnForSpeed = false;
            pointsToDraw.add(this);
        }
    }

    /**
     * Turn off all display for this node onward.
     */
    public void turnOffBranchDisplay() {
        recurseDownTreeInclusive(n -> {
            displayLine = false;
            displayPoint = false;
            notDrawnForSpeed = true;
            pointsToDraw.remove(this);
        });
    }

    /**
     * Single out one run up to this node to highlight the lines, while dimming the others.
     */
    public void highlightSingleRunToThisNode() {
        getRoot().setLineBrightnessBelow(0.4f); // Fade the entire tree, then go and highlight the run we care about.
        recurseUpTreeInclusive(n -> n.setLineBrightness(lineBrightnessDefault));
    }

    /**
     * Fade a certain part of the tree.
     */
    public void setLineBrightnessBelow(float brightness) {
        recurseDownTreeInclusive(n -> n.setLineBrightness(brightness));
    }

    /**
     * Resets the angle at which child nodes will fan out from this node in the visualization. This must be done
     * before the children are created. The angle is set to 3pi/2.
     */
    public void resetSweepAngle() {
        sweepAngle = (float) (3. * Math.PI / 2.);
    }

    /**
     * Give this branch a nodeLocationZOffset to make it stand out.
     */
    public void setBranchZOffset(float zOffset) {
        recurseDownTreeInclusive(n -> n.nodeLocationZOffset = zOffset);
    }

    /**
     * Give this branch a nodeLocationZOffset to make it stand out. Goes backwards towards root.
     */
    public void setBackwardsBranchZOffset(float zOffset) {
        recurseUpTreeInclusive(n -> n.nodeLocationZOffset = zOffset);
    }

    /**
     * Clear z offsets in this branch. Works backwards towards root.
     */
    public void clearBackwardsBranchZOffset() {
        setBackwardsBranchZOffset(0f);
    }

    /**
     * Clear z offsets in this branch. Called from root, it resets all back to 0.
     */
    public void clearBranchZOffset() {
        setBranchZOffset(0f);
    }

    /**
     * Set an override line color for this branch (all descendants).
     */
    public void setOverrideBranchColor(Color newColor) {
        recurseDownTreeExclusive(n -> n.setOverrideLineColor(newColor));
    }

    /**
     * Set an override line color for this path (all ancestors).
     */
    public void setBackwardsBranchOverrideColor(Color newColor) {
        recurseUpTreeInclusive(n -> n.setOverrideLineColor(newColor));
    }

    /**
     * Clear an overridden line color on this branch. Call from root to get all line colors back to default.
     */
    public void clearBranchLineOverrideColor() {
        setOverrideBranchColor(null);
    }

    /**
     * Clear an overridden line color on this branch. Goes back towards root.
     */
    public void clearBackwardsBranchLineOverrideColor() {
        setBackwardsBranchOverrideColor(null);
    }

    @Override
    public synchronized void updateValue(float valueUpdate, IValueUpdater updater) {
        super.updateValue(valueUpdate, updater);
        displayLabel = true;
        nodeLabel = String.format("%.2f", getValue());
    }

    @Override
    void setLock(boolean isLocked) {
        super.setLock(isLocked);
        if (drawLockedNodes) {
            displayPoint = isLocked;
        }
    }
}