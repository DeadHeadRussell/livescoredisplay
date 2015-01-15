package edu.cmu.mat.lsd.panels;

import javax.swing.JComponent;
import javax.swing.JPanel;
import javax.swing.JScrollPane;

import edu.cmu.mat.lsd.Model;
import edu.cmu.mat.lsd.components.JPage;
import edu.cmu.mat.scores.Page;
import edu.cmu.mat.scores.Score;

public class NotationPanel implements Panel {
	private Model _model;

	private JScrollPane _scroller;
	private JPanel _panel = new JPanel();

	public NotationPanel(Model model) {
		_model = model;
		_scroller = new JScrollPane(_panel);
		onUpdateScore();
	}

	public JComponent getContainer() {
		return _scroller;
	}

	public void onUpdateModel() {
	}

	public void onUpdateScore() {
		_panel.removeAll();

		Score score = _model.getCurrentScore();
		for (Page page : score.getPages()) {
			_panel.add(new JPage(_model, page));
		}

		_scroller.repaint();
	}

	public void onUpdateTool() {
	}

	public void onUpdateView() {
	}

	public void onUpdateLibraryPath() {
	}

	public void onProgramQuit() {
	}
}
