package StormLib;

import java.util.Comparator;

public class StormLocalizationFrameComperator implements Comparator<StormLocalization> {
	@Override
	public int compare(StormLocalization loc1, StormLocalization loc2) {
		if (loc1.getFrame()<loc2.getFrame()){
			return -1;
		}
		else if (loc1.getFrame()>loc2.getFrame()){
			return 1;
		}
		else {return 0;}
		
	}

}