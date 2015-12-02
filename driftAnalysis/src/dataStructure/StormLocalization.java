package dataStructure;

public class StormLocalization {
	private double x;
	

	private double y;
	private double z;
	private int frame;
	private double intensity;
	private double angle;
	
	public StormLocalization(double x, double y, double z, int frame, double intensity){
		this.x = x;
		this.y = y;
		this.z = z;
		this.frame = frame;
		this.intensity = intensity;
		this.angle = 0;
	}
	public StormLocalization(double x, double y, double z, int frame, double intensity, double angle){
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
	public StormLocalization(StormLocalization sl, double angle){
		this.x = sl.getX();
		this.y = sl.getY();
		this.z = sl.getZ();
		this.frame = sl.getFrame();
		this.intensity = sl.getIntensity();
		this.angle = angle;
	}
	
	public double getX(){return this.x;}
	public double getY(){return this.y;}
	public double getZ(){return this.z;}
	void setZ(double z){this.z = z;};
	public int getFrame(){return this.frame;}
	void setFrame(int frame){this.frame = frame;}
	public double getAngle(){return this.angle;}
	public double getIntensity(){return this.intensity;}
	public void setX(double x) {this.x = x;}
	public void setY(double y) {this.y = y;}
	public void setIntensity(double intensity) {this.intensity = intensity;}
	public void setAngle(double angle) {this.angle = angle;}
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
	
	public String toPlainFRCString(int mode){
		String tmp;
		switch (mode){
			case 1:
				tmp = this.x/106.66+" "+this.y/106.66+" "+this.frame;
				break;
			case 2:
				tmp = this.x/106.66+" "+this.z/106.66+" "+this.frame;
				break;
			case 3:
				tmp = this.y/106.66+" "+this.z/106.66+" "+this.frame;
				break;
			default :
				tmp = this.x/106.66+" "+this.y/106.66+" "+this.frame;
		}
		return tmp;
	}
}
