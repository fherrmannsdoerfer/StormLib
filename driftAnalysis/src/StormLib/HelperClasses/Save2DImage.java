package StormLib.HelperClasses;

import StormLib.OutputClass;
import ij.ImagePlus;

public class Save2DImage extends BasicProcessingInformation {

	public Save2DImage(String path, String basename, String tag,
			ImagePlus imgP, double pixelsize) {
		setNameOfProcessing("Save 2D image");
		addParam("Image tag:",tag);
		addParam("Pixelsize:",pixelsize+" nm");
		addParam("Resolution (width * height):",imgP.getWidth()+" * "+imgP.getHeight());
		String fullPath = OutputClass.save2DImage(path, basename, tag, imgP, pixelsize);
		addGraph("2D image of "+ this.breakName(basename)+ "with tag:"+tag, fullPath);
		// TODO Auto-generated constructor stub
	}

	@Override
	public String toLatexString() {
		// TODO Auto-generated method stub
		String retString = "\\begin{minipage}{\\linewidth}\n"+paramsToString()+"\\end{minipage}\n";
		return retString;
	}

}
