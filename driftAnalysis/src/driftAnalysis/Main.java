package driftAnalysis;

import ij.ImagePlus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import StormLib.BeadRegistration;
import StormLib.Demixing;
import StormLib.DemixingParameters;
import StormLib.FeatureBasedDriftCorrection;
import StormLib.StormData;
import StormLib.StormLocalization;
import StormLib.Utilities;
import StormLib.HelperClasses.BasicProcessingInformation;



public class Main {

	public static void main(String[] args) {
		
		String tag = "150703_Calyx3b_TomCF680_VGlut647_Gain20_ExpTime120";
		String path1 = "D:\\MessungenTemp\\"+tag+"\\Auswertung\\RapidStorm\\";
		
		//String tag = "150612BeadNo3DMessung1";
		//String path1 = "D:\\Nup\\Int2000\\";
		String tag2 ="pt1";
		//twoColorRegistration(path1,"LeftChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt", path1, "RightChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt");
		//demixingMultipleInputFiles(path1,"LeftChannel",path1,"RightChannel");
		dualColor(path1, "LeftChannel"+tag+tag2+".txt", path1, "RightChannel"+tag+tag2+".txt");
		//singleColor2dImage(path1,"Nup 133 colony 12Int1000"+".txt");
		//createVispOutput(path1,"LeftChannel"+tag+".txt");
		//singleColor3dImage(path1,"LeftChannel"+tag+tag2+".txt");
		//singleColor3dMultipleInput(path1,"LeftChannel");
		//singleColor3dImage(path1,"Concatenated Stacks150508nativerythrocytesphalloidin647.txt");
		//dualColor2dImage(path1, "LeftChannel"+tag+tag2+".txt", path1, "RightChannel"+tag+tag2+".txt");
		//String fname = "SelfMeassuredloa15.00aoa1.57bspnm1.65pabs0.10abpf14.00rof12.00sxy8.00sz35.00bspsnm0.01_MalkOutput.txt";
		//createVispOutput("D:\\MessungenTemp\\150701ErythrocytesPhalloidin647Messung12\\LeftChannel\\","LeftChannel150701ErythrocytesPhalloidin647Messung12pt1.txt");
																																														 
	}
	
	static void createVispOutput(String path, String fname){
		StormData sd = new StormData(path,fname);
		//sd.correctDrift(5050);
		sd.writeArrayListForVisp();
		sd.renderImage3D(10);
	}
	
	static void renderSimulationResult(String path, String fname){
		StormData sd1 = new StormData(path,fname);
		sd1.renderImage2D(10);
		sd1.estimateLocalizationPrecision(100, 200);
	}
	
	static void singleColor3dMultipleInput(String path, String pattern){
		ArrayList<StormData> list = Utilities.openSeries(path, pattern, "lol", "undso");
		StormData sd = list.get(0);
		sd.renderImage3D(10);
		sd.correctDrift((int)Math.ceil((double)sd.getDimensions().get(7)/7 ));
		sd.connectPoints(100, 100, 150, 3);
		sd.estimateLocalizationPrecision(100, 100);
		
		sd.writeArrayListForVisp("");
		//sd.correctDrift(4000);
		sd.renderImage3D(10);
		sd.createPdf();
	}
	
	static void demixingMultipleInputFiles(String path1, String pattern1, String path2, String pattern2){
		ArrayList<StormData> list = Utilities.openSeries(path1, pattern1, path2, pattern2);
		StormData sd1 = list.get(0);
		StormData sd2 = list.get(1);
		sd1.getLocsPerFrame();

		//System.out.println("maxFrame ch1"+sd1.getDimensions().get(7));
		//sd1.correctDrift(5000);
		//sd1.connectPoints(100., 100., 150, 3);
		sd1.renderImage2D(10);
		sd1.estimateLocalizationPrecision(50, 900);
		sd1.createPdf();
		//System.out.println("maxFrame ch2"+sd2.getDimensions().get(7));
		sd2.estimateLocalizationPrecision(50, 900);
		//sd2.correctDrift(5000);
		//sd2.connectPoints(100., 100., 150, 3);
		sd2.renderImage2D(10);

		sd2.createPdf();
		StormData unmixedSd = Demixing.spectralUnmixing(sd1, sd2,false);
		unmixedSd.estimateLocalizationPrecision(50, 900);

		unmixedSd.correctDrift((int)Math.ceil((double)unmixedSd.getDimensions().get(7)/12));

		unmixedSd.connectPoints(60, 60, 120, 2);
		unmixedSd.estimateLocalizationPrecision(50, 300);
		DemixingParameters demixingParams= new DemixingParameters((44)/180. * Math.PI,
				(67)/180.*Math.PI, 20/180.*Math.PI, 15/180.*Math.PI);
		ArrayList<ImagePlus> colImg = unmixedSd.renderDemixingImage(10, demixingParams);
		unmixedSd.writeArrayListForVisp(demixingParams);
		unmixedSd.createPdf();
	}
	
