package StormLib.HelperClasses;

import java.io.Serializable;
import java.util.ArrayList;

import StormLib.OutputClass;

public class LocalizationsPerFrameLog extends BasicProcessingInformation implements Serializable{

	public LocalizationsPerFrameLog(String path, String basename,
			ArrayList<ArrayList<Integer>> data, int binWidth,
			String tag) {
		setNameOfProcessing("Localizations per Frame.");
		addParam("Binwidth of Graph:",binWidth);
		ArrayList<ArrayList<Double>> dataD = new ArrayList<ArrayList<Double>>();
		dataD.add(new ArrayList<Double>());
		dataD.add(new ArrayList<Double>());
		for (int i =0;i<data.get(0).size();i++){
			dataD.get(0).add((double)data.get(0).get(i));
			dataD.get(1).add((double)data.get(1).get(i));
		}
		String fullPath = OutputClass.savePlot(path, basename, tag, dataD, "", "frames", 
				"counts", "Number of localizations per frame", "LocsPerFrame");
		addGraph("Histogram of distribution of angles of :"+ this.breakName(basename), fullPath);
	}

	@Override
	public String toLatexString() {
		// TODO Auto-generated method stub
		String retString = "\\begin{minipage}{\\linewidth}\n"+paramsToString()+"\\end{minipage}\n";
		return retString;
	}

}
