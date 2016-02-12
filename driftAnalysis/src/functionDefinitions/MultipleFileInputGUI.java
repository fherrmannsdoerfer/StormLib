package functionDefinitions;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataStructure.StormData;
import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class MultipleFileInputGUI extends ProcessingStepsPanel{
	JTextField path = new JTextField();
	JTextField pattern = new JTextField();
	private static String name = "MultipleFileInput";
	public MultipleFileInputGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getColorInput());
		this.setOptionPanel(createOptionPanel());
	}
	
	public MultipleFileInputGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Path:"));
		verticalBox.add(path);
		verticalBox.add(new JLabel("Pattern:"));
		verticalBox.add(pattern);
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public String getPath(){
		return path.getText();
	}
	public String getPattern(){
		return pattern.getText();
	}
	public String[] getSettings(){
		String[] tempString = {path.getText(), pattern.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		path.setText(tempString[0]);
		pattern.setText(tempString[1]);
	}
	public MultipleFileInputGUI getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof MultipleFileInputGUI){
			MultipleFileInputGUI returnObject = new MultipleFileInputGUI(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
	return new MultipleFileInputGUI(mf);
	}
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {		
		sd1.setFname(getPattern());
		sd1.setPath(getPath());		
//		sd1.setLocs(sd1.importData(getPath()+getPattern()));		
//		sd1.setLocs(sd1.importData(getPath()+getFilename()));
//		PropertyChangeListener pcl = new Controler();
//		Utilities.addPropertyChangeListener(pcl);
//		sd1 = Utilities.openSeries(getPath(), getPattern());
		setProgressbarValue(100);
	}
}
