package StormLib;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Iterator;

import org.apache.commons.math3.analysis.UnivariateFunction;
import org.apache.commons.math3.analysis.interpolation.SplineInterpolator;
import org.apache.commons.math3.analysis.interpolation.UnivariateInterpolator;

import ij.ImagePlus;
import ij.process.FHT;
import ij.process.FloatProcessor;
import ij.process.ImageProcessor;
import net.imglib2.Cursor;
import net.imglib2.RandomAccess;
import net.imglib2.img.ImagePlusAdapter;
import net.imglib2.img.Img;
import net.imglib2.img.ImgFactory;
import net.imglib2.img.array.ArrayImg;
import net.imglib2.img.array.ArrayImgFactory;
import net.imglib2.img.array.ArrayImgs;
import net.imglib2.img.basictypeaccess.array.FloatArray;
import net.imglib2.img.cell.CellImgFactory;
import net.imglib2.img.display.imagej.ImageJFunctions;
import net.imglib2.type.numeric.real.FloatType;
import net.imglib2.type.numeric.complex.ComplexFloatType;
import net.imglib2.type.numeric.integer.UnsignedByteType;
import net.imglib2.algorithm.fft.FourierConvolution;
import net.imglib2.algorithm.fft.FourierTransform;
import net.imglib2.algorithm.fft.InverseFourierTransform;
import net.imglib2.algorithm.fft2.FFT;
import net.imglib2.converter.ComplexImaginaryFloatConverter;
import net.imglib2.converter.ComplexPhaseFloatConverter;
import net.imglib2.converter.ComplexRealFloatConverter;
import net.imglib2.exception.IncompatibleTypeException;
import Jama.*;
import StormLib.HelperClasses.DriftCorrectionLog;

public class FeatureBasedDriftCorrection {
	public static StormData correctDrift(StormData sd ,int chunksize)
	{
		//int chunksize = 5000;  //number frames that are summed up to calculate drift between this frame and the next
		int pixelsize = 20; //pixelsize of intermediate reconstructed images used for fouriertransformations
		ArrayList<double[][]> dds = finddisplacements2(sd, chunksize, pixelsize); //dds contains the displacement matrices for x and y. the displacement beteween each chunk is calculated
		StormData sdTrans = correctLocalizations(sd,dds,chunksize, pixelsize); //uses the displacement to perform a spline interpolation and correct the data accordingly
		return sdTrans;
	}
	
	static ArrayList<double[]> calculateAbsoluteDrift(double[][] dds1, double[][] dds2, int chunksize){
		int nbrChunks = dds1[0].length; //the idea is that also the information about the shift
		//for example from the first to the third and fourth chunk is considered and not only the shift of consecutive chunks
		double frames[] = new double[nbrChunks+2];
		frames[0] = 0;
		frames[1] = chunksize / 2;
		double dx[] = new double[nbrChunks+2];
		double dy[] = new double[nbrChunks+2];

		for (int i = 1;i<nbrChunks;i++){
			double tmpx = 0;
			double tmpy = 0;
			for (int j = 0;j<i;j++){
				tmpx = tmpx + dds1[i][j]- dds1[i-1][j];
				tmpy = tmpy + dds2[i][j]- dds2[i-1][j];
			}
			dx[i+1] = -tmpx / i; //dy and dx contain the difference between the chunks
			dy[i+1] = -tmpy / i; //mean of all differences that describe the shift between
			//the i th and the previous chunk
			frames[i+1] = frames[i]+chunksize;
		}
		dx[0] = 0;				//the first frame has no drift
		dy[0] = 0;
		dx[1] = (dx[2])/2; //the middle of the first block gets an back interpolated value
		dy[1] = (dy[2])/2; //based on the difference between the middle of the first chunk and the middle of the second chunk
		dx[nbrChunks+1] = (dx[nbrChunks]) ;// the end of the last chunk has to be interpolated also
		dy[nbrChunks+1] = (dy[nbrChunks]) ;// 
		frames[nbrChunks+1] = frames[nbrChunks]+chunksize;//this is the last frame of the last chunk
		for (int i = 0;i<nbrChunks;i++){
		dx[i+1] = dx[i] + dx[i+1]; //sum the shift up so that dx now holds the displacement relative to the first frame
		dy[i+1] = dy[i] + dy[i+1];
		}
		ArrayList<double[]> retList = new ArrayList<double[]>();
		retList.add(dx);
		retList.add(dy);
		retList.add(frames);
		return retList;
	}
	
