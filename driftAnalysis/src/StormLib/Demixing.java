package StormLib;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;
import org.apache.commons.math3.linear.RealVector;

public class Demixing {
	static boolean verbose = false;
	public static StormData spectralUnmixing(StormData ch1, StormData ch2){
		double[][] trafo = findGlobalTransformation(ch1, ch2);
		StormData combinedSet = doUnmixing(ch1, ch2, trafo);
		return combinedSet;
	}
	
	public static StormData spectralUnmixing(StormData ch1, StormData ch2, boolean verbose_){
		verbose = verbose_;
		double[][] trafo = findGlobalTransformation(ch1, ch2);
		StormData combinedSet = doUnmixing(ch1, ch2, trafo, false);
		return combinedSet;
	}
	static StormData doUnmixing(StormData ch1, StormData ch2, double[][] trafo){	
		return doUnmixing( ch1, ch2, trafo, false);
	}
	
	static StormData doUnmixing(StormData ch1, StormData ch2, double[][] trafo, boolean useAll){
		ch1 = TransformationControl.applyTrafo(trafo, ch1);
		double dist = 50; //in nm //this variable determines within which distance for matching points are searched
		double minInt = 1500; // minimal intensity of at least one channel
		if (verbose) {
			System.out.println("start unmixing...");
			System.out.println("maximal tolerance for matching points: "+dist);
			System.out.println("minimal intensity for matching points: "+minInt);
		}
		StormData coloredSet = new StormData();
		coloredSet.setFname(ch1.getFname());
		coloredSet.setPath(ch1.getPath());
		ArrayList<StormLocalization> pairsCh1 = new ArrayList<StormLocalization>();
		ArrayList<StormLocalization> pairsCh2 = new ArrayList<StormLocalization>();
		int matchingCounter = 0;
		int maxFrame = (int) Math.max((double)ch1.getDimensions().get(7),(double)ch2.getDimensions().get(7));
		for (int currFrame = 0; currFrame < maxFrame; currFrame++){
			//System.out.println(currFrame);
			StormData currFrameCh1 = ch1.findSubset(currFrame, currFrame);
			StormData currFrameCh2 = ch2.findSubset(currFrame, currFrame);
			currFrameCh1.sortX();
			currFrameCh2.sortX();
			for (int i = 0; i<currFrameCh1.getSize(); i++){
				StormLocalization currLoc = currFrameCh1.getElement(i); 
				boolean matching = false;
				int startvalue = 0; //if a match was found at the 20 th. position of the second channel, all following matches will lie after that
				for (int j = startvalue; j<currFrameCh2.getSize(); j++){
					StormLocalization thisLoc = currFrameCh2.getElement(j);
					//System.out.println("thisLoc.getX() "+thisLoc.getX()+" "+"currLoc.getX() "+currLoc.getX()+"thisLoc.getY() "+thisLoc.getY()+" "+"currLoc.getY() "+currLoc.getY());
					if (thisLoc.getX()>currLoc.getX()+dist){ //sorted for X, if thislocs x value is larger all following will be larger
						break;
					}
					
					if (Math.abs(thisLoc.getX() - currLoc.getX())<dist && Math.abs(thisLoc.getY() - currLoc.getY())<dist){
						matching = true;
						startvalue = i;
						if (thisLoc.getIntensity()<minInt && currLoc.getIntensity()< minInt){ // skip if not bright enough
							
						}
						else {
							double atan = Math.atan(thisLoc.getIntensity() / currLoc.getIntensity()); //atan(ch2 / ch1)
							coloredSet.addElement(new StormLocalization((thisLoc.getX()+currLoc.getX())/2,(thisLoc.getY()+currLoc.getY())/2,(thisLoc.getZ()+currLoc.getZ())/2, currFrame,(thisLoc.getIntensity()+currLoc.getIntensity())/2, atan));
							pairsCh1.add(currLoc);
							pairsCh2.add(thisLoc);
							matchingCounter = matchingCounter + 1;
						}
						break;
					}
				}
				if (!matching){
					if (currLoc.getIntensity()>minInt && useAll){
						coloredSet.addElement(new StormLocalization(currLoc,0));
					}
				}
			}
			for (int i = 0; i<currFrameCh2.getSize(); i++){ //second run to find all points which are unique in channel 2
				StormLocalization currLoc = currFrameCh2.getElement(i);
				boolean matching = false;
				int startvalue = 0;
				for (int j = startvalue; j<currFrameCh1.getSize(); j++){
					StormLocalization thisLoc = currFrameCh1.getElement(j);
					if (thisLoc.getX()>currLoc.getX()+dist){ //sorted for X, if thislocs x value is larger all following will be larger
						break;
					}
					if (Math.abs(thisLoc.getX() - currLoc.getX())<dist && Math.abs(thisLoc.getY() - currLoc.getY())<dist){
						//double atan = Math.atan(thisLoc.getIntensity() / currLoc.getIntensity());
						//coloredSet.addElement(new StormLocalization((thisLoc.getX()+currLoc.getX())/2,(thisLoc.getY()+currLoc.getY())/2,(thisLoc.getZ()+currLoc.getZ())/2, currFrame,(thisLoc.getIntensity()+currLoc.getIntensity())/2, atan));
						matching = true;
						startvalue = i;
						break;
					}
				}
				if (!matching){
					if (currLoc.getIntensity()>minInt && useAll){
						coloredSet.addElement(new StormLocalization(currLoc,Math.PI/2));
					}
				}
			}
		}
		writeStatistics(ch1.getPath(),ch1.getBasename(), pairsCh1, pairsCh2);
		if (verbose) {
			System.out.println("unmixing done.");
			System.out.println("Number of matches: "+ matchingCounter+" of "+ coloredSet.getSize()+" points.");
		}
		return coloredSet;
	}
	
