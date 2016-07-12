package gui;

import java.io.Serializable;
import java.util.ArrayList;

import dataStructure.StormData;

public class Controler implements Serializable{
	MainFrame mf;
	StormData ch1 = new StormData();
	StormData ch2 = new StormData();
	
	public void resetProgressBar(ArrayList<ProcessingStepsPanel> functions){
	    for (int i = 0; i<functions.size(); i++){
	        functions.get(i).setProgressbarValue(0);
	    }
	}
	
	public void resetData(){
		ch1 = new StormData();
		ch2 = new StormData();
	}
	
	public void setMainFrameReference(MainFrame mf) {
		this.mf = mf;
	}
	public void startProcessing(
			ArrayList<ProcessingStepsPanel> functions) {
		for (ProcessingStepsPanel psp: functions){
			psp.process(ch1, ch2);
		}
		System.out.println("Program finished");
	}
	
}
