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
	public RenderImage3DGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName("Render 3D Image");
		this.setColor(Color.GREEN);
		this.setOptionPanel(createOptionPanel());
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
		return Double.valueOf(pixelsize.getText());
	}
	public String getTag(){
		return tag.getText();
	}

}