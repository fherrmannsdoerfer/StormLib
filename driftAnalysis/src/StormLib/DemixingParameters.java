package StormLib;

public class DemixingParameters {
	private double angle1;
	private double angle2;
	private double width1;
	private double width2;
	public DemixingParameters(double angle1, double angle2, double width1, double width2){
		this.angle1 = angle1;
		this.angle2 = angle2;
		this.width1 = width1;
		this.width2 = width2;
	}
	public double getAngle1(){
		return angle1;
	}
	public double getAngle2(){
		return angle2;
	}
	public double getWidth1(){
		return width1;
	}
	public double getWidth2(){
		return width2;
	}
}