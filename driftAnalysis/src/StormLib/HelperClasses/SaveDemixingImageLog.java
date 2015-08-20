package StormLib.HelperClasses;

import java.util.ArrayList;

import dataStructure.DemixingParameters;
import ij.ImagePlus;
import StormLib.OutputClass;

public class SaveDemixingImageLog extends BasicProcessingInformation {
	public SaveDemixingImageLog(String path, String basename, String tag,
			ArrayList<ImagePlus> colImg,DemixingParameters params, double pixelsize) {
		setNameOfProcessing("Save demixing image");
		addParam("Image tag:",tag);
		addParam("Pixelsize:",pixelsize);
		addParam("Resolution (width * height):",colImg.get(0).getWidth()+" * "+colImg.get(0).getHeight());
		String fullPath = OutputClass.saveDemixingImage(path, basename, tag,
				colImg);
		addGraph("Demixing image of:"+ this.breakName(basename), fullPath);
		// TODO Auto-generated constructor stub
	}
	@Override
	public String toLatexString() {
		// TODO Auto-generated method stub
		String retString = "\\begin{minipage}{\\linewidth}\n"+paramsToString()+"\\end{minipage}\n";
		return retString;
	}

}
