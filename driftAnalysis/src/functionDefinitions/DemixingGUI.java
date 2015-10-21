package functionDefinitions;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

import java.awt.Color;

import javax.swing.Box;
import javax.swing.JLabel;
import javax.swing.JPanel;

public class DemixingGUI extends ProcessingStepsPanel{
	public DemixingGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName("Demixing");
		this.setColor(Color.RED);
		this.setOptionPanel(createOptionPanel());
	}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		return retPanel;
	}

}