	static ArrayList<UnivariateFunction> getInterpolation(ArrayList<double[][]> dds, int chunksize){
		
		ArrayList<double[]> displXY = calculateAbsoluteDrift(dds.get(0), dds.get(1), chunksize);
		ArrayList<double[]> displXZ = calculateAbsoluteDrift(dds.get(2), dds.get(3), chunksize);
		ArrayList<double[]> displYZ = calculateAbsoluteDrift(dds.get(4), dds.get(5), chunksize);
		ArrayList<Double> aldx = new ArrayList<Double>();
		ArrayList<Double> aldy = new ArrayList<Double>();
		ArrayList<Double> aldz = new ArrayList<Double>();
		double[] frames = displXY.get(2);
		for (int i = 0;i<displXY.get(0).length; i++){
			aldx.add((displXY.get(0)[i]+displXZ.get(0)[i])/2.);
			aldy.add((displXY.get(1)[i]+displYZ.get(0)[i])/2.);
			aldz.add((displXZ.get(1)[i]+displYZ.get(1)[i])/2.);
		}
		double[] dx = new double[aldx.size()];
		double[] dy = new double[aldy.size()];
		double[] dz = new double[aldz.size()];
		for (int i = 0; i<aldx.size(); i++){
			dx[i] = aldx.get(i);
			dy[i] = aldy.get(i);
			dz[i] = aldz.get(i);
		}
		UnivariateInterpolator i = new SplineInterpolator(); //for the interpolation dx and dy must contain the shift relative to the first frame
		UnivariateFunction fx = i.interpolate(frames, dx);
		UnivariateFunction fy = i.interpolate(frames, dy);
		UnivariateFunction fz = i.interpolate(frames, dz);
		ArrayList<UnivariateFunction> retList = new ArrayList<UnivariateFunction>();
		retList.add(fx);
		retList.add(fy);
		retList.add(fz);
		return retList;
	}
	
	static StormData correctLocalizations(StormData sd, ArrayList<double[][]> dds, int chunksize, int pixelsize){//corrects the data for drift an returns an StormData set
		/*
		int nbrChunks = dds.get(0)[0].length; //the idea is that also the information about the shift
												//for example from the first to the third and fourth chunk is considered and not only the shift of consecutive chunks
		double frames[] = new double[nbrChunks+2];
		frames[0] = 0;
		frames[1] = chunksize / 2;
		double dx[] = new double[nbrChunks+2];
		double dy[] = new double[nbrChunks+2];
		for (int i = 1;i<nbrChunks;i++){
			double tmpx = 0;
			double tmpy = 0;
			for (int j = 0;j<i;j++){
				tmpx = tmpx + dds.get(0)[i][j]- dds.get(0)[i-1][j];
				tmpy = tmpy + dds.get(1)[i][j]- dds.get(1)[i-1][j];
			}
			dx[i+1] = -tmpx / i; //dy and dx contain the difference between the chunks
			dy[i+1] = -tmpy / i; //mean of all differences that describe the shift between
							//the i th and the previous chunk
			frames[i+1] = frames[i]+chunksize;
		}
		dx[0] = 0;				//the first frame has no drift
		dy[0] = 0;
		dx[1] = (dx[2])/2; //the middle of the first block gets an back interpolated value
		dy[1] = (dy[2])/2; //based on the difference between the middle of the first chunk and the middle of the second chunk
		dx[nbrChunks+1] = (dx[nbrChunks]) ;// the end of the last chunk has to be interpolated also
		dy[nbrChunks+1] = (dy[nbrChunks]) ;// 
		frames[nbrChunks+1] = frames[nbrChunks]+chunksize;//this is the last frame of the last chunk
		for (int i = 0;i<nbrChunks;i++){
			dx[i+1] = dx[i] + dx[i+1]; //sum the shift up so that dx now holds the displacement relative to the first frame
			dy[i+1] = dy[i] + dy[i+1];
		}
		UnivariateInterpolator i = new SplineInterpolator(); //for the interpolation dx and dy must contain the shift relative to the first frame
		UnivariateFunction fx = i.interpolate(frames, dx);
		UnivariateFunction fy = i.interpolate(frames, dy); */
		int nbrChunks = dds.get(0)[0].length;
		ArrayList<UnivariateFunction> aluf = getInterpolation(dds, chunksize);
		UnivariateFunction fx = aluf.get(0);
		UnivariateFunction fy = aluf.get(1);
		UnivariateFunction fz = aluf.get(2);
		boolean safeMode = true; //savemode skips localizations from the first and last chunk
		StormData sdTrans = new StormData();
		sdTrans.setPath(sd.getPath());
		sdTrans.setFname(sd.getFname());
		sdTrans.setProcessingLog(sd.getProcessingLog()+"DC");
		int counter = 0;
		for (int j = 0; j<sd.getSize();j++){
			int frame = sd.getElement(j).getFrame();
			/*if (safeMode&&frame>chunksize && frame<(nbrChunks-1)*chunksize){	
				double x = sd.getElement(j).getX()+fx.value(frame)*pixelsize;
				double y = sd.getElement(j).getY()+fy.value(frame)*pixelsize;
				sdTrans.addElement(new StormLocalization(x, y, sd.getElement(j).getZ(), frame, sd.getElement(j).getIntensity()));
			}
			else{
				counter = counter + 1;
			}*/
			double x = sd.getElement(j).getX()+fx.value(frame)*pixelsize;
			double y = sd.getElement(j).getY()+fy.value(frame)*pixelsize;
			double z = sd.getElement(j).getZ()+fz.value(frame)*pixelsize;
			sdTrans.addElement(new StormLocalization(x, y, z, frame, sd.getElement(j).getIntensity(), sd.getElement(j).getAngle()));
		}
		System.out.println(counter+" Localizations were skipped.");
		Double frameMax2 = (double)sd.getDimensions().get(7);
		int frameMax = frameMax2.intValue()-chunksize;
		OutputClass.writeDriftLogFile(dds,fx,fy,fz,sd.getPath(), sd.getBasename(), frameMax,sd.getProcessingLog());
		DriftCorrectionLog cl = new DriftCorrectionLog(dds,fx,fy,fz,sd.getPath(), sd.getBasename(), frameMax, chunksize, nbrChunks, sd.getProcessingLog());
		sd.addToLog(cl);
		
		return sdTrans;
	}
	
