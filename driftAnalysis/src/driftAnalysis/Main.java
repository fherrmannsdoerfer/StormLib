package driftAnalysis;

import ij.ImagePlus;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;

import StormLib.BeadRegistration;
import StormLib.Demixing;
import StormLib.FeatureBasedDriftCorrection;
import StormLib.StormData;
import StormLib.StormDataProc;
import StormLib.StormLocalization;
import StormLib.Utilities;


public class Main {

	public static void main(String[] args) {
		
		String tag = "141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4";
		String path1 = "D:\\MessungenTemp\\"+tag+"\\Auswertung\\RapidStorm\\";
		//twoColorRegistration(path1,"meos-storm.txt", path1, "mover-storm.txt");
		//twoColorRegistration(path1, "VGAT-641-1.txt", path1, "NaV-532-1.txt");
		//singleColor3dImage(path1,"LeftChannel"+tag+"-cropwith230intensity.txt");
		//singleColor3dImage("D:\\MessungenTemp\\141203-ActinPhalloidin\\Auwertung\\RapidStorm\\","LeftChannel141126ActinPhalloidinAlexa647CalyxSlices3DMessung1-cropwith230intensity.txt");
		dualColor2dImage(path1, "LeftChannel"+tag+".txt", path1, "RightChannel"+tag+".txt");
	}
	
	static void twoColorRegistrationMultipleFiles(String path1,String pattern1,String path2,String pattern2){
		ArrayList<StormData> list = Utilities.openSeries(path1, pattern1, path2, pattern2);
		StormData sd1 = list.get(0);
		StormData sd2 = list.get(1);
		ImagePlus ucolImg = sd1.renderImage2D(10,"uncorr");
		sd1.connectPoints(40, 40, 100, 3);
		sd1 = FeatureBasedDriftCorrection.correctDrift(sd1, (int)Math.ceil((double)sd1.getDimensions().get(7)/5));
		ImagePlus colImg = sd1.renderImage2D(10,"corr");
		sd1.getLocsPerFrame();
		
		ImagePlus ucolImg2 = sd2.renderImage2D(10,"uncorr");
		sd2.connectPoints(40, 40, 100, 3);
		sd2 = FeatureBasedDriftCorrection.correctDrift(sd2, (int)Math.ceil((double)sd2.getDimensions().get(7)/5));
		ImagePlus colImg2 = sd2.renderImage2D(10, "corr");
		sd2.getLocsPerFrame();
		
		ArrayList<StormData> channels = BeadRegistration.doRegistration(sd1,sd2);
		ImagePlus alignedCh1 = channels.get(0).renderImage2D(10,"aligned");
		//ij.IJ.save(alignedCh1, path2+sd1.getBasename()+"Image2DAlignedCh1.tiff");
		
		ImagePlus alignedCh2 = channels.get(1).renderImage2D(10,"aligned");
		//ij.IJ.save(alignedCh2, path2+sd2.getBasename()+"Image2DAlignedCh2.tiff");
	}
	
	static void twoColorRegistration(String path1, String fname1, String path2, String fname2){
		StormData sd1 = new StormData(path1, fname1);
		StormData sd2 = new StormData(path2, fname2);
		ImagePlus ucolImg = sd1.renderImage2D(10,"uncorr");
		sd1.connectPoints(40, 40, 100, 3);
		sd1 = FeatureBasedDriftCorrection.correctDrift(sd1, (int)Math.ceil((double)sd1.getDimensions().get(7)/5));
		ImagePlus colImg = sd1.renderImage2D(10,"corr");
		sd1.getLocsPerFrame();
		
		ImagePlus ucolImg2 = sd2.renderImage2D(10,"uncorr");
		sd2.connectPoints(40, 40, 100, 3);
		sd2 = FeatureBasedDriftCorrection.correctDrift(sd2, (int)Math.ceil((double)sd2.getDimensions().get(7)/5));
		ImagePlus colImg2 = sd2.renderImage2D(10, "corr");
		sd2.getLocsPerFrame();
		
		ArrayList<StormData> channels = BeadRegistration.doRegistration(sd1,sd2);
		ImagePlus alignedCh1 = channels.get(0).renderImage2D(10,"aligned");
		//ij.IJ.save(alignedCh1, path2+sd1.getBasename()+"Image2DAlignedCh1.tiff");
		
		ImagePlus alignedCh2 = channels.get(1).renderImage2D(10,"aligned");
		//ij.IJ.save(alignedCh2, path2+sd2.getBasename()+"Image2DAlignedCh2.tiff");
		
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
		sd = FeatureBasedDriftCorrection.correctDrift(sd, 4000);
		sd.writeArrayListForVisp(path+"Visp_"+fname);
		ArrayList<ImagePlus> colImg = sd.renderImage3D(10);
		new File(path + "Pictures").mkdir();
		ij.IJ.save(colImg.get(0), path+"\\Pictures\\"+sd.getBasename()+"Red.tiff");
		ij.IJ.save(colImg.get(1), path+"\\Pictures\\"+sd.getBasename()+"Green.tiff");
		ij.IJ.save(colImg.get(2), path+"\\Pictures\\"+sd.getBasename()+"Blue.tiff");
	}
	
