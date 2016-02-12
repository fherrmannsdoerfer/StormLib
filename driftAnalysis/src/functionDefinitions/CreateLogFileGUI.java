package functionDefinitions;

import java.awt.Color;
import java.awt.Component;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.Serializable;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import dataStructure.DemixingParameters;
import dataStructure.StormData;
import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class CreateLogFileGUI extends ProcessingStepsPanel implements Serializable{
	
	private static String name = "Create Log File";
	JCheckBox ch1Chkbox = new JCheckBox();
	JCheckBox ch2Chkbox = new JCheckBox();
	
	public CreateLogFileGUI(MainFrame mf) {
		super(mf);
		String[] settings = new String[2];
		settings[0] = "";
		settings[1] = "";
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getColorOutput());
		this.setOptionPanel(createOptionPanel());
	}
	
	public CreateLogFileGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		Box hb = Box.createHorizontalBox();
		hb.add(ch1Chkbox);
		hb.add(Box.createHorizontalStrut(20));
		hb.add(ch2Chkbox);
		ch1Chkbox.setSelected(true);
		return retPanel;
	}
	

//	public String[] getSettings(){
//		String[] tempString = {tag.getText()};
//		return tempString;
//	}
//	public void setSettings(String[] settings){
//		tag.setText(settings[0]);
//	}
	
	public ProcessingStepsPanel getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof CreateLogFileGUI){
			CreateLogFileGUI returnObject = new CreateLogFileGUI(mf);
			return returnObject;
		}
		return null;
	}
	
	public ProcessingStepsPanel getFunctionOfName(String tempName, MainFrame mf){
		if (tempName.equals(name)){
			CreateLogFileGUI returnObject = new CreateLogFileGUI(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
		return new CreateLogFileGUI(mf);
	}
	
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {
		if (ch1Chkbox.isSelected()){
			sd1.createPdf();
		}
		if (ch2Chkbox.isSelected()){
			sd2.createPdf();
		}
		setProgressbarValue(100);
	}

	@Override
	public String[] getSettings() {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public void setSettings(String[] tempString) {
		// TODO Auto-generated method stub
		
	}
	
	
}

