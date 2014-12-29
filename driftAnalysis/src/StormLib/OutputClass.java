package StormLib;

import ij.ImagePlus;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.commons.math3.analysis.UnivariateFunction;

public class OutputClass {
	
	public static void writeLoadingStatistics(String path, String basename, ArrayList<Integer> errorlist, int nbrLocs){
		try{
			new File(path + "Statistics").mkdir();
			new File(path + "Statistics\\Texts\\").mkdir();
			new File(path + "Statistics\\Pictures\\").mkdir();
			PrintWriter outputStream = new PrintWriter(new FileWriter(path+"Statistics\\Texts\\"+basename+"_generalInformation.txt"));
			outputStream.println("Filename: "+basename);
			DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
			Date date = new Date();
			outputStream.println("Reconstruction date: "+dateFormat.format(date));
			outputStream.println("Number of localizations: "+nbrLocs);
			outputStream.println("Number of incorrectly read lines: "+errorlist.size());
			outputStream.println("Line numbers:");
			for (int i =0; i<errorlist.size(); i++){
				outputStream.print(errorlist.get(i)+", ");
			}
			outputStream.println();
			outputStream.close();
		} catch (IOException e) {e.printStackTrace();}
	}

	private static void writeImageSaveStatistics(String path, String basename, double pixelsize, ImagePlus imgP, String picname){
		try{
			PrintWriter outputStream = new PrintWriter(new FileWriter(path+"Statistics\\Texts\\"+basename+".txt"));
			outputStream.println("Filename: "+picname);
			outputStream.println("Used pixelsize: "+pixelsize);
			outputStream.println("Dimensions: "+imgP.getHeight()+" x "+imgP.getWidth());
			outputStream.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	private static void writeSaveDemixingStatistics(String path, String basename, double pixelsize, 
			ImagePlus imgP, String picname, DemixingParameters params){
		try{
			PrintWriter outputStream = new PrintWriter(new FileWriter(path+"Statistics\\Texts\\"+basename+".txt"));
			outputStream.println("Filename: "+picname);
			outputStream.println("Used pixelsize: "+pixelsize);
			outputStream.println("Dimensions: "+imgP.getHeight()+" x "+imgP.getWidth());
			outputStream.println("Angle1: "+params.getAngle1()+ "Angle2: "+params.getAngle2()+" Width1: "
					+ params.getWidth1()+ "Width2: "+ params.getWidth2());
			outputStream.close();
		} catch (IOException e) {e.printStackTrace();}
	}

	public static void save2DImage(String path, String basename, String tag, ImagePlus imgP, double pixelsize) {
		String picname = basename+"_2Dreconstruction_"+tag+".tif";
		ij.IJ.save(imgP, path+"Statistics\\Pictures\\"+picname);
		writeImageSaveStatistics(path, basename, pixelsize, imgP, picname);
	}

	public static void saveDemixingImage(String path, String basename, String tag,
			ArrayList<ImagePlus> colImg, DemixingParameters params, double pixelsize) {
		String picBaseName = basename+"_3Ddemixing_"+tag;
		ij.IJ.save(colImg.get(0),path+"Statistics\\Pictures\\"+picBaseName+"_red.tif");
		ij.IJ.save(colImg.get(1),path+"Statistics\\Pictures\\"+picBaseName+"_green.tif");
		ij.IJ.save(colImg.get(2),path+"Statistics\\Pictures\\"+picBaseName+"_blue.tif");
	}
	
	public static void save3DImage(String path, String basename, String tag,
			ArrayList<ImagePlus> colImg, double pixelsize){
		String picBaseName = basename+"_3Dreconstruction_"+tag;
		ij.IJ.save(colImg.get(0),path+"Statistics\\Pictures\\"+picBaseName+"_red.tif");
		ij.IJ.save(colImg.get(1),path+"Statistics\\Pictures\\"+picBaseName+"_green.tif");
		ij.IJ.save(colImg.get(2),path+"Statistics\\Pictures\\"+picBaseName+"_blue.tif");
	}

	public static void writeArrayListForVisp(String path, String basename, ArrayList<StormLocalization> locs, String tag) {
		try {
			FileWriter writer = new FileWriter(path+"forVisp_"+basename+tag+".txt");
			for (int i = 0; i<locs.size(); i++){
				writer.append(locs.get(i).toPlainVispString()+"\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}

	public static void writeLocs(String path, String basename,
			ArrayList<StormLocalization> locs, String tag) {
		try{
			FileWriter writer = new FileWriter(path+basename+tag+".txt");
			for (int i = 0; i<locs.size(); i++){
				writer.append(locs.get(i).toPlainString()+"\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}

	public static void saveLocsPerFrame(String path, String basename,
			ArrayList<ArrayList<Integer>> tmp, int binWidth, String tag) {
		try {
			PrintWriter outputStream = new PrintWriter(new FileWriter(path+"Statistics\\Texts\\"+basename+"LocsPerFrame"+tag+".txt"));
			outputStream.println("Histogram for number of localizations per frame, first row frame second row count (bin with is: "+binWidth+")");
			String strFrames= "", strLocsPerFrame= "";
			for (int k = 0; k<tmp.get(0).size(); k=k+1){
				strFrames = strFrames +tmp.get(0).get(k)+" ";
				strLocsPerFrame = strLocsPerFrame +tmp.get(1).get(k)+" ";
			}
			outputStream.println(strFrames);
			outputStream.println(strLocsPerFrame);
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Histogram saved.");
	}
	
	public static void saveDriftLog(ArrayList<double[][]> dds, UnivariateFunction fx, 
			UnivariateFunction fy, String path, String basename, int frameMax, String tag, double pixelSize){
		try {
			int nbrChunks = dds.get(0)[0].length-1;
			PrintWriter outputStream = new PrintWriter(new FileWriter(
					path+"Statistics\\Texts\\"+basename+"Driftlog"+tag+".txt"));
			outputStream.println("Automatically generated log file for drift correction");
			outputStream.println("Pixesl to nm ratio: "+pixelSize);
			outputStream.println("Matrix of chunkwise drift X");
			for (int j = 0;j<nbrChunks;j++){
				for (int jj = 0;jj<nbrChunks;jj++){
					outputStream.print(dds.get(0)[j][jj]+" ");
				}
				outputStream.println();
			}
			outputStream.println("Matrix of chunkwise drift Y");
			for (int j = 0;j<nbrChunks;j++){
				for (int jj = 0;jj<nbrChunks;jj++){
					outputStream.print(dds.get(1)[j][jj]+" ");
				}
				outputStream.println();
			}
			String strFrames= "", strDriftX= "", strDriftY = "";
			double maxDriftX = 0;
			double maxDriftY = 0;
			for (int k = 0; k<frameMax; k=k+100){
				strFrames = strFrames +k+" ";
				strDriftX = strDriftX +fx.value(k)+" ";
				strDriftY = strDriftY +fy.value(k)+" ";
				maxDriftX = Math.max(maxDriftX, fx.value(k));
				maxDriftY = Math.max(maxDriftY, fy.value(k));
			}
			outputStream.println(strFrames);
			outputStream.println(strDriftX);
			outputStream.println(strDriftY);
			outputStream.close();
			if (maxDriftX>40 || maxDriftY > 40){
				System.out.println("High drift probably incorrect driftcorrection!!!");
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Drift log saved.");
	}
	
	public static void writeDemixingOutput(String path, String basename, ArrayList<StormLocalization> ch1, ArrayList<StormLocalization> ch2, String tag){
		try {
			PrintWriter outputStream = new PrintWriter(new FileWriter(
					path+"Statistics\\Texts\\"+basename+"DemixingStatistic"+tag+".txt"));
			outputStream.print("Automatically generated log file for the pairwise occuring points.  ");
			outputStream.println("Structure: data StormLocalization channel 1 , data StormLocalization channel2");
			for (int j = 0;j<ch1.size();j++){
					outputStream.print(ch1.get(j).toPlainString()+" ");
					outputStream.print(ch2.get(j).toPlainString()+" ");
					outputStream.println();
			}
			
			outputStream.close();
			System.out.println("Demixing statistics written");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void saveConnectionResult(String path, String basename,
			int counter, int size, String tag) {
		try {
			PrintWriter outputStream = new PrintWriter(new FileWriter(path+"Statistics\\Texts\\"+basename+"ConnectionStatistic"+tag+".txt"));
			outputStream.println("Number of connected traces: "+counter);
			outputStream.println("Total number of traces: "+size);
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
