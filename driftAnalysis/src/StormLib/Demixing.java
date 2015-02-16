package StormLib;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import StormLib.HelperClasses.DemixingResultLog;
import StormLib.HelperClasses.DemixingTransformationLog;


public class Demixing {
	static boolean verbose = false;
	static ExecutorService executor;
	static ExecutorService executor2;
	public static StormData spectralUnmixing(StormData ch1, StormData ch2){
		executor = Executors.newFixedThreadPool(7);
		executor2 = Executors.newFixedThreadPool(7);
		double[][] trafo = findGlobalTransformationMultithreaded(ch1, ch2);
		StormData combinedSet = doUnmixingMultiThreaded(ch1,ch2,trafo);
		//StormData combinedSet = doUnmixing(ch1, ch2, trafo);
		return combinedSet;
	}
	
	static StormData doUnmixingMultiThreaded(StormData ch1, StormData ch2, double[][] trafo){
		ch1 = TransformationControl.applyTrafo(trafo, ch1);
		double dist = 75; //in nm //this variable determines within which distance for matching points are searched
		double minInt = 500; // minimal intensity of at least one channel
		if (verbose) {
			System.out.println("start unmixing...");
			System.out.println("maximal tolerance for matching points: "+dist);
			System.out.println("minimal intensity for matching points: "+minInt);
		}
		StormData coloredSet = new StormData();
		coloredSet.setFname(ch1.getMeassurement());
		coloredSet.setPath(ch1.getPath());
		coloredSet.setProcessingLog(ch1.getProcessingLog()+"demixed");
		for (int i = 0; i<ch1.getLog().size();i++){
			coloredSet.addToLog(ch1.getLog().get(i));
		}
		for (int i = 0; i<ch2.getLog().size();i++){
			coloredSet.addToLog(ch2.getLog().get(i));
		}
		DemixingData demixingData = new DemixingData();
		int maxFrame = (int) Math.max((double)ch1.getDimensions().get(7),(double)ch2.getDimensions().get(7));
		Progressbar pb = new Progressbar(0, maxFrame,0, "Start demixing ...");
		for (int currFrame = 0; currFrame < maxFrame; currFrame++){
			Runnable t = new Thread(new UnmixFrame(demixingData, coloredSet, ch1, ch2, 
					currFrame, dist, minInt,pb));
			executor2.execute(t);
		}
		executor2.shutdown();
		try {
			executor2.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
		
		}
		OutputClass.writeDemixingOutput(ch1.getPath(), ch1.getBasename(), demixingData.getList1(), demixingData.getList2(), ch1.getProcessingLog());
		DemixingResultLog rl = new DemixingResultLog(demixingData.getList1().size(), demixingData.getList2().size());
		ch1.addToLog(rl);
		if (true) {
			System.out.println("unmixing done.");
			System.out.println("Number of matches: "+ demixingData.getList1().size()+" of "+ coloredSet.getSize()+" points.");
		}
		return coloredSet;
	}
	
		static double[][] findGlobalTransformationMultithreaded(StormData ch1, StormData ch2){
		int nbrIter = 2000;
		double toleratedError = 50;
		ArrayList<ArrayList<ArrayList<StormLocalization>>> collectionOfGoodPoints = new ArrayList<ArrayList<ArrayList<StormLocalization>>>();
		ArrayList<Integer> listOfMatchingPoints = new ArrayList<Integer>();
		ArrayList<Double> listOfErrors = new ArrayList<Double>();
		ArrayList<Integer> frames = new ArrayList<Integer>();//(Arrays.asList(1,10,15,20,25,30,35,50));//,75,100,200,300,1000,10000,15000,20000,25000));//,3,4,5,6,7,8,9,10,20,90,1000,4000));
		for (int i = 0; i<20;i++){
			frames.add(i*10);
		}
		Progressbar pb = new Progressbar(0,nbrIter * frames.size(),0,"Finding transformation.");
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
			Runnable t = new Thread(new findTransformation(collectionOfGoodPoints, listOfMatchingPoints, listOfErrors, 
					frame, ch1, ch2, verbose, nbrIter, toleratedError, pb));
			executor.execute(t);
		}
		executor.shutdown();
		try {
			executor.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
		
		}
		
		if (verbose) {
			System.out.println("searching for final transformation based on " + collectionOfGoodPoints.size()+" subsets.");
		}
		OutputClass.writeDemixingParameters(ch1.getPath(), ch1.getBasename(), ch1.getProcessingLog(), nbrIter, toleratedError, frames, listOfMatchingPoints, listOfErrors);
		double[][] finalTrafo = TransformationControl.findFinalTrafo(collectionOfGoodPoints);
		DemixingTransformationLog tl = new DemixingTransformationLog(nbrIter, toleratedError, frames,listOfMatchingPoints, listOfErrors, finalTrafo);
		ch1.addToLog(tl);
		if (true) {
			System.out.println("final transformation found:");
			System.out.println(finalTrafo[0][0]+ " "+finalTrafo[0][1]+" "+finalTrafo[0][2]);
			System.out.println(finalTrafo[1][0]+ " "+finalTrafo[1][1]+" "+finalTrafo[1][2]);
		}
		
