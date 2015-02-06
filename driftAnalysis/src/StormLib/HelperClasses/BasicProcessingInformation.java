package StormLib.HelperClasses;

import java.util.ArrayList;

public abstract class BasicProcessingInformation<T> {
	private String nameOfProcessing;
	private ArrayList<Parameter<T>> params = new ArrayList<Parameter<T>>();
	private ArrayList<Graph> graphs = new ArrayList<Graph>();
	
	public void addParam(String name, T value){
		Parameter<T> param = new Parameter<T>(name,value);
		params.add(param);
	}
	public void removeParam(int idx){
		if(idx<params.size())
		params.remove(idx);
	}
	public ArrayList<Parameter<T>> getParameters(){
		return params;
	}
	public void addGraph(String description, String fname){
		description = description.replace("_", "\\_");
		Graph graph = new Graph(description,fname);
		graphs.add(graph);
	}
	public void removeGraph(int idx){
		if(idx<graphs.size())
		graphs.remove(idx);
	}
	public ArrayList<Graph> getGraphs(){
		return graphs;
	}
	
	public boolean hasGraph(){
		if(graphs.size()>0){return true;}
		else{return false;}
	}
	
	public void setNameOfProcessing(String nameOfProcessing){
		this.nameOfProcessing = nameOfProcessing;
	}
	public String getNameOfProcessing(){
		return nameOfProcessing;
	}
	
	public String paramsToString(){
		String ret = "\\noindent\n";
		ret = ret+"{\\bf "+getNameOfProcessing()+"}\\newline\n";
		for(int i=0; i<params.size(); ++i){
			ret = ret + params.get(i).toString().replace("_", "\\_")+"\n";
		}
		if(hasGraph()){
			for(int i =0; i<getGraphs().size(); i++){
				ret = ret+"\\begin{figure}[H]\n";
				ret = ret+"\\begin{center}\n";
				String path = getGraphs().get(i).getFilename().replaceAll("\\\\", "/");
				ret = ret+"\\includegraphics[width=80mm]{"+path+"}\n";
				ret = ret+"\\caption{"+getGraphs().get(i).getDescription()+"}\n";
				ret = ret+"\\end{center}\n";
				ret = ret+"\\end{figure}\n";
			}
		}
		return ret;
	};
	
	public String breakName(String input){
		String output = "";
		String[] array = input.split(" ");
		for (int i = 0; i<array.length; i++){
			if (array[i].length()>60){
				output = output +array[i].substring(0, 60);
				output = output + "- " + array[i].substring(60);
			}
			else{
				output = output + array[i]+" ";
			}
		}
		return output;
	}
	
	public abstract String toLatexString();
}
