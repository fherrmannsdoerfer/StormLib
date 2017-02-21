package functions;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import dataStructure.StormData;
import dataStructure.StormLocalization;
import StormLib.OutputClass;
import StormLib.Progressbar;
import StormLib.Utilities;
import StormLib.HelperClasses.DemixingResultLog;
import StormLib.HelperClasses.DemixingTransformationLog;


public class Demixing {
	private static PropertyChangeSupport propertyChangeSupport =
		       new PropertyChangeSupport(Demixing.class);
	private static int lastVal = 0;
	static boolean verbose = false;
	static ExecutorService executor;
	static ExecutorService executor2;
	static double[][] noTransMat = {{1, 0, 0},{0, 1, 0}};
	
	public static void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }

    public static void setProgress(String messageName, int val) {
    	propertyChangeSupport.firePropertyChange(messageName, lastVal, val);
		lastVal = val;
    }
    
    public static StormData spectralUnmixing(StormData ch1, StormData ch2, boolean useAll,
			String tag){
		return spectralUnmixing(ch1, ch2, useAll,"",60,500, 1500, 50,false);
	}
	
	public static StormData spectralUnmixing(StormData ch1, StormData ch2, boolean useAll,
			double dist,double minInt, int nbrIter, double toleratedError){
		return spectralUnmixing(ch1, ch2, useAll,"",dist,minInt, nbrIter, toleratedError,false);
	}
	
	public static StormData spectralUnmixing(StormData ch1, StormData ch2,double dist, double minInt,
			int nbrIter, double toleratedError,boolean savePairedPoints){
		return spectralUnmixing(ch1, ch2, false,"",dist, minInt, nbrIter, toleratedError, savePairedPoints);
	}
	
	public static StormData spectralUnmixing(StormData ch1, StormData ch2, boolean useAll, String tag,
			double dist, double minInt, int nbrIter, double toleratedError,boolean savePairedPoints){
		executor = Executors.newFixedThreadPool(12);
		executor2 = Executors.newFixedThreadPool(12);
		double[][] trafo = findGlobalTransformationMultithreaded(ch1, ch2,nbrIter, toleratedError);
		//double[][] trafo = {{0.9989659705798773, -0.00152716543305873, 100.09632875943628},{-4.850730075698712E-4, 0.9986236679388222, -51.39384826518874}};
		
		
		//double[][] trafo = {{0.9988,-0.0013,65.9763},{-0.0004,0.9988,-7.46}};
		StormData combinedSet = doUnmixingMultiThreaded(ch1,ch2,trafo, useAll,tag,dist, minInt,savePairedPoints);
		combinedSet.setFname(ch1.getFname());
		combinedSet.setPath(ch1.getPath());
		//StormData combinedSet = doUnmixing(ch1, ch2, trafo);
		return combinedSet;
	}
	
	static StormData doUnmixingMultiThreaded(StormData untransformedCh1, StormData ch2, double[][] trafo, 
			boolean useAll,String tag, double dist, double minInt, boolean savePairedPoints){
		StormData ch1 = TransformationControl.applyTrafo(trafo, untransformedCh1);
//		double dist = 60; //in nm //this variable determines within which distance for matching points are searched
//		double minInt = 500; // minimal intensity of at least one channel
		if (verbose) {
			System.out.println("start unmixing...");
			System.out.println("maximal tolerance for matching points: "+dist);
			System.out.println("minimal intensity for matching points: "+minInt);
		}
		ArrayList<ArrayList<Double>> demixingProperties = new ArrayList<ArrayList<Double>>(); //matrix contains mean xyz coordinates and difference in xy and 
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
					currFrame, dist, minInt,pb, useAll, untransformedCh1));
			executor2.execute(t);
		}
		executor2.shutdown();
		try {
			executor2.awaitTermination(Long.MAX_VALUE, TimeUnit.NANOSECONDS);
		} catch (InterruptedException e) {
		
		}
		if (savePairedPoints){
			OutputClass.writeDemixingOutput(ch1.getPath(), ch1.getBasename(), demixingData.getCh1(), demixingData.getCh2(), demixingData.getUntransformedCh1(), ch1.getProcessingLog()+tag);
		}
		DemixingResultLog rl = new DemixingResultLog(demixingData.getCh1().size(), demixingData.getCh2().size(), useAll);
		ch1.addToLog(rl);
		if (true) {
			System.out.println("unmixing done.");
			System.out.println("Number of matches: "+ demixingData.getCh1().size()+" of "+ coloredSet.getSize()+" points.");
		}
		return coloredSet;
	}
	
	static double[][] findGlobalTransformationMultithreaded(StormData ch1, StormData ch2, int nbrIter, 
			double toleratedError){
//		int nbrIter = 5000;
//		double toleratedError = 60;
		ArrayList<ArrayList<ArrayList<StormLocalization>>> collectionOfGoodPoints = new ArrayList<ArrayList<ArrayList<StormLocalization>>>();
		ArrayList<Integer> listOfMatchingPoints = new ArrayList<Integer>();
		ArrayList<Double> listOfErrors = new ArrayList<Double>();
		ArrayList<Integer> frames = new ArrayList<Integer>();//(Arrays.asList(1,10,15,20,25,30,35,50));//,75,100,200,300,1000,10000,15000,20000,25000));//,3,4,5,6,7,8,9,10,20,90,1000,4000));
		ArrayList<Double>dims = ch1.getDimensions();
		for (int i = 1; i<10;i++){
			frames.add((int)Math.floor((dims.get(7)+dims.get(6))/2)+i);
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
		
		int counter = 0;
		for (int frame : frames) {
			counter +=1;
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
		if (collectionOfGoodPoints.size()>4){
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
		else{
			return noTransMat;
		}
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
			if (subsets.size()<2){
				
			}
			else{
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
	private StormData untransformedCh1;
	int currFrame;
	double dist;
	double minInt;
	Progressbar pb;
	boolean useAll;
	
	public UnmixFrame(DemixingData demixingData ,StormData coloredSet,StormData ch1, 
			StormData ch2, int currFrame, double dist, double minInt, Progressbar pb, boolean useAll, StormData untransformedCh1){
		this.demixingData = demixingData;
		this.coloredSet = coloredSet;
		this.ch1 = ch1;
		this.ch2 = ch2;
		this.untransformedCh1 = untransformedCh1;
		this.currFrame = currFrame;
		this.dist = dist;
		this.minInt = minInt;
		this.pb = pb;
		this.useAll = useAll;
	}
	
	public void run(){
		//System.out.println(currFrame);
		StormData currFrameCh1 = ch1.findSubset(currFrame, currFrame);
		StormData currFrameCh2 = ch2.findSubset(currFrame, currFrame);
		StormData currFrameUntransformedCh1 = untransformedCh1.findSubset(currFrame,currFrame);
		currFrameCh1.sortX();
		currFrameCh2.sortX();
		currFrameUntransformedCh1.sortX();
		for (int i = 0; i<currFrameCh1.getSize(); i++){
			//Demixing.setProgress("demixing",(int)((double)i/(double)currFrameCh1.getSize()*50.+50));
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
							double int0 = thisLoc.getIntensity();
							double int1 = currLoc.getIntensity();
							double sumInt = int0+int1;
							coloredSet.addElement(new StormLocalization((thisLoc.getX()),
																		(thisLoc.getY()),
																		(thisLoc.getZ()), 
																		currFrame,
																		(thisLoc.getIntensity()+currLoc.getIntensity())/2, 
																		atan));}
						synchronized(demixingData){
						demixingData.addElements(currLoc, thisLoc, currFrameUntransformedCh1.getElement(i));}
						//matchingCounter = matchingCounter + 1;
					}
					break;
				}
			}
			if (!matching){
				if (currLoc.getIntensity()>minInt && useAll){
					synchronized(coloredSet){
						coloredSet.addElement(new StormLocalization(currLoc,0));
					}
				}
			}
		}
		//second run to add not matching points from channel 2, this is only necessary if useAll is set to true
		if (useAll){
			for (int i = 0; i<currFrameCh2.getSize(); i++){
				StormLocalization currLoc = currFrameCh2.getElement(i); 
				boolean matching = false;
				int startvalue = 0; //if a match was found at the 20 th. position of the second channel, all following matches will lie after that
				for (int j = startvalue; j<currFrameCh1.getSize(); j++){
					StormLocalization thisLoc = currFrameCh1.getElement(j);
					//System.out.println("thisLoc.getX() "+thisLoc.getX()+" "+"currLoc.getX() "+currLoc.getX()+"thisLoc.getY() "+thisLoc.getY()+" "+"currLoc.getY() "+currLoc.getY());
					if (thisLoc.getX()>currLoc.getX()+dist){ //sorted for X, if thislocs x value is larger all following will be larger
						break;
					}
					
					if (Math.abs(thisLoc.getX() - currLoc.getX())<dist && Math.abs(thisLoc.getY() - currLoc.getY())<dist){
						matching = true;
						startvalue = i;
						
						break;
					}
				}
				if (!matching){
					if (currLoc.getIntensity()>minInt && useAll){
						synchronized(coloredSet){
							coloredSet.addElement(new StormLocalization(currLoc,Math.PI/2));
						}
					}
				}
			}
		}
		pb.updateProgress();
	}
}

class DemixingData{
	private ArrayList<StormLocalization> listCh1 = new ArrayList<StormLocalization>();
	private ArrayList<StormLocalization> listCh2 = new ArrayList<StormLocalization>();
	private ArrayList<StormLocalization> listUntransformedCh2 = new ArrayList<StormLocalization>();
	synchronized void addElements(StormLocalization slch1,StormLocalization slch2, StormLocalization slutch1){
		listCh1.add(slch1);
		listCh2.add(slch2);
		listUntransformedCh2.add(slutch1);
	}
	public ArrayList<StormLocalization> getCh1(){
		return listCh1;
	}
	public ArrayList<StormLocalization> getCh2(){
		return listCh2;
	}
	public ArrayList<StormLocalization> getUntransformedCh1(){
		return listUntransformedCh2;
	}

}
 
