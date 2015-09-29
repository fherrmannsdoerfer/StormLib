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
}