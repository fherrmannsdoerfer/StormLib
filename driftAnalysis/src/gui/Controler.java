package gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.ArrayList;

import StormLib.Utilities;
import dataStructure.StormData;
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

public class Controler implements PropertyChangeListener{
	MainFrame mf;
	static StormData ch1;
	static StormData ch2;
	
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
		for (int i = 0; i<functions.size(); i++){
			if (functions.get(i).getClass() == SingleFileInputGUI.class){
				SingleFileInputGUI sfi = (SingleFileInputGUI) functions.get(i);
				ch1 = new StormData(sfi.getPath(),sfi.getFoldername());
				sfi.setProgressbarValue(100);
			}
			
			if (functions.get(i).getClass() == MultipleFileInputGUI.class){
				mfi = (MultipleFileInputGUI) functions.get(i);
				PropertyChangeListener pcl = new Controler();
				Utilities.addPropertyChangeListener(pcl);
				ch1 = Utilities.openSeries(mfi.getPath(), mfi.getPattern());
				mfi.setProgressbarValue(100);
			}
			
			if (functions.get(i).getClass() == DualChannelSingleFileInputGUI.class){
				DualChannelSingleFileInputGUI dcsfi = (DualChannelSingleFileInputGUI) functions.get(i);
				ch1 = new StormData(dcsfi.getPath1(),dcsfi.getFile1());
				ch2 = new StormData(dcsfi.getPath2(),dcsfi.getFile2());
				dcsfi.setProgressbarValue(100);
			}
			if (functions.get(i).getClass() == DualColorMultipleFileInputGUI.class){
				dcmfi = (DualColorMultipleFileInputGUI) functions.get(i);
				PropertyChangeListener pcl = new Controler();
				Utilities.addPropertyChangeListener(pcl);
		    	ArrayList<StormData> list = Utilities.openSeries(dcmfi.getPath1(), dcmfi.getPattern1(), dcmfi.getPath2(), dcmfi.getPattern2());
				ch1 = list.get(0);
				ch2 = list.get(1);
				dcmfi.setProgressbarValue(100);
			}
			
			if (functions.get(i).getClass() == DriftcorrectionGUI.class){
				dc = (DriftcorrectionGUI) functions.get(i);
				PropertyChangeListener pcl = new Controler();
				FeatureBasedDriftCorrection.addPropertyChangeListener(pcl);
				dc.setProgressbarValue(100);
				ch1.correctDrift(dc.getChunksize());
			}
			
			if (functions.get(i).getClass() == MergePointsGUI.class){
				mp = (MergePointsGUI) functions.get(i);
				PropertyChangeListener pcl = new Controler();
				ch1.connectPoints(mp.getDistX(), mp.getDistY(), mp.getDistZ(), mp.getDistFrames());
				mp.setProgressbarValue(100);
			}
			
			if (functions.get(i).getClass() == DemixingGUI.class){
				int chunkSize = 5000;//number of frames per chunk to get "local" transformations
				int numberChunks = (int)Math.ceil((double)ch1.getDimensions().get(7)/chunkSize);
				ArrayList<StormData> chunksChannel1 = new ArrayList<StormData>();
				for (int j = 0; j < numberChunks; j++){
					chunksChannel1.add(ch1.findSubset(chunkSize*j,chunkSize*(j+1),false));
				}
				
				ArrayList<StormData> chunksChannel2 = new ArrayList<StormData>();
				for (int j = 0; j < numberChunks; j++){
					chunksChannel2.add(ch2.findSubset(chunkSize*j,chunkSize*(j+1),false));
				}

				ArrayList<StormData> unmixedChannels = new ArrayList<StormData>();
				for (int j = 0; j < numberChunks; j++){
					unmixedChannels.add(Demixing.spectralUnmixing(chunksChannel1.get(j), chunksChannel2.get(j),false));
					functions.get(i).setProgressbarValue((int)(j*100./numberChunks));
				}
				StormData unmixedFromParts = new StormData();
				for (int j = 0; j < numberChunks; j++){
					unmixedFromParts.addStormData(unmixedChannels.get(i));
				}
				ch1 = unmixedFromParts;
				functions.get(i).setProgressbarValue(100);
			}
			
			if (functions.get(i).getClass() == RenderImage2DGUI.class){
				RenderImage2DGUI ri2D = (RenderImage2DGUI) functions.get(i);
				ch1.renderImage2D(ri2D.getPixelsize(), ri2D.getTag());
				ri2D.setProgressbarValue(100);
			}
			if (functions.get(i).getClass() == RenderImage3DGUI.class){
				RenderImage3DGUI ri3D = (RenderImage3DGUI) functions.get(i);
				ch1.renderImage2D(ri3D.getPixelsize(), ri3D.getTag());
				ri3D.setProgressbarValue(100);
			}
		}
		
		
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