		return finalTrafo;
	}
	
	static ArrayList<ArrayList<StormLocalization>> findCandidatesForTransformation(double[][] distMat, StormData subset1, StormData subset2){
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
	
}

class Pair<T extends Comparable<T>> implements Comparable<Pair<T>>{
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

class findTransformation implements Runnable{
	private ArrayList<ArrayList<ArrayList<StormLocalization>>> collectionOfGoodPoints;
	private ArrayList<Integer> listOfMatchingPoints;
	private ArrayList<Double> listOfErrors;
	private int frame;
	private StormData ch1;
	private StormData ch2;
	private boolean verbose;
	private int nbrIter;
	private Progressbar pb;
	private double toleratedError;
	
	public findTransformation(ArrayList<ArrayList<ArrayList<StormLocalization>>> collectionOfGoodPoints,
			ArrayList<Integer> listOfMatchingPoints, ArrayList<Double> listOfErrors,
			int frame,StormData ch1, StormData ch2, boolean verbose, int nbrIter, double toleratedError, Progressbar pb){
		this.collectionOfGoodPoints = collectionOfGoodPoints;
		this.listOfMatchingPoints = listOfMatchingPoints;
		this.listOfErrors = listOfErrors;
		this.frame = frame;
		this.ch1 = ch1;
		this.ch2 = ch2;
		this.verbose = verbose;
		this.nbrIter = nbrIter;
		this.pb = pb;
		this.toleratedError = toleratedError;
	}
	public void run(){
		int bestMatches = 0;
		double bestError = 1e10;
		ArrayList<ArrayList<StormLocalization>> bestSubsets = new ArrayList<ArrayList<StormLocalization>>();
		StormData subset1 = ch1.findSubset(frame, frame+1,true);
		StormData subset2 = ch2.findSubset(frame, frame+1,true);
		double[][] distMat = TransformationControl.createDistanceMatrix(subset1, subset2);
		for (int i = 0; i<nbrIter; i++){
			double[][] currTrafo = new double[2][3];
			ArrayList<ArrayList<StormLocalization>> subsets = TransformationControl.findCandidatesForTransformation(distMat, subset1, subset2);
			currTrafo = TransformationControl.findTransformation(subsets);
			boolean usable = TransformationControl.isThisTrafoUsable(currTrafo);
			if (usable){
				int matches = TransformationControl.findMatches(currTrafo, subset1, subset2, toleratedError);
				double rsme = TransformationControl.findError(currTrafo, subset1, subset2,toleratedError);
				//System.out.println(matches);
				if (matches >=3 && matches>bestMatches || (matches == bestMatches && rsme<bestError)){
					bestMatches = matches;
					bestSubsets = subsets;
					bestError = rsme;
					//System.out.println("matches: "+matches+" error: "+rsme+" frame: " +frame);
				}
			}
			pb.updateProgress();
		}
		synchronized(this){
			if (bestMatches > 3 && bestError<toleratedError){
				collectionOfGoodPoints.add(bestSubsets);
				listOfMatchingPoints.add(bestMatches);
				listOfErrors.add(bestError);}
		}
		
	}
}

class UnmixFrame implements Runnable{
	private DemixingData demixingData;
	StormData coloredSet;
	private StormData ch1;
	private StormData ch2;
	int currFrame;
	double dist;
	double minInt;
	Progressbar pb;
	
	public UnmixFrame(DemixingData demixingData ,StormData coloredSet,StormData ch1, 
			StormData ch2, int currFrame, double dist, double minInt, Progressbar pb){
		this.demixingData = demixingData;
		this.coloredSet = coloredSet;
		this.ch1 = ch1;
		this.ch2 = ch2;
		this.currFrame = currFrame;
		this.dist = dist;
		this.minInt = minInt;
		this.pb = pb;
	}
	
	public void run(){
		boolean useAll = false;
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
						synchronized(coloredSet){
							coloredSet.addElement(new StormLocalization((thisLoc.getX()+currLoc.getX())/2,(thisLoc.getY()+currLoc.getY())/2,(thisLoc.getZ()+currLoc.getZ())/2, currFrame,(thisLoc.getIntensity()+currLoc.getIntensity())/2, atan));}
						synchronized(demixingData){
						demixingData.addElements(currLoc, thisLoc);}
						//matchingCounter = matchingCounter + 1;
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
		pb.updateProgress();
	}
}

class DemixingData{
	private ArrayList<StormLocalization> listCh1 = new ArrayList<StormLocalization>();
	private ArrayList<StormLocalization> listCh2 = new ArrayList<StormLocalization>();
	synchronized void addElements(StormLocalization slch1,StormLocalization slch2){
		listCh1.add(slch1);
		listCh2.add(slch2);
	}
	public ArrayList<StormLocalization> getList1(){
		return listCh1;
	}
	public ArrayList<StormLocalization> getList2(){
		return listCh2;
	}
}
