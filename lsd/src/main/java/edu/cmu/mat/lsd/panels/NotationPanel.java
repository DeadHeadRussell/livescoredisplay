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
	private int _panelHeight;  // without tool bar and menu mar
	private JScrollPane _scroller;
	private JPanel _panel = new JPanel();

	public NotationPanel(Model model, int panelHeight) {
		_model = model;
		_panelHeight = panelHeight;
		_scroller = new JScrollPane(_panel, JScrollPane.VERTICAL_SCROLLBAR_NEVER, JScrollPane.HORIZONTAL_SCROLLBAR_AS_NEEDED);
		_scroller.getHorizontalScrollBar().setUnitIncrement(16);
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
		if (score == null) {
			return;
		}
		
		for (Page page : score.getPages()) {
			_panel.add(new JPage(_model, page, _panelHeight));
		}
		_scroller.revalidate();
		_scroller.repaint();
	}

	public void onUpdateTool() {
	}

	public void onUpdateView() {
		if (_model.getCurrentView() == Model.VIEW_NOTATION) {
			_scroller.revalidate();
			_scroller.repaint();
		}
	}

	public void onUpdateLibraryPath() {
		onUpdateScore();
	}

	public void onProgramQuit() {
	}
}
