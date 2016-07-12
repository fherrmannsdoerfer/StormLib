package StormLib.HelperClasses;

import java.io.Serializable;
import java.util.ArrayList;

import org.apache.commons.math3.analysis.UnivariateFunction;

import StormLib.OutputClass;

public class DriftCorrectionLog extends BasicProcessingInformation implements Serializable{
	private String matrixX;
	private String matrixY;
	private String matrixZ;
	private String strFrames= "";
	private String strDriftX = "";
	private String strDriftY = "";
	private String strDriftZ = "";
	public DriftCorrectionLog(ArrayList<double[][]> dds, UnivariateFunction fx,
			UnivariateFunction fy,UnivariateFunction fz, String path, String basename, int frameMax, int chunksize, int nbrChunks, String tag) {
		setNameOfProcessing("Drift correction");
		addParam("Used chunksize:",chunksize);
		addParam("Number of chunks:",nbrChunks);
		addParam("Largest frame:", frameMax);
		
		matrixX = matrixX+"Matrix of chunkwise drift X (XY)\\newline\n";
		matrixX = matrixX+fillMatrix(dds.get(0));
		matrixX = matrixX+"Matrix of chunkwise drift X (XZ)\\newline\n";
		matrixX = matrixX+fillMatrix(dds.get(2));
				
		matrixY=matrixY+"Matrix of chunkwise drift Y (XY)\\newline\n";
		matrixY=matrixY+fillMatrix(dds.get(1));
		matrixY=matrixY+"Matrix of chunkwise drift Y (YZ)\\newline\n";
		matrixY=matrixY+fillMatrix(dds.get(4));
		
		matrixZ=matrixZ+"Matrix of chunkwise drift Z (XZ)\\newline\n";
		matrixZ =matrixZ+fillMatrix(dds.get(3));
		matrixZ =matrixZ+"Matrix of chunkwise drift Z (YZ)\\newline\n";
		matrixZ = matrixZ+fillMatrix(dds.get(5));
		double maxDriftX = 0;
		double maxDriftY = 0;
		ArrayList<Integer> frames = new ArrayList<Integer>();
		for (int k = 0; k<frameMax; k=k+100){
			strFrames = strFrames +k+" ";
			strDriftX = strDriftX +fx.value(k)+" ";
			strDriftY = strDriftY +fy.value(k)+" ";
			strDriftZ = strDriftZ +fz.value(k)+" ";
			frames.add(k);
			maxDriftX = Math.max(maxDriftX, fx.value(k));
			maxDriftY = Math.max(maxDriftY, fy.value(k));
		}
		strFrames = strFrames +"\\newline\n";
		strDriftX = strDriftX +"\\newline\n";
		strDriftY = strDriftY +"\\newline\n";
		strDriftZ = strDriftZ +"\\newline\n";
		if (maxDriftX>40 || maxDriftY > 40){
			System.out.println("High drift probably incorrect driftcorrection!!!");
		}
		String fullFilename = OutputClass.saveDriftGraph(path, basename, tag, frames,fx,fy,fz);
		addGraph("Drift over frames",fullFilename);
		// TODO Auto-generated constructor stub
	}
	
	private String fillMatrix(double[][] mat){
		String retString ="";
		retString = retString+"\\begin{equation}\n";
		retString = retString +"\\begin{pmatrix}\n";
		for (int j = 0;j<mat.length;j++){
			for (int jj = 0;jj<mat[j].length-1;jj++){
				retString = retString+String.format( "%.2f", mat[j][jj] )+" &";
			}
			retString = retString+String.format( "%.2f", mat[j][mat[j].length-1]);
			retString = retString+"\\\\\n";
		}
		retString = retString+"\\end{pmatrix}\n";
		retString = retString+"\\end{equation}\n";
		return retString;
	}

	@Override
	public String toLatexString() {
		String retStr = "";
	    retStr = retStr + "\\begin{minipage}{\\linewidth}\n"+paramsToString();
		retStr = retStr + matrixX;
		retStr = retStr + "\\end{minipage}\n";
		
		retStr = retStr + "\\begin{minipage}{\\linewidth}\n"+paramsToString();
		retStr = retStr + matrixY;
		retStr = retStr + "\\end{minipage}\n";
		
		retStr = retStr + "\\begin{minipage}{\\linewidth}\n"+paramsToString();
		retStr = retStr + matrixZ;
		retStr = retStr + "\\end{minipage}\n";
/*		retStr = retStr + strFrames;
		retStr = retStr + strDriftX;
		retStr = retStr + strDriftY;*/
		// TODO Auto-generated method stub
		return retStr;
	}

}
