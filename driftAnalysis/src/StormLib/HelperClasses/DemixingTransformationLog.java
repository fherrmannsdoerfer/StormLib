package StormLib.HelperClasses;

import java.util.ArrayList;

public class DemixingTransformationLog extends BasicProcessingInformation {
	private String trafo = "";
	
	public DemixingTransformationLog(int nbrIter, double toleratedError,
			ArrayList<Integer> frames, ArrayList<Integer> listOfMatchingPoints,
			ArrayList<Double> listOfErrors, double[][] finalTrafo) {
		setNameOfProcessing("Transformation for demixing found.");
		addParam("Number of iterations:", nbrIter);
		addParam("toleratedError:", toleratedError);
		String lof = "";
		
		for (int i = 0; i<frames.size(); i++){
			lof = lof+", "+frames.get(i);
		}
		
		String lomp = "";
		double meanMp = 0;
		for (int i = 0; i<listOfMatchingPoints.size(); i++){
			lomp = lomp+", "+listOfMatchingPoints.get(i);
			meanMp += listOfMatchingPoints.get(i);
		}	
		meanMp /= listOfMatchingPoints.size();
				
		String loe = "";
		double meanOe = 0;
		for (int i = 0; i<listOfErrors.size(); i++){
			loe = loe+", "+listOfErrors.get(i);
			meanOe += listOfErrors.get(i);
		}
		meanOe /= listOfErrors.size();
		addParam("Frames used:", lof+"\\newline");
		//addParam("Matching points found:", lomp+"\\newline");
		//addParam("Averaged RSME of the matching poinst:", loe+"\\newline");
		addParam("Average number of matching points per frame:", meanMp);
		addParam("Average number error between pair of matchin points:",meanOe);
		
		trafo = trafo + "Transformation matrix:\\newline\n";
		trafo = trafo + "\\begin{equation}\n";
		trafo = trafo +"\\left(\n";
		trafo = trafo + "\\begin{array}{rrr}\n";
		trafo = trafo + String.format( "%.4f", finalTrafo[0][0])+"&"+String.format( "%.4f", finalTrafo[0][1])+"&"
				+String.format( "%.4f", finalTrafo[0][2])+"\\\\ \n";
		trafo = trafo + String.format( "%.4f", finalTrafo[1][0])+"&"+String.format( "%.4f", finalTrafo[1][1])
				+"&"+String.format( "%.4f", finalTrafo[1][2])+"\n";
		trafo = trafo + "\\end{array}\n";
		trafo = trafo +"\\right)\n";
		trafo = trafo + "\\end{equation}\n";
	}

	@Override
	public String toLatexString() {
		String retString = "\\begin{minipage}{\\linewidth}\n";
		retString = retString +paramsToString();
		retString = retString + trafo;
		retString = retString +"\\end{minipage}\n";
		return retString;
	}

}
