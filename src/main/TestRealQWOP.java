package main;

import java.awt.BorderLayout;

import javax.swing.JComponent;
import javax.swing.JFrame;
import javax.swing.SwingUtilities;

import chrriis.common.UIUtils;
import chrriis.dj.nativeswing.swtimpl.NativeInterface;
import chrriis.dj.nativeswing.swtimpl.components.JFlashPlayer;

public class TestRealQWOP {

	  public static JComponent createContent() {
	    JFlashPlayer flashPlayer = new JFlashPlayer();
	    flashPlayer.load(Utility.getExcutionPath() + "./Andy.swf");
	    return flashPlayer;
	  }

	  /* Standard main method to try that test as a standalone application. */
	  public static void main(String[] args) {
	    NativeInterface.open();
	    UIUtils.setPreferredLookAndFeel();
	    SwingUtilities.invokeLater(new Runnable() {
	      public void run() {
	        JFrame frame = new JFrame("DJ Native Swing Test");
	        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
	        frame.getContentPane().add(createContent(), BorderLayout.CENTER);
	        frame.setSize(1200, 1200);
	        frame.setLocationByPlatform(true);
	        frame.setVisible(true);
	      }
	    });
	    NativeInterface.runEventPump();
	  }

	}
