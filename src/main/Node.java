package main;
import java.awt.Color;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.jogamp.opengl.GL2;

import data.SaveableSingleGame;

/*
 * This version will hopefully get rid of many legacy features.
 * Also, this should handle adding actions which are not from a 
 * predetermined set.
 * 
 */
public class Node {

	/******** All node stats *********/
	private static int nodesCreated = 0;
	private static int nodesImported = 0;
	private static int gamesImported = 0;
	private static int gamesCreated = 0;

	/** Some sampling methods want to track how many times this node has been visited. **/
	public int visitCount = 0;
	public float ucbValue = 0;

	/********* QWOP IN/OUT ************/
	/** Keys and duration **/
	private final Action action; //Actual delay used as control.

	/** What is the state after taking this node's action? **/
	public State state;

	/** If assigned, this automatically adds potential actions to children when they are created. This makes the functionality a little
	 * more like the old version which selected from a fixed pool of durations for each action in the sequence. This time, the action 
	 * generator can use anything arbitrary to decide what the potential children are, and the potentialActionGenerator can be hot swapped
	 * at any point.
	 **/
	public static IActionGenerator potentialActionGenerator;

	/********* TREE CONNECTION INFO ************/

	/** Node which leads up to this node. **/
	public final Node parent; // Parentage may not be changed.

	/** Child nodes. Not fixed size any more. **/
	public CopyOnWriteArrayList<Node> children = new CopyOnWriteArrayList<Node>();

	/** Untried child actions. **/
	public ActionSet uncheckedActions;

	/** Are there any untried things below this node? **/
	public boolean fullyExplored = false;

	/** Does this node represent a failed state? Stronger than fullyExplored. **/
	public boolean isTerminal = false;

	/** How deep is this node down the tree? 0 is root. **/
	public final int treeDepth;

	/** Maximum depth yet seen in this tree. **/
	public static int maxDepthYet = 1;

	/********* TREE VISUALIZATION ************/
	public Color overrideLineColor = null; // Only set when the color is overridden.
	public Color overrideNodeColor = null; // Only set when the color is overridden.

	public Color nodeColor = Color.GREEN;

	private static final float lineBrightness_default = 0.85f;
	private float lineBrightness = lineBrightness_default;

	public boolean displayPoint = false; // Round dot at this node. Is it on?
	public boolean displayLine = true; // Line from this node to parent. Is it on?

	/********* NODE PLACEMENT ************/

	/** Parameters for visualizing the tree **/
	public float[] nodeLocation = new float[3]; // Location that this node appears on the tree visualization
	public float nodeAngle = 0; // Keep track of the angle that the line previous node to this node makes.
	public float sweepAngle = 2f*(float)Math.PI;

	public float edgeLength = 1.f;

	/** If we want to bring out a certain part of the tree so it doesn't hide under other parts, give it a small z offset. **/
	private float zOffset = 0.f; 


	/*********** UTILITY **************/

	/** Are we bulk adding saved nodes? If so, some construction things are deferred. **/
	private static boolean currentlyAddingSavedNodes = false;

	/**********************************/
	/********** CONSTRUCTORS **********/
	/**********************************/

	/** 
	 * Make a new node which is NOT the root. Add this to the tree hierarchy.
	 **/
	public Node(Node parent, Action action) {
		this.parent = parent;
		treeDepth = parent.treeDepth + 1;
		this.action = action;

		// Error check for duplicate actions.
		for (Node parentChildren : parent.children){
			if (parentChildren.action == action){
				throw new RuntimeException("Tried to add a duplicate action node at depth " + treeDepth + ". Action was: " + action.toString() + ".");
			}	
		}
		parent.children.add(this);
		if (treeDepth > maxDepthYet){
			maxDepthYet = treeDepth;
		}

		// Add some child actions to try if an action generator is assigned.
		autoAddUncheckedActions();

		edgeLength = 5.00f * (float)Math.pow(0.6947, 0.1903 * treeDepth ) + 1.5f;
		//(float)Math.pow(0.787, 0.495) + 1.0f; // Optimized exponential in Matlab 

		lineBrightness = parent.lineBrightness;
		zOffset = parent.zOffset;

		if (!currentlyAddingSavedNodes){
			calcNodePos();
		}
	}


