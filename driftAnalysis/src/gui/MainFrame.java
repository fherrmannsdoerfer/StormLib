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
	private final ArrayList<ProcessingStepsPanel> listProcessingStepPanels = new ArrayList<ProcessingStepsPanel>();
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
	String[] optionsPreselectedTasksComboBox = {"Single Channel Input", "Demixing"};
	String[] optionsInputComboBox = {"Single File Input", "Multiple File Input", "Dual Channel Single File Input", "Dual Channel Multiple File Input"};
	String[] optionsProcessingComboBox = {"Drift Correction", "Merging", "Demixing", "Cropping", "Multi Channel Alignment"};
	String[] optionsOutputComboBox = {"Render 2D Image", "Render 3D Image", "Processing Log File", "Visp File", "Localization File "};
	File folder = new File("C:\\Users\\bwpc\\git\\StormLib\\driftAnalysis\\preSetSettings");
	
	
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
		

		File[] listOfFiles = folder.listFiles();
		String[] optionsPreselectedTasksComboBoxAuto = new String[listOfFiles.length];
		
		    for (int i = 0; i < listOfFiles.length; i++) {
		      if (listOfFiles[i].isFile()) {
		        optionsPreselectedTasksComboBoxAuto[i] = (listOfFiles[i].getName());
		      } 
		    }
		
		preselectionComboBox = new JComboBox(optionsPreselectedTasksComboBoxAuto);
		preselectionComboBox.addActionListener(outputActionListener);
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
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox_1);
		
		Box verticalBox_3 = Box.createVerticalBox();
		horizontalBox_1.add(verticalBox_3);
		
		JButton loadSettingsButton = new JButton("Load Settings");
		verticalBox_3.add(loadSettingsButton);
		
		
		JButton saveSettingsButton = new JButton("Save Settings");
		verticalBox_3.add(saveSettingsButton);
		
		JButton runButton = new JButton("Start Processing");
		verticalBox_3.add(runButton);
		
		Box verticalBox_4 = Box.createVerticalBox();
		horizontalBox_1.add(verticalBox_4);
		
		JButton loadDefaultSettingsButton = new JButton("Load Default Settings");
		verticalBox_4.add(loadDefaultSettingsButton);
		
		JButton saveDefaultSettingsButton = new JButton("Save Default Settings");
		verticalBox_4.add(saveDefaultSettingsButton);
		
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
		
		final JFileChooser settingsFileChooserLoad = new JFileChooser();
		final JFileChooser settingsFileChooserSave = new JFileChooser();
		
		saveDefaultSettingsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				try {
					FileOutputStream fileOutDefault = new FileOutputStream("settings.default");
					ObjectOutputStream outDefault = new ObjectOutputStream(fileOutDefault);
					outDefault.writeObject(listProcessingStepPanels);
					outDefault.close();
					fileOutDefault.close();
					System.out.println("saved default");					
				} catch (FileNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				} catch (IOException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
			}
		});
		
		saveSettingsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("before saved");
				int saveVal = settingsFileChooserSave.showSaveDialog(null);
				System.out.println("saveSettingsButton");
				if (saveVal == JFileChooser.APPROVE_OPTION){
					File file = settingsFileChooserSave.getSelectedFile();
					String settingsPath = file.getAbsolutePath();
					try {
//						if (!settingsPath.endsWith(".ser")){return;}
						FileOutputStream fileOut = new FileOutputStream(settingsPath);
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
			}
		});
		
		loadDefaultSettingsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {				
				ArrayList<ProcessingStepsPanel> tempOrderList = new ArrayList<ProcessingStepsPanel>();
				String settingsPath = "settings.default";
				try {
					FileInputStream fileInDefault = new FileInputStream(settingsPath);
					ObjectInputStream inDefault = new ObjectInputStream(fileInDefault);
					tempOrderList = (ArrayList<ProcessingStepsPanel>) inDefault.readObject();
					inDefault.close();
					fileInDefault.close();
					System.out.println("loaded default");
					listProcessingStepPanels.clear();
					panel.removeAll();
					optionPanel.removeAll();				
					for (int i = 0; i < tempOrderList.size(); i++){						
						ProcessingStepsPanel tempObject = tempOrderList.get(i);
						listProcessingStepPanels.add(tempObject.getProcessingStepsPanelObject(tempObject, mf));
						listProcessingStepPanels.get(i).setSettings(tempOrderList.get(i).getSettings());
					}				
					updatePanels();
					revalidate();
					repaint();					
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
			}			
		});
		
		loadSettingsButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				System.out.println("before loaded");
				int returnVal = settingsFileChooserLoad.showOpenDialog(null);
				System.out.println("loadSettingsButton");
				if (returnVal == JFileChooser.APPROVE_OPTION){
					ArrayList<ProcessingStepsPanel> tempOrderList = new ArrayList<ProcessingStepsPanel>();
					String settingsPath = null;
					File file = settingsFileChooserLoad.getSelectedFile();
					settingsPath = file.getAbsolutePath();
					try {
						FileInputStream fileIn = new FileInputStream(settingsPath);
						ObjectInputStream in = new ObjectInputStream(fileIn);
						tempOrderList = (ArrayList<ProcessingStepsPanel>) in.readObject();
						in.close();
						fileIn.close();
						System.out.println("loaded");	
						listProcessingStepPanels.clear();
						panel.removeAll();
						optionPanel.removeAll();				
						for (int i = 0; i < tempOrderList.size(); i++){						
							ProcessingStepsPanel tempObject = tempOrderList.get(i);
							listProcessingStepPanels.add(tempObject.getProcessingStepsPanelObject(tempObject, mf));
							listProcessingStepPanels.get(i).setSettings(tempOrderList.get(i).getSettings());
						}
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
					updatePanels();
					revalidate();
					repaint();
				}
			}
		});
		
		
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
		
		if (thisBox == preselectionComboBox){
			File[] listOfFiles = folder.listFiles();
			String[] preselectionComboBoxSelection = new String[listOfFiles.length];
			
			    for (int i = 0; i < listOfFiles.length; i++) {
			      if (listOfFiles[i].isFile()) {
			    	  preselectionComboBoxSelection[i] = (listOfFiles[i].getName());
			      } 
			    }			
			ArrayList<ProcessingStepsPanel> tempOrderListSettings = new ArrayList<ProcessingStepsPanel>();
			String settingsPath = folder + "\\"+ preselectionComboBoxSelection[thisBox.getSelectedIndex()];
//			System.out.println(settingsPath);
			try {
				FileInputStream fileInDefault = new FileInputStream(settingsPath);
				ObjectInputStream inPreselection = new ObjectInputStream(fileInDefault);
				tempOrderListSettings = (ArrayList<ProcessingStepsPanel>) inPreselection.readObject();
				inPreselection.close();
				fileInDefault.close();
				System.out.println("loaded default");
				listProcessingStepPanels.clear();
				panel.removeAll();
				optionPanel.removeAll();				
				for (int i = 0; i < tempOrderListSettings.size(); i++){						
					ProcessingStepsPanel tempObject = tempOrderListSettings.get(i);
					listProcessingStepPanels.add(tempObject.getProcessingStepsPanelObject(tempObject, mf));
					listProcessingStepPanels.get(i).setSettings(tempOrderListSettings.get(i).getSettings());
				}				
				updatePanels();
				revalidate();
				repaint();					
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



