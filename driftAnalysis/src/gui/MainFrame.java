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
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import functionDefinitions.CropGUI;
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
import javax.swing.filechooser.FileNameExtensionFilter;

public class MainFrame extends JFrame implements Serializable{
	private final ArrayList<ProcessingStepsPanel> listProcessingStepPanels = new ArrayList<ProcessingStepsPanel>();
	Controler controlerReference;
	JPanel panel;
	JPanel optionPanel;
	static MainFrame mf;
	private static DataFlavor dragAndDropPanelDataFlavor = null;
	private JComboBox preselectionComboBox;
	private JComboBox inputComboBox;
	private JComboBox outputComboBox;
	private JComboBox processingComboBox;
	transient ActionListener outputActionListener;
	private final ArrayList<String> optionsPreselectedTasksComboBoxAuto = new ArrayList<String>();
	File folder = new File(System.getProperty("user.home")+"//PostProcessingSoftware"); //Folder of savedPresettings
	private final ArrayList<ProcessingStepsPanel> outputComboBoxOptions = new ArrayList<ProcessingStepsPanel>();
	private final ArrayList<ProcessingStepsPanel> inputComboBoxOptions = new ArrayList<ProcessingStepsPanel>();
	private final ArrayList<ProcessingStepsPanel> processingComboBoxOptions = new ArrayList<ProcessingStepsPanel>();

	public MainFrame(final Controler controler) {
		folder.mkdir();
		optionPanel = new JPanel();
		final JFileChooser settingsFileChooserLoad = new JFileChooser(folder);
		final JFileChooser settingsFileChooserSave = new JFileChooser(folder);
		JPanel accessorySave = new JPanel();
		final JCheckBox saveAsShortcut = new JCheckBox("<html>Additionally, save as Shortcut (.default) in<br>user/home/PostProcessingSoftware/");
		accessorySave.setLayout(new BorderLayout());
		accessorySave.add(saveAsShortcut, BorderLayout.SOUTH);
		settingsFileChooserSave.setAccessory(accessorySave);
		
		final FileNameExtensionFilter filterSave = new FileNameExtensionFilter("Setting Files (.settings) and Shortcut Files (.default; show only)", "settings", "default");
		final FileNameExtensionFilter filterLoad = new FileNameExtensionFilter("Setting Files (.settings) and Shortcut Files (.default)", "settings", "default");
		final FileNameExtensionFilter filterSave2 = new FileNameExtensionFilter("Setting Files (.settings)", "settings");
		final FileNameExtensionFilter filterLoad2 = new FileNameExtensionFilter("Shortcut Files (.default)", "default");
		settingsFileChooserSave.setFileFilter(filterSave);
		settingsFileChooserSave.setFileFilter(filterSave2);
		settingsFileChooserLoad.setFileFilter(filterSave2);
		settingsFileChooserLoad.setFileFilter(filterLoad2);
		settingsFileChooserLoad.setFileFilter(filterLoad);
		settingsFileChooserLoad.setAcceptAllFileFilterUsed(false);
		settingsFileChooserSave.setAcceptAllFileFilterUsed(false);
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

		//////////////////////////////////////////////////////////////////// set options to choose from for drop-down menus; creates empty GUI class objects with member name

		outputComboBoxOptions.add(new RenderImage2DGUI());
		outputComboBoxOptions.add(new RenderImage3DGUI());
		
		inputComboBoxOptions.add(new SingleFileInputGUI());
		inputComboBoxOptions.add(new DualChannelSingleFileInputGUI());
		inputComboBoxOptions.add(new MultipleFileInputGUI());
		inputComboBoxOptions.add(new DualColorMultipleFileInputGUI());
		
		processingComboBoxOptions.add(new DriftcorrectionGUI());
		processingComboBoxOptions.add(new MergePointsGUI());
		processingComboBoxOptions.add(new DemixingGUI());
//		processingComboBoxOptions.add(new CropGUI());
		
		///////////////////////////////////////////////////////////////////
		
			
		Box verticalBox_1 = Box.createVerticalBox();
		horizontalBox.add(verticalBox_1);
		
		Box horizontalBox_5 = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox_5);
		
		JLabel lblNewLabel_3 = new JLabel("Preselected tasks");
		horizontalBox_5.add(lblNewLabel_3);
		
		Component horizontalGlue_4 = Box.createHorizontalGlue();
		horizontalBox_5.add(horizontalGlue_4);
			
		preselectionComboBox = new JComboBox(optionsPreselectedTasksComboBoxAuto.toArray());
		preselectionComboBox.setMaximumSize(new Dimension(32767, 22));
		verticalBox_1.add(preselectionComboBox);
		setupPreselectedTasks();
		preselectionComboBox.addActionListener(outputActionListener);
		
