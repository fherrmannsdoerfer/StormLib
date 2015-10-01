package functionDefinitions;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class DriftcorrectionGUI extends ProcessingStepsPanel{
	JTextField chunksize = new JTextField();
	public DriftcorrectionGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName("Drift Correction");
		this.setColor(Color.RED);
		this.setOptionPanel(createOptionPanel());
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(new JLabel("Chunk Size:"));
		verticalBox.add(chunksize);
		
		retPanel.add(verticalBox);
		return retPanel;
	}
	
	public int getChunksize(){
		try{
			return Integer.valueOf(chunksize.getText());
		}
		catch(Exception e){
			return Integer.valueOf("3000");
		}
	
	}
	public String[] getSettings(){
		String[] tempString = {chunksize.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		chunksize.setText(tempString[0]);
	}
}
