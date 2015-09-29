package functions;

import java.awt.Color;
import java.awt.Font;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

import javax.imageio.ImageIO;
import javax.swing.JFrame;

import ij.ImagePlus;
import ij.process.ImageProcessor;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartFrame;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.ChartRenderingInfo;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.CategoryAxis;
import org.jfree.chart.axis.ValueAxis;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.chart.title.LegendTitle;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

public class CreateScatterPlot{
	public static void createScatterPlotSingle(ArrayList<Double> x, ArrayList<Double> y,String datalabel,String xlabel, String ylabel, String title, String fname){
		if (x.size() != y.size()){
			System.out.println("x and y list must be of same size!");
			return;
		}
		XYSeriesCollection  dataset = new XYSeriesCollection();
		XYSeries series = new XYSeries(datalabel);
		for (int i = 0; i<x.size(); i++){
			series.add(x.get(i), y.get(i));
		}
		dataset.addSeries(series);
		JFreeChart chart = ChartFactory.createScatterPlot(title, xlabel, ylabel, dataset);
		Font font = new Font("Dialog", Font.PLAIN, 30);
		Font font2 = new Font("Dialog", Font.PLAIN, 15);
		XYPlot plot = chart.getXYPlot();
		plot.getDomainAxis().setLabelFont(font);
		plot.getRangeAxis().setLabelFont(font);
		plot.getDomainAxis().setTickLabelFont(font);
		plot.getRangeAxis().setTickLabelFont(font);
		plot.getRenderer().setBaseItemLabelFont(font);
		LegendTitle legend = new LegendTitle(plot.getRenderer());
		legend.setItemFont(font);
		ChartRenderingInfo info = new ChartRenderingInfo();
		chart.addLegend(legend);
		//ChartRenderingInfo info = new ChartRenderingInfo();
		BufferedImage img = chart.createBufferedImage(1000, 1000, info);
		try {
			ImageIO.write(img, "png", new File(fname));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	public static void createScatterPlot2(ArrayList<ArrayList<ArrayList<Double>>> data, ArrayList<String> datalabels, String xlabel, String ylabel, String title, String fname){
		if (data.size() != datalabels.size()){
			System.out.println("number of datasets and datalabels has to match!");
			return;
		}
		XYSeriesCollection dataset1 = new XYSeriesCollection();
		JFreeChart chart2 = ChartFactory.createScatterPlot(title, xlabel, ylabel, dataset1);
		XYPlot plot = chart2.getXYPlot();
		ArrayList<Color> colors = new ArrayList<Color>();
		colors.add(Color.black);
		colors.add(Color.blue);
		colors.add(Color.green);
		colors.add(Color.magenta);
		colors.add(Color.orange);
		colors.add(Color.red);
		colors.add(Color.yellow);
		ArrayList<XYLineAndShapeRenderer> renderer = new ArrayList<XYLineAndShapeRenderer>();
		for (int i = 0; i<data.size(); i++){
			XYSeriesCollection  dataset = new XYSeriesCollection();
			XYSeries series = new XYSeries(datalabels.get(i));
			for (int j = 0;j<data.get(i).get(1).size();j++){
				series.add(data.get(i).get(0).get(j),data.get(i).get(1).get(j));
			}			
			dataset.addSeries(series);
			plot.setDataset(i, dataset);
			renderer.add(new XYLineAndShapeRenderer());
			plot.setRenderer(i,renderer.get(i));
			plot.getRendererForDataset(plot.getDataset(i)).setSeriesPaint(i,  colors.get(i));
			//plot.getRenderer().setSeriesPaint(0, colors.get(i));	

		}

		ValueAxis axisX = plot.getDomainAxis();
		ValueAxis axisY = plot.getRangeAxis();
		Font font = new Font("Dialog", Font.PLAIN, 30);
		Font font2 = new Font("Dialog", Font.PLAIN, 150);
		axisX.setLabelFont(font);
		axisY.setLabelFont(font);
		axisX.setTickLabelFont(font);
		axisY.setTickLabelFont(font);
		plot.getRenderer().setBaseItemLabelFont(font);
		LegendTitle legend = new LegendTitle(plot.getRenderer());
		legend.setItemFont(font);
		ChartRenderingInfo info = new ChartRenderingInfo();
		BufferedImage img = chart2.createBufferedImage(1000, 1000, info);
		try {
			ImageIO.write(img, "png", new File(fname));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