	static void singleColor2dImage(String path, String fname){
		StormData sd = new StormData(path, fname);
		ImagePlus uncorrImg = sd.renderImage2D(10, "uncorr");
		sd.connectPoints(100, 100, 150, 3);
		sd.getLocsPerFrame();
		sd = FeatureBasedDriftCorrection.correctDrift(sd, (int)Math.ceil((double)sd.getDimensions().get(7)/5));
		sd.getLocsPerFrame();
		ImagePlus colImg = sd.renderImage2D(10,"corr");
	}
	
	static void dualColor2dImage(String path1, String fname1, String path2, String fname2){
		StormData sd1 = new StormData(path1,fname1);
		//sd1 = FeatureBasedDriftCorrection.correctDrift(sd1,(int)Math.ceil((double)sd1.getDimensions().get(7)/7));
		//sd1.connectPoints(100., 100., 150, 3);
		sd1.getLocsPerFrame();
		sd1.saveLocs();
		ImagePlus corrImg = sd1.renderImage2D(10,"driftcorrected");
		//ij.IJ.save(corrImg,path1+"driftcorrectedLeftChannel.tiff");
		StormData sd2 = new StormData(path2,fname2);
		//sd2 = FeatureBasedDriftCorrection.correctDrift(sd2,(int)Math.ceil((double)sd2.getDimensions().get(7)/7));
		//sd2.connectPoints(100., 100., 150, 3);
		sd2.getLocsPerFrame();
		sd2.saveLocs();
		ImagePlus corrImg2 = sd2.renderImage2D(10,"driftcorrected");
		//ij.IJ.save(corrImg2,path1+"driftcorrectedRightChannel.tiff");
		StormData unmixedSd = Demixing.spectralUnmixing(sd1, sd2);
		unmixedSd = FeatureBasedDriftCorrection.correctDrift(unmixedSd,(int)Math.ceil((double)unmixedSd.getDimensions().get(7)/7));
		ArrayList<StormData> channels = unmixedSd.separateChannels( (90-22.)/180. * Math.PI, (90-48)/180.*Math.PI, 10/180.*Math.PI, 14/180.*Math.PI);
		channels.get(0).saveLocs();
		channels.get(1).saveLocs();
		//ArrayList<ImagePlus> colImg = unmixedSd.renderDemixingImage(10, (69.)/180. * Math.PI, (42.2)/180.*Math.PI, 10/180.*Math.PI, 14/180.*Math.PI);
		//ij.IJ.save(colImg.get(0), path1+"\\Pictures\\DemixingTransformedImageRed2.tiff");
		//ij.IJ.save(colImg.get(1), path1+"\\Pictures\\DemixingTransformedImageGreen2.tiff");
		//ij.IJ.save(colImg.get(2), path1+"\\Pictures\\DemixingTransformedImageBlue2.tiff");
				
		ArrayList<ImagePlus> colImg2 = unmixedSd.renderDemixingImage(10, (90-22.)/180. * Math.PI, (90-48)/180.*Math.PI, 10/180.*Math.PI, 14/180.*Math.PI);
		ij.IJ.save(colImg2.get(0), path1+"\\Pictures\\"+fname2+"DemixingTransformedImageRed.tiff");
		ij.IJ.save(colImg2.get(1), path1+"\\Pictures\\"+fname2+"DemixingTransformedImageGreen.tiff");
		ij.IJ.save(colImg2.get(2), path1+"\\Pictures\\"+fname2+"DemixingTransformedImageBlue.tiff");
	}
}
