package StormLib;


import functions.CreateScatterPlot;
import ij.ImagePlus;
import ij.ImageStack;
import ij.plugin.RGBStackMerge;
import ij.process.ShortProcessor;
import ij.process.StackConverter;

import java.awt.image.BufferedImage;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import javax.imageio.ImageIO;

import org.apache.commons.math3.analysis.UnivariateFunction;

import dataStructure.DemixingParameters;
import dataStructure.StormLocalization;
import StormLib.HelperClasses.BasicProcessingInformation;

public class OutputClass {
	
	public static void writeLoadingStatistics(String path, String basename, ArrayList<Integer> errorlist, int nbrLocs){
		try{
			createOutputFolder(path);
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
	
	public static void writeTransformation(String path, String basename, double[][] trafo){
		String subfolder = "\\Statistics\\Texts";
		new File(path + subfolder).mkdir();
		String fname = "\\"+basename+"_transformationMatrix.txt";
		PrintWriter outputStream;
		try {
			outputStream = new PrintWriter(new FileWriter(path + subfolder+fname));
			outputStream.println("Automatically generated file containing the transformation matrix applied to register two color probes");
			outputStream.println(trafo[0][0]+ " "+trafo[0][1]+" "+trafo[0][2]);
			outputStream.println(trafo[1][0]+ " "+trafo[1][1]+" "+trafo[1][2]);
			
			outputStream.close();
			System.out.println("Transformation written");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void createOutputFolder(String path) throws IOException{
		new File(path + "\\Statistics").mkdir();
		new File(path + "\\Statistics\\Texts\\").mkdir();
		new File(path + "\\Statistics\\Pictures\\").mkdir();
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
			outputStream.println("Angle1: "+params.getAngle1()+ "Angle2: "+params.getAngle2()+" Width1: " //Angle 1 = maximum intensity color 1
					+ params.getWidth1()+ "Width2: "+ params.getWidth2());
			outputStream.close();
		} catch (IOException e) {e.printStackTrace();}
	}

	public static String save2DImage(String path, String basename, String tag, ImagePlus imgP, double pixelsize) {
		String picname = basename+"_2Dreconstruction_"+tag+".png";
		String picnameTif = basename+"_2Dreconstruction_"+tag+".tif";
		String fullFilename = path+"Statistics\\Pictures\\"+picname;
		String fullFilenameTif = path+"Statistics\\Pictures\\"+picnameTif;
		save2DImage(fullFilename,imgP);
		//ij.IJ.save(new ImagePlus("",imgP.getProcessor().convertToByte(false)), fullFilename);
		save2DImage(fullFilenameTif,imgP);
		//ij.IJ.save(imgP, fullFilenameTif);
		writeImageSaveStatistics(path, basename, pixelsize, imgP, picname);
		return fullFilename;
	}
	
	public static void save2DImage(String fullFilename,ImagePlus imgP){
		try{
			ij.IJ.save(imgP,fullFilename);
		}
		catch(Exception e){}
	}

	public static String saveDemixingImage(String path, String basename, String tag,
			ArrayList<ImagePlus> colImg) {
		String picBaseName = basename+"_3Ddemixing_"+tag;
		String fullFilename = path+"Statistics\\Pictures\\"+picBaseName+"All.png";
		//String fullFilenameTif = path+"Statistics\\Pictures\\"+picBaseName+"All.tif";
		ij.IJ.save(colImg.get(0),path+"Statistics\\Pictures\\"+picBaseName+"_red.tif");
		ij.IJ.save(colImg.get(1),path+"Statistics\\Pictures\\"+picBaseName+"_green.tif");
		ij.IJ.save(colImg.get(2),path+"Statistics\\Pictures\\"+picBaseName+"_blue.tif");
		ImageStack is = new ImageStack(colImg.get(0).getWidth(),colImg.get(0).getHeight());
		is.addSlice(colImg.get(0).getProcessor());
		is.addSlice(colImg.get(1).getProcessor());
		is.addSlice(colImg.get(2).getProcessor());
		
		// Timm Addition
		ImagePlus[] imPlusStack = new ImagePlus[3];
		imPlusStack[0] = colImg.get(0);
		imPlusStack[1] = colImg.get(1);
		imPlusStack[2] = colImg.get(2);
		ImagePlus imgRGB = RGBStackMerge.mergeChannels(imPlusStack, true);
		ij.IJ.saveAs(imgRGB, "png", fullFilename);
		
		ImagePlus coloredImage = new ImagePlus("", is);
		coloredImage.setDimensions(3, 1, 1);
		//ij.IJ.save(coloredImage, fullFilenameTif);
//		coloredImage.getProcessor().convertToRGB();
//		ij.IJ.saveAs(coloredImage, "png", fullFilename);
		return fullFilename;
	}
	public static String save3DImage(String path, String basename, String tag,
			ArrayList<ImagePlus> colImg){
		//mode 0: 3 individual channels + png + Tiffstack
		//mode 1: png + tiffStack
		//mode 2: png
		return save3DImage(path, basename, tag, colImg, 0);
	}
	
	public static String save3DImage(String path, String basename, String tag,
			ArrayList<ImagePlus> colImg, int mode){
		String picBaseName = basename+"_3Dreconstruction_"+tag;
		String fullFilename = path+"Statistics\\Pictures\\"+picBaseName+"All.tif";
		if (mode <1){
			ij.IJ.save(colImg.get(0),path+"Statistics\\Pictures\\"+picBaseName+"_red.tif");
			ij.IJ.save(colImg.get(1),path+"Statistics\\Pictures\\"+picBaseName+"_green.tif");
			ij.IJ.save(colImg.get(2),path+"Statistics\\Pictures\\"+picBaseName+"_blue.tif");
		}
		ImageStack is = new ImageStack(colImg.get(0).getWidth(),colImg.get(0).getHeight());
		is.addSlice(colImg.get(0).getProcessor());
		is.addSlice(colImg.get(1).getProcessor());
		is.addSlice(colImg.get(2).getProcessor());
		ImagePlus[] imPlusStack = new ImagePlus[3];
		imPlusStack[0] = colImg.get(0);
		imPlusStack[1] = colImg.get(1);
		imPlusStack[2] = colImg.get(2);
		ImagePlus imgRGB = RGBStackMerge.mergeChannels(imPlusStack, true);
		ij.IJ.saveAs(imgRGB, "png", fullFilename);
		ImagePlus coloredImage = new ImagePlus("", is);
		coloredImage.setDimensions(3, 1, 1);
		if (mode < 2){
			ij.IJ.save(coloredImage, fullFilename);
		}
		return fullFilename;
	}
	
	public static String save3Dstack(String path, String basename, String tag,
			ArrayList<ImagePlus> colImg){
		String picBaseName = basename+"_3Dreconstruction_"+tag;
		String fullFilename = path+"Statistics\\Pictures\\"+picBaseName+"STACK.tif";
		
		ImageStack is = new ImageStack(colImg.get(0).getWidth(),colImg.get(0).getHeight());
		for (int i =0; i<colImg.size(); i++){
			is.addSlice(colImg.get(i).getProcessor());
		}
		ImagePlus coloredImage = new ImagePlus("", is);
		coloredImage.setDimensions(3, 1, 1);
		ij.IJ.save(coloredImage, fullFilename);
		return fullFilename;
	}
	
	
	public static void writeArrayListForVisp(String path, String basename, ArrayList<StormLocalization> locs, 
			String tag,DemixingParameters demixingParams) {
		double minAngle1 = demixingParams.getAngle1() - demixingParams.getWidth1()/2;
		double maxAngle1 = demixingParams.getAngle1() + demixingParams.getWidth1()/2;
		double minAngle2 = demixingParams.getAngle2() - demixingParams.getWidth2()/2;
		double maxAngle2 = demixingParams.getAngle2() + demixingParams.getWidth2()/2;
		try {
			FileWriter writer1 = new FileWriter(path+"Statistics\\Texts\\"+"forVispCH1_"+basename+tag+".txt");
			FileWriter writer2 = new FileWriter(path+"Statistics\\Texts\\"+"forVispCH2_"+basename+tag+".txt");
			for (int i = 0; i<locs.size(); i++){
				StormLocalization sl = locs.get(i);
				if (((sl.getAngle()> minAngle1 && sl.getAngle()< maxAngle1))|| sl.getAngle() == 0){
					writer1.append(sl.toPlainVispString()+"\n");
				}
				else if ((sl.getAngle()> minAngle2 && sl.getAngle()< maxAngle2) || sl.getAngle() == Math.PI/2){
					writer2.append(sl.toPlainVispString()+"\n");
				}
			}
			writer1.flush();
			writer1.close();
			writer2.flush();
			writer2.close();
		} catch (IOException e) {e.printStackTrace();}
	}

	public static void writeArrayListForVisp(String path, String basename, ArrayList<StormLocalization> locs, String tag) {
		if (locs.size()>0){
			try {
				FileWriter writer = new FileWriter(path+"Statistics\\Texts\\"+"forVisp_"+basename+tag+".txt");
				for (int i = 0; i<locs.size(); i++){
					writer.append(locs.get(i).toPlainVispString()+"\n");
				}
				writer.flush();
				writer.close();
			} catch (IOException e) {e.printStackTrace();}
		}
	}

	public static void writeArrayListForFRC(String path, String basename, ArrayList<StormLocalization> locs, String tag, int mode) {
		try {
			FileWriter writer = new FileWriter(path+"Statistics\\Texts\\"+"forFRC_"+basename+tag+".txt");
			for (int i = 0; i<locs.size(); i++){
				writer.append(locs.get(i).toPlainFRCString(mode)+"\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public static void writeArrayListForFRC(String path, String basename, ArrayList<StormLocalization> locs, 
			String tag,DemixingParameters demixingParams, int mode) {
		double minAngle1 = demixingParams.getAngle1() - demixingParams.getWidth1()/2;
		double maxAngle1 = demixingParams.getAngle1() + demixingParams.getWidth1()/2;
		double minAngle2 = demixingParams.getAngle2() - demixingParams.getWidth2()/2;
		double maxAngle2 = demixingParams.getAngle2() + demixingParams.getWidth2()/2;
		try {
			FileWriter writer1 = new FileWriter(path+"Statistics\\Texts\\"+"forFRCCH1_"+basename+tag+".txt");
			FileWriter writer2 = new FileWriter(path+"Statistics\\Texts\\"+"forFRCCH2_"+basename+tag+".txt");
			for (int i = 0; i<locs.size(); i++){
				StormLocalization sl = locs.get(i);
				if (((sl.getAngle()> minAngle1 && sl.getAngle()< maxAngle1))|| sl.getAngle() == 0){
					writer1.append(sl.toPlainFRCString(mode)+"\n");
				}
				else if ((sl.getAngle()> minAngle2 && sl.getAngle()< maxAngle2) || sl.getAngle() == Math.PI/2){
					writer2.append(sl.toPlainFRCString(mode)+"\n");
				}
			}
			writer1.flush();
			writer1.close();
			writer2.flush();
			writer2.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public static void writeLocs(String path, String basename,
			ArrayList<StormLocalization> locs, String tag) {
		if (locs.size()>0){
			try{
				FileWriter writer = new FileWriter(path+"Statistics\\Texts\\"+basename+tag+".txt");
				writer.append("x in nm; y in nm; z in nm; frame; intensity; angle\n");
				for (int i = 0; i<locs.size(); i++){
					writer.append(locs.get(i).toPlainString()+"\n");
				}
				writer.flush();
				writer.close();
			} catch (IOException e) {e.printStackTrace();}
		}
	}
	
	public static void writeLocs(String path, String basename, ArrayList<StormLocalization> locs, 
			String tag,DemixingParameters demixingParams) {
		double minAngle1 = demixingParams.getAngle1() - demixingParams.getWidth1()/2;
		double maxAngle1 = demixingParams.getAngle1() + demixingParams.getWidth1()/2;
		double minAngle2 = demixingParams.getAngle2() - demixingParams.getWidth2()/2;
		double maxAngle2 = demixingParams.getAngle2() + demixingParams.getWidth2()/2;
		try {
			FileWriter writer1 = new FileWriter(path+"Statistics\\Texts\\"+"forLocsDemixedCH1_"+basename+tag+".txt");
			FileWriter writer2 = new FileWriter(path+"Statistics\\Texts\\"+"forLocsDemixedCH2_"+basename+tag+".txt");
			writer1.append("x in nm; y in nm; z in nm; frame; intensity; angle\n");
			writer2.append("x in nm; y in nm; z in nm; frame; intensity; angle\n");
			for (int i = 0; i<locs.size(); i++){
				StormLocalization sl = locs.get(i);
				if (((sl.getAngle()> minAngle1 && sl.getAngle()< maxAngle1))|| sl.getAngle() == 0){
					writer1.append(sl.toPlainString()+"\n");
				}
				else if ((sl.getAngle()> minAngle2 && sl.getAngle()< maxAngle2) || sl.getAngle() == Math.PI/2){
					writer2.append(sl.toPlainString()+"\n");
				}
			}
			writer1.flush();
			writer1.close();
			writer2.flush();
			writer2.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public static void writeLocsForBaumgart(String path, String basename,
			ArrayList<StormLocalization> locs, String tag) {
		try{
			FileWriter writer = new FileWriter(path+basename+tag+".csv");
			writer.append(",,,,,\n");
			for (int i = 0; i<locs.size(); i++){
				writer.append(","+(locs.get(i).getX()+2000)+","+(locs.get(i).getY()+2000)+",0,"+locs.get(i).getFrame()+",0\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}

	public static void writeLocsPerFrame(String path, String basename,
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
		System.out.println("Histogram of localizations per frame saved.");
	}
	
	public static void writeDemixingParameters(String path, String basename, String tag,
			int nbrIter, double toleratedError, ArrayList<Integer> frames, ArrayList<Integer> listOfMatchingPoints, 
			ArrayList<Double> listOfErrors){
		try {
			PrintWriter outputStream = new PrintWriter(new FileWriter(path+"Statistics\\Texts\\"+
				basename+"DemixingTransformationParameters"+tag+".txt"));
			outputStream.println("Parameters for the transformation used to align both channels");
			outputStream.println("Number of iterations per frame: " + nbrIter);
			outputStream.println("Tolerated error (each set of points used for registration must have a smaller RMS error): "+toleratedError);
			outputStream.print("Used frames: ");
			for(int i = 0; i<frames.size(); i++){
				outputStream.print(frames.get(i)+ ", ");
			}
			outputStream.println(" ");
			outputStream.print("Number of matching points per subset: ");
			for(int i = 0; i<listOfMatchingPoints.size(); i++){
				outputStream.print(listOfMatchingPoints.get(i)+ ", ");
			}
			outputStream.println(" ");
			outputStream.print("Number of RMSE per subset: ");
			for(int i = 0; i<listOfErrors.size(); i++){
				outputStream.print(listOfErrors.get(i)+ ", ");
			}
			outputStream.println(" ");
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void writeDemixingHistogram(String path, String basename,
			ArrayList<ArrayList<Double>> tmp, double binWidth, String tag) {
		try {
			PrintWriter outputStream = new PrintWriter(new FileWriter(path+"Statistics\\Texts\\"+basename+"DemixingHistogram"+tag+".txt"));
			outputStream.println("Histogram of distribution of angles of the intensity ratios, first row angle second row count (bin with is: "+binWidth+")");
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
		System.out.println("Histogram of localizations per frame saved.");
	}
	
	public static void writeLocalizationEstimationHistogram(String path, String basename,
			ArrayList<ArrayList<Double>> histXY,ArrayList<ArrayList<Double>> histZ, double binWidth, String tag) {
		try {
			PrintWriter outputStream = new PrintWriter(new FileWriter(path+"Statistics\\Texts\\"+basename+"LocalizationEstimationHistogram"+tag+".txt"));
			outputStream.println("Histogram of distances between consecutive points (bin with is: "+binWidth+")");
			String strFrames= "", strLocsPerFrame= "";
			for (int k = 0; k<histXY.get(0).size(); k=k+1){
				strFrames = strFrames +histXY.get(0).get(k)+" ";
				strLocsPerFrame = strLocsPerFrame +histXY.get(1).get(k)+" ";
			}
			outputStream.println(strFrames);
			outputStream.println(strLocsPerFrame);
			
			strFrames = "";
			strLocsPerFrame = "";
			for (int k = 0; k<histZ.get(0).size(); k=k+1){
				strFrames = strFrames +histZ.get(0).get(k)+" ";
				strLocsPerFrame = strLocsPerFrame +histZ.get(1).get(k)+" ";
			}
			outputStream.println(strFrames);
			outputStream.println(strLocsPerFrame);
			
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		System.out.println("Histogram of localizations per frame saved.");
	}
	
	public static void writeDriftLog(ArrayList<double[][]> dds, UnivariateFunction fx, 
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
	
	public static void writeDemixingOutput(String path, String basename, ArrayList<StormLocalization> ch1, 
			ArrayList<StormLocalization> ch2 ,ArrayList<StormLocalization> utch1, String tag){
		try {
			PrintWriter outputStream = new PrintWriter(new FileWriter(
					path+"Statistics\\Texts\\"+basename+"DemixingStatistic"+tag+".txt"));
			outputStream.print("Automatically generated log file for the pairwise occuring points.  ");
			outputStream.println("Structure: channel 1 channel2 untransformed channel 1");
			for (int j = 0;j<ch1.size();j++){
					outputStream.print(ch1.get(j).toPlainString()+" ");
					outputStream.print(ch2.get(j).toPlainString()+" ");
					outputStream.print(utch1.get(j).toPlainString());
					outputStream.println();
			}
			
			outputStream.close();
			System.out.println("Demixing statistics written");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public static void writeConnectionResult(String path, String basename,
			int counter, int size, String tag) {
		try {
			PrintWriter outputStream = new PrintWriter(new FileWriter(path+"Statistics\\Texts\\"+basename+"ConnectionStatistic"+tag+".txt"));
			outputStream.println("Number of localizations after connecting: "+counter);
			outputStream.println("Number of localizations before connecting: "+size);
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static String saveImgHist(String path, String basename, String tag,ArrayList<ArrayList<Double>> histData, 
			String datalabel, String xlabel, String ylabel, String title,String kindOfHistogram){
		String picname = basename+"_Histogram_"+kindOfHistogram+tag+".png";
		String fullFilename = path+"Statistics\\Pictures\\"+picname;
		new File(path + "Statistics\\Pictures").mkdir();
		CreateScatterPlot.createScatterPlotSingle(histData.get(0), histData.get(1),datalabel, xlabel,  ylabel,  title, fullFilename);
		return fullFilename;
	}
	
	public static void writeDriftLogFile(ArrayList<double[][]> dds, UnivariateFunction fx, UnivariateFunction fy, UnivariateFunction fz, String path, String basename, int frameMax, String tag){
		try {
			int nbrChunks = dds.get(0)[0].length-1;
			PrintWriter outputStream = new PrintWriter(new FileWriter(path+"Statistics\\Texts\\"+basename+"DriftLog"+tag+".txt"));
			outputStream.println("Automatically generated log file for drift correction");
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
			outputStream.println("Matrix of chunkwise drift Z");
			for (int j = 0;j<nbrChunks;j++){
				for (int jj = 0;jj<nbrChunks;jj++){
					outputStream.print(dds.get(3)[j][jj]+" ");
				}
				outputStream.println();
			}
			String strFrames= "", strDriftX= "", strDriftY = "", strDriftZ = "";
			double maxDriftX = 0;
			double maxDriftY = 0;
			double maxDriftZ = 0;
			for (int k = 0; k<frameMax; k=k+100){
				strFrames = strFrames +k+" ";
				strDriftX = strDriftX +fx.value(k)+" ";
				strDriftY = strDriftY +fy.value(k)+" ";
				strDriftZ = strDriftZ +fz.value(k)+" ";
				maxDriftX = Math.max(maxDriftX, fx.value(k));
				maxDriftY = Math.max(maxDriftY, fy.value(k));
				maxDriftZ = Math.max(maxDriftZ, fz.value(k));
			}
			outputStream.println(strFrames);
			outputStream.println(strDriftX);
			outputStream.println(strDriftY);
			outputStream.println(strDriftZ);
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

	public static String saveDriftGraph(String path, String basename, String tag,
			ArrayList<Integer> frames, UnivariateFunction fx,
			UnivariateFunction fy, int pixelsize) {
		String picname = basename+"_DriftData_"+tag+".png";
		String fullFilename = path+"Statistics\\Pictures\\"+picname;
		ArrayList<ArrayList<ArrayList<Double>>> data = new ArrayList<ArrayList<ArrayList<Double>>>();
		data.add(new ArrayList<ArrayList<Double>>());
		data.add(new ArrayList<ArrayList<Double>>());
		ArrayList<Double> dFrames = new ArrayList<Double>();
		ArrayList<Double> xdrift = new ArrayList<Double>();
		ArrayList<Double> ydrift = new ArrayList<Double>();
		for (int i = 0;i<frames.size(); ++i){
			dFrames.add((double)frames.get(i));
			xdrift.add(fx.value(frames.get(i))*pixelsize);
			ydrift.add(fy.value(frames.get(i))*pixelsize);
		}
		data.get(0).add(dFrames);
		data.get(0).add(xdrift);
		data.get(1).add(dFrames);
		data.get(1).add(ydrift);
		ArrayList<String> datalabels = new ArrayList<String>();
		datalabels.add("x");
		datalabels.add("y");
		
		CreateScatterPlot.createScatterPlot2(data, datalabels, "frame", "drift in nm", "Overview drift x\\y over frames", fullFilename);
		
		return fullFilename;
		// TODO Auto-generated method stub
	}
	
	public static String saveDriftGraph(String path, String basename, String tag,
			ArrayList<Integer> frames, UnivariateFunction fx,
			UnivariateFunction fy, UnivariateFunction fz,int pixelsize) {
		String picname = basename+"_DriftData_"+tag+".png";
		String fullFilename = path+"Statistics\\Pictures\\"+picname;
		ArrayList<ArrayList<ArrayList<Double>>> data = new ArrayList<ArrayList<ArrayList<Double>>>();
		data.add(new ArrayList<ArrayList<Double>>());
		data.add(new ArrayList<ArrayList<Double>>());
		data.add(new ArrayList<ArrayList<Double>>());
		ArrayList<Double> dFrames = new ArrayList<Double>();
		ArrayList<Double> xdrift = new ArrayList<Double>();
		ArrayList<Double> ydrift = new ArrayList<Double>();
		ArrayList<Double> zdrift = new ArrayList<Double>();
		for (int i = 0;i<frames.size(); ++i){
			dFrames.add((double)frames.get(i));
			xdrift.add(fx.value(frames.get(i))*pixelsize);
			ydrift.add(fy.value(frames.get(i))*pixelsize);
			zdrift.add(fz.value(frames.get(i))*pixelsize);
		}
		data.get(0).add(dFrames);
		data.get(0).add(xdrift);
		data.get(1).add(dFrames);
		data.get(1).add(ydrift);
		data.get(2).add(dFrames);
		data.get(2).add(zdrift);
		ArrayList<String> datalabels = new ArrayList<String>();
		datalabels.add("x");
		datalabels.add("y");
		datalabels.add("z");
		
		CreateScatterPlot.createScatterPlot2(data, datalabels, "frame", "drift in nm", "Overview drift x\\y over frames", fullFilename);
		
		return fullFilename;
		// TODO Auto-generated method stub
	}
	
	public static String saveMulticolorPlot(String path, String basename, String tag, String nameOfPicture,String xtitle, String ytitle, String title, ArrayList<ArrayList<ArrayList<Double>>> data, ArrayList<String> datalabels){
		String picname = basename+nameOfPicture+tag+".png";
		String fullFilename = path+"Statistics\\Pictures\\"+picname;
		CreateScatterPlot.createScatterPlot2(data, datalabels, xtitle, ytitle, title, fullFilename);
		return fullFilename;
	}
	
	public static String savePlot(String path, String basename, String tag, ArrayList<ArrayList<Double>> data, String datalabel, 
			String xlabel, String ylabel, String title,String kindOfPlot){
		String picname = basename+"_Plot_"+kindOfPlot+tag+".png";
		String fullFilename = path+"Statistics\\Pictures\\"+picname;
		CreateScatterPlot.createScatterPlotSingle(data.get(0), data.get(1),datalabel, xlabel,  ylabel,  title, fullFilename);
		return fullFilename;
	}

	public static void createPDF(ArrayList<Object> logs, String path, String basename, String tag) {
		//use latex to combine all information in one pdf file
		try {
			PrintWriter outputStream = new PrintWriter(new FileWriter(path+"Statistics\\ProcessingLogOf_"+basename+tag+".tex"));
			outputStream.println("\\documentclass[a4paper,12pt,twoside]{book}");
			outputStream.println("\\usepackage[english]{babel}");
			outputStream.println("\\usepackage[utf8]{inputenc}");
			outputStream.println("\\pagestyle{headings}");
			outputStream.println("\\usepackage{graphicx}");
			outputStream.println("\\usepackage{mathcomp}");
			outputStream.println("\\usepackage{amsmath}");
			outputStream.println("\\usepackage{float}");
			outputStream.println("\\begin{document}");
			outputStream.println("\\noindent");
			for(int i = 0; i<logs.size(); i++){
				outputStream.print(((BasicProcessingInformation)logs.get(i)).toLatexString());
				//outputStream.print("\\newline\n");
			}
			outputStream.println("\\end{document}");
			outputStream.close();
			Runtime rt = Runtime.getRuntime();
			Process proc = rt.exec("pdflatex -output-directory="+path+"Statistics\\ "+path+"Statistics\\ProcessingLogOf_"+basename+tag+".tex");
			BufferedReader input = new BufferedReader(new InputStreamReader(proc.getInputStream()));
			String line = null;
			while ((line = input.readLine()) != null){
				//System.out.println(line);
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void saveLocalLocalizationPrecision(double[][] lLP, String path, String basename, String tag){
		try {
			String output = "";
			PrintWriter outputStream = new PrintWriter(new FileWriter(path+"Statistics\\LocalLocalizationPrecision_"+basename+tag+".txt"));
			for(int i = 0; i<lLP.length; i++){
				for (int j=0; j<lLP[i].length; j++){
					outputStream.print(lLP[i][j]+" ");
				}
				outputStream.print("\n");
			}
			outputStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