	static void twoColorRegistrationMultipleFiles(String path1,String pattern1,String path2,String pattern2){
		ArrayList<StormData> list = Utilities.openSeries(path1, pattern1, path2, pattern2);
		StormData sd1 = list.get(0);
		StormData sd2 = list.get(1);
		sd1.renderImage2D(10);
		sd1.connectPoints(40, 40, 100, 3);
		sd1.correctDrift((int)Math.ceil((double)sd1.getDimensions().get(7)/5));
		sd1.renderImage2D(10);
		sd1.getLocsPerFrame();
		
		sd2.renderImage2D(10);
		sd2.connectPoints(40, 40, 100, 3);
		sd2.correctDrift((int)Math.ceil((double)sd2.getDimensions().get(7)/5));
		sd2.renderImage2D(10);
		sd2.getLocsPerFrame();
		
		ArrayList<StormData> channels = BeadRegistration.doRegistration(sd1,sd2);
		channels.get(0).renderImage2D(10,"alignedCH1");
		channels.get(1).renderImage2D(10,"alignedCH1");
	}
	
	static void twoColorRegistration(String path1, String fname1, String path2, String fname2){
		StormData sd1 = new StormData(path1, fname1);
		StormData sd2 = new StormData(path2, fname2);
		sd1.renderImage2D(10);
		sd1.connectPoints(40, 40, 100, 3);
		sd1.correctDrift((int)Math.ceil((double)sd1.getDimensions().get(7)/5));
		sd1.renderImage2D(10);
		sd1.getLocsPerFrame();
		sd1.createPdf();
		
		sd2.renderImage2D(10);
		sd2.connectPoints(40, 40, 100, 3);
		sd2.correctDrift((int)Math.ceil((double)sd2.getDimensions().get(7)/5));
		sd2.renderImage2D(10);
		sd2.getLocsPerFrame();
		sd2.createPdf();
		
		ArrayList<StormData> channels = BeadRegistration.doRegistration(sd1,sd2);
		channels.get(0).renderImage2D(10,true,"ch1");
		channels.get(1).renderImage2D(10,true,"ch1");
		channels.get(0).writeLocs();
		channels.get(1).writeLocs();
	}
	
	static void driftCorrectionTest(){
		StormData sd = new StormData();
		sd.setFname("driftcorrTest.txt");
		sd.setPath("C:\\tmp2\\");
		sd.createLineSample(500, 300, 100, 20000);
		sd = FeatureBasedDriftCorrection.correctDrift(sd, 2000);
	}
	static void singleColor3dImage(String path, String fname){
		StormData sd = new StormData(path, fname);
		
		sd.renderImage3D(10);
		sd.correctDrift((int)Math.ceil((double)sd.getDimensions().get(7)/3 ));
		sd.connectPoints(100, 100, 150, 3);
		sd.estimateLocalizationPrecision(100, 100);
		
		sd.writeArrayListForVisp("");
		//sd.correctDrift(4000);
		sd.renderImage3D(10);
		sd.createPdf();
	}
	
	static void singleColor2dImage(String path, String fname){
		StormData sd = new StormData(path, fname);
		//sd.estimateLocalLocalizationPrecision2(100, 100,2000, 2000, 50, 50);

		sd.renderImage2D(10);
		//sd.estimateLocalizationPrecision(100, 200);
		//sd.createPdf();
		//sd.connectPoints(50, 50, 150, 3);
		//sd.renderImage2D(10);
		//sd.getLocsPerFrame();
		sd.correctDrift((int)Math.ceil((double)sd.getDimensions().get(7)/5));
		sd.renderImage2D(10);
		sd.getLocsPerFrame();
	}
	
