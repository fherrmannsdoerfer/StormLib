package StormLib.HelperClasses;

class Parameter<T>{
	private String name;
	private T value;
	public Parameter(String name, T value){
		this.name = name;
		this.value = value;
	}
	public String getName(){
		return name;
	}
	public T getValue(){
		return value;
	}
	
	public String toString(){
		return name +" "+value+"\\newline";
	}
}