	/** 
	 * Make a new node which is NOT the root. connectNodeToTree is the default.
	 **/
	public Node(Node parent, Action action, boolean connectNodeToTree) {
		this.parent = parent;
		treeDepth = parent.treeDepth + 1;
		this.action = action;

		// Error check for duplicate actions.
		for (Node parentChildren : parent.children){
			if (parentChildren.action == action){
				throw new RuntimeException("Tried to add a duplicate action node at depth " + treeDepth + ". Action was: " + action.toString() + ".");
			}	
		}

		if (connectNodeToTree) {
			parent.children.add(this);
			if (treeDepth > maxDepthYet){
				maxDepthYet = treeDepth;
			}
		}

		// Add some child actions to try if an action generator is assigned.
		autoAddUncheckedActions();

		edgeLength = 5.00f * (float)Math.pow(0.6947, 0.1903 * treeDepth ) + 1.5f;
		//(float)Math.pow(0.787, 0.495) + 1.0f; // Optimized exponential in Matlab 

		lineBrightness = parent.lineBrightness;
		zOffset = parent.zOffset;

		if (connectNodeToTree && !currentlyAddingSavedNodes){
			calcNodePos();
		}
	}

	/**
	 * Make the root of a new tree.
	 **/
	public Node() {
		parent = null;
		action = null;
		treeDepth = 0; 
		nodeLocation[0] = 0f;
		nodeLocation[1] = 0f;
		nodeLocation[2] = 0f;

		nodeColor = Color.BLUE;
		displayPoint = true;

		// Root node gets the QWOP initial condition.
		setState(QWOPGame.getInitialState());

		// Add some child actions to try if an action generator is assigned.
		autoAddUncheckedActions();
	}

	/************************************/
	/******* ADDING TO THE TREE *********/
	/************************************/

	/** Add a new child node from a given action. If the action is in uncheckedActions, remove it. **/
	public Node addChild(Action childAction) {
		uncheckedActions.remove(childAction);
		nodesCreated++;
		return new Node(this,childAction);
	}

	/** If we've assigned a potentialActionGenerator, this can auto-add potential child actions. Ignores duplicates. **/
	private void autoAddUncheckedActions() {
		// If we've set rules to auto-select potential children, do so.
		if (potentialActionGenerator != null) {
			ActionSet potentialActions = potentialActionGenerator.getPotentialChildActionSet(this);

			// If no unchecked actions have been previously added (must have assigned a sampling distribution to do so),
			// then just use the new one outright.
			if (uncheckedActions == null) {
				uncheckedActions = potentialActions;
			}else { // Otherwise, just use the existing distribution, but add the new actions anyway.
				for (Action potentialAction : potentialActions) {
					if (!uncheckedActions.contains(potentialAction)) {
						uncheckedActions.add(potentialAction);
					}
				}
			}
		}
	}

	/***********************************************/
	/******* GETTING CERTAIN SETS OF NODES *********/
	/***********************************************/

	/** Get a random child **/
	public Node getRandomChild(){
		return children.get(Utility.randInt(0,children.size()-1));
	}

	/** Add all the nodes below and including this one to a list. Does not include nodes whose state have not yet been assigned. **/
	public ArrayList<Node> getNodes_below(ArrayList<Node> nodeList){
		if (state != null){
			nodeList.add(this);
		}
		for (Node child : children){
			child.getNodes_below(nodeList);
		}	
		return nodeList;
	}


	/** Locate all the endpoints in the tree. Starts from the node it is called from. **/
	public void getLeaves(List<Node> descendants){
		for (Node child : children){
			if (child.children.isEmpty()){
				descendants.add(child);
			}else{
				child.getLeaves(descendants);
			}
		}	
	}

	/** Returns the tree root no matter which node in the tree this is called from. **/
	public Node getRoot(){
		Node currentNode = this;
		while (currentNode.treeDepth > 0){
			currentNode = currentNode.parent;
		}
		return currentNode;
	}

	/** Recount how many descendants this node has. **/
	public int countDescendants(){
		int count = 0;
		for (Node current : children){
			count++;
			count += current.countDescendants(); // Recurse down through the tree.
		}
		return count;
	}

	/** Check whether a node is an ancestor of this one. */
	public boolean isOtherNodeAncestor(Node otherNode){
		if (otherNode.treeDepth >= this.treeDepth){ // Don't need to check if this is as far down the tree.
			return false;
		}
		Node currNode = parent;

		while (currNode.treeDepth != otherNode.treeDepth){ // Find the node at the same depth as the one we're checking.
			currNode = currNode.parent;
		}

		if (currNode.equals(otherNode)){
			return true;
		}

		return false;
	}

