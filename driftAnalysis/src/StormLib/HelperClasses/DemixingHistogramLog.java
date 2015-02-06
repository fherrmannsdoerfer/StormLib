package StormLib.HelperClasses;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import StormLib.OutputClass;

public class DemixingHistogramLog extends BasicProcessingInformation {
	public DemixingHistogramLog(){
		
	}
	public DemixingHistogramLog(String path, String basename,
			ArrayList<ArrayList<Double>> histData, double binWidth, String tag) {
		setNameOfProcessing("Save demixing Histogram.");
		addParam("Binwidth of Histogram:",binWidth);
		String strAngle= "", strCount= "";
		for (int k = 0; k<histData.get(0).size(); k=k+1){
			strAngle = strAngle +histData.get(0).get(k)+" ";
			strCount = strCount +histData.get(1).get(k)+" ";
		}
		//addParam("Angles:", strAngle);
		//addParam("Counts:", strCount);
		String fullPath = OutputClass.saveImgHist(path, basename, tag, histData, "", "angles", 
				"counts", "distribution of angles of the intensity ratios", "demixingHist");
		addGraph("Histogram of distribution of angles :"+ this.breakName(basename), fullPath);
	}
	@Override
	public String toLatexString() {
		// TODO Auto-generated method stub
		String retString = "\\begin{minipage}{\\linewidth}\n"+paramsToString()+"\\end{minipage}\n";
		return retString;
	}

}
