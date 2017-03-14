package gui;

import java.io.Serializable;
import java.util.ArrayList;

import dataStructure.StormData;
import functionDefinitions.BatchProcessingGUI;

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
			if (psp instanceof BatchProcessingGUI){
				batchprocessingWorkflow(functions);
			}
			else{
				normalWorkflow(functions);
			}
		}
		System.out.println("Program finished");
	}

	private void normalWorkflow(ArrayList<ProcessingStepsPanel> functions) {
		for (ProcessingStepsPanel psp: functions){
			psp.process(ch1, ch2);
		}
	}

	//if the first module is the BatchProcessing module this workflow is executed
	private void batchprocessingWorkflow(
			ArrayList<ProcessingStepsPanel> functions) {
		functions.get(0).process(ch1, ch2);
		int numberModules = functions.size();
		int numberBatchprocessingCycles = ((BatchProcessingGUI)functions.get(0)).getPaths().size();
		for (int k = 0; k<numberBatchprocessingCycles; k++){
			try{
				resetData();
				resetProgressBar(functions);
				functions.get(0).setProgressbarValue((int)Math.floor(100*(float)k/(float)numberBatchprocessingCycles));
				if (functions.get(1) instanceof ImportModules){
					System.out.println("next module is import module");
					((ImportModules) functions.get(1)).setPath(((BatchProcessingGUI)functions.get(0)).getPaths().get(k));
				}
				else{
					System.out.println("please select an import module after the batchprocessing module");
				}
				for (int i = 1; i<numberModules;i++){
					functions.get(i).process(ch1, ch2);
				}
			}
			catch (Exception e){
				System.out.println(e.getMessage());
			}
		}
		functions.get(0).setProgressbarValue(99);
	}
	
}
