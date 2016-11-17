package functionDefinitions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeEvent;
import java.io.File;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import StormLib.Utilities;
import dataStructure.StormData;
import gui.MainFrame;
import gui.ProcessingStepsPanel;

public class DualColorMultipleFileInputGUI extends ProcessingStepsPanel{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	JTextField path1 = new JTextField();
	JTextField pattern1 = new JTextField();
	JTextField path2 = new JTextField();
	JTextField pattern2 = new JTextField();
	JTextField basename1 = new JTextField();
	JTextField basename2 = new JTextField();
	private final JButton loadPath1Button = new JButton("Load File 1");
	private final JButton loadPath2Button = new JButton("Load File 2");
	final JFileChooser dualChannel1FileChooser = new JFileChooser();
	final JFileChooser dualChannel2FileChooser = new JFileChooser();
	private static String name = "Dual-Color Multiple File Input";
	
	public DualColorMultipleFileInputGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getColorInput());
		this.setOptionPanel(createOptionPanel());
	}
	
	public DualColorMultipleFileInputGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		Dimension d = new Dimension(350,22);
		path1.setPreferredSize(d);
		path2.setPreferredSize(d);
		pattern1.setPreferredSize(d);
		pattern2.setPreferredSize(d);
		pattern1.setText("Left");
		pattern2.setText("Right");
		path1.setAlignmentX(0);
		path2.setAlignmentX(0);
		pattern1.setAlignmentX(0);
		pattern2.setAlignmentX(0);
		basename1.setAlignmentX(0);
		basename2.setAlignmentX(0);
		verticalBox.add(new JLabel("Path 1:"));
		verticalBox.add(path1);
		verticalBox.add(new JLabel("Pattern 1:"));
		verticalBox.add(pattern1);
		verticalBox.add(new JLabel("Basename 1:"));
		verticalBox.add(basename1);
		verticalBox.add(new JLabel("Path 2:"));
		verticalBox.add(path2);
		verticalBox.add(new JLabel("Pattern 2:"));
		verticalBox.add(pattern2);
		verticalBox.add(new JLabel("Basename 2:"));
		verticalBox.add(basename2);
		retPanel.add(verticalBox);
		Box hb = Box.createHorizontalBox();
		hb.setAlignmentX(0);
		verticalBox.add(hb);		
		hb.add(loadPath1Button);
		hb.add(loadPath2Button);
		
		loadPath1Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				dualChannel1FileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = dualChannel1FileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File file = dualChannel1FileChooser.getSelectedFile();
					path1.setText(file.getParent()+"\\");
				}
			}
		});
		
		loadPath2Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				dualChannel2FileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				int returnVal = dualChannel2FileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File file = dualChannel2FileChooser.getSelectedFile();
					path2.setText(file.getParent()+"\\");
				}
			}
		});
		return retPanel;
	}
	
	public String getPath1(){
		return path1.getText();
	}
	public String getPattern1(){
		return pattern1.getText();
	}
	public String getPath2(){
		return path2.getText();
	}
	public String getPattern2(){
		return pattern2.getText();
	}
	

	public String[] getSettings(){
		String[] tempString = {path1.getText(), pattern1.getText(), path2.getText(), pattern2.getText(), basename1.getText(), basename2.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		path1.setText(tempString[0]);
		pattern1.setText(tempString[1]);
		path2.setText(tempString[2]);
		pattern2.setText(tempString[3]);
		basename1.setText(tempString[4]);
		basename2.setText(tempString[5]);
	}
	public DualColorMultipleFileInputGUI getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof DualColorMultipleFileInputGUI){
			DualColorMultipleFileInputGUI returnObject = new DualColorMultipleFileInputGUI(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
		return new DualColorMultipleFileInputGUI(mf);
	}
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {
		Utilities.addPropertyChangeListener(this);
		ArrayList<StormData> list = Utilities.openSeries(path1.getText(), pattern1.getText(), path2.getText(), pattern2.getText());
		sd1.copyStormData(list.get(0));
		sd2.copyStormData(list.get(1));
		if (basename1.getText().equals("")){
			sd1.setBasename(pattern1.getText());
		}
		else{
			sd1.setBasename(basename1.getText());
		}
		if (basename1.getText().equals("")){
			sd2.setBasename(pattern2.getText());
		}
		else{
			sd2.setBasename(basename2.getText());
		}
		
		setProgressbarValue(100);
	}
	
}