	static void writeStatistics(String path, String basename, ArrayList<StormLocalization> ch1, ArrayList<StormLocalization> ch2){
		String subfolder = "\\AdditionalInformation";
		new File(path + subfolder).mkdir();
		String fname = "\\"+basename+"_demixingStatistic.txt";
		PrintWriter outputStream;
		try {
			outputStream = new PrintWriter(new FileWriter(path + subfolder+fname));
			outputStream.print("Automatically generated log file for the pairwise occuring points.  ");
			outputStream.println("Structure: data StormLocalization channel 1 , data StormLocalization channel2");
			for (int j = 0;j<ch1.size();j++){
					outputStream.print(ch1.get(j).toPlainString()+" ");
					outputStream.print(ch2.get(j).toPlainString()+" ");
					outputStream.println();
			}
			
			outputStream.close();
			System.out.println("Demixing statistics written");
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	/*static StormData applyTrafo(double[][] trafo, StormData ch2){
		StormData transformedCh2 = new StormData();
		transformedCh2.setFname(ch2.getFname());
		transformedCh2.setPath(ch2.getPath());
		for (int i = 0; i< ch2.getSize(); i++){
			StormLocalization sl = ch2.getElement(i);
			double x = trafo[0][0] * sl.getX() + trafo[0][1] * sl.getY() + trafo[0][2];
			double y = trafo[1][0] * sl.getX() + trafo[1][1] * sl.getY() + trafo[1][2];
			transformedCh2.addElement(new StormLocalization(x,y,sl.getZ(),sl.getFrame(), sl.getIntensity()));
		}
		return transformedCh2;
	}*/
	
	static double[][] findGlobalTransformation(StormData ch1, StormData ch2){
		//int bestMatches = 0;
		double[][] bestTrafo = {{1,0,0},{0,1,0}};
		int nbrIter = 1500;
		ArrayList<ArrayList<ArrayList<StormLocalization>>> collectionOfGoodPoints = new ArrayList<ArrayList<ArrayList<StormLocalization>>>();
		ArrayList<Integer> frames = new ArrayList<Integer>(Arrays.asList(301,305,308));//,10,15,20,25,30,35,50,75,100,200,300,1000,10000,15000,20000,25000));//,3,4,5,6,7,8,9,10,20,90,1000,4000));
	
		if (verbose) {
			System.out.println("finding transformation...");
			System.out.println(nbrIter + " iterations per frame.");
			System.out.print("based on frame(s): ");
			for(int i =0; i<frames.size(); i++){
				System.out.print(frames.get(i)+", ");
			}
			System.out.println(" ");
		}
		
		for (int frame : frames) {
			if (verbose) {
				System.out.println("working on frame: "+frame);
			}
			int bestMatches = 0;
			ArrayList<ArrayList<StormLocalization>> bestSubsets = new ArrayList<ArrayList<StormLocalization>>();
			StormData subset1 = ch1.findSubset(frame, frame+1);
			StormData subset2 = ch2.findSubset(frame, frame+1);
			double[][] distMat = TransformationControl.createDistanceMatrix(subset1, subset2);
			for (int i = 0; i<nbrIter; i++){
				double[][] currTrafo = new double[2][3];
				ArrayList<ArrayList<StormLocalization>> subsets = TransformationControl.findCandidatesForTransformation(distMat, subset1, subset2);
				currTrafo = TransformationControl.findTransformation(subsets);
				boolean usable = TransformationControl.isThisTrafoUsable(currTrafo);
				if (usable){
					int matches = TransformationControl.findMatches(currTrafo, subset1, subset2);
					//System.out.println(matches);
					if (matches>bestMatches){
						bestMatches = matches;
						bestTrafo = currTrafo;
						bestSubsets = subsets;
					}
				}
			}
			collectionOfGoodPoints.add(bestSubsets);
		}
		if (verbose) {
			System.out.println("searching for final transformation based on " + collectionOfGoodPoints.size()+" subsets.");
		}
		double[][] finalTrafo = findFinalTrafo(collectionOfGoodPoints);
		if (verbose) {
			System.out.println("final transformation found:");
			System.out.println(finalTrafo[0][0]+ " "+finalTrafo[0][1]+" "+finalTrafo[0][2]);
			System.out.println(finalTrafo[1][0]+ " "+finalTrafo[1][1]+" "+finalTrafo[1][2]);
		}
		return bestTrafo;
	}
	
	static double[][] findFinalTrafo(ArrayList<ArrayList<ArrayList<StormLocalization>>> collectionOfGoodPoints){
		ArrayList<StormLocalization> ch1 = new ArrayList<StormLocalization>();
		ArrayList<StormLocalization> ch2 = new ArrayList<StormLocalization>();
		for (int i = 0; i<collectionOfGoodPoints.size(); i++){
			for (int j = 0; j< collectionOfGoodPoints.get(i).get(0).size(); j++){
				ch1.add(collectionOfGoodPoints.get(i).get(0).get(j)); //Add all points used for valid transformations to one
				ch2.add(collectionOfGoodPoints.get(i).get(1).get(j)); //dataset
			}
		}
		ArrayList<ArrayList<StormLocalization>> finalSet = new ArrayList<ArrayList<StormLocalization>>();
		finalSet.add(ch1);
		finalSet.add(ch2);

		double[][] finalTrafo = TransformationControl.findTransformation(finalSet);
		//double[][] finalTrafo = {{0.9912178988219845, -0.008543115225657089, 380.4777162113191}, {0.006083027898422995, 0.990757188540027, 101.88096725214348}};
		return finalTrafo;
	}
	
	
	

	
	/*static double[][] findTransformation(ArrayList<ArrayList<StormLocalization>> subsets){
		ArrayList<StormLocalization> pointsCh1 = subsets.get(0);
		ArrayList<StormLocalization> pointsCh2 = subsets.get(1);
		double[][] sCh1 = new double[pointsCh1.size()][3];
		double[][] sCh2 = new double[pointsCh1.size()][3];
		for (int i = 0; i<pointsCh1.size(); i++){
			sCh1[i][0] = pointsCh1.get(i).getX();
			sCh2[i][0] = pointsCh2.get(i).getX();
			sCh1[i][1] = pointsCh1.get(i).getY();
			sCh2[i][1] = pointsCh2.get(i).getY();
			sCh1[i][2] = 1;
			sCh2[i][2] = 1;
		}
		RealMatrix mCh1 = new Array2DRowRealMatrix(sCh1);
		RealMatrix mCh2 = new Array2DRowRealMatrix(sCh2);
		RealMatrix rx = mCh2.getColumnMatrix(0).transpose().multiply(mCh1);
		RealMatrix ry = mCh2.getColumnMatrix(1).transpose().multiply(mCh1);
		double[][] mtmp = {{0,0,0},{0,0,0},{0,0,0}};
		RealMatrix m = new Array2DRowRealMatrix(mtmp);
		for (int i = 0; i< pointsCh1.size(); i++){
			RealMatrix tm = mCh1.getRowMatrix(i).transpose().multiply(mCh1.getRowMatrix(i));
			for (int k = 0; k<3; k++){
				for (int kk = 0; kk<3; kk++){
					m.addToEntry(k, kk, tm.getEntry(k,kk));
				}
			}
			//m.add(mCh1.getRowMatrix(i).transpose().multiply(mCh1.getRowMatrix(i))); //this did not work idk why
		}
		DecompositionSolver solver = new LUDecomposition(m).getSolver();
		try{
			RealMatrix solx = solver.solve(rx.transpose());
			RealMatrix soly = solver.solve(ry.transpose());
			double[][] retMat = new double[2][3];
			retMat[0][0] = solx.getEntry(0, 0);
			retMat[0][1] = solx.getEntry(1, 0);
			retMat[0][2] = solx.getEntry(2, 0);
			retMat[1][0] = soly.getEntry(0, 0);
			retMat[1][1] = soly.getEntry(1, 0);
			retMat[1][2] = soly.getEntry(2, 0);
			return retMat;
		} catch(org.apache.commons.math3.linear.SingularMatrixException e){ 
			double[][] unit = {{1,0,0},{0,1,0}};
			return unit;
		}
		
		
	}*/
	
	/*static ArrayList<ArrayList<StormLocalization>> findCandidatesForTransformation(double[][] distMat, StormData subset1, StormData subset2){
		int minPointsReq = 3; 
		Random rand = new Random();
		ArrayList<Integer> randomIndicesCh1 = new ArrayList<Integer>();
		ArrayList<Integer> assignedIndicesCh2 = new ArrayList<Integer>();
		for (int i = 0; i<minPointsReq; i++){	//duplicates are allowed but will do no harm due to high number of tries
			randomIndicesCh1.add(rand.nextInt(subset1.getSize()));
		}
		// pick partner based on probabilistic approach
		//nearer points of the other channel have higher probability
		//to be chosen
		for (int j = 0; j< minPointsReq; j++){
			double sum = 0;
			Pair[] tmp = new Pair[subset2.getSize()];
			for (int i = 0; i<subset2.getSize(); i++){
				tmp[i] = new Pair(distMat[randomIndicesCh1.get(j)][i],i);
				sum = sum + 1./distMat[randomIndicesCh1.get(j)][i];
			}
			
			Arrays.sort(tmp);
			double marker = rand.nextDouble() * sum;
			sum = 0;
			for (int i = 0; i< subset2.getSize(); i++){
				sum = sum + 1./((double) tmp[i].getValue());
				if (marker< sum){
					assignedIndicesCh2.add(tmp[i].getIndex());
					break;
				}
			}
		}
		ArrayList<ArrayList<StormLocalization>> candidates = new ArrayList<ArrayList<StormLocalization>>();
		ArrayList<StormLocalization> slCh1 = new ArrayList<StormLocalization>();
		ArrayList<StormLocalization> slCh2 = new ArrayList<StormLocalization>();
		for (int i= 0;i<minPointsReq; i++){
			slCh1.add(subset1.getElement(randomIndicesCh1.get(i)));
			slCh2.add(subset2.getElement(assignedIndicesCh2.get(i)));
		}
		candidates.add(slCh1);
		candidates.add(slCh2);
		return candidates;
	}
	static double[][] createDistanceMatrix(StormData subset1, StormData subset2){
		int dim0 = subset1.getSize();
		int dim1 = subset2.getSize();
		double[][] distMat = new double[dim0][dim1];
		for (int i = 0; i< dim0; i++){
			for (int j = 0; j<dim1; j++){
				StormLocalization sl1 = subset1.getElement(i);
				StormLocalization sl2 = subset2.getElement(j);
				distMat[i][j] = Math.pow(sl1.getX() - sl2.getX(),2) + Math.pow(sl1.getY()-sl2.getY(),2)+Math.pow(sl1.getZ()-sl2.getZ(), 2);
			}
		}
		return distMat;
	}
	*/
}

