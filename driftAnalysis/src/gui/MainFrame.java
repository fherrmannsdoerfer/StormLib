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
import java.util.ArrayList;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JButton;
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

public class MainFrame extends JFrame{
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
	ActionListener outputActionListener;
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



