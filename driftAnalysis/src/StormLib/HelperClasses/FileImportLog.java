package StormLib.HelperClasses;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class FileImportLog extends BasicProcessingInformation {

	public FileImportLog(){}
	public FileImportLog(ArrayList<Integer> errorlines, int nbrLocs, String filename){
		setNameOfProcessing("File import.");
		addParam("Number of lines with error:", errorlines.size());
		String str = "";
		for (int i = 0; i<errorlines.size(); i++){
			str = str+", "+errorlines.get(i);
		}
		addParam("Filename:", this.breakName(filename));
		addParam("Lines containing errors:", str);
		addParam("Number of localizations:", nbrLocs);
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Date date = new Date();
		addParam("Date of last processing:",dateFormat.format(date));
	}
	
	@Override
	public String toLatexString() {
		String retString = "\\newpage\n "
				+ "\\begin{minipage}{\\linewidth}\n"+paramsToString()+"\\end{minipage}\n";
		return retString;
	}

}
