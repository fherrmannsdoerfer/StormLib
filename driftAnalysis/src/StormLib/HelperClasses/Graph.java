package StormLib.HelperClasses;

import java.io.Serializable;

public class Graph implements Serializable{
	private String description;
	private String filename;
	
	public Graph(String description, String filename){
		this.description = description;
		this.filename = filename;
	}
	public String getDescription(){
		return description;
	}
	public String getFilename(){
		return filename;
	}
}
