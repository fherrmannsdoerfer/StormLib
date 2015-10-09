package functionDefinitions;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DemixingGUI extends ProcessingStepsPanel{
	private static String name = "Demixing";
	public DemixingGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(Color.RED);
		this.setOptionPanel(createOptionPanel());
	}
	
	public DemixingGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		return retPanel;
	}
	public String[] getSettings(){
		String[] tempString = null;
		return tempString;
	}
	public void setSettings(String[] tempString){
	}
	public DemixingGUI getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof DemixingGUI){
			DemixingGUI returnObject = new DemixingGUI(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
		return new DemixingGUI(mf);
	}
	public String getFunctionName(){
		return name;
	}
}
