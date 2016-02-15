package gui;

import java.awt.Color;
import java.awt.Dimension;
import java.io.Serializable;

public class StyleClass implements Serializable{
	private Color colorInput = new Color(0,0,158);
	private Color colorProcessing = new Color(0,238,118);
	private Color colorOutput = new Color(255,185,15);
	private int widthProcessingStepsPanel = 350;
	private int heightProcessingStepsPanel = 75;
	private int leftIndent = 15;
	private int rightIndent = 15;
	private int upperIndent = 5;
	private int lowerIndent = 5;
	private Color removeButtonColor = new Color(176,23,31);
	private Dimension pathFields = new Dimension(400,22);
	private Dimension optionPaneWidth = new Dimension(450,999);
	
	public Color getColorInput() {
		return colorInput;
	}
	public void setColorInput(Color colorInput) {
		this.colorInput = colorInput;
	}
	public Color getColorProcessing() {
		return colorProcessing;
	}
	public void setColorProcessing(Color colorProcessing) {
		this.colorProcessing = colorProcessing;
	}
	public Color getColorOutput() {
		return colorOutput;
	}
	public void setColorOutput(Color colorOutput) {
		this.colorOutput = colorOutput;
	}
	public int getWidthProcessingStepsPanel() {
		return widthProcessingStepsPanel;
	}
	public void setWidthProcessingStepsPanel(int widthProcessingStepsPanel) {
		this.widthProcessingStepsPanel = widthProcessingStepsPanel;
	}
	public int getHeightProcessingStepsPanel() {
		return heightProcessingStepsPanel;
	}
	public void setHeightProcessingStepsPanel(int heightProcessingStepsPanel) {
		this.heightProcessingStepsPanel = heightProcessingStepsPanel;
	}
	public int getLeftIndent() {
		return leftIndent;
	}
	public void setLeftIndent(int leftIndent) {
		this.leftIndent = leftIndent;
	}
	public int getRightIndent() {
		return rightIndent;
	}
	public void setRightIndent(int rightIndent) {
		this.rightIndent = rightIndent;
	}
	public int getUpperIndent() {
		return upperIndent;
	}
	public void setUpperIndent(int upperIndent) {
		this.upperIndent = upperIndent;
	}
	public int getLowerIndent() {
		return lowerIndent;
	}
	public void setLowerIndent(int lowerIndent) {
		this.lowerIndent = lowerIndent;
	}
	public Color getRemoveButtonColor() {
		return removeButtonColor;
	}
	public void setRemoveButtonColor(Color removeButtonColor) {
		this.removeButtonColor = removeButtonColor;
	}
	public Dimension getDimensionPathFields() {
		return pathFields;
	}
	public void setDimensionPathFields(Dimension widthPathFields) {
		this.pathFields = widthPathFields;
	}
	public Dimension getDimensionOptionPane() {
		return optionPaneWidth;
	}
}
