package functionDefinitions;

import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import StormLib.Utilities;
import dataStructure.StormData;
import gui.MainFrame;
import gui.ProcessingStepsPanel;


public class BatchProcessingGUI extends ProcessingStepsPanel{
	JTextField path = new JTextField();
	JTextField foldername = new JTextField();
	ArrayList<String> paths = new ArrayList<String>();
	int counter = 0;
	private static String name = "Batch Processing";
	public BatchProcessingGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getBatchProcessingColor());
		path.setPreferredSize(mf.style.getDimensionPathFields());
		this.setOptionPanel(createOptionPanel());
	}
	
	public BatchProcessingGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		Dimension d = new Dimension(350,22);
		path.setPreferredSize(d);
		
		foldername.setPreferredSize(d);
		verticalBox.add(new JLabel("Path of Parent Directory:"));
		verticalBox.add(path);
		verticalBox.add(new JLabel("Pattern:"));
		verticalBox.add(foldername);
		foldername.setAlignmentX(0);
		path.setAlignmentX(0);
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public String getPath(){
		return path.getText();
	}
	public String getFolderName(){
		return foldername.getText();
	}
	
	public String[] getSettings(){
		String[] tempString = {path.getText(), foldername.getText()};
		return tempString;
	}
	public ArrayList<String> getPaths(){
		return paths;
	}
	public void setSettings(String[] tempString){
		path.setText(tempString[0]);
		foldername.setText(tempString[1]);
	}
	public BatchProcessingGUI getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof BatchProcessingGUI){
			BatchProcessingGUI returnObject = new BatchProcessingGUI(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
	return new BatchProcessingGUI(mf);
	}
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {	
		paths = Utilities.findPaths(getPath(), getFolderName());
		setProgressbarValue(0);
	}
}
