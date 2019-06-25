package savers;

import game.action.Action;
import game.IGameInternal;
import game.state.IState;
import tree.node.NodeQWOPBase;

import java.util.List;

/**
 * Data saver placeholder for when we don't wish to actually save anything.
 *
 * @author Matt
 */
public class DataSaver_Null implements IDataSaver {

    @Override
    public void reportGameInitialization(IState initialState) {}

    @Override
    public void reportTimestep(Action action, IGameInternal game) {}

    @Override
    public void reportGameEnding(NodeQWOPBase<?> endNode) {}

    @Override
    public void setSaveInterval(int numGames) {}

    @Override
    public void setSavePath(String fileLoc) {}

    @Override
    public void reportStageEnding(NodeQWOPBase<?> rootNode, List<NodeQWOPBase<?>> targetNodes) {}

    @Override
    public void finalizeSaverData() {}

    @Override
    public DataSaver_Null getCopy() {
        return new DataSaver_Null();
    }
}
