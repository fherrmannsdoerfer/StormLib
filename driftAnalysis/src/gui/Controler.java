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

public class Controler implements PropertyChangeListener, Serializable{
	MainFrame mf;
	static StormData ch1 = new StormData();
	static StormData ch2 = new StormData();
	
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
	public static void startProcessing(
			ArrayList<ProcessingStepsPanel> functions) {
		for (ProcessingStepsPanel psp: functions){
			psp.process(ch1, ch2);
		}
		System.out.println("fertig");
		
		
	}
	
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		
		if (evt.getPropertyName().equals("MultipleInputDC")) {
           dcmfi.setProgressbarValue((Integer)evt.getNewValue());
        }
		if (evt.getPropertyName().equals("MultipleInput")) {
           mfi.setProgressbarValue((Integer)evt.getNewValue());
        }
		if (evt.getPropertyName().equals("DriftCorrection")) {
            dc.setProgressbarValue((Integer)evt.getNewValue());
        }
		if (evt.getPropertyName().equals("MergePoints")) {
            mp.setProgressbarValue((Integer)evt.getNewValue());
        }
	}
	
}
