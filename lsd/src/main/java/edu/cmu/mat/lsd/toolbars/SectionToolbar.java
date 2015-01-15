package edu.cmu.mat.lsd.toolbars;

import java.awt.Color;
import java.awt.Dimension;

import javax.swing.DefaultListModel;
import javax.swing.JList;
import javax.swing.JScrollPane;
import javax.swing.JToolBar;

import edu.cmu.mat.lsd.ControllerListener;
import edu.cmu.mat.lsd.Model;

public class SectionToolbar implements Toolbar, ControllerListener {
	private Model _model;
	private JToolBar _toolbar = new JToolBar("ArrangmentTools");
	private JList<String> _list;
	private DefaultListModel<String> _list_model;

	public SectionToolbar(Model model) {
		_model = model;
		_toolbar.setBackground(new Color(220, 220, 220));

		_list_model = new DefaultListModel<String>();
		_list = new JList<String>(_list_model);
		_list.setLayoutOrientation(JList.HORIZONTAL_WRAP);
		_list.setVisibleRowCount(-1);
		JScrollPane listScroller = new JScrollPane(_list);
		listScroller.setPreferredSize(new Dimension(250, 80));

		_toolbar.add(listScroller);
		onUpdateScore();
	}

	public JToolBar getToolbar() {
		return _toolbar;
	}

	public void setVisible(boolean visible) {
		_toolbar.setVisible(visible);
	}

	public void onUpdateModel() {
		_model.loadSections(_list_model);
		_list.invalidate();
	}

	public void onUpdateScore() {
	}

	public void onUpdateLibraryPath() {
	}

	public void onUpdateView() {
	}

	public void onUpdateTool() {
	}

	public void onProgramQuit() {
	}
}
