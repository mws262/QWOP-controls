package goals.q_learning;

import game.cartpole.CartPole;
import goals.q_learning.PolicyQNetwork.Timestep;

import java.io.File;
import java.io.FileNotFoundException;

// See https://github.com/simoninithomas/Deep_reinforcement_learning_Course/blob/master/Deep%20Q%20Learning/Space
// %20Invaders/DQN%20Atari%20Space%20Invaders.ipynb
public class PolicyQCartPole {

    private final CartPole cartPole = new CartPole();
    PolicyQNetwork net;

    PolicyQCartPole(PolicyQNetwork net) {
        this.net = net;
        cartPole.connect(true);
    }

    // Returns the first ts in a game. Is a forward-linked list.
    public void playGame() {
        cartPole.reset();
        boolean done = false;

        Timestep firstTs = new Timestep();
        Timestep prevTs = null;
        Timestep currentTs = firstTs;
        int total = 0;
        while (!done) {
            net.addTimestep(currentTs);

            currentTs.state = cartPole.getCurrentState();
            currentTs.action = net.policyExplore(currentTs.state);
            cartPole.step(currentTs.action);
            currentTs.reward = (float) cartPole.getLastReward();
            done = cartPole.isDone();

            if (prevTs != null) {
                prevTs.nextTs = currentTs;
            }
            prevTs = currentTs;
            currentTs = new Timestep();

            net.incrementDecay();
            total++;
        }
        prevTs.reward = -prevTs.reward; // TMP penalty for last ts.
        System.out.print("ts: " + total + ", ");
    }

    public void justEvaluate() {
        cartPole.reset();
        int ts = 0;
        while (!cartPole.isDone()) {
            float[] state = cartPole.getCurrentState();
            int action = net.policyGreedy(state);
            cartPole.step(action);
            ts++;
            System.out.print(action);
        }
        System.out.println("Greedy evaluation went for: " + ts);
    }

    public static void main(String[] args) throws FileNotFoundException {
//        List<Integer> layerSizes = new ArrayList<>();
//        layerSizes.add(CartPole.STATE_SIZE);
//        layerSizes.add(4);
//        layerSizes.add(2);
//        layerSizes.add(CartPole.ACTIONSPACE_SIZE);
//
//        List<String> addedArgs = new ArrayList<>();
//        addedArgs.add("-lr");
//        addedArgs.add("1e-3");
//        addedArgs.add("-a");
//        addedArgs.add("relu");
//        PolicyQNetwork net = PolicyQNetwork.makeNewNetwork("src/main/resources/tflow_models/cpole.pb",
//                layerSizes, addedArgs, true);

        PolicyQNetwork net = new PolicyQNetwork(new File("./src/main/resources/tflow_models/dqn.pb"), false);
        PolicyQCartPole policy = new PolicyQCartPole(net);
        net.outputSize = 2; // TMP override since there are more sets of layers than we're used to.
        for (int i = 0; i < 10000000; i++) {
            policy.playGame();
            float loss = net.train(1);
            System.out.println("epi: " + i + ", loss: " + loss);
            if (i % 10 == 0) {
                System.out.println("Greedy evaluation coming up!");
                policy.justEvaluate();
            }
        }
    }
}