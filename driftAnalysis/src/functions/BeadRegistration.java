package functions;

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

import dataStructure.StormData;
import dataStructure.StormLocalization;
import StormLib.Utilities;

public class BeadRegistration {

	public static ArrayList<StormData> doRegistration(StormData sd1,
			StormData sd2) {
		ArrayList<ArrayList<StormLocalization>> beads = findBeads(sd1,sd2);

		String b1 = "";
		for(int i = 0; i<beads.get(0).size(); i++){
			b1 = b1+beads.get(0).get(i).toPlainString()+"\n";
		}
		String b2 = "";
		for(int i = 0; i<beads.get(1).size(); i++){
			b2 = b2+beads.get(1).get(i).toPlainString()+"\n";
		}
		System.out.println(b1);
		System.out.println("  ");
		System.out.println(b2);
		System.out.println("number Beads channel1: "+beads.get(0).size()+" number Beads channel2: "+beads.get(1).size());
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
		double bestError = 1e8;
		double toleranceForMatching = 200;
		double[][] bestTrafo = {{1,0,0},{0,1,0}};
		ArrayList<ArrayList<StormLocalization>> bestSubsets = new ArrayList<ArrayList<StormLocalization>>();
		StormData set1 = new StormData();
		set1.setLocs(beads.get(0));
		StormData set2 = new StormData();
		set2.setLocs(beads.get(1));
		double[][] distmat = TransformationControl.createDistanceMatrix(set1,set2);
		int nbrIter = 100000;
		for (int i = 0; i<nbrIter; i++){
			if(i%1000==0){
				System.out.println(i+ " /"+nbrIter);
			}
			double[][] currTrafo = new double[2][3];
			ArrayList<ArrayList<StormLocalization>> subsets = TransformationControl.findCandidatesForTransformation(distmat, set1, set2,5);
			currTrafo = TransformationControl.findTransformation(subsets);
			boolean usable = TransformationControl.isThisTrafoUsable(currTrafo);
			if (usable){
				int matches = TransformationControl.findMatches(currTrafo, set1, set2,toleranceForMatching);
				double error = TransformationControl.findError(currTrafo, set1, set2,toleranceForMatching);
				if (matches>bestMatches || (matches == bestMatches && error< bestError)){
					bestMatches = matches;
					bestTrafo = currTrafo;
					bestSubsets = subsets;
					bestError = error;
					System.out.println("matches: "+matches+ " Error: "+error);
				}
			}
		}
		System.out.println("maximal number of matches: "+bestMatches+" with error: "+ bestError);
		return bestTrafo;
	}
	
	public static ArrayList<ArrayList<double[]>> findBeadCandidatesImageBased(StormData sd1, StormData sd2, int pixelsize){
		ImagePlus img1 = sd1.renderImage2D(pixelsize,true,"channel1");
		ImagePlus img2 = sd2.renderImage2D(pixelsize,true,"channel2");
		MaximumFinder mf = new MaximumFinder();
		Polygon maxima1 = mf.getMaxima(img1.getProcessor(), img1.getStatistics().max*0.3,true);
		Polygon maxima2 = mf.getMaxima(img2.getProcessor(), img2.getStatistics().max*0.3,true);
		ArrayList<ArrayList<double[]>> retList = new ArrayList<ArrayList<double[]>>();
		ArrayList<double[]> ch1 = new ArrayList<double[]>();
		ArrayList<double[]> ch2 = new ArrayList<double[]>();
		for (int i = 0; i<maxima1.npoints; i++){
			double[] tmp = {maxima1.xpoints[i], maxima1.ypoints[i]};
			ch1.add(tmp);
		}
		for (int i = 0; i<maxima2.npoints; i++){
			double[] tmp = {maxima2.xpoints[i], maxima2.ypoints[i]};
			ch2.add(tmp);
		}
		retList.add(ch1);
		retList.add(ch2);
		return retList;
	}
	
