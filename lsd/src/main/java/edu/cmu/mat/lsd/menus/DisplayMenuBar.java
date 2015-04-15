package edu.cmu.mat.lsd.menus;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.swing.Box;
import javax.swing.ButtonGroup;
import javax.swing.JCheckBox;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JRadioButtonMenuItem;
import javax.swing.JSeparator;
import javax.swing.JTextField;

import edu.cmu.mat.lsd.ControllerListener;
import edu.cmu.mat.lsd.Model;
import edu.cmu.mat.parsers.exceptions.CompilerException;
import edu.cmu.mat.scores.Score;

public class DisplayMenuBar extends JMenuBar implements ControllerListener {
	private static final long serialVersionUID = 6196042768418609356L;

	private Model _model;
	private JMenu _open;
	private JRadioButtonMenuItem _notation;
	private JRadioButtonMenuItem _display;

	public DisplayMenuBar(Model model) {
		_model = model;

		add(createFileMenu());
		add(createLibraryMenu());
		add(createScoreMenu());
		onUpdateView();
		onUpdateLibraryPath();
	}

	private JMenu createFileMenu() {
		JMenu file = new JMenu("File");
		JMenuItem new_score = new JMenuItem("New Score...");
		JMenuItem save = new JMenuItem("Save");
		JMenuItem quit = new JMenuItem("Quit");

		file.add(new_score);
		file.add(new JSeparator());
		file.add(save);
		file.add(quit);

		new_score.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent event) {
				String name = JOptionPane.showInputDialog("Score Name");
				if (name == null || name.length() == 0) {
					return;
				}

				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setMultiSelectionEnabled(true);
				chooser.showOpenDialog(null);
				File[] images = chooser.getSelectedFiles();
				if (images == null || images.length == 0) {
					return;
				}

				try {
					_model.onNewScore(name, images);
				} catch (IOException | CompilerException exception) {
					exception.printStackTrace();
				}
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
		_open = new JMenu("Open Score");
		JMenuItem setHcmpData = new JMenuItem("Set HCMP Info...");
		JMenuItem setPath = new JMenuItem("Set Path...");

		library.add(_open);
		library.add(new JSeparator());
		library.add(setHcmpData);
		library.add(setPath);

		setHcmpData.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JTextField addressField = new JTextField(15);
				JTextField pullPortField = new JTextField(4);
				JTextField publishPortField = new JTextField(4);
				JCheckBox enabledField = new JCheckBox();

				addressField.setText(_model.getHcmpAddress());
				pullPortField.setText(_model.getHcmpPullPort());
				publishPortField.setText(_model.getHcmpPublishPort());
				enabledField.setSelected(_model.getHcmpEnabled());

				JPanel panel = new JPanel();
				panel.add(new JLabel("IP Address: "));
				panel.add(addressField);
				panel.add(Box.createVerticalStrut(10));
				panel.add(new JLabel("Pull Port: "));
				panel.add(pullPortField);
				panel.add(Box.createHorizontalStrut(15));
				panel.add(new JLabel("Publish Port: "));
				panel.add(publishPortField);
				panel.add(Box.createVerticalStrut(10));
				panel.add(new JLabel("Enable HCMP"));
				panel.add(enabledField);

				int result = JOptionPane.showConfirmDialog(null, panel,
						"Please Enter HCMP Data", JOptionPane.OK_CANCEL_OPTION);
				if (result == JOptionPane.OK_OPTION) {
					String address = addressField.getText();
					String pullPort = pullPortField.getText();
					String publishPort = publishPortField.getText();
					if (address.length() > 0 && pullPort.length() > 0
							&& publishPort.length() > 0) {
						_model.setHcmpData(address, pullPort, publishPort);
					}

					_model.setHcmpEnabled(enabledField.isSelected());
				}
			}
		});

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

	private JMenu createScoreMenu() {
		JMenu score = new JMenu("Score");

		JMenuItem rename = new JMenuItem("Rename...");
		JMenuItem add_images = new JMenuItem("Add Pages...");

		ButtonGroup group = new ButtonGroup();
		_notation = new JRadioButtonMenuItem("Notation");
		_display = new JRadioButtonMenuItem("Display");
		group.add(_notation);
		group.add(_display);

		score.add(rename);
		score.add(add_images);
		score.add(new JSeparator());
		score.add(_notation);
		score.add(_display);

		rename.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				String name = JOptionPane.showInputDialog("Score Name");
				if (name == null || name.length() == 0) {
					return;
				}
				try {
					_model.renameCurrentScore(name);
				} catch (IOException exception) {
					// TODO Auto-generated catch block
					exception.printStackTrace();
				}
			}
		});

		add_images.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				JFileChooser chooser = new JFileChooser();
				chooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
				chooser.setMultiSelectionEnabled(true);
				chooser.showOpenDialog(null);
				File[] images = chooser.getSelectedFiles();
				if (images == null || images.length == 0) {
					return;
				}

				try {
					_model.addPages(images);
				} catch (IOException exception) {
					// TODO Auto-generated catch block
					exception.printStackTrace();
				}
			}
		});

		_notation.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_model.setCurrentView(Model.VIEW_NOTATION);
			}
		});

		_display.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_model.setCurrentView(Model.VIEW_DISPLAY);
			}
		});

		return score;
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
