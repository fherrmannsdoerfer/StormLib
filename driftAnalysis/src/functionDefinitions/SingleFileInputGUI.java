package functionDefinitions;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.Serializable;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

import javax.swing.JButton;

public class SingleFileInputGUI extends ProcessingStepsPanel{
	JTextField path = new JTextField();
	JTextField foldername = new JTextField();
	final JFileChooser singleFileChooser = new JFileChooser();
	JButton loadFileButton;
	
	public SingleFileInputGUI(MainFrame mf) {
		super(mf);
		String[] settings = new String[2];
		settings[0] = "C:\\Uni\\STORM-Test-Data\\";
		settings[1] = "Daten.txt";
		path.setText(settings[0]);
		foldername.setText(settings[1]);
		setSettings(settings);
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

		loadFileButton = new JButton("Load File...");
		retPanel.add(loadFileButton);
		loadFileButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				int returnVal = singleFileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File file = singleFileChooser.getSelectedFile();
					path.setText(file.getParent()+"\\");
					foldername.setText(file.getName());
				}
			}
		});
		
		return retPanel;
	}
	
	public String getPath(){
		return path.getText();
	}
	public String getFoldername(){
		return foldername.getText();
	}
	public String[] getSettings(){
		String[] tempString = {path.getText(), foldername.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		path.setText(tempString[0]);
		foldername.setText(tempString[1]);
	}
	public ProcessingStepsPanel getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof SingleFileInputGUI){
			SingleFileInputGUI returnObject = new SingleFileInputGUI(mf);
			return returnObject;
		}
		return null;
	}
}
