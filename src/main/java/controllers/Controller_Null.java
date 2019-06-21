package controllers;

import game.action.Action;
import tree.node.NodeQWOPExplorableBase;

/**
 * A do-nothing placeholder controller. It always concludes that no keys should be pressed.
 *
 * @author matt
 */
public class Controller_Null implements IController {

    @Override
    public Action policy(NodeQWOPExplorableBase<?> state) {
        return new Action(1, false, false, false, false);
    }

    @Override
    public IController getCopy() {
        return new Controller_Null();
    }
}
