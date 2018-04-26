package main;

import java.awt.Dimension;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import javax.swing.JFrame;

import org.tensorflow.example.Example;
import org.tensorflow.example.Feature;
import org.tensorflow.example.FeatureList;
import org.tensorflow.example.SequenceExample;

import com.google.common.io.LittleEndianDataInputStream;

import data.TFRecordReader;
import game.GameLoader;
import game.State;
import game.StateVariable;
import ui.PanelRunner_AnimatedFromStates;

/**
 * Playback runs or sections of runs saved in SaveableSingleRun files.
 * 
 * @author matt
 *
 */

@SuppressWarnings("serial")
public class MAIN_PlaybackSaved_TFRecord extends JFrame{

	public GameLoader game;
	private PanelRunner_AnimatedFromStates runnerPane;

	/** Window width **/
	public static int windowWidth = 1920;

	/** Window height **/
	public static int windowHeight = 1000;


	File saveLoc = new File("./4_25_18");

	List<Node> leafNodes = new ArrayList<Node>(); 

	public static void main(String[] args) {
		MAIN_PlaybackSaved_TFRecord mc = new MAIN_PlaybackSaved_TFRecord();
		mc.setup();
		mc.run();
	}

	public void setup() {
		/* Runner pane */   
		runnerPane = new PanelRunner_AnimatedFromStates();
		runnerPane.activateTab();
		runnerPane.yOffsetPixels = 600;
		this.add(runnerPane);
		Thread runnerThread = new Thread(runnerPane);
		runnerThread.start();

		/*******************/

		this.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		this.setPreferredSize(new Dimension(windowWidth, windowHeight));
		this.setContentPane(this.getContentPane());
		this.pack();
		this.setVisible(true); 
		repaint();
	}

	public void run() {
		File[] allFiles = saveLoc.listFiles();
		if (allFiles == null) throw new RuntimeException("Bad directory given: " + saveLoc.getName());
		
		List<File> playbackFiles = new ArrayList<File>();
		for (File f : allFiles){
			if (f.getName().contains("TFRecord")) {
				playbackFiles.add(f);
			}
		}
		Collections.shuffle(playbackFiles);

		// Validate -- not needed during batch run.
		SequenceExample dataSeries = null;
		String fileName = playbackFiles.get(0).getAbsolutePath();
		System.out.println(fileName);
		FileInputStream fIn = null;

		try {
			fIn = new FileInputStream(fileName);
			DataInputStream dIn = new DataInputStream(fIn);

			TFRecordReader tfReader = new TFRecordReader(dIn, true);
			dataSeries = SequenceExample.parser().parseFrom(tfReader.read());
		} catch (IOException e) {
			e.printStackTrace();
		} finally {
			try {
				fIn.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		int totalTimestepsInRun = dataSeries.getFeatureLists().getFeatureListMap().get("BODY").getFeatureCount();
		State[] stateVars = new State[totalTimestepsInRun];

		for (int i = 0; i < totalTimestepsInRun; i++) {
			// Unpack each x y th... value in a given timestep. Turn them into StateVariables.
			Map<String, FeatureList> featureListMap = dataSeries.getFeatureLists().getFeatureListMap();
			StateVariable[] sVarBuffer = new StateVariable[State.ObjectName.values().length];
			int idx = 0;
			for (State.ObjectName bodyPart : State.ObjectName.values()) {
				List<Float> sValList = featureListMap.get(bodyPart.toString()).getFeature(i).getFloatList().getValueList();

				sVarBuffer[idx] = new StateVariable(sValList);
				idx++;
			}

			// Turn the StateVariables into a single State for this timestep.
			stateVars[i] = new State(sVarBuffer[0], sVarBuffer[1], sVarBuffer[2], sVarBuffer[3], sVarBuffer[4], 
					sVarBuffer[5], sVarBuffer[6], sVarBuffer[7], sVarBuffer[8], sVarBuffer[9], sVarBuffer[10], sVarBuffer[11], false);

		}

		runnerPane.simRun(new LinkedList(Arrays.asList(stateVars)));
		
		while (!runnerPane.isFinishedWithRun()) {
			runnerPane.repaint();
			try {
				Thread.sleep(20);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}
}