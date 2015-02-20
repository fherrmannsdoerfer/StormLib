package StormLib;

import java.util.Comparator;

public class StormLocalizationZComperator implements Comparator<StormLocalization> {
	@Override
	public int compare(StormLocalization loc1, StormLocalization loc2) {
		if (loc1.getZ()<loc2.getZ()){
			return -1;
		}
		else if (loc1.getZ()>loc2.getZ()){
			return 1;
		}
		else {return 0;}
		
	}

}
