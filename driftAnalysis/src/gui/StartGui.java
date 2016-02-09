package gui;

import java.awt.EventQueue;
import java.util.ArrayList;

import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;

import functionDefinitions.DualColorMultipleFileInputGUI;
import StormLib.StormData;
import StormLib.Utilities;
import gui.MainFrame;

public class StartGui {
	static Controler controler;
	static MainFrame mf;
	
	public static void main(String[] args) {
		controler = new Controler();
		try {
	        // Set System L&F
	    UIManager.setLookAndFeel(
	        UIManager.getSystemLookAndFeelClassName());
	    } 
	    catch (UnsupportedLookAndFeelException e) {
	       // handle exception
	    }
	    catch (ClassNotFoundException e) {
	       // handle exception
	    }
	    catch (InstantiationException e) {
	       // handle exception
	    }
	    catch (IllegalAccessException e) {
	       // handle exception
	    }
		EventQueue.invokeLater(new Runnable() {
			public void run() {
				try {
					mf = new MainFrame(controler);
					controler.setMainFrameReference(mf);
					mf.setVisible(true);
					mf.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
				} catch (Exception e) {
					e.printStackTrace();
				}
			}
		});
	}
	
}
