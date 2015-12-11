package functionDefinitions;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataStructure.StormData;
import functions.FeatureBasedDriftCorrection;
import gui.Controler;
import gui.MainFrame;
import gui.MyPropertyChangeListener;
import gui.ProcessingStepsPanel;

public class DriftcorrectionGUI extends ProcessingStepsPanel{
	JTextField chunksize = new JTextField();
	private static String name = "Driftcorrection";
	public DriftcorrectionGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(Color.RED);
		this.setOptionPanel(createOptionPanel());
	}
	
	public DriftcorrectionGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Chunk Size:"));
		verticalBox.add(chunksize);
		
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public int getChunksize(){
		try{
			return Integer.valueOf(chunksize.getText());
		}
		catch(Exception e){
			return Integer.valueOf("500");
		}
	
	}
	public String[] getSettings(){
		String[] tempString = {chunksize.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		chunksize.setText(tempString[0]);
	}
	public DriftcorrectionGUI getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof DriftcorrectionGUI){
			DriftcorrectionGUI returnObject = new DriftcorrectionGUI(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
		return new DriftcorrectionGUI(mf);
	}
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {
		PropertyChangeListener pcl = new MyPropertyChangeListener(this);
		FeatureBasedDriftCorrection.addPropertyChangeListener(pcl);
		sd1.correctDrift(getChunksize());	
		setProgressbarValue(100);	
	}

}
