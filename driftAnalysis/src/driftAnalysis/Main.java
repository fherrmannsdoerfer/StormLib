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

		String path1 = "C:\\Users\\herrmannsdoerfer\\Desktop\\StormData\\";
		//twoColorRegistration(path1,"LeftChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt", path1, "RightChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt");
		//twoColorRegistration(path1, "Cell2 - 0 min - 488 -_2_MMImages-undrift.txt", path1, "Cell2 - 0 min - 647 -_1_MMImages-undrift.txt");

		singleColor2dImage(path1,"LeftChannel141219Phalloidin647Synaptophysin1CF680Calyx600nm3DSchnitt2Messung4.txt");
		//dualColor2dImage(path1, "LeftChannel141112MicrotubuliCF680Cos2D1_500Messung3.txt", path1, "RightChannel141112MicrotubuliCF680Cos2D1_500Messung3.txt");
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
		channels.get(0).writeLocs("SHIFTED");
		channels.get(1).writeLocs("SHIFTED");
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
		ArrayList<ImagePlus> colImg = sd.renderImage3D(10);
		ij.IJ.save(colImg.get(0), "c:\\tmp2\\ImageRed1.tiff");
		ij.IJ.save(colImg.get(1), "c:\\tmp2\\ImageGreen1.tiff");
		ij.IJ.save(colImg.get(2), "c:\\tmp2\\ImageBlue1.tiff");
	}
	
	static void singleColor2dImage(String path, String fname){
		StormData sd = new StormData(path, fname);
		ImagePlus uncorrImg = sd.renderImage2D(10);
		ij.IJ.save(uncorrImg,path+"UncorrImage2D.tiff");
		sd.connectPoints(100, 100, 150, 3);
		sd.getLocsPerFrame();
		sd = FeatureBasedDriftCorrection.correctDrift(sd, 5000);
		sd.getLocsPerFrame();
		ImagePlus colImg = sd.renderImage2D(10);
		ij.IJ.save(colImg, path+"Image2D.tiff");
		
	}
	
	static void dualColor2dImage(String path1, String fname1, String path2, String fname2){
		StormData sd1 = new StormData(path1,fname1);
		sd1 = FeatureBasedDriftCorrection.correctDrift(sd1,2000);
		sd1.connectPoints(100., 100., 150, 3);
		ImagePlus corrImg = sd1.renderImage2D(10);
		ij.IJ.save(corrImg,path1+"driftcorrectedLeftChannel.tiff");
		StormData sd2 = new StormData(path2,fname2);
		sd2 = FeatureBasedDriftCorrection.correctDrift(sd2,2000);
		sd2.connectPoints(100., 100., 150, 3);
		ImagePlus corrImg2 = sd2.renderImage2D(10);
		ij.IJ.save(corrImg2,path1+"driftcorrectedRightChannel.tiff");
		StormData unmixedSd = Demixing.spectralUnmixing(sd1, sd2,true);
		
		ArrayList<ImagePlus> colImg = unmixedSd.renderDemixingImage(10, (70.7)/180. * Math.PI, (52.9)/180.*Math.PI, 15/180.*Math.PI, 15/180.*Math.PI);
		ij.IJ.save(colImg.get(0), path1+"\\DemixingTransformedImageRed2.tiff");
		ij.IJ.save(colImg.get(1), path1+"\\DemixingTransformedImageGreen2.tiff");
		ij.IJ.save(colImg.get(2), path1+"\\DemixingTransformedImageBlue2.tiff");
	}
}
