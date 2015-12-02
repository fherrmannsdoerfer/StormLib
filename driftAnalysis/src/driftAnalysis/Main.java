package driftAnalysis;

import functions.BeadRegistration;
import functions.Demixing;
import functions.FeatureBasedDriftCorrection;
import ij.ImagePlus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import dataStructure.DemixingParameters;
import dataStructure.StormData;
import dataStructure.StormLocalization;
import StormLib.OutputClass;
import StormLib.Utilities;
import StormLib.HelperClasses.BasicProcessingInformation;



public class Main {
//
	public static void main(String[] args) {
//		File folder = new File("Y:\\Users_shared\\SuReSim-Software Project\\SuReSim Rebuttal\\Simulierte Epitope aus Ingmars Paper\\outputTiffStackAuto\\randomEpitopes30msdol13000Photons\\");
//		File[] files = folder.listFiles();
//		for (File file : files){
//			if (file.getName().contains("protocol")){
//				
//			}
//			else if(file.getName().contains(".txt")){
//				System.out.println(file.getParentFile());
//				System.out.println(file.getName());
//				System.out.println(file.getParentFile()+"\\"+file.getName().substring(0, file.getName().length()-4)+"renderd.tif");
//				StormData sd1 = new StormData(file.getParentFile()+"\\",file.getName());
//				sd1.shift(0,-24*133);
//				sd1.shift(1,-24*133);
//				ImagePlus imgP = sd1.renderImage2D(2.5, false, "", 0, -1, 6);
//				OutputClass.save2DImage(file.getParentFile()+"\\"+file.getName().substring(0, file.getName().length()-4)+"renderd.tif", imgP);
//			}
//		}
//		
		//
		
//		String tag = "sig6_15labEff10duty0AngDev0LabLen16ver0TiffStack.txt";
//		//String path1 = "D:\\MessungenTemp\\"+tag+"\\Auswertung\\RapidStorm\\";
//		
////		String path1 = "D:\\MessungenTemp\\"+tag+"\\Auswertung\\ThunderStorm\\";
//
//		String path1 = "Y:\\Users_shared\\SuReSim-Software Project\\SuReSim Rebuttal\\Fire\\Simulation Data\\Vesicles\\TiffStacks\\3800PhotonenNichtEinzelnAlsoRealistisch\\sig6_15labEff10duty0AngDev0LabLen16ver0\\";
//
////String path1 = "D:\\141219-RapidStorm-SynPhys-Phalloidin\\";
	String path1 = "C:\\Users\\herrmannsdoerfer\\Desktop\\V16-4NeueSimulation\\-600-22Noise2700Photonen\\2std\\";
//		
//		String tag2 = "pt001";
////		String path1 = "D:\\ery-exp\\";
//		//String tag2 ="Nup133colony12-int2500.txt";
//		//twoColorRegistration(path1,"LeftChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt", path1, "RightChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt");
////		singleColor3dMultipleInput(path1, "LeftChannel");
//		//demixingMultipleInputFiles(path1,"LeftChannel",path1,"RightChannel");
//
//		//dualColor(path1, "LeftChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt", path1, "RightChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt");
//		//dualColor(path1, "LeftChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt", path1, "RightChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt");
//		//singleColor3dImage(path1,tag+tag2+".txt");
//		singleColor3dImage(path1,tag);
//		//createVispOutput(path1,"LeftChannel"+tag+".txt");
//		//singleColor3dImage(path1,"LeftChannel"+tag+tag2+".txt");
		singleColor3dMultipleInput(path1,"");
////		singleColor3dImage(path1,"7StandardDeviations.txt");
// dualColor(path1, "LeftChannel"+tag+tag2+".txt", path1, "RightChannel"+tag+tag2+".txt");
		//String fname = "SelfMeassuredloa15.00aoa1.57bspnm1.65pabs0.10abpf14.00rof12.00sxy8.00sz35.00bspsnm0.01_MalkOutput.txt";
		//createVispOutput("D:\\MessungenTemp\\150705Phalloidin647-NativeErythrocytesMessung7\\Messung1\\Auswertung\\ThunderStorm\\","LeftChannel150705Phalloidin647-NativeErythrocytesMessung7.txt");
		//singleColor3dImage(path1,tag2);
//		singleColor3dImage(path1,"LeftChannel150512ErythrocytesPhalloidinAlexa647Messung1Thunderstorm.txt");
		//singleColor2dImage(path1,tag);
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
		StormData sd = Utilities.openSeries(path, pattern);
//		sd.cropCoords(0, 60000, 0, 60000);
//		sd.renderImage3D(10);
////		sd.writeArrayListForFRC("native");
////		sd.correctDrift((int)Math.ceil((double)sd.getDimensions().get(7)/7 ));
//		sd.writeArrayListForVisp();
		sd.estimateLocalizationPrecision(100, 100);
		sd.connectPoints(100, 100, 150, 3);
		sd.estimateLocalizationPrecision(100, 100);
		sd.writeArrayListForFRC("");
		sd.writeArrayListForVisp("");
		//sd.correctDrift(4000);
		sd.renderImage3D(10);
//		sd.writeLocs();
//		sd.createPdf();
//		sd.writeArrayListForFRC();
//		sd.cropCoords(0, 17000, 7199, 30607, 0, 1000);
//		sd.writeArrayListForFRC("connectedandzcroppedxyandz0-1000");
//		sd.writeArrayListForVisp("connectedandzcropped0-1000");
//		sd.cropCoords(0, 17000, 7199, 30607, 15, 210);
		sd.writeArrayListForFRC("connected");
		sd.writeArrayListForVisp("connected");
	}
	
