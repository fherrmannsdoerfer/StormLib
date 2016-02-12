package functionDefinitions;

import java.awt.Color;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import StormLib.Utilities;
import dataStructure.StormData;
import functions.FeatureBasedDriftCorrection;
import gui.Controler;
import gui.MainFrame;
import gui.MyPropertyChangeListener;
import gui.ProcessingStepsPanel;

public class MergePointsGUI extends ProcessingStepsPanel{
	JTextField distx = new JTextField();
	JTextField disty = new JTextField();
	JTextField distz = new JTextField();
	JTextField distframe = new JTextField();
	private static String name = "MergePoints";
	public MergePointsGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getColorProcessing());
		this.setOptionPanel(createOptionPanel());
	}
	
	public MergePointsGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Maximal Tolerated Distance in X:"));
		distx.setText("100");
		verticalBox.add(distx);
		verticalBox.add(new JLabel("Maximal Tolerated Distance in Y:"));
		disty.setText("100");
		verticalBox.add(disty);
		verticalBox.add(new JLabel("Maximal Tolerated Distance in Z:"));
		distz.setText("200");
		verticalBox.add(distz);
		verticalBox.add(new JLabel("Maximal Tolerated Distance in Frames:"));
		distframe.setText("3");
		verticalBox.add(distframe);
		
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public int getDistX(){
		try{
			return Integer.valueOf(distx.getText());
		}
		catch(Exception e){
			return Integer.valueOf("100");
		}
	}
	public int getDistY(){
		try{
			return Integer.valueOf(distx.getText());
		}
		catch(Exception e){
			return Integer.valueOf("100");
		}
	}
	public int getDistZ(){
		try{
			return Integer.valueOf(distx.getText());
		}
		catch(Exception e){
			return Integer.valueOf("200");
		}
	}
	public int getDistFrames(){
		try{
			return Integer.valueOf(distx.getText());
		}
		catch(Exception e){
			return Integer.valueOf("3");
		}
		
	}
	public String[] getSettings(){
		String[] tempString = {distx.getText(), disty.getText(), distz.getText(), distframe.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		distx.setText(tempString[0]);
		disty.setText(tempString[1]);
		distz.setText(tempString[2]);
		distframe.setText(tempString[3]);
	}
	public MergePointsGUI getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof MergePointsGUI){
			MergePointsGUI returnObject = new MergePointsGUI(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
		return new MergePointsGUI(mf);
	}
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {
		PropertyChangeListener pcl = new MyPropertyChangeListener(this);
		Utilities.addPropertyChangeListener(pcl);
		sd1.connectPoints(getDistX(), getDistY(), getDistZ(), getDistFrames());
		setProgressbarValue(100);
		
	}
}
