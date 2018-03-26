package main;

import javax.swing.event.ChangeEvent;

import game.GameLoader;

public interface IUserInterface extends Runnable{

	/** Main graphics loop. **/
	@Override
	void run();

	/** Stop the FSM. **/
	void kill();

	/** Pick a node for the UI to highlight and potentially display. **/
	void selectNode(Node selected);

	void addRootNode(Node node);
	
	public interface TabbedPaneActivator {
		public void activateTab();
		public void deactivateTab();
		public boolean isActive();
	}
}