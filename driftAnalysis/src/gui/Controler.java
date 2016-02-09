package gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.ArrayList;

import StormLib.Utilities;
import dataStructure.StormData;
import functionDefinitions.CropGUI;
import functionDefinitions.DemixingGUI;
import functionDefinitions.DriftcorrectionGUI;
import functionDefinitions.DualChannelSingleFileInputGUI;
import functionDefinitions.DualColorMultipleFileInputGUI;
import functionDefinitions.MergePointsGUI;
import functionDefinitions.MultipleFileInputGUI;
import functionDefinitions.RenderImage2DGUI;
import functionDefinitions.RenderImage3DGUI;
import functionDefinitions.SingleFileInputGUI;
import functions.Demixing;
import functions.FeatureBasedDriftCorrection;

public class Controler implements Serializable{
	MainFrame mf;
	StormData ch1 = new StormData();
	StormData ch2 = new StormData();
	
	public static DriftcorrectionGUI dc;
	public static DualColorMultipleFileInputGUI dcmfi;
	public static MultipleFileInputGUI mfi;
	public static MergePointsGUI mp;
	
	public void resetProgressBar(ArrayList<ProcessingStepsPanel> functions){
	    for (int i = 0; i<functions.size(); i++){
	        functions.get(i).setProgressbarValue(0);
	    }
	}
	
	
	public void setMainFrameReference(MainFrame mf) {
		this.mf = mf;
	}
	public void startProcessing(
			ArrayList<ProcessingStepsPanel> functions) {
		for (ProcessingStepsPanel psp: functions){
			psp.process(ch1, ch2);
		}
		
		
	}
	
}
