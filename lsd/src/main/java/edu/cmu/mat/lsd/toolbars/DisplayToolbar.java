package edu.cmu.mat.lsd.toolbars;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

import javax.swing.JButton;
import javax.swing.JToolBar;

import edu.cmu.mat.lsd.Model;

public class DisplayToolbar implements Toolbar {
	private JToolBar _toolbar = new JToolBar("DisplayTools");
	JButton _play_button = new JButton("Play");
	JButton _stop_button = new JButton("Stop");
	JButton _magnify_button = new JButton("+");
	JButton _reduce_button = new JButton("-");

	public DisplayToolbar(Model model) {
		_toolbar.setBackground(new Color(220, 220, 220));
//		_toolbar.add(_play_button);
//		_toolbar.add(_stop_button);
//		
//		_toolbar.addSeparator();
//		_toolbar.add(_magnify_button);
//		_toolbar.add(_reduce_button);
//		
//		_play_button.addActionListener(new ActionListener() {
//			public void actionPerformed(ActionEvent event) {
//				
//			}
//		});
//		
	}

	public JToolBar getToolbar() {
		return _toolbar;
	}

	public void setVisible(boolean visible) {
		_toolbar.setVisible(visible);
	}

}
