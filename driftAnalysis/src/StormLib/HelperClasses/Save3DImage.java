package StormLib.HelperClasses;

import java.io.Serializable;
import java.util.ArrayList;

import ij.ImagePlus;
import StormLib.OutputClass;

public class Save3DImage extends BasicProcessingInformation implements Serializable{
	
	public Save3DImage(String path, String basename, String tag,
			ArrayList<ImagePlus> colImg, double pixelsize, int mode) {
		setNameOfProcessing("Save 3D image");
		addParam("Image tag:",tag);
		addParam("Pixelsize:",pixelsize+" nm");
		addParam("Resolution (width * height):",colImg.get(0).getWidth()+" * "+colImg.get(0).getHeight());
		String fullPath = OutputClass.save3DImage(path, basename, tag, colImg,mode);
		addGraph("3D image of "+ this.breakName(basename), fullPath);
	}
	@Override
	public String toLatexString() {
		// TODO Auto-generated method stub
		String retString = "\\begin{minipage}{\\linewidth}\n"+paramsToString()+"\\end{minipage}\n";
		return retString;
	}

}
