package functionDefinitions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataStructure.StormData;
import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class ScalingGUI extends ProcessingStepsPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField scaleX = new JTextField("1");
	JTextField scaleY = new JTextField("0.925");
	JTextField scaleZ = new JTextField("1");
	JCheckBox ch1Chkbox = new JCheckBox();
	JCheckBox ch2Chkbox = new JCheckBox();
	
	private static String name = "Scaling";
	
	public ScalingGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getColorProcessing());
		this.setOptionPanel(createOptionPanel());
	}
	
	public ScalingGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		Component hs = Box.createHorizontalStrut(20);
		Component hs2 = Box.createHorizontalStrut(20);
		Box verticalBox = Box.createVerticalBox();
		Box hb2 = Box.createHorizontalBox();
		Box vb2 = Box.createVerticalBox();
		vb2.add(new JLabel("Scale x:"));
		vb2.add(scaleX);
		
		Box vb3 = Box.createVerticalBox();
		vb3.add(new JLabel("Scale y:"));
		vb3.add(scaleY);
		
		Box vb4 = Box.createVerticalBox();
		vb4.add(new JLabel("Scale z:"));
		vb4.add(scaleZ);
		hb2.add(vb2);
		hb2.add(hs);
		hb2.add(vb3);
		hb2.add(hs2);
		hb2.add(vb4);
		verticalBox.add(hb2);
		
		ch1Chkbox.setText("Crop Channel 1");
		ch1Chkbox.setSelected(true);
		ch2Chkbox.setText("Crop Channel 2");
		ch2Chkbox.setSelected(false);
		Box hb = Box.createHorizontalBox();
		hb.add(ch1Chkbox);
		hb.add(Box.createHorizontalStrut(20));
		hb.add(ch2Chkbox);
		verticalBox.add(hb);
		retPanel.add(verticalBox);
		
		return retPanel;
	}
	
	private double getScaleX(){
		return Double.valueOf(scaleX.getText());
	}
	
	private double getScaleY(){
		return Double.valueOf(scaleY.getText());
	}
	private double getScaleZ(){
		return Double.valueOf(scaleZ.getText());
	}
	
	@Override
	public void process(StormData sd1, StormData sd2) {
		if (ch1Chkbox.isSelected()){
			sd1.scaleCoords(getScaleX(), getScaleY(), getScaleZ());
		}
		if (ch2Chkbox.isSelected()){
			sd2.scaleCoords(getScaleX(), getScaleY(), getScaleZ());
		}
		setProgressbarValue(100);
	}

	@Override
	public ProcessingStepsPanel getFunction(MainFrame mf) {
		return new ScalingGUI(mf);
	}

	public String[] getSettings(){
		String statusChkBox = "";
		String statusChkBox2 = "";
		if (ch1Chkbox.isSelected()){
			statusChkBox = "selected";
		}
		else{
			statusChkBox = "notSelected";
		}
		if (ch2Chkbox.isSelected()){
			statusChkBox2 = "selected";
		}
		else{
			statusChkBox2 = "notSelected";
		}
		String[] tempString = new String[5];
		tempString[0] = statusChkBox;
		tempString[1] = statusChkBox2;
		tempString[2] = scaleX.getText();
		tempString[3] = scaleY.getText();
		tempString[4] = scaleZ.getText();
		return tempString;
	}
	public void setSettings(String[] tempString){
		if (tempString[0].equals("selected")){
			ch1Chkbox.setSelected(true);
		}
		else{
			ch1Chkbox.setSelected(false);
		}
		if (tempString[1].equals("selected")){
			ch2Chkbox.setSelected(true);
		}
		else{
			ch2Chkbox.setSelected(false);
		}
		scaleX.setText(tempString[2]);
		scaleY.setText(tempString[3]);
		scaleZ.setText(tempString[4]);
	}

	@Override
	public ProcessingStepsPanel getProcessingStepsPanelObject(
			ProcessingStepsPanel processingStepsPanelObject, MainFrame mf) {
		if (processingStepsPanelObject instanceof ScalingGUI){
			ScalingGUI returnObject = new ScalingGUI(mf);
			return returnObject;
		}
		return null;
	}

	@Override
	public String getFunctionName() {
		return name;
	}
}
