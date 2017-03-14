package gui;

import dataStructure.StormData;

public abstract class ImportModules extends ProcessingStepsPanel{
	public ImportModules(MainFrame mf){
		super(mf);
	}
	public ImportModules(){
	}
	abstract public void setPath(String path);

}