		Component verticalStrut_2 = Box.createVerticalStrut(20);
		verticalBox_1.add(verticalStrut_2);
		
		Box verticalBox_2 = Box.createVerticalBox();
		verticalBox_2.setBorder(new TitledBorder(null, "Modules", TitledBorder.LEADING, TitledBorder.TOP, null, null));
		verticalBox_1.add(verticalBox_2);
		
				
		Box horizontalBox_3 = Box.createHorizontalBox();
		verticalBox_2.add(horizontalBox_3);
		
		JLabel lblNewLabel_2 = new JLabel("Input");
		horizontalBox_3.add(lblNewLabel_2);
		lblNewLabel_2.setAlignmentY(Component.TOP_ALIGNMENT);
		
		
		String[] optionsInputComboBox = new String[inputComboBoxOptions.size()];		
		for (int i = 0; i < inputComboBoxOptions.size(); i++){
			optionsInputComboBox[i] = inputComboBoxOptions.get(i).getFunctionName();}	
		inputComboBox = new JComboBox(optionsInputComboBox);
		inputComboBox.addActionListener(outputActionListener);
		
		Component horizontalGlue_2 = Box.createHorizontalGlue();
		horizontalBox_3.add(horizontalGlue_2);
		verticalBox_2.add(inputComboBox);
		inputComboBox.setMaximumSize(new Dimension(32767, 22));
		
		Component verticalStrut = Box.createVerticalStrut(20);
		verticalBox_2.add(verticalStrut);
		
		Box horizontalBox_2 = Box.createHorizontalBox();
		verticalBox_2.add(horizontalBox_2);
		
		JLabel lblNewLabel_1 = new JLabel("Processing");
		horizontalBox_2.add(lblNewLabel_1);
		
		Component horizontalGlue_1 = Box.createHorizontalGlue();
		horizontalBox_2.add(horizontalGlue_1);
		
		String[] optionsProcessingComboBox = new String[processingComboBoxOptions.size()];		
		for (int i = 0; i < processingComboBoxOptions.size(); i++){
			optionsProcessingComboBox[i] = processingComboBoxOptions.get(i).getFunctionName();}		
		
		processingComboBox = new JComboBox(optionsProcessingComboBox);
		processingComboBox.addActionListener(outputActionListener);
		verticalBox_2.add(processingComboBox);
		processingComboBox.setMaximumSize(new Dimension(32767, 22));
		
		Component verticalStrut_1 = Box.createVerticalStrut(20);
		verticalBox_2.add(verticalStrut_1);
		
		Box horizontalBox_1 = Box.createHorizontalBox();
		
		
		JLabel lblNewLabel = new JLabel("Output");
		lblNewLabel.setAlignmentX(Component.CENTER_ALIGNMENT);
		horizontalBox_1.add(lblNewLabel);
		
		Component horizontalGlue = Box.createHorizontalGlue();
		horizontalBox_1.add(horizontalGlue);
		verticalBox_2.add(horizontalBox_1);
		String[] optionsOutputComboBox = new String[outputComboBoxOptions.size()];
		
		for (int i = 0; i < outputComboBoxOptions.size(); i++){
			optionsOutputComboBox[i] = outputComboBoxOptions.get(i).getFunctionName();}
		
		
		outputComboBox = new JComboBox(optionsOutputComboBox);
		outputComboBox.addActionListener(outputActionListener);
		verticalBox_2.add(outputComboBox);
		outputComboBox.setMaximumSize(new Dimension(32767, 22));
		
		Component verticalGlue = Box.createVerticalGlue();
		verticalBox_1.add(verticalGlue);
		
		Box horizontalBox_4 = Box.createHorizontalBox();
		verticalBox_1.add(horizontalBox_4);
		
		Box verticalBox_3 = Box.createVerticalBox();
		horizontalBox_4.add(verticalBox_3);
		
		JButton loadSettingsButton = new JButton("Load Settings");
		verticalBox_3.add(loadSettingsButton);
		
		
		JButton saveSettingsButton = new JButton("Save Settings");
		verticalBox_3.add(saveSettingsButton);
		
		JButton runButton = new JButton("Start Processing");
				verticalBox_3.add(runButton);
				
				Box verticalBox_4 = Box.createVerticalBox();
				horizontalBox_4.add(verticalBox_4);
				
				JButton loadDefaultSettingsButton = new JButton("Load Default Settings");
				verticalBox_4.add(loadDefaultSettingsButton);
				
				JButton saveDefaultSettingsButton = new JButton("Save Default Settings");
				verticalBox_4.add(saveDefaultSettingsButton);
				
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
				
