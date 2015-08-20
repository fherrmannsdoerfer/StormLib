package comperators;

import java.util.Comparator;

import dataStructure.StormLocalization;

public class StormLocalizationYComperator implements Comparator<StormLocalization> {
	@Override
	public int compare(StormLocalization loc1, StormLocalization loc2) {
		if (loc1.getY()<loc2.getY()){
			return -1;
		}
		else if (loc1.getY()>loc2.getY()){
			return 1;
		}
		else {return 0;}
		
	}

}
