package StormLib;

public class Progressbar {
	private double minVal;
	private double maxVal;
	private double currVal;
	private int numberMarkersPrinted = 0; //number of currently printed markers
	private int parts = 120;
	private boolean endReached = false;
	private int internalCounter = 0;
	
	public Progressbar(double minVal, double maxVal, double startVal, String title){
		this.minVal = minVal;
		this.maxVal = maxVal;
		this.currVal = startVal;
		System.out.println(title);
		makeScale();
		System.out.print("[");
	}
	
	private void makeScale(){
		System.out.print("[");
		for (int i = 0; i<parts; i++){
			if (i == 0){
				System.out.print("0");
			}
			if (i == (int)Math.floor(parts/2)){
				System.out.print("50");
				i = i +1;
			}
			if (i == parts -4){
				System.out.print("100");
			}
			else{
				System.out.print(" ");
			}
		}
		System.out.println("");
	}
	
	public synchronized void updateProgress(double currVal){
		this.currVal = currVal;
		if (endReached) {
			
		}
		else {
			updateBar();
		}
		
	}
	public synchronized void updateProgress(){
		internalCounter += 1;
		updateProgress(internalCounter);
	}
	private void updateBar(){
		int targetNumber = (int) Math.ceil(currVal/(maxVal-minVal)*parts);
		int toPrintYet = targetNumber - numberMarkersPrinted;
		for (int i = 0;i<toPrintYet;i++){
			System.out.print("=");
		}
		numberMarkersPrinted = targetNumber;
		if (numberMarkersPrinted == parts){
			System.out.println("]");
			endReached = true;
		}
	}
}