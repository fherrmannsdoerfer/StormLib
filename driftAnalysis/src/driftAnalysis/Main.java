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
		
		String path1 = "C:\\Users\\herrmannsdoerfer\\Desktop\\moverVglut-10000\\Auswahl\\";
		String tag = "141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4";
		//String path1 = "Z:\\MessungenNeuesSetup\\"+tag+"\\Auswertung\\RapidStorm\\";
		//twoColorRegistration(path1,"LeftChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt", path1, "RightChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt");
		//twoColorRegistration(path1, "Cell2 - 0 min - 488 -_2_MMImages-undrift.txt", path1, "Cell2 - 0 min - 647 -_1_MMImages-undrift.txt");
		singleColor2dImage("C:\\Users\\herrmannsdoerfer\\Desktop\\StormData\\","DataFrankfurt2dMicrotubules"+".txt");
		//singleColor3dImage(path1,"LeftChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt");

		//dualColor2dImage(path1, "LeftChannel"+tag+".txt", path1, "RightChannel"+tag+".txt");
		//twoColorRegistrationMultipleFiles(path1, "141204Mover-Int10000", path1, "141204vglut1-Int10000");
		//twoColorRegistration(path1, "141204Mover-Int10000.txt", path1, "141204vglut1-Int10000.txt");
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
		channels.get(0).renderImage2D(10);
		channels.get(1).renderImage2D(10);
	}
	
	static void twoColorRegistration(String path1, String fname1, String path2, String fname2){
		StormData sd1 = new StormData(path1, fname1);
		StormData sd2 = new StormData(path2, fname2);
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
		sd.connectPoints(100, 100, 150, 3);
		sd.estimateLocalizationPrecision(100, 100);
		sd.createPdf();
		sd.renderImage3D(10);
		sd.connectPoints(100, 100, 150, 3);
		sd.renderImage3D(10);
		sd.writeArrayListForVisp("");
		//sd.correctDrift(4000);
		//sd.renderImage3D(10);
	}
	
	static void singleColor2dImage(String path, String fname){
		StormData sd = new StormData(path, fname);
		sd.estimateLocalLocalizationPrecision2(100, 100, 4000, 4000, 100, 100);
		sd.renderImage2D(10);
		sd.estimateLocalizationPrecision(100, 200);
		sd.createPdf();
		sd.connectPoints(100, 100, 150, 3);
		sd.renderImage2D(10);
		sd.getLocsPerFrame();
		sd.correctDrift((int)Math.ceil((double)sd.getDimensions().get(7)/5));
		sd.renderImage2D(10);
		sd.getLocsPerFrame();
	}
	
	static void dualColor2dImage(String path1, String fname1, String path2, String fname2){
		StormData sd1 = new StormData(path1,fname1);
		sd1.getLocsPerFrame();
		sd1.correctDrift(5000);
		//sd1.connectPoints(100., 100., 150, 3);
		sd1.renderImage2D(10);
		sd1.estimateLocalizationPrecision(50, 900);
		sd1.createPdf();
		StormData sd2 = new StormData(path2,fname2);
		sd2.estimateLocalizationPrecision(50, 900);
		//sd2.correctDrift(5000);
		//sd2.connectPoints(100., 100., 150, 3);
		sd2.renderImage2D(10);

		sd2.createPdf();
		StormData unmixedSd = Demixing.spectralUnmixing(sd1, sd2);
		unmixedSd.estimateLocalizationPrecision(50, 900);
		unmixedSd.correctDrift(5000);
		unmixedSd.connectPoints(60, 60, 120, 2);
		unmixedSd.estimateLocalizationPrecision(50, 300);
		DemixingParameters demixingParams= new DemixingParameters((44)/180. * Math.PI,
				(67)/180.*Math.PI, 20/180.*Math.PI, 15/180.*Math.PI);
		ArrayList<ImagePlus> colImg = unmixedSd.renderDemixingImage(10, demixingParams);
		unmixedSd.createPdf();
	}
}