	public static ArrayList<ArrayList<double[]>> findBeadCandidatesTraceBased(StormData sd1, 
			StormData sd2, int minimalTracelength){
		ArrayList<ArrayList<double[]>> retList = new ArrayList<ArrayList<double[]>>();
		ArrayList<double[]> candidatesCh1 = new ArrayList<double[]>();
		ArrayList<double[]> candidatesCh2 = new ArrayList<double[]>();
		ArrayList<ArrayList<StormLocalization>> tracesCh1 = 
				Utilities.findTraces(sd1.getLocs(), 200, 200, 400, 3);
		int maxTraceLength = 0;
		for (int i = 0; i< tracesCh1.size(); i++){
			if (tracesCh1.get(i).size() > maxTraceLength){
				maxTraceLength = tracesCh1.get(i).size();
				System.out.println("Maximal TraceLengthCh1: "+maxTraceLength);
			}
			if (tracesCh1.get(i).size()>minimalTracelength){
				double meanX = 0;
				double meanY = 0;
				for (int j = 0; j<tracesCh1.get(i).size(); j++){
					meanX = meanX + tracesCh1.get(i).get(j).getX();
					meanY = meanY + tracesCh1.get(i).get(j).getY();
				}
				double[] tmp = {meanX / tracesCh1.get(i).size(),meanY / tracesCh1.get(i).size()};
				candidatesCh1.add(tmp);
			}
		}
		ArrayList<ArrayList<StormLocalization>> tracesCh2 = 
				Utilities.findTraces(sd2.getLocs(), 200, 200, 200, 5);
		maxTraceLength = 0;
		for (int i = 0; i< tracesCh2.size(); i++){
			if (tracesCh2.get(i).size() > maxTraceLength){
				maxTraceLength = tracesCh2.get(i).size();
				System.out.println("Maximal TraceLengthCh2: "+maxTraceLength);
			}
			if (tracesCh2.get(i).size()>minimalTracelength){
				double meanX = 0;
				double meanY = 0;
				for (int j = 0; j<tracesCh2.get(i).size(); j++){
					meanX = meanX + tracesCh2.get(i).get(j).getX();
					meanY = meanY + tracesCh2.get(i).get(j).getY();
				}
				double[] tmp = {meanX / tracesCh2.get(i).size(),meanY / tracesCh2.get(i).size()};
				candidatesCh2.add(tmp);
			}
		}
		retList.add(candidatesCh1);
		retList.add(candidatesCh2);
		return retList;
	}
	
	private static ArrayList<ArrayList<StormLocalization>> findBeads(
			StormData sd1, StormData sd2) {

		int pixelsize = 10;
		ArrayList<ArrayList<double[]>> beadEstimates = findBeadCandidatesImageBased(sd1,sd2,pixelsize);
		ArrayList<ArrayList<double[]>> beadEstimates2 = findBeadCandidatesTraceBased(sd1,sd2,400);

		ArrayList<ArrayList<StormLocalization>> listOfBeadsCh1 = new ArrayList<ArrayList<StormLocalization>>(); //an Arraylist for each potential bead
		ArrayList<ArrayList<StormLocalization>> listOfBeadsCh2 = new ArrayList<ArrayList<StormLocalization>>(); //to collect all localizations to be averaged later
		for (int i = 0; i<beadEstimates.get(0).size(); i++){
			listOfBeadsCh1.add(new ArrayList<StormLocalization>());
		}
		for (int i = 0; i<beadEstimates.get(1).size(); i++){
			listOfBeadsCh2.add(new ArrayList<StormLocalization>());
		}
		sd1.sortX();
		sd2.sortX();
		double lateralTolerance = 100; //in nm
		for (int i = 0; i<sd1.getSize();i++){
			for (int j = 0; j<beadEstimates.get(0).size();j++){
				if(Math.abs(sd1.getElement(i).getX()-pixelsize*beadEstimates.get(0).get(j)[0])<lateralTolerance
				&& Math.abs(sd1.getElement(i).getY()-pixelsize*beadEstimates.get(0).get(j)[1])<lateralTolerance){
					listOfBeadsCh1.get(j).add(sd1.getElement(i));
				}
			}
		}
		for (int i = 0; i<sd2.getSize();i++){
			for (int j = 0; j<beadEstimates.get(1).size();j++){
				if(Math.abs(sd2.getElement(i).getX()-pixelsize*beadEstimates.get(1).get(j)[0])<lateralTolerance
				&& Math.abs(sd2.getElement(i).getY()-pixelsize*beadEstimates.get(1).get(j)[1])<lateralTolerance){
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
