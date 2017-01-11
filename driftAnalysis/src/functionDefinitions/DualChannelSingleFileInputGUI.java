package functionDefinitions;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;

import javax.swing.Box;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import gui.MainFrame;
import gui.ProcessingStepsPanel;

import javax.swing.JButton;

import dataStructure.StormData;

public class DualChannelSingleFileInputGUI extends ProcessingStepsPanel{
	JTextField path1 = new JTextField("");
	JTextField file1 = new JTextField("");
	JTextField path2 = new JTextField("");
	JTextField file2 = new JTextField("");
	JTextField basename1 = new JTextField("");
	JTextField basename2 = new JTextField("");
	private static String name = "Dual-Channel Single File Input";
	private final Box verticalBox2 = Box.createVerticalBox();
	private final Box hb = Box.createHorizontalBox();
	private final JButton loadFile1Button = new JButton("Load File 1");
	private final JButton loadFile2Button = new JButton("Load File 2");
	final JFileChooser dualChannel1FileChooser = new JFileChooser();
	final JFileChooser dualChannel2FileChooser = new JFileChooser();
	
	
	public DualChannelSingleFileInputGUI(MainFrame mf) {
		super(mf);
		path1.setPreferredSize(mf.style.getDimensionPathFields());
		file1.setPreferredSize(mf.style.getDimensionPathFields());
		path2.setPreferredSize(mf.style.getDimensionPathFields());
		file2.setPreferredSize(mf.style.getDimensionPathFields());
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getColorInput());
		this.setOptionPanel(createOptionPanel());
	}
	
	public DualChannelSingleFileInputGUI(){}
	
	private JPanel createOptionPanel(){
		JPanel retPanel = new JPanel();
		retPanel.add(verticalBox2);
		Box verticalBox = Box.createVerticalBox();
		verticalBox2.add(verticalBox);
		Dimension d = new Dimension(350,22);
		path1.setPreferredSize(d);
		file1.setPreferredSize(d);
		path2.setPreferredSize(d);
		file2.setPreferredSize(d);
		path1.setAlignmentX(0);
		file1.setAlignmentX(0);
		path2.setAlignmentX(0);
		file2.setAlignmentX(0);
		basename1.setAlignmentX(0);
		basename2.setAlignmentX(0);
		verticalBox.add(new JLabel("Path 1:"));
		verticalBox.add(path1);
		verticalBox.add(new JLabel("File 1:"));
		verticalBox.add(file1);
		verticalBox.add(new JLabel("Basename 1:"));
		verticalBox.add(basename1);
		verticalBox.add(new JLabel("Path 2:"));
		verticalBox.add(path2);
		verticalBox.add(new JLabel("File 2:"));
		verticalBox.add(file2);
		verticalBox.add(new JLabel("Basename 2:"));
		verticalBox.add(basename2);
		hb.setAlignmentX(0);
		verticalBox2.add(hb);		
		hb.add(loadFile1Button);
		hb.add(loadFile2Button);
		
		loadFile1Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				int returnVal = dualChannel1FileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File file = dualChannel1FileChooser.getSelectedFile();
					path1.setText(file.getParent()+"\\");
					file1.setText(file.getName());
				}
			}
		});
		
		loadFile2Button.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				int returnVal = dualChannel2FileChooser.showOpenDialog(null);
				if (returnVal == JFileChooser.APPROVE_OPTION){
					File file = dualChannel2FileChooser.getSelectedFile();
					path2.setText(file.getParent()+"\\");
					file2.setText(file.getName());
				}
			}
		});
		
		return retPanel;
	}
	
	public String getPath1(){
		return path1.getText();
	}
	public String getFile1(){
		return file1.getText();
	}
	public String getPath2(){
		return path2.getText();
	}
	public String getFile2(){
		return file2.getText();
	}
	public String getBasename1(){
		return basename1.getText();
	}
	public String getBasename2(){
		return basename2.getText();
	}
	public String[] getSettings(){
		String[] tempString = {path1.getText(), file1.getText(), path2.getText(), file2.getText(), basename1.getText(), basename2.getText()};
		return tempString;
	}
	public void setSettings(String[] tempString){
		path1.setText(tempString[0]);
		file1.setText(tempString[1]);
		path2.setText(tempString[2]);
		file2.setText(tempString[3]);
		basename1.setText(tempString[4]);
		basename2.setText(tempString[5]);
	}
	public DualChannelSingleFileInputGUI getProcessingStepsPanelObject(ProcessingStepsPanel processingStepsPanelObject, MainFrame mf){
		if (processingStepsPanelObject instanceof DualChannelSingleFileInputGUI){
			DualChannelSingleFileInputGUI returnObject = new DualChannelSingleFileInputGUI(mf);
			return returnObject;
		}
		return null;
	}
	public ProcessingStepsPanel getFunction(MainFrame mf){
		return new DualChannelSingleFileInputGUI(mf);
	}
	public String getFunctionName(){
		return name;
	}

	@Override
	public void process(StormData sd1, StormData sd2) {
		sd1.setFname(getFile1());
		sd1.setPath(getPath1());
		sd1.setLocs(sd1.importData(getPath1()+getFile1()));
		sd1.setBasename(basename1.getText());
		setProgressbarValue(50);
		sd2.setFname(getFile2());
		sd2.setPath(getPath2());
		sd2.setLocs(sd2.importData(getPath2()+getFile2()));
		sd2.setBasename(basename2.getText());
		setProgressbarValue(100);
		
	}
}