	/** Change whether this node or any above it have become fully explored. 
	 * This is lite because it assumes all existing fullyExplored tags in its children are accurate. 
	 * Call from a leaf node that we just assigned to be fully explored.
	 **/
	public void checkFullyExplored_lite(){
		boolean flag = true;

		if (!state.failedState) {
			if (!uncheckedActions.isEmpty()){
				flag = false;
			}
			for (Node child : children){
				if (!child.fullyExplored){ // If any child is not fully explored, then this node isn't too.
					flag = false;
				}
			}
		}
		fullyExplored = flag;
		if (treeDepth > 0){ // We already know this node is fully explored, check the parent.
			parent.checkFullyExplored_lite();
		}
	}

	/** Check from this node and below which nodes are fully explored. Good to call this after a 
	 * bunch of nodes have been loaded. 
	 **/
	public void checkFullyExplored_complete(){
		ArrayList<Node> leaves = new ArrayList<Node>();
		getLeaves(leaves);

		// Reset all existing exploration flags out there.
		for (Node leaf : leaves){
			Node currNode = leaf;
			while (currNode.treeDepth > 0){
				currNode.fullyExplored = false;
				currNode = currNode.parent;
			}
			currNode.fullyExplored = false;
		}

		for (Node leaf : leaves){
			leaf.checkFullyExplored_lite();
		}
	}

	/** Total number of nodes in this or any tree. **/
	public static int getTotalNodeCount(){
		return nodesImported + nodesCreated;
	}

	/** Total number of nodes imported from save file. **/
	public static int getImportedNodeCount(){
		return nodesImported;
	}

	/** Total number of nodes created this session. **/
	public static int getCreatedNodeCount(){
		return nodesCreated;
	}

	/** Total number of nodes created this session. **/
	public static int getImportedGameCount(){
		return gamesImported;
	}
	/** Total number of nodes created this session. **/
	public static int getCreatedGameCount(){
		return gamesCreated;
	}

	/** Mark this node as representing a terminal state. **/
	public void markTerminal(){
		isTerminal = true;
		gamesCreated++;
	}

	/***************************************************/
	/******* STATE & SEQUENCE SETTING/GETTING **********/
	/***************************************************/

	//	/** Capture the state from an active game **/
	//	@Deprecated
	//	public void captureState(QWOPInterface QWOPHandler){
	//		state = new CondensedStateInfo(QWOPHandler.game); // MATT add 8/22/17
	//	}

	/** Capture the state from an active game **/
	public void captureState(QWOPGame game){
		state = new State(game); // MATT add 8/22/17
	}

	/** Assign the state directly. Usually when loading nodes. **/
	public void setState(State newState){
		state = newState;
	}

	/** Get the action object (keypress + duration) that leads to this node from its parent. **/
	public Action getAction(){
		action.reset(); // Make sure internal counter for executing this action is reset.
		return action;
	}

	/** Get the sequence of actions up to, and including this node **/
	public synchronized Action[] getSequence(){
		Action[] sequence = new Action[treeDepth];
		if (treeDepth == 0) return sequence; // Empty array for root node.
		Node current = this;
		sequence[treeDepth-1] = current.action;
		for (int i = treeDepth - 2; i >= 0; i--){
			current = current.parent;
			sequence[i] = current.action;
		}
		return sequence;
	}

	/****************************************/
	/******* SAVE/LOAD AND UTILITY **********/
	/****************************************/

	/* Takes a list of runs and figures out the tree hierarchy without duplicate objects. Returns the ROOT of a tree. */
	public static Node makeNodesFromRunInfo(ArrayList<SaveableSingleGame> runs, boolean initializeTreePhysics){
		Node rootNode = new Node();
		return makeNodesFromRunInfo(runs, rootNode);
	}

