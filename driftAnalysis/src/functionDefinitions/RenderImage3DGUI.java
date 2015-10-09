package functionDefinitions;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class RenderImage3DGUI extends ProcessingStepsPanel{
	JTextField pixelsize = new JTextField();
	JTextField tag = new JTextField();
	private static String name = "RenderImage3D";
	public RenderImage3DGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(Color.GREEN);
		this.setOptionPanel(createOptionPanel());
	}
	
	public RenderImage3DGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Pixelsize:"));
		verticalBox.add(pixelsize);
		verticalBox.add(new JLabel("Tag:"));
		verticalBox.add(tag);
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
		String[] tempString = {pixelsize.getText(), tag.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		pixelsize.setText(tempString[0]);
		tag.setText(tempString[1]);
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
}