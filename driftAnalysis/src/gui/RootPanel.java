package gui;

import java.awt.dnd.DropTarget;

import javax.swing.JPanel;

public class RootPanel extends JPanel{
	private final MainFrame mainFrame;
	RootPanel(MainFrame mainFrame){
		super();
		this.mainFrame = mainFrame;
		this.setTransferHandler(new DragAndDropTransferHandler());
		this.setDropTarget(new DropTarget(RootPanel.this, new PanelDropTargetListener(this)));
	}
	
	public MainFrame getMainFrame() {
        return mainFrame;
    }
}
