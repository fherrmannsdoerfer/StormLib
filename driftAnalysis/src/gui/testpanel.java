package gui;

import javax.swing.JPanel;
import javax.swing.Box;
import javax.swing.JButton;
import javax.swing.JProgressBar;
import java.awt.Component;
import java.awt.Dimension;

public class testpanel extends JPanel {

	/**
	 * Create the panel.
	 */
	public testpanel() {
		
		Box verticalBox = Box.createVerticalBox();
		verticalBox.setPreferredSize(new Dimension(200, 50));
		add(verticalBox);
		
		Box horizontalBox = Box.createHorizontalBox();
		verticalBox.add(horizontalBox);
		
		JButton btnNewButton = new JButton("New button");
		horizontalBox.add(btnNewButton);
		
		JButton btnNewButton_1 = new JButton("New button");
		horizontalBox.add(btnNewButton_1);
		
		Component verticalGlue = Box.createVerticalGlue();
		verticalBox.add(verticalGlue);
		
		JProgressBar progressBar = new JProgressBar();
		verticalBox.add(progressBar);

	}

}
