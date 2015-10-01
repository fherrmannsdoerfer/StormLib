package functionDefinitions;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class MultipleFileInputGUI extends ProcessingStepsPanel{
	JTextField path = new JTextField();
	JTextField pattern = new JTextField();
	public MultipleFileInputGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName("Multiple File Input");
		this.setColor(Color.WHITE);
		this.setOptionPanel(createOptionPanel());
	}
	
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
}
