package comperators;

import java.util.ArrayList;
import java.util.Comparator;

import dataStructure.StormLocalization;

public class TraceYComperator implements Comparator<ArrayList<StormLocalization>>{
	@Override
	public int compare(ArrayList<StormLocalization> loc1, ArrayList<StormLocalization> loc2) {
		if (loc1.get(0).getY()<loc2.get(0).getY()){
			return -1;
		}
		else if (loc1.get(0).getY()>loc2.get(0).getY()){
			return 1;
		}
		else {return 0;}
		
	}
}

