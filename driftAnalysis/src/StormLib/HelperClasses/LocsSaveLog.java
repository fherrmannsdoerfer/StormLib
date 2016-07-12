package StormLib.HelperClasses;

import java.io.Serializable;
import java.util.ArrayList;

import dataStructure.StormLocalization;
import StormLib.OutputClass;

public class LocsSaveLog extends BasicProcessingInformation implements Serializable{

	public LocsSaveLog(String path, String basename,
			ArrayList<StormLocalization> locs, String tag) {
		setNameOfProcessing("Localization results saved (tag: "+tag+").");
		addParam("Number of localizations:",locs.size());
		OutputClass.writeLocs(path, basename, locs, tag);
	}

	@Override
	public String toLatexString() {
		// TODO Auto-generated method stub
		String retString = "\\begin{minipage}{\\linewidth}\n"+paramsToString()+"\\end{minipage}\n";
		return retString;
	}

}