	static ArrayList<Double> findmaximumgauss(ImagePlus img, int window){
		int centerImg =(int) img.getWidth()/2; //assuming that img is rectangular 
		ImageProcessor ip = img.getProcessor();
		ip.setRoi(centerImg - window,centerImg - window, 2*window, 2*window);
		ImageProcessor center = ip.crop();
		center.blurGaussian(2);
		double maximum = 0;
		int posX = -1;
		int posY = -1;
		for(int i = 0; i< center.getWidth();i++){
			for(int j= 0; j< center.getHeight(); j++){
				if (center.getPixelValue(i, j)>maximum){
					maximum = center.getPixelValue(i, j);
					posX = i;
					posY = j;
				}
			}
		}
		ArrayList<Double> centerCoords = new ArrayList<Double>();
		centerCoords.add(0.0);
		centerCoords.add(0.0);
		try {
			centerCoords = fitGaussian2D(new ImagePlus("",center), 5, maximum, 0, posX, posY);
			centerCoords.set(0, centerCoords.get(0)+centerImg-window-1);
			centerCoords.set(1, centerCoords.get(1)+centerImg-window-1);
			//System.out.println("Posx: "+posX+" posY: "+posY+"fitted x: "+centerCoords.get(0)+" Fitted y: "+centerCoords.get(1));
		} catch (EOFException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return centerCoords;
	}
	
	static ArrayList<Double> findDisplacementBetweenFrames(ImageProcessor ip1, ImageProcessor ip2, int window){
		FHT fftk = new FHT(ip1);
		FHT fftl = new FHT(ip2);
		FHT fftCM = fftk.conjugateMultiply(fftl);
		fftCM.inverseTransform();
		fftCM.swapQuadrants();
		ImageProcessor retransformed = fftCM.convertToFloat();
		ImagePlus retransformed2D = new ImagePlus("", retransformed);
		//ij.IJ.save(retransformed2D, "c:\\tmp2\\retransformed2D"+k+"_"+l+".tiff");
		//System.out.println("k: "+k+" l: "+l);
		ArrayList<Double> mxmy = new ArrayList<Double>();
		mxmy = findmaximumgauss(retransformed2D, window);
		double dxh = mxmy.get(0) - ip1.getWidth()/2+1;
		double dyh = mxmy.get(1) - ip1.getWidth()/2+1;
		ArrayList<Double> retList = new ArrayList<Double>();
		retList.add(dxh);
		retList.add(dyh);
		return retList;
	}
		
	static ArrayList<double[][]> finddisplacements2(StormData sd, int chunksize, int pixelsize){
		ArrayList<ArrayList<ImagePlus>> movie = makemovie(sd, chunksize, pixelsize);
		int window = 90;
		int numFrames = movie.get(0).size();
		double [][] ddxXY = new double[numFrames][numFrames];
		double [][] ddyXY = new double[numFrames][numFrames];
		double [][] ddxXZ = new double[numFrames][numFrames];
		double [][] ddzXZ = new double[numFrames][numFrames];
		double [][] ddyYZ = new double[numFrames][numFrames];
		double [][] ddzYZ = new double[numFrames][numFrames];
		//for (int k = 0; k< numFrames - 1; k++){
		//for (int k = 0; k< 1; k++){ only upper row
		for (int k = 0; k< numFrames - 1; k++){
			for (int l = k+1; l< numFrames; l++){
				for(int m = 0; m<3; m++){
				//for (int l = k+1; l< k+2; l++){
					ArrayList<Double> displacements = findDisplacementBetweenFrames(movie.get(0).get(k).getProcessor(),
							movie.get(0).get(l).getProcessor(), window);
					ddxXY[k][l] = displacements.get(0);
					ddyXY[k][l] = displacements.get(1);
					ddxXY[l][k] = -displacements.get(0);
					ddyXY[l][k] = -displacements.get(1);
					
					displacements = findDisplacementBetweenFrames(movie.get(1).get(k).getProcessor(),
							movie.get(1).get(l).getProcessor(), window);
					ddxXZ[k][l] = displacements.get(0);
					ddzXZ[k][l] = displacements.get(1);
					ddxXZ[l][k] = -displacements.get(0);
					ddzXZ[l][k] = -displacements.get(1);
					
					displacements = findDisplacementBetweenFrames(movie.get(2).get(k).getProcessor(),
							movie.get(2).get(l).getProcessor(), window);
					ddyYZ[k][l] = displacements.get(0);
					ddzYZ[k][l] = displacements.get(1);
					ddyYZ[l][k] = -displacements.get(0);
					ddzYZ[l][k] = -displacements.get(1);
					/*FHT fftk = new FHT(movie.get(0).get(k).getProcessor());
					FHT fftl = new FHT(movie.get(0).get(l).getProcessor());
					FHT fftCM = fftk.conjugateMultiply(fftl);
					fftCM.inverseTransform();
					fftCM.swapQuadrants();
					ImageProcessor retransformed = fftCM.convertToFloat();
					ImagePlus retransformed2D = new ImagePlus("", retransformed);
					//ij.IJ.save(retransformed2D, "c:\\tmp2\\retransformed2D"+k+"_"+l+".tiff");
					//System.out.println("k: "+k+" l: "+l);
					ArrayList<Double> mxmy = new ArrayList<Double>();
					mxmy = findmaximumgauss(retransformed2D, window);
					double dxh = mxmy.get(0) - movie.get(0).get(0).getProcessor().getWidth()/2+1;
					double dyh = mxmy.get(1) - movie.get(0).get(0).getProcessor().getWidth()/2+1;
					ddxXY[k][l] = dxh;
					ddyXY[k][l] = dyh;
					ddxXY[l][k] = -dxh;
					ddyXY[l][k] = -dyh;*/
				}
			}
		}
		ArrayList<double[][]> ret = new ArrayList<double[][]>();
		ret.add(ddxXY);
		ret.add(ddyXY);
		ret.add(ddxXZ);
		ret.add(ddzXZ);
		ret.add(ddyYZ);
		ret.add(ddzYZ);
		return ret;
	}
	
	static ArrayList<ArrayList<ImagePlus>> makemovie(StormData sd, int chunksize, int pixelsize){
		ArrayList<ArrayList<ImagePlus>> movie = new ArrayList<ArrayList<ImagePlus>>();
		ArrayList<Double> dims = sd.getDimensions();
		int startFrame = dims.get(6).intValue();
		int endFrame = dims.get(7).intValue();
		int i = startFrame;
		int counter = 0;
		ArrayList<ImagePlus> imgsxy = new ArrayList<ImagePlus>();
		ArrayList<ImagePlus> imgsxz = new ArrayList<ImagePlus>();
		ArrayList<ImagePlus> imgsyz = new ArrayList<ImagePlus>();
		
		int pixelX =(int) Math.pow(2, Math.ceil(Math.log(dims.get(1) / pixelsize)/Math.log(2)));
		int pixelY = (int) Math.pow(2, Math.ceil(Math.log(dims.get(3) / pixelsize)/Math.log(2)));
		int longestDimension = (int) Math.max(pixelX, pixelY);//it might be that different subsets have different
		//widths or heights, especially if the longer dimension is the one projected on
		while (i<endFrame){
			//System.out.println(i+"\\"+endFrame);
			
			//ij.IJ.save(transformedImage2D,"c:\\tmp2\\movie"+counter+".tiff");
			StormData subset = sd.findSubset(i,i+chunksize);
			ArrayList<ImagePlus> imgs = calculateFourierTransforms(subset,pixelsize,longestDimension);
			imgsxy.add(imgs.get(0));
			imgsxz.add(imgs.get(1));
			imgsyz.add(imgs.get(2));
			i = i + chunksize;
			counter = counter +1;
		}	
		movie.add(imgsxy);
		movie.add(imgsxz);
		movie.add(imgsyz);
		return movie;
	}
	
	static ArrayList<ImagePlus> calculateFourierTransforms(StormData subset, int pixelsize, int longestDimension){
		ArrayList<ImagePlus> retImg = new ArrayList<ImagePlus>();
		ImagePlus imgXY = subset.renderImage2D(pixelsize, false,"",0,longestDimension);
		ImagePlus imgXZ = subset.renderImage2D(pixelsize, false,"",1,longestDimension);
		ImagePlus imgYZ = subset.renderImage2D(pixelsize, false,"",2,longestDimension);
		ArrayList<ImagePlus> realImgs = new ArrayList<ImagePlus>();
		realImgs.add(imgXY);
		realImgs.add(imgXZ);
		realImgs.add(imgYZ);
		//ij.IJ.save(img, "c:\\tmp2\\origImg"+i+".tiff");
		for (int i = 0; i<realImgs.size(); i++){
			FHT fft3 = new FHT(realImgs.get(i).getProcessor());
			fft3.rc2DFHT((float[])fft3.getPixels(), false, realImgs.get(i).getWidth());
			ImageProcessor transformedImageIP = fft3.convertToFloat();
			ImagePlus transformedImage2D = new ImagePlus("", transformedImageIP);
			retImg.add(transformedImage2D);
		}
		return retImg;
	}

	float[][] addFilteredPoints(float[][] image, double sigma, int filterwidth, double pixelsize, StormData sd){
		if (filterwidth %2 == 0) {System.err.println("filterwidth must be odd");}
		double factor = 10000*1/(2*Math.PI*sigma*sigma);
		double factor2 = -0.5/sigma/sigma;
		//System.out.println(sd.getSize());
		for (int i = 1; i<sd.getSize(); i++){
			StormLocalization sl = sd.getElement(i);
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
	
	Img<FloatType> addFilteredPointsImage(Img<FloatType> image, double sigma, int filterwidth, double pixelsize, StormData sd){
		if (filterwidth %2 == 0) {System.err.println("filterwidth must be odd");}
		double factor = 1/(2*Math.PI*sigma*sigma);
		//System.out.println(sd.getSize());
		RandomAccess< FloatType > r = image.randomAccess();
		
		for (int i = 1; i<sd.getSize(); i++){
			StormLocalization sl = sd.getElement(i);
			double posX = sl.getX()/pixelsize; //position of current localization
			double posY = sl.getY()/pixelsize;
			int pixelXStart = (int)Math.floor(posX) - (filterwidth-1)/2;
			int pixelYStart = (int)Math.floor(posY) - (filterwidth-1)/2;
			for (int k = pixelXStart; k<pixelXStart+ filterwidth;k++){
				for(int l= pixelYStart; l<pixelYStart+ filterwidth;l++){
					try{
						r.setPosition(k,0);
						r.setPosition(l,1);
						final FloatType t = r.get();
						t.set((float) ( factor * Math.exp(-0.5/sigma/sigma*(Math.pow((k-posX),2)+Math.pow((l-posY),2)))));
					} catch(IndexOutOfBoundsException e){e.toString();}
				}
			}
		}
		return image;
	}
	
	
	
	//Levenberg Marquard 2D Gaussian fit, return center in pixel
	static ArrayList<Double> fitGaussian2D(ImagePlus img, double sigma, double scale, double offset, double x0, double y0) throws EOFException{
	    double tr = 0.0;
		double t = 1.4, l = 0.1;

		for(int k=0; k< 10; ++k)
		{
			double [][] jr = new double[5][1];
			for(int i = 0; i<5; i++){
				jr[i][0] = 0.0;
			}
			double [] j = new double[5];
			for(int i = 0; i<5; i++){
				j[i] = 0.0;
			}
			double [][] jj = new double[5][5];
			for (int i = 0; i<5;i++){
				for (int p=0;p<5;p++){
					jj[i][p] = 0.0;
				}
			}
		 
			for(int x = 0; x<img.getWidth(); x++){
				for(int y = 0; y<img.getHeight();y++){
					double xs = Math.pow((x-x0)/sigma,2)+Math.pow((y-y0)/sigma, 2);
					double e = Math.exp(-0.5*xs);
					double r = img.getProcessor().getPixelValue(x, y) - (scale * e + offset);
					j[0] = (scale*e*xs/sigma);
					j[1] = (e);
					j[2] = (1.0);
					j[3] = (scale * e*(x-x0)/Math.pow(sigma,2));
					j[4] =(scale * e*(y-y0)/Math.pow(sigma,2));
					
					for (int i = 0; i<5; i++){ //jr += r*j
						jr[i][0]=(jr[i][0] + r * j[i]);
					}
					for (int i1 =0; i1<5; i1++){ //jj+=j*transpose(j)
						for (int i2 = 0; i2<5;i2++){
							jj[i1][i2] = jj[i1][i2] + j[i1]*j[i2];
						}
					}
					tr = tr + Math.pow(r, 2);
					
				}
			}
	
			Matrix jj1 = new Matrix(jj);
			Matrix jj2 = new Matrix(jj);
			Matrix d1 = new Matrix(5,1);
			Matrix d2 = new Matrix(5,1);
			
			for (int i = 0; i<5;i++){
				jj1.set(i, i, jj1.get(i,i)+l);
				jj2.set(i, i, jj2.get(i, i)+l/t);
			}
			d1 = jj1.solve(new Matrix(jr));
			d2 = jj2.solve(new Matrix(jr));
			
		    double si1 = sigma + d1.get(0,0), s1 = scale + d1.get(1,0), o1 = offset + d1.get(2,0), c1 = x0 + d1.get(3,0), g1 = y0 + d1.get(4,0);
		    double si2 = sigma + d2.get(0,0), s2 = scale + d2.get(1,0), o2 = offset + d2.get(2,0), c2 = x0 + d2.get(3,0), g2 = y0 + d2.get(4,0);
		    double tr1 = 0.0, tr2 = 0.0;

		    for(int x = 0; x<img.getWidth(); x++){
				for(int y = 0; y<img.getHeight();y++){
					double r1 = img.getProcessor().getPixelValue(x, y) - (s1 * Math.exp(-0.5 * (Math.pow((x - c1) / si1,2) + Math.pow(y - g1 ,2)))+ o1);
		            double r2 = img.getProcessor().getPixelValue(x, y) - (s2 * Math.exp(-0.5 * (Math.pow((y - c2) / si2,2) + Math.pow(y - g2 ,2)))+ o2);
		            tr1 += Math.pow(r1,2);
		            tr2 += Math.pow(r2,2);
				}
		    }
		    
		    if(tr1 < tr2)
		    {
		        if(tr1 < tr)
		        {
		            sigma = si1;
		            scale = s1;
		            offset = o1;
		            x0 = c1;
					y0 = g1;
		        }
		        else
		        {
		            l *= t;
		        }
		    }
		    else
		    {
		        if(tr2 < tr)
		        {
		            sigma = si2;
		            scale = s2;
		            offset = o2;
		            x0 = c2;
					y0 = g2;
		            l /= t;
		        }
		        else
		        {
		            l *= t;
		        }
		    }

			if(Math.abs((tr - Math.min(tr1, tr2)) / tr) < 1e-15)
		        break;
		}
		ArrayList<Double> res = new ArrayList<Double>();
		res.add(x0);
		res.add(y0);
		return res;
	}
}