	static void demixingMultipleInputFiles(String path1, String pattern1, String path2, String pattern2){
		ArrayList<StormData> list = Utilities.openSeries(path1, pattern1, path2, pattern2);
		StormData sd1 = list.get(0);
		StormData sd2 = list.get(1);
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
		
		
		//sd1.getLocsPerFrame();
		//sd1.cropCoords(5000, 6000, 5000, 6000);
		//System.out.println("maxFrame ch1"+sd1.getDimensions().get(7));
		//sd1.correctDrift(5000);
		//sd1.connectPoints(50., 50., 150, 3);
		//sd1.renderImage2D(10);
		//sd1.estimateLocalizationPrecision(50, 900);
		//sd1.createPdf();

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
			try{
				unmixedChannels.add(Demixing.spectralUnmixing(chunksChannel1.get(i), chunksChannel2.get(i),false,""+i));
			}
			catch (Exception e){
				unmixedChannels.add(new StormData());
			}
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
		System.out.println("total number of partwise demixed localizations: "+unmixedFromParts.getSize()+" of "+Math.min(sd1.getSize(),sd2.getSize()));
		System.out.println("total number of partwise demixed localizations in %: "+100*unmixedFromParts.getSize()/Math.min(sd1.getSize(),sd2.getSize()));
		StormData unmixedSd = unmixedFromParts;
		unmixedSd.estimateLocalizationPrecision(50, 900);
		//unmixedSd.correctDrift((int)Math.ceil((double)unmixedSd.getDimensions().get(7)/5));

		unmixedSd.connectPoints(50, 50, 100, 2);
		unmixedSd.estimateLocalizationPrecision(50, 300);
		DemixingParameters demixingParams= new DemixingParameters((42)/180. * Math.PI,
				(70)/180.*Math.PI, 20/180.*Math.PI, 20/180.*Math.PI);
		unmixedSd.estimateLocalizationPrecision(50, 300,demixingParams);
		unmixedSd.estimateLocalizationPrecision(50, 300,"testTag",demixingParams);
		
		ArrayList<ImagePlus> colImg = unmixedSd.renderDemixingImage(10, demixingParams);
		unmixedSd.writeArrayListForVisp(demixingParams);
		unmixedSd.correctDrift((int)Math.ceil((double)unmixedSd.getDimensions().get(7)/5));
		//unmixedSd.cropCoords(0, 100000, 0, 100000, 380, 680, 0, 10000);
		colImg = unmixedSd.renderDemixingImage(10, demixingParams);
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
		sd.estimateLocalizationPrecision(200, 200);
		//sd.writeArrayListForFRC();
		sd.connectPoints(100, 100, 150, 3);
		sd.writeArrayListForFRC("connected");

		//sd.correctDrift((int)Math.ceil((double)sd.getDimensions().get(7)/5 ));
		//sd.writeArrayListForFRC("connectedanddrift");
		//sd.connectPoints(100, 100, 150, 3);
	//	sd.writeArrayListForFRC("connected");
	//	sd.estimateLocalizationPrecision(100, 100);
		//sd.cropCoords(-1580, -940, -200, 440);
		//sd.correctDrift((int)Math.ceil((double)sd.getDimensions().get(7)/3 ));8
		//sd.connectPoints(100, 100, 150, 3);
	//sd.renderImage3D(10);
	//sd.writeArrayListForFRC("ConDC");
		
		sd.writeArrayListForVisp("connected");
		//sd.correctDrift(4000);
	//	sd.writeArrayListForFRC("drift");
		//sd.cropCoords(0, 100000, 0, 100000, 300, 600, 0, 10000);
		
	//	sd.writeArrayListForVisp("conDCcropped300-600-0-10000");
	
		//sd.cropCoords(4960, 5600, 8660, 9300, 200, 550, 0, 60000);
		
		//sd.writeArrayListForFRC("driftandcropped10000frames");
		//sd.writeArrayListForVisp();
		//sd.renderImage3D(10,"cropped200-500NewColor");
		//sd.createPdf();
		//sd.writeArrayListForFRC("conDCcropped300-600-0-10000");
		//sd.renderImage3D(10);
	}
	
