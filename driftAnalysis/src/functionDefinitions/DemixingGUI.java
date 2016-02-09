package functionDefinitions;

import functions.Demixing;
import gui.MainFrame;
import gui.ProcessingStepsPanel;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.io.Serializable;
import java.util.ArrayList;

import javax.swing.Box;
import javax.swing.JCheckBox;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JTextField;

import dataStructure.StormData;

public class DemixingGUI extends ProcessingStepsPanel implements Serializable{
	private static String name = "Demixing";
	JCheckBox savePairedOutput = new JCheckBox("Save Paired Output");
	JTextField tag = new JTextField();
	JTextField dist = new JTextField("50");
	JTextField minInt = new JTextField("500");
	JTextField nbrIter = new JTextField("1500");
	JTextField toleratedError = new JTextField("60");
	JTextField chunkSize = new JTextField("5000");
	JTextField[] listTextFields = {tag,dist,minInt,nbrIter,toleratedError, chunkSize};
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
		Box verticalBox = Box.createVerticalBox();
		verticalBox.add(savePairedOutput);
		verticalBox.add(new JLabel("tag:"));
		verticalBox.add(tag);
		Box hb = Box.createHorizontalBox();
		Box vb = Box.createVerticalBox();
		Component hs = Box.createHorizontalStrut(20);
		vb.add(new JLabel("maximal Distance:"));
		vb.add(dist);
		vb.add(new JLabel("number of Iterations"));
		vb.add(nbrIter);
		Box vb2 = Box.createVerticalBox();
		vb2.add(new JLabel("minimal Intensity:"));
		vb2.add(minInt);
		vb2.add(new JLabel("tolerated Error:"));
		vb2.add(toleratedError);
		hb.add(vb);
		hb.add(hs);
		hb.add(vb2);
		verticalBox.add(hb);
		Box verticalBox3 = Box.createVerticalBox();
		verticalBox3.add(new JLabel("Chunksize:"));
		chunkSize.setMaximumSize(new Dimension(100,20));
		verticalBox3.add(chunkSize);
		verticalBox.add(verticalBox3);
		retPanel.add(verticalBox);
		
		return retPanel;
	}
	public String[] getSettings(){
		String statusChkBox = "";
		if (savePairedOutput.isSelected()){
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
			savePairedOutput.setSelected(true);
		}
		else{
			savePairedOutput.setSelected(false);
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
			unmixedChannels.add(Demixing.spectralUnmixing(chunksChannel1.get(j), chunksChannel2.get(j),false,tag.getText()));
			setProgressbarValue((int)(j*100./numberChunks));
		}
		StormData unmixedFromParts = new StormData();
		for (int j = 0; j < numberChunks; j++){
//			unmixedFromParts.addStormData(unmixedChannels.get(i)); //get(i) bezieht sich auf controler-schleife durch die listprocessingstepspanel, ist die nummer der ausgeführten funktion
			unmixedFromParts.addStormData(unmixedChannels.get(j));
		}
		sd1.copyStormData(unmixedFromParts);
		setProgressbarValue(100);
		
	}
}
