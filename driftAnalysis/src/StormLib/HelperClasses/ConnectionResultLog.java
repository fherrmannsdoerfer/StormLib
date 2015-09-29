package StormLib.HelperClasses;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ConnectionResultLog extends BasicProcessingInformation {

	public ConnectionResultLog(String path, String basename, int size,
			int size2, String processingLog) {
		setNameOfProcessing("Connecting localizations.");
		addParam("Number of localizations after connection:", size);
		addParam("Number of localizations before connection:", size2);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toLatexString() {
		String retString = "\\begin{minipage}{\\linewidth}\n"+paramsToString()+"\\end{minipage}\n";
		return retString;
	}

}
