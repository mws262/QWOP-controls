package main;
import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Paint;
import java.awt.Stroke;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Rectangle2D;
import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;

import javax.swing.BorderFactory;
import javax.swing.BoxLayout;
import javax.swing.JComboBox;
import javax.swing.JDialog;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Vector3f;

import org.jblas.*;
import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

import com.jogamp.opengl.*;
import com.jogamp.opengl.GLEventListener;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartMouseEvent;
import org.jfree.chart.ChartMouseListener;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.annotations.XYTextAnnotation;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.Range;
import org.jfree.data.xy.AbstractXYDataset;
import org.jfree.data.xy.XYDataset;
import org.jfree.ui.RectangleInsets;

import com.jogamp.opengl.util.awt.TextRenderer;
import com.jogamp.opengl.util.gl2.GLUT;


/**
 * All UI stuff happens here and most of the analysis that individual panes show happens here too.
 * @author Matt
 */
@SuppressWarnings("serial")
public class FSM_UI extends JFrame implements ChangeListener, Runnable{

	/** Negotiator acts like a listener. **/
	Negotiator negotiator;

	/** Thread loop running? **/
	public boolean running = true;

	/** Tree root nodes associated with this interface. **/
	ArrayList<TrialNodeMinimal> rootNodes = new ArrayList<TrialNodeMinimal>();

	/** Individual pane for the tree. **/
	TreePane treePane;

	/** Pane for the tabbed side of the interface. **/
	JTabbedPane tabPane;

	/** Pane for the runner. **/
	RunnerPane runnerPane;

	/** Pane for the snapshots of the runner. **/
	SnapshotPane snapshotPane;

	/** Pane for comparing different states across the tree. **/
	ComparisonPane comparisonPane;

	/** Plots here. **/
	DataPane dataPane_state;
	DataPane dataPane_pca;

	/** Selected node by user click/key **/
	TrialNodeMinimal selectedNode;

	/** List of panes which can be activated, deactivated. **/
	private ArrayList<TabbedPaneActivator> allTabbedPanes= new ArrayList<TabbedPaneActivator>(); //List of all panes in the tabbed part

	/** Window width **/
	public static int windowWidth = 1920;

	/** Window height **/
	public static int windowHeight = 1000;

	/** Attempted frame rate **/
	private int FPS = 25;

	/** Usable milliseconds per frame **/
	private long MSPF = (long)(1f/(float)FPS * 1000f);

	/** Drawing offsets within the viewing panel (i.e. non-physical) **/
	public int xOffsetPixels_init = 960;
	public int xOffsetPixels = xOffsetPixels_init;
	public int yOffsetPixels = 100;

	/** Runner coordinates to pixels. **/
	public float runnerScaling = 10f;

	private final Color historyDrawColor = new Color(0.6f,0.6f,0.6f);
	private final Color appleGray = new Color(230,230,230);

	final Font QWOPLittle = new Font("Ariel", Font.BOLD,21);
	final Font QWOPBig = new Font("Ariel", Font.BOLD,28);


	/** Continuously update the estimate of the display loop time in milliseconds. **/
	private long avgLoopTime = MSPF;
	/** Filter the average loop time. Lower numbers gives more weight to the lower estimate, higher numbers gives more weight to the old value. **/
	private final float loopTimeFilter = 100f;
	private long lastIterTime = System.currentTimeMillis();
	/** Have we turned the physics off due to slowness? **/
	private boolean physOn = false;
	/** Keep track of whether we sent a pause tree command back to negotiator. **/
	private boolean treePause = false;

	/********** Writing actions on the left pane. **********/
	/** Fonts used for drawing on the left side pane. **/
	//private final Font giantFont = new Font("Ariel",Font.BOLD,36);
	private final Font bigFont = new Font("Ariel", Font.BOLD, 16);
	private final Font littleFont = new Font("Ariel", Font.BOLD, 12);

	/** Spacing for sequence number drawing on the left side panel. **/
	private final int vertTextSpacing = 18;
	private final int vertTextAnchor = 15;

	/********** Plots **********/
	public boolean plotColorsByDepth = true;

	/** State machine states for all UI **/
	public enum Status{
		IDLE_ALL, INITIALIZE, DRAW_ALL, VIEW_RUN
	}

	private Status currentStatus = Status.IDLE_ALL;
	private Status previousStatus = Status.IDLE_ALL;

