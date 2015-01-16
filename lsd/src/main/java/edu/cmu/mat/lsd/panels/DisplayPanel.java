package edu.cmu.mat.lsd.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import edu.cmu.mat.lsd.Model;
import edu.cmu.mat.lsd.components.JCursor;
import edu.cmu.mat.lsd.components.JSection;
import edu.cmu.mat.lsd.hcmp.HcmpClient;
import edu.cmu.mat.lsd.hcmp.HcmpListener;
import edu.cmu.mat.lsd.hcmp.TimeMap;
import edu.cmu.mat.scores.Arrangement;
import edu.cmu.mat.scores.Barline;
import edu.cmu.mat.scores.Score;
import edu.cmu.mat.scores.Section;
import edu.cmu.mat.scores.System;

public class DisplayPanel implements Panel, HcmpListener {
	private Model _model;

	private JScrollPane _scroller;
	private JPanel _panel = new JPanel();
	private JLayeredPane _layers = new JLayeredPane();

	private int _current_beat = 0;
	List<JSection> _jsections;

	private TimeMap _time_map;
	private Timer _play_timer;
	private JCursor _cursor;
	private int _playback_id = 0;

	public DisplayPanel(Model model) {
		_model = model;

		_panel.setLayout(new BoxLayout(_panel, BoxLayout.Y_AXIS));
		_layers.add(_panel, 2);
		_layers.setPreferredSize(new Dimension(800, 800));
		_panel.setSize(_layers.getPreferredSize());
		_panel.setLocation(0, 0);
		_scroller = new JScrollPane(_layers);

		_layers.setVisible(true);
		_cursor = new JCursor();

		onUpdateScore();
		onUpdateView();
	}

	public JComponent getContainer() {
		return _scroller;
	}

	public void onUpdateLibraryPath() {
	}

	public void onUpdateModel() {
	}

	public void onUpdateScore() {
		_panel.removeAll();

		Score score = _model.getCurrentScore();
		if (score == null) {
			return;
		}
		List<Section> arrangement = score.getArrangementList();
		if (arrangement == null) {
			return;
		}

		_jsections = new ArrayList<JSection>(arrangement.size());
		/*
		 * List<Section> sections = score.getSections(); for (Section section :
		 * arrangement) { int index = sections.indexOf(section); Section next =
		 * index < sections.size() - 1 ? sections .get(index + 1) : null;
		 * JSection jsection = new JSection(section, next);
		 * _jsections.add(jsection); _panel.add(jsection); }
		 */

		_layers.remove(_cursor);

		_cursor = new JCursor(_panel, score.getArrangement());
		_cursor.setOpaque(false);
		_cursor.setLocation(0, 0);
		_cursor.setSize(_layers.getPreferredSize());

		_layers.add(_cursor, 2);
		_layers.moveToFront(_cursor);

		_current_beat = 0;

		redraw();
	}

	public void onUpdateView() {
		if (_model.getCurrentView() == Model.VIEW_DISPLAY) {
			_model.getHcmp().setListener(this);
			onUpdateScore();
		} else {
			_model.getHcmp().unsetListener(this);
		}
	}

	public void onProgramQuit() {
	}

	public void onUpdateTool() {
	}

	@Override
	public Boolean handleNewPosition(int beat) {
		setTime(beat);
		return true;
	}

	@Override
	public Boolean handleNewTime(TimeMap time_map) {
		_time_map = time_map;
		restart();
		return true;
	}

	@Override
	public Boolean handlePlay() {
		start();
		return true;
	}

	@Override
	public Boolean handlePause() {
		stop();
		return true;
	}

	@Override
	public Boolean handleStop() {
		stop();
		resetTime();
		return true;
	}

	private void fireNextEvent(final int id) {
		if (id != _playback_id) {
			return;
		}

		double next_event_time = getNextTime();
		if (next_event_time < 0) {
			return;
		}

		double next_real_time = _time_map.from(next_event_time);
		int next_time = (int) (next_real_time - new Date().getTime());

		_play_timer = new Timer(next_time, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				onEvent(_time_map.at((double) new Date().getTime()));
				fireNextEvent(id);
			}
		});

		_play_timer.setRepeats(false);
		_play_timer.start();
	}

	private void resetTime() {
		_cursor.resetTime();
		redraw();
	}

	private void setTime(int beat) {
		_current_beat = beat;

		if (_current_beat < 0) {
			_cursor.resetTime();
			return;
		}
		Arrangement arrangement = _model.getCurrentScore().getArrangement();
		Barline current_bar = arrangement.getBarline(_current_beat);
		if (current_bar == null) {
			_cursor.resetTime();
			return;
		}

		int i = arrangement.getSectionNumber(_current_beat);
		Section current_section = arrangement.getList().get(i);
		JSection current_jsection = _jsections.get(i);
		System top_system = current_section.getStart().getParent();
		int image_top = top_system.getTop();

		int x = current_bar.getOffset();
		int y = current_bar.getParent().getBottom() - image_top
				+ current_jsection.getY();

		_cursor.setPosition(x, y);
		redraw();
	}

	private double getNextTime() {
		return _model.getCurrentScore().getArrangement()
				.getNextBarlineBeat(_current_beat);
	}

	private void onEvent(double event_time) {
		setTime((int) event_time);
	}

	private void start() {
		_playback_id++;
		fireNextEvent(_playback_id);
	}

	private void stop() {
		_playback_id++;
		if (_play_timer != null) {
			_play_timer.stop();
		}
	}

	private void restart() {
		stop();
		start();
	}

	private void redraw() {
		_layers.revalidate();
		_layers.repaint();

		_scroller.revalidate();
		_scroller.repaint();
	}
}
