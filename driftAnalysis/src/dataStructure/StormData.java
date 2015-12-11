
package dataStructure;

import functions.FeatureBasedDriftCorrection;
import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import comperators.StormLocalizationFrameComperator;
import comperators.StormLocalizationXComperator;
import comperators.StormLocalizationYComperator;
import comperators.StormLocalizationZComperator;
import comperators.TraceYComperator;
import StormLib.OutputClass;
import StormLib.Progressbar;
import StormLib.Utilities;
import StormLib.HelperClasses.BasicProcessingInformation;
import StormLib.HelperClasses.ConnectionResultLog;
import StormLib.HelperClasses.DemixingHistogramLog;
import StormLib.HelperClasses.FileImportLog;
import StormLib.HelperClasses.LocalizationPrecissionEstimationHistogramLog;
import StormLib.HelperClasses.LocalizationsPerFrameLog;
import StormLib.HelperClasses.LocsSaveLog;
import StormLib.HelperClasses.Save2DImage;
import StormLib.HelperClasses.Save3DImage;
import StormLib.HelperClasses.SaveDemixingImageLog;


public class StormData {
	boolean isSortedByFrame = false;
	private ArrayList<StormLocalization> locs = new ArrayList<StormLocalization>();
	private String path;
	private String fname;
	private String processingLog = "-";
	private ArrayList<Object> logs = new ArrayList<Object>();
	private String outputPath;
	
	public StormData(String path, String fname){
		Path fullPath = (Paths.get(path, fname));
		this.path = fullPath.getParent().toString()+"\\";
		this.fname = fullPath.getFileName().toString();
		this.outputPath = this.path;
		importData(this.path+fname);
	}
	
	public StormData(StormData sl){
		this.locs = sl.getLocs();
		this.fname = sl.getFname();
		this.path = sl.getPath();
		this.processingLog = sl.getProcessingLog();
		this.logs = sl.logs;
	}
	

	public StormData(){
		this.fname = "fname not set yet";
		this.path = "path not set yet";
		
	}
	private StormData(ArrayList<StormLocalization> sl, String path, String fname){
		this.locs = sl;
		this.path = path;
		this.fname = fname;
	}
	private StormData(ArrayList<StormLocalization> sl, String path){
		this.locs = sl;
		this.path = path;
	}
	public StormData(String fullpath){
		this.fname = "fname not set yet";
		this.path = "path not set yet";
		importData(fullpath);
	}
	
