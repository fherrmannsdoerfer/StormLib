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

public class CropGUI extends ProcessingStepsPanel{
	JTextField minX = new JTextField();
	JTextField maxX = new JTextField();
	JTextField minY = new JTextField();
	JTextField maxY = new JTextField();
	JTextField minZ = new JTextField();
	JTextField maxZ = new JTextField();
	JTextField minInt = new JTextField();
	JTextField maxInt = new JTextField();
	JTextField minFrame = new JTextField();
	JTextField maxFrame = new JTextField();
	JCheckBox ch1Chkbox = new JCheckBox();
	JCheckBox ch2Chkbox = new JCheckBox();
	String[] listLabelTexts = {"minimal x-value [nm]:", "maximal x-value [nm]:","minimal y-value [nm]:", "maximal y-value [nm]:","minimal z-value [nm]:", "maximal z-value [nm]:","minimal frame:", "maximal frame:", "minimal intensity:", "maximal intensity"};
	JTextField[] listTextFields = {minX, maxX,minY,maxY,minZ,maxZ,minFrame,maxFrame,minInt, maxInt};
	String[] listTextFieldTexts = {"xmin", "xmax", "ymin", "ymax", "zmin", "zmax", "framemin", "framemax", "intmin", "intmax"};
	private static String name = "Crop";
	
	public CropGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getColorProcessing());
		this.setOptionPanel(createOptionPanel());
	}
	
	public CropGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		
		Box verticalBox = Box.createVerticalBox();
		for (int i=0; i<5; i++){
			Component vs = Box.createVerticalStrut(20);
			Component hs = Box.createHorizontalStrut(20);
			Box hb1 = Box.createHorizontalBox();
			Box vb1 = Box.createVerticalBox();
			vb1.add(new JLabel(listLabelTexts[2*i]));
			vb1.add(listTextFields[2*i]);
			listTextFields[2*i].setText(listTextFieldTexts[2*i]);
			hb1.add(vb1);
			hb1.add(hs);
			Box vb2 = Box.createVerticalBox();
			vb2.add(new JLabel(listLabelTexts[2*i+1]));
			vb2.add(listTextFields[2*i+1]);
			listTextFields[2*i+1].setText(listTextFieldTexts[2*i+1]);
			hb1.add(vb2);
			verticalBox.add(hb1);
			verticalBox.add(vs);
		}
		for (JTextField tf :listTextFields){
			tf.setMaximumSize(new Dimension(150,22));
		}
		
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
	
	public Double getMinX(){
	try{
			return Double.valueOf(minX.getText());
		}
		catch(Exception e){
			return Double.valueOf("-1e20");
		}
	}
	public Double getMaxX(){
		try{
			return Double.valueOf(maxX.getText());
		}
		catch(Exception e){
			return Double.valueOf("1e20");
		}
	}
	
	public Double getMinY(){
		try{
			return Double.valueOf(minY.getText());
		}
		catch(Exception e){
			return Double.valueOf("-1e20");
		}
	}
	
	public Double getMaxY(){
		try{
			return Double.valueOf(maxY.getText());
		}
		catch(Exception e){
			return Double.valueOf("1e20");
		}
	}
	public Double getMinZ(){
		try{
			return Double.valueOf(minZ.getText());
		}
		catch(Exception e){
			return Double.valueOf("-1e20");
		}
	}
	public Double getMaxZ(){
		try{
			return Double.valueOf(maxZ.getText());
		}
		catch(Exception e){
			return Double.valueOf("1e20");
		}
	}
	public Double getMinInt(){
		try{
			return Double.valueOf(minInt.getText());
		}
		catch(Exception e){
			return Double.valueOf("0");
		}
	}
	public Double getMaxInt(){
		try{
			return Double.valueOf(maxInt.getText());
		}
		catch(Exception e){
			return Double.valueOf("1e20");
		}
	}
	public Integer getMinFrame(){
		try{
			return Integer.valueOf(minFrame.getText());
		}
		catch(Exception e){
			return Integer.valueOf("0");
		}
	}
	public Integer getMaxFrame(){
		try{
			return Integer.valueOf(maxFrame.getText());
		}
		catch(Exception e){
			return Integer.valueOf("10000000");
		}
	}
	@Override
	public void process(StormData sd1, StormData sd2) {
		if (ch1Chkbox.isSelected()){
			sd1.cropCoords(getMinX(), getMaxX(), getMinY(), getMaxY(), getMinZ(), getMaxZ(), getMinFrame(), getMaxFrame(), getMinInt(), getMaxInt());
		}
		if (ch2Chkbox.isSelected()){
			sd2.cropCoords(getMinX(), getMaxX(), getMinY(), getMaxY(), getMinZ(), getMaxZ(), getMinFrame(), getMaxFrame(), getMinInt(), getMaxInt());
		}
		setProgressbarValue(100);
	}

	@Override
	public ProcessingStepsPanel getFunction(MainFrame mf) {
		return new CropGUI(mf);
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
		String[] tempString = new String[listTextFields.length+2];
		tempString[0] = statusChkBox;
		tempString[1] = statusChkBox2;
		getTextFieldTexts(listTextFields, 2, tempString);
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
		setTextFieldTexts(listTextFields, 2, tempString);
	}

	@Override
	public ProcessingStepsPanel getProcessingStepsPanelObject(
			ProcessingStepsPanel processingStepsPanelObject, MainFrame mf) {
		if (processingStepsPanelObject instanceof CropGUI){
			CropGUI returnObject = new CropGUI(mf);
			return returnObject;
		}
		return null;
	}

	@Override
	public String getFunctionName() {
		return name;
	}
}
