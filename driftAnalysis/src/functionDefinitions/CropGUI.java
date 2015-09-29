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
	public CropGUI(MainFrame mf) {
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
}
