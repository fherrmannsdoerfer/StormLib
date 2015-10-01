package functionDefinitions;

import java.awt.Color;
import java.io.Serializable;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class RenderImage2DGUI extends ProcessingStepsPanel implements Serializable{
	JTextField pixelsize = null; 
	JTextField tag = null;

	
	public RenderImage2DGUI(MainFrame mf) {
		super(mf);
		String[] settings = new String[2];
		settings[0] = "";
		settings[1] = "";
//		pixelsize  = new JTextField();
//		tag = new JTextField();
		setSettings(settings);
//		this.setParameterButtonsName("Render 2D Image");
//		this.setColor(Color.GREEN);
//		this.setOptionPanel(createOptionPanel());
	}
	
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
		String[] tempString = {pixelsize.getText(), tag.getText()};
		return tempString;
	}
	public void setSettings(String[] settings){
		pixelsize  = new JTextField(settings[0]);
		tag = new JTextField(settings[1]);
//		pixelsize.setText(tempString[0]);
//		tag.setText(tempString[1]);
		this.setParameterButtonsName("Render 2D Image");
		this.setColor(Color.GREEN);
		this.setOptionPanel(createOptionPanel());
	}
}
