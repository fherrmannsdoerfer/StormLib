package functionDefinitions;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JRadioButton;
import javax.swing.JTextField;

import dataStructure.StormData;
import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class RenderImage2DGUI extends ProcessingStepsPanel implements Serializable{
	JTextField pixelsize = null; 
	JTextField tag = null;
	JRadioButton photonBased = new JRadioButton("Photon based intensities");
	JRadioButton locBased = new JRadioButton("Localization count based intensities");
	private static String name = "Render 2D Image";

	
	public RenderImage2DGUI(MainFrame mf) {
		super(mf);
		String[] settings = new String[2];
		settings[0] = "";
		settings[1] = "";
		pixelsize  = new JTextField();
		tag = new JTextField();
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getColorOutput());
		this.setOptionPanel(createOptionPanel());
	}
	
	public RenderImage2DGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Pixelsize:"));
		verticalBox.add(pixelsize);
		pixelsize.setText("10");
		verticalBox.add(new JLabel("Tag:"));
		verticalBox.add(tag);
		Box hb2 = Box.createHorizontalBox();
		ButtonGroup group = new ButtonGroup();
		group.add(photonBased);
		group.add(locBased);
		hb2.add(photonBased);
		hb2.add(locBased);
		photonBased.setSelected(true);
		verticalBox.add(hb2);
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public double getPixelsize(){
		try{
			return Double.valueOf(pixelsize.getText());
		}
		catch(Exception e){
			return Double.valueOf("10");
		}
		
	}
	public String getTag(){
		return tag.getText();
	}
	public void setRenderImage2DGUI(String pixelsizeText, String tagText) {
		pixelsize.setText(pixelsizeText);
		tag.setText(tagText);
		repaint();
	}
	public String[] getSettings(){
		String[] tempString = new String[3];
		tempString[0] = pixelsize.getText();
		tempString[1] = tag.getText();
		if (photonBased.isSelected()){
			tempString[2] = "photons";
		}
		else{
			tempString[2] = "locCount";
		}
		
		return tempString;
	}
	public void setSettings(String[] settings){
		pixelsize.setText(settings[0]);
		tag.setText(settings[1]);
		if (settings[2].equals("photons")){
			photonBased.setSelected(true);
		}
		else{
			locBased.setSelected(true);
		}
	}
	
	public ProcessingStepsPanel getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof RenderImage2DGUI){
			RenderImage2DGUI returnObject = new RenderImage2DGUI(mf);
			return returnObject;
		}
		return null;
	}
	
	public ProcessingStepsPanel getFunctionOfName(String tempName, MainFrame mf){
		if (tempName.equals(name)){
			RenderImage2DGUI returnObject = new RenderImage2DGUI(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
		return new RenderImage2DGUI(mf);
	}
	
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {
		if (photonBased.isSelected()){
			sd1.renderImage2D(getPixelsize(), getTag(),0);
		}
		else{
			sd1.renderImage2D(getPixelsize(), getTag(),1);
		}
		
		setProgressbarValue(100);
	}
}
