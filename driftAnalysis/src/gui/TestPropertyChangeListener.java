package gui;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;



public class TestPropertyChangeListener implements PropertyChangeListener{
    public static void main(String[] args) {
        MyClassWithText interestingText = new MyClassWithText();
        PropertyChangeListener listener = new TestPropertyChangeListener();
        interestingText.addPropertyChangeListener(listener);
        interestingText.setText("FRIST!");
        interestingText.setText("it's more like when you take a car, and you...");
    }
    
    @Override
    public void propertyChange(PropertyChangeEvent event) {
        if (event.getPropertyName().equals("MyTextProperty")) {
            System.out.println(event.getNewValue().toString());
        }
    }
}

class MyClassWithText {
    protected PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(MyClassWithText.class);
    private String text;


    public void setText(String text) {
        String oldText = this.text;
        this.text = text;
        propertyChangeSupport.firePropertyChange("MyTextProperty",5, 5);
    }

    public void addPropertyChangeListener(PropertyChangeListener listener) {
        propertyChangeSupport.addPropertyChangeListener(listener);
    }
}
