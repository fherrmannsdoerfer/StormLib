package functionDefinitions;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class DualColorMultipleFileInputGUI extends ProcessingStepsPanel{
	JTextField path1 = new JTextField();
	JTextField pattern1 = new JTextField();
	JTextField path2 = new JTextField();
	JTextField pattern2 = new JTextField();
	
	public DualColorMultipleFileInputGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName("Multiple File Input");
		this.setColor(Color.WHITE);
		this.setOptionPanel(createOptionPanel());
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		Dimension d = new Dimension(350,22);
		path1.setPreferredSize(d);
		path2.setPreferredSize(d);
		pattern1.setPreferredSize(d);
		pattern2.setPreferredSize(d);
		pattern1.setText("Left");
		pattern2.setText("Right");
		verticalBox.add(new JLabel("Path 1:"));
		verticalBox.add(path1);
		verticalBox.add(new JLabel("Pattern 1:"));
		verticalBox.add(pattern1);
		verticalBox.add(new JLabel("Path 2:"));
		verticalBox.add(path2);
		verticalBox.add(new JLabel("Pattern 2:"));
		verticalBox.add(pattern2);
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public String getPath1(){
		return path1.getText();
	}
	public String getPattern1(){
		return pattern1.getText();
	}
	public String getPath2(){
		return path2.getText();
	}
	public String getPattern2(){
		return pattern2.getText();
	}
	public String[] getSettings(){
		String[] tempString = {path1.getText(), pattern1.getText(), path2.getText(), pattern2.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		path1.setText(tempString[0]);
		pattern1.setText(tempString[1]);
		path2.setText(tempString[2]);
		pattern2.setText(tempString[3]);
	}
	public DualColorMultipleFileInputGUI getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof DualColorMultipleFileInputGUI){
			DualColorMultipleFileInputGUI returnObject = new DualColorMultipleFileInputGUI(mf);
			return returnObject;
		}
		return null;
	}
}