	public FSM_UI(){
		Container pane = this.getContentPane();
		pane.setLayout(new GridBagLayout());

		/**** Tabbed panes ****/
		GridBagConstraints dataConstraints = new GridBagConstraints();
		dataConstraints.fill = GridBagConstraints.HORIZONTAL;
		dataConstraints.gridx = 0;
		dataConstraints.gridy = 1;
		dataConstraints.weightx = 0.3;
		dataConstraints.weighty = 0.125; // Turn this up if the tree stuff starts to cover the tabs
		dataConstraints.ipady = (int)(0.28*windowHeight);
		dataConstraints.ipadx = (int)(windowWidth*0.5);

		/* Pane for all tabs */
		tabPane = new JTabbedPane();
		tabPane.setBorder(BorderFactory.createRaisedBevelBorder());

		/* Runner pane */   
		runnerPane = new RunnerPane();
		tabPane.addTab("Run Animation", runnerPane);

		/* Snapshot pane */
		snapshotPane = new SnapshotPane();
		tabPane.addTab("State Viewer", snapshotPane);

		/* Data pane */
		dataPane_state = new DataPane_State();
		tabPane.addTab("State Data Viewer", dataPane_state);
		dataPane_pca = new DataPane_PCA();
		tabPane.addTab("PCA Viewer", dataPane_pca);

		/* State comparison pane */
		comparisonPane = new ComparisonPane();
		tabPane.addTab("State Compare", comparisonPane);

		/* Tree pane */
		treePane = new TreePane();

		pane.add(tabPane, dataConstraints);

		allTabbedPanes.add(runnerPane);
		allTabbedPanes.add(snapshotPane);
		allTabbedPanes.add(dataPane_state);
		allTabbedPanes.add(dataPane_pca);
		allTabbedPanes.add(comparisonPane);
		tabPane.addChangeListener(this);
		//Make sure the currently active tab is actually being updated.
		allTabbedPanes.get(tabPane.getSelectedIndex()).activateTab();

		/**** TREE PANE ****/
		GridBagConstraints treeConstraints = new GridBagConstraints();
		treeConstraints.fill = GridBagConstraints.HORIZONTAL;
		treeConstraints.gridx = 0;
		treeConstraints.gridy = 0;
		treeConstraints.weightx = 0.8;
		treeConstraints.weighty = 0.6;
		treeConstraints.ipady = (int)(windowHeight*0.8f);
		treeConstraints.ipadx = (int)(windowWidth*0.8f);

		treePane = new TreePane();

		treePane.setBorder(BorderFactory.createRaisedBevelBorder());
		pane.add(treePane,treeConstraints);
		/*******************/

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(windowWidth, windowHeight));
		this.setContentPane(this.getContentPane());
		this.pack();
		this.setVisible(true); 
		//treePane.requestFocus();
		repaint();
	}

	/** Main graphics loop. **/
	public void run(){
		while (running){
			long currentTime = System.currentTimeMillis();
			switch(currentStatus){
			case IDLE_ALL:
				currentStatus = Status.INITIALIZE;
				break;
			case INITIALIZE:
				currentStatus = Status.DRAW_ALL;
				break;
			case DRAW_ALL:
				if (physOn){
					Iterator<TrialNodeMinimal> iter = rootNodes.iterator();
					while (iter.hasNext()){
						iter.next().stepTreePhys(1);
					}
				}
				repaint();
				break;
			case VIEW_RUN:
				break;
			default:
				break;
			}

			if (currentStatus != previousStatus){
				negotiator.statusChange_UI(currentStatus);
			}

			previousStatus = currentStatus;

			long extraTime = MSPF - (System.currentTimeMillis() - currentTime);
			if (extraTime > 5){
				try {
					Thread.sleep(extraTime);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}

			// Tree frames per sec
			avgLoopTime = (long)(((loopTimeFilter - 1f) * avgLoopTime + 1f * (System.currentTimeMillis() - lastIterTime)) / loopTimeFilter); // Filter the loop time
			lastIterTime = System.currentTimeMillis();		
		}
	}

	/** Pick a node for the UI to highlight and potentially display. **/
	public void selectNode(TrialNodeMinimal selected){
		boolean success = false; // We don't allow new node selection while a realtime game is being played. 
		if (negotiator != null) success = negotiator.uiNodeSelect(selected);
		if (success){
			if (selectedNode != null){ // Clear things from the old selected node.
				selectedNode.displayPoint = false;
				selectedNode.clearBranchColor();
				selectedNode.clearBranchZOffset();
			}
			selectedNode = selected;
			selectedNode.displayPoint = true;
			selectedNode.nodeColor = Color.RED;
			selectedNode.setBranchZOffset(0.4f);

			if (snapshotPane.active) snapshotPane.giveSelectedNode(selectedNode);
			if (comparisonPane.active) comparisonPane.giveSelectedNode(selectedNode);
			if (dataPane_state.active) dataPane_state.update(); // Updates data being put on plots
			if (dataPane_pca.active) dataPane_pca.update(); // Updates data being put on plots
		}
	}

	@Override
	public void stateChanged(ChangeEvent e) {
		for (TabbedPaneActivator p: allTabbedPanes){
			p.deactivateTab();
		}
		allTabbedPanes.get(tabPane.getSelectedIndex()).activateTab();
	}

	public void setNegotiator(Negotiator negotiator){
		this.negotiator = negotiator;
	}

	/** Draw the actions on the left side pane. **/
	private void drawActionString(int[] sequence, Graphics g){
		drawActionString(sequence, g, -1);
	}

	private void drawActionString(int[] sequence, Graphics g, int highlightIdx){
		g.setFont(bigFont);
		g.setColor(Color.BLACK);
		g.drawString("Selected sequence: ", 10, vertTextAnchor);
		g.setColor(Color.DARK_GRAY);

		int currIdx = 0;
		int lineNum = 1;
		while (currIdx < sequence.length - 1){
			String line = sequence[currIdx] + ",";

			if (currIdx == highlightIdx){
				g.setColor(Color.GREEN);
			}else{
				g.setColor(Color.DARK_GRAY);
			}
			g.drawString(line, 10 + (currIdx % 4)*50, vertTextAnchor + vertTextSpacing * (lineNum + 2));
			currIdx++;
			lineNum = currIdx/4 + 1;
		}

		// Draw the little keys above the column.
		Graphics2D g2 = (Graphics2D)g;
		g2.setColor(Color.DARK_GRAY);
		g2.drawRoundRect(8, vertTextAnchor + 15, 30, 20, 5, 5);
		g2.drawRoundRect(8 + 49, vertTextAnchor + 15, 30, 20, 5, 5);
		g2.drawRoundRect(8 + 2*49, vertTextAnchor + 15, 30, 20, 5, 5);
		g2.drawRoundRect(8 + 3*49, vertTextAnchor + 15, 30, 20, 5, 5);

		g2.setColor(Color.LIGHT_GRAY);
		g2.fillRoundRect(8, vertTextAnchor + 15, 30, 20, 6, 6);
		g2.fillRoundRect(8 + 49, vertTextAnchor + 15, 30, 20, 6, 6);
		g2.fillRoundRect(8 + 2*49, vertTextAnchor + 15, 30, 20, 6, 6);
		g2.fillRoundRect(8 + 3*49, vertTextAnchor + 15, 30, 20, 6, 6);

		g.setFont(littleFont);
		g.setColor(Color.BLACK);
		g.drawString("- -", 12, vertTextAnchor + 30);
		g.drawString("W O", 12 + 49, vertTextAnchor + 30);
		g.drawString("- -", 12 + 2*49, vertTextAnchor + 30);
		g.drawString("Q P", 12 + 3*49, vertTextAnchor + 30);
	}

	/**
	 * Pane for displaying the entire tree in OpenGL. Not part of the tabbed system.
	 * @author Matt
	 */
	public class TreePane extends GenericGLPanel implements TabbedPaneActivator, GLEventListener, MouseListener, MouseMotionListener, MouseWheelListener, KeyListener {

		/** For rendering text overlays. Note that textrenderer is for overlays while GLUT is for labels in world space **/
		TextRenderer textRenderBig;
		TextRenderer textRenderSmall;

		/** Currently tracked mouse x location in screen coordinates of the panel **/
		int mouseX;

		/** Currently tracked mouse x location in screen coordinates of the panel **/
		int mouseY;

		/** Is the mouse cursor inside the bounds of the tree panel? **/
		boolean mouseInside = false;

		public TreePane(){
			super();
			canvas.setFocusable(true);
			canvas.addKeyListener(this);
			canvas.addMouseListener(this);
			canvas.addMouseMotionListener(this);
			canvas.addMouseWheelListener(this);

			textRenderBig = new TextRenderer(new Font("Calibri", Font.BOLD, 36));
			textRenderSmall = new TextRenderer(new Font("Calibri", Font.PLAIN, 18));
		}

		@Override
		public void activateTab() {}
		@Override
		public void deactivateTab() {}

		@Override
		public void display(GLAutoDrawable drawable) {		
			if (negotiator == null) return;

			super.display(drawable);

			float ptSize = 50f/cam.getZoomFactor(); //Let the points be smaller/bigger depending on zoom, but make sure to cap out the size!
			ptSize = Math.min(ptSize, 10f);

			for (TrialNodeMinimal node : rootNodes){
				gl.glColor3f(1f, 0.1f, 0.1f);
				gl.glPointSize(ptSize);

				gl.glBegin(GL2.GL_POINTS);
				node.drawNodes_below(gl);
				gl.glEnd();

				gl.glColor3f(1f, 1f, 1f);
				gl.glBegin(GL2.GL_LINES);
				node.drawLines_below(gl);
				gl.glEnd();
			}

			// Draw games played and games/sec in upper left.
			textRenderBig.beginRendering(panelWidth, panelHeight);
			textRenderBig.setColor(0.7f, 0.7f, 0.7f, 1.0f);
			textRenderBig.draw(negotiator.getGamesPlayed() + " games", 20, panelHeight - 50);

			if (treePause){
				textRenderBig.setColor(0.7f, 0.1f, 0.1f, 1.0f);	
				textRenderBig.draw("PAUSED", panelWidth/2, panelHeight - 50);
			}
			textRenderBig.endRendering();

			// Draw the FPS of the tree drawer at the moment.
			textRenderSmall.beginRendering(panelWidth, panelHeight);
			textRenderSmall.setColor(0.7f, 0.7f, 0.7f, 1.0f);
			int fps = (int)(10000./avgLoopTime);
			textRenderSmall.draw( ( (Math.abs(fps) > 10000) ? "???" : fps/10f ) + " FPS", panelWidth - 75, panelHeight - 20);
			// Physics on/off alert
			if (physOn){
				textRenderSmall.setColor(0.1f, 0.7f, 0.1f, 1.0f);
				textRenderSmall.draw("Tree physics on", panelWidth - 120, panelHeight - 35);
			}else{
				textRenderSmall.setColor(0.7f, 0.1f, 0.1f, 1.0f);
				textRenderSmall.draw("Tree physics off", panelWidth - 120, panelHeight - 35);
			}
			// Number of imported games
			textRenderSmall.setColor(0.7f, 0.7f, 0.7f, 1.0f);
			textRenderSmall.draw(negotiator.getGamesImported() + " Games imported", 20, panelHeight - 70);
			// Total games played
			textRenderSmall.setColor(0.7f, 0.7f, 0.7f, 1.0f);
			textRenderSmall.draw(negotiator.getGamesTotal() + " Total games", 20, panelHeight - 85);

			textRenderSmall.setColor(0.1f, 0.7f, 0.1f, 1.0f);
			textRenderSmall.draw(Math.round(negotiator.getTimeSimulated()/360)/10f + " hours simulated!", 20, panelHeight - 100);
			textRenderSmall.draw((int)negotiator.getGamesPerSecond() + " games/s", 20, panelHeight - 115);

			textRenderSmall.endRendering();

		}

		/** Draw a text string using GLUT (for openGL rendering version of my stuff) **/
		public void drawString(String toDraw, float x, float y, float z, GL2 gl, GLUT glut ) 
		{
			// Fomat numbers with Java.
			NumberFormat format = NumberFormat.getNumberInstance();
			format.setMinimumFractionDigits(2);
			format.setMaximumFractionDigits(2);

			// Printing fonts, letters and numbers is much simpler with GLUT.
			// We do not have to use our own bitmap for the font.
			gl.glRasterPos3d(x,y,z);
			glut.glutBitmapString(GLUT.BITMAP_HELVETICA_12, toDraw);
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {
			if (e.getWheelRotation() < 0){ //Negative mouse direction -> zoom in.
				cam.smoothZoom(0.9f, 5);
			}else{
				cam.smoothZoom(1.1f, 5);
			}
		}

		@Override
		public void mouseDragged(MouseEvent e) {
			Vector3f relCamMove = cam.windowFrameToWorldFrameDiff(e.getX(), e.getY(), mouseX, mouseY);
			cam.smoothTranslateRelative(relCamMove, relCamMove, 5);
			mouseX = e.getX();
			mouseY = e.getY();

		}

		@Override
		public void mouseClicked(MouseEvent e) {
			if (e.isMetaDown()){
				selectNode(cam.nodeFromClick(e.getX(), e.getY(), rootNodes));
			}
		}

		@Override
		public void mousePressed(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
		}

		@Override
		public void keyTyped(KeyEvent e) {}
		@Override
		public void keyPressed(KeyEvent e) {

			if (mouseInside){
				//Navigating the focused node tree
				int keyCode = e.getKeyCode();

				if(e.isMetaDown()){ //if we're using GL, then we'll move the camera with mac key + arrows
					switch( keyCode ) { 
					case KeyEvent.VK_UP: //Go out the branches of the tree

						cam.smoothRotateLong(0.1f, 5);
						break;
					case KeyEvent.VK_DOWN: //Go back towards root one level
						cam.smoothRotateLong(-0.1f, 5);
						break;
					case KeyEvent.VK_LEFT: //Go left along an isobranch (like that word?)
						cam.smoothRotateLat(0.1f, 5);
						break;
					case KeyEvent.VK_RIGHT : //Go right along an isobranch
						cam.smoothRotateLat(-0.1f, 5);
						break;
					case KeyEvent.VK_S : // toggle the score text at the end of all branches
						//TODO
						break;
					}
				}else if(e.isShiftDown()){
					switch( keyCode ) { 
					case KeyEvent.VK_LEFT: //Go left along an isobranch (like that word?)
						cam.smoothTwist(0.1f, 5);
						break;
					case KeyEvent.VK_RIGHT : //Go right along an isobranch
						cam.smoothTwist(-0.1f, 5);
						break;
					}
				}else{
					switch( keyCode ) { 
					case KeyEvent.VK_UP: //Go out the branches of the tree
						arrowSwitchNode(0,1);
						break;
					case KeyEvent.VK_DOWN: //Go back towards root one level
						arrowSwitchNode(0,-1); 
						break;
					case KeyEvent.VK_LEFT: //Go left along an isobranch (like that word?)
						arrowSwitchNode(1,0);
						break;
					case KeyEvent.VK_RIGHT : //Go right along an isobranch
						arrowSwitchNode(-1,0);
						break;
					case KeyEvent.VK_P : //Pause everything except for graphics updates
						treePause = !treePause;
						if (treePause){
							negotiator.pauseTree();
						}else{
							negotiator.unpauseTree();
						}
						break;
					case KeyEvent.VK_C:
						negotiator.redistributeNodes();
						break;
					case KeyEvent.VK_V:
						physOn = !physOn;
						break;
					case KeyEvent.VK_ESCAPE:
						System.exit(0);
						break;
					}
				}
			}
		}
		@Override
		public void keyReleased(KeyEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();

			// If the snapshot pane is displaying stuff, this lets us potentially select some of the future nodes displayed in the snapshot pane.
			if (snapshotPane.active && mouseInside){
				ArrayList<TrialNodeMinimal> snapshotLeaves = snapshotPane.getDisplayedLeaves();
				if (snapshotLeaves.size() > 0){
					TrialNodeMinimal nearest = cam.nodeFromClick_set(mouseX, mouseY, snapshotLeaves, 50);
					if (nearest != null){
						snapshotPane.giveSelectedFuture(nearest);
					}else{
						snapshotPane.giveSelectedFuture(null); // clear it out if the mouse is too far away from selectable nodes.
					}
				}
			}
		}
		@Override
		public void mouseReleased(MouseEvent e) {}
		@Override
		public void mouseEntered(MouseEvent e) {
			mouseInside = true;
		}
		@Override
		public void mouseExited(MouseEvent e) {
			mouseInside = false;
		}

		// The following 2 methods are probably too complicated. when you push the arrow at the edge of one branch, this tries to jump to the nearest next branch node at the same depth.
		/** Called by key listener to change our focused node to the next adjacent one in the +1 or -1 direction **/
		private void arrowSwitchNode(int direction,int depth){
			//Stupid way of getting this one's index according to its parent.
			if(selectedNode != null){
				if (selectedNode.treeDepth == 0){ // At root, don't try to look at parent.
					// <nothing>
				}else{
					int thisIndex = selectedNode.parent.children.indexOf(selectedNode);
					//This set of logicals eliminates the edge cases, then takes the proposed action as default
					if (thisIndex == 0 && direction == -1){ //We're at the lowest index of this node and must head to a new parent node.
						ArrayList<TrialNodeMinimal> blacklist = new ArrayList<TrialNodeMinimal>(); //Keep a blacklist of nodes that already proved to be duds.
						blacklist.add(selectedNode);
						nextOver(selectedNode.parent,blacklist,1,direction,selectedNode.parent.children.indexOf(selectedNode),0);

					}else if (thisIndex == selectedNode.parent.children.size()-1 && direction == 1){ //We're at the highest index of this node and must head to a new parent node.
						ArrayList<TrialNodeMinimal> blacklist = new ArrayList<TrialNodeMinimal>();
						blacklist.add(selectedNode);
						nextOver(selectedNode.parent,blacklist, 1,direction,selectedNode.parent.children.indexOf(selectedNode),0);

					}else{ //Otherwise we can just switch nodes within the scope of this parent.
						selectNode(selectedNode.parent.children.get(thisIndex+direction));
					}
				}

				//These logicals just take the proposed motion (or not) and ignore any edges.
				if(depth == 1 && selectedNode.children.size()>0){ //Go further down the tree if this node has children
					selectNode(selectedNode.children.get(0));
				}else if(depth == -1 && selectedNode.treeDepth>0){ //Go up the tree if this is not root.
					selectNode(selectedNode.parent);
				}
				repaint();
			}
		}

		/** Take a node back a layer. Don't return to node past. Try to go back out by the deficit depth amount in the +1 or -1 direction left/right **/
		private boolean nextOver(TrialNodeMinimal current, ArrayList<TrialNodeMinimal> blacklist, int deficitDepth, int direction,int prevIndexAbove,int numTimesTried){ // numTimesTried added to prevent some really deep node for causing some really huge search through the whole tree. If we don't succeed in a handful of iterations, just fail quietly.
			numTimesTried++;
			boolean success = false;
			//TERMINATING CONDITIONS-- fail quietly if we get back to root with nothing. Succeed if we get back to the same depth we started at.
			if (deficitDepth == 0){ //We've successfully gotten back to the same level. Great.
				selectNode(current);
				return true;
			}else if(current.treeDepth == 0){
				return true; // We made it back to the tree's root without any success. Just return.

			}else if(numTimesTried>100){// If it takes >100 movements between nodes, we'll just give up.
				return true;
			}else{
				//CCONDITIONS WE NEED TO STEP BACKWARDS TOWARDS ROOT.
				//If this new node has no children OR it's 1 child is on the blacklist, move back up the tree.
				if((prevIndexAbove+1 == current.children.size() && direction == 1) || (prevIndexAbove == 0 && direction == -1)){
					blacklist.add(current); 
					success = nextOver(current.parent,blacklist,deficitDepth+1,direction,current.parent.children.indexOf(current),numTimesTried); //Recurse back another node.
				}else if (!(current.children.size() >0) || (blacklist.contains(current.children.get(0)) && current.children.size() == 1)){ 
					blacklist.add(current); 
					success = nextOver(current.parent,blacklist,deficitDepth+1,direction,current.parent.children.indexOf(current),numTimesTried); //Recurse back another node.
				}else{

					//CONDITIONS WE NEED TO GO DEEPER:
					if(direction == 1){ //March right along this previous node.
						for (int i = prevIndexAbove+1; i<current.children.size(); i++){
							success = nextOver(current.children.get(i),blacklist,deficitDepth-1,direction,-1,numTimesTried);
							if(success){
								return true;
							}
						}
					}else if(direction == -1){ //March left along this previous node
						for (int i = prevIndexAbove-1; i>=0; i--){
							success = nextOver(current.children.get(i),blacklist,deficitDepth-1,direction,current.children.get(i).children.size(),numTimesTried);
							if(success){
								return true;
							}
						}
					}
				}
			}
			success = true;
			return success;
		}

	}

	/**
	 * Pane for displaying the animated runner executing a sequence selected on the tree. A tab.
	 * @author Matt
	 */
	public class RunnerPane extends JPanel implements TabbedPaneActivator {

		public int headPos;

		boolean active = false;

		private World world;

		public RunnerPane(){}

		public void paintComponent(Graphics g){
			if (!active) return;
			super.paintComponent(g);

			if (world != null){
				Body newBody = world.getBodyList();
				while (newBody != null){

					Shape newfixture = newBody.getShapeList();

					while(newfixture != null){

						if(newfixture.getType() == ShapeType.POLYGON_SHAPE){

							PolygonShape newShape = (PolygonShape)newfixture;
							Vec2[] shapeVerts = newShape.m_vertices;
							for (int k = 0; k<newShape.m_vertexCount; k++){

								XForm xf = newBody.getXForm();
								Vec2 ptA = XForm.mul(xf,shapeVerts[k]);
								Vec2 ptB = XForm.mul(xf, shapeVerts[(k+1) % (newShape.m_vertexCount)]);
								g.drawLine((int)(runnerScaling * ptA.x) + xOffsetPixels,
										(int)(runnerScaling * ptA.y) + yOffsetPixels,
										(int)(runnerScaling * ptB.x) + xOffsetPixels,
										(int)(runnerScaling * ptB.y) + yOffsetPixels);			    		
							}
						}else if (newfixture.getType() == ShapeType.CIRCLE_SHAPE){
							CircleShape newShape = (CircleShape)newfixture;
							float radius = newShape.m_radius;
							headPos = (int)(runnerScaling * newBody.getPosition().x);
							g.drawOval((int)(runnerScaling * (newBody.getPosition().x - radius) + xOffsetPixels),
									(int)(runnerScaling * (newBody.getPosition().y - radius) + yOffsetPixels),
									(int)(runnerScaling * radius * 2),
									(int)(runnerScaling * radius * 2));		

						}else if(newfixture.getType() == ShapeType.EDGE_SHAPE){

							EdgeShape newShape = (EdgeShape)newfixture;
							XForm trans = newBody.getXForm();

							Vec2 ptA = XForm.mul(trans, newShape.getVertex1());
							Vec2 ptB = XForm.mul(trans, newShape.getVertex2());
							Vec2 ptC = XForm.mul(trans, newShape.getVertex2());

							g.drawLine((int)(runnerScaling * ptA.x) + xOffsetPixels,
									(int)(runnerScaling * ptA.y) + yOffsetPixels,
									(int)(runnerScaling * ptB.x) + xOffsetPixels,
									(int)(runnerScaling * ptB.y) + yOffsetPixels);			    		
							g.drawLine((int)(runnerScaling * ptA.x) + xOffsetPixels,
									(int)(runnerScaling * ptA.y) + yOffsetPixels,
									(int)(runnerScaling * ptC.x) + xOffsetPixels,
									(int)(runnerScaling * ptC.y) + yOffsetPixels);			    		

						}else{
							System.out.println("Not found: " + newfixture.m_type.name());
						}
						newfixture = newfixture.getNext();
					}
					newBody = newBody.getNext();
				}
				//This draws the "road" markings to show that the ground is moving relative to the dude.
				for(int i = 0; i<this.getWidth()/69; i++){
					g.drawString("_", ((xOffsetPixels - xOffsetPixels_init-i * 70) % getWidth()) + getWidth(), yOffsetPixels + 92);
					keyDrawer(g, negotiator.Q,negotiator.W,negotiator.O,negotiator.P);
				}

				drawActionString(negotiator.getCurrentSequence(), g, negotiator.getCurrentActionIdx());

			}else{
				keyDrawer(g, false, false, false, false);
			}

			//    	g.drawString(dc.format(-(headpos+30)/40.) + " metres", 500, 110);
			xOffsetPixels = -headPos + xOffsetPixels_init;

		}

		public void setWorldToView(World world){
			this.world = world;
		}

		public void clearWorldToView(){
			world = null;
		}

		@Override
		public void activateTab() {
			active = true;
		}
		@Override
		public void deactivateTab() {
			active = false;
			negotiator.killRealtimeRun();
		}
		public void keyDrawer(Graphics g, boolean q, boolean w, boolean o, boolean p){

			int qOffset = (q ? 10:0);
			int wOffset = (w ? 10:0);
			int oOffset = (o ? 10:0);
			int pOffset = (p ? 10:0);

			int offsetBetweenPairs = getWidth()/4;
			int startX = -45;
			int startY = yOffsetPixels - 200;
			int size = 40;

			Font activeFont;
			FontMetrics fm;
			Graphics2D g2 = (Graphics2D)g;

			g2.setColor(Color.DARK_GRAY);
			g2.drawRoundRect(startX + 80 - qOffset/2, startY - qOffset/2, size + qOffset, size + qOffset, (size + qOffset)/10, (size + qOffset)/10);
			g2.drawRoundRect(startX + 160 - wOffset/2, startY - wOffset/2, size + wOffset, size + wOffset, (size + wOffset)/10, (size + wOffset)/10);
			g2.drawRoundRect(startX + 240 - oOffset/2 + offsetBetweenPairs, startY - oOffset/2, size + oOffset, size + oOffset, (size + oOffset)/10, (size + oOffset)/10);
			g2.drawRoundRect(startX + 320 - pOffset/2 + offsetBetweenPairs, startY - pOffset/2, size + pOffset, size + pOffset, (size + pOffset)/10, (size + pOffset)/10);

			g2.setColor(Color.LIGHT_GRAY);
			g2.fillRoundRect(startX + 80 - qOffset/2, startY - qOffset/2, size + qOffset, size + qOffset, (size + qOffset)/10, (size + qOffset)/10);
			g2.fillRoundRect(startX + 160 - wOffset/2, startY - wOffset/2, size + wOffset, size + wOffset, (size + wOffset)/10, (size + wOffset)/10);
			g2.fillRoundRect(startX + 240 - oOffset/2 + offsetBetweenPairs, startY - oOffset/2, size + oOffset, size + oOffset, (size + oOffset)/10, (size + oOffset)/10);
			g2.fillRoundRect(startX + 320 - pOffset/2 + offsetBetweenPairs, startY - pOffset/2, size + pOffset, size + pOffset, (size + pOffset)/10, (size + pOffset)/10);

			g2.setColor(Color.BLACK);

			//Used for making sure text stays centered.

			activeFont = q ? QWOPBig:QWOPLittle;
			g2.setFont(activeFont);
			fm = g2.getFontMetrics();
			g2.drawString("Q", startX + 80 + size/2-fm.stringWidth("Q")/2, startY + size/2+fm.getHeight()/3);


			activeFont = w ? QWOPBig:QWOPLittle;
			g2.setFont(activeFont);
			fm = g2.getFontMetrics();
			g2.drawString("W", startX + 160 + size/2-fm.stringWidth("W")/2, startY + size/2+fm.getHeight()/3);

			activeFont = o ? QWOPBig:QWOPLittle;
			g2.setFont(activeFont);
			fm = g2.getFontMetrics();
			g2.drawString("O", startX + 240 + size/2-fm.stringWidth("O")/2 + offsetBetweenPairs, startY + size/2+fm.getHeight()/3);

			activeFont = p ? QWOPBig:QWOPLittle;
			g2.setFont(activeFont);
			fm = g2.getFontMetrics();
			g2.drawString("P", startX + 320 + size/2-fm.stringWidth("P")/2 + offsetBetweenPairs, startY + size/2+fm.getHeight()/3);

		}
	}

	/**
	 * Displays fixed shots of the runner at selected nodes. Can also preview the past and future from these nodes. A tab.
	 * @author Matt
	 */
	public class SnapshotPane extends JPanel implements TabbedPaneActivator, MouseListener, MouseMotionListener, MouseWheelListener {

		/** Number of runner states in the past to display. **/
		public int numHistoryStatesDisplay = 10;

		/** Is this tab currently active? If not, don't run the draw loop. **/
		public boolean active = false;

		/** Highlight stroke for line drawing. **/
		Stroke normalStroke = new BasicStroke(0.5f);

		/** Highlight stroke for line drawing. **/
		Stroke boldStroke = new BasicStroke(2);

		/** Node we are focusing on displaying. **/
		private TrialNodeMinimal snapshotNode;

		TrialNodeMinimal highlightedRunNode;

		TrialNodeMinimal queuedFutureLeaf;

		Font floatingActionText = new Font("Ariel", Font.BOLD, 18);

		private int mouseX = 0;
		private int mouseY = 0;
		private boolean mouseIsIn = false;

		/** How close do we have to be (squared) from the chest of a single figure for it to be eligible for selection. **/
		float figureSelectThreshSq = 150;

		public SnapshotPane(){
			addMouseListener(this);
			addMouseMotionListener(this);
		}

		private ArrayList<TrialNodeMinimal> focusLeaves = new ArrayList<TrialNodeMinimal>();
		private ArrayList<XForm[]> transforms = new ArrayList<XForm[]>();
		private ArrayList<Stroke> strokes = new ArrayList<Stroke>();
		private ArrayList<Color> colors = new ArrayList<Color>();

		Shape[] shapes;

		/** Assign a selected node for the snapshot pane to display. **/
		public void giveSelectedNode(TrialNodeMinimal node){
			transforms.clear();
			focusLeaves.clear();
			strokes.clear();
			colors.clear();

			shapes = QWOPGame.shapeList;

			/***** Focused node first *****/
			snapshotNode = node;
			XForm[] nodeTransform = snapshotNode.state.getTransforms();
			// Make the sequence centered around the selected node state.
			xOffsetPixels = xOffsetPixels_init + (int)(-runnerScaling * nodeTransform[1].position.x);
			transforms.add(nodeTransform);
			strokes.add(boldStroke);
			colors.add(Color.BLACK);
			focusLeaves.add(snapshotNode);

			/***** History nodes *****/
			TrialNodeMinimal historyNode = snapshotNode;
			for (int i = 0; i < numHistoryStatesDisplay; i++){
				if (historyNode.treeDepth > 0){
					historyNode = historyNode.parent;
					nodeTransform = historyNode.state.getTransforms();
					transforms.add(nodeTransform);
					strokes.add(normalStroke);
					colors.add(historyDrawColor);
					focusLeaves.add(historyNode);
				}
			}

			/***** Future leaf nodes *****/
			ArrayList<TrialNodeMinimal> descendants = new ArrayList<TrialNodeMinimal>();
			for (int i = 0; i < selectedNode.children.size(); i++){
				TrialNodeMinimal child = selectedNode.children.get(i);
				child.getLeaves(descendants);

				Color runnerColor = TrialNodeMinimal.getColorFromTreeDepth(i*10);
				child.setBranchColor(runnerColor); // Change the color on the tree too.

				for (TrialNodeMinimal descendant : descendants){
					if (descendant.state != null){
						focusLeaves.add(descendant);
						transforms.add(descendant.state.getTransforms());
						strokes.add(normalStroke);
						colors.add(runnerColor);
					}
				}
			}
		}

		private float getDistFromMouseSq(float x, float y){
			float xdist = (mouseX - (runnerScaling * x + xOffsetPixels));
			float ydist = (mouseY - (runnerScaling * y + yOffsetPixels));
			return xdist*xdist + ydist*ydist;
		}

		/** Draws the selected node state and potentially previous and future states. **/
		public void paintComponent(Graphics g){
			if (!active) return;
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;

			if (snapshotNode != null && snapshotNode.state != null){ // TODO this keeps the root node from throwing errors because I didn't assign it a state. We really should do that.

				float bestSoFar = Float.MAX_VALUE;
				int bestIdx = Integer.MIN_VALUE;

				// Figure out if the mouse close enough to highlight one state.
				if (mouseIsIn && mouseX > getWidth()/2){ // If we are mousing over this panel, see if we're hovering close enough over any particular dude state.

					// Check body first
					for (int i = 0; i < focusLeaves.size(); i++){
						float distSq = getDistFromMouseSq(focusLeaves.get(i).state.body.x,focusLeaves.get(i).state.body.y);
						if (distSq < bestSoFar  && distSq < figureSelectThreshSq){
							bestSoFar = distSq;
							bestIdx = i;
						}
					}
					// Then head
					if (bestIdx < 0){
						for (int i = 0; i < focusLeaves.size(); i++){
							float distSq = getDistFromMouseSq(focusLeaves.get(i).state.head.x,focusLeaves.get(i).state.head.y);
							if (distSq < bestSoFar  && distSq < figureSelectThreshSq){
								bestSoFar = distSq;
								bestIdx = i;
							}
						}
					}
					// Then both feet equally
					if (bestIdx < 0){
						for (int i = 0; i < focusLeaves.size(); i++){
							float distSq = getDistFromMouseSq(focusLeaves.get(i).state.lfoot.x,focusLeaves.get(i).state.lfoot.y);
							if (distSq < bestSoFar  && distSq < figureSelectThreshSq){
								bestSoFar = distSq;
								bestIdx = i;
							}
							distSq = getDistFromMouseSq(focusLeaves.get(i).state.rfoot.x,focusLeaves.get(i).state.rfoot.y);
							if (distSq < bestSoFar  && distSq < figureSelectThreshSq){
								bestSoFar = distSq;
								bestIdx = i;
							}

						}
					}
				}

				// Draw all non-highlighted runners.
				for (int i = transforms.size() - 1; i >= 0; i--){
					if (!mouseIsIn || bestIdx != i){
						if (highlightedRunNode != null && focusLeaves.get(i).treeDepth > selectedNode.treeDepth){ // Make the nodes after the selected one lighter if one is highlighted.
							drawRunner(g2, colors.get(i).brighter(), strokes.get(i), shapes, transforms.get(i));
						}else{
							drawRunner(g2, colors.get(i), strokes.get(i), shapes, transforms.get(i));
						}

					}
				}

				// Change things if one runner is selected.
				if (mouseIsIn && bestIdx >= 0){
					TrialNodeMinimal newHighlightNode = focusLeaves.get(bestIdx);
					changeFocusedFuture(g2, highlightedRunNode, newHighlightNode);
					highlightedRunNode = newHighlightNode;

					// Externally commanded pick, instead of mouse-picked.
				}else if(queuedFutureLeaf != null){
					changeFocusedFuture(g2, highlightedRunNode, queuedFutureLeaf);
					highlightedRunNode = queuedFutureLeaf;

				}else if (highlightedRunNode != null){ // When we stop mousing over, clear the brightness changes.
					highlightedRunNode.displayPoint = false;
					highlightedRunNode.nodeColor = Color.GREEN;
					rootNodes.get(0).resetLineBrightness_below();
					highlightedRunNode.clearBackwardsBranchZOffset();
					highlightedRunNode = null;
				}

				// Draw the sequence too.
				drawActionString(selectedNode.getSequence(), g);
			}
		}

		/** Change highlighting on both the tree and the snapshot when selections change. **/
		private void changeFocusedFuture(Graphics2D g2, TrialNodeMinimal oldFuture, TrialNodeMinimal newFuture){
			// Clear out highlights from the old node.
			if (oldFuture != null && !oldFuture.equals(newFuture)){
				oldFuture.clearBackwardsBranchZOffset();
				oldFuture.displayPoint = false;
				oldFuture.nodeColor = Color.GREEN;
			}

			// Add highlights to the new node if it's different or previous is nonexistant
			if (oldFuture == null || !oldFuture.equals(newFuture)){
				newFuture.displayPoint = true;
				newFuture.nodeColor = Color.ORANGE;
				newFuture.setBackwardsBranchZOffset(0.8f);
				newFuture.highlightSingleRunToThisNode(); // Tell the tree to highlight a section and darken others.
			}
			// Draw
			int idx = focusLeaves.indexOf(newFuture);
			if (idx > -1){ // Focus leaves no longer contains the no focus requested.
				try{
					drawRunner(g2, colors.get(idx).darker(), boldStroke, shapes, transforms.get(idx));

					TrialNodeMinimal currentNode = newFuture;

					// Also draw parent nodes back the the selected one to view the run that leads to the highlighted failure.
					int prevX = Integer.MAX_VALUE;
					while (currentNode.treeDepth > selectedNode.treeDepth){
						// Make color shades slightly alternate between subsequent move frames.
						Color everyOtherEvenColor = colors.get(idx).darker();
						if (currentNode.treeDepth % 2 == 0){
							everyOtherEvenColor = everyOtherEvenColor.darker();
						}
						drawRunner(g2, everyOtherEvenColor, boldStroke, shapes, currentNode.state.getTransforms());

						// Draw actions above their heads.
						g2.setFont(QWOPBig);
						g2.setFont(floatingActionText);
						g2.setColor(everyOtherEvenColor);
						prevX = Math.min((int)(runnerScaling * currentNode.state.head.x) + xOffsetPixels - 3,prevX - 25);

						g2.drawString(String.valueOf(currentNode.controlAction), 
								prevX, 
								Math.min((int)(runnerScaling * currentNode.state.head.y) + yOffsetPixels - 25, 45));

						currentNode = currentNode.parent;
					}
				}catch(IndexOutOfBoundsException e){
					// I don't really care tbh. Just skip this one.
				}
			}
		}

		/** Focus a single future leaf **/
		public void giveSelectedFuture(TrialNodeMinimal queuedFutureLeaf){
			this.queuedFutureLeaf = queuedFutureLeaf;
		}

		/** Draw the runner at a certain state. **/
		private void drawRunner(Graphics2D g, Color drawColor, Stroke stroke, Shape[] shapes, XForm[] transforms){

			for (int i = 0; i < shapes.length; i++){
				g.setColor(drawColor);
				g.setStroke(stroke);
				switch(shapes[i].getType()){
				case CIRCLE_SHAPE:
					CircleShape circleShape = (CircleShape)shapes[i];
					float radius = circleShape.getRadius();
					Vec2 circleCenter = XForm.mul(transforms[i], circleShape.getLocalPosition());
					g.drawOval((int)(runnerScaling * (circleCenter.x - radius) + xOffsetPixels),
							(int)(runnerScaling * (circleCenter.y - radius) + yOffsetPixels),
							(int)(runnerScaling * radius * 2),
							(int)(runnerScaling * radius * 2));
					break;
				case POLYGON_SHAPE:
					//Get both the shape and its transform.
					PolygonShape polygonShape = (PolygonShape)shapes[i];
					XForm transform = transforms[i];

					// Ground is black regardless.
					if (shapes[i].m_filter.groupIndex == 1){
						g.setColor(Color.BLACK);
						g.setStroke(normalStroke);
					}
					for (int j = 0; j < polygonShape.getVertexCount(); j++){ // Loop through polygon vertices and draw lines between them.
						Vec2 ptA = XForm.mul(transform, polygonShape.m_vertices[j]);
						Vec2 ptB = XForm.mul(transform, polygonShape.m_vertices[(j + 1) % (polygonShape.getVertexCount())]); //Makes sure that the last vertex is connected to the first one.
						g.drawLine((int)(runnerScaling * ptA.x) + xOffsetPixels,
								(int)(runnerScaling * ptA.y) + yOffsetPixels,
								(int)(runnerScaling * ptB.x) + xOffsetPixels,
								(int)(runnerScaling * ptB.y) + yOffsetPixels);		
					}
					break;
				default:
					break;
				}
			}
		}

		/** Get the list of leave nodes (failure states) that we're displaying in the snapshot pane. **/
		public ArrayList<TrialNodeMinimal> getDisplayedLeaves(){
			return focusLeaves;
		}

		@Override
		public void activateTab() {
			active = true;
		}
		@Override
		public void deactivateTab() {
			active = false;
		}

		@Override
		public void mouseWheelMoved(MouseWheelEvent e) {}

		@Override
		public void mouseDragged(MouseEvent e) {}

		@Override
		public void mouseMoved(MouseEvent e) {
			mouseX = e.getX();
			mouseY = e.getY();
		}

		@Override
		public void mouseClicked(MouseEvent e) {}

		@Override
		public void mousePressed(MouseEvent e) {}

		@Override
		public void mouseReleased(MouseEvent e) {}

		@Override
		public void mouseEntered(MouseEvent e) {
			mouseIsIn = true;
			queuedFutureLeaf = null; // No longer using what the tree says is focused when the mouse is in this pane.
		}

		@Override
		public void mouseExited(MouseEvent e) {
			mouseIsIn = false;
		}
	}

	/**
	 * Pane for estimating and displaying similar states.
	 * @author Matt
	 */
	public class ComparisonPane extends JPanel implements TabbedPaneActivator, ActionListener {

		/** Highlight stroke for line drawing. **/
		private final Stroke normalStroke = new BasicStroke(0.5f);

		/** Highlight stroke for line drawing. **/
		private final Stroke boldStroke = new BasicStroke(2);

		/** Node that we compare all others to. **/
		private TrialNodeMinimal primaryNode;
		
		/** How many of the eigenvectors do we use? Dimension reduction. **/
		private int lastEigToDisp = 10; // Display eigenvalues 0-4
		private int[] eigsToDisp;
		
		/** Drawn shapes **/
		Shape[] shapes;
		
		private final JComboBox<Integer> eigSelector;

		/** Is this tab currently on top? **/
		private boolean active = false;

		private ArrayList<XForm[]> transforms = new ArrayList<XForm[]>();
		private ArrayList<Stroke> strokes = new ArrayList<Stroke>();
		private ArrayList<Color> colors = new ArrayList<Color>();

		public ComparisonPane(){
			eigSelector = new JComboBox<Integer>();
			for (int i = 0; i < 10; i++){
				eigSelector.addItem(i);
			}
			//add(eigSelector);
			eigSelector.addActionListener(this);
			pack();
			//MATT CHECKBOX DOESN"T WORK YET, COME BACK HERE
			eigsToDisp = new int[lastEigToDisp + 1];
			
			for (int i = 0; i < eigsToDisp.length; i++){
				eigsToDisp[i] = i;
			}
		}
		/** Assign a selected node for the snapshot pane to display. **/
		public void giveSelectedNode(TrialNodeMinimal node){
			rootNodes.get(0).clearNodeOverrideColor();
			transforms.clear();
			strokes.clear();
			colors.clear();

			shapes = QWOPGame.shapeList;

			/***** Focused node first *****/
			primaryNode = node;
			primaryNode.overrideNodeColor = Color.RED; // Restore its red color
			primaryNode.displayPoint = true;
			XForm[] nodeTransform = primaryNode.state.getTransforms();
			// Make the sequence centered around the selected node state.
			xOffsetPixels = xOffsetPixels_init + (int)(-runnerScaling * nodeTransform[1].position.x);
			transforms.add(nodeTransform);
			strokes.add(boldStroke);
			colors.add(Color.RED);


			//////////// DO COMPARISON TO OTHER NODES //////////////
			// This is a kind of hacky way of getting to the PCA stuff.
			DataPane_PCA pcaPane = (DataPane_PCA)(dataPane_pca);
			DataPane.PCATransformedData pcaData = pcaPane.getPCAData();
			ArrayList<TrialNodeMinimal> allNodes = new ArrayList<TrialNodeMinimal>();
			rootNodes.get(0).getNodes_below(allNodes);

			FloatMatrix transDat = pcaData.transformDataset(allNodes,eigsToDisp);
			FloatMatrix selectedRowDat = transDat.getRow(allNodes.indexOf(primaryNode));

			// Error
			transDat.subiRowVector(selectedRowDat);

			// Error squared
			transDat.muli(transDat);

			// Sum.
			FloatMatrix errSq = transDat.rowSums();


			TreeMap<Float,TrialNodeMinimal> treeMap = new TreeMap<Float,TrialNodeMinimal> ();

			for (int i = 0; i < allNodes.size(); i++){
				treeMap.put(errSq.get(i), allNodes.get(i));
			}

			Iterator<TrialNodeMinimal> iter = treeMap.values().iterator();
			iter.next(); // First one is a self-comparison.

			int count = 0;
			while (iter.hasNext() && count <= 5){
				TrialNodeMinimal closeMatch = iter.next();

				XForm[] nodeXForm = closeMatch.state.getTransforms();
				// Make the sequence centered around the selected node state.
				transforms.add(nodeXForm);
				strokes.add(normalStroke);
				Color matchColor = TrialNodeMinimal.getColorFromTreeDepth(count*5);
				colors.add(matchColor);
				closeMatch.overrideNodeColor = matchColor;
				closeMatch.displayPoint = true;

				count++;
			}

		}


		/** Draws the selected node state and potentially previous and future states. **/
		public void paintComponent(Graphics g){
			if (!active) return;
			super.paintComponent(g);
			Graphics2D g2 = (Graphics2D)g;
			DataPane_PCA pcaPane = (DataPane_PCA)(dataPane_pca);
			if (pcaPane.getPCAData() == null){
				g.setFont(bigFont);
				g.drawString("Use the PCA tab to calculate PCA from a selected node.", windowWidth/3, 100);
			}else if (primaryNode != null && primaryNode.state != null){ // TODO this keeps the root node from throwing errors because I didn't assign it a state. We really should do that.
				// Draw all non-highlighted runners.
				for (int i = transforms.size() - 1; i >= 0; i--){
					drawRunner(g2, colors.get(i), strokes.get(i), shapes, transforms.get(i));
				}
			}
		}
		/** Draw the runner at a certain state. NOTE NOTE NOTE: This version subtracts out the X components of everything. **/
		private void drawRunner(Graphics2D g, Color drawColor, Stroke stroke, Shape[] shapes, XForm[] transforms){

			float thisBodyXOffset = transforms[0].position.x; // Offset in actual world coordinates so all the torsos line up.
			
			for (int i = 0; i < shapes.length; i++){
				g.setColor(drawColor);
				g.setStroke(stroke);
				switch(shapes[i].getType()){
				case CIRCLE_SHAPE:
					CircleShape circleShape = (CircleShape)shapes[i];
					float radius = circleShape.getRadius();
					Vec2 circleCenter = XForm.mul(transforms[i], circleShape.getLocalPosition());
					g.drawOval((int)(runnerScaling * (circleCenter.x - radius  - thisBodyXOffset) + windowWidth/2),
							(int)(runnerScaling * (circleCenter.y - radius) + yOffsetPixels),
							(int)(runnerScaling * radius * 2),
							(int)(runnerScaling * radius * 2));
					break;
				case POLYGON_SHAPE:
					//Get both the shape and its transform.
					PolygonShape polygonShape = (PolygonShape)shapes[i];
					XForm transform = transforms[i];

					// Ground is black regardless.
					if (shapes[i].m_filter.groupIndex == 1){
						g.setColor(Color.BLACK);
						g.setStroke(normalStroke);
					}
					for (int j = 0; j < polygonShape.getVertexCount(); j++){ // Loop through polygon vertices and draw lines between them.
						Vec2 ptA = XForm.mul(transform, polygonShape.m_vertices[j]);
						Vec2 ptB = XForm.mul(transform, polygonShape.m_vertices[(j + 1) % (polygonShape.getVertexCount())]); //Makes sure that the last vertex is connected to the first one.
						g.drawLine((int)(runnerScaling * (ptA.x - thisBodyXOffset) + windowWidth/2),
								(int)(runnerScaling * ptA.y) + yOffsetPixels,
								(int)(runnerScaling * (ptB.x - thisBodyXOffset) + windowWidth/2),
								(int)(runnerScaling * ptB.y) + yOffsetPixels);		
					}
					break;
				default:
					break;
				}
			}
		}

		@Override
		public void activateTab() {
			active = true;
		}

		@Override
		public void deactivateTab() {
			active = false;
		}
		@Override
		public void actionPerformed(ActionEvent e) {
            JComboBox<Integer> eigSelect = (JComboBox<Integer>)e.getSource();
            lastEigToDisp = (Integer)eigSelect.getSelectedItem();
            eigsToDisp = new int[lastEigToDisp + 1];
            for (int i = 0; i < eigsToDisp.length; i++){
            	eigsToDisp[i] = 1;
            }
            //giveSelectedNode(primaryNode); // Redo calculations.
		}	
	}

	
	/**
	 * Pane for displaying plots of various state variables. Click each plot to pull up a menu for selecting data.
	 * A tab.
	 * @author Matt
	 */
	public class DataPane_State extends DataPane implements ItemListener{

		/** Which plot index has an active menu. **/
		int activePlotIdx = 0;

		/** Body parts associated with each plot and axis. **/
		private CondensedStateInfo.ObjectName[] plotObjectsX = new CondensedStateInfo.ObjectName[numberOfPlots];
		private CondensedStateInfo.ObjectName[] plotObjectsY = new CondensedStateInfo.ObjectName[numberOfPlots];

		/** State variables associated with each plot and axis. **/
		private CondensedStateInfo.StateName[] plotStatesX = new CondensedStateInfo.StateName[numberOfPlots];
		private CondensedStateInfo.StateName[] plotStatesY = new CondensedStateInfo.StateName[numberOfPlots];

		/** Drop down menus for the things to plot. **/
		private JComboBox<String> objListX;
		private JComboBox<String> stateListX;
		private JComboBox<String> objListY;
		private JComboBox<String> stateListY;

		/** String names of the body parts. **/
		private final String[] objNames = new String[CondensedStateInfo.ObjectName.values().length];

		/** String names of the state variables. **/
		private final String[] stateNames = new String[CondensedStateInfo.StateName.values().length];

		/** Menu for selecting which data is displayed. **/
		private final JDialog menu;

		/** Offsets to put the data selection menu right above the correct panel. **/
		private final int menuXOffset = 30;
		private final int menuYOffset = -30;

		public DataPane_State(){
			super();
			// Make string arrays of the body part and state variable names.
			int count = 0;
			for (CondensedStateInfo.ObjectName obj : CondensedStateInfo.ObjectName.values()) {
				objNames[count] = obj.name();
				count++;
			}
			count = 0;
			for (CondensedStateInfo.StateName st : CondensedStateInfo.StateName.values()) {
				stateNames[count] = st.name();
				count++;
			}

			// Initial plots to display			
			for (int i = 0; i < numberOfPlots; i++){
				plotObjectsX[i] = CondensedStateInfo.ObjectName.values()[TrialNodeMinimal.randInt(0, numberOfPlots - 1)];
				plotStatesX[i] = CondensedStateInfo.StateName.values()[TrialNodeMinimal.randInt(0, numberOfPlots - 1)];
				plotObjectsY[i] = CondensedStateInfo.ObjectName.values()[TrialNodeMinimal.randInt(0, numberOfPlots - 1)];
				plotStatesY[i] = CondensedStateInfo.StateName.values()[TrialNodeMinimal.randInt(0, numberOfPlots - 1)];	
			}

			// Drop down menus
			objListX = new JComboBox<>(objNames);
			stateListX = new JComboBox<>(stateNames);
			objListY = new JComboBox<>(objNames);
			stateListY = new JComboBox<>(stateNames);

			objListX.addItemListener(this);
			stateListX.addItemListener(this);
			objListY.addItemListener(this);
			stateListY.addItemListener(this);

			menu = new JDialog(); // Pop up box for the menus.
			menu.setLayout(new GridLayout(2,4));
			menu.add(new JLabel("X-axis", JLabel.CENTER));
			menu.getContentPane().add(objListX);
			menu.getContentPane().add(stateListX);
			menu.add(new JLabel("Y-axis", JLabel.CENTER));
			menu.getContentPane().add(objListY);
			menu.getContentPane().add(stateListY);

			menu.setAlwaysOnTop(true);
			menu.pack();
			menu.setVisible(false); // Start with this panel hidden.
		}

		public void update(){
			// Fetching new data.
			ArrayList<TrialNodeMinimal> nodesBelow = new ArrayList<TrialNodeMinimal>();
			if (selectedNode != null){
				selectedNode.getNodes_below(nodesBelow);

				for (int i = 0; i < numberOfPlots; i++){
					XYPlot pl = (XYPlot)plotPanels[i].getChart().getPlot();
					LinkStateCombination statePlotDat = new LinkStateCombination(nodesBelow);
					pl.setRenderer(statePlotDat.getRenderer());
					statePlotDat.addSeries(0, plotObjectsX[i], plotStatesX[i], plotObjectsY[i], plotStatesY[i]);
					pl.setDataset(statePlotDat);
					pl.getRangeAxis().setLabel(plotObjectsX[i].toString() + " " + plotStatesX[i].toString());
					pl.getDomainAxis().setLabel(plotObjectsY[i].toString() + " " + plotStatesY[i].toString());
					JFreeChart chart = plotPanels[i].getChart();
					chart.fireChartChanged();
					//XYPlot plot = (XYPlot)(plotPanels[i].getChart().getPlot());
					//pl.getDomainAxis().setRange(new Range(statePlotDat1.xLimLo,statePlotDat1.xLimHi));
					//plot.getRangeAxis().setRange(range);
				}

				if (!plotColorsByDepth){
					addCommandLegend((XYPlot)plotPanels[0].getChart().getPlot());
				}
			}

		}

		@Override
		public void deactivateTab(){
			super.deactivateTab();
			menu.setVisible(false);	
		}

		@Override
		public void plotClicked(int plotIdx) {
			activePlotIdx = plotIdx;
			menu.setTitle("Select plot " + (plotIdx + 1) + " data.");
			menu.setLocation(plotPanels[plotIdx].getX() + menuXOffset, tabPane.getY() + menuYOffset);
			objListX.setSelectedIndex(plotObjectsX[plotIdx].ordinal()); // Make the drop down menus match the current plots.
			objListY.setSelectedIndex(plotObjectsY[plotIdx].ordinal());
			stateListX.setSelectedIndex(plotStatesX[plotIdx].ordinal());
			stateListY.setSelectedIndex(plotStatesY[plotIdx].ordinal());

			menu.setVisible(true);
		}

		@Override
		public void itemStateChanged(ItemEvent e) {
			int state = e.getStateChange();
			if (state == ItemEvent.SELECTED){
				if (e.getSource() == objListX){
					plotObjectsX[activePlotIdx] = CondensedStateInfo.ObjectName.valueOf((String)e.getItem());
				}else if (e.getSource() == objListY){
					plotObjectsY[activePlotIdx] = CondensedStateInfo.ObjectName.valueOf((String)e.getItem());
				}else if (e.getSource() == stateListX){
					plotStatesX[activePlotIdx] = CondensedStateInfo.StateName.valueOf((String)e.getItem());
				}else if ((e.getSource() == stateListY)){
					plotStatesY[activePlotIdx] = CondensedStateInfo.StateName.valueOf((String)e.getItem());
				}else{
					throw new RuntimeException("Unknown item status in plots from: " + e.getSource().toString());
				}
				update();
			}
		}

	}

	/**
	 * Pane for displaying plots of the states transformed using principle component analysis (PCA). A tab.
	 * @author Matt
	 */
	public class DataPane_PCA extends DataPane implements KeyListener {
		// Which pairs of eigenvalues we're plotting.
		int[][] dataSelect;

		private TrialNodeMinimal lastSelectedNode;

		/** Data transformed by PCA **/
		private PCATransformedData pcaPlotDat;

		public boolean mouseIsIn = false;

		public DataPane_PCA(){
			super();
			addKeyListener(this);
			setFocusable(true);
			dataSelect = new int[numberOfPlots][2];

			// First set of plots are the 0 vs 1-6 eig
			for (int i = 0; i < numberOfPlots; i++){
				dataSelect[i] = new int[]{0,i};
			}
		}

		public void update(){
			requestFocus();
			setDatasets(dataSelect);
			for (int i = 0; i < numberOfPlots; i++){
				JFreeChart chart = plotPanels[i].getChart();
				chart.fireChartChanged();
				// TODO: resizing plots axes
				//XYPlot plot = (XYPlot)(plotPanels[i].getChart().getPlot());
				//plot.getDomainAxis().setRange(range);
				//plot.getRangeAxis().setRange(range);
			}
			XYPlot firstPlot = (XYPlot)plotPanels[0].getChart().getPlot();
			if (!plotColorsByDepth){
				addCommandLegend(firstPlot);
			}
		}

		private void setDatasets(int[][] dataSelect){			
			// Fetching new data.
			ArrayList<TrialNodeMinimal> nodesBelow = new ArrayList<TrialNodeMinimal>();
			if (selectedNode != null){

				// A state pair being added to the first plot.
				XYPlot pl = (XYPlot)plotPanels[0].getChart().getPlot();

				// Only update the plots shown, don't redo PCA calcs.
				if (lastSelectedNode != null && lastSelectedNode.equals(selectedNode)){
					pcaPlotDat = (PCATransformedData)pl.getDataset();
				}else{
					selectedNode.getNodes_below(nodesBelow);
					pcaPlotDat = new PCATransformedData(nodesBelow);
					lastSelectedNode = selectedNode;
				}

				pl.setRenderer(pcaPlotDat.getRenderer());
				pcaPlotDat.addSeries(0, dataSelect[0][0], dataSelect[0][1]);
				pl.setDataset(pcaPlotDat);
				pl.getDomainAxis().setLabel("Eig " + dataSelect[0][0] + " (" + Math.round(1000.*pcaPlotDat.evalsNormalized.get(dataSelect[0][0]))/10. + "%)");
				pl.getRangeAxis().setLabel("Eig " + dataSelect[0][1] + " (" + Math.round(1000.*pcaPlotDat.evalsNormalized.get(dataSelect[0][1]))/10. + "%)");

				for (int i = 1; i < dataSelect.length; i++){
					pl = (XYPlot)plotPanels[i].getChart().getPlot();
					PCATransformedData pcaPlotDatNext = pcaPlotDat.duplicateWithoutRecalcPCA();
					pl.setRenderer(pcaPlotDat.getRenderer());
					pcaPlotDatNext.addSeries(0, dataSelect[i][0], dataSelect[i][1]);
					pl.setDataset(pcaPlotDatNext);
					pl.getDomainAxis().setLabel("Eig " + dataSelect[i][0] + " (" + Math.round(1000.*pcaPlotDat.evalsNormalized.get(dataSelect[i][0]))/10. + "%)");
					pl.getRangeAxis().setLabel("Eig " + dataSelect[i][1] + " (" + Math.round(1000.*pcaPlotDat.evalsNormalized.get(dataSelect[i][1]))/10. + "%)");
				}	
			}
		}

		public void plotShift(int xShift, int yShift){
			if (xShift != 0 || yShift != 0){
				// First set of plots are the 0 vs 1-6 eig

				XYPlot pl = (XYPlot)plotPanels[0].getChart().getPlot();
				PCATransformedData dat = (PCATransformedData)pl.getDataset();
				int totalEigs = dat.evals.length;

				if (dataSelect[0][0] + xShift < 0 || dataSelect[numberOfPlots - 1][0] + xShift > totalEigs - 1){
					xShift = 0;
				}

				if (dataSelect[0][1] + yShift < 0 || dataSelect[numberOfPlots - 1][1] + yShift > totalEigs - 1){
					yShift = 0;
				}

				for (int i = 0; i < numberOfPlots; i++){

					dataSelect[i][0] = dataSelect[i][0] + xShift; // Clamp within the actual number of eigenvalues we have.
					dataSelect[i][1] = dataSelect[i][1] + yShift;
				}
				update();
			}
		}

		/** Get the last PCA data. **/
		public PCATransformedData getPCAData(){
			return pcaPlotDat;
		}

		@Override
		public void keyTyped(KeyEvent e) {
		}

		@Override
		public void keyPressed(KeyEvent e) {
			switch(e.getKeyCode()){
			case KeyEvent.VK_UP:
				plotShift(0,-1);
				break;
			case KeyEvent.VK_DOWN:
				plotShift(0,1);
				break;
			case KeyEvent.VK_LEFT:
				plotShift(-1,0);
				break;
			case KeyEvent.VK_RIGHT:
				plotShift(1,0);
				break;
			case KeyEvent.VK_B:
				plotColorsByDepth = !plotColorsByDepth;
				break;
			}	
		}

		@Override
		public void keyReleased(KeyEvent e) {}

		@Override
		public void plotClicked(int plotIdx) {
			// TODO Auto-generated method stub

		}
	
		@Override
		public void activateTab() {
			active = true;
			// update(); // Don't update though. Make the user click a node before doing this.
			requestFocus();
		}
	}

	/** Plots are made here. **/
	
	/**
	 * Plots extend this for basic features. Specific XYDataset objects are inside here. A tab.
	 * @author Matt
	 */
	abstract public class DataPane extends JPanel implements TabbedPaneActivator, ChartMouseListener{

		/** How many plots do we want to squeeze in there horizontally? **/
		protected final int numberOfPlots = 6;

		/** Array of the numberOfPlots number of plots we make. **/
		public ChartPanel[] plotPanels = new ChartPanel[numberOfPlots];

		/** Is this tab active? Do we bother to do updates in other words. **/
		public boolean active = false;

		public DataPane(){
			setLayout(new BoxLayout(this, BoxLayout.X_AXIS));
			for (int i = 0; i < numberOfPlots; i++){
				JFreeChart chart = createChart(null,null); // Null means no title and no data yet too
				ChartPanel chartPanel = new ChartPanel(chart);
				chartPanel.addChartMouseListener(this);
				chartPanel.setPopupMenu(null);
				chartPanel.setDomainZoomable(false);
				chartPanel.setRangeZoomable(false);
				chartPanel.setVisible(true);
				plotPanels[i] = chartPanel;
				add(chartPanel);
			}
			pack();
		}

		/** Check if the bounds need expanding, tell JFreeChart to update, and set the bounds correctly **/
		public abstract void update();

		/** Tells what plot is clicked by the user. **/
		public abstract void plotClicked(int plotIdx);

		/** Plotting colors for dots. **/
		public final Color actionColor1 = TrialNodeMinimal.getColorFromTreeDepth(0);
		public final Color actionColor2 = TrialNodeMinimal.getColorFromTreeDepth(10);
		public final Color actionColor3 = TrialNodeMinimal.getColorFromTreeDepth(20);
		public final Color actionColor4 = TrialNodeMinimal.getColorFromTreeDepth(30);


		/** My default settings for each plot. **/
		private JFreeChart createChart(XYDataset dataset,String name) {
			JFreeChart chart = ChartFactory.createScatterPlot(name,
					"X", "Y", dataset, PlotOrientation.VERTICAL, false, false, false);

			chart.setBackgroundPaint(appleGray);
			chart.setPadding(new RectangleInsets(-8,-12,-8,-5)); // Pack em in really tight.
			chart.setBorderVisible(false);

			XYPlot plot = (XYPlot) chart.getPlot();

			plot.setNoDataMessage("NO DATA");
			plot.setDomainZeroBaselineVisible(true);
			plot.setRangeZeroBaselineVisible(true);
			plot.setBackgroundPaint(Color.WHITE); // Background of actual plotting area, not the surrounding border area.


			NumberAxis domainAxis = (NumberAxis) plot.getDomainAxis();
			domainAxis.setAutoRangeIncludesZero(false);
			domainAxis.setTickMarkInsideLength(2.0f);
			domainAxis.setTickMarkOutsideLength(0.0f);
			domainAxis.setLabelFont(littleFont);
			domainAxis.setRange(new Range(-10, 10));

			NumberAxis rangeAxis = (NumberAxis) plot.getRangeAxis();
			rangeAxis.setTickMarkInsideLength(2.0f);
			rangeAxis.setTickMarkOutsideLength(0.0f);
			rangeAxis.setLabelFont(littleFont);
			rangeAxis.setRange(new Range(-10, 10));
			return chart;
		}

		/** Add some labels for which action led to this state. Hackish. **/
		public void addCommandLegend(XYPlot pl){
			double axisDUB = pl.getDomainAxis().getUpperBound();
			double axisDLB = pl.getDomainAxis().getLowerBound();
			double axisDSpan = (axisDUB - axisDLB);
			double axisRUB = pl.getRangeAxis().getUpperBound();
			double axisRLB = pl.getRangeAxis().getLowerBound();
			double axisRSpan = (axisRUB - axisRLB);

			XYTextAnnotation a1 = new XYTextAnnotation("< -, - >", axisDLB + axisDSpan/8, axisRUB - axisRSpan/12);
			a1.setPaint(actionColor1);
			a1.setFont(new Font(Font.SANS_SERIF,Font.BOLD,16));

			XYTextAnnotation a2 = new XYTextAnnotation("< W, O >", axisDLB + axisRSpan/8, axisRUB - 2*axisRSpan/12);
			a2.setPaint(actionColor2);
			a2.setFont(new Font(Font.SANS_SERIF,Font.BOLD,16));


			XYTextAnnotation a3 = new XYTextAnnotation("< -, - >", axisDLB + axisRSpan/8, axisRUB - 3*axisRSpan/12);
			a3.setPaint(actionColor3);
			a3.setFont(new Font(Font.SANS_SERIF,Font.BOLD,16));

			XYTextAnnotation a4 = new XYTextAnnotation("< Q, P >", axisDLB + axisRSpan/8, axisRUB - 4*axisRSpan/12);
			a4.setPaint(actionColor4);
			a4.setFont(new Font(Font.SANS_SERIF,Font.BOLD,16));
			pl.addAnnotation(a1);
			pl.addAnnotation(a2);
			pl.addAnnotation(a3);
			pl.addAnnotation(a4);
		}

		@Override
		public void chartMouseClicked(ChartMouseEvent event) {
			JFreeChart clickedChart = event.getChart();
			for (int i = 0; i < numberOfPlots; i++){
				if(plotPanels[i].getChart() == (clickedChart)){
					plotClicked(i); // Alert implementation specific method.
					break;
				}
			}
		}

		@Override
		public void chartMouseMoved(ChartMouseEvent event) {}

		@Override
		public void activateTab() {
			active = true;
			update();
			requestFocus();

		}
		@Override
		public void deactivateTab() {
			active = false;
		}

		/** XYDataset gets data for ploting states vs other states here. **/
		public class LinkStateCombination extends AbstractXYDataset{

			public float xLimHi = Float.MIN_VALUE;
			public float xLimLo = Float.MAX_VALUE;

			/** Nodes to appear on the plot. **/
			private final ArrayList<TrialNodeMinimal> nodeList;

			private Map<Integer,Pair> dataSeries = new HashMap<Integer,Pair>();

			private StatePlotRenderer renderer = new StatePlotRenderer();

			public LinkStateCombination(ArrayList<TrialNodeMinimal> nodes){
				nodeList = nodes;
			}

			public void addSeries(int plotIdx, CondensedStateInfo.ObjectName objectX, CondensedStateInfo.StateName stateX,
					CondensedStateInfo.ObjectName objectY, CondensedStateInfo.StateName stateY){
				dataSeries.put(plotIdx, new Pair(plotIdx, objectX, stateX,
						objectY, stateY));
			}

			@Override
			public Number getX(int series, int item) {
				CondensedStateInfo state = nodeList.get(item).state; // Item is which node.
				Pair dat = dataSeries.get(series);
				float value = state.getStateVarFromName(dat.objectX, dat.stateX);

				if (value > xLimLo){
					xLimLo = value;
				}
				if (value < xLimHi){
					xLimLo = value;
				}

				return value;
			}
			@Override
			public Number getY(int series, int item) {
				CondensedStateInfo state = nodeList.get(item).state; // Item is which node.
				Pair dat = dataSeries.get(series);
				return state.getStateVarFromName(dat.objectY, dat.stateY);
			}

			/** State + body part name pairs for looking up data. **/
			private class Pair{
				CondensedStateInfo.ObjectName objectX;
				CondensedStateInfo.StateName stateX;
				CondensedStateInfo.ObjectName objectY;
				CondensedStateInfo.StateName stateY;

				public Pair(int plotIdx, CondensedStateInfo.ObjectName objectX, CondensedStateInfo.StateName stateX,
						CondensedStateInfo.ObjectName objectY, CondensedStateInfo.StateName stateY){
					this.objectX = objectX;
					this.objectY = objectY;
					this.stateX = stateX;
					this.stateY = stateY;
				}
			}

			@Override
			public int getItemCount(int series) {
				return nodeList.size();
			}

			@Override
			public int getSeriesCount() {
				return dataSeries.size();
			}

			@Override
			public Comparable getSeriesKey(int series) {
				// TODO Auto-generated method stub
				return null;
			}

			public XYLineAndShapeRenderer getRenderer(){
				return renderer;
			}

			public class StatePlotRenderer extends XYLineAndShapeRenderer {
				/** Color points by corresponding depth in the tree or by command leading to this point. **/
				public boolean colorByDepth = plotColorsByDepth;

				public StatePlotRenderer() {
					super(false, true); //boolean lines, boolean shapes
					setSeriesShape( 0, new Ellipse2D.Double( -2.0, -2.0, 2.0, 2.0 ) );
					setUseOutlinePaint(false);
				}

				@Override
				public Paint getItemPaint(int series, int item) {
					if (colorByDepth){
						return TrialNodeMinimal.getColorFromTreeDepth(nodeList.get(item).treeDepth);
					}else{
						Color dotColor = Color.RED;
						switch (nodeList.get(item).treeDepth % 4){
						case 0:
							dotColor = actionColor1;
							break;
						case 1:
							dotColor = actionColor2;
							break;
						case 2:
							dotColor = actionColor3;
							break;
						case 3:
							dotColor = actionColor4;
							break;
						}
						return dotColor;
					}
				}
				@Override
				public java.awt.Shape getItemShape(int row, int col){ // Dumb because box2d also has shape imported.
					//				if (col == pane.selectedPoint) {
					//					return (java.awt.Shape)BigMarker;
					//				} else {
					return (java.awt.Shape) super.getItemShape(row, col);
					//				}
				}
			}
		}

		/** XYDataset gets data for plotting transformed data from PCA here. **/
		public class PCATransformedData extends AbstractXYDataset{

			/** Nodes to appear on the plot. **/
			private ArrayList<TrialNodeMinimal> nodeList;

			/** Eigenvectors found during SVD of the conditioned states. They represent the principle directions that explain most of the state variance. **/
			private FloatMatrix evecs;

			/** During SVD we find the eigenvalues, the weights for what portion of variance is explained by the corresponding eigenvector. **/
			private FloatMatrix evals;

			/** Normalized so the sum of the evals == 1 **/
			private FloatMatrix evalsNormalized;

			/** The conditioned dataset. Mean of each state is subtracted and divided by its standard deviation to give a variance of 1. **/
			private FloatMatrix dataSet;

			private PCAPlotRenderer renderer = new PCAPlotRenderer();

			/** Specific series of data to by plotted. Integer is the plotindex, the matrix is 2xn for x-y plot. **/
			private Map<Integer,FloatMatrix> tformedData = new HashMap<Integer,FloatMatrix>();

			ArrayList<CondensedStateInfo.ObjectName> objectsUsed = new ArrayList<CondensedStateInfo.ObjectName>(Arrays.asList(CondensedStateInfo.ObjectName.values()));
			ArrayList<CondensedStateInfo.StateName> statesUsed = new ArrayList<CondensedStateInfo.StateName>(Arrays.asList(CondensedStateInfo.StateName.values()));


			public PCATransformedData(ArrayList<TrialNodeMinimal> nodes){
				super();
				// Can blacklist things NOT to be PCA'd
				statesUsed.remove(CondensedStateInfo.StateName.X);

				nodeList = nodes;
				doPCA();
			}

			private PCATransformedData(){
				super();
			}

			/** Make another one of these but using the same PCA calculation. No data series are specified. **/
			public PCATransformedData duplicateWithoutRecalcPCA(){
				PCATransformedData duplicate = new PCATransformedData();
				duplicate.dataSet = dataSet;
				duplicate.nodeList = nodeList;
				duplicate.evals = evals;
				duplicate.evecs = evecs;
				duplicate.evalsNormalized = evalsNormalized;
				return duplicate;
			}

			/** PCA is already done when the object is created with a list of nodes. This determines which data, transformed according to which
			 * eigenvalue, is plotted on which plot index.
			 * @param plotIdxNum
			 * @param eigForXAxis
			 * @param eigForYAxis
			 */
			public void addSeries(int plotIdx, int eigForXAxis, int eigForYAxis){
				tformedData.put(plotIdx, dataSet.mmul(evecs.getColumns(new int[]{eigForXAxis,eigForYAxis})));	
			}

			/** Mostly for external use. Transform any data by the chosen PCA components. Must have done the PCA already! **/
			public FloatMatrix transformDataset(ArrayList<TrialNodeMinimal> nodesToTransform, int[] chosenPCAComponents){
				FloatMatrix preppedDat = prepTrialNodeData(nodesToTransform, objectsUsed, statesUsed);
				FloatMatrix lowDimData = preppedDat.mmul(evecs.getColumns(chosenPCAComponents));
				return lowDimData;
			}

			@Override
			public Number getX(int series, int item) {
				return tformedData.get(series).get(item,0);
			}

			@Override
			public Number getY(int series, int item) {
				return tformedData.get(series).get(item,1);
			}

			/** Run PCA on all the states in the nodes stored here. **/
			public void doPCA(){

				dataSet = prepTrialNodeData(nodeList, objectsUsed, statesUsed);

				FloatMatrix[] USV = Singular.fullSVD(dataSet);
				evecs = USV[2]; // Eigenvectors
				evals = USV[1].mul(USV[1]).div(dataSet.rows); // Eigenvalues
				// Transforming with the first two eigenvectors
				//tformedData = dataSet.mmul(evecs.getColumns(new int[]{pcaEigX,pcaEigY}));

				// Also make the vector of normalized eigenvalues.
				float evalSum = 0;
				for (int i = 0; i < evals.length; i++){
					evalSum += evals.get(i);
				}
				evalsNormalized = new FloatMatrix(evals.length);
				for (int i = 0; i < evals.length; i++){
					evalsNormalized.put(i, evals.get(i)/evalSum);
				}
			}

			/** Unpack the state data from the nodes, pulling only the stuff we want. Condition data to variance 1, mean 0. **/
			private FloatMatrix prepTrialNodeData(ArrayList<TrialNodeMinimal> nodes, 
					ArrayList<CondensedStateInfo.ObjectName> includedObjects, 
					ArrayList<CondensedStateInfo.StateName> includedStates){


				int numStates = includedObjects.size() * includedStates.size();
				FloatMatrix dat = new FloatMatrix(nodes.size(), numStates);

				// Iterate through all nodes
				for (int i = 0; i < nodes.size(); i++){
					int colCounter = 0;
					// Through all body parts...
					for (CondensedStateInfo.ObjectName obj : includedObjects){
						// For each state of each body part.
						for (CondensedStateInfo.StateName st : includedStates){
							dat.put(i, colCounter, nodes.get(i).state.getStateVarFromName(obj, st));
							colCounter++;
						}
					}
				}
				conditionData(dat);
				return dat;
			}

			/** Subtracts the mean for each variable, and converts to unit variance.
			 *  Samples are rows, variables are columns. Alters matrix x.
			 * @param x
			 */
			private void conditionData(FloatMatrix x){

				for (int i = 0; i < x.columns; i++){
					// Calculate the mean of a column.
					float sum = 0;
					for (int j = 0; j < x.rows; j++){
						sum += x.get(j,i);
					}
					// Subtract the mean out.
					float avg = sum/(float)x.rows;
					for (int j = 0; j < x.rows; j++){
						float centered = x.get(j,i) - avg;
						x.put(j,i,centered);
					}
					// Find the standard deviation for each column.
					sum = 0;
					for (int j = 0; j < x.rows; j++){
						sum += x.get(j,i) * x.get(j,i);
					}
					float std = (float)Math.sqrt(sum/(float)(x.rows - 1));

					// Divide the standard deviation out.
					for (int j = 0; j < x.rows; j++){
						float unitVar = x.get(j,i)/std;
						x.put(j,i,unitVar);
					}	
				}	
			}

			@Override
			public int getItemCount(int series) {
				return nodeList.size();
			}

			@Override
			public int getSeriesCount() {
				return tformedData.size();
			}

			@Override
			public Comparable getSeriesKey(int series) {
				// TODO Auto-generated method stub
				return null;
			}

			public XYLineAndShapeRenderer getRenderer(){
				return renderer;
			}

			public class PCAPlotRenderer extends XYLineAndShapeRenderer {
				/** Color points by corresponding depth in the tree or by command leading to this point. **/
				public boolean colorByDepth = plotColorsByDepth;

				public PCAPlotRenderer() {
					super(false, true); //boolean lines, boolean shapes
					setSeriesShape( 0, new Rectangle2D.Double( -2.0, -2.0, 2.0, 2.0 ) );
					setUseOutlinePaint(false);
				}

				@Override
				public Paint getItemPaint(int series, int item) {
					if (colorByDepth){
						return TrialNodeMinimal.getColorFromTreeDepth(nodeList.get(item).treeDepth);
					}else{
						Color dotColor = Color.RED;
						switch (nodeList.get(item).treeDepth % 4){
						case 0:
							dotColor = actionColor1;
							break;
						case 1:
							dotColor = actionColor2;
							break;
						case 2:
							dotColor = actionColor3;
							break;
						case 3:
							dotColor = actionColor4;
							break;
						}
						return dotColor;
					}
				}
				@Override
				public java.awt.Shape getItemShape(int row, int col){ // Dumb because box2d also has shape imported.
					//				if (col == pane.selectedPoint) {
					//					return (java.awt.Shape)BigMarker;
					//				} else {
					return (java.awt.Shape) super.getItemShape(row, col);
					//				}
				}
			}
		}
	}

	/** All panes should implement this so we can switch which is active at any given moment. **/
	private interface TabbedPaneActivator {
		public void activateTab();
		public void deactivateTab();
	}	
}
