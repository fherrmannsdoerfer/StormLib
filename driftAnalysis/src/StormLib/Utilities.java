package StormLib;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;

public class Utilities {
	public static ArrayList<StormData> openSeries(String path1, String pattern1, String path2, String pattern2){
		File folder = new File(path1);
		File[] files = folder.listFiles();
		File folder2 = new File(path2);
		File[] files2 = folder2.listFiles();
		Arrays.sort(files);
		Arrays.sort(files2);
		StormData sd1 = new StormData();
		sd1.setPath(path1);
		sd1.setFname(pattern1+"mergedFile"+".txt");
		StormData sd2 = new StormData();
		sd2.setPath(path2);
		sd2.setFname(pattern2+"mergedFile"+".txt");

		for (int i = 0; i<files.length; i++ ){
			if(files[i].isFile() && files[i].getAbsolutePath().contains(pattern1)&& files[i].getAbsolutePath().contains(".txt")&&!files[i].getAbsolutePath().contains("-settings")){
				StormData tmp = new StormData(files[i].getAbsolutePath());
				sd1.addStormData(tmp);
			}
		}
		for (int i = 0; i<files2.length; i++ ){
			if(files2[i].isFile() && files2[i].getAbsolutePath().contains(pattern2)&& files2[i].getAbsolutePath().contains(".txt")&&!files2[i].getAbsolutePath().contains("-settings")){
				StormData tmp = new StormData(files2[i].getAbsolutePath());
				sd2.addStormData(tmp);
			}
		}
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
		int numberIntervals = (int) Math.ceil((maxVal-minVal)/binwidth);
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
				while (currFrame + maxdistBetweenLocalizations > evaluatedFrame && evaluatedFrame < framemax){//runs as long as there are consecutive localizations within a maximum distance of maxdistBetweenLoc...
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
				pb.updateProgress(i);
			}
			//System.out.println(i +" " +frames.get(i).size());
		}
		System.out.println("Number of detected traces: "+traces.size()+" Number of all localizations: "+locs.size());
		return traces;
	}
	
	public static ArrayList<ArrayList<Double>> getDistancesWithinTraces(ArrayList<ArrayList<StormLocalization>> traces){
		ArrayList<Double> distancesX = new ArrayList<Double>();
		ArrayList<Double> distancesY = new ArrayList<Double>();
		ArrayList<Double> distancesZ = new ArrayList<Double>();
		for (int i = 0; i< traces.size(); i++) {
			if (traces.get(i).size() < 10){ //beads are not connected
				for (int j = 0; j<traces.get(i).size(); j++) {
					distancesX.add(traces.get(i).get(j).getX());
					distancesY.add(traces.get(i).get(j).getY());
					distancesZ.add(traces.get(i).get(j).getZ());
				}
			}
		}
		ArrayList<ArrayList<Double>> retList = new ArrayList<ArrayList<Double>>();
		retList.add(distancesX);
		retList.add(distancesY);
		retList.add(distancesZ);
		return retList;
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
			pb.updateProgress(i);
		}
		return connectedLoc;
	}
}

