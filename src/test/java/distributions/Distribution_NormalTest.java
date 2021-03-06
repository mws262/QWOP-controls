package distributions;

import game.action.Action;
import game.qwop.CommandQWOP;
import org.junit.Assert;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.ArrayList;
import java.util.List;

public class Distribution_NormalTest {

    @Rule
    public final ExpectedException exception = ExpectedException.none(); // For asserting that exceptions should occur.

    @Test
    public void randOnDistribution() {
        Action a1 = new Action(5, CommandQWOP.NONE);
        Action a2 = new Action(10, CommandQWOP.QO);
        Action a3 = new Action(4, CommandQWOP.O);
        Action a4 = new Action(6, CommandQWOP.P);

        List<Action> actionList = new ArrayList<>();
        actionList.add(a1);
        actionList.add(a2);
        actionList.add(a3);
        actionList.add(a4);

        // Distribution with 0 standard deviation should always produce the same value.
        Distribution<Action> distributionZeroStdev = new Distribution_Normal(5, 0);

        // See if it finds the command with duration equalling the mean.
        Action selectedAction = distributionZeroStdev.randOnDistribution(actionList);
        Assert.assertEquals(a1, selectedAction);

        // Remove the precisely equal one. It should find the first of the two nearest.
        actionList.remove(a1);
        selectedAction = distributionZeroStdev.randOnDistribution(actionList);
        Assert.assertEquals(a3, selectedAction);

        // Remove the next-closest.
        actionList.remove(a3);
        selectedAction = distributionZeroStdev.randOnDistribution(actionList);
        Assert.assertEquals(a4, selectedAction);

        // Only one remaining.
        actionList.remove(a4);
        selectedAction = distributionZeroStdev.randOnDistribution(actionList);
        Assert.assertEquals(a2, selectedAction);
    }

    @Test
    public void constructorIllegalArgumentException() {
        exception.expect(IllegalArgumentException.class);

        new Distribution_Normal(5, -1); // Only non-negative standard deviation.
    }

    @Test
    public void randOnDistributionIllegalArgumentException() {
        exception.expect(IllegalArgumentException.class);

        Distribution<Action> distribution = new Distribution_Normal(5, 1);
        distribution.randSample(new ArrayList<>()); // Empty list should not be tolerated.
    }

    @Test
    public void equalsTest() {
        Distribution_Normal distNorm1 = new Distribution_Normal(8,1);
        Distribution_Normal distNorm2 = new Distribution_Normal(8,1);
        Distribution_Normal distNorm3 = new Distribution_Normal(8,2);
        Distribution_Normal distNorm4 = new Distribution_Normal(7,1);
        Distribution_Normal distNorm5 = new Distribution_Normal(7,6);

        Distribution_Equal distEq = new Distribution_Equal();
        Object object = new Object();

        Assert.assertEquals(distNorm1, distNorm1);
        Assert.assertEquals(distNorm1, distNorm2);
        Assert.assertNotEquals(distNorm1, distNorm3);
        Assert.assertNotEquals(distNorm1, distNorm4);
        Assert.assertNotEquals(distNorm1, distNorm5);

        Assert.assertNotEquals(distNorm1, distEq);
        Assert.assertNotEquals(distNorm1, object);
    }
}