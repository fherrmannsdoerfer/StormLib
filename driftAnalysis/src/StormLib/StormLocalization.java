package StormLib;

public class StormLocalization {
	private double x;
	private double y;
	private double z;
	private int frame;
	private double intensity;
	private double angle;
	
	StormLocalization(double x, double y, double z, int frame, double intensity){
		this.x = x;
		this.y = y;
		this.z = z;
		this.frame = frame;
		this.intensity = intensity;
		this.angle = 0;
	}
	StormLocalization(double x, double y, double z, int frame, double intensity, double angle){
		this.x = x;
		this.y = y;
		this.z = z;
		this.frame = frame;
		this.intensity = intensity;
		this.angle = angle;
	}
	StormLocalization(double x, double y, int frame, double intensity){
		this.x = x;
		this.y = y;
		this.z = 0;
		this.frame = frame;
		this.intensity = intensity;
		this.angle = 0;
	}
	StormLocalization(double x, double y, int frame, double intensity, double angle){
		this.x = x;
		this.y = y;
		this.z = 0;
		this.frame = frame;
		this.intensity = intensity;
		this.angle = angle;
	}
	StormLocalization(StormLocalization sl, double angle){
		this.x = sl.getX();
		this.y = sl.getY();
		this.z = sl.getZ();
		this.frame = sl.getFrame();
		this.intensity = sl.getIntensity();
		this.angle = angle;
	}
	
	double getX(){return this.x;}
	double getY(){return this.y;}
	double getZ(){return this.z;}
	int getFrame(){return this.frame;}
	void setFrame(int frame){this.frame = frame;}
	double getAngle(){return this.angle;}
	double getIntensity(){return this.intensity;}
	@Override public String toString(){
		String tmp = "X: "+this.x+" Y: "+this.y+" Z: "+this.z+" frame: "+this.frame+" intensity: "+this.intensity+" angle: "+this.angle;
		return tmp;
	}
	
	public String toPlainString() {
		String tmp = this.x+" "+this.y+" "+this.z+" "+this.frame+" "+this.intensity+" "+this.angle;
		return tmp;
	}
	public String toPlainVispString() {
		String tmp = this.x+" "+this.y+" "+this.z+" "+this.intensity+" "+this.frame;
		return tmp;
	}
}
