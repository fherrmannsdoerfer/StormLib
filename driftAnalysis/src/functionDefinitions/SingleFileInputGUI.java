package functionDefinitions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;
import java.nio.file.Paths;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

import javax.swing.JButton;

import dataStructure.StormData;

public class SingleFileInputGUI extends ProcessingStepsPanel{
	JTextField path = new JTextField();
	JTextField filename = new JTextField();
	final JFileChooser singleFileChooser = new JFileChooser();
	JButton loadFileButton;
	private static String name ="SingleFileInput";
	
	public SingleFileInputGUI(MainFrame mf) {
		super(mf);
		String[] settings = new String[2];
		settings[0] = "C:\\Uni\\STORM-Test-Data\\";
		settings[1] = "Daten.txt";
		path.setText(settings[0]);
		filename.setText(settings[1]);
		setSettings(settings);
		this.setParameterButtonsName(name);
		this.setColor(Color.WHITE);
		this.setOptionPanel(createOptionPanel());
	}
	
	public SingleFileInputGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Path:"));
		verticalBox.add(path);
		verticalBox.add(new JLabel("Foldername:"));
		verticalBox.add(filename);
		retPanel.add(verticalBox);

		loadFileButton = new JButton("Load File...");
		retPanel.add(loadFileButton);
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
	public String[] getSettings(){
		String[] tempString = {path.getText(), filename.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		path.setText(tempString[0]);
		filename.setText(tempString[1]);
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
		sd1.setLocs(sd1.importData(getPath()+getFilename()));
		setProgressbarValue(100);
	}
}
