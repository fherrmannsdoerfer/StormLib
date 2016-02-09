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

public class WriteLocalizationsToFile extends ProcessingStepsPanel implements Serializable{
	JTextField tag = null;
	JRadioButton bothChannels = new JRadioButton("original Channels");
	JRadioButton demixedChannels = new JRadioButton("demixed Channels");

	JTextField width1 = new JTextField();
	JTextField width2 = new JTextField();
	JTextField middle1 = new JTextField();
	JTextField middle2 = new JTextField();
	JCheckBox ch1Chkbox = new JCheckBox();
	JCheckBox ch2Chkbox = new JCheckBox();
	JTextField[] listTextFields = {tag,width1,width2,middle1,middle2};
	private static String name = "Write Localizations to File";

	
	public WriteLocalizationsToFile(MainFrame mf) {
		super(mf);
		String[] settings = new String[2];
		settings[0] = "";
		settings[1] = "";
		tag = new JTextField();
		this.setParameterButtonsName(name);
		this.setColor(Color.GREEN);
		this.setOptionPanel(createOptionPanel());
	}
	
	public WriteLocalizationsToFile(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Tag:"));
		verticalBox.add(tag);
		Box hb2 = Box.createHorizontalBox();
		ButtonGroup group = new ButtonGroup();
		group.add(bothChannels);
		group.add(demixedChannels);

		hb2.add(bothChannels);
		hb2.add(demixedChannels);

		
		demixedChannels.addActionListener(new SwitchDemixingParameters());
		bothChannels.addActionListener(new SwitchDemixingParameters());

		verticalBox.add(hb2);
		
		Box hb1 = Box.createHorizontalBox();
		Box vb1 = Box.createVerticalBox();
		Box vb2 = Box.createVerticalBox();
		vb1.add(new JLabel("Mean Angle Specimen 1:"));
		vb1.add(middle1);
		middle1.setText("40");
		vb1.add(new JLabel("Width Specimen 1:"));
		vb1.add(width1);
		width1.setText("20");
		vb2.add(new JLabel("Mean Angle Specimen 2:"));
		vb2.add(middle2);
		middle2.setText("67");
		vb2.add(new JLabel("Width Specimen 2:"));
		vb2.add(width2);
		width2.setText("20");
		Component hs = Box.createHorizontalStrut(20);
		hb1.add(vb1);
		hb1.add(hs);
		hb1.add(vb2);
		verticalBox.add(hb1);
		demixedChannels.setSelected(true);
		retPanel.add(verticalBox);
		return retPanel;
	}
	

	public String getTag(){
		return tag.getText();
	}
//	public void setRenderImage2DGUI(String pixelsizeText, String tagText) {
//		pixelsize.setText(pixelsizeText);
//		tag.setText(tagText);
//		repaint();
//	}
	public String[] getSettings(){

		String[] tempString = new String[listTextFields.length+1];
		
		if (bothChannels.isSelected()){
			tempString[0] = "both";
		}
		else{
			tempString[0] = "demixing";
		}
		
		setTextFieldTexts(listTextFields, 1, tempString);
		return tempString;
	}
	public void setSettings(String[] tempString){
		if (tempString[0].equals("both")){
			bothChannels.setSelected(true);
		}
		else{
			demixedChannels.setSelected(true);
		}
		getTextFieldTexts(listTextFields, 1, tempString);
	}
	
	public ProcessingStepsPanel getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof WriteLocalizationsToFile){
			WriteLocalizationsToFile returnObject = new WriteLocalizationsToFile(mf);
			return returnObject;
		}
		return null;
	}
	
	public ProcessingStepsPanel getFunctionOfName(String tempName, MainFrame mf){
		if (tempName.equals(name)){
			WriteLocalizationsToFile returnObject = new WriteLocalizationsToFile(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
		return new WriteLocalizationsToFile(mf);
	}
	
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {
		if (bothChannels.isSelected()){
			sd1.writeLocs(tag.getText());
			sd2.writeLocs(tag.getText());
		}
		else if (demixedChannels.isSelected()){
			DemixingParameters demixingParams= new DemixingParameters((Double.parseDouble(middle1.getText()))/180. * Math.PI,
					(Double.parseDouble(middle2.getText()))/180.*Math.PI, Double.parseDouble(width1.getText())/180.*Math.PI, Double.parseDouble(width2.getText())/180.*Math.PI);
			sd1.writeLocs(demixingParams,tag.getText());
		}
		setProgressbarValue(100);
	}
	
	class SwitchDemixingParameters implements ActionListener{

		@Override
		public void actionPerformed(ActionEvent e) {
			if (demixedChannels.isSelected()){
				middle1.setEnabled(true);
				middle2.setEnabled(true);
				width1.setEnabled(true);
				width2.setEnabled(true);
			}
			else{
				middle1.setEnabled(false);
				middle2.setEnabled(false);
				width1.setEnabled(false);
				width2.setEnabled(false);
			}
		}
		
	}
}

