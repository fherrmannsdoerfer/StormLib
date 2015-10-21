package functionDefinitions;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class DualChannelSingleFileInputGUI extends ProcessingStepsPanel{
	JTextField path1 = new JTextField();
	JTextField file1 = new JTextField();
	JTextField path2 = new JTextField();
	JTextField file2 = new JTextField();
	
	public DualChannelSingleFileInputGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName("Dual Color Singe File Input");
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
		file1.setPreferredSize(d);
		file1.setPreferredSize(d);
		
		verticalBox.add(new JLabel("Path 1:"));
		verticalBox.add(path1);
		verticalBox.add(new JLabel("File 1:"));
		verticalBox.add(file1);
		verticalBox.add(new JLabel("Path 2:"));
		verticalBox.add(path2);
		verticalBox.add(new JLabel("File 2:"));
		verticalBox.add(file2);
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public String getPath1(){
		return path1.getText();
	}
	public String getFile1(){
		return file1.getText();
	}
	public String getPath2(){
		return path2.getText();
	}
	public String getFile2(){
		return file2.getText();
	}
}