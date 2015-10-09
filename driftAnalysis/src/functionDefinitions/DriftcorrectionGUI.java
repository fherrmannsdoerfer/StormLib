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
	private static String name = "Driftcorrection";
	public DriftcorrectionGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(Color.RED);
		this.setOptionPanel(createOptionPanel());
	}
	
	public DriftcorrectionGUI(){}
	
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
	public DriftcorrectionGUI getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof DriftcorrectionGUI){
			DriftcorrectionGUI returnObject = new DriftcorrectionGUI(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
		return new DriftcorrectionGUI(mf);
	}
	public String getFunctionName(){
		return name;
	}
}
