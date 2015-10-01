package gui;

import javax.swing.JFrame;
import javax.swing.Box;

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.datatransfer.DataFlavor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import functionDefinitions.DemixingGUI;
import functionDefinitions.DriftcorrectionGUI;
import functionDefinitions.DualChannelSingleFileInputGUI;
import functionDefinitions.DualColorMultipleFileInputGUI;
import functionDefinitions.MergePointsGUI;
import functionDefinitions.MultipleFileInputGUI;
import functionDefinitions.RenderImage2DGUI;
import functionDefinitions.RenderImage3DGUI;
import functionDefinitions.SingleFileInputGUI;

import javax.swing.JLabel;
import javax.swing.JComboBox;
import javax.swing.border.TitledBorder;

public class MainFrame extends JFrame implements Serializable{
//	private final ArrayList<ProcessingStepsPanel> listProcessingStepPanels = new ArrayList<ProcessingStepsPanel>();
	private ArrayList<ProcessingStepsPanel> listProcessingStepPanels = new ArrayList<ProcessingStepsPanel>();
	Controler controlerReference;
	JPanel panel;
	JPanel optionPanel;
	MainFrame mf;
	private static DataFlavor dragAndDropPanelDataFlavor = null;
	private JComboBox preselectionComboBox;
	private JComboBox inputComboBox;
	private JComboBox outputComboBox;
	private JComboBox processingComboBox;
	transient ActionListener outputActionListener;
	final JFileChooser settingsFileChooser = new JFileChooser();
	String[] optionsPreselectedTasksComboBox = {"Single Channel Input", "Demixing"};
	String[] optionsInputComboBox = {"Single File Input", "Multiple File Input", "Dual Channel Single File Input", "Dual Channel Multiple File Input"};
	String[] optionsProcessingComboBox = {"Drift Correction", "Merging", "Demixing", "Cropping", "Multi Channel Alignment"};
	String[] optionsOutputComboBox = {"Render 2D Image", "Render 3D Image", "Processing Log File", "Visp File", "Localization File "};
	
	
	public MainFrame(final Controler controler) {
		this.controlerReference = controler;
		outputActionListener = new OutputActionListener();
		this.setBounds(0,0,1200,800);
		getContentPane().setPreferredSize(new Dimension(1200, 0));
		setPreferredSize(new Dimension(1200, 0));
		mf = this;
		Box horizontalBox = Box.createHorizontalBox();
		horizontalBox.setPreferredSize(new Dimension(1200, 0));
		getContentPane().add(horizontalBox, BorderLayout.CENTER);
		panel = new RootPanel(this);
		panel.setMinimumSize(new Dimension(4, 2));
		
		Box verticalBox = Box.createVerticalBox();
			
		Box verticalBox_1 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_1);
		
		JLabel lblNewLabel_3 = new JLabel("Preselected tasks");
		verticalBox_1.add(lblNewLabel_3);
		
		preselectionComboBox = new JComboBox(optionsPreselectedTasksComboBox);
		preselectionComboBox.setMaximumSize(new Dimension(32767, 22));
		verticalBox_1.add(preselectionComboBox);
		
		Component verticalStrut_2 = Box.createVerticalStrut(20);
		verticalBox_1.add(verticalStrut_2);
		