				runButton.addActionListener(new ActionListener(){
					@Override
					public void actionPerformed(ActionEvent e) {
						Thread t = new Thread(){
							@Override
							public 
							void run(){
								controler.resetProgressBar(getListProcessingStepPanels());
								controler.startProcessing(getListProcessingStepPanels());
								/// reset progress bar after 1000 ms///
								try {
								    Thread.sleep(1000);
								} catch(InterruptedException ex) {
								    Thread.currentThread().interrupt();
								}
								controler.resetProgressBar(getListProcessingStepPanels());
								/// progress bar reset after 1000 ms///
							}
						};
						t.start();
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
							if(!settingsPath.endsWith(".settings")){
								if (settingsPath.endsWith(".default")){
									settingsPath = settingsPath.substring(0, settingsPath.length() - 8);
								}
								settingsPath += ".settings";
							}
							try {
								FileOutputStream fileOut = new FileOutputStream(settingsPath);
								ObjectOutputStream out = new ObjectOutputStream(fileOut);
								out.writeObject(listProcessingStepPanels);
								out.close();
								fileOut.close();
								System.out.println("saved settings");	
//								setupPreselectedTasks();
							} catch (FileNotFoundException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IOException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}
							if(saveAsShortcut.isSelected()){
								settingsPath = folder +"\\" + settingsFileChooserSave.getSelectedFile().getName();
								if (settingsPath.endsWith(".settings")){
									settingsPath = settingsPath.substring(0, settingsPath.length() - 9);
								}
								if (!settingsPath.endsWith(".default")){
									settingsPath += ".default";
								}
								try {
									FileOutputStream fileOut = new FileOutputStream(settingsPath);
									ObjectOutputStream out = new ObjectOutputStream(fileOut);
									out.writeObject(listProcessingStepPanels);
									out.close();
									fileOut.close();
									System.out.println("saved default");
									preselectionComboBox.removeActionListener(outputActionListener);
									setupPreselectedTasks();
									preselectionComboBox.addActionListener(outputActionListener);
								} catch (FileNotFoundException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								} catch (IOException e1) {
									// TODO Auto-generated catch block
									e1.printStackTrace();
								}
							}							
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
							if(settingsPath.endsWith(".settings") || settingsPath.endsWith(".default")){
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
					}
				});
		
		
		
		
		horizontalBox.add(panel);
		panel.setLayout(new BoxLayout(panel,BoxLayout.Y_AXIS));
		JScrollPane scrollPane = new JScrollPane(panel);
		horizontalBox.add(scrollPane);		
		
		
		optionPanel.setPreferredSize(new Dimension(500, 10));
		horizontalBox.add(optionPanel);
		
	}
		
	private void setupPreselectedTasks() {
		File[] listOfFiles = folder.listFiles();	
		preselectionComboBox.removeAllItems();
		optionsPreselectedTasksComboBoxAuto.clear();
	    for (int i = 0; i < listOfFiles.length; i++) {
		if (listOfFiles[i].isFile()) {
			if(listOfFiles[i].getName().endsWith(".default")){
				optionsPreselectedTasksComboBoxAuto.add(listOfFiles[i].getName());
				preselectionComboBox.addItem(listOfFiles[i].getName());
			}
		} 
	    }
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
	//			System.out.println(outputComboBoxOptions.get(thisBox.getSelectedIndex()).getFunctionName());
				panelToAdd = outputComboBoxOptions.get(thisBox.getSelectedIndex()).getFunction(mf);
			}
		if (thisBox == inputComboBox){
	//			System.out.println(inputComboBoxOptions.get(thisBox.getSelectedIndex()).getFunctionName());
				panelToAdd = inputComboBoxOptions.get(thisBox.getSelectedIndex()).getFunction(mf);
			}
		if (thisBox == processingComboBox){
	//			System.out.println(processingComboBoxOptions.get(thisBox.getSelectedIndex()).getFunctionName());
				panelToAdd = processingComboBoxOptions.get(thisBox.getSelectedIndex()).getFunction(mf);
			}
		
		if (thisBox == preselectionComboBox && thisBox.getSelectedIndex()>=0){
			ArrayList<ProcessingStepsPanel> tempOrderListSettings = new ArrayList<ProcessingStepsPanel>();
			String settingsPath = folder + "\\"+ optionsPreselectedTasksComboBoxAuto.toArray()[thisBox.getSelectedIndex()];
			try {
				FileInputStream fileInDefault = new FileInputStream(settingsPath);
				ObjectInputStream inPreselection = new ObjectInputStream(fileInDefault);
				tempOrderListSettings = (ArrayList<ProcessingStepsPanel>) inPreselection.readObject();
				inPreselection.close();
				fileInDefault.close();
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
//				case 3:
//					panelToAdd = new CropGUI(mf);
//					break;
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



