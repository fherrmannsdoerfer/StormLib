package StormLib;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.Vector;
import java.util.regex.Pattern;

public class StormDataProc {
	public ArrayList<StormLocalization> locs = new ArrayList<StormLocalization>();
	String fname;
	String basename;
	public StormDataProc(String fname){
		this.fname = fname;
		String[] roots = fname.split(Pattern.quote(File.separator));
		String[] basename2 = roots[roots.length-1].split(Pattern.quote(".")); //basename of the file e.g. "c:\alabab.txt" basename is alabab
		basename = basename2[0];
		BufferedReader br = null;
		String line = "";
		String delimiter = " ";
		try {
			br = new BufferedReader(new FileReader(fname));
			line = br.readLine(); //skip header
			while ((line = br.readLine())!= null){
				String[] tmpStr = line.split(delimiter);
				//System.out.println(line);
				if (tmpStr.length == 4) { //2D data
					StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[0]), Double.valueOf(tmpStr[1]), Integer.valueOf(tmpStr[2]), Double.valueOf(tmpStr[3]));
					locs.add(sl);
				}
				else if(tmpStr.length == 5) { //3d data
					StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[0]), Double.valueOf(tmpStr[1]), Double.valueOf(tmpStr[2]), Integer.valueOf(tmpStr[3]), Double.valueOf(tmpStr[4]));
					locs.add(sl);
				}
				else if(tmpStr.length == 6) { //Malk output
					StormLocalization sl = new StormLocalization(Double.valueOf(tmpStr[0]), Double.valueOf(tmpStr[1]), Integer.valueOf(tmpStr[2]), Double.valueOf(tmpStr[3]));
					locs.add(sl);
				}
				else {System.out.println("File format not understood!");}
				
			}
			System.out.println("File contains "+locs.size()+" localizations.");
		} 
		catch (FileNotFoundException e) {
			e.printStackTrace();
			System.out.println(fname);
		}
		catch (IOException e) {e.printStackTrace();}
		
	}
	StormDataProc(){}
	void addLocalization(StormLocalization loc){
		locs.add(loc);
	}
	public int getNumberLocalizations(){return locs.size();}
	
	public void sortFrame(ArrayList<StormLocalization> sl){
		Comparator<StormLocalization> compFrame = new StormLocalizationFrameComperator();
		Collections.sort(sl,compFrame);
	}
	
	public ArrayList<ArrayList<ArrayList<StormLocalization>>> detectBeads(double dx, double dy, double dz, double maxDriftX, int maxdistBetweenLocalizations,ArrayList<StormLocalization> locs) {
		//double dx = 50; //nm tolerance for the coordinates deviation between
		//double dy = 50; //consecutive detections
		///double dz = 30;
		//double maxDriftX = 2000; //maximum of expected drift over the whole movie
		//int maxdistBetweenLocalizations = 50;//number of missing consecutive localizations. If no other near localization is found within that certain number of frames the trace is broken
		Comparator<StormLocalization> compFrame = new StormLocalizationFrameComperator();
		Collections.sort(locs,compFrame);
		int framemax = locs.get(locs.size()-1).getFrame();
		int framemin = locs.get(0).getFrame();
		
		ArrayList<ArrayList<StormLocalization>> traces = findTraces(locs, dx, dy, dz, maxDriftX, maxdistBetweenLocalizations);
		traces = sortArrayListsBySize(traces);
		System.out.println("Longest Trace has "+traces.get(0).size()+" members. That are "+100*(traces.get(0).size() / (double)(framemax-framemin))+" percent of all Frames.");
		//for (int i = 0; i<100;i++){System.out.println(traces.get(i).size());}
		//writeArrayList(traces.get(0),"c:\\tmp2\\bead1.txt");
		//writeArrayList(traces.get(1),"c:\\tmp2\\bead2.txt");
			
		String path = "c:\\tmp2\\BeadUntersuchung\\";
		ArrayList<ArrayList<ArrayList<StormLocalization>>> beadTraces;
		beadTraces = smoothBeadTraces(traces, 0.8, framemax, framemin, path);
		return beadTraces;
	}
	
	public ArrayList<StormLocalization> transformCoordinates(ArrayList<ArrayList<ArrayList<StormLocalization>>> beadTraces){
		int beadIndex = findBestBead(beadTraces);
		ArrayList<StormLocalization> transformedLoc = new ArrayList<StormLocalization>();
		transformedLoc = transformLoc(locs, beadTraces.get(0), beadIndex);
		return transformedLoc;
	}
	
	ArrayList<StormLocalization> transformLoc(ArrayList<StormLocalization> locs, ArrayList<ArrayList<StormLocalization>> beadTrace, int bestIndex){
		ArrayList<StormLocalization> transformedLoc = new ArrayList<StormLocalization>();
		double x0 = beadTrace.get(0).get(bestIndex).getX();
		double y0 = beadTrace.get(0).get(bestIndex).getY();
		for (int i = 0, frame = 0; i<locs.size(); i++){
			frame = locs.get(i).getFrame();
			
			//TODO falls das Bead luecken hat oder spaeter anfaengt gibt es kein gutes ergebnis
			if (frame<beadTrace.size()-1) { 
				if (beadTrace.get(frame).get(bestIndex).getX() == -1) {
					System.out.println("no bead found in frame: "+frame+"no localizations from this frame will be considered.");
				}
				else {
					double offsetX = beadTrace.get(frame).get(bestIndex).getX() - x0;
					double offsetY = beadTrace.get(frame).get(bestIndex).getY() - y0;
					transformedLoc.add(new StormLocalization(locs.get(i).getX()-offsetX,locs.get(i).getY()-offsetY,locs.get(i).getZ(), locs.get(i).getFrame(), locs.get(i).getIntensity()));
				}
			}
		}
		return transformedLoc;
	}
	
	int findBestBead(ArrayList<ArrayList<ArrayList<StormLocalization>>> beadTraces){ //function finds to predict the best bead trace for driftcorrection
		double variances = 0;
		double lastVar = Double.MAX_VALUE;
		int bestBeadIndex= 0;
		for (int i=0; i<beadTraces.get(0).get(0).size();i++){
			variances = (calculateVariances(beadTraces.get(0),i));
			if (variances < lastVar) {
				lastVar = variances;
				bestBeadIndex = i;
			}
		}
		return bestBeadIndex;
	}
	
	double calculateVariances(ArrayList<ArrayList<StormLocalization>> beadTrace,int currentIndex) {
		int iterations = 1000;
		double summeX = 0, summeY = 0;
		for (int i = 0; i< iterations; i++) {
			//System.out.println(beadTrace.get(i).get(0).toString());
			//System.out.println(i+" "+currentIndex+" "+beadTrace.size()+" "+beadTrace.get(i).size());
			summeX = summeX + beadTrace.get(i).get(currentIndex).getX();
			summeY = summeY + beadTrace.get(i).get(currentIndex).getY();
		}
		double meanX = summeX/iterations;
		double meanY = summeY/iterations;
		double varianceX = 0;
		double varianceY = 0;
		for (int i = 0; i< iterations; i++) {
			varianceX = varianceX + ((beadTrace.get(i).get(currentIndex).getX()) - meanX)*((beadTrace.get(i).get(currentIndex).getX()) - meanX);
			varianceY = varianceY + ((beadTrace.get(i).get(currentIndex).getY()) - meanY)*((beadTrace.get(i).get(currentIndex).getY()) - meanY);
		}
		varianceX = varianceX/ iterations;
		varianceY = varianceY/ iterations;
		
		return varianceX + varianceY;
	}
	ArrayList<ArrayList<ArrayList<StormLocalization>>> smoothBeadTraces(ArrayList<ArrayList<StormLocalization>> traces, double quantile, int framemax, int framemin, String path) {
		ArrayList<ArrayList<ArrayList<StormLocalization>>> beadTraces = new ArrayList<ArrayList<ArrayList<StormLocalization>>>();
		
		ArrayList kernel = createGaussianKernel1D(1.5,31);
		ArrayList<ArrayList<StormLocalization>> smoothedTraces = new ArrayList<ArrayList<StormLocalization>>();
		ArrayList<Double> offsetsX = new ArrayList<Double>(); //offset between different bead traces to make them comparable
		ArrayList<Double> offsetsY = new ArrayList<Double>();
		ArrayList<Double> offsetsSmoothedX = new ArrayList<Double>();
		ArrayList<Double> offsetsSmoothedY = new ArrayList<Double>();
		int outputCounter = 0;
		for (int i = 0; i< traces.size();i++){//traces.size(); i++) {
			ArrayList smoothedBead = convolveStormLocalizations(traces.get(i), kernel);
			smoothedTraces.add(smoothedBead);
			if (traces.get(i).size()>quantile * (framemax-framemin)) {
				System.out.println(i+ " ht Trace has "+traces.get(i).size()+" members. That are "+100*(traces.get(i).size() / (double)(framemax-framemin))+" percent of all Frames.");
				outerloop:
				for (int k1 = 0; k1< traces.get(0).size(); k1++) { //determin the offsets between the different beadtraces based on the first element
					for( int k2 = 0; k2< traces.get(i).size(); k2++){
						if (traces.get(0).get(k1).getFrame() == traces.get(i).get(k2).getFrame()){
							offsetsX.add(traces.get(0).get(k1).getX() - traces.get(i).get(k2).getX());
							offsetsY.add(traces.get(0).get(k1).getY() - traces.get(i).get(k2).getY());
							offsetsSmoothedX.add(smoothedTraces.get(0).get(k1).getX() - smoothedTraces.get(i).get(k2).getX());
							offsetsSmoothedY.add(smoothedTraces.get(0).get(k1).getY() - smoothedTraces.get(i).get(k2).getY());
							break outerloop;
						}
					}
				}
				outputCounter = outputCounter +1;
			}
		}
		
		ArrayList<ArrayList<StormLocalization>> outputArrayRaw = new ArrayList<ArrayList<StormLocalization>>();
		ArrayList<ArrayList<StormLocalization>> outputArrayRawAdjusted = new ArrayList<ArrayList<StormLocalization>>();
		ArrayList<ArrayList<StormLocalization>> outputArraySmoothed = new ArrayList<ArrayList<StormLocalization>>();
		ArrayList<ArrayList<StormLocalization>> outputArraySmoothedAdjusted = new ArrayList<ArrayList<StormLocalization>>();
		ArrayList<Integer> lastIndex = new ArrayList<Integer>();
		for (int i = 0; i< outputCounter; i++) {
			lastIndex.add(0);
		}
	
		for (int frame = 0; frame<framemax; frame++){// fills the arrays with the bead traces. if the frame is not represented by the bead x and y will be set to -1
			outputArrayRaw.add(new ArrayList<StormLocalization>());
			outputArrayRawAdjusted.add(new ArrayList<StormLocalization>());
			outputArraySmoothed.add(new ArrayList<StormLocalization>());
			outputArraySmoothedAdjusted.add(new ArrayList<StormLocalization>());
			for (int i = 0; i< outputCounter; i++) {
				//System.out.println(frame+ " "+lastIndex.get(i)+" "+i);
				if (traces.get(i).get(lastIndex.get(i)).getFrame() == frame) { //if frame is represented, the proper values are fed in the output arrays
					outputArrayRaw.get(frame).add(new StormLocalization(traces.get(i).get(lastIndex.get(i)).getX(),traces.get(i).get(lastIndex.get(i)).getY(),traces.get(i).get(lastIndex.get(i)).getZ(),frame,traces.get(i).get(lastIndex.get(i)).getIntensity()));
					outputArrayRawAdjusted.get(frame).add(new StormLocalization(traces.get(i).get(lastIndex.get(i)).getX() + offsetsX.get(i),traces.get(i).get(lastIndex.get(i)).getY() + offsetsY.get(i),traces.get(i).get(lastIndex.get(i)).getZ(),frame,traces.get(i).get(lastIndex.get(i)).getIntensity()));
					outputArraySmoothed.get(frame).add(new StormLocalization(smoothedTraces.get(i).get(lastIndex.get(i)).getX(),smoothedTraces.get(i).get(lastIndex.get(i)).getY(),traces.get(i).get(lastIndex.get(i)).getZ(),frame,traces.get(i).get(lastIndex.get(i)).getIntensity()));
					outputArraySmoothedAdjusted.get(frame).add(new StormLocalization(smoothedTraces.get(i).get(lastIndex.get(i)).getX() + offsetsSmoothedX.get(i),smoothedTraces.get(i).get(lastIndex.get(i)).getY() + offsetsSmoothedY.get(i),traces.get(i).get(lastIndex.get(i)).getZ(),frame,traces.get(i).get(lastIndex.get(i)).getIntensity()));
					
					if (lastIndex.get(i) < traces.get(i).size()-1){ //Beadtraces that are shorter might cause problems 
						lastIndex.set(i, lastIndex.get(i) + 1);
					}
				}
				else{
					//fill outputarray with -1 in case the frame is not represented by the bead
					outputArrayRaw.get(frame).add(new StormLocalization(-1.,-1.,-1.,frame,-1.));
					outputArrayRawAdjusted.get(frame).add(new StormLocalization(-1.,-1.,-1.,frame,-1));
					outputArraySmoothed.get(frame).add(new StormLocalization(-1.,-1.,-1.,frame,-1));
					outputArraySmoothedAdjusted.get(frame).add(new StormLocalization(-1.,-1.,-1.,frame,-1));			
				}
			}
		}
				
		beadTraces.add(outputArrayRaw);
		beadTraces.add(outputArrayRawAdjusted);
		beadTraces.add(outputArraySmoothed);
		beadTraces.add(outputArraySmoothedAdjusted);
				
		return beadTraces;
	}
	
	public void writeBeadTracesToDisk(ArrayList<ArrayList<ArrayList<StormLocalization>>> beadTraces, String path, String additionalInfo){
		writeBeadArrayToDisk(beadTraces.get(0), path+basename+"_"+additionalInfo+"_outputArrayRaw.txt");
		writeBeadArrayToDisk(beadTraces.get(1), path+basename+"_"+additionalInfo+"_outputArrayRawAdjusted.txt");
		writeBeadArrayToDisk(beadTraces.get(2), path+basename+"_"+additionalInfo+"_outputArraySmoothed.txt");
		writeBeadArrayToDisk(beadTraces.get(3), path+basename+"_"+additionalInfo+"_outputArraySmoothedAdjusted.txt");
	}
	
	void writeBeadArrayToDisk(ArrayList<ArrayList<StormLocalization>> array, String fname){
		try {
			FileWriter writer = new FileWriter(fname);
			for (int j = 0; j<array.get(0).size();j++){
				if (j == 0){
					writer.append("Frame Bead0X Bead0Y");
				}
				else {
					writer.append(" Bead"+j+"X Bead"+j+"Y");
				}
			}
			writer.append("\n");
			for (int i = 0; i<array.size();i++) {
				for(int j = 0; j< array.get(i).size();j++) {
					if (j == 0){
						writer.append(array.get(i).get(j).getFrame()+ " "+array.get(i).get(j).getX()+" "+array.get(i).get(j).getY());
					}
					else {
						writer.append(" "+array.get(i).get(j).getX()+" "+array.get(i).get(j).getY());
					}
				}
				writer.append("\n");
			}
			
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
		
	}
	ArrayList<ArrayList<StormLocalization>> sortArrayListsBySize(ArrayList<ArrayList<StormLocalization>> traces){
		Collections.sort(traces, new Comparator<ArrayList>(){
		    public int compare(ArrayList a1, ArrayList a2) {
		        return a2.size() - a1.size(); // assumes you want biggest to smallest
		    }
		});
		return traces;
	}
	
	ArrayList<ArrayList<StormLocalization>> findTraces(ArrayList<StormLocalization> locs, double dx, double dy, double dz,double maxDriftX, int maxdistBetweenLocalizations) {
		Comparator<StormLocalization> compFrame = new StormLocalizationFrameComperator();
		Collections.sort(locs,compFrame);
		int framemax = locs.get(locs.size()-1).getFrame();
		int framemin = locs.get(0).getFrame();
		//System.out.println(framemax+" "+framemin);
		Comparator<StormLocalization> comp = new StormLocalizationXComperator();
		Collections.sort(locs,comp);
		double xmax = locs.get(locs.size()-1).getX();
		double xmin = locs.get(0).getX();
		int nbrIntervals =(int) (Math.ceil((xmax - xmin) / (2* maxDriftX))); //smaller subset of all points are created to find beads within every set.
		ArrayList<ArrayList> subsets; 
		subsets = partitionData(nbrIntervals, maxDriftX, locs);
		ArrayList<ArrayList> connectedPoints = new ArrayList<ArrayList>();
		ArrayList<ArrayList<StormLocalization>> traces = new ArrayList<ArrayList<StormLocalization>>();
		for (int i = 0; i< subsets.size(); i++) {
			Collections.sort(subsets.get(i),compFrame);
			ArrayList<ArrayList> subsetInFrames = new ArrayList<ArrayList>();
			for (int k = 0; k<=framemax+1; k++) {
				subsetInFrames.add(new ArrayList<StormLocalization>());
			}
			ArrayList<StormLocalization> tmpList = (ArrayList<StormLocalization>) subsets.get(i); 
			for (int j = 0; j< subsets.get(i).size(); j++){
				subsetInFrames.get(tmpList.get(j).getFrame()).add(tmpList.get(j)); //subsetInFrames contains one list for each frame the data of the current subset is fed into it.
			}
			int currFrame = 0;
			for (int k = framemin; k<=framemax+1; k++) { //bead traces will be found and added to a new list containing all consecutive localizations. Every spot is only used once and therefor deleted after it was assigned to a trace
				for (int o = 0; o < subsetInFrames.get(k).size(); o++) {
					ArrayList<StormLocalization> currentTrace = new ArrayList<StormLocalization>();
					currentTrace.add((StormLocalization) subsetInFrames.get(k).get(o));
					currFrame = currentTrace.get(currentTrace.size()-1).getFrame();
					StormLocalization ll = currentTrace.get(currentTrace.size() -1); //last Localization which will be compared with the localizations of the following frames
					int frameEvaluated = currFrame + 1;
					while (currFrame + maxdistBetweenLocalizations > frameEvaluated && frameEvaluated<framemax) {
						//System.out.println(subsetInFrames.get(frameEvaluated).size());
						for (int p = 0; p<subsetInFrames.get(frameEvaluated).size();p++){
							StormLocalization tl = (StormLocalization) subsetInFrames.get(frameEvaluated).get(p); // localization to test
							if (Math.abs(tl.getY()-ll.getY())<dy && Math.abs(tl.getX()-ll.getX())<dx && Math.abs(tl.getZ()-ll.getZ())<dz) {//test for y in the beginning because it is more likely to fail since the subset contains similar x values
								currentTrace.add(tl);
								currFrame = tl.getFrame();
								subsetInFrames.get(frameEvaluated).remove(p); //localization is deleted
								ll = tl; // last localization is updated
								break;
							}
						}
						frameEvaluated = frameEvaluated + 1; //if no match was found in this frame or if break exited the for loop the localizations of the next frame will be looked through. If this happens to often in a row the while loop breaks and the trace is broken
					}
					traces.add(currentTrace); //traces is updated, next trace will be found
				}
			}
			System.out.println("Number of detected traces: "+traces.size());
		}
		
		/*for (int ii = 0; ii< 100; ii++) {
			System.out.println();
			System.out.println();
			System.out.println(traces.get(ii).size());
			for (int i = 0; i< traces.get(ii).size(); i++){System.out.println(traces.get(ii).get(i).toString());}
		}*/
		return traces;
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
	public String toString(){
		//System.out.println(Math.min(locs.size(), 1000));
		String tmp = "";
		for (int i = 0; i<Math.min(locs.size(),1000);i++) {
			tmp = tmp + locs.get(i).toString() + "\n";
		}
		
		return tmp;
	}
	public void sort(String str) {
		if (str.equalsIgnoreCase("x")){
			System.out.println("x");
			Comparator<StormLocalization> comp = new StormLocalizationXComperator();
			Collections.sort(locs,comp);
		}
	}
	
	public void writeArrayList(ArrayList<StormLocalization> locs, String fname) {
		try {
			FileWriter writer = new FileWriter(fname);
			for (int i = 0; i<locs.size(); i++){
				writer.append(locs.get(i).toPlainString()+"\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public void writeArrayListForVisp(ArrayList<StormLocalization> locs, String fname) {
		try {
			FileWriter writer = new FileWriter(fname);
			for (int i = 0; i<locs.size(); i++){
				writer.append(locs.get(i).toPlainVispString()+"\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	void writeArrayList(ArrayList<StormLocalization> locs) {
		try {
			FileWriter writer = new FileWriter("c:\\tmp2\\javaoutput.txt");
			for (int i = 0; i<locs.size(); i++){
				writer.append(locs.get(i).toPlainString()+"\n");
			}
			writer.flush();
			writer.close();
		} catch (IOException e) {e.printStackTrace();}
	}
	
	public ArrayList<Double> createGaussianKernel1D(double width, int size) {
		if (size<=0) {
			System.out.println("size must be a positive integer number larger 0");
		}
		ArrayList<Double> kernel = new ArrayList<Double>(size);
		double mean = 0;
		if (size % 2 == 1){
			mean = Math.floor(size / 2.);
		}
		else {
			mean = size/2.-0.5;
		}
		double sum = 0;
		for (int i = 0; i<size;i++){
			kernel.add(1/(Math.sqrt(2*Math.PI)*width)*Math.exp(-(i-mean)*(i-mean)/width/width));
			sum = sum + kernel.get(kernel.size()-1);
		}
		for (int i = 0; i< size;i++) {
			kernel.set(i, kernel.get(i)/sum);
		}
		return kernel;
	}
	
	public ArrayList<StormLocalization> convolveStormLocalizations(ArrayList<StormLocalization> sl, ArrayList<Double> kernel) {
		int kernelSize = kernel.size();
		ArrayList<StormLocalization> slsmooth = new ArrayList<StormLocalization>();
		ArrayList<ArrayList<Double>> coordinates = new ArrayList<ArrayList<Double>>();
		for (int i = 0; i<3; i++){
			coordinates.add(new ArrayList<Double>());
		}
		for (int i = 0; i<sl.size(); i++){
			coordinates.get(0).add(sl.get(i).getX());
			coordinates.get(1).add(sl.get(i).getY());
			coordinates.get(2).add(sl.get(i).getZ());

		}
		double firsts[] = {coordinates.get(0).get(0),coordinates.get(1).get(0),coordinates.get(2).get(0)};
		double lasts[] = {coordinates.get(0).get(coordinates.get(0).size()-1),coordinates.get(1).get(coordinates.get(1).size()-1),coordinates.get(2).get(coordinates.get(2).size()-1)};
		
		for (int i = 0; i<Math.floor(kernelSize/2); i++) { //add the first and the last value to the data so that the filter can work properly and directly
			for (int j=0; j<3; j++) {
				coordinates.get(j).add(0,firsts[j]);
				coordinates.get(j).add(coordinates.get(j).size(),firsts[j]);
			}
		}
		ArrayList<Double> xvals = coordinates.get(0);
		ArrayList<Double> yvals = coordinates.get(1);
		ArrayList<Double> zvals = coordinates.get(2);
		for (int i = 0; i<sl.size();i++){
			double x = 0,y = 0,z = 0;
			for (int j = 0; j<kernelSize;j++){
				x = x + xvals.get(i+j) * kernel.get(j);
				y = y + yvals.get(i+j) * kernel.get(j);
				z = z + zvals.get(i+j) * kernel.get(j);
			}
			slsmooth.add(new StormLocalization(x,y,z,sl.get(i).getFrame(),sl.get(i).getIntensity()));
		}
		return slsmooth;
	}
	public ArrayList<StormLocalization> connectPoints(double dx, double dy, double dz, double maxDriftX, int maxdistBetweenLocalizations,ArrayList<StormLocalization> transformedLoc) {
		// TODO Auto-generated method stub
		ArrayList<ArrayList<StormLocalization>> traces = findTraces(locs, dx, dy, dz, maxDriftX, maxdistBetweenLocalizations);
		ArrayList<StormLocalization> connectedLoc = connectTraces(traces);
		return connectedLoc;
	}
	private ArrayList<StormLocalization> connectTraces(
			ArrayList<ArrayList<StormLocalization>> traces) {
		// consecutive detections will be merged spatial coordinates are averaged
		//intensities added and the first frame is chosen for the connected localization
		ArrayList<StormLocalization> connectedLoc = new ArrayList<StormLocalization>();
		int counter = 0;
		for (int i = 0; i< traces.size(); i++) {
			if (traces.get(i).size() < 10){
				counter = counter + 1;
				double x = 0, y = 0, z = 0, intensity =0;
				int frame = traces.get(i).get(0).getFrame();
				for (int j = 0; j<traces.get(i).size(); j++) {
					x = x + traces.get(i).get(j).getX();
					y = y + traces.get(i).get(j).getY();
					z = z + traces.get(i).get(j).getZ();
					intensity = intensity + traces.get(i).get(j).getIntensity();
				}
				x = x / traces.get(i).size();
				y = y / traces.get(i).size();
				z = z / traces.get(i).size();
				connectedLoc.add(new StormLocalization(x,y,z,frame,intensity));
			}
		}
		System.out.println(counter + " tracks were averaged.");
		return connectedLoc;
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
				
				
				
				locs.add(new StormLocalization(x + contributionDriftX+(Math.random()*50-25), y + contributionDriftY+(Math.random()*50-25), 0., frame, 100));
			}
		}
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
	public StormLocalization getLocalization(int index){
		if (locs.size() > index){
			return locs.get(index);
		}
		else {
			System.err.println("Index "+index+" exceeds the number of elements ("+locs.size()+")!");
			return null;
		}
	}
}


