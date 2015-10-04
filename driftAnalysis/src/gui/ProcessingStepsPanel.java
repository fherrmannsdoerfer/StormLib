package gui;

import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.IOException;

import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JProgressBar;

public class ProcessingStepsPanel extends JPanel implements Transferable{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private int id = 0;
	public JButton parameterButton;
	private MainFrame mf;
	private ProcessingStepsPanel thisPanel;
	private Color color;
	private boolean visibilityOptionPanel = false;
	private JPanel optionPanel;
	private JProgressBar progressbar;
	
	public ProcessingStepsPanel(final MainFrame mf){
		thisPanel = this;
		Box horizontalBox = Box.createHorizontalBox();
		Box verticalBox = Box.createVerticalBox();
		final Dimension d = new Dimension(300,50);
		verticalBox.setPreferredSize(d);
		verticalBox.add(horizontalBox);
		this.add(verticalBox);
		this.mf = mf;
		this.addMouseListener(new MyDraggableMouseListener());
		this.setTransferHandler(new DragAndDropTransferHandler());
		this.setBackground(Color.cyan);
		this.setMaximumSize(d);
		this.setPreferredSize(d);

		parameterButton = new JButton();
		parameterButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e){
				mf.hideAllOptionPanels();
				setVisibilityOptionPanel(true);
				mf.repaint();
			}
		});
				
		JButton removeButton = new JButton("X");
		removeButton.addActionListener(new ActionListener(){
			@Override
			public void actionPerformed(ActionEvent e) {
				mf.optionPanel.remove(optionPanel);
				mf.removePanel(thisPanel);
				mf.repaint();
			}
		});
		horizontalBox.add(parameterButton);
		Component hg = Box.createHorizontalGlue();
		horizontalBox.add(hg);
		horizontalBox.add(removeButton);
		
		Component verticalGlue = Box.createVerticalGlue();
		verticalBox.add(verticalGlue);
		
		progressbar = new JProgressBar(0,100);
		progressbar.setValue(0);
		progressbar.setPreferredSize(new Dimension(d.width,20));
		verticalBox.add(progressbar);
	}
	
	public void setParameterButtonsName(String name){
		parameterButton.setText(name);
	}
	
	public void setColor(Color color){
		this.color = color;
		this.setBackground(color);
	}
	
	@Override
	public DataFlavor[] getTransferDataFlavors() {
		DataFlavor[] flavors ={null};
		 try {
	            flavors[0] = MainFrame.getDragAndDropPanelDataFlavor();
	        } catch (Exception ex) {
	            ex.printStackTrace(System.err);
	            return null;
	        }
		return null;
	}

	@Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
		DataFlavor[] flavors = {null};
        try {
            flavors[0] = MainFrame.getDragAndDropPanelDataFlavor();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return false;
        }

        for (DataFlavor f : flavors) {
            if (f.equals(flavor)) {
                return true;
            }
        }

        return false;
	}

	@Override
	public Object getTransferData(DataFlavor flavor)
			throws UnsupportedFlavorException, IOException {
       
        DataFlavor thisFlavor = null;

        try {
            thisFlavor = MainFrame.getDragAndDropPanelDataFlavor();
        } catch (Exception e) {
            e.printStackTrace(System.err);
            return null;
        }
        
        if (thisFlavor != null && flavor.equals(thisFlavor)) {
            return ProcessingStepsPanel.this;
        }

        return null;
	}

	public JPanel getOptionPanel() {
		return optionPanel;
	}

	public void setOptionPanel(JPanel optionPanel) {
		this.optionPanel = optionPanel;
		mf.optionPanel.add(optionPanel);
		optionPanel.setVisible(visibilityOptionPanel);
		mf.hideAllOptionPanels();
		setVisibilityOptionPanel(true);
		mf.repaint();
		
	}

	public boolean getIsVisibilityOptionPanel() {
		return visibilityOptionPanel;
	}

	public void setVisibilityOptionPanel(boolean visibilityOptionPanel) {
		this.visibilityOptionPanel = visibilityOptionPanel;
		optionPanel.setVisible(visibilityOptionPanel);
	}
	
	public void setProgressbarValue(int val){
		progressbar.setValue(val);
	}

}
