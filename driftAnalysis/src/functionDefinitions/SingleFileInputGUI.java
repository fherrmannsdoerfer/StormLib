package functionDefinitions;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;

import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

import javax.swing.JButton;

import dataStructure.StormData;

public class SingleFileInputGUI extends ProcessingStepsPanel{
	JTextField path = new JTextField("");
	JTextField filename = new JTextField("");
	JTextField basename = new JTextField("");
	final JFileChooser singleFileChooser = new JFileChooser();
	JButton loadFileButton;
	private static String name ="Single File Input";
	
	public SingleFileInputGUI(MainFrame mf) {
		super(mf);
		path.setMinimumSize(mf.style.getDimensionPathFields());
		filename.setPreferredSize(mf.style.getDimensionPathFields());
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getColorInput());
		this.setOptionPanel(createOptionPanel());
	}
	
	public SingleFileInputGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		Box verticalBox = Box.createVerticalBox();
		//verticalBox.setLayout(new BoxLayout(verticalBox,BoxLayout.Y_AXIS));	
		path.setAlignmentX(0);
		JLabel lab1 =new JLabel("Path:");
		lab1.setAlignmentX(0);
		verticalBox.add(lab1);
		verticalBox.add(path);
		JLabel lab2 = new JLabel("Filename:");
		lab2.setAlignmentX(0);
		verticalBox.add(lab2);
		filename.setAlignmentX(0);
		verticalBox.add(filename);
		verticalBox.add(new JLabel("Basename:"));
		verticalBox.add(basename);
		basename.setAlignmentX(0);
		
		
		retPanel.add(verticalBox);

		loadFileButton = new JButton("Load File...");
		verticalBox.add(loadFileButton);
		loadFileButton.setAlignmentX(0);
		loadFileButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				int returnVal = singleFileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File file = singleFileChooser.getSelectedFile();
					path.setText(file.getParent()+"\\");
					filename.setText(file.getName());
				}
			}
		});
		
		return retPanel;
	}
	
	public String getPath(){
		return path.getText();
	}
	public String getFilename(){
		return filename.getText();
	}
	public String getBasename(){
		return basename.getText();
	}
	public String[] getSettings(){
		String[] tempString = {path.getText(), filename.getText(), basename.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		path.setText(tempString[0]);
		filename.setText(tempString[1]);
		basename.setText(tempString[2]);
	}
	public ProcessingStepsPanel getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof SingleFileInputGUI){
			return new SingleFileInputGUI(mf);
		}
		return null;
	}
	public ProcessingStepsPanel getFunctionOfName(String tempName, MainFrame mf){
		if (tempName.equals(name)){
			return new SingleFileInputGUI(mf);
		}
		return null;
	}	
	
	public ProcessingStepsPanel getFunction(MainFrame mf){
			return new SingleFileInputGUI(mf);
	}
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {
		sd1.setFname(getFilename());
		sd1.setPath(getPath());
		sd1.setBasename(getBasename());
		sd1.setLocs(sd1.importData(getPath()+getFilename()));
		//sd1.copyAttributes(new StormData(getPath(), getFilename()));
		setProgressbarValue(100);
	}
}
