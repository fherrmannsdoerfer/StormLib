package StormLib.HelperClasses;

import java.util.ArrayList;

import StormLib.OutputClass;
import StormLib.StormLocalization;

public class LocsSaveLog extends BasicProcessingInformation {

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