	static void singleColor2dImage(String path, String fname){
		StormData sd = new StormData(path, fname);
		
		//sd.estimateLocalizationPrecision(100, 300);
		//sd.renderImage2D(10);
		//sd.cropCoords(0, 100000, 0, 100000, 0, 900, 0, 10000);
		//sd.createPdf();
		//sd.connectPoints(50, 50, 150, 3);
		//sd.renderImage2D(10);
		//sd.getLocsPerFrame();
		//sd.correctDrift((int)Math.ceil((double)sd.getDimensions().get(7)/3));
		//sd.writeArrayListForVisp();
		//sd.renderImage2D(10);
		//sd.cropCoords(0, 100000, 0, 100000, -500, 900, 0, 10000);
		sd.estimateLocalizationPrecision(100, 150);
		sd.renderImage2D(10);
		sd.writeArrayListForFRC();
	//sd.getLocsPerFrame();
		//sd.createPdf();
		
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
		
		
		//sd1.getLocsPerFrame();
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
			unmixedChannels.add(Demixing.spectralUnmixing(chunksChannel1.get(i), chunksChannel2.get(i),false,""+i));
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
		//unmixedSd.correctDrift((int)Math.ceil((double)unmixedSd.getDimensions().get(7)/5));

		//unmixedSd.connectPoints(20, 20, 120, 2);
		unmixedSd.estimateLocalizationPrecision(50, 300);
		DemixingParameters demixingParams= new DemixingParameters((40)/180. * Math.PI,
				(67)/180.*Math.PI, 20/180.*Math.PI, 20/180.*Math.PI);
		unmixedSd.estimateLocalizationPrecision(50, 300,demixingParams);
		unmixedSd.estimateLocalizationPrecision(50, 300,"testTag",demixingParams);
		ArrayList<ImagePlus> colImg = unmixedSd.renderDemixingImage(10, demixingParams);
		unmixedSd.writeArrayListForVisp(demixingParams);
		unmixedSd.correctDrift((int)Math.ceil((double)unmixedSd.getDimensions().get(7)/5));
		//unmixedSd.cropCoords(0, 100000, 0, 100000, 380, 680, 0, 10000);
		colImg = unmixedSd.renderDemixingImage(10, demixingParams);
		unmixedSd.writeArrayListForVisp(demixingParams);
		unmixedSd.writeArrayListForFRC(demixingParams);
	
		unmixedSd.cropCoords(0, 100000, 0, 100000, 0, 900, 0, 10000);
		unmixedSd.writeArrayListForVisp("cropped0-10000frames");
		unmixedSd.writeArrayListForFRC("cropped0-10000frames");
		unmixedSd.cropCoords(0, 100000, 0, 100000, 300, 600, 0, 10000);
		unmixedSd.writeArrayListForVisp("cropped0-10000frames-0-300z");
		unmixedSd.writeArrayListForFRC("cropped0-10000frames0-300z");
	
	}
}
