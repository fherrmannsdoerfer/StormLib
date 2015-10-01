package functionDefinitions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

import javax.swing.JButton;

public class DualChannelSingleFileInputGUI extends ProcessingStepsPanel{
	JTextField path1 = new JTextField();
	JTextField file1 = new JTextField();
	JTextField path2 = new JTextField();
	JTextField file2 = new JTextField();
	private final Box horizontalBox = Box.createHorizontalBox();
	private final Box verticalBox_1 = Box.createVerticalBox();
	private final JButton loadFile1Button = new JButton("Load File1");
	private final JButton loadFile2Button = new JButton("Load File2");
	final JFileChooser dualChannel1FileChooser = new JFileChooser();
	final JFileChooser dualChannel2FileChooser = new JFileChooser();
	
	
	public DualChannelSingleFileInputGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName("Dual Color Singe File Input");
		this.setColor(Color.WHITE);
		this.setOptionPanel(createOptionPanel());
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Dimension d = new Dimension(100,22);
		
		retPanel.add(horizontalBox);
		Box verticalBox = Box.createVerticalBox();
		horizontalBox.add(verticalBox);
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
		
		horizontalBox.add(verticalBox_1);		
		verticalBox_1.add(loadFile1Button);
		verticalBox_1.add(loadFile2Button);
		
		loadFile1Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				int returnVal = dualChannel1FileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File file = dualChannel1FileChooser.getSelectedFile();
					path1.setText(file.getParent()+"\\");
					file1.setText(file.getName());
				}
			}
		});
		
		loadFile2Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				int returnVal = dualChannel2FileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File file = dualChannel2FileChooser.getSelectedFile();
					path2.setText(file.getParent()+"\\");
					file2.setText(file.getName());
				}
			}
		});
		
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
	public String[] getSettings(){
		String[] tempString = {path1.getText(), file1.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		path1.setText(tempString[0]);
		file1.setText(tempString[1]);
	}
}