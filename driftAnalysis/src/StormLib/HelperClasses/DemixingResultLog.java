package StormLib.HelperClasses;

public class DemixingResultLog extends BasicProcessingInformation {

	public DemixingResultLog(int size, int size2, boolean useAll) {
		setNameOfProcessing("Demixing report.");
		addParam("Number of localizations found in both channels:", size);
		addParam("Total number of localizations added (localizations found in only one channel might be added):", size2);
		if (useAll){
			addParam("Used all points:", "true");
		}
		else{
			addParam("Used all points", "false");
		}
		
	}

	@Override
	public String toLatexString() {
		// TODO Auto-generated method stub
		String retString = "\\begin{minipage}{\\linewidth}\n"+paramsToString()+"\\end{minipage}\n";
		return retString;
	}

}
