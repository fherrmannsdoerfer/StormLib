package functionDefinitions;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;

import javax.swing.Box;
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
	JTextField minFrame = new JTextField();
	JTextField maxFrame = new JTextField();
	String[] listLabelTexts = {"minimal x-value:", "maximal x-value:","minimal y-value:", "maximal y-value:","minimal z-value:", "maximal z-value:","minimal frame:", "maximal frame:"};
	JTextField[] listTextFields = {minX, maxX,minY,maxY,minZ,maxZ,minFrame,maxFrame};
	String[] listTextFieldTexts = {"xmin", "xmax", "ymin", "ymax", "zmin", "zmax", "framemin", "framemax"};
	private static String name = "Crop";
	
	public CropGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(Color.WHITE);
		this.setOptionPanel(createOptionPanel());
	}
	
	public CropGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		for (int i=0; i<4; i++){
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
	public Integer getFrameMin(){
		try{
			return Integer.valueOf(minFrame.getText());
		}
		catch(Exception e){
			return Integer.valueOf("0");
		}
	}
	public Integer getFrameMax(){
		try{
			return Integer.valueOf(maxFrame.getText());
		}
		catch(Exception e){
			return Integer.valueOf("10000000");
		}
	}
	@Override
	public void process(StormData sd1, StormData sd2) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public ProcessingStepsPanel getFunction(MainFrame mf) {
		// TODO Auto-generated method stub
		return null;
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

	@Override
	public ProcessingStepsPanel getProcessingStepsPanelObject(
			ProcessingStepsPanel processingStepsPanelObject, MainFrame mf) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public String getFunctionName() {
		// TODO Auto-generated method stub
		return null;
	}
}
