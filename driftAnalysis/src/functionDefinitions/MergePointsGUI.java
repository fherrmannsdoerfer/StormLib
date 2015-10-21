package functionDefinitions;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class MergePointsGUI extends ProcessingStepsPanel{
	JTextField distx = new JTextField();
	JTextField disty = new JTextField();
	JTextField distz = new JTextField();
	JTextField distframe = new JTextField();
	public MergePointsGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName("Merge Consecutive Localizations");
		this.setColor(Color.RED);
		this.setOptionPanel(createOptionPanel());
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Maximal Tolerated Distance in X:"));
		distx.setText("100");
		verticalBox.add(distx);
		verticalBox.add(new JLabel("Maximal Tolerated Distance in Y:"));
		disty.setText("100");
		verticalBox.add(disty);
		verticalBox.add(new JLabel("Maximal Tolerated Distance in Z:"));
		distz.setText("200");
		verticalBox.add(distz);
		verticalBox.add(new JLabel("Maximal Tolerated Distance in Frames:"));
		distframe.setText("3");
		verticalBox.add(distframe);
		
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public int getDistX(){
		try{
			return Integer.valueOf(distx.getText());
		}
		catch(Exception e){
			return Integer.valueOf("100");
		}
	}
	public int getDistY(){
		try{
			return Integer.valueOf(distx.getText());
		}
		catch(Exception e){
			return Integer.valueOf("100");
		}
	}
	public int getDistZ(){
		try{
			return Integer.valueOf(distx.getText());
		}
		catch(Exception e){
			return Integer.valueOf("200");
		}
	}
	public int getDistFrames(){
		try{
			return Integer.valueOf(distx.getText());
		}
		catch(Exception e){
			return Integer.valueOf("3");
		}
		
	}
}