		Box verticalBox_2 = Box.createVerticalBox();
		verticalBox_2.setBorder(new TitledBorder(null, "Modules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		verticalBox_1.add(verticalBox_2);
		
		JLabel lblNewLabel_2 = new JLabel("Input");
		lblNewLabel_2.setAlignmentY(Component.TOP_ALIGNMENT);
		verticalBox_2.add(lblNewLabel_2);
		
		inputComboBox = new JComboBox(optionsInputComboBox);
		inputComboBox.addActionListener(outputActionListener);
		verticalBox_2.add(inputComboBox);
		inputComboBox.setMaximumSize(new Dimension(32767, 22));
		
		Component verticalStrut = Box.createVerticalStrut(20);
		verticalBox_2.add(verticalStrut);
		
		JLabel lblNewLabel_1 = new JLabel("Processing");
		verticalBox_2.add(lblNewLabel_1);
		
		processingComboBox = new JComboBox(optionsProcessingComboBox);
		processingComboBox.addActionListener(outputActionListener);
		verticalBox_2.add(processingComboBox);
		processingComboBox.setMaximumSize(new Dimension(32767, 22));
		
		Component verticalStrut_1 = Box.createVerticalStrut(20);
		verticalBox_2.add(verticalStrut_1);
		
		JLabel lblNewLabel = new JLabel("Output");
		verticalBox_2.add(lblNewLabel);
		
		outputComboBox = new JComboBox(optionsOutputComboBox);
		outputComboBox.addActionListener(outputActionListener);
		verticalBox_2.add(outputComboBox);
		outputComboBox.setMaximumSize(new Dimension(32767, 22));
		
		Component verticalGlue = Box.createVerticalGlue();
		verticalBox_1.add(verticalGlue);
		
		JButton runButton = new JButton("Start Processing");
		runButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				Thread t = new Thread(){
					@Override
					public 
					void run(){
						controler.resetProgressBar(getListProcessingStepPanels());
						controler.startProcessing(getListProcessingStepPanels());
					}
				};
				t.start();
			}
			
		});
		
		JButton loadSettingsButton = new JButton("Load Settings");
		verticalBox_1.add(loadSettingsButton);
		loadSettingsButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				
				ArrayList<ProcessingStepsPanel> tempOrderList = new ArrayList<ProcessingStepsPanel>();
				String settingsPath = "settings.ser";
