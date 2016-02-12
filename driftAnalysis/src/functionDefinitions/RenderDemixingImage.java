package functionDefinitions;

import java.awt.Color;
import java.awt.Component;
import java.io.Serializable;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

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
		verticalBox.add(new JLabel("Pixelsize:"));
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
		Component hs = Box.createHorizontalStrut(20);
		hb1.add(vb1);
		hb1.add(hs);
		hb1.add(vb2);
		verticalBox.add(hb1);
		
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
		String[] tempString = new String[listTextFields.length];
		getTextFieldTexts(listTextFields, 0, tempString);
		return tempString;
	}
	public void setSettings(String[] tempString){
		setTextFieldTexts(listTextFields, 0, tempString);
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
				(Double.parseDouble(middle2.getText()))/180.*Math.PI, Double.parseDouble(width1.getText())/180.*Math.PI, Double.parseDouble(width2.getText())/180.*Math.PI);
		sd1.renderDemixingImage(Double.parseDouble(pixelsize.getText()), demixingParams, tag.getText());
		setProgressbarValue(100);
	}
}