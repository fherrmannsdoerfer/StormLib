package StormLib;

import ij.ImagePlus;
import ij.plugin.filter.MaximumFinder;

import java.awt.Polygon;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;
import org.apache.commons.math3.stat.descriptive.rank.Median;

public class BeadRegistration {

	public static ArrayList<StormData> doRegistration(StormData sd1,
			StormData sd2) {
		ArrayList<ArrayList<StormLocalization>> beads = findBeads(sd1,sd2);
		double[][] trafo = findTransformation(beads);
		StormData transformedSd1 = TransformationControl.applyTrafo(trafo, sd1);
		writeTransformation(sd1.getPath(), sd1.getBasename(), trafo);
		ArrayList<StormData> retList = new ArrayList<StormData>();
		retList.add(transformedSd1);
		retList.add(sd2);
		return retList;
	}

	private static double[][] findTransformation(
			ArrayList<ArrayList<StormLocalization>> beads) {
		int bestMatches = 0;
		double[][] bestTrafo = {{1,0,0},{0,1,0}};
		ArrayList<ArrayList<StormLocalization>> bestSubsets = new ArrayList<ArrayList<StormLocalization>>();
		StormData set1 = new StormData();
		set1.setLocs(beads.get(0));
		StormData set2 = new StormData();
		set2.setLocs(beads.get(1));
		double[][] distmat = TransformationControl.createDistanceMatrix(set1,set2);
		int nbrIter = 1500;
		for (int i = 0; i<nbrIter; i++){
			double[][] currTrafo = new double[2][3];
			ArrayList<ArrayList<StormLocalization>> subsets = TransformationControl.findCandidatesForTransformation(distmat, set1, set2);
			currTrafo = TransformationControl.findTransformation(subsets);
			boolean usable = TransformationControl.isThisTrafoUsable(currTrafo);
			if (usable){
				int matches = TransformationControl.findMatches(currTrafo, set1, set2);
				//System.out.println(matches);
				if (matches>bestMatches){
					bestMatches = matches;
					bestTrafo = currTrafo;
					bestSubsets = subsets;
				}
			}
		}
		return bestTrafo;
	}

	private static ArrayList<ArrayList<StormLocalization>> findBeads(
			StormData sd1, StormData sd2) {
		int pixelsize = 20;
		ImagePlus img1 = sd1.renderImage2D(pixelsize,false);
		ImagePlus img2 = sd2.renderImage2D(pixelsize,false);
		MaximumFinder mf = new MaximumFinder();
		Polygon maxima1 = mf.getMaxima(img1.getProcessor(), img1.getStatistics().max*0.1,true);
		Polygon maxima2 = mf.getMaxima(img2.getProcessor(), img2.getStatistics().max*0.1,true);

		ArrayList<ArrayList<StormLocalization>> listOfBeadsCh1 = new ArrayList<ArrayList<StormLocalization>>(); //an Arraylist for each potential bead
		ArrayList<ArrayList<StormLocalization>> listOfBeadsCh2 = new ArrayList<ArrayList<StormLocalization>>(); //to collect all localizations to be averaged later
		for (int i = 0; i<maxima1.npoints; i++){
			listOfBeadsCh1.add(new ArrayList<StormLocalization>());
		}
		for (int i = 0; i<maxima2.npoints; i++){
			listOfBeadsCh2.add(new ArrayList<StormLocalization>());
		}
		sd1.sortX();
		sd2.sortX();
		double lateralTolerance = 3* pixelsize; //in nm
		for (int i = 0; i<sd1.getSize();i++){
			for (int j = 0; j<maxima1.npoints;j++){
				if(Math.abs(sd1.getElement(i).getX()-pixelsize*maxima1.xpoints[j])<lateralTolerance
				&& Math.abs(sd1.getElement(i).getY()-pixelsize*maxima1.ypoints[j])<lateralTolerance){
					listOfBeadsCh1.get(j).add(sd1.getElement(i));
				}
			}
		}
		for (int i = 0; i<sd2.getSize();i++){
			for (int j = 0; j<maxima2.npoints;j++){
				if(Math.abs(sd2.getElement(i).getX()-pixelsize*maxima2.xpoints[j])<lateralTolerance
				&& Math.abs(sd2.getElement(i).getY()-pixelsize*maxima2.ypoints[j])<lateralTolerance){
					listOfBeadsCh2.get(j).add(sd2.getElement(i));
				}
			}
		}
			
		ArrayList<StormLocalization> sl1 = new ArrayList<StormLocalization>();
		ArrayList<StormLocalization> sl2 = new ArrayList<StormLocalization>();
	
		for (int j = 0; j<listOfBeadsCh1.size(); j++){
			double posx = 0;
			double posy = 0;
			double posz = 0;
			double weights = 0;
			ArrayList<StormLocalization> currBead = listOfBeadsCh1.get(j);
			for (int i = 0; i < currBead.size(); i++){
				posx = posx + currBead.get(i).getX() * Math.sqrt(currBead.get(i).getIntensity()); //weight by squareroot of intensity
				posy = posy + currBead.get(i).getY() * Math.sqrt(currBead.get(i).getIntensity());
				posz = posz + currBead.get(i).getZ() * Math.sqrt(currBead.get(i).getIntensity());
				weights = weights + Math.sqrt(currBead.get(i).getIntensity());
			}
			sl1.add(new StormLocalization(posx / weights, posy / weights, posz / weights, 0, 1));
		}
		
		for (int j = 0; j<listOfBeadsCh2.size(); j++){
			double posx = 0;
			double posy = 0;
			double posz = 0;
			double weights = 0;
			ArrayList<StormLocalization> currBead = listOfBeadsCh2.get(j);
			for (int i = 0; i < currBead.size(); i++){
				posx = posx + currBead.get(i).getX() * Math.sqrt(currBead.get(i).getIntensity()); //weight by squareroot of intensity
				posy = posy + currBead.get(i).getY() * Math.sqrt(currBead.get(i).getIntensity());
				posz = posz + currBead.get(i).getZ() * Math.sqrt(currBead.get(i).getIntensity());
				weights = weights + Math.sqrt(currBead.get(i).getIntensity());
			}
			sl2.add(new StormLocalization(posx / weights, posy / weights, posz / weights, 0, 1));
		}
		
		ArrayList<ArrayList<StormLocalization>> retList = new ArrayList<ArrayList<StormLocalization>>();
		retList.add(sl1);
		retList.add(sl2);
		return retList;
	}
	
	static void writeTransformation(String path, String basename, double[][] trafo){
		String subfolder = "\\AdditionalInformation";
		new File(path + subfolder).mkdir();
		String fname = "\\"+basename+"_transformationMatrix.txt";
		PrintWriter outputStream;
		try {
			outputStream = new PrintWriter(new FileWriter(path + subfolder+fname));
			outputStream.println("Automatically generated file containing the transformation matrix applied to register two color probes");
			outputStream.println(trafo[0][0]+ " "+trafo[0][1]+" "+trafo[0][2]);
			outputStream.println(trafo[1][0]+ " "+trafo[1][1]+" "+trafo[1][2]);
			
			outputStream.close();
			System.out.println("Transformation written");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
}
