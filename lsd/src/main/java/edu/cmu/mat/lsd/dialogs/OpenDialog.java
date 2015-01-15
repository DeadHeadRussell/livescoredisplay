package edu.cmu.mat.lsd.dialogs;

import java.awt.LayoutManager;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.List;

import javax.swing.AbstractAction;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.InputMap;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JPanel;
import javax.swing.KeyStroke;

import edu.cmu.mat.scores.Score;

public class OpenDialog {

	private JDialog _dialog = new JDialog();
	private JComboBox<Score> _scoresSelect;
	private JButton _open = new JButton("Open");
	private JButton _close = new JButton("Close");
	private boolean _opened = false;

	public OpenDialog(List<Score> scores) {
		_dialog.setResizable(false);
		addCancelByEscapeKey();

		JPanel standardLayout = new JPanel();
		standardLayout
				.setLayout(new BoxLayout(standardLayout, BoxLayout.Y_AXIS));

		_scoresSelect = new JComboBox<Score>((Score[]) scores.toArray());
		standardLayout.add(_scoresSelect);

		JPanel panel = new JPanel();
		LayoutManager layout = new BoxLayout(panel, BoxLayout.X_AXIS);
		panel.setLayout(layout);
		panel.add(Box.createHorizontalGlue());
		panel.add(_open);
		panel.add(Box.createHorizontalStrut(1));
		panel.add(_close);
		standardLayout.add(panel);

		_dialog.getContentPane().add(standardLayout);
		_dialog.pack();
		_dialog.setModal(true);
		_dialog.setVisible(true);

		_open.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent e) {
				_opened = true;
				_dialog.dispose();
			}
		});

		_close.addActionListener(new ActionListener() {
			public void actionPerformed(ActionEvent arg0) {
				_dialog.dispose();
			}
		});
	}

	public Score getSelection() {
		int index = _scoresSelect.getSelectedIndex();
		System.out.println(index);
		if (_opened && index >= 0 && index < _scoresSelect.getItemCount()) {
			return _scoresSelect.getItemAt(index);
		}
		return null;
	}

	private void addCancelByEscapeKey() {
		String CANCEL_ACTION_KEY = "CANCEL_ACTION_KEY";
		int noModifiers = 0;
		KeyStroke escapeKey = KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE,
				noModifiers, false);
		InputMap inputMap = _dialog.getRootPane().getInputMap(
				JComponent.WHEN_ANCESTOR_OF_FOCUSED_COMPONENT);
		inputMap.put(escapeKey, CANCEL_ACTION_KEY);
		AbstractAction cancelAction = new AbstractAction() {
			private static final long serialVersionUID = 1L;

			public void actionPerformed(ActionEvent e) {
				_dialog.dispose();
			}
		};
		_dialog.getRootPane().getActionMap()
				.put(CANCEL_ACTION_KEY, cancelAction);
	}
}
