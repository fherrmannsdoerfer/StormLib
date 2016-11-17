
package functions;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;

import org.apache.commons.math3.linear.Array2DRowRealMatrix;
import org.apache.commons.math3.linear.DecompositionSolver;
import org.apache.commons.math3.linear.LUDecomposition;
import org.apache.commons.math3.linear.RealMatrix;

import dataStructure.StormData;
import dataStructure.StormLocalization;


public class TransformationControl {
	
	public static ArrayList<ArrayList<StormLocalization>> findCandidatesForTransformation(
			double[][] distMat, StormData subset1, StormData subset2){
		return findCandidatesForTransformation(distMat, subset1, subset2, 3);
	}
	public synchronized static ArrayList<ArrayList<StormLocalization>> findCandidatesForTransformation(
			double[][] distMat, StormData subset1, StormData subset2, int minPointsReq) {
			if (subset1.getSize()<minPointsReq || subset2.getSize()<minPointsReq){
				//System.out.println("not enough points in either dataset1 or dataset2");
				return new ArrayList<ArrayList<StormLocalization>>();
			}
			Random rand = new Random();
			ArrayList<Integer> randomIndicesCh1 = new ArrayList<Integer>();
			ArrayList<Integer> assignedIndicesCh2 = new ArrayList<Integer>();
			for (int i = 0; i<minPointsReq; i++){	//duplicates are allowed but will do no harm due to high number of tries
				int randI = rand.nextInt(subset1.getSize());
				for(int j = 0; j<randomIndicesCh1.size(); j++){
					if(randomIndicesCh1.get(j) == randI){
						i = i-1;
						break;
					}
				}
				randomIndicesCh1.add(randI);
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
				try{
					slCh1.add(subset1.getElement(randomIndicesCh1.get(i)));
					slCh2.add(subset2.getElement(assignedIndicesCh2.get(i)));
				} catch(Error e)
				{System.out.println(e);};
			}
			candidates.add(slCh1);
			candidates.add(slCh2);
			return candidates;
		}

	public static StormData applyTrafo(double[][] trafo, StormData ch2){
		StormData transformedCh2 = new StormData();
		transformedCh2.copyAttributes(ch2);
		transformedCh2.setFname(transformedCh2.getBasename()+"Transformed.txt");
		for (int i = 0; i< ch2.getSize(); i++){
			StormLocalization sl = ch2.getElement(i);
			double x = trafo[0][0] * sl.getX() + trafo[0][1] * sl.getY() + trafo[0][2];
			double y = trafo[1][0] * sl.getX() + trafo[1][1] * sl.getY() + trafo[1][2];
			transformedCh2.addElement(new StormLocalization(x,y,sl.getZ(),sl.getFrame(), sl.getIntensity()));
		}
		return transformedCh2;
	}
	public static double[][] createDistanceMatrix(StormData subset1, StormData subset2){
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
	
	public static double[][] findTransformation(ArrayList<ArrayList<StormLocalization>> subsets){
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
	
	static boolean isThisTrafoUsable(double[][] currTrafo){
		boolean usable = false;
		if (Math.abs((Math.pow(currTrafo[0][0],2)+Math.pow(currTrafo[0][1],2))-1)<0.2 &&Math.abs((Math.pow(currTrafo[1][0],2)+Math.pow(currTrafo[1][1],2))-1)<0.2){
			if (Math.abs(currTrafo[0][0]-1)<0.2 &&Math.abs(currTrafo[1][1]-1)<0.2){
				usable = true;
			}
		}
		
		if (currTrafo[0][0] == 1 && currTrafo[0][1] == 0 && currTrafo[0][2] == 0 && currTrafo[1][0] == 0 && currTrafo[1][1] == 1 && currTrafo[1][2] == 0){
			usable = false;
		}
		return usable;
	}
	static int findMatches(double[][] currTrafo, StormData 			subset1, StormData subset2, double toleranceForMatching){
		int matches = 0;
		StormData transformedSubset1 = TransformationControl.applyTrafo(currTrafo, subset1);
		double[][] distMat = TransformationControl.createDistanceMatrix(transformedSubset1,subset2);
		for (int i = 0; i<subset1.getSize(); i++){
			for(int j = 0; j<subset2.getSize(); j++){
				if (distMat[i][j]<toleranceForMatching){
					matches = matches + 1;
				}
			}
		}
		return matches;
	}
	
	static double findError(double[][] currTrafo, StormData subset1, StormData subset2, double toleranceForMatching){
		double error = 0;
		int counter = 0;
		StormData transformedSubset1 = TransformationControl.applyTrafo(currTrafo, subset1);
		double[][] distMat = TransformationControl.createDistanceMatrix(transformedSubset1,subset2);
		for (int i = 0; i<subset1.getSize(); i++){
			for(int j = 0; j<subset2.getSize(); j++){
				if (distMat[i][j]<toleranceForMatching){
					error = error + distMat[i][j];
					counter = counter + 1;
				}
			}
		}
		if (counter > 0) {
			return error/counter;
		}
		else {
			return 0;
		}
	}
	
	static class Pair<T extends Comparable<T>> implements Comparable<Pair<T>>{
		final T value;
		final int index;
		
		public Pair(T value, int index){
			this.value = value;
			this.index = index;
		}
		public double getValue(){
			return (Double) value;
		}
		
		public int getIndex(){
			return index;
		}
		
		@Override
		public int compareTo(Pair<T> o){
			return value.compareTo(o.value);
		}
	}

	
}
