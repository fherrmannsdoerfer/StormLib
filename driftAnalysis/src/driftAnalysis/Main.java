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

 
		String tag = "150212MitochondriaCF680CosMessung2";
		String addition = "";//"_cropped";
		//String path1 = "D:\\MessungenTemp\\"+tag+"\\Auswertung\\RapidStorm\\";
		String path1 = "D:\\Mover vglut1-140216\\";
		//twoColorRegistration(path1,"meos-storm.txt", path1, "mover-storm.txt");
		twoColorRegistration(path1, "141204Mover.txt", path1, "141204Vglut.txt");
		//singleColor2dImage(path1,"LeftChannel"+tag+".txt");
		//singleColor3dImage(path1, "RightChannel"+tag+addition+".txt");

		//dualColor2dImage(path1, "LeftChannel"+tag+addition+".txt", path1, "RightChannel"+tag+addition+".txt");
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
		channels.get(0).renderImage2D(10,"alignedCH1");
		channels.get(1).renderImage2D(10,"alignedCH1");
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
		sd.correctDrift(4000);
		sd.renderImage3D(10);
		sd.writeArrayListForVisp();

	}
	
	static void singleColor2dImage(String path, String fname){
		StormData sd = new StormData(path, fname);
		sd.renderImage2D(10);
		sd.estimateLocalizationPrecision(100, 200);
		sd.createPdf();
		sd.connectPoints(100, 100, 150, 3);
		sd.renderImage2D(10);
		sd.getLocsPerFrame();
		sd.correctDrift(5000);
		sd.renderImage2D(10);
		sd.getLocsPerFrame();
	}
	
	static void dualColor2dImage(String path1, String fname1, String path2, String fname2){
		StormData sd1 = new StormData(path1,fname1);
		sd1.getLocsPerFrame();
		//System.out.println("maxFrame ch1"+sd1.getDimensions().get(7));
		//sd1.correctDrift(5000);
		//sd1.connectPoints(100., 100., 150, 3);
		sd1.renderImage2D(10);
		sd1.estimateLocalizationPrecision(50, 900);
		sd1.createPdf();
		StormData sd2 = new StormData(path2,fname2);
		//System.out.println("maxFrame ch2"+sd2.getDimensions().get(7));
		sd2.estimateLocalizationPrecision(50, 900);
		//sd2.correctDrift(5000);
		//sd2.connectPoints(100., 100., 150, 3);
		sd2.renderImage2D(10);

		sd2.createPdf();
		StormData unmixedSd = Demixing.spectralUnmixing(sd1, sd2);
		unmixedSd.estimateLocalizationPrecision(50, 900);
		unmixedSd.correctDrift((int)Math.ceil((double)unmixedSd.getDimensions().get(7)/5));

		unmixedSd.connectPoints(60, 60, 120, 2);
		unmixedSd.estimateLocalizationPrecision(50, 300);
		DemixingParameters demixingParams= new DemixingParameters((44)/180. * Math.PI,
				(67)/180.*Math.PI, 20/180.*Math.PI, 15/180.*Math.PI);
		ArrayList<ImagePlus> colImg = unmixedSd.renderDemixingImage(10, demixingParams);
		unmixedSd.createPdf();
		unmixedSd.writeArrayListForVisp();
	}
}
