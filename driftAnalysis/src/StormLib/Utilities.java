package StormLib;

import ij.ImagePlus;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.EOFException;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

import comperators.StormLocalizationFrameComperator;
import dataStructure.StormData;
import dataStructure.StormLocalization;
import Jama.Matrix;

public class Utilities {
	private static PropertyChangeSupport propertyChangeSupport =
		       new PropertyChangeSupport(Utilities.class);
	private static int lastVal;
	
	public static void addPropertyChangeListener(PropertyChangeListener listener) {
	       propertyChangeSupport.addPropertyChangeListener(listener);
	   }

	   public static void setProgress(String messageName, int val) {
		  propertyChangeSupport.firePropertyChange(messageName, lastVal, val);
		  lastVal = val;
	   }
	   public static StormData openSeries(String path1, String pattern1){
			File folder = new File(path1);
			File[] files = folder.listFiles();
			
			try {
				OutputClass.createOutputFolder(path1);
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			StormData sd1 = new StormData();
		
			try{
				Arrays.sort(files);
				for (int i = 0; i<files.length; i++ ){
					if(isValidInputFile(files[i], pattern1)){
						StormData tmp = new StormData(files[i].getParent(),files[i].getName());
						sd1.addStormData(tmp);
						setProgress("MultipleInput",(int)(100.*i/(files.length)));
					}
				}
				sd1.setPath(path1);
				sd1.setFname(pattern1+"mergedFile"+".txt");
			}
			catch(NullPointerException e){
				System.out.println(e.getMessage());
			}
			return sd1;
		}
	public static ArrayList<StormData> openSeries(String path1, String pattern1, String path2, String pattern2){
		File folder = new File(path1);
		File[] files = folder.listFiles();
		File folder2 = new File(path2);
		File[] files2 = folder2.listFiles();
		if (files == null){
			files = new File[0];
		}
		if (files2 == null){
			files2 = new File[0];
		}
		try {
			OutputClass.createOutputFolder(path1);
			OutputClass.createOutputFolder(path2);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		StormData sd1 = new StormData();
		StormData sd2 = new StormData();
		try{
			Arrays.sort(files);
			sd1.setPath(path1);
			sd1.setFname(pattern1+"mergedFile"+".txt");
			for (int i = 0; i<files.length; i++ ){
				if(isValidInputFile(files[i], pattern1)){
					StormData tmp = new StormData(files[i].getParent(),files[i].getName());
					sd1.addStormData(tmp);
					setProgress("MultipleInputDC",(int)(100.*i/(files.length+files2.length)));
				}
			}
			
		}
		catch(NullPointerException e){}
		try{
			Arrays.sort(files2);
			sd2.setPath(path2);
			sd2.setFname(pattern2+"mergedFile"+".txt");
	
			
			for (int i = 0; i<files2.length; i++ ){
				if(isValidInputFile(files2[i], pattern2)){
					StormData tmp = new StormData(files2[i].getAbsolutePath());
					sd2.addStormData(tmp);
					setProgress("MultipleInput",(int)(100.*i/(files.length+files2.length)+100*files.length/(files.length+files2.length)));
				}
			}
		}
		catch(NullPointerException e){}
		ArrayList<StormData> retList = new ArrayList<StormData>();
		retList.add(sd1);
		retList.add(sd2);
		return retList;
	}
	
	public static ArrayList<ArrayList<Double>> getHistogram(ArrayList<Double> vals, double binwidth){
		double minVal, maxVal;
		ArrayList<Double> minMaxVals = findMinMaxVal(vals);
		minVal = minMaxVals.get(0);
		maxVal = minMaxVals.get(1);
		int numberIntervals = (int) Math.ceil((maxVal-minVal)/binwidth)+1;
		Collections.sort(vals);
		int currentBin = 0;
		ArrayList<Double> means = new ArrayList<Double>();
		ArrayList<Double> counts = new ArrayList<Double>();
		for (int j = 0; j<numberIntervals; j++){
			counts.add((double) 0);
			means.add(minVal + (j+0.5)*binwidth);
		}
		for (int i = 0; i< vals.size(); i++){
			if (vals.get(i)>(currentBin+1)*binwidth+minVal){
				currentBin = currentBin +1;
				i = i -1;//stay at same element
			}
			else {
				counts.set(currentBin, counts.get(currentBin)+1);
			}
		}
		ArrayList<ArrayList<Double>> retList = new ArrayList<ArrayList<Double>>();
		retList.add(means);
		retList.add(counts);
		return retList;
	}
	
	public static ArrayList<Double> findMinMaxVal(ArrayList<Double> vals){
		double minVal = 1e10;
		double maxVal = 0;
		for (int i = 0; i< vals.size(); i++){
			if (vals.get(i)>maxVal){
				maxVal = vals.get(i);
			}
			if (vals.get(i)<minVal){
				minVal = vals.get(i);
			}
		}
		ArrayList<Double> retList = new ArrayList<Double>();
		retList.add(minVal);
		retList.add(maxVal);
		return retList;
	}
	
	public static ArrayList<ArrayList<StormLocalization>> findTraces(ArrayList<StormLocalization> locs, double dx, double dy, double dz, int maxdistBetweenLocalizations) {
		return findTraces( locs, dx, dy, dz, maxdistBetweenLocalizations, true);
	}
	
	public static ArrayList<ArrayList<StormLocalization>> findTraces(ArrayList<StormLocalization> locs, double dx, double dy, double dz, int maxdistBetweenLocalizations, boolean showProgress) {
		Comparator<StormLocalization> compFrame = new StormLocalizationFrameComperator();
		Collections.sort(locs,compFrame);
		int framemax = locs.get(locs.size()-1).getFrame();
		int framemin = locs.get(0).getFrame();
		//System.out.println(framemax+" "+framemin);

		ArrayList<ArrayList> connectedPoints = new ArrayList<ArrayList>();
		ArrayList<ArrayList<StormLocalization>> traces = new ArrayList<ArrayList<StormLocalization>>();
		ArrayList<ArrayList<StormLocalization>> frames = new ArrayList<ArrayList<StormLocalization>>();
		
		for (int k = 0; k<=framemax+1; k++) {
			frames.add(new ArrayList<StormLocalization>());
		}
		for (int j = 0; j< locs.size(); j++){
			frames.get(locs.get(j).getFrame()).add(locs.get(j)); //frames contains one list for each frame the data of the current subset is fed into it.
		}
		
		Progressbar pb = new Progressbar(0, framemax+1,0,"Finding traces ...");
				
		for (int i = 0; i<framemax+1; i++){
			for (int j = 0; j<frames.get(i).size(); j++){
				StormLocalization currLoc = frames.get(i).get(j);
				ArrayList<StormLocalization> currTrace = new ArrayList<StormLocalization>();
				currTrace.add(currLoc);
				int currFrame = currLoc.getFrame();
				int evaluatedFrame = currFrame + 1;
				//System.out.println(i+" "+j);
				while (currFrame + maxdistBetweenLocalizations >= evaluatedFrame && evaluatedFrame < framemax){//runs as long as there are consecutive localizations within a maximum distance of maxdistBetweenLoc...
					for (int k = 0; k<frames.get(evaluatedFrame).size(); k++){//runs through all locs of the currently evaluated frame
						StormLocalization compLoc = frames.get(evaluatedFrame).get(k);
						if (Math.abs(currLoc.getY()-compLoc.getY())<dy && Math.abs(currLoc.getX()-compLoc.getX())<dx && Math.abs(currLoc.getZ()-compLoc.getZ())<dz) {
							frames.get(evaluatedFrame).remove(k); // remove found localization to avoid duplication
							currFrame = evaluatedFrame; //currFrame describes the frame of the current localization so it is changed to the frame of the matching loc which becomes the new current loc
							evaluatedFrame = currFrame +1;
							currTrace.add(compLoc);
							currLoc = compLoc;
							break;
						}
					}
					evaluatedFrame += 1;
				}
				traces.add(currTrace);
				if (showProgress){
					pb.updateProgress(i);
				}
			}
			//System.out.println(i +" " +frames.get(i).size());
		}
		System.out.println("Number of detected traces: "+traces.size()+" Number of all localizations: "+locs.size());
		return traces;
	}
	
	public static ArrayList<ArrayList<StormLocalization>> findTraces2(ArrayList<StormLocalization> locs, double dx, double dy, double dz, int maxdistBetweenLocalizations) {
		Comparator<StormLocalization> compFrame = new StormLocalizationFrameComperator();
		Collections.sort(locs,compFrame);
		int framemax = locs.get(locs.size()-1).getFrame();
		int framemin = locs.get(0).getFrame();
		//System.out.println(framemax+" "+framemin);

		ArrayList<ArrayList> connectedPoints = new ArrayList<ArrayList>();
		ArrayList<ArrayList<StormLocalization>> traces = new ArrayList<ArrayList<StormLocalization>>();
		ArrayList<ArrayList<StormLocalization>> frames = new ArrayList<ArrayList<StormLocalization>>();
		
		for (int k = 0; k<=framemax+1; k++) {
			frames.add(new ArrayList<StormLocalization>());
		}
		for (int j = 0; j< locs.size(); j++){
			frames.get(locs.get(j).getFrame()).add(locs.get(j)); //frames contains one list for each frame the data of the current subset is fed into it.
		}
		Progressbar pb = new Progressbar(0, framemax+1,0,"Finding traces ...");
		for (int i = 0; i<framemax+1; i++){
			for (int j = 0; j<frames.get(i).size(); j++){
				StormLocalization currLoc = frames.get(i).get(j);
				ArrayList<StormLocalization> currTrace = new ArrayList<StormLocalization>();
				currTrace.add(currLoc);
				int currFrame = currLoc.getFrame();
				int evaluatedFrame = currFrame + 1;
				//System.out.println(i+" "+j);
				while (currFrame + maxdistBetweenLocalizations >= evaluatedFrame && evaluatedFrame < framemax){//runs as long as there are consecutive localizations within a maximum distance of maxdistBetweenLoc...
					ArrayList<Double> distX = new ArrayList<Double>();
					ArrayList<Double> distY = new ArrayList<Double>();
					ArrayList<Double> distZ = new ArrayList<Double>();
					int idx = 0;
					for (int k = 0; k<frames.get(evaluatedFrame).size(); k++){//runs through all locs of the currently evaluated frame
						StormLocalization compLoc = frames.get(evaluatedFrame).get(k);
						distX.add(Math.abs(currLoc.getX()-compLoc.getX()));
						distY.add(Math.abs(currLoc.getY()-compLoc.getY()));
						distZ.add(Math.abs(currLoc.getZ()-compLoc.getZ()));

						
					}
					double mindist = 9e9;
					idx = -1;
					for (int k = 0; k<frames.get(evaluatedFrame).size(); k++){
						double dist = Math.sqrt(distX.get(k)*distX.get(k)+distY.get(k)*distY.get(k)+distZ.get(k)*distZ.get(k));
						if (dist<mindist&&dist<(Math.sqrt(dx*dx+dy*dy+dz*dz))){
							mindist = dist;
							idx = k;
						}		
					}
					if (idx>-1){
						currTrace.add(frames.get(evaluatedFrame).get(idx));
						currLoc = frames.get(evaluatedFrame).get(idx);
						currFrame = evaluatedFrame;
					}
					evaluatedFrame += 1;
				}
				traces.add(currTrace);
				pb.updateProgress(i);
			}
			//System.out.println(i +" " +frames.get(i).size());
		}
		System.out.println("Number of detected traces: "+traces.size()+" Number of all localizations: "+locs.size());
		return traces;
	}
	
	public static ArrayList<ArrayList<Double>> getDistancesWithinTraces(ArrayList<ArrayList<StormLocalization>> traces){
		//This function returns a list of the distances between consecutive localizations
		//as a list for the xy component combined and the z component.
		ArrayList<Double> distancesXY = new ArrayList<Double>();
		ArrayList<Double> distancesZ = new ArrayList<Double>();
		for (int i = 0; i< traces.size(); i++) {
			if (traces.get(i).size() < 10 && traces.get(i).size()>=2){ //beads are not connected
				for (int j = 1; j<traces.get(i).size(); j++) {
					distancesXY.add(Math.sqrt(Math.pow(traces.get(i).get(j).getX()-traces.get(i).get(j-1).getX(),2) + 
							Math.pow(traces.get(i).get(j).getY()-traces.get(i).get(j-1).getY(),2)));
					distancesZ.add(traces.get(i).get(j).getZ()-traces.get(i).get(j-1).getZ());
				}
			}
		}
		ArrayList<ArrayList<Double>> retList = new ArrayList<ArrayList<Double>>();
		retList.add(distancesXY);
		retList.add(distancesZ);
		return retList;
	}
	
	public static void getDistances(
			ArrayList<ArrayList<StormLocalization>> traces, ArrayList<Double> distancesXY,
			ArrayList<Integer> startingIdx, double heightWindow ,double shiftY, double minY) {
		int windowCounter = 0;
		int windowCounterOld = 0;
		for (int i = 0; i< traces.size(); i++) {
			if (traces.get(i).size() < 10 && traces.get(i).size()>=2){ //beads are not connected
				windowCounter = (int) Math.ceil((traces.get(i).get(0).getY() - minY)/shiftY); //calculates index of current window
				if (windowCounter > windowCounterOld){//begin of new patch
					for(int k = 0; k<windowCounter-windowCounterOld; k++){
						startingIdx.add(distancesXY.size());
					}
					windowCounterOld = windowCounter;
				}
				for (int j = 1; j<traces.get(i).size(); j++) {
					distancesXY.add(Math.sqrt(Math.pow(traces.get(i).get(j).getX()-traces.get(i).get(j-1).getX(),2) + 
							Math.pow(traces.get(i).get(j).getY()-traces.get(i).get(j-1).getY(),2)));
				}
			}
		}
	}
	
	public static ArrayList<StormLocalization> connectTraces(
			ArrayList<ArrayList<StormLocalization>> traces) {
		// consecutive detections will be merged spatial coordinates are averaged
		//intensities added and the first frame is chosen for the connected localization
		ArrayList<StormLocalization> connectedLoc = new ArrayList<StormLocalization>();
		int counter = 0;
		Progressbar pb = new Progressbar(0,traces.size(), 0,"Connecting traces ...");
		for (int i = 0; i< traces.size(); i++) {
			if (traces.get(i).size() < 10){ //beads are not connected
				if (traces.get(i).size()>1){
					counter = counter + 1;
				}
				double x = 0, y = 0, z = 0, intensity =0,angle = 0;
				int frame = traces.get(i).get(0).getFrame();
				for (int j = 0; j<traces.get(i).size(); j++) {
					x = x + traces.get(i).get(j).getX();
					y = y + traces.get(i).get(j).getY();
					z = z + traces.get(i).get(j).getZ();
					intensity = intensity + traces.get(i).get(j).getIntensity();
					angle = angle +traces.get(i).get(j).getAngle();
				}
				x = x / traces.get(i).size();
				y = y / traces.get(i).size();
				z = z / traces.get(i).size();
				angle =angle / traces.get(i).size();
				connectedLoc.add(new StormLocalization(x,y,z,frame,intensity,angle));	
			}
			pb.updateProgress(i);
		}
		return connectedLoc;
	}
	
	public static ArrayList<Double> createDist(ArrayList<Double> x, double sigma){
		ArrayList<Double> y = new ArrayList<Double>();
		for (int i = 0; i<x.size(); i++){
			y.add(x.get(i)/(2*Math.pow(sigma,2))*Math.exp(-Math.pow(x.get(i),2)/(4*Math.pow(sigma, 2)))+Math.random()/10000);            
		}
		return y;
	}
	
	public static Double fitLocalizationPrecissionDistribution(ArrayList<Double> x, ArrayList<Double> y, double sigma) throws Exception{
		double max = 0;
		for (int i =0; i< y.size(); i++){
			if(max<y.get(i)){
				max = y.get(i);
			}
			//sum = sum + y.get(i);
		}
		for (int i = 0; i<y.size(); i++){
			y.set(i, y.get(i)/max); //normalization to 1
		}
		double tr = 0.0;
		double t = 1.4, l = 0.1;
		for(int k = 0; k< 10000; ++k){
			double[][] jr = new double[1][1];
			jr[0][0]=0;
			double[][] j = new double[1][1];
			j[0][0] = 0;
			double[][] jj = new double[1][1];
			jj[0][0] = 0;
			for (int i = 0; i<y.size(); ++i){
				double d = x.get(i);
				double fac = Math.exp(0.5)/Math.sqrt(2);
				double e =fac* d/sigma*Math.exp(-Math.pow(d,2)/(4*Math.pow(sigma, 2)));
				double r = 0;
				if (x.get(i)<20){
					r = y.get(i) - e;
				}

				j[0][0] = +fac*d/Math.pow(sigma,2)*Math.exp(-Math.pow(d,2)/(4*Math.pow(sigma, 2)))*(Math.pow(d, 2)/(2*Math.pow(sigma, 2)));
						
						//-x.get(i)/Math.pow(sigma, 3)*Math.exp(-Math.pow(x.get(i),2)/(4*Math.pow(sigma, 2))) +
						//x.get(i)/(2*Math.pow(sigma,2))*Math.exp(-Math.pow(x.get(i),2)/(4*Math.pow(sigma, 2)))*Math.pow(x.get(i),2)/2/Math.pow(sigma, 3);
				//System.out.println(-x.get(i)/Math.pow(sigma, 3)*Math.exp(-Math.pow(x.get(i),2)/(4*Math.pow(sigma, 2))));
				//System.out.println(x.get(i)/(2*Math.pow(sigma,2))*Math.exp(-Math.pow(x.get(i),2)/(4*Math.pow(sigma, 2)))*Math.pow(x.get(i),2)/2/Math.pow(sigma, 3));
				//System.out.println(x.get(i));
				jr[0][0] = jr[0][0] + r*j[0][0];
				jj[0][0] = jj[0][0]+ j[0][0]*j[0][0];
				tr = tr + Math.pow(r,2);
				
			}
			//System.out.println("trace: "+tr);
			double[][] tmp11 = new double[1][1];
			tmp11[0][0] = jj[0][0];
			double[][] tmp12 = new double[1][1];
			tmp12[0][0] = jj[0][0];
			Matrix jj1 = new Matrix(tmp11);
			Matrix jj2 = new Matrix(tmp12);
			Matrix d1 = new Matrix(1,1);
			Matrix d2 = new Matrix(1,1);
			
			for (int i = 0; i<1;i++){
				//System.out.println(jj1.get(i,i)+ " "+jj2.get(i,i));
				//System.out.println(jj1.get(i,i)*(1+l)+ " "+(1+l)+" "+jj2.get(i, i)*(1+l/t)+" "+(1+l/t));
				double tmp1 = jj1.get(i,i)*(1+l);
				double tmp2 = jj2.get(i,i)*(1+l/t);
				//System.out.println(tmp1+" "+tmp2);
				jj1.set(i, i, tmp1);
				jj2.set(i, i, tmp2);
				//System.out.println(jj1.get(i,i)+" "+ jj2.get(i,i));
			}
			try{
				d1 = jj1.solve(new Matrix(jr));
				d2 = jj2.solve(new Matrix(jr));
			}
			catch(java.lang.RuntimeException e){
				return -1.;
				//System.out.println(" ");
			}
			
			
			double si1 = sigma + d1.get(0, 0);
			double si2 = sigma + d2.get(0, 0);
			double tr1 = 0;
			double tr2 = 0;
			for (int i = 0; i< y.size();++i){
				double r1 =0,r2=0;
				if(x.get(i)<50){//use small values to consider only small distances
					r1 = y.get(i)-Math.exp(0.5)/Math.sqrt(2)* x.get(i)/si1*Math.exp(-Math.pow(x.get(i),2)/(4*Math.pow(si1, 2)));
					r2 = y.get(i)-Math.exp(0.5)/Math.sqrt(2)* x.get(i)/si2*Math.exp(-Math.pow(x.get(i),2)/(4*Math.pow(si2, 2)));
				}
				tr1 = tr1 + Math.pow(r1, 2);
				tr2 = tr2 + Math.pow(r2, 2);
				

			}
			if (tr1<tr2) {
				if(tr1<tr) {
					sigma = si1;
				}
				else{
					l=l*t;
				}
			}
			else{
				if(tr2<tr){
					sigma = si2;
				}
				else{
					l = l*t;
				}
			}
		}	
		return sigma;
	}
	
	static ArrayList<Double> fitLocalizationPrecissionDistribution(ArrayList<Double> x, ArrayList<Double> y,
			double sigma, double omega, double dc, double A1, double A2) throws Exception{
		//function fitts p(d) = A1 * d/(2*sigma^2)*exp(-d^2/(4*sigma^2)) + A2*1/sqrt(2*pi*omega^2)*exp(-(d-dc)^2/(2*omega^2))
		double sum = 0;
		for (int i =0; i< y.size(); i++){
			sum = sum + y.get(i);
		}
		for (int i = 0; i<y.size(); i++){
			y.set(i, y.get(i)/sum); //normalization to 1
		}
		double tr = 0.0;
		double t = 1.4, l = 0.1;

		for(int k=0; k< 10000; ++k)
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
		 
			for(int i=0; i<y.size(); i++){
				double exp1 = Math.exp(-Math.pow(x.get(i),2)/(4*Math.pow(sigma, 2)));
				double exp2 = Math.exp(-Math.pow(x.get(i)-dc, 2)/(2*Math.pow(omega, 2)));
				double fac1 = x.get(i)/(2*Math.pow(sigma, 2));
				double fac2 = 1/(Math.sqrt(2*Math.PI*Math.pow(omega, 2)));
				j[0] = -A1*2*fac1/sigma*exp1 +A1*fac1*exp1*Math.pow(x.get(i),2)/2/Math.pow(sigma, 3); // derivation wrt sigma
				j[1] = -A2*fac2/omega*exp2 + A2*Math.pow(x.get(i)-dc,2)/(Math.sqrt(2*Math.PI)*Math.pow(omega, 4))*exp2;
				j[2] = A2*fac2*exp2*(x.get(i)-dc)/(Math.pow(omega, 2));
				j[3] = fac1*exp1;
				j[4] = fac2*exp2;
				double r = y.get(i) - (A1*fac1*exp1+A2*fac2*exp2);
								
				for (int ii = 0; ii<5; ii++){ //jr += r*j
					jr[ii][0]=(jr[ii][0] + r * j[ii]);
				}
				for (int i1 =0; i1<5; i1++){ //jj+=j*transpose(j)
					for (int i2 = 0; i2<5;i2++){
						jj[i1][i2] = jj[i1][i2] + j[i1]*j[i2];
					}
				}
				tr = tr + Math.pow(r, 2);
				
			
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
			
		    double si1 = sigma + d1.get(0,0), o1 = omega + d1.get(1,0), dc1 = dc + d1.get(2,0), A1_1 = A1 + d1.get(3,0), A2_1 = A2 + d1.get(4,0);
		    double si2 = sigma + d2.get(0,0), o2 = omega + d2.get(1,0), dc2 = dc + d2.get(2,0), A1_2 = A1 + d2.get(3,0), A2_2 = A2 + d2.get(4,0);
		    double tr1 = 0.0, tr2 = 0.0;

		    for(int i = 0; i<y.size(); i++){
				double r1 = y.get(i) - (A1_1 * x.get(i)/(2*Math.pow(si1, 2)) *Math.exp(-Math.pow(x.get(i),2)/(4*Math.pow(si1, 2)))+A2_1* 1/(Math.sqrt(2*Math.PI*Math.pow(o1, 2)))*Math.exp(-Math.pow(x.get(i)-dc1, 2)/(2*Math.pow(o1, 2))));
				double r2 = y.get(i) - (A1_2 * x.get(i)/(2*Math.pow(si2, 2)) *Math.exp(-Math.pow(x.get(i),2)/(4*Math.pow(si2, 2)))+A2_2* 1/(Math.sqrt(2*Math.PI*Math.pow(o2, 2)))*Math.exp(-Math.pow(x.get(i)-dc2, 2)/(2*Math.pow(o2, 2))));
	            tr1 += Math.pow(r1,2);
	            tr2 += Math.pow(r2,2);
		    }
		    
		    if(tr1 < tr2)
		    {
		        if(tr1 < tr)
		        {
		            sigma = si1;
		            omega = o1;
		            dc = dc1;
		            A1 = A1_1;
					A2 = A2_1;
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
		            omega = o2;
		            dc = dc2;
		            A1 = A1_2;
					A2 = A2_2;
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
		res.add(sigma);
		res.add(omega);
		res.add(dc);
		res.add(A1);
		res.add(A2);
		return res;
	}
	
	static ArrayList<Double> fitLocalizationPrecissionDistribution2(ArrayList<Double> x, ArrayList<Double> y, double sigma, double scale) throws Exception{
		//function fitts p(d) = A1 * d/(2*sigma^2)*exp(-d^2/(4*sigma^2)) + A2*1/sqrt(2*pi*omega^2)*exp(-(d-dc)^2/(2*omega^2))
		double sum = 0;
		for (int i =0; i< y.size(); i++){
			sum = sum + y.get(i);
		}
		for (int i = 0; i<y.size(); i++){
			y.set(i, y.get(i)/sum); //normalization to 1
		}
		double tr = 0.0;
		double t = 1.4, l = 0.1;

		for(int k=0; k< 10000; ++k)
		{
			double [][] jr = new double[2][1];
			for(int i = 0; i<2; i++){
				jr[i][0] = 0.0;
			}
			double [] j = new double[2];
			for(int i = 0; i<2; i++){
				j[i] = 0.0;
			}
			double [][] jj = new double[2][2];
			for (int i = 0; i<2;i++){
				for (int p=0;p<2;p++){
					jj[i][p] = 0.0;
				}
			}
		 
			for(int i=0; i<y.size(); i++){
				double exp1 = Math.exp(-Math.pow(x.get(i),2)/(4*Math.pow(sigma, 2)));
				double fac1 = x.get(i)/(2*Math.pow(sigma, 2));
				j[0] = -scale*2*fac1/sigma*exp1;
				j[1] = fac1*exp1;
				double r = y.get(i) - (scale*fac1*exp1);
								
				for (int ii = 0; ii<2; ii++){ //jr += r*j
					jr[ii][0]=(jr[ii][0] + r * j[ii]);
				}
				for (int i1 =0; i1<2; i1++){ //jj+=j*transpose(j)
					for (int i2 = 0; i2<2;i2++){
						jj[i1][i2] = jj[i1][i2] + j[i1]*j[i2];
					}
				}
				tr = tr + Math.pow(r, 2);
				
			
			}
	
			Matrix jj1 = new Matrix(jj);
			Matrix jj2 = new Matrix(jj);
			Matrix d1 = new Matrix(2,1);
			Matrix d2 = new Matrix(2,1);
			
			for (int i = 0; i<2;i++){
				jj1.set(i, i, jj1.get(i,i)+l);
				jj2.set(i, i, jj2.get(i, i)+l/t);
			}
			d1 = jj1.solve(new Matrix(jr));
			d2 = jj2.solve(new Matrix(jr));
			
		    double si1 = sigma + d1.get(0,0), sc1 = scale + d1.get(1,0);
		    double si2 = sigma + d2.get(0,0), sc2 = scale + d2.get(1,0);
		    double tr1 = 0.0, tr2 = 0.0;

		    for(int i = 0; i<y.size(); i++){
				double r1 = y.get(i) - sc1 * x.get(i)/(2*Math.pow(si1, 2)) *Math.exp(-Math.pow(x.get(i),2)/(4*Math.pow(si1, 2)));
				double r2 = y.get(i) - sc2 * x.get(i)/(2*Math.pow(si2, 2)) *Math.exp(-Math.pow(x.get(i),2)/(4*Math.pow(si2, 2)));
	            tr1 += Math.pow(r1,2);
	            tr2 += Math.pow(r2,2);
		    }
		    
		    if(tr1 < tr2)
		    {
		        if(tr1 < tr)
		        {
		            sigma = si1;
		            scale = sc1;
		    
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
		        	scale = sc2;
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
		res.add(sigma);
		res.add(scale);
		
		return res;
	}
	
	//Levenberg Marquard 1D Gaussian fit, return sigma
		public static Double fitGaussian1D(ArrayList<Double> x, ArrayList<Double> y, double sigma, double scale, double x0) throws Exception{
		    double tr = 0.0;
			double t = 1.4, l = 0.1;
			int nbrParams = 3;
			for(int k=0; k< 1000; ++k)
			{
				double [][] jr = new double[nbrParams][1];
				for(int i = 0; i<nbrParams; i++){
					jr[i][0] = 0.0;
				}
				double [] j = new double[nbrParams];
				for(int i = 0; i<nbrParams; i++){
					j[i] = 0.0;
				}
				double [][] jj = new double[nbrParams][nbrParams];
				for (int i = 0; i<nbrParams;i++){
					for (int p=0;p<nbrParams;p++){
						jj[i][p] = 0.0;
					}
				}
			 
				for(int d = 0; d<x.size(); d++){
					double xs = Math.pow((x.get(d)-x0)/sigma,2);
					double e = Math.exp(-0.5*xs);
					double r = y.get(d) - (scale * e);
					j[0] = (scale*e*xs/sigma);
					j[1] = (e);
					j[2] = (scale * e*(x.get(d)-x0)/Math.pow(sigma,2));
										
					for (int i = 0; i<nbrParams; i++){ //jr += r*j
						jr[i][0]=(jr[i][0] + r * j[i]);
					}
					for (int i1 =0; i1<nbrParams; i1++){ //jj+=j*transpose(j)
						for (int i2 = 0; i2<nbrParams;i2++){
							jj[i1][i2] = jj[i1][i2] + j[i1]*j[i2];
						}
					}
					tr = tr + Math.pow(r, 2);
				}
		
				Matrix jj1 = new Matrix(jj);
				Matrix jj2 = new Matrix(jj);
				Matrix d1 = new Matrix(nbrParams,1);
				Matrix d2 = new Matrix(nbrParams,1);
				
				for (int i = 0; i<nbrParams;i++){
					jj1.set(i, i, jj1.get(i,i)+l);
					jj2.set(i, i, jj2.get(i, i)+l/t);
				}
				d1 = jj1.solve(new Matrix(jr));
				d2 = jj2.solve(new Matrix(jr));
				
			    double si1 = sigma + d1.get(0,0), s1 = scale + d1.get(1,0), x01 = x0 + d1.get(2, 0);
			    double si2 = sigma + d2.get(0,0), s2 = scale + d2.get(1,0), x02 = x0 + d2.get(2, 0);
			    double tr1 = 0.0, tr2 = 0.0;

			    for(int d = 0; d<x.size(); d++){
					double r1 = y.get(d) - (s1 * Math.exp(-0.5 * (Math.pow((x.get(d) - x01) / si1,2))));
		            double r2 = y.get(d) - (s2 * Math.exp(-0.5 * (Math.pow((x.get(d) - x02) / si2,2))));
		            tr1 += Math.pow(r1,2);
		            tr2 += Math.pow(r2,2);
				}
			    
			    if(tr1 < tr2)
			    {
			        if(tr1 < tr)
			        {
			            sigma = si1;
			            scale = s1;
			            x0 = x01;
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
			            x0 = x02;
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
			return sigma;
		}

		//Levenberg Marquard 2D Gaussian fit, return center in pixel
		public static ArrayList<Double> fitGaussian2D(ImagePlus img, double sigma, double scale, double offset, double x0, double y0) throws EOFException{
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
		
		static boolean isValidInputFile(File file, String pattern){
			String absPath = file.getAbsolutePath();
			if (file.isFile() && absPath.contains(pattern)&& (absPath.contains(".txt") || absPath.contains(".csv"))){//RapidStorm or Thunderstorm input
				if (!(absPath.contains("-settings.")||absPath.contains("-protocol."))){
					return true;
				}
				else{return false;}
			}
			else{return false;}
			
		}
		
		public static void splitTxtCummulative(int startFrame, int endFrame, int chunksize, StormData sd, String path, boolean connected){
			int i = startFrame;
			int counter = 0;
			while (i<endFrame){
				StormData subset = sd.findSubset(0,i+chunksize);
				if (connected){//connect
					subset.setPath(sd.getPath()+"\\splittedTraced\\");
					subset.connectPoints(90, 90, 9e9, 15);
					subset.setFname("partTraced0_"+(i+chunksize)+".txt");
				}
				else{
					subset.setPath(sd.getPath()+"\\splitted\\");
					subset.setFname("part0_"+(i+chunksize)+".txt");
				}
				
				new File(subset.getPath()).mkdir();
				subset.writeLocsForBaumgart();
				ij.IJ.save(subset.renderImage2D(10, false, "", 0, -1, 5, 1, 1),subset.getPath()+"\\"+subset.getBasename()+".tiff");
				i = i + chunksize;
				counter = counter +1;
			}

		}
		
}