	/* Takes a list of runs and figures out the tree hierarchy without duplicate objects. Adds to an existing given root. **/
	public static Node makeNodesFromRunInfo(ArrayList<SaveableSingleGame> runs, Node existingRootToAddTo){
		Node rootNode = existingRootToAddTo;
		currentlyAddingSavedNodes = true;
		for (SaveableSingleGame run : runs){ // Go through all runs, placing them in the tree.
			Node currentNode = rootNode;	

			for (int i = 0; i < run.actions.length; i++){ // Iterate through individual actions of this run, travelling down the tree in the process.

				boolean foundExistingMatch = false;
				for (Node child: currentNode.children){ // Look at each child of the currently investigated node.
					if (child.action == run.actions[i]){ // If there is already a node for the action we are trying to place, just use it.
						currentNode = child;
						foundExistingMatch = true;
						break; // We found a match, move on.
					}
				}
				// If this action is unique at this point in the tree, we need to add a new node there.
				if (!foundExistingMatch){
					Node newNode = new Node(currentNode, run.actions[i]);
					newNode.setState(run.states[i]);
					newNode.calcNodePos();
					currentNode = newNode;
					nodesImported++;
				}
			}
			gamesImported++;
		}
		rootNode.checkFullyExplored_complete(); // Handle marking the nodes which are fully explored.
		currentlyAddingSavedNodes = false;
		rootNode.calcNodePos_below();
		return rootNode;
	}

	/************************************************/
	/*******         NODE POSITIONING      **********/
	/************************************************/

	/** Vanilla node positioning from all previous versions.
	 * Makes a rough best guess for position based on its parent.
	 * Should still be used for initial conditions before letting
	 * the physics kick in.
	 **/
	public void calcNodePos(float[] nodeLocationsToAssign){
		//Angle of this current node -- parent node's angle - half the total sweep + some increment so that all will span the required sweep.
		if(treeDepth >= 0){
			if (parent.children.size() + ((parent.uncheckedActions == null) ? 0 :parent.uncheckedActions.size()) > 1){ //Catch the div by 0
				int division = parent.children.size() + parent.uncheckedActions.size(); // Split into this many chunks.
				int childNo = parent.children.indexOf(this);

				sweepAngle = (parent.sweepAngle/division) * (1 + treeDepth * 0.05f);

				if (childNo == 0) {
					nodeAngle = parent.nodeAngle;
				}else if (childNo % 2 == 0) {
					nodeAngle = parent.nodeAngle + sweepAngle * childNo/2;
				}else {
					nodeAngle = parent.nodeAngle - sweepAngle * (childNo + 1)/2;
				}
			}else{
				sweepAngle = parent.sweepAngle; //Only reduce the sweep angle if the parent one had more than one child.
				nodeAngle = parent.nodeAngle;
			}
		}

		nodeLocationsToAssign[0] = (float) (parent.nodeLocation[0] + edgeLength*Math.cos(nodeAngle));
		nodeLocationsToAssign[1] = (float) (parent.nodeLocation[1] + edgeLength*Math.sin(nodeAngle));
		nodeLocationsToAssign[2] = 0f; // No out of plane stuff yet.
	}

	/** Same, but assumes that we're talking about this node's nodeLocation **/
	public void calcNodePos(){
		calcNodePos(nodeLocation);
	}

	/** Recalculate all node positions below this one (NOT including this one for the sake of root). **/
	public void calcNodePos_below(){		
		for (Node current : children){
			current.calcNodePos();	
			current.calcNodePos_below(); // Recurse down through the tree.
		}
	}

	/*******************************/
	/******* TREE DRAWING **********/
	/*******************************/

	/** Draw the line connecting this node to its parent. **/
	public void drawLine(GL2 gl){
		if (treeDepth > 0 && displayLine){ // No lines for root.
			if (overrideLineColor == null){
				gl.glColor3fv(getColorFromTreeDepth(treeDepth,lineBrightness).getColorComponents(null),0);
			}else{
				// The most obtuse possible way to change just the brightness of the color. Rediculous, but I can't find anything else.
				float[] color = Color.RGBtoHSB(overrideLineColor.getRed(),overrideLineColor.getGreen(),overrideLineColor.getBlue(),null);
				gl.glColor3fv(Color.getHSBColor(color[0], color[1], lineBrightness).getColorComponents(null),0);
			}
			gl.glVertex3d(parent.nodeLocation[0], parent.nodeLocation[1], parent.nodeLocation[2] + zOffset);
			gl.glVertex3d(nodeLocation[0], nodeLocation[1], nodeLocation[2] + zOffset);
		}
	}

	/** Draw the node point if enabled **/
	public void drawPoint(GL2 gl){
		if(displayPoint){
			if (overrideNodeColor == null){
				gl.glColor3fv(nodeColor.getColorComponents(null),0);
			}else{
				gl.glColor3fv(overrideNodeColor.getColorComponents(null),0);
			}
			gl.glVertex3d(nodeLocation[0], nodeLocation[1], nodeLocation[2] + zOffset);
		}
	}


