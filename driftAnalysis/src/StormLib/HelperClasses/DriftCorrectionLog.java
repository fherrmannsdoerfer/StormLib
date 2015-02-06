package StormLib.HelperClasses;

import java.util.ArrayList;

import org.apache.commons.math3.analysis.UnivariateFunction;

import StormLib.OutputClass;

public class DriftCorrectionLog extends BasicProcessingInformation {
	private String matrixX;
	private String matrixY;
	private String strFrames= "";
	private String strDriftX = "";
	private String strDriftY = "";
	public DriftCorrectionLog(ArrayList<double[][]> dds, UnivariateFunction fx,
			UnivariateFunction fy, String path, String basename, int frameMax, int chunksize, int nbrChunks, String tag) {
		setNameOfProcessing("Drift correction");
		addParam("Used chunksize:",chunksize);
		addParam("Number of chunks:",nbrChunks);
		addParam("Largest frame:", frameMax);
		
		matrixX = matrixX+"Matrix of chunkwise drift X\\newline\n";
		matrixX = matrixX+"\\begin{equation}\n";
		matrixX = matrixX +"\\begin{pmatrix}\n";
		for (int j = 0;j<nbrChunks;j++){
			for (int jj = 0;jj<nbrChunks-1;jj++){
				matrixX = matrixX+String.format( "%.2f", dds.get(0)[j][jj] )+" &";
			}
			matrixX = matrixX+String.format( "%.2f", dds.get(0)[j][nbrChunks-2]);
			matrixX = matrixX+"\\\\\n";
		}
		matrixX = matrixX+"\\end{pmatrix}\n";
		matrixX = matrixX+"\\end{equation}\n";
		matrixY="Matrix of chunkwise drift Y";
		matrixY = matrixY+"\\begin{equation}\n";
		matrixY = matrixY +"\\begin{pmatrix}\n";
		for (int j = 0;j<nbrChunks;j++){
			for (int jj = 0;jj<nbrChunks-1;jj++){
				matrixY = matrixY+String.format( "%.2f", dds.get(1)[j][jj])+" &";
			}
			matrixY = matrixY+String.format( "%.2f", dds.get(1)[j][nbrChunks-2]);
			matrixY = matrixY+"\\\\\n";
		}
		matrixY = matrixY+"\\end{pmatrix}\n";
		matrixY = matrixY+"\\end{equation}\n";
		double maxDriftX = 0;
		double maxDriftY = 0;
		ArrayList<Integer> frames = new ArrayList<Integer>();
		for (int k = 0; k<frameMax; k=k+100){
			strFrames = strFrames +k+" ";
			strDriftX = strDriftX +fx.value(k)+" ";
			strDriftY = strDriftY +fy.value(k)+" ";
			frames.add(k);
			maxDriftX = Math.max(maxDriftX, fx.value(k));
			maxDriftY = Math.max(maxDriftY, fy.value(k));
		}
		strFrames = strFrames +"\\newline\n";
		strDriftX = strDriftX +"\\newline\n";
		strDriftY = strDriftY +"\\newline\n";
		if (maxDriftX>40 || maxDriftY > 40){
			System.out.println("High drift probably incorrect driftcorrection!!!");
		}
		String fullFilename = OutputClass.saveDriftGraph(path, basename, tag, frames,fx,fy);
		addGraph("Drift over frames",fullFilename);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toLatexString() {
		String retStr = "\\begin{minipage}{\\linewidth}\n"+paramsToString();
		retStr = retStr + matrixX;
		retStr = retStr + matrixY;
		retStr = retStr + "\\end{minipage}\n";
/*		retStr = retStr + strFrames;
		retStr = retStr + strDriftX;
		retStr = retStr + strDriftY;*/
		// TODO Auto-generated method stub
		return retStr;
	}

}
