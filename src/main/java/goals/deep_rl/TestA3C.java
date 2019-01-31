package goals.deep_rl;

import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscrete;
import org.deeplearning4j.rl4j.learning.async.a3c.discrete.A3CDiscreteDense;
import org.deeplearning4j.rl4j.mdp.MDP;
import org.deeplearning4j.rl4j.network.ac.ActorCriticFactorySeparateStdDense;
import org.deeplearning4j.rl4j.policy.ACPolicy;
import org.deeplearning4j.rl4j.space.Box;
import org.deeplearning4j.rl4j.util.DataManager;
import org.nd4j.linalg.learning.config.Adam;

import java.io.IOException;

public class TestA3C {

    private static A3CDiscrete.A3CConfiguration QWOP_A3C =
            new A3CDiscrete.A3CConfiguration(
                    123,            //Random seed
                    200,            //Max step By epoch
                    500000,         //Max step
                    16,              //Number of threads
                    5,              //t_max
                    0,             //num step noop warmup
                    0.01,           //reward scaling
                    0.99,           //gamma
                    10.0           //td-error clipping
            );


    private static final ActorCriticFactorySeparateStdDense.Configuration QWOP_NET_A3C =
            ActorCriticFactorySeparateStdDense.Configuration
            .builder().updater(new Adam(1e-4)).l2(0).numHiddenNodes(16).numLayer(3).build();

    public static void main(String[] args) throws IOException {
        TestA3C();
    }

    public static void TestA3C() throws IOException {

        //record the training data in rl4j-data in a new folder
        DataManager manager = new DataManager(true);

        MDP gameMDP = new QWOP_MDP_Threadable();

        //define the training
        A3CDiscreteDense<Box> a3c = new A3CDiscreteDense<Box>(gameMDP, QWOP_NET_A3C, QWOP_A3C, manager);
        //start the training
        a3c.train();

        ACPolicy<Box> pol = a3c.getPolicy();

        pol.save("/tmp/val1/", "/tmp/pol1");

        //close the mdp (http connection)
        gameMDP.close();

        //reload the policy, will be equal to "pol", but without the randomness
        ACPolicy<Box> pol2 = ACPolicy.load("/tmp/val1/", "/tmp/pol1");
    }
}
