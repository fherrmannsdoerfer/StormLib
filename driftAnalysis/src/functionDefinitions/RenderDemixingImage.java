package functionDefinitions;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;

import javax.swing.Box;
import javax.swing.ButtonGroup;
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
	JTextField tag = new JTextField();
	JTextField width1 = new JTextField();
	JTextField width2 = new JTextField();
	JTextField middle1 = new JTextField();
	JTextField middle2 = new JTextField();
	JRadioButton photonBased = new JRadioButton("Photon based intensities");
	JRadioButton locBased = new JRadioButton("Localization count based intensities");
	JTextField[] listTextFields = {pixelsize,tag,width1,width2,middle1,middle2};
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
		JLabel pixelsizeLabel = new JLabel("Pixelsize:");
		pixelsizeLabel.setAlignmentX(0);
		verticalBox.add(pixelsizeLabel);
		pixelsize.setText("10");
		verticalBox.add(pixelsize);
		verticalBox.add(new JLabel("Tag:"));
		verticalBox.add(tag);
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
			sd1.renderDemixingImage(Double.parseDouble(pixelsize.getText()), demixingParams, tag.getText(),0);
		}
		else{
			sd1.renderDemixingImage(Double.parseDouble(pixelsize.getText()), demixingParams, tag.getText(),1);
		}
		setProgressbarValue(100);
	}
}