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

public class WriteArrayListForVisp extends ProcessingStepsPanel implements Serializable{
	JTextField tag = null;
	JRadioButton bothChannels = new JRadioButton("original Channels");
	JRadioButton demixedChannels = new JRadioButton("demixed Channels");
	JTextField width1 = new JTextField();
	JTextField width2 = new JTextField();
	JTextField middle1 = new JTextField();
	JTextField middle2 = new JTextField();
	JCheckBox vispChkbox = new JCheckBox("Visp Output");
	JCheckBox frcChkbox = new JCheckBox("FRC Output");
	JTextField[] listTextFields = {tag,width1,width2,middle2,middle2};
	private static String name = "Write Special output (Visp, FRC)";

	
	public WriteArrayListForVisp(MainFrame mf) {
		super(mf);
		String[] settings = new String[2];
		settings[0] = "";
		settings[1] = "";
		tag = new JTextField();
		this.setParameterButtonsName(name);
		this.setColor(Color.GREEN);
		this.setOptionPanel(createOptionPanel());
	}
	
	public WriteArrayListForVisp(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Tag:"));
		verticalBox.add(tag);
		Box hb2 = Box.createHorizontalBox();
		
		Box hb = Box.createHorizontalBox();
		hb.add(vispChkbox);
		vispChkbox.setSelected(true);
		hb.add(Box.createHorizontalStrut(20));
		hb.add(frcChkbox);
		verticalBox.add(hb);
		
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

		String[] tempString = new String[listTextFields.length+3];
		if (vispChkbox.isSelected()){
			tempString[0] = "selected";
		}
		else{
			tempString[0] = "notSelected";
		}
		if (frcChkbox.isSelected()){
			tempString[1] = "selected";
		}
		else{
			tempString[1] = "notSelected";
		}
		if (bothChannels.isSelected()){
			tempString[2] = "both";
		}
		else{
			tempString[2] = "demixing";
		}
		
		setTextFieldTexts(listTextFields, 3, tempString);
		return tempString;
	}
	public void setSettings(String[] tempString){
		if (tempString[0].equals("selected")){
			vispChkbox.setSelected(true);
		}
		else{
			vispChkbox.setSelected(false);
		}
		if (tempString[1].equals("selected")){
			frcChkbox.setSelected(true);
		}
		else{
			frcChkbox.setSelected(false);
		}
		if (tempString[2].equals("both")){
			bothChannels.setSelected(true);
		}
		else{
			demixedChannels.setSelected(true);
		}
		getTextFieldTexts(listTextFields, 3, tempString);
	}
	
	public ProcessingStepsPanel getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof WriteArrayListForVisp){
			WriteArrayListForVisp returnObject = new WriteArrayListForVisp(mf);
			return returnObject;
		}
		return null;
	}
	
	public ProcessingStepsPanel getFunctionOfName(String tempName, MainFrame mf){
		if (tempName.equals(name)){
			WriteArrayListForVisp returnObject = new WriteArrayListForVisp(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
		return new WriteArrayListForVisp(mf);
	}
	
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {
		if (demixedChannels.isSelected()){
			DemixingParameters demixingParams= new DemixingParameters((Double.parseDouble(middle1.getText()))/180. * Math.PI,
					(Double.parseDouble(middle2.getText()))/180.*Math.PI, Double.parseDouble(width1.getText())/180.*Math.PI, Double.parseDouble(width2.getText())/180.*Math.PI);
			if (vispChkbox.isSelected()){
				sd1.writeArrayListForVisp(demixingParams, tag.getText());
			}
			if (frcChkbox.isSelected()){
				sd1.writeArrayListForFRC(demixingParams, tag.getText());
			}
		}
		else{
			if (vispChkbox.isSelected()){
				sd1.writeArrayListForVisp(tag.getText());
				sd2.writeArrayListForVisp(tag.getText());
			}
			if (frcChkbox.isSelected()){
				sd1.writeArrayListForFRC(tag.getText());
				sd2.writeArrayListForFRC(tag.getText());
			}
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

