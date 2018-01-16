package main;

import java.awt.Color;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;

import javax.swing.BorderFactory;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JTabbedPane;

import org.jbox2d.collision.shapes.CircleShape;
import org.jbox2d.collision.shapes.EdgeShape;
import org.jbox2d.collision.shapes.PolygonShape;
import org.jbox2d.collision.shapes.Shape;
import org.jbox2d.collision.shapes.ShapeType;
import org.jbox2d.common.Vec2;
import org.jbox2d.common.XForm;
import org.jbox2d.dynamics.Body;
import org.jbox2d.dynamics.World;

@SuppressWarnings("serial")
public class MAIN_Controlled extends JFrame{

	private QWOPGame game;
	private Tensorflow_Predictor pred = new Tensorflow_Predictor();
	private RunnerPane runnerPane;
	
	/** Physics engine stepping parameters. **/
	public final float timestep = 0.04f;
	private final int iterations = 5;
	
	/** Window width **/
	public static int windowWidth = 1920;

	/** Window height **/
	public static int windowHeight = 1000;
	
	final Font QWOPLittle = new Font("Ariel", Font.BOLD,21);
	final Font QWOPBig = new Font("Ariel", Font.BOLD,28);
	
	/** Runner coordinates to pixels. **/
	public float runnerScaling = 10f;
	
	/** Drawing offsets within the viewing panel (i.e. non-physical) **/
	public int xOffsetPixels_init = 700;
	public int xOffsetPixels = xOffsetPixels_init;
	public int yOffsetPixels = 600;
	
	private int phase = 0;
	
	public static void main(String[] args) {
		
		MAIN_Controlled mc = new MAIN_Controlled();
		mc.setup();
		mc.run();
	}
	
	public void setup() {
		/* Runner pane */   
		runnerPane = new RunnerPane();
		this.add(runnerPane);
		/*******************/

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(windowWidth, windowHeight));
		this.setContentPane(this.getContentPane());
		this.pack();
		this.setVisible(true); 
		repaint();
	}
	
	int toSwitchCount = Integer.MAX_VALUE;
	public void run() {
		game = new QWOPGame();
		
		game.Setup();
		runnerPane.setWorldToView(game.getWorld());
		
		
		while (true) {
			
			switch(phase) {
			case 0:
				game.everyStep(false,false,false,false);
				break;
			case 1:
				game.everyStep(false,true,true,false);
				break;
			case 2:
				game.everyStep(false,false,false,false);
				break;
			case 3:
				game.everyStep(true,false,false,true);
				break;
			default: 
				throw new RuntimeException("Sequence phase is busted: " + phase);
			}
			
			game.getWorld().step(timestep, iterations);
			
			State st = new State(game);
			float prediction = pred.getPrediction(st);
			System.out.println(prediction);
			
			if (toSwitchCount > 10 && prediction <1.2f) {
				
				System.out.println("SWITCHING SOON");
				toSwitchCount = (int) Math.round(prediction); 
			}
			
			if (toSwitchCount == 0) {
				phase = (phase + 1) % 4;
				toSwitchCount = Integer.MAX_VALUE;
			}
			toSwitchCount--;
			repaint();
			try {
				Thread.sleep(40);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
	
	/**
	 * Pane for displaying the animated runner executing a sequence selected on the tree. A tab.
	 * @author Matt
	 */
	public class RunnerPane extends JPanel {

		public int headPos;

		boolean active = true;

		private World world;
		
		public RunnerPane() {}

		public void paintComponent(Graphics g) {
			if (!active) return;
			super.paintComponent(g);

			if (world != null) {
				Body newBody = world.getBodyList();
				while (newBody != null) {

					Shape newfixture = newBody.getShapeList();

					while(newfixture != null) {

						if(newfixture.getType() == ShapeType.POLYGON_SHAPE) {

							PolygonShape newShape = (PolygonShape)newfixture;
							Vec2[] shapeVerts = newShape.m_vertices;
							for (int k = 0; k<newShape.m_vertexCount; k++) {

								XForm xf = newBody.getXForm();
								Vec2 ptA = XForm.mul(xf,shapeVerts[k]);
								Vec2 ptB = XForm.mul(xf, shapeVerts[(k+1) % (newShape.m_vertexCount)]);
								g.drawLine((int)(runnerScaling * ptA.x) + xOffsetPixels,
										(int)(runnerScaling * ptA.y) + yOffsetPixels,
										(int)(runnerScaling * ptB.x) + xOffsetPixels,
										(int)(runnerScaling * ptB.y) + yOffsetPixels);		
							}
						}else if (newfixture.getType() == ShapeType.CIRCLE_SHAPE) {
							CircleShape newShape = (CircleShape)newfixture;
							float radius = newShape.m_radius;
							headPos = (int)(runnerScaling * newBody.getPosition().x);
							g.drawOval((int)(runnerScaling * (newBody.getPosition().x - radius) + xOffsetPixels),
									(int)(runnerScaling * (newBody.getPosition().y - radius) + yOffsetPixels),
									(int)(runnerScaling * radius * 2),
									(int)(runnerScaling * radius * 2));		

						}else if(newfixture.getType() == ShapeType.EDGE_SHAPE) {

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
				for(int i = 0; i<this.getWidth()/69; i++) {
					g.drawString("_", ((xOffsetPixels - xOffsetPixels_init-i * 70) % getWidth()) + getWidth(), yOffsetPixels + 92);
					//keyDrawer(g, negotiator.Q,negotiator.W,negotiator.O,negotiator.P);
				}

				//drawActionString(negotiator.getCurrentSequence(), g, negotiator.getCurrentActionIdx());

			}else{
				//keyDrawer(g, false, false, false, false);
			}

			//    	g.drawString(dc.format(-(headpos+30)/40.) + " metres", 500, 110);
			xOffsetPixels = -headPos + xOffsetPixels_init;

		}

		public void setWorldToView(World world) {
			this.world = world;
		}

		public void clearWorldToView() {
			world = null;
		}

		public void keyDrawer(Graphics g, boolean q, boolean w, boolean o, boolean p) {

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

}