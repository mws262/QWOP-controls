package TreeStages;

import java.util.ArrayList;
import java.util.List;

import main.IDataSaver;
import main.ISampler;
import main.Node;
import main.TreeStage;

public class TreeStage_SearchForever extends TreeStage {

    public TreeStage_SearchForever(ISampler sampler, IDataSaver saver) {
        this.sampler = sampler;
        this.saver = saver;
    }

    @Override
    public List<Node> getResults() {
        List<Node> resultList = new ArrayList<>();
        resultList.add(getRootNode()); // No particularly interesting results.
        return resultList;
    }

    @Override
    public boolean checkTerminationConditions() {
        return getRootNode().fullyExplored.get(); // Only termination condition is a completely explored tree. Unlikely when the selection pool is good.
    }

}
