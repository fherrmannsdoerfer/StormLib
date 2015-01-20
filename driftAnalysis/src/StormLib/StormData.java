
package StormLib;

import ij.ImagePlus;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;


public class StormData {
	boolean isSortedByFrame = false;
	private ArrayList<StormLocalization> locs = new ArrayList<StormLocalization>();
	private String path;
	private String fname;
	private String processingLog = "_";
	
	public StormData(String path, String fname){
		this.path = path;
		this.fname = fname;
		importData(path+fname);
	}
	
	public StormData(StormData sl){
		this.locs = sl.getLocs();
		this.fname = sl.getFname();
		this.path = sl.getPath();
		this.processingLog = sl.getProcessingLog();
	}
	

	public StormData(){
		this.fname = "fname not set yet";
		this.path = "path not set yet";
		
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
	
	private void importData(String fullpath){
		BufferedReader br = null;
		String line = "";
		String delimiter = " ";
		try {
			int counter = 0;
			br = new BufferedReader(new FileReader(fullpath));
			line = br.readLine(); //skip header
			ArrayList<Integer> errorLines = new ArrayList<Integer>();
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
					else {System.out.println("File format not understood!");}
				}
				catch(java.lang.NumberFormatException ne){System.out.println("Problem in line:"+counter+ne); errorLines.add(counter);}
			}
			OutputClass.writeLoadingStatistics(path, getBasename(), errorLines, locs.size());
			System.out.println("File contains "+getLocs().size()+" localizations.");
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println(path+fname);
		}
		catch (IOException e) {e.printStackTrace();}
	}
	
	public void sortFrame(){
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
	
	public int findFirstIndexForFrame(int frame){ //finds the index with the first appearance of a framenumber larger or equal the given frame
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
	
	public ArrayList getDimensions(){ //returns minimal and maximal positions in an ArrayList in the following order (xmin, xmax, ymin, ymax, zmin, zmax, minFrame, maxFrame)
		double minX = Double.MAX_VALUE;
		double minY = Double.MAX_VALUE;
		double minZ = Double.MAX_VALUE;
		double maxX = 0;
		double maxY = 0;
		double maxZ = 0;
		double minFrame = Double.MAX_VALUE;
		double maxFrame = 0;
		for (int i = 0; i<getLocs().size(); i++){
			StormLocalization sl = getLocs().get(i);
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
		
		ArrayList ret = new ArrayList();
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
		return renderImage2D(pixelsize, true, processingLog); // function is also used to create images for the fourier transformation for the drift correction
	}
	public ImagePlus renderImage2D(double pixelsize, String tag) {
		return renderImage2D(pixelsize, true, tag);
	}
	public ImagePlus renderImage2D(double pixelsize, boolean saveImage){
		return renderImage2D(pixelsize, saveImage, "");
	}
	public ImagePlus renderImage2D(double pixelsize, boolean saveImage, String tag){ //render localizations from Stormdata to Image Plus Object
		double sigma = 20/pixelsize; //in nm sigma to blur localizations
		int filterwidth = 3; // must be odd
		ArrayList<Double> dims = getDimensions();
		int pixelX, pixelY;
		if (saveImage){
			pixelX =(int) Math.pow(2, Math.ceil(Math.log(dims.get(1) / pixelsize)/Math.log(2)));
			pixelY = (int) Math.pow(2, Math.ceil(Math.log(dims.get(3) / pixelsize)/Math.log(2)));
			//pixelX = (int) Math.ceil(dims.get(1)/pixelsize);
			//pixelY = (int) Math.ceil(dims.get(3)/pixelsize);
		}
		else{
			//finds nearest power of 2 to either the width or height of the image, depending on which number is larger
			
			int dimsImg = Math.max((int) Math.pow(2, Math.ceil(Math.log(dims.get(1) / pixelsize)/Math.log(2))), (int) Math.pow(2, Math.ceil(Math.log(dims.get(3) / pixelsize)/Math.log(2))));
			pixelX = dimsImg;
			//int pixelX = (int) Math.ceil(dims.get(1) / pixelsize);
			pixelY = dimsImg;
		}
		
		float [][] image = new float[pixelX][pixelY];
		image = addFilteredPoints(image, sigma, filterwidth, pixelsize, getLocs());
		ImageProcessor ip = new FloatProcessor(pixelX,pixelY);
		ip.setFloatArray(image);
		ImagePlus imgP = new ImagePlus("", ip);
		//System.out.println("Image rendered ("+imgP.getWidth()+"*"+imgP.getHeight()+")");
		if (saveImage){
			OutputClass.save2DImage(path, getBasename(), processingLog, imgP, pixelsize);
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
		OutputClass.writeDemixingHistogram(path, getBasename(), histData, binWidth, tag);
		coloredImage = renderDemixing(coloredImage, sigma, filterwidth, pixelsize, params);
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
		
		OutputClass.saveDemixingImage(path, getBasename(), processingLog, colImg, params, pixelsize);
		
		return colImg;
	}
	
	ArrayList<float[][]> renderDemixing(ArrayList<float[][]> coloredImage, double sigma, int filterwidth, double pixelsize, DemixingParameters params){
		if (filterwidth %2 == 0) {System.err.println("filterwidth must be odd");}
		double minAngle1 = params.getAngle1() - params.getWidth1()/2;
		double maxAngle1 = params.getAngle1() + params.getWidth1()/2;
		double minAngle2 = params.getAngle2() - params.getWidth2()/2;
		double maxAngle2 = params.getAngle2() + params.getWidth2()/2;
		double factor = 10000*1/(2*Math.PI*sigma*sigma);
		double factor2 = -0.5/sigma/sigma;
		ArrayList<Double> dims = getDimensions();
		double zMin = dims.get(4);
		double zMax = dims.get(5);
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
						}
						else if ((sl.getAngle()> minAngle2 && sl.getAngle()< maxAngle2) || sl.getAngle() == Math.PI/2){
							greenChannel[k][l] = greenChannel[k][l] + (float)((sl.getIntensity())*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
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
		double sigma = 10/pixelsize; //in nm sigma to blur localizations
		int filterwidth = 3; // must be odd
		ArrayList<Double> dims = getDimensions();
		int pixelX = (int) Math.pow(2, Math.ceil(Math.log(dims.get(1) / pixelsize)/Math.log(2)));
		//int pixelX = (int) Math.ceil(dims.get(1) / pixelsize);
		int pixelY = pixelX;//(int) Math.ceil(dims.get(1) / pixelsize);
		float [][] imageRed = new float[pixelX][pixelY];
		float [][] imageGreen = new float[pixelX][pixelY];
		float [][] imageBlue = new float[pixelX][pixelY];
		ArrayList<float[][]> coloredImage = new ArrayList<float[][]>();
		coloredImage.add(imageRed);
		coloredImage.add(imageGreen);
		coloredImage.add(imageBlue);
		coloredImage = addFilteredPoints(coloredImage, sigma, filterwidth, pixelsize, getLocs());
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
		
		
		OutputClass.save3DImage(path, getBasename(), tag, colImg, pixelsize);
		return colImg;
	}
	
	float[][] addFilteredPoints(float[][] image, double sigma, int filterwidth, double pixelsize, ArrayList<StormLocalization> sd){
		if (filterwidth %2 == 0) {System.err.println("filterwidth must be odd");}
		double factor = 10000*1/(2*Math.PI*sigma*sigma);
		double factor2 = -0.5/sigma/sigma;
		//System.out.println(sd.getSize());
		for (int i = 1; i<getSize(); i++){
			StormLocalization sl = sd.get(i);
			double posX = sl.getX()/pixelsize; //position of current localization
			double posY = sl.getY()/pixelsize;
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
		System.out.println("zMax: "+zMax);
		float[][] redChannel = coloredImage.get(0);
		float[][] greenChannel = coloredImage.get(1);
		float[][] blueChannel = coloredImage.get(2);
		for (int i = 1; i<getSize(); i++){
			StormLocalization sl = sd.get(i);
			double posX = sl.getX()/pixelsize; //position of current localization
			double posY = sl.getY()/pixelsize;
			double posZ = sl.getZ();
			int pixelXStart = (int)Math.floor(posX) - (filterwidth-1)/2;
			int pixelYStart = (int)Math.floor(posY) - (filterwidth-1)/2;
			for (int k = pixelXStart; k<pixelXStart+ filterwidth;k++){
				for(int l= pixelYStart; l<pixelYStart+ filterwidth;l++){
					try{
						if (false){
							if (posZ < 0.25* zMax){
								//redChannel[k][l] = redChannel[k][l] + (float)((0)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								//greenChannel[k][l] = greenChannel[k][l] + (float)((posZ)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								//blue rises from 0 to 1
								blueChannel[k][l] = blueChannel[k][l] + (float)((4*posZ / zMax)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								
							}
							else if (posZ < 0.5* zMax){
								//redChannel[k][l] = redChannel[k][l] + (float)((0)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								//green rises from 0 to 1 blue stays one
								greenChannel[k][l] = greenChannel[k][l] + (float)((4*posZ/zMax - 1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								blueChannel[k][l] = blueChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else if (posZ < 0.75* zMax){
								//green stays one, blue goes to zero again
								//redChannel[k][l] = redChannel[k][l] + (float)((0)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								greenChannel[k][l] = greenChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								blueChannel[k][l] = blueChannel[k][l] + (float)((3 - 4*posZ/zMax)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else {
								//green goes to zero red rises
								redChannel[k][l] = redChannel[k][l] + (float)((4*posZ/zMax - 3)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								greenChannel[k][l] = greenChannel[k][l] + (float)((4-4*posZ/zMax)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								//blueChannel[k][l] = blueChannel[k][l] + (float)((0)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
						}
						else{
							double parts = 6;
							double invparts = 1./parts;
							if (posZ < invparts * zMax){
								blueChannel[k][l] = blueChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								greenChannel[k][l] = greenChannel[k][l] + (float)((parts*posZ/zMax)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else if (posZ<invparts * 2*zMax){
								greenChannel[k][l] = greenChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								blueChannel[k][l] = blueChannel[k][l] + (float)((1-parts*(posZ/zMax-invparts))*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else if (posZ<invparts * 3*zMax){
								greenChannel[k][l] = greenChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								redChannel[k][l] = redChannel[k][l] + (float)((parts*(posZ/zMax - invparts*2))*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else if (posZ<invparts * 4*zMax){
								redChannel[k][l] = redChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								greenChannel[k][l] = greenChannel[k][l] + (float)((1-parts*(posZ/zMax-invparts*3))*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else if (posZ<invparts * 5*zMax){
								redChannel[k][l] = redChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								blueChannel[k][l] = blueChannel[k][l] + (float)((parts*(posZ/zMax-invparts*4))*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
							}
							else if (posZ<invparts * 6*zMax){
								redChannel[k][l] = redChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								greenChannel[k][l] = greenChannel[k][l] + (float)((parts*(posZ/zMax-invparts*5))*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
								blueChannel[k][l] = blueChannel[k][l] + (float)((1)*factor * Math.exp(factor2*(Math.pow((k-posX),2)+Math.pow((l-posY),2))));
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
					} catch(IndexOutOfBoundsException e){e.toString();}
				}
			}
		}
		
		ArrayList<float[][]> normalizedChannels = normalizeChannels(redChannel, greenChannel, blueChannel);
		coloredImage.clear();
		coloredImage.add(normalizedChannels.get(0));
		coloredImage.add(normalizedChannels.get(1));
		coloredImage.add(normalizedChannels.get(2));
		return coloredImage;
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
		double percentile = 0.98;
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
	}
	
	
	public ArrayList<StormLocalization> connectPoints(double dx, double dy, double dz, int maxdistBetweenLocalizations) {
		// TODO Auto-generated method stub
		ArrayList<ArrayList<StormLocalization>> traces = Utilities.findTraces(locs, dx, dy, dz, maxdistBetweenLocalizations);
		ArrayList<StormLocalization> connectedLoc = Utilities.connectTraces(traces);
		OutputClass.writeConnectionResult(path,getBasename(),connectedLoc.size(), locs.size(),processingLog + "Con");
		this.locs = connectedLoc;
		this.processingLog += "Con"; 
		return connectedLoc;
	}
	
	public void estimateLocalizationPrecision(double dxy, double dz){
		ArrayList<ArrayList<StormLocalization>> traces = Utilities.findTraces(locs, dxy, dxy, dz, 1);
		ArrayList<ArrayList<Double>> distances = Utilities.getDistancesWithinTraces(traces);
		
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
	
	StormData findSubset(int minFrame, int maxFrame, boolean setZCoordToZero){
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
	
	StormData findSubset(int minFrame, int maxFrame){ //only returns StormLocalizations which come from frames between minFrame and maxFrame
		return findSubset(minFrame, maxFrame, false); 
	}
	
	void sortX(){
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
		return tmp;
	}
	
	public void addStormData(StormData tmp) {
		int lastFrame = (int) ((double)getDimensions().get(7));
		for (int i = 0; i< tmp.getSize(); i++){
			StormLocalization sl = tmp.getElement(i);
			sl.setFrame(sl.getFrame()+lastFrame);
			getLocs().add(sl);
		}
		
	}
	public String getBasename(){
		return fname.substring(0, fname.length()-4);
	}

	public void correctDrift(int chunksize) {
		StormData sd = FeatureBasedDriftCorrection.correctDrift(this, chunksize);
		processingLog = processingLog +"DC";
		locs = sd.getLocs();
	}

	public void addToProcessingLog(String extenstion){
		this.processingLog = this.processingLog + extenstion;
	}
}