	/** Draw all lines in the subtree below this node **/
	public void drawLines_below(GL2 gl){	
		Iterator<Node> iter = children.iterator();
		while (iter.hasNext()){
			Node current = iter.next();
			current.drawLine(gl);
			if (current.treeDepth <= this.treeDepth) throw new RuntimeException("Node hierarchy problem. Node with an equal or lesser depth is below another. At " + current.treeDepth + " and " + this.treeDepth + ".");
			current.drawLines_below(gl); // Recurse down through the tree.
		}
	}

	/** Draw all nodes in the subtree below this node. **/
	public void drawNodes_below(GL2 gl){
		drawPoint(gl);
		Iterator<Node> iter = children.iterator();
		while (iter.hasNext()){
			Node child = iter.next();
			child.drawPoint(gl);	
			child.drawNodes_below(gl); // Recurse down through the tree.
		}
	}

	/** Single out one run up to this node to highline the lines, while dimming the others. **/
	public void highlightSingleRunToThisNode(){
		Node rt = getRoot();
		rt.setLineBrightness_below(0.4f); // Fade the entire tree, then go and highlight the run we care about.

		Node currentNode = this;
		while (currentNode.treeDepth > 0){
			currentNode.setLineBrightness(0.85f);
			currentNode = currentNode.parent;
		}
	}

	/** Fade a single line going from this node to its parent. **/
	public void setLineBrightness(float brightness){
		lineBrightness = brightness;
	}

	/** Fade a certain part of the tree. **/
	public void setLineBrightness_below(float brightness){
		setLineBrightness(brightness);
		for (Node child : children){
			child.setLineBrightness_below(brightness);
		}
	}

	/** Reset line brightnesses to default. **/
	public void resetLineBrightness_below(){
		setLineBrightness_below(lineBrightness_default);
	}

	/** Color the node scaled by depth in the tree. Skip the brightness argument for default value. **/
	public static Color getColorFromTreeDepth(int depth, float brightness){
		float coloffset = 0.35f;
		float scaledDepth = (float)depth/(float)maxDepthYet;
		float H = scaledDepth* 0.38f+coloffset;
		float S = 0.8f;
		return Color.getHSBColor(H, S, brightness);
	}

	/** Color the node scaled by depth in the tree. Totally for gradient pleasantness. **/
	public static Color getColorFromTreeDepth(int depth){
		return getColorFromTreeDepth(depth, lineBrightness_default);
	}

	/** Set an override line color for this branch (all descendants). **/
	public void setBranchColor(Color newColor){
		overrideLineColor = newColor;
		for (Node child : children){
			child.setBranchColor(newColor);
		}
	}

	/** Clear an overriden line color on this branch. Call from root to get all line colors back to default. **/
	public void clearBranchColor(){
		overrideLineColor = null;
		for (Node child : children){
			child.clearBranchColor();
		}
	}

	/** Clear node override colors from this node onward. Only clear the specified color. Call from root to clear all. **/
	public void clearNodeOverrideColor(Color colorToClear){
		if (overrideNodeColor == colorToClear){
			overrideNodeColor = null;
			displayPoint = false;
		}
		for (Node child : children){
			child.clearNodeOverrideColor(colorToClear);
		}
	}

	/** Clear all node override colors from this node onward. Call from root to clear all. **/
	public void clearNodeOverrideColor(){
		if (overrideNodeColor != null){
			overrideNodeColor = null;
			displayPoint = false;
		}
		for (Node child : children){
			child.clearNodeOverrideColor();
		}
	}

	/** Give this branch a zOffset to make it stand out. **/
	public void setBranchZOffset(float zOffset){
		this.zOffset = zOffset;
		for (Node child : children){
			child.setBranchZOffset(zOffset);
		}
	}

	/** Give this branch a zOffset to make it stand out. Goes backwards towards root. **/
	public void setBackwardsBranchZOffset(float zOffset){
		this.zOffset = zOffset;
		Node currentNode = this;
		while (currentNode.treeDepth > 0){
			currentNode.zOffset = zOffset;
			currentNode = currentNode.parent;
		}
	}

	/** Clear z offsets in this branch. Works backwards towards root. **/
	public void clearBackwardsBranchZOffset(){
		setBackwardsBranchZOffset(0f);
	}

	/** Clear z offsets in this branch. Called from root, it resets all back to 0. **/
	public void clearBranchZOffset(){
		setBranchZOffset(0f);
	}
}
