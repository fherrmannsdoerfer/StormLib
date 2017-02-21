package functionDefinitions;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataStructure.StormData;
import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class RenderImage3DGUI extends ProcessingStepsPanel implements Serializable{
	JTextField pixelsize = new JTextField();
	JTextField sigma = new JTextField();
	JTextField tag = new JTextField();
	JTextField percentile = new JTextField();
	private static String name = "Render Image 3D";
	public RenderImage3DGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getColorOutput());
		this.setOptionPanel(createOptionPanel());
	}
	
	public RenderImage3DGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Pixel Size [nm]:"));
		verticalBox.add(pixelsize);
		pixelsize.setText("10");
		verticalBox.add(new JLabel("Sigma for Gaussian Rendering [nm]:"));
		verticalBox.add(sigma);
		sigma.setText("10");
		verticalBox.add(new JLabel("Tag:"));
		verticalBox.add(tag);
		percentile.setText("0.999");
		verticalBox.add(new JLabel("Percentile:"));
		verticalBox.add(percentile);
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public double getPixelsize(){
		return Double.valueOf(pixelsize.getText());
	}
	public String getTag(){
		return tag.getText();
	}
	public double getPercentile(){
		return Double.valueOf(percentile.getText());
	}
	public double getSigma(){
		return Double.valueOf(sigma.getText());
	}
	public String[] getSettings(){
		String[] tempString = {pixelsize.getText(), tag.getText(), percentile.getText(),sigma.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		pixelsize.setText(tempString[0]);
		tag.setText(tempString[1]);
		percentile.setText(tempString[2]);
		sigma.setText(tempString[3]);
	}
	public RenderImage3DGUI getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof RenderImage3DGUI){
			RenderImage3DGUI returnObject = new RenderImage3DGUI(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
	return new RenderImage3DGUI(mf);
	}
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {
		sd1.renderImage3D(getPixelsize(), getTag(),getSigma(),getPercentile());
		setProgressbarValue(100);		
	}
}