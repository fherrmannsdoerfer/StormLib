package gui;

import java.awt.Cursor;
import java.awt.List;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.dnd.DropTargetContext;
import java.awt.dnd.DropTargetDragEvent;
import java.awt.dnd.DropTargetDropEvent;
import java.awt.dnd.DropTargetEvent;
import java.awt.dnd.DropTargetListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import javax.swing.JPanel;

class PanelDropTargetListener implements DropTargetListener {

    private final RootPanel rootPanel;
    
   
    private static final Cursor droppableCursor = Cursor.getPredefinedCursor(Cursor.HAND_CURSOR),
            notDroppableCursor = Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR);

    public PanelDropTargetListener(RootPanel sheet) {
        this.rootPanel = sheet;
    }

    public void dragOver(DropTargetDragEvent dtde) {
        if (!this.rootPanel.getCursor().equals(droppableCursor)) {
            this.rootPanel.setCursor(droppableCursor);
        }
    }

    public void dragExit(DropTargetEvent dte) {
        this.rootPanel.setCursor(notDroppableCursor);
    }

    public void drop(DropTargetDropEvent dtde) {
        
        this.rootPanel.setCursor(Cursor.getDefaultCursor());
        
        DataFlavor dragAndDropPanelFlavor = null;
        
        Object transferableObj = null;
        Transferable transferable = null;
        
        try {
            dragAndDropPanelFlavor = MainFrame.getDragAndDropPanelDataFlavor();
            
            transferable = dtde.getTransferable();
            DropTargetContext c = dtde.getDropTargetContext();
            
            if (transferable.isDataFlavorSupported(dragAndDropPanelFlavor)) {
                transferableObj = dtde.getTransferable().getTransferData(dragAndDropPanelFlavor);
            } 
            
        } catch (Exception ex) {}
        
        if (transferableObj == null) {
            return;
        }
        ProcessingStepsPanel droppedPanel = (ProcessingStepsPanel)transferableObj;
        final int dropYLoc = dtde.getLocation().y;
        Map<Integer, ProcessingStepsPanel> yLocMapForPanels = new HashMap<Integer, ProcessingStepsPanel>();
        yLocMapForPanels.put(dropYLoc, droppedPanel);

        for (ProcessingStepsPanel nextPanel : rootPanel.getMainFrame().getListProcessingStepPanels()) {
            int y = nextPanel.getY();
            if (!nextPanel.equals(droppedPanel)) {
                yLocMapForPanels.put(y, nextPanel);
            }
        }

        ArrayList<Integer> sortableYValues = new ArrayList<Integer>();
        sortableYValues.addAll(yLocMapForPanels.keySet());
        Collections.sort(sortableYValues);

        ArrayList<ProcessingStepsPanel> orderedPanels = new ArrayList<ProcessingStepsPanel>();
        for (Integer i : sortableYValues) {
            orderedPanels.add(yLocMapForPanels.get(i));
        }
        
        ArrayList<ProcessingStepsPanel> inMemoryPanelList = this.rootPanel.getMainFrame().getListProcessingStepPanels();
        inMemoryPanelList.clear();
        inMemoryPanelList.addAll(orderedPanels);
    
        this.rootPanel.getMainFrame().updatePanels();
    }

	@Override
	public void dragEnter(DropTargetDragEvent dtde) {
	}


	@Override
	public void dropActionChanged(DropTargetDragEvent dtde) {
	}


} 