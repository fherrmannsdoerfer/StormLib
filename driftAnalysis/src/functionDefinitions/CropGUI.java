package functionDefinitions;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class CropGUI extends ProcessingStepsPanel{
	JTextField minX = new JTextField();
	JTextField maxX = new JTextField();
	JTextField minY = new JTextField();
	JTextField maxY = new JTextField();
	JTextField minZ = new JTextField();
	JTextField maxZ = new JTextField();
	JTextField minFrame = new JTextField();
	JTextField maxFrame = new JTextField();
	private static String name = "Crop";
	
	public CropGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(Color.WHITE);
		this.setOptionPanel(createOptionPanel());
	}
	
	public CropGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Path:"));
		verticalBox.add(new JLabel("Pattern:"));
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public String[] getSettings(){
		String[] tempString = {minX.getText(), maxX.getText(), minY.getText(), maxY.getText(), minZ.getText(), maxZ.getText(), minFrame.getText(), maxFrame.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		minX.setText(tempString[0]);
		maxX.setText(tempString[1]);
		minY.setText(tempString[2]);
		maxY.setText(tempString[3]);
		minZ.setText(tempString[4]);
		maxZ.setText(tempString[5]);
		minFrame.setText(tempString[6]);
		maxFrame.setText(tempString[7]);
	}
	public CropGUI getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof CropGUI){
			CropGUI returnObject = new CropGUI(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
		return new CropGUI(mf);
	}
	public String getFunctionName(){
		return name;
	}
}