//				int returnVal = settingsFileChooser.showOpenDialog(null);
//
//				if (returnVal == JFileChooser.APPROVE_OPTION){
//					File file = settingsFileChooser.getSelectedFile();
//					settingsPath = file.getAbsolutePath();
//				}
				try {
					FileInputStream fileIn = new FileInputStream(settingsPath);
					ObjectInputStream in = new ObjectInputStream(fileIn);
					tempOrderList = (ArrayList<ProcessingStepsPanel>) in.readObject();
					in.close();
					fileIn.close();
					System.out.println("loaded");					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				listProcessingStepPanels.clear();
				panel.removeAll();
				optionPanel.removeAll();
				listProcessingStepPanels = tempOrderList;
//				updatePanels();
//				revalidate();
//				repaint();
//				for (int i = 0; i < tempOrderList.size(); i++){
//					Class<? extends ProcessingStepsPanel> c = tempOrderList.get(i).getClass();
//
//					((c) listProcessingStepPanels.get(i)).setRenderImage2DGUI(
//							String.valueOf(((RenderImage2DGUI) tempOrderList.get(i)).getPixelsize()),
//							((RenderImage2DGUI) tempOrderList.get(i)).getTag());
//				}
				
				for (int i = 0; i < tempOrderList.size(); i++){					
//					listProcessingStepPanels.get(i).initialize(mf);
					listProcessingStepPanels.get(i).setActionListener();
					listProcessingStepPanels.get(i).setSettings(tempOrderList.get(i).getSettings());
//					listProcessingStepPanels.add(new RenderImage2DGUI(mf));
//					listProcessingStepPanels.get(i)
				}
				
//				for (int i = 0; i < tempOrderList.size(); i++){
//					if (tempOrderList.get(i).getClass() == RenderImage2DGUI.class){
////						listProcessingStepPanels.add(tempOrderList.get(i));
////						listProcessingStepPanels.add(tempOrderList.get(i).copyTo());
////						((RenderImage2DGUI) listProcessingStepPanels).get(i).setRenderImage2DGUI(
////								String.valueOf(tempOrderList.get(i).getPixelsize()),
////								((RenderImage2DGUI) tempOrderList.get(i)).getTag());	
//					}
//					if (tempOrderList.get(i).getClass() == RenderImage3DGUI.class){
//						listProcessingStepPanels.add(new RenderImage3DGUI(mf));
//						
//					}
//					if (tempOrderList.get(i).getClass() == SingleFileInputGUI.class){
//						listProcessingStepPanels.add(new SingleFileInputGUI(mf));
//						
//					}
//					if (tempOrderList.get(i).getClass() == MultipleFileInputGUI.class){
//						listProcessingStepPanels.add(new MultipleFileInputGUI(mf));
//						
//					}
//					if (tempOrderList.get(i).getClass() == DemixingGUI.class){
//						listProcessingStepPanels.add(new DemixingGUI(mf));
//						
//					}
//					if (tempOrderList.get(i).getClass() == DriftcorrectionGUI.class){
//						listProcessingStepPanels.add(new DriftcorrectionGUI(mf));
//						
//					}
//					if (tempOrderList.get(i).getClass() == MergePointsGUI.class){
//						listProcessingStepPanels.add(new MergePointsGUI(mf));
//						
//					}
//					
//				}
				updatePanels();
				revalidate();
				repaint();
			}			
		});
		
		
		JButton saveSettingsButton = new JButton("Save Settings");
		saveSettingsButton.addActionListener(new ActionListener(){

			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileOutputStream fileOut = new FileOutputStream("settings.ser");
					ObjectOutputStream out = new ObjectOutputStream(fileOut);
					out.writeObject(listProcessingStepPanels);
					out.close();
					fileOut.close();
					System.out.println("saved");					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		verticalBox_1.add(saveSettingsButton);
		verticalBox_1.add(runButton);
		
		
		horizontalBox.add(panel);
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(panel);
		horizontalBox.add(scrollPane);		
		
		optionPanel = new JPanel();
		optionPanel.setPreferredSize(new Dimension(500, 10));
		horizontalBox.add(optionPanel);
		
	}
	
	public static DataFlavor getDragAndDropPanelDataFlavor() throws Exception {
        // Lazy load/create the flavor
        if (dragAndDropPanelDataFlavor == null) {
            dragAndDropPanelDataFlavor = new DataFlavor(DataFlavor.javaJVMLocalObjectMimeType + ";class="+ProcessingStepsPanel.class.getName());
        }

        return dragAndDropPanelDataFlavor;
    }
	
	
	protected ArrayList<ProcessingStepsPanel> getListProcessingStepPanels() {
        return listProcessingStepPanels;
    }

	public void updatePanels() {
		panel.removeAll();
		for (ProcessingStepsPanel p : listProcessingStepPanels){
			Component verticalStrut = Box.createVerticalStrut(20);
    		panel.add(verticalStrut);
			panel.add(p);
			
		}
		panel.repaint();
		panel.revalidate();
	}

	public void removePanel(ProcessingStepsPanel thisPanel) {
		listProcessingStepPanels.remove(thisPanel);
		updatePanels();
	}

	
	public JComboBox getPreselectionComboBox() {
		return preselectionComboBox;
	}
	public JComboBox getInputComboBox() {
		return inputComboBox;
	}
	public JComboBox getOutputComboBox() {
		return outputComboBox;
	}
	public JComboBox getProcessingComboBox() {
		return processingComboBox;
	}
	public void moduleAdded(ActionEvent e){
		JComboBox thisBox = (JComboBox)e.getSource();
		ProcessingStepsPanel panelToAdd = null;
		if (thisBox == outputComboBox){
			switch (thisBox.getSelectedIndex()){
				case 0:
					panelToAdd =new RenderImage2DGUI(mf);
					break;
				case 1:
					panelToAdd = new RenderImage3DGUI(mf);
					break;
				case 2:
					break;
			}
					
		}
		if (thisBox == inputComboBox){
			switch (thisBox.getSelectedIndex()){
				case 0:
					panelToAdd = new SingleFileInputGUI(mf);
					break;
				case 1:
					panelToAdd = new MultipleFileInputGUI(mf);
					break;
				case 2:
					panelToAdd = new DualChannelSingleFileInputGUI(mf);
					break;
				case 3:
					panelToAdd = new DualColorMultipleFileInputGUI(mf);
					break;
			}
		}
		if (thisBox == processingComboBox){
			switch (thisBox.getSelectedIndex()){
				case 0:
					panelToAdd = new DriftcorrectionGUI(mf);
					break;
				case 1:
					panelToAdd = new MergePointsGUI(mf);
					break;
				case 2:
					panelToAdd = new DemixingGUI(mf);
					break;
			}
		}
		if (panelToAdd !=null){
			listProcessingStepPanels.add(panelToAdd);
		}
		updatePanels();
	}
	
	class OutputActionListener implements ActionListener{
		
		@Override
		public void actionPerformed(ActionEvent e) {
			mf.moduleAdded(e);
		}
	}

	public void hideAllOptionPanels() {
		for (ProcessingStepsPanel psp: listProcessingStepPanels){
			psp.setVisibilityOptionPanel(false);
		}
	}
}