	public ArrayList<StormLocalization> importData(String fullpath){
		BufferedReader br = null;
		String line = "";
		String delimiter = " ";
		try {
			int counter = 0;
			ArrayList<Integer> errorLines = new ArrayList<Integer>(); 
			br = new BufferedReader(new FileReader(fullpath));
			line = br.readLine(); //skip header
			String[] headerComma = line.split(",");
			String[] headerBlank = line.split(" ");
			if (headerBlank.length > headerComma.length){
				while ((line = br.readLine())!= null){
					String[] tmpStr = line.split(delimiter);
					
					counter  = counter + 1;
					try{
						if (tmpStr.length == 4) { //2D data
							StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[0]), Double.valueOf(tmpStr[1]), Integer.valueOf(tmpStr[2]), Double.valueOf(tmpStr[3]));
							getLocs().add(sl);
						}
						else if(tmpStr.length == 5) { //3d data
							StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[0]), Double.valueOf(tmpStr[1]), Double.valueOf(tmpStr[2]), Integer.valueOf(tmpStr[3]), Double.valueOf(tmpStr[4]));
							getLocs().add(sl);
						}
						else if(tmpStr.length == 6) { //Malk output
							StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[0]), Double.valueOf(tmpStr[1]), Integer.valueOf(tmpStr[2]), Double.valueOf(tmpStr[3]));
							getLocs().add(sl);
						}
						else if(tmpStr.length == 7) { //no Malk output
							StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[0]), Double.valueOf(tmpStr[1]), Double.valueOf(tmpStr[2]), Integer.valueOf(tmpStr[3]), Double.valueOf(tmpStr[4]));
							getLocs().add(sl);
						}
						else {System.out.println("File format not understood!");}
					}
					catch(java.lang.NumberFormatException ne){System.out.println("Problem in line:"+counter+ne); errorLines.add(counter);}
				}
			}
			else if (headerBlank.length <= headerComma.length) {
				while ((line = br.readLine())!= null){
					String[] tmpStr = line.split(",");
					
					counter  = counter + 1;
					try{
						if (tmpStr.length >=10) { // 3D
							StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[1]), Double.valueOf(tmpStr[2]), Double.valueOf(tmpStr[3]), Double.valueOf(tmpStr[0]).intValue(), Double.valueOf(tmpStr[6]));
							getLocs().add(sl);
						}
						if (tmpStr.length <10) { //2D
							StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[1]), Double.valueOf(tmpStr[2]), 0., Double.valueOf(tmpStr[0]).intValue(), Double.valueOf(tmpStr[4]));
							getLocs().add(sl);
						}
					} catch (java.lang.NumberFormatException ne){System.out.println("Problem in line:"+counter+ne); errorLines.add(counter);}
				}
			}
			else {
				
			}
			FileImportLog fl = new FileImportLog(errorLines,locs.size(),path,getBasename());
			logs.add(fl);
			OutputClass.writeLoadingStatistics(path, getBasename(), errorLines, locs.size());
			System.out.println("File contains "+getLocs().size()+" localizations.");
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println(path+fname);
		}
		catch (IOException e) {e.printStackTrace();}
		return locs;
	}
	
	public synchronized void sortFrame(){
		Comparator<StormLocalization> compFrame = new StormLocalizationFrameComperator();
		Collections.sort(getLocs(),compFrame);
		isSortedByFrame = true;
	}
	
	public ArrayList<StormLocalization> getList(){
		return getLocs();
	}
	
	public StormLocalization getElement(int index){
		if (getLocs().size() > index){
			return getLocs().get(index);
		}
		else {
			System.err.println("Index "+index+" exceeds the number of elements ("+getLocs().size()+")!");
			return null;
		}
	}
	
	public void addElement(StormLocalization sl){
		getLocs().add(sl);
	}
	
	public void append(StormData sd){
		this.locs.addAll(sd.getLocs());
	}
	
	public synchronized int findFirstIndexForFrame(int frame){ //finds the index with the first appearance of a framenumber larger or equal the given frame
		if (isSortedByFrame){
		}
		else {
			sortFrame();
		}
		int ret = 0;
		for (int i = 0;i<getLocs().size();i++){
			if (getLocs().get(i).getFrame() >= frame){
				return i;
			}
		}
		System.out.println("Given frame "+frame+"is larger than any contained localization!");
		return getLocs().size()-1; //if the given frame is larger than any frame the last index is reported
	}
	
	public void setPath(String path){
		this.path = path;
	}
	
	public void setFname(String fname){
		this.fname = fname;
	}
	
	public void setProcessingLog(String proclog){
		this.processingLog = proclog;
	}
	
	public String getPath(){
		return path;
	}
	
	public String getFname(){
		return fname;
	}
	
	public String getProcessingLog(){
		return processingLog;
	}
	
	public int findLastIndexForFrame(int frame){ //finds the last index for which the frame is equal or lower the given frame 
		if (isSortedByFrame){
		}
		else {
			sortFrame();
		}
		int ret = 0;
		for (int i = 0;i<getLocs().size();i++){
			if (getLocs().get(i).getFrame() > frame){
				return Math.max(0, i-1); //if the given frame is lower than any frame the first index is reported
			}
		}
		System.out.println("Given frame "+frame+"is larger than any contained localization!");
		return getLocs().size()-1;
	}
	
	public int getSize(){
		return getLocs().size();
	}
	
	public ArrayList<Double> getDimensions(){
		return getDimensions(this.locs);
	}
	
	public ArrayList<Double> getDimensions(ArrayList<StormLocalization> locs){ //returns minimal and maximal positions in an ArrayList in the following order (xmin, xmax, ymin, ymax, zmin, zmax, minFrame, maxFrame)
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = 0;
		double maxY = 0;
		double maxZ = 0;
		double minFrame = Double.MAX_VALUE;
		double maxFrame = 0;
		for (int i = 0; i<locs.size(); i++){
			StormLocalization sl = locs.get(i);
			double currX = sl.getX();
			double currY = sl.getY();
			double currZ = sl.getZ();
			double currFrame = (double) sl.getFrame();
			
			if (minX > currX) {
				minX = currX;
			}
			if (maxX < currX) {
				maxX = currX;
			}
			if (minY > currY) {
				minY = currY;
			}
			if (maxY < currY) {
				maxY = currY;
			}
			if (minZ > currZ) {
				minZ = currZ;
			}
			if (maxZ < currZ) {
				maxZ = currZ;
			}
			if (minFrame>currFrame) {
				minFrame = currFrame;
			}
			if (maxFrame<currFrame){
				maxFrame = currFrame;
			}
		}
		
		ArrayList<Double> ret = new ArrayList<Double>();
		ret.add(minX);
		ret.add(maxX);
		ret.add(minY);
		ret.add(maxY);
		ret.add(minZ);
		ret.add(maxZ);
		ret.add(minFrame);
		ret.add(maxFrame);
		
		return ret;
	}
	public ImagePlus renderImage2D(double pixelsize){
		return renderImage2D(pixelsize, true, processingLog,0,-1); // function is also used to create images for the fourier transformation for the drift correction
	}
	public ImagePlus renderImage2D(double pixelsize, String tag) {
		return renderImage2D(pixelsize, true, tag,0,-1);
	}
	public ImagePlus renderImage2D(double pixelsize, boolean saveImage){
		return renderImage2D(pixelsize, saveImage, "",0,-1);
	}
	public ImagePlus renderImage2D(double pixelsize, boolean saveImage, String tag){
		return renderImage2D(pixelsize, saveImage,tag,0,-1);
	}
	public ImagePlus renderImage2D(double pixelsize, boolean saveImage, String tag,int mode, int maxPixelsize){ 
		//render localizations from Stormdata to Image Plus Object
		//mode specifies which projection is rendered 0:xy plane, 1: xz, 2:yz
		double sigma = 5/pixelsize; //in nm sigma to blur localizations
		int filterwidth = 3; // must be odd
		ArrayList<Double> dims = getDimensions();
		int pixelX = 0;
		int pixelY = 0;
		if (saveImage){
			switch (mode){
				case 0://xy
					pixelX =(int) Math.pow(2, Math.ceil(Math.log(dims.get(1) / pixelsize)/Math.log(2)));
					pixelY = (int) Math.pow(2, Math.ceil(Math.log(dims.get(3) / pixelsize)/Math.log(2)));
					break;
				case 1://xz
					pixelX =(int) Math.pow(2, Math.ceil(Math.log(dims.get(1) / pixelsize)/Math.log(2)));
					pixelY = (int) Math.pow(2, Math.ceil(Math.log(dims.get(5) / pixelsize)/Math.log(2)));
					break;
				case 2://yz
					pixelX =(int) Math.pow(2, Math.ceil(Math.log(dims.get(3) / pixelsize)/Math.log(2)));
					pixelY = (int) Math.pow(2, Math.ceil(Math.log(dims.get(5) / pixelsize)/Math.log(2)));
					break;
			}
		}
		else{
			//finds nearest power of 2 to either the width or height of the image, depending on which number is larger
			int dimsImg = 0;
			if (maxPixelsize==-1){
				switch (mode){
					case 0:
						dimsImg = Math.max((int) Math.pow(2, Math.ceil(Math.log(dims.get(1) / pixelsize)/Math.log(2))), 
										   (int) Math.pow(2, Math.ceil(Math.log(dims.get(3) / pixelsize)/Math.log(2))));
						break;
					case 1:
						dimsImg = Math.max((int) Math.pow(2, Math.ceil(Math.log(dims.get(1) / pixelsize)/Math.log(2))), 
										   (int) Math.pow(2, Math.ceil(Math.log(dims.get(5) / pixelsize)/Math.log(2))));
						break;
					case 2:
						dimsImg = Math.max((int) Math.pow(2, Math.ceil(Math.log(dims.get(3) / pixelsize)/Math.log(2))), 
										   (int) Math.pow(2, Math.ceil(Math.log(dims.get(5) / pixelsize)/Math.log(2))));
						break;
					
				}
			}
				else{//max pixel size is overwritten if provided
					dimsImg = maxPixelsize;
				}
			
			//int pixelX = (int) Math.ceil(dims.get(1) / pixelsize);
			pixelY = dimsImg;
			pixelX = dimsImg;
		}
		
		float [][] image = new float[pixelX][pixelY];
		image = addFilteredPoints(image, sigma, filterwidth, pixelsize, getLocs(),mode);
		ImageProcessor ip = new FloatProcessor(pixelX,pixelY);
		ip.setFloatArray(image);
		ImagePlus imgP = new ImagePlus("", ip);
		//System.out.println("Image rendered ("+imgP.getWidth()+"*"+imgP.getHeight()+")");
		if (saveImage){
			Save2DImage si = new Save2DImage(path, getBasename(), tag,imgP, pixelsize);
			logs.add(si);

			OutputClass.save2DImage(path, getBasename(), tag, imgP, pixelsize);
			//OutputClass.writeImageSaveStatistics(path, getBasename(), pixelsize, imgP, picname);
		}
		
		return imgP;
	}
	
	
	public ArrayList<ImagePlus> renderDemixingImage(double pixelsize, DemixingParameters params){
		return renderDemixingImage(pixelsize, params, processingLog);
	}
		
	public ArrayList<ImagePlus> renderDemixingImage(double pixelsize, DemixingParameters params, String tag){
		double sigma = 10/pixelsize; //in nm sigma to blur localizations
		int filterwidth = 3; // must be odd
		ArrayList<Double> dims = getDimensions();
		//int pixelX = (int) Math.pow(2, Math.ceil(Math.log(dims.get(1) / pixelsize)/Math.log(2)));
		int pixelX = (int) Math.ceil(dims.get(1) / pixelsize);
		int pixelY = (int) Math.ceil(dims.get(3) / pixelsize);
		float [][] imageRed = new float[pixelX][pixelY];
		float [][] imageGreen = new float[pixelX][pixelY];
		float [][] imageBlue = new float[pixelX][pixelY];
		ArrayList<float[][]> coloredImage = new ArrayList<float[][]>();
		coloredImage.add(imageRed);
		coloredImage.add(imageGreen);
		coloredImage.add(imageBlue);
		ArrayList<Double> vals = new ArrayList<Double>();
		for (int i = 0; i<locs.size(); i++){
			vals.add(locs.get(i).getAngle()*180/Math.PI);
		}
		double binWidth = 1.;
		ArrayList<ArrayList<Double>> histData = Utilities.getHistogram(vals, binWidth);
		DemixingHistogramLog dl = new DemixingHistogramLog(path, getBasename(), histData, binWidth, tag);
		logs.add(dl);
		OutputClass.writeDemixingHistogram(path, getBasename(), histData, binWidth, tag);
		ArrayList<StormLocalization> locsCh1 = new ArrayList<StormLocalization>();
		ArrayList<StormLocalization> locsCh2 = new ArrayList<StormLocalization>();
		coloredImage = renderDemixing(coloredImage, sigma, filterwidth, pixelsize, params, locsCh1, locsCh2);
		StormData channel1 = new StormData(locsCh1,getPath(),getFname());
		StormData channel2 = new StormData(locsCh2,getPath(),getFname());
		channel1.renderImage3D(pixelsize, "ColorCodedChannel1");
		channel2.renderImage3D(pixelsize, "ColorCodedChannel2");
		
		ImageProcessor ipRed = new FloatProcessor(pixelX,pixelY);
		ImageProcessor ipGreen = new FloatProcessor(pixelX,pixelY);
		ImageProcessor ipBlue = new FloatProcessor(pixelX,pixelY);
		ipRed.setFloatArray(coloredImage.get(0));
		ipGreen.setFloatArray(coloredImage.get(1));
		ipBlue.setFloatArray(coloredImage.get(2));
		ImagePlus imgPRed = new ImagePlus("", ipRed);
		ImagePlus imgPGreen = new ImagePlus("", ipGreen);
		ImagePlus imgPBlue = new ImagePlus("", ipBlue);
		System.out.println("3D Image rendered ("+imgPRed.getWidth()+"*"+imgPRed.getHeight()+")");
		ArrayList<ImagePlus> colImg = new ArrayList<ImagePlus>();
		colImg.add(imgPRed);
		colImg.add(imgPGreen);
		colImg.add(imgPBlue);
		SaveDemixingImageLog sl = new SaveDemixingImageLog(path, getBasename(), processingLog, colImg, params, pixelsize);
		logs.add(sl);
		OutputClass.saveDemixingImage(path, getBasename(), processingLog, colImg);
		
		return colImg;
	}
	
	ArrayList<float[][]> renderDemixing(ArrayList<float[][]> coloredImage, double sigma, int filterwidth, 
			double pixelsize, DemixingParameters params,ArrayList<StormLocalization> locsCh1,ArrayList<StormLocalization> locsCh2){
		if (filterwidth %2 == 0) {System.err.println("filterwidth must be odd");}
		double minAngle1 = params.getAngle1() - params.getWidth1()/2;
		double maxAngle1 = params.getAngle1() + params.getWidth1()/2;
		double minAngle2 = params.getAngle2() - params.getWidth2()/2;
		double maxAngle2 = params.getAngle2() + params.getWidth2()/2;
		double factor = 10000*1/(2*Math.PI*sigma*sigma);
		double factor2 = -0.5/sigma/sigma;
		ArrayList<Double> dims = getDimensions();
		double zMin = 400 ; //dims.get(4);
		double zMax = 800; //dims.get(5);
		System.out.println("zMax: "+zMax);
		float[][] redChannel = coloredImage.get(0);
		float[][] greenChannel = coloredImage.get(1);
		float[][] blueChannel = coloredImage.get(2);
		Progressbar pb = new Progressbar(0, getSize(), 0, "Rendering demixing Image ...");
		for (int i = 1; i<getSize(); i++){
			StormLocalization sl = locs.get(i);
			double posX = sl.getX()/pixelsize; //position of current localization
			double posY = sl.getY()/pixelsize;
			//double posZ = sl.getZ();
			int pixelXStart = (int)Math.floor(posX) - (filterwidth-1)/2;
			int pixelYStart = (int)Math.floor(posY) - (filterwidth-1)/2;
			for (int k = pixelXStart; k<pixelXStart+ filterwidth;k++){
				for(int l= pixelYStart; l<pixelYStart+ filterwidth;l++){
					try{
						if (((sl.getAngle()> minAngle1 && sl.getAngle()< maxAngle1))|| sl.getAngle() == 0){
							redChannel[k][l] = redChannel[k][l] + (float)((sl.getIntensity())*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							locsCh1.add(sl);
						}
						else if ((sl.getAngle()> minAngle2 && sl.getAngle()< maxAngle2) || sl.getAngle() == Math.PI/2){
							greenChannel[k][l] = greenChannel[k][l] + (float)((sl.getIntensity())*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							locsCh2.add(sl);
						}
					} catch(IndexOutOfBoundsException e){e.toString();}
				}
			}
			pb.updateProgress();
		}
		
		ArrayList<float[][]> normalizedChannels = normalizeChannels(redChannel, greenChannel, blueChannel);
		coloredImage.clear();
		coloredImage.add(normalizedChannels.get(0));
		coloredImage.add(normalizedChannels.get(1));
		coloredImage.add(normalizedChannels.get(2));
		return coloredImage;
	}
	
	public ArrayList<ImagePlus> renderImage3D(double pixelsize){
		return renderImage3D(pixelsize, processingLog);
	}
	
	public ArrayList<ImagePlus> renderImage3D(double pixelsize, String tag){ //render localizations from Stormdata to Image Plus Object
		double sigma = 20/pixelsize; //in nm sigma to blur localizations
		int filterwidth = 3; // must be odd
		ArrayList<Double> dims = getDimensions();
		int pixelX = (int) Math.pow(2, Math.ceil(Math.log(dims.get(1) / pixelsize)/Math.log(2)));
		//int pixelX = (int) Math.ceil(dims.get(1) / pixelsize);
		int pixelY = (int) Math.ceil(dims.get(3) / pixelsize);
		
		ArrayList<float[][]> coloredImage = new ArrayList<float[][]>();
		for (int i = 0; i<3; i++){
			float[][] ch = new float[pixelX][pixelY];
			coloredImage.add(ch);
		}
		
		coloredImage = addFilteredPoints(coloredImage, sigma, filterwidth, pixelsize, getLocs());
		ArrayList<ImagePlus> colImg =write3dImage(pixelX,pixelY, coloredImage,pixelsize,tag);
		
		return colImg;
	}
	
	public ArrayList<ImagePlus> write3dImage(int pixelX, int pixelY, ArrayList<float[][]> coloredImage, double pixelsize,String tag){
		ImageProcessor ipRed = new FloatProcessor(pixelX,pixelY);
		ImageProcessor ipGreen = new FloatProcessor(pixelX,pixelY);
		ImageProcessor ipBlue = new FloatProcessor(pixelX,pixelY);
		ipRed.setFloatArray(coloredImage.get(0));
		ipGreen.setFloatArray(coloredImage.get(1));
		ipBlue.setFloatArray(coloredImage.get(2));
		ImagePlus imgPRed = new ImagePlus("", ipRed);
		ImagePlus imgPGreen = new ImagePlus("", ipGreen);
		ImagePlus imgPBlue = new ImagePlus("", ipBlue);
		System.out.println("3D Image rendered ("+imgPRed.getWidth()+"*"+imgPRed.getHeight()+")");
		ArrayList<ImagePlus> colImg = new ArrayList<ImagePlus>();
		colImg.add(imgPRed);
		colImg.add(imgPGreen);
		colImg.add(imgPBlue);
		Save3DImage si = new Save3DImage(path, getBasename(), tag, colImg, pixelsize);
		OutputClass.save3DImage(path, getBasename(), tag, colImg);
		return colImg;
	}
	
	float[][] addFilteredPoints(float[][] image, double sigma, int filterwidth, 
			double pixelsize, ArrayList<StormLocalization> sd, int mode){
		if (filterwidth %2 == 0) {System.err.println("filterwidth must be odd");}
		double factor = 100*1/(2*Math.PI*sigma*sigma);
		double factor2 = -0.5/sigma/sigma;
		//System.out.println(sd.getSize());
		for (int i = 1; i<getSize(); i++){
			StormLocalization sl = sd.get(i);
			double posX = 0;
			double posY = 0;
			switch (mode){
				case 0:
					posX = sl.getX()/pixelsize; //position of current localization
					posY = sl.getY()/pixelsize;
					break;
				case 1:
					posX = sl.getX()/pixelsize; //position of current localization
					posY = sl.getZ()/pixelsize;
					break;
				case 2:
					posX = sl.getY()/pixelsize; //position of current localization
					posY = sl.getZ()/pixelsize;
					break;
			}
			
			int pixelXStart = (int)Math.floor(posX) - (filterwidth-1)/2;
			int pixelYStart = (int)Math.floor(posY) - (filterwidth-1)/2;
			for (int k = pixelXStart; k<pixelXStart+ filterwidth;k++){
				for(int l= pixelYStart; l<pixelYStart+ filterwidth;l++){
					try{
						image[k][l] = image[k][l] + (float)(factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
						//System.out.println("factor: "+factor+" k: "+k+" l: "+l+"posX: "+posX+"posY: "+posY+" image[k][l]" +image[k][l]+" res: "+(float)(factor * Math.exp(-0.5/sigma/sigma*(Math.pow((k-posX),2)+Math.pow((l-posY),2)))));
					} catch(IndexOutOfBoundsException e){e.toString();}
				}
			}
		}
		return image;
	}
	
	ArrayList<float[][]> addFilteredPoints(ArrayList<float[][]> coloredImage, double sigma, int filterwidth, double pixelsize, ArrayList<StormLocalization> sd){
		if (filterwidth %2 == 0) {System.err.println("filterwidth must be odd");}
		double factor = 10000*1/(2*Math.PI*sigma*sigma);
		double factor2 = -0.5/sigma/sigma;
		ArrayList<Double> dims = getDimensions();
		double zMin = dims.get(4);
		double zMax = dims.get(5);
		zMax = zMax - zMin;//all z should lie between 0 and a certain maximum for the rendering
		System.out.println("zMax: "+zMax);
		float[][] redChannel = coloredImage.get(0);
		float[][] greenChannel = coloredImage.get(1);
		float[][] blueChannel = coloredImage.get(2);
		for (int i = 1; i<getSize(); i++){
			StormLocalization sl = sd.get(i);
			double posX = sl.getX()/pixelsize; //position of current localization
			double posY = sl.getY()/pixelsize;
			double posZ = sl.getZ() - zMin;
			int pixelXStart = (int)Math.floor(posX) - (filterwidth-1)/2;
			int pixelYStart = (int)Math.floor(posY) - (filterwidth-1)/2;
			for (int k = pixelXStart; k<pixelXStart+ filterwidth;k++){
				for(int l= pixelYStart; l<pixelYStart+ filterwidth;l++){
					double kk = 1;
					try{
						double weight = factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2)));
						if (true){
							redChannel[k][l] = (float) (redChannel[k][l] + getColor(posZ,zMax,0) * weight);
							greenChannel[k][l] = (float) (greenChannel[k][l] +getColor(posZ,zMax,1) * weight);
							blueChannel[k][l] = (float) (blueChannel[k][l] +getColor(posZ,zMax,2) * weight);
							if (redChannel[k][l]<0||greenChannel[k][l]<0||blueChannel[k][l]<0){
								System.out.println(k+" "+l);
							}
						}
						else{
							double parts = 6;
							double invparts = 1./parts;
							if (posZ < invparts * zMax){
								blueChannel[k][l] = blueChannel[k][l] + (float)((1)*weight);
								greenChannel[k][l] = greenChannel[k][l] + (float)((parts*posZ/zMax)*weight);
							}
							else if (posZ<invparts * 2*zMax){
								greenChannel[k][l] = greenChannel[k][l] + (float)((1)*weight);
								blueChannel[k][l] = blueChannel[k][l] + (float)((1-parts*(posZ/zMax-invparts))*weight);
							}
							else if (posZ<invparts * 3*zMax){
								greenChannel[k][l] = greenChannel[k][l] + (float)((1)*weight);
								redChannel[k][l] = redChannel[k][l] + (float)((parts*(posZ/zMax - invparts*2))*weight);
							}
							else if (posZ<invparts * 4*zMax){
								redChannel[k][l] = redChannel[k][l] + (float)((1)*weight);
								greenChannel[k][l] = greenChannel[k][l] + (float)((1-parts*(posZ/zMax-invparts*3))*weight);
							}
							else if (posZ<invparts * 5*zMax){
								redChannel[k][l] = redChannel[k][l] + (float)((1)*weight);
								blueChannel[k][l] = blueChannel[k][l] + (float)((parts*(posZ/zMax-invparts*4))*weight);
							}
							else if (posZ<invparts * 6*zMax){
								redChannel[k][l] = redChannel[k][l] + (float)((1)*weight);
								greenChannel[k][l] = greenChannel[k][l] + (float)((parts*(posZ/zMax-invparts*5))*weight);
								blueChannel[k][l] = blueChannel[k][l] + (float)((1)*weight);
							}
							if (redChannel[k][l]<0||greenChannel[k][l]<0||blueChannel[k][l]<0){
								System.out.print(redChannel[k][l] +" "+greenChannel[k][l] +" "+blueChannel[k][l] );
							}
						}
						/*else{
							if (posZ < 0.2 * zMax){
								blueChannel[k][l] = blueChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								greenChannel[k][l] = greenChannel[k][l] + (float)((5*posZ/zMax)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else if (posZ<0.4*zMax){
								greenChannel[k][l] = greenChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								blueChannel[k][l] = blueChannel[k][l] + (float)((1-5*(posZ/zMax-0.2))*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else if (posZ<0.6*zMax){
								greenChannel[k][l] = greenChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								redChannel[k][l] = redChannel[k][l] + (float)((5*(posZ/zMax - 0.4))*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else if (posZ<0.8*zMax){
								redChannel[k][l] = redChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								greenChannel[k][l] = greenChannel[k][l] + (float)((1-5*(posZ/zMax-0.6))*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else if (posZ<zMax){
								redChannel[k][l] = redChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								blueChannel[k][l] = blueChannel[k][l] + (float)((5*(posZ/zMax-0.8))*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							
						}*/
					} catch(Exception e){
						//System.out.println(e.toString());
						}
				}
			}
		}
		double max= 0;
		double min = 1e19;
		for (int i = 0; i<redChannel.length;i++){
			for(int j = 0; j<redChannel[0].length; j++){
				max = Math.max(redChannel[i][j],max);
				max = Math.max(greenChannel[i][j],max);
				max = Math.max(blueChannel[i][j],max);
				min = Math.min(redChannel[i][j],min);
				min = Math.min(greenChannel[i][j],min);
				min = Math.min(blueChannel[i][j],min);
			}
		}
		ArrayList<float[][]> normalizedChannels = normalizeChannels(redChannel, greenChannel, blueChannel);
		coloredImage.clear();
		coloredImage.add(normalizedChannels.get(0));
		coloredImage.add(normalizedChannels.get(1));
		coloredImage.add(normalizedChannels.get(2));
		return coloredImage;
	}
	
	private static float getColor(double posZ, double zMax, int color) {
		
		if (posZ < 0.25* zMax&&posZ>=0){
			//blue rises from 0 to 1
			if (color == 2){
				return (float) (4*posZ / zMax);
			}
		}
		else if (posZ < 0.5* zMax&&posZ>0){
			//green rises from 0 to 1 blue stays one
			if (color == 1){
				return (float)(4*posZ/zMax - 1);
			}
			if (color == 2){
				return (float) 1;//(2 - 4*posZ/zMax)	;
			}
		}
		else if (posZ < 0.75* zMax&&posZ>0){
			//green stays one, blue goes to zero again
			if (color == 1){
				return (float) 1;//(4*posZ/zMax - 2);
			}
			if (color == 2){
				return (float) (3 - 4*posZ/zMax);
			}
		}
		else if (posZ<zMax&&posZ>0) {
			//green goes to zero red rises
			if (color == 0){
				return (float) (4*posZ/zMax - 3);
			}
			if (color == 1){
				return (float) (4-4*posZ/zMax);
			}
		}
		return 0;
	}

	ArrayList<float[][]> normalizeChannels(float[][] redChannel, float[][] greenChannel, float[][] blueChannel){
		double max = 0;
		double min = Double.MAX_VALUE;
		for (int i = 0; i<redChannel.length;i++){
			for(int j = 0; j<redChannel[0].length; j++){
				max = Math.max(redChannel[i][j],max);
				max = Math.max(greenChannel[i][j],max);
				max = Math.max(blueChannel[i][j],max);
				min = Math.min(redChannel[i][j],min);
				min = Math.min(greenChannel[i][j],min);
				min = Math.min(blueChannel[i][j],min);
			}
		}
		int[] hist = new int[65536];
		int nbrEntries = 0;
		for (int i=0;i<65536;i++){
			hist[i] = 0;
		}
		for (int i = 0; i<redChannel.length;i++){
			for(int j = 0; j<redChannel[0].length; j++){
				redChannel[i][j] = (float) Math.ceil((redChannel[i][j] - min)/(max - min) * 65535);
				greenChannel[i][j] = (float) Math.ceil((greenChannel[i][j] - min)/(max - min) * 65535);
				blueChannel[i][j] = (float) Math.ceil((blueChannel[i][j] - min)/(max - min) * 65535);
				hist[(int)redChannel[i][j]] += 1;
				hist[(int)greenChannel[i][j]] += 1;
				hist[(int)blueChannel[i][j]] += 1;
				nbrEntries +=3;
				//System.out.println(hist[0]+ " "+ (int)redChannel[i][j]+ "nbrEntries "+ nbrEntries);
			}
		}
		double percentile = 0.99;
		int sum = 0;
		double counts = nbrEntries - hist[0];//counts is the number of intensities above 0
		double newMaximum = 0;
		for (int i=1;i<65536;i++){
			//System.out.println("sum: "+sum+" counts: "+ counts+"nbrEntries "+nbrEntries+"hist[0] "+hist[0]);
			sum = sum +hist[i];
			if (sum>percentile * counts){
				newMaximum = i;
				break;
			}
		}
		System.out.println("Max: "+max+" newMax: "+newMaximum);
		for (int i = 0; i<redChannel.length;i++){
			for(int j = 0; j<redChannel[0].length; j++){
				redChannel[i][j] = (float)Math.min((redChannel[i][j] )/(newMaximum)*65535,65535);
				greenChannel[i][j] = (float)Math.min((greenChannel[i][j] )/(newMaximum)*65535,65535);
				blueChannel[i][j] = (float)Math.min((blueChannel[i][j] )/(newMaximum)*65535,65535);
			}
		}
		max = 0;
		for (int i = 0; i<redChannel.length;i++){
			for(int j = 0; j<redChannel[0].length; j++){
				max = Math.max(redChannel[i][j],max);
				max = Math.max(greenChannel[i][j],max);
				max = Math.max(blueChannel[i][j],max);
				min = Math.min(redChannel[i][j],min);
				min = Math.min(greenChannel[i][j],min);
				min = Math.min(blueChannel[i][j],min);
			}
		}
		ArrayList<float[][]> ret = new ArrayList<float[][]>();
		ret.add(redChannel);
		ret.add(greenChannel);
		ret.add(blueChannel);
		return ret;
	}
	
	public ArrayList<StormLocalization> getLocs() {
		return locs;
	}
	public void setLocs(ArrayList<StormLocalization> locs) {
		this.locs = locs;
	}
	
	public void createLineSample(double driftx, double drifty, int locsPerFrame, int frames){
		locs = new ArrayList<StormLocalization>();
		for (int frame = 0; frame< frames; frame++){
			double contributionDriftX = driftx * frame / frames;
			double contributionDriftY = drifty * frame / frames;
			//System.out.println(contributionDriftX+" "+contributionDriftY);
			double x = 0;
			double y = 0;
			double t = 0;
			for (int locPerFrameCounter = 0; locPerFrameCounter < locsPerFrame; locPerFrameCounter++) {
				double s1 = (Math.random()*8);
				int s2 = (int)s1;
				//System.out.println(s2);
				switch (s2){
				case 0:
					//System.out.println("case0");
					t = Math.random()*Math.PI;
					x = 10000 * Math.cos(t);
					y = 10000 * Math.cos(t) * Math.sin(t);	
					break;
				case 1:
					//System.out.println("case1");
					t = Math.random()*Math.PI;
					x = 6000 * Math.sin(t)*Math.sin(t);
					y = 10000 * Math.cos(t) * Math.cos(t);
					break;
				case 2:
					//System.out.println("case2");
					t = Math.random()*Math.PI;
					x = 10000 * Math.sin(t);
					y = 10000 * Math.cos(t) * Math.cos(t);
					break;
				case 3:
					//System.out.println("case3");
					t = Math.random()*Math.PI;
					x = 10000 * Math.cos(t)*Math.tan(t);
					y = 3000 * Math.cos(t) * Math.sin(t);
					break;
				case 4:
					//System.out.println("case3");
					t = Math.random()*Math.PI;
					x = 10000 * Math.cos(t) + 4000;
					y = 3000 *Math.sin(t)+ 4000;
					break;
				case 5:
					//System.out.println("case3");
					t = 2*Math.random()*Math.PI;
					x = 3000 * Math.cos(t) + 4000;
					y = 3000 *Math.sin(t)+ 6000;
					break;
				case 6:
					//System.out.println("case3");
					x = Math.random()*600+3000;
					y = Math.random()*600+3000;
					break;
					
				case 7:
					//System.out.println("case3");
					x = Math.random()*100+2000;
					y = Math.random()*1000+10000;
					break;
				}

				locs.add(new StormLocalization(x + contributionDriftX+(Math.random()*50-25), y + contributionDriftY+(Math.random()*50-25), Math.random(), frame, 100));
				if (frame<frames-2){
					locs.add(new StormLocalization(x + contributionDriftX+(Math.random()*50-25), y + contributionDriftY+(Math.random()*50-25), Math.random(), frame+1, 100));
				}
			}
		}
	}
	
	public void createSingleLineSample(double driftx, double drifty, int locsPerFrame, int frames){
		locs = new ArrayList<StormLocalization>();
		for (int frame = 0; frame< frames; frame++){
			double contributionDriftX = driftx * frame / frames;
			double contributionDriftY = drifty * frame / frames;
			//System.out.println(contributionDriftX+" "+contributionDriftY);
			double x = 0;
			double y = 0;
			double z = 0;
			double t = 0;
			for (int locPerFrameCounter = 0; locPerFrameCounter < locsPerFrame; locPerFrameCounter++) {
				x = Math.random() * 2000;
				y = 500;
				z = x * 0.6;

				locs.add(new StormLocalization(x + contributionDriftX+(Math.random()*50-25), y + contributionDriftY+(Math.random()*50-25), z+(Math.random()*50-25), frame, 100));
				if (frame<frames-2){
					locs.add(new StormLocalization(x + contributionDriftX+(Math.random()*50-25), y + contributionDriftY+(Math.random()*50-25), z+(Math.random()*50-25), frame+1, 100));
				}
			}
		}
	}
	
	public void writeArrayListForVisp(DemixingParameters demixingParams){
		OutputClass.writeArrayListForVisp(path,getBasename(), locs,processingLog, demixingParams);
	}
	
	public void writeArrayListForVisp(DemixingParameters demixingParams, String tag){
		OutputClass.writeArrayListForVisp(path,getBasename(), locs,tag, demixingParams);
	}
	
	public void writeArrayListForVisp(){
		writeArrayListForVisp(processingLog);
	}
	
	public void writeArrayListForVisp(String tag) {
		OutputClass.writeArrayListForVisp(path, getBasename(), locs, tag);
	}
	
	public void writeLocs(){
		writeLocs(processingLog);
	}
	
	public void writeLocs(String tag){
		OutputClass.writeLocs(path,  getBasename(), locs, tag);
		LocsSaveLog sl = new LocsSaveLog(path, getBasename(), locs, tag);
		logs.add(sl);
	}
	
	
	public ArrayList<StormLocalization> connectPoints(double dx, double dy, double dz, int maxdistBetweenLocalizations) {
		// TODO Auto-generated method stub
		ArrayList<ArrayList<StormLocalization>> traces = Utilities.findTraces(locs, dx, dy, dz, maxdistBetweenLocalizations);
		ArrayList<StormLocalization> connectedLoc = Utilities.connectTraces(traces);
		this.processingLog += "Con"; 
		OutputClass.writeConnectionResult(path,getBasename(),connectedLoc.size(), locs.size(),processingLog);
		ConnectionResultLog rl = new ConnectionResultLog(path, getBasename(), connectedLoc.size(), locs.size(), processingLog);
		logs.add(rl);
		this.locs = connectedLoc;
		
		return connectedLoc;
	}
	
	public void estimateLocalLocalizationPrecision2(double dxy, double dz, int widthWindow, int heightWindow, int shiftX, int shiftY){
		double ulcX = 0; //upper left corner in nm
		double ulcY = 0;
		double widthImg = this.getDimensions().get(1)-this.getDimensions().get(0);
		double ymin = this.getDimensions().get(2);
		double ymax = this.getDimensions().get(3);
		double heightImg = ymax-ymin;
		int nbrWindowsX =((Double) Math.floor((widthImg-widthWindow)/shiftX)).intValue();
		int nbrWindowsY =((Double) Math.floor((heightImg-heightWindow)/shiftY)).intValue();
		double[][] localSigma = new double[nbrWindowsX][nbrWindowsY];
		ExecutorService executor2 = Executors.newFixedThreadPool(8);
		for (int i = 0; i<nbrWindowsX; i++){
			System.out.println(i+" "+nbrWindowsX);
			ulcX = i * shiftX;
			ArrayList<StormLocalization> sl = this.cropCoords(ulcX, ulcX+widthWindow,ymin, ymax);
			System.out.println("sl.size() "+sl.size());
			Runnable t = new Thread(new processColumn(sl, localSigma, ymin, heightWindow, shiftY, i,dxy, dz));
			executor2.execute(t);
		}
		executor2.shutdown();
		try {
			executor2.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
		
		}
		OutputClass.saveLocalLocalizationPrecision(localSigma, path, getBasename(), processingLog);
	}
	
	public void estimateLocalLocalizationPrecision(double dxy, double dz, int widthWindow, int heightWindow, int shiftX, int shiftY){
		double ulcX = 0; //upper left corner in nm
		double ulcY = 0;
		double widthImg = this.getDimensions().get(1)-this.getDimensions().get(0);
		double heightImg = this.getDimensions().get(3)-this.getDimensions().get(2);
		int nbrWindowsX =((Double) Math.floor((widthImg-widthWindow)/shiftX)).intValue();
		int nbrWindowsY =((Double) Math.floor((heightImg-heightWindow)/shiftY)).intValue();
		double[][] localSigma = new double[nbrWindowsX][nbrWindowsY];
		for (int i = 0; i<nbrWindowsX; i++){
			
			for (int j = 0; j<nbrWindowsY; j++){
				System.out.println(i+"/"+nbrWindowsX+" "+j+"/"+nbrWindowsY);
				ulcX = i * shiftX;
				ulcY = j * shiftY;
				StormData tsd = new StormData(this.cropCoords(ulcX, ulcX+widthWindow, 
						ulcY, ulcY+heightWindow)," ");
				localSigma[i][j] = estimateLocalizationPrecisionXY(dxy, dz, tsd);
			}
		}
		OutputClass.saveLocalLocalizationPrecision(localSigma, path, getBasename(), processingLog);
	}
	
	private Double estimateLocalizationPrecisionXY(double dxy, double dz, StormData tsd){
		double sigmaXY = 0;
		try {
			ArrayList<ArrayList<StormLocalization>> traces = Utilities.findTraces(tsd.getLocs(), dxy, dxy, dz, 2, false);
			if (traces.size() == tsd.getLocs().size()){
				return 0.0;
			}
			ArrayList<ArrayList<Double>> distances = Utilities.getDistancesWithinTraces(traces);
			double binwidth = 1;
			ArrayList<ArrayList<Double>> histXY = Utilities.getHistogram(distances.get(0), binwidth);
			sigmaXY = Utilities.fitLocalizationPrecissionDistribution(histXY.get(0), histXY.get(1), 11);
		} catch (Exception e) {
			sigmaXY =0 ;
			//System.out.println("no fit found.");
			//e.printStackTrace();
		}
		return sigmaXY;
	}
	
	public void estimateLocalizationPrecision(double dxy, double dz){
		ArrayList<ArrayList<StormLocalization>> traces = Utilities.findTraces(locs, dxy, dxy, dz, 2);
		ArrayList<ArrayList<Double>> distances = Utilities.getDistancesWithinTraces(traces);
		double binwidth = 1;
		ArrayList<ArrayList<Double>> histZ = Utilities.getHistogram(distances.get(1),binwidth);
		ArrayList<ArrayList<Double>> histXY = Utilities.getHistogram(distances.get(0), binwidth);
		ArrayList<ArrayList<Double>> h = new ArrayList<ArrayList<Double>>();
		ArrayList<Double> y = Utilities.createDist(histXY.get(0), 22);
		h.add(histXY.get(0));
		h.add(y);
		OutputClass.writeLocalizationEstimationHistogram(path, getBasename(),
				histXY, histZ, binwidth, processingLog);
		double sigmaXY, sigmaZ;
		try {
			//ArrayList<Double> params = Utilities.fitLocalizationPrecissionDistribution(h.get(0), h.get(1), 5,10,1,1,1);
			sigmaXY = Utilities.fitLocalizationPrecissionDistribution(histXY.get(0), histXY.get(1), 11);
		} catch (Exception e) {
			sigmaXY =0 ;
			System.out.println("no fit found.");
			e.printStackTrace();
		}
		try {
			//ArrayList<Double> params = Utilities.fitLocalizationPrecissionDistribution(h.get(0), h.get(1), 5,10,1,1,1);
			sigmaZ = Utilities.fitGaussian1D(histZ.get(0), histZ.get(1), 11,1,1);
		} catch (Exception e) {
			sigmaZ =0 ;
			System.out.println("no fit found.");
			e.printStackTrace();
		}
		//ArrayList<Double> params = Utilities.fitLocalizationPrecissionDistribution(histXY.get(0), histXY.get(1), 10,5,1,10,1);
		//ArrayList<Double> params = Utilities.fitLocalizationPrecissionDistribution2(histXY.get(0), histXY.get(1), 10,1);
		System.out.println("sigmaXY: "+sigmaXY);
		LocalizationPrecissionEstimationHistogramLog pehl = new LocalizationPrecissionEstimationHistogramLog(path, getBasename(),
				histXY, histZ, binwidth, sigmaXY, sigmaZ, processingLog);
		logs.add(pehl);
		//System.out.println("sigmaXY: "+params.get(0)+" scale: "+params.get(1));
		//System.out.println("sigmaXY: "+params.get(0)+ " omega: "+params.get(1)+ " dc: "+params.get(2)+" A1: "+params.get(3)+ " A2: "+params.get(4));
		
	}
		
	ArrayList partitionData(int nbrIntervals, double maxDriftX, ArrayList<StormLocalization> locs) {
		ArrayList<ArrayList> subsets= new ArrayList<ArrayList>(nbrIntervals);
		ArrayList<StormLocalization> partition = new ArrayList<StormLocalization>();
		int startNextInterval = 0;
		int counter = 0;
		for (int i= 0; i<locs.size();i++) {
			partition.add(locs.get(i));
			if (locs.get(i).getX() > (counter + 1) * 2* maxDriftX - maxDriftX) { startNextInterval = i;}
			if (locs.get(i).getX() > (counter + 1) * 2* maxDriftX + maxDriftX) {
				counter = counter + 1; 
				i = startNextInterval; 
				subsets.add(partition);
				partition.clear();
			}
		}
		//last partition is not added yet.
		subsets.add(partition);
		return subsets;
	}
	
	public StormData findSubset(int minFrame, int maxFrame, boolean setZCoordToZero){
		int currframe = minFrame;
		StormData subset = new StormData();
		subset.setFname(fname);
		subset.setPath(path);
		int start = findFirstIndexForFrame(minFrame);
		int ende = findLastIndexForFrame(maxFrame);
		for (int i = start; i<ende; i++){
			if (setZCoordToZero){
				StormLocalization sl = getElement(i);
				sl.setZ(0);
				subset.addElement(sl);
			}
			else{
				subset.addElement(getElement(i));
			}
		}
		return subset;
	}
	
	public StormData findSubset(int minFrame, int maxFrame){ //only returns StormLocalizations which come from frames between minFrame and maxFrame
		return findSubset(minFrame, maxFrame, false); 
	}
	
	public void sortX(){
		isSortedByFrame = false;
		Comparator<StormLocalization> compX = new StormLocalizationXComperator();
		Collections.sort(this.locs,compX);
	}
	
	
	public ArrayList<ArrayList<Integer>> getLocsPerFrame(){
		int binWidth = 50;
		int maxFrame = ((Double) getDimensions().get(7)).intValue();
		ArrayList<Integer> frames = new ArrayList<Integer>();
		ArrayList<Integer> locsPerFrame = new ArrayList<Integer>();
		for (int i = 0; i<(maxFrame/binWidth)+1; i++){
			frames.add(i*binWidth);
			locsPerFrame.add(0);
		}
		sortFrame();
		for (int i = 0;i<getLocs().size();i++){
			locsPerFrame.set(locs.get(i).getFrame()/binWidth,locsPerFrame.get(locs.get(i).getFrame()/binWidth)+1);
		}
		
		ArrayList<ArrayList<Integer>> tmp = new ArrayList<ArrayList<Integer>>();
		tmp.add(frames);
		tmp.add(locsPerFrame);
		OutputClass.writeLocsPerFrame(path, getBasename(), tmp, binWidth, processingLog);
		LocalizationsPerFrameLog pfl = new LocalizationsPerFrameLog(path, getBasename(), tmp, binWidth, processingLog);
		logs.add(pfl);
		return tmp;
	}
	
	public void addStormData(StormData tmp) {
		if (this.getLocs().size()==0){
			this.path = tmp.getPath();
			this.fname = tmp.getFname();
		}
		int lastFrame = (int) ((double)getDimensions().get(7));
		int firstFrame = (int) ((double)tmp.getDimensions().get(6));
		for (int i = 0; i< tmp.getSize(); i++){
			StormLocalization sl = tmp.getElement(i);
			sl.setFrame(sl.getFrame()+lastFrame-firstFrame);
			getLocs().add(sl);
		}
		
	}
	public String getBasename(){
		return fname.substring(0, fname.length()-4);
	}
	
	public String getMeassurement(){
		String[] parts = path.split("\\\\");
		return parts[parts.length-3];
	}
	
	public void correctDrift(int chunksize) {
		StormData sd = FeatureBasedDriftCorrection.correctDrift(this, chunksize);
		processingLog = processingLog +"DC";
		locs = sd.getLocs();
	}

	public void addToProcessingLog(String extenstion){
		this.processingLog = this.processingLog + extenstion;
	}
	public void addToLog(Object obj){
		logs.add(obj);
	}
	public ArrayList<Object> getLog(){
		return logs;
	}

	public void createPdf() {
		// TODO Auto-generated method stub
		OutputClass.createPDF(logs, path, getBasename(), processingLog);
	}

	public void setLog(ArrayList<Object> logs){
		this.logs = logs;
	}
	public void copyAttributes(StormData sd){
		this.fname = sd.getFname();
		this.path = sd.getPath();
		this.logs = sd.getLog();
		this.processingLog = sd.getProcessingLog();
	}
	
	public ArrayList<StormLocalization> cropCoords(double xmin, double xmax, double ymin, double ymax){
		return cropCoords(xmin, xmax, ymin, ymax, this.getDimensions().get(4), this.getDimensions().get(5), this.getDimensions().get(6).intValue(), this.getDimensions().get(7).intValue());
	}
	
	public ArrayList<StormLocalization> cropCoords(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax){
		return cropCoords(xmin, xmax, ymin, ymax, zmin, zmax, this.getDimensions().get(6).intValue(), this.getDimensions().get(7).intValue());
	}
	
	public ArrayList<StormLocalization> adjustCrop(ArrayList<StormLocalization> sl, double ymin, double ymax, double xmin, double xmax){
		Comparator<StormLocalization> compY = new StormLocalizationYComperator();
		Collections.sort(sl,compY);
		Collections.sort(this.locs, compY);
		double oldYmax = ymin; 
		if(sl.get(sl.size()-1).getY()<ymin){//if there is no overlap
			sl.clear();
		}
		else{
			while (sl.get(0).getY()<ymin){
				sl.remove(0);
			}
			oldYmax = sl.get(sl.size()-1).getY();
		}
		for (int i = 0; i<this.getLocs().size(); i++){
			StormLocalization csl = this.getLocs().get(i);
			if (csl.getY()>oldYmax && csl.getY()<ymax&csl.getX()>xmin&&csl.getX()<xmax){
				sl.add(this.getLocs().get(i));
			}
			if (csl.getY()>ymax){
				break;
			}
		}
		return sl;
	}
	
	public synchronized ArrayList<StormLocalization> cropCoords(double xmin, double xmax, double ymin, double ymax, double zmin, double zmax, int framemin, int framemax){
		Comparator<StormLocalization> compX = new StormLocalizationXComperator();
		Collections.sort(this.locs,compX);
		ArrayList<StormLocalization> croppedList = new ArrayList<StormLocalization>();
		for (int i = 0; i<this.locs.size(); i++){
			if (this.locs.get(i).getX()<xmin){
				continue;
			}
			if (this.locs.get(i).getX()>xmax){
				continue;
			}
			croppedList.add(this.locs.get(i));
		}
		Comparator<StormLocalization> compY = new StormLocalizationYComperator();
		Collections.sort(croppedList,compY);
		ArrayList<StormLocalization> croppedList2 = new ArrayList<StormLocalization>();
		for (int i = 0; i<croppedList.size(); i++){
			if (croppedList.get(i).getY()<ymin){
				continue;
			}
			if (croppedList.get(i).getY()>ymax){
				continue;
			}
			croppedList2.add(croppedList.get(i));
		}
		croppedList.clear();
		Comparator<StormLocalization> compZ = new StormLocalizationZComperator();
		Collections.sort(croppedList2,compZ);
		for (int i = 0; i<croppedList2.size(); i++){
			if (croppedList2.get(i).getZ()<zmin){
				continue;
			}
			if (croppedList2.get(i).getZ()>zmax){
				continue;
			}
			croppedList.add(croppedList2.get(i));
		}
		croppedList2.clear();
		Comparator<StormLocalization> compFrame = new StormLocalizationFrameComperator();
		Collections.sort(croppedList,compFrame);
		for (int i = 0; i<croppedList.size(); i++){
			if (croppedList.get(i).getFrame()<framemin){
				continue;
			}
			if (croppedList.get(i).getFrame()>framemax){
				continue;
			}
			croppedList2.add(croppedList.get(i));
		}
		this.locs = croppedList2;
		return croppedList2;
	}

	public String getOutputPath() {
		return outputPath;
	}

	public void setOutputPath(String outputPath) {
		this.outputPath = outputPath;
	}
}
	
