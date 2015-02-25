package StormLib.HelperClasses;

import java.util.ArrayList;

import StormLib.OutputClass;

public class LocalizationPrecissionEstimationHistogramLog extends
		BasicProcessingInformation {

	
	public LocalizationPrecissionEstimationHistogramLog(String path,
			String basename, ArrayList<ArrayList<Double>> histXY,
			ArrayList<ArrayList<Double>> histZ, double binwidth,
			double sigmaXY, double sigmaZ, String tag) {
		setNameOfProcessing("Save localization precission estimation histogram XY and Z.");
		addParam("Binwidth of Histogram:",binwidth+ " nm");
		addParam("Fitted simga of XY:", sigmaXY);
		addParam("Fitted sigma of Z:", sigmaZ);
		String strAngle= "", strCount= "";
		double maxCount = 0;
		for (int k = 0; k<histXY.get(0).size(); k=k+1){
			strAngle = strAngle +histXY.get(0).get(k)+" ";
			strCount = strCount +histXY.get(1).get(k)+" ";
			if (histXY.get(1).get(k)>maxCount){
				maxCount = histXY.get(1).get(k);
			}
		}
		//addParam("Distances XY:", strAngle);
		//addParam("Counts XY:", strCount);
		ArrayList<Double> distsXY = new ArrayList<Double>();
		ArrayList<Double> fit = new ArrayList<Double>();
		for (int i = 0; i<histXY.get(0).size(); i++){
			distsXY.add(histXY.get(0).get(i));
			fit.add(maxCount*Math.exp(0.5)/Math.sqrt(2)/sigmaXY*histXY.get(0).get(i)*Math.exp(-Math.pow(histXY.get(0).get(i), 2)/4/Math.pow(sigmaXY, 2)));
		}
		ArrayList<ArrayList<Double>> fitPlot = new ArrayList<ArrayList<Double>>();
		fitPlot.add(distsXY);
		fitPlot.add(fit);
		ArrayList<ArrayList<ArrayList<Double>>> data = new ArrayList<ArrayList<ArrayList<Double>>>();
		data.add(histXY);
		data.add(fitPlot);
		ArrayList<String> labels = new ArrayList<String>();
		labels.add("data");
		labels.add("fit");
		String fullPath = OutputClass.saveMulticolorPlot(path, basename, tag, "Localization Fit", "distances in nm", 
				"counts","fit of local localization precision estimation",data, labels);
		//String fullPath = OutputClass.saveImgHist(path, basename, tag, histXY, "", "distances", 
		//		"counts", "distances of consecutive points","locHistXY");
		addGraph("Histogram of distribution of distances of consecutive points in XY of :"+ this.breakName(basename), fullPath);
		
		strAngle= "";
		strCount= "";
		for (int k = 0; k<histZ.get(0).size(); k=k+1){
			strAngle = strAngle +histZ.get(0).get(k)+" ";
			strCount = strCount +histZ.get(1).get(k)+" ";
		}
		//addParam("Distances Z:", strAngle);
		//addParam("Counts Z:", strCount);
		fullPath = OutputClass.saveImgHist(path, basename, tag, histZ, "", "distances", 
				"counts", "distances of consecutive points","locHistZ");
		addGraph("Histogram of distribution of distances of consecutive points in Z of :"+ this.breakName(basename), fullPath);
	}
	
	@Override
	public String toLatexString() {
		// TODO Auto-generated method stub
		String retString = "\\begin{minipage}{\\linewidth}\n"+paramsToString()+"\\end{minipage}\n";
		return retString;
	}

}
