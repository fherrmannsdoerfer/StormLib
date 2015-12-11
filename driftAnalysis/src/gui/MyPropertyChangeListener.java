package gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

public class MyPropertyChangeListener implements PropertyChangeListener{
	ProcessingStepsPanel psp;
	public MyPropertyChangeListener(ProcessingStepsPanel psp){
		this.psp = psp;
	}
	@Override
	public void propertyChange(PropertyChangeEvent evt) {
		psp.setProgressbarValue((Integer)evt.getNewValue());
	}

}
