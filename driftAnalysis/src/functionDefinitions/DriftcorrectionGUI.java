package functionDefinitions;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;

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

public class DriftcorrectionGUI extends ProcessingStepsPanel implements Serializable{
	JTextField chunksize = new JTextField();
	JTextField pixelsize = new JTextField();
	JTextField sigma = new JTextField();
	private static String name = "Drift Correction";
	public DriftcorrectionGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getColorProcessing());
		this.setOptionPanel(createOptionPanel());
	}
	
	public DriftcorrectionGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Chunk Size  [frames]:"));
		verticalBox.add(chunksize);
		chunksize.setText("5000");
		verticalBox.add(new JLabel("Pixel size for drift correction [nm]:"));
		verticalBox.add(pixelsize);
		pixelsize.setText("20");
		verticalBox.add(new JLabel("Sigma for drift correction [nm]:"));
		verticalBox.add(sigma);
		sigma.setText("10");
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public int getChunksize(){
		try{
			return Integer.valueOf(chunksize.getText());
		}
		catch(Exception e){
			return Integer.valueOf("5000");
		}
	
	}
	
	public int getPixelsize(){
		try{
			return Integer.valueOf(pixelsize.getText());
		}
		catch(Exception e){
			return Integer.valueOf("20");
		}
	
	}
	
	public int getSigma(){
		try{
			return Integer.valueOf(sigma.getText());
		}
		catch(Exception e){
			return Integer.valueOf("10");
		}
	
	}
	public String[] getSettings(){
		String[] tempString = {chunksize.getText(), pixelsize.getText(), sigma.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		chunksize.setText(tempString[0]);
		pixelsize.setText(tempString[1]);
		sigma.setText(tempString[2]);
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
		try{
			sd2.correctDrift(getChunksize(), getPixelsize(), getSigma());
		}
		catch(Exception e){
			
		}
		setProgressbarValue(100);	
	}

}
