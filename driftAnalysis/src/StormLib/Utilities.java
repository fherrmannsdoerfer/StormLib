package StormLib;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

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
		int numberIntervals = (int) Math.floor((maxVal-minVal)/binwidth);
		Collections.sort(vals);
		int currentBin = 0;
		ArrayList<Double> means = new ArrayList<Double>();
		ArrayList<Double> counts = new ArrayList<Double>();
		for (int j = 0; j<numberIntervals; j++){
			counts.add((double) 0);
			means.add(minVal + (j+0.5)*binwidth);
		}
		for (int i = 0; i< vals.size(); i++){
			if (vals.get(i)<(currentBin+1)*binwidth+minVal){
				currentBin = currentBin +1;
				i = i -1;
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
		return null;
	}
}