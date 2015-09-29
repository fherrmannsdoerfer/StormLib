package functionDefinitions;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class SingleFileInputGUI extends ProcessingStepsPanel{
	JTextField path = new JTextField();
	JTextField foldername = new JTextField();
	public SingleFileInputGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName("Single File Input");
		this.setColor(Color.WHITE);
		this.setOptionPanel(createOptionPanel());
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Path:"));
		verticalBox.add(path);
		verticalBox.add(new JLabel("Foldername:"));
		verticalBox.add(foldername);
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public String getPath(){
		return path.getText();
	}
	public String getFoldername(){
		return foldername.getText();
	}
}
