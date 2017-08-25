package functionDefinitions;

import functions.Demixing;
import gui.MainFrame;
import gui.ProcessingStepsPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Font;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataStructure.StormData;

public class DemixingGUI extends ProcessingStepsPanel implements Serializable{
	private static String name = "Spectral Demixing";
	JCheckBox useShiftOnly = new JCheckBox("Use only shift for Trafo");
	JTextField tag = new JTextField();
	JTextField dist = new JTextField("200");
	JTextField minInt = new JTextField("500");
	JTextField nbrIter = new JTextField("1500");
	JTextField toleratedError = new JTextField("60");
	JTextField chunkSize = new JTextField("5000");
	JTextField[] listTextFields = {tag,dist,minInt,nbrIter,toleratedError, chunkSize};
	public DemixingGUI(MainFrame mf) {
		super(mf);
		this.setParameterButtonsName(name);
		this.setColor(mf.style.getColorProcessing());
		this.setOptionPanel(createOptionPanel());
		
	}
	
	public DemixingGUI(){}
	
	private JPanel createOptionPanel(){
		useShiftOnly.setSelected(true);
		JPanel retPanel = new JPanel();
		retPanel.setSize(300, 500);
		Box verticalBox = Box.createVerticalBox();
		Box verticalBox3 = Box.createVerticalBox();
		verticalBox3.add(new JLabel("Chunk Size [frames]:"));
		chunkSize.setMaximumSize(new Dimension(100,20));
		verticalBox3.add(chunkSize);
		Box horizontalBox2 = Box.createHorizontalBox();
		horizontalBox2.add(verticalBox3);
		horizontalBox2.add(Box.createHorizontalGlue());
		horizontalBox2.add(useShiftOnly);
		verticalBox.add(horizontalBox2);
		
		Component vs = Box.createVerticalStrut(20);
		verticalBox.add(vs);
//		verticalBox.add(new JLabel("tag:"));
//		verticalBox.add(tag);
		Box hb = Box.createHorizontalBox();
		Box vb = Box.createVerticalBox();
		Component hs = Box.createHorizontalStrut(20);
		
		JLabel trafoLab = new JLabel("Params. for Transformation");
		trafoLab.setFont(new Font(trafoLab.getFont().getName(),Font.BOLD,trafoLab.getFont().getSize()));
		vb.add(trafoLab);
		vb.add(new JLabel("Number of Iterations"));
		vb.add(nbrIter);
		vb.add(new JLabel("Tolerated Error [nm]:"));
		vb.add(toleratedError);
		
		
		Box vb2 = Box.createVerticalBox();
		JLabel demixingLab = new JLabel("Params. for Demixing");
		demixingLab.setFont(new Font(trafoLab.getFont().getName(),Font.BOLD,trafoLab.getFont().getSize()));
		vb2.add(demixingLab);
		vb2.add(new JLabel("Minimal Intensity:"));
		vb2.add(minInt);
		vb2.add(new JLabel("Tolerated Error [nm]:"));
		vb2.add(dist);
		hb.add(vb);
		hb.add(hs);
		hb.add(vb2);
		verticalBox.add(hb);
		
		retPanel.add(verticalBox);
		
		return retPanel;
	}
	public String[] getSettings(){
		String statusChkBox = "";
		if (useShiftOnly.isSelected()){
			statusChkBox = "selected";
		}
		else{
			statusChkBox = "notSelected";
		}
		String[] tempString = new String[listTextFields.length+1];
		tempString[0] = statusChkBox;
		getTextFieldTexts(listTextFields, 1, tempString);
		return tempString;
	}
	public void setSettings(String[] tempString){
		if (tempString[0].equals("selected")){
			useShiftOnly.setSelected(true);
		}
		else{
			useShiftOnly.setSelected(false);
		}
		setTextFieldTexts(listTextFields, 1, tempString);
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
	public int getChunksize(){
		try{
			return Integer.valueOf(chunkSize.getText());
		}
		catch(Exception e){
			return Integer.valueOf("5000");
		}
	
	}
	@Override
	public void process(StormData sd1, StormData sd2) {
		int chunkSize = getChunksize();//number of frames per chunk to get "local" transformations
		int numberChunks = (int)Math.ceil((double)sd1.getDimensions().get(7)/chunkSize);
		ArrayList<StormData> chunksChannel1 = new ArrayList<StormData>();
		for (int j = 0; j < numberChunks; j++){
			chunksChannel1.add(sd1.findSubset(chunkSize*j,chunkSize*(j+1),false));
		}
		
		ArrayList<StormData> chunksChannel2 = new ArrayList<StormData>();
		for (int j = 0; j < numberChunks; j++){
			chunksChannel2.add(sd2.findSubset(chunkSize*j,chunkSize*(j+1),false));
		}
		Demixing.addPropertyChangeListener(this);
		ArrayList<StormData> unmixedChannels = new ArrayList<StormData>();
		for (int j = 0; j < numberChunks; j++){
			unmixedChannels.add(Demixing.spectralUnmixing(chunksChannel1.get(j), chunksChannel2.get(j),false,"",Double.parseDouble(dist.getText())
					,Double.parseDouble(minInt.getText()),Integer.parseInt(nbrIter.getText()),Double.parseDouble(toleratedError.getText()),useShiftOnly.isSelected(),false));
			setProgressbarValue((int)(j*100./numberChunks));
		}
		StormData unmixedFromParts = new StormData();
		unmixedFromParts.copyAttributes(unmixedChannels.get(0));
		for (int j = 0; j < numberChunks; j++){
//			unmixedFromParts.addStormData(unmixedChannels.get(i)); //get(i) bezieht sich auf controler-schleife durch die listprocessingstepspanel, ist die nummer der ausgeführten funktion
			unmixedFromParts.addStormData(unmixedChannels.get(j));
		}
		sd1.copyStormData(unmixedFromParts);
		setProgressbarValue(100);
		
	}
}
