package functionDefinitions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;
import javax.swing.SwingConstants;

import dataStructure.DemixingParameters;
import dataStructure.StormData;
import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class RenderDemixingImage extends ProcessingStepsPanel implements Serializable{
	JTextField pixelsize = new JTextField();
	JTextField sigma = new JTextField();
	JTextField tag = new JTextField();
	JTextField percentile = new JTextField();
	JTextField width1 = new JTextField();
	JTextField width2 = new JTextField();
	JTextField middle1 = new JTextField();
	JTextField middle2 = new JTextField();
	JRadioButton photonBased = new JRadioButton("Photon based intensities");
	JRadioButton locBased = new JRadioButton("Localization count based intensities");
	JCheckBox saveStack = new JCheckBox("Save Stack");
	JTextField voxelsizeXY = new JTextField("10");
	JTextField voxelsizeZ = new JTextField("20");
	JTextField sigmaZXY = new JTextField("10");
	JTextField sigmaZZ = new JTextField("30");
	JTextField[] listTextFields = {pixelsize,tag,percentile,width1,width2,middle1,middle2,sigma};
	private static String name = "Render Demixing Image";
	public RenderDemixingImage(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getColorOutput());
		this.setOptionPanel(createOptionPanel());
	}
	
	public RenderDemixingImage(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		JLabel pixelsizeLabel = new JLabel("Pixelsize [nm]:");
		pixelsizeLabel.setAlignmentX(0);
		verticalBox.add(pixelsizeLabel);
		pixelsize.setText("10");
		verticalBox.add(pixelsize);
		verticalBox.add(new JLabel("Sigma for Gaussian Rendering [nm]:"));
		verticalBox.add(sigma);
		sigma.setText("10");
		verticalBox.add(new JLabel("Tag:"));
		verticalBox.add(tag);
		verticalBox.add(new JLabel("Percentile:"));
		percentile.setText("0.99");
		verticalBox.add(percentile);
		
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
		
		Box hb2 = Box.createHorizontalBox();
		hb2.setAlignmentX(0);
		hb1.setAlignmentX(0);
		ButtonGroup group = new ButtonGroup();
		group.add(photonBased);
		group.add(locBased);
		hb2.add(photonBased);
		hb2.add(locBased);
		photonBased.setSelected(true);
		Component hs = Box.createHorizontalStrut(20);
		hb1.add(vb1);
		hb1.add(hs);
		hb1.add(vb2);
		verticalBox.add(hb1);
		verticalBox.add(hb2);
		
		verticalBox.add(saveStack);
		verticalBox.add(new JLabel("Voxel Size XY [nm]:"));
		verticalBox.add(voxelsizeXY);
		verticalBox.add(new JLabel("Voxel Size Z[nm]:"));
		verticalBox.add(voxelsizeZ);
		verticalBox.add(new JLabel("SigmaZ XY [nm]:"));
		verticalBox.add(sigmaZXY);
		verticalBox.add(new JLabel("SigmaZ Z [nm]:"));
		verticalBox.add(sigmaZZ);;
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public double getPixelsize(){
		return Double.valueOf(pixelsize.getText());
	}
	public String getTag(){
		return tag.getText();
	}
	public String[] getSettings(){
		String[] tempString = new String[listTextFields.length+1];
		getTextFieldTexts(listTextFields, 0, tempString);
		if (photonBased.isSelected()){
			tempString[listTextFields.length] = "photons";
		}
		else{
			tempString[listTextFields.length] = "locs";
		}
		return tempString;
	}
	public void setSettings(String[] tempString){
		setTextFieldTexts(listTextFields, 0, tempString);
		if (tempString[listTextFields.length].equals("photons")){
			photonBased.setSelected(true);
		}
		else{
			locBased.setSelected(true);
		}
	}
	public RenderDemixingImage getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof RenderDemixingImage){
			RenderDemixingImage returnObject = new RenderDemixingImage(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
	return new RenderDemixingImage(mf);
	}
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {
		DemixingParameters demixingParams= new DemixingParameters((Double.parseDouble(middle1.getText()))/180. * Math.PI,
				(Double.parseDouble(middle2.getText()))/180.*Math.PI, Double.parseDouble(width1.getText())/180.*Math.PI,
				Double.parseDouble(width2.getText())/180.*Math.PI);
		if (photonBased.isSelected()){
			sd1.renderDemixingImage(Double.parseDouble(pixelsize.getText()), Double.parseDouble(percentile.getText()), demixingParams, 
					tag.getText(),0, Double.parseDouble(sigma.getText()),saveStack.isSelected(),
					Double.parseDouble(voxelsizeXY.getText()),Double.parseDouble(voxelsizeZ.getText()),Double.parseDouble(sigmaZXY.getText()),
					Double.parseDouble(sigmaZZ.getText()));
		}		
		else{
			sd1.renderDemixingImage(Double.parseDouble(pixelsize.getText()), Double.parseDouble(percentile.getText()), demixingParams, 
					tag.getText(),1, Double.parseDouble(sigma.getText()),saveStack.isSelected(),
					Double.parseDouble(voxelsizeXY.getText()),Double.parseDouble(voxelsizeZ.getText()),Double.parseDouble(sigmaZXY.getText()),
					Double.parseDouble(sigmaZZ.getText()));
		}
		
		setProgressbarValue(100);
	}
}