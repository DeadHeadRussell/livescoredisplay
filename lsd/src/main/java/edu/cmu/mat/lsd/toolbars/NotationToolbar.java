package edu.cmu.mat.lsd.toolbars;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import javax.swing.AbstractAction;
import javax.swing.JButton;
import javax.swing.JLabel;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.JTextArea;
import javax.swing.JToolBar;

import com.google.common.base.Joiner;

import edu.cmu.mat.lsd.ControllerListener;
import edu.cmu.mat.lsd.Model;
import edu.cmu.mat.scores.Score;
import edu.cmu.mat.scores.Section;

public class NotationToolbar implements Toolbar, ControllerListener {
	private Model _model;
	private JToolBar _toolbar = new JToolBar("NotationTools");
	JTextArea _sections;
	JTextArea _arrangement;

	public NotationToolbar(Model model) {
		_model = model;

		_toolbar.setBackground(new Color(220, 220, 220));

		JButton newButton = new JButton("New...");
		JButton moveButton = new JButton("Move");
		JButton deleteButton = new JButton("Delete");
		JButton noneButton = new JButton("None");

		_toolbar.add(newButton);
		_toolbar.add(moveButton);
		_toolbar.add(deleteButton);
		_toolbar.add(noneButton);

		_toolbar.addSeparator();

		JLabel section_label = new JLabel("Sections:");
		_sections = new JTextArea();
		_sections.setEditable(false);
		_toolbar.add(section_label);
		_toolbar.addSeparator(new Dimension(8, 0));
		_toolbar.add(_sections);

		_toolbar.addSeparator();

		JLabel arrangement_label = new JLabel("Arrangement:");
		_arrangement = new JTextArea();
		_toolbar.add(arrangement_label);
		_toolbar.addSeparator(new Dimension(8, 0));
		_toolbar.add(_arrangement);

		_toolbar.addSeparator();

		JButton saveArrangementButton = new JButton("Save Arrangement");
		_toolbar.add(saveArrangementButton);

		onUpdateScore();

		final JPopupMenu newPopupMenu = new JPopupMenu();

		newPopupMenu.add(new JMenuItem(new AbstractAction("System") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent event) {
				_model.setCurrentTool(_model.NEW_SYSTEM_TOOL);
			}
		}));

		newPopupMenu.add(new JMenuItem(new AbstractAction("Barline") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent event) {
				_model.setCurrentTool(_model.NEW_BARLINE_TOOL);
			}
		}));

		newPopupMenu.add(new JMenuItem(new AbstractAction("Section") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent event) {
				_model.setCurrentTool(_model.NEW_SECTION_TOOL);
			}
		}));

		newPopupMenu.add(new JMenuItem(new AbstractAction("Repeat") {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent arg0) {
				_model.setCurrentTool(_model.NEW_REPEAT_TOOL);
			}
		}));

		newButton.addMouseListener(new MouseAdapter() {
			public void mousePressed(MouseEvent event) {
				newPopupMenu.show(event.getComponent(), event.getX(),
						event.getY());
			}
		});

		moveButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				_model.setCurrentTool(_model.MOVE_TOOL);
			}
		});

		deleteButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				_model.setCurrentTool(_model.DELETE_TOOL);
			}
		});

		noneButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_model.setCurrentTool(null);
			}
		});

		saveArrangementButton.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_model.sendArrangement();
			}
		});

		_arrangement.addKeyListener(new KeyListener() {
			@Override
			public void keyTyped(KeyEvent e) {
			}

			@Override
			public void keyReleased(KeyEvent e) {
				Score score = _model.getCurrentScore();
				if (score != null) {
					score.saveArrangment(_arrangement.getText());
				}
			}

			@Override
			public void keyPressed(KeyEvent e) {
			}
		});
	}

	public JToolBar getToolbar() {
		return _toolbar;
	}

	public void setVisible(boolean visible) {
		_toolbar.setVisible(visible);
	}

	@Override
	public void onUpdateLibraryPath() {
	}

	@Override
	public void onUpdateModel() {
		Score score = _model.getCurrentScore();
		if (score == null) {
			return;
		}

		Joiner joiner = Joiner.on("  ");

		Set<Section> sections = score.getSections();
		List<String> section_names = new ArrayList<String>(sections.size());
		for (Section section : sections) {
			section_names.add(section.getName());
		}
		_sections.setText(joiner.join(section_names));

		List<Section> arrangement = _model.getCurrentScore()
				.getArrangementList();
		List<String> arrangement_names = new ArrayList<String>(
				arrangement.size());
		for (Section section : arrangement) {
			arrangement_names.add(section.getName());
		}
		_arrangement.setText(joiner.join(arrangement_names));
	}

	@Override
	public void onUpdateScore() {
		onUpdateModel();
	}

	@Override
	public void onUpdateView() {
	}

	@Override
	public void onUpdateTool() {
	}

	@Override
	public void onProgramQuit() {
	}
}