class processColumn implements Runnable{
	ArrayList<StormLocalization> sl;
	double[][] localSigma;
	double shiftY;
	int currentRow;
	double dxy;
	double dz;
	double minY; 
	double heightWindow;
	
	public processColumn(ArrayList<StormLocalization> sl, double[][] localSigma, double minY, 
			double heightWindow, double shiftY, int currentRow,double dxy, double dz){
		//heigthWindow must be n * shiftY with n element Natrual numbers!!!
		this.sl = sl;
		this.localSigma = localSigma;
		this.shiftY = shiftY;
		this.currentRow = currentRow;
		this.dxy = dxy;
		this.dz = dz;
		this.minY = minY;
		this.heightWindow = heightWindow;
	}
	
	public void run(){
		int n = (int) (heightWindow/ shiftY);
		ArrayList<Double> distances = new ArrayList<Double>();
		ArrayList<Integer> startingIdx = new ArrayList<Integer>();
		ArrayList<ArrayList<StormLocalization>> traces = 
			Utilities.findTraces(sl, dxy, dxy, dz, 2, false);
		Comparator<ArrayList<StormLocalization>> compY = new TraceYComperator();
		Collections.sort(traces,compY);
		Utilities.getDistances(traces, distances, startingIdx, heightWindow ,shiftY, minY);
		for(int i = 0; i<localSigma[0].length;i++){
			try {
				ArrayList<Double> tmp = new ArrayList(distances.subList(startingIdx.get(i), startingIdx.get(i+n)));
				ArrayList<ArrayList<Double>> histXY = Utilities.getHistogram(tmp, 1);
				double sigmaXY;
				if (histXY.get(0).size()<100){
					sigmaXY = -1;
				}
				else {
					sigmaXY = Utilities.fitLocalizationPrecissionDistribution(histXY.get(0), histXY.get(1), 11);
				}
				synchronized(this){
					System.out.println(currentRow+" "+i+" "+sigmaXY);
					localSigma[currentRow][i] = sigmaXY;
				}
			} catch (Exception e) {
				// TODO Auto-generated catch block
				synchronized(this){
					localSigma[currentRow][i] = -1;
					System.out.println(currentRow+" "+i+" "+"-1");
				}
				e.printStackTrace();
			}
			
		}
		System.out.println(" a ");
	}
}