	static void dualColor(String path1, String fname1, String path2, String fname2){
		StormData sd1 = new StormData(path1,fname1);
		int chunkSize = 5000;
		int numberChunks = (int)Math.ceil((double)sd1.getDimensions().get(7)/chunkSize);
		ArrayList<StormData> chunksChannel1 = new ArrayList<StormData>();
		for (int i = 0; i < numberChunks; i++){
			chunksChannel1.add(sd1.findSubset(chunkSize*i,chunkSize*(i+1),false));
		}
		//StormData sd11 = sd1.findSubset(0,5000,false);
		//StormData sd12 = sd1.findSubset(5001,10000,false);
		//StormData sd13 = sd1.findSubset(10001,15000,false);
		//StormData sd14 = sd1.findSubset(15001,20000,false);
		
		
		sd1.getLocsPerFrame();
		//sd1.cropCoords(5000, 6000, 5000, 6000);
		//System.out.println("maxFrame ch1"+sd1.getDimensions().get(7));
		//sd1.correctDrift(5000);
		//sd1.connectPoints(50., 50., 150, 3);
		//sd1.renderImage2D(10);
		//sd1.estimateLocalizationPrecision(50, 900);
		//sd1.createPdf();
		
		
		StormData sd2 = new StormData(path2,fname2);
		ArrayList<StormData> chunksChannel2 = new ArrayList<StormData>();
		for (int i = 0; i < numberChunks; i++){
			chunksChannel2.add(sd2.findSubset(chunkSize*i,chunkSize*(i+1),false));
		}
		/*
		StormData sd21 = sd2.findSubset(0,5000,false);
		StormData sd22 = sd2.findSubset(5001,10000,false);
		StormData sd23 = sd2.findSubset(10001,15000,false);
		StormData sd24 = sd2.findSubset(15001,20000,false);
		*/
		
		//sd2.cropCoords(10000, 15000, 10000, 15000);
		//System.out.println("maxFrame ch2"+sd2.getDimensions().get(7));
		//sd2.estimateLocalizationPrecision(50, 900);
		//sd2.correctDrift(5000);
		//sd2.connectPoints(50., 50., 150, 3);
		//sd2.renderImage2D(10);
		
		ArrayList<StormData> unmixedChannels = new ArrayList<StormData>();
		for (int i = 0; i < numberChunks; i++){
			unmixedChannels.add(Demixing.spectralUnmixing(chunksChannel1.get(i), chunksChannel2.get(i),false));
		}
		StormData unmixedFromParts = new StormData();
		for (int i = 0; i < numberChunks; i++){
			unmixedFromParts.addStormData(unmixedChannels.get(i));
		}
		/*
		StormData unmixedSd1 = Demixing.spectralUnmixing(sd11, sd21,false);
		StormData unmixedSd2 = Demixing.spectralUnmixing(sd12, sd22,false);
		StormData unmixedSd3 = Demixing.spectralUnmixing(sd13, sd23,false);
		StormData unmixedSd4 = Demixing.spectralUnmixing(sd14, sd24,false);
		
		
		StormData unmixedFromParts = new StormData();
		unmixedFromParts.addStormData(unmixedSd1);
		unmixedFromParts.addStormData(unmixedSd2);
		unmixedFromParts.addStormData(unmixedSd3);
		unmixedFromParts.addStormData(unmixedSd4);
		System.out.println("total number of partwise demixed localizations: "+unmixedFromParts.getSize());
		*/
		
		//sd2.createPdf();
		//StormData unmixedSd = Demixing.spectralUnmixing(sd1, sd2,false);
		System.out.println("total number of partwise demixed localizations: "+unmixedFromParts.getSize());
		StormData unmixedSd = unmixedFromParts;
		unmixedSd.estimateLocalizationPrecision(50, 900);

		unmixedSd.correctDrift((int)Math.ceil((double)unmixedSd.getDimensions().get(7)/5));

		unmixedSd.connectPoints(20, 20, 120, 2);
		unmixedSd.estimateLocalizationPrecision(50, 300);
		DemixingParameters demixingParams= new DemixingParameters((40)/180. * Math.PI,
				(65)/180.*Math.PI, 20/180.*Math.PI, 20/180.*Math.PI);
		ArrayList<ImagePlus> colImg = unmixedSd.renderDemixingImage(10, demixingParams);
		unmixedSd.writeArrayListForVisp(demixingParams);
		unmixedSd.createPdf();
	}
}
