package edu.cmu.mat.lsd.toolbars;

import java.awt.Color;

import javax.swing.JToolBar;

import edu.cmu.mat.lsd.Model;

public class DisplayToolbar implements Toolbar {
	private JToolBar _toolbar = new JToolBar("DisplayTools");

	public DisplayToolbar(Model model) {
		_toolbar.setBackground(new Color(220, 220, 220));
	}

	public JToolBar getToolbar() {
		return _toolbar;
	}

	public void setVisible(boolean visible) {
		_toolbar.setVisible(visible);
	}

}
