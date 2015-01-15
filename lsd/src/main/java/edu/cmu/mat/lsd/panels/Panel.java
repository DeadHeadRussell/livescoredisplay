package edu.cmu.mat.lsd.panels;

import javax.swing.JComponent;

import edu.cmu.mat.lsd.ControllerListener;

public interface Panel extends ControllerListener {
	public JComponent getContainer();
}
