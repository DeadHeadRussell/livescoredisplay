package edu.cmu.mat.lsd.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.ButtonGroup;
import javax.swing.JFileChooser;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;

import edu.cmu.mat.lsd.ControllerListener;
import edu.cmu.mat.lsd.Model;
import edu.cmu.mat.scores.Score;

public class DisplayMenuBar extends JMenuBar implements ControllerListener {
	private static final long serialVersionUID = 6196042768418609356L;

	private Model _model;
	private JMenu _open;
	private JRadioButtonMenuItem _notation;
	private JRadioButtonMenuItem _section;
	private JRadioButtonMenuItem _arrangement;
	private JRadioButtonMenuItem _display;

	public DisplayMenuBar(Model model) {
		_model = model;

		add(createFileMenu());
		add(createLibraryMenu());
		add(createViewMenu());
		onUpdateView();
		onUpdateLibraryPath();
	}

	private JMenu createFileMenu() {
		JMenu file = new JMenu("File");
		JMenu new_ = new JMenu("New");
		JMenuItem new_score = new JMenuItem("Score...");
		JMenuItem new_arrangement = new JMenuItem("Arrangment...");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem quit = new JMenuItem("Quit");

		new_.add(new_score);
		new_.add(new_arrangement);
		file.add(new_);
		file.add(new JSeparator());
		file.add(save);
		file.add(quit);

		new_score.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				_model.onNewScore();
			}
		});

		new_arrangement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				_model.onNewArrangement();
			}
		});

		save.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_model.save();
			}
		});

		quit.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_model.quit();
			}
		});

		return file;
	}

	private JMenu createLibraryMenu() {
		JMenu library = new JMenu("Library");
		JMenu new_ = new JMenu("New");
		JMenuItem newScore = new JMenuItem("Score...");
		JMenuItem newArrangement = new JMenuItem("Arrangment...");
		_open = new JMenu("Open");
		JMenuItem setPath = new JMenuItem("Set Path...");

		new_.add(newScore);
		new_.add(newArrangement);
		library.add(new_);
		library.add(_open);
		library.add(new JSeparator());
		library.add(setPath);

		setPath.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
				chooser.showOpenDialog(null);
				try {
					File path = chooser.getSelectedFile();
					if (path != null) {
						_model.onSetPath(path);
					}
				} catch (IOException exception) {
					exception.printStackTrace();
				}
			}
		});

		return library;
	}

	private JMenu createViewMenu() {
		JMenu view = new JMenu("View");

		ButtonGroup group = new ButtonGroup();
		_notation = new JRadioButtonMenuItem("Notation");
		_section = new JRadioButtonMenuItem("Section");
		_arrangement = new JRadioButtonMenuItem("Arragnment");
		_display = new JRadioButtonMenuItem("Display");
		group.add(_notation);
		group.add(_section);
		group.add(_arrangement);
		group.add(_display);

		_notation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_model.setCurrentView(Model.VIEW_NOTATION);
			}
		});

		_section.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_model.setCurrentView(Model.VIEW_SECTIONS);
			}
		});

		_arrangement.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_model.setCurrentView(Model.VIEW_ARRANGMENT);
			}
		});

		_display.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_model.setCurrentView(Model.VIEW_DISPLAY);
			}
		});

		view.add(_notation);
		view.add(_section);
		view.add(_arrangement);
		view.add(_display);

		return view;
	}

	public void onUpdateLibraryPath() {
		_open.removeAll();
		List<Score> scores = _model.getScoreList();
		ButtonGroup group = new ButtonGroup();
		String current = _model.getCurrentScoreName();
		for (Score score : scores) {
			final String scoreName = score.getName();
			boolean checked = (current.equals(scoreName));

			JRadioButtonMenuItem scoreItem = new JRadioButtonMenuItem(
					score.getName(), checked);

			scoreItem.addActionListener(new ActionListener() {
				public void actionPerformed(ActionEvent e) {
					_model.setCurrentScore(scoreName);
				}
			});
			group.add(scoreItem);
			_open.add(scoreItem);
		}
		if (scores.size() == 0) {
			_open.add(new JMenuItem(""));
		}
	}

	public void onUpdateModel() {
	}

	public void onUpdateView() {
		int view = _model.getCurrentView();
		if (view == Model.VIEW_NOTATION) {
			_notation.setSelected(true);
		} else if (view == Model.VIEW_SECTIONS) {
			_section.setSelected(true);
		} else if (view == Model.VIEW_ARRANGMENT) {
			_arrangement.setSelected(true);
		} else if (view == Model.VIEW_DISPLAY) {
			_display.setSelected(true);
		}
	}

	public void onUpdateScore() {
	}

	public void onUpdateTool() {
	}

	public void onProgramQuit() {
	}
}
