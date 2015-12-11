package functionDefinitions;

import functions.Demixing;
import gui.MainFrame;
import gui.ProcessingStepsPanel;

import java.awt.Color;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

import dataStructure.StormData;

public class DemixingGUI extends ProcessingStepsPanel{
	private static String name = "Demixing";
	public DemixingGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(Color.RED);
		this.setOptionPanel(createOptionPanel());
	}
	
	public DemixingGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		return retPanel;
	}
	public String[] getSettings(){
		String[] tempString = null;
		return tempString;
	}
	public void setSettings(String[] tempString){
	}
	public DemixingGUI getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof DemixingGUI){
			DemixingGUI returnObject = new DemixingGUI(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
		return new DemixingGUI(mf);
	}
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {
		int chunkSize = 5000;//number of frames per chunk to get "local" transformations
		int numberChunks = (int)Math.ceil((double)sd1.getDimensions().get(7)/chunkSize);
		ArrayList<StormData> chunksChannel1 = new ArrayList<StormData>();
		for (int j = 0; j < numberChunks; j++){
			chunksChannel1.add(sd1.findSubset(chunkSize*j,chunkSize*(j+1),false));
		}
		
		ArrayList<StormData> chunksChannel2 = new ArrayList<StormData>();
		for (int j = 0; j < numberChunks; j++){
			chunksChannel2.add(sd2.findSubset(chunkSize*j,chunkSize*(j+1),false));
		}

		ArrayList<StormData> unmixedChannels = new ArrayList<StormData>();
		for (int j = 0; j < numberChunks; j++){
			unmixedChannels.add(Demixing.spectralUnmixing(chunksChannel1.get(j), chunksChannel2.get(j),false));
			setProgressbarValue((int)(j*100./numberChunks));
		}
		StormData unmixedFromParts = new StormData();
		for (int j = 0; j < numberChunks; j++){
//			unmixedFromParts.addStormData(unmixedChannels.get(i)); //get(i) bezieht sich auf controler-schleife durch die listprocessingstepspanel, ist die nummer der ausgeführten funktion
			unmixedFromParts.addStormData(unmixedChannels.get(j));
		}
		sd1 = unmixedFromParts;
		setProgressbarValue(100);
		
	}
}
