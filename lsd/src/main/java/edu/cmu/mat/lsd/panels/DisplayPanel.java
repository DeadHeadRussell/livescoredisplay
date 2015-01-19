package edu.cmu.mat.lsd.panels;

import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.swing.BoxLayout;
import javax.swing.JComponent;
import javax.swing.JLayeredPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.Timer;

import edu.cmu.mat.lsd.Model;
import edu.cmu.mat.lsd.components.JCursor;
import edu.cmu.mat.lsd.components.JSection;
import edu.cmu.mat.lsd.hcmp.HcmpListener;
import edu.cmu.mat.lsd.hcmp.TimeMap;
import edu.cmu.mat.scores.Barline;
import edu.cmu.mat.scores.PlaybackEvent;
import edu.cmu.mat.scores.Section;
import edu.cmu.mat.scores.System;

public class DisplayPanel implements Panel, HcmpListener {
	private Model _model;

	private JScrollPane _scroller;
	private JPanel _panel = new JPanel();
	private JLayeredPane _layers = new JLayeredPane();

	private int _current_beat = 0;
	Map<String, JSection> _jsection_map;

	private TimeMap _time_map;
	private Timer _play_timer;
	private JCursor _cursor;
	private int _playback_id = 0;
	private List<PlaybackEvent> _playback_events;
	private int _events_index = -1;

	public DisplayPanel(Model model) {
		_model = model;

		_panel.setLayout(new BoxLayout(_panel, BoxLayout.Y_AXIS));
		_layers.add(_panel, 2);
		_layers.setPreferredSize(new Dimension(800, 800));
		_panel.setSize(_layers.getPreferredSize());
		_panel.setLocation(0, 0);
		_scroller = new JScrollPane(_layers);

		_layers.setVisible(true);

		_cursor = new JCursor(_panel);
		_cursor.setOpaque(false);
		_cursor.setSize(_layers.getPreferredSize());
		_layers.add(_cursor, 2);

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
		pause();
		return true;
	}

	@Override
	public Boolean handleStop() {
		pause();
		resetTime();
		return true;
	}

	@Override
	public Boolean handleNewArrangement(String[] arrangement_string) {
		_playback_events = _model.getCurrentScore().createPlaybackEvents(
				arrangement_string);

		if (_playback_events == null) {
			java.lang.System.err
					.println("Invalid section name found in arrangement!");
			return false;
		}

		_panel.removeAll();

		_jsection_map = new HashMap<String, JSection>();

		for (PlaybackEvent event : _playback_events) {
			if (event.isSectionStart()) {
				JSection jsection = new JSection(event.getSection());
				_jsection_map.put(event.getSection().getName(), jsection);
				_panel.add(jsection);
			}
		}

		_cursor.setLocation(0, 0);
		_layers.moveToFront(_cursor);

		_current_beat = 0;

		redraw();

		if (_play_timer != null) {
			restart();
		}
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
		_events_index++;

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

		PlaybackEvent current_event = _playback_events.get(_events_index);
		Section current_section = current_event.getSection();
		Barline current_bar = current_event.getStart();

		JSection current_jsection = _jsection_map
				.get(current_section.getName());
		System top_system = current_section.getStart().getParent();
		int image_top = top_system.getTop();

		int x = current_bar.getOffset();
		int y = current_bar.getParent().getBottom() - image_top
				+ current_jsection.getY();

		_cursor.setPosition(x, y);
		redraw();
	}

	private double getNextTime() {
		if (_events_index == -1) {
			return 0;
		}
		return _playback_events.get(_events_index).getDuration();
	}

	private void onEvent(double event_time) {
		setTime((int) event_time);
	}

	private void start() {
		_playback_id++;
		fireNextEvent(_playback_id);
	}

	private void pause() {
		_playback_id++;
		if (_play_timer != null) {
			_play_timer.stop();
		}
	}

	private void restart() {
		pause();
		start();
	}

	private void redraw() {
		_layers.revalidate();
		_layers.repaint();

		_scroller.revalidate();
		_scroller.repaint();
	}
}
