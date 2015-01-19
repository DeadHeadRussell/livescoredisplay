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

	List<JSection> _jsections;

	private TimeMap _time_map;
	private Timer _play_timer;
	private JCursor _cursor;
	private int _playback_id = 0;
	private List<PlaybackEvent> _playback_events = new ArrayList<PlaybackEvent>();
	private int _events_index = 0;

	public DisplayPanel(Model model) {
		_model = model;
		_model.getHcmp().setListener(this);

		_panel.setLayout(new BoxLayout(_panel, BoxLayout.Y_AXIS));
		_layers.add(_panel, 2);
		_panel.setLocation(0, 0);
		_scroller = new JScrollPane(_layers);

		_layers.setVisible(true);

		_cursor = new JCursor(_panel);
		_cursor.setOpaque(false);
		_cursor.setSize(_layers.getPreferredSize());
		_layers.add(_cursor, 2);

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
			_scroller.revalidate();
			_scroller.repaint();
		} else {
			handleStop();
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
		if (_play_timer != null) {
			restart();
		}
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
		List<PlaybackEvent> new_events = _model.getCurrentScore()
				.createPlaybackEvents(arrangement_string);

		if (new_events == null) {
			java.lang.System.err.println("Could not parse new arrangement!");
			return false;
		}

		_playback_events = new_events;

		_panel.removeAll();

		int width = 0;
		int height = 0;

		_jsections = new ArrayList<JSection>();
		for (PlaybackEvent event : _playback_events) {
			if (event.isSectionStart()) {
				JSection jsection = new JSection(event.getSection());
				_jsections.add(jsection);
				_panel.add(jsection);

				width = Math.max(width, jsection.getWidth());
				height += jsection.getHeight();
			}
		}

		_layers.setPreferredSize(new Dimension(width, height));
		_panel.setSize(_layers.getPreferredSize());

		_cursor.setSize(_layers.getPreferredSize());
		_cursor.setLocation(0, 0);
		_layers.moveToFront(_cursor);

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

		long delay = 0;
		long time = new Date().getTime();
		for (;; _events_index++) {
			delay = (long) (_time_map.from(_events_index * 4) - time);
			if (delay > 0) {
				break;
			}
		}

		delay = Math.max(delay, 0);
		_play_timer = new Timer((int) delay, new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				moveCursor();
				if (_events_index <= _playback_events.size()) {
					fireNextEvent(id);
				} else {
					handleStop();
				}
			}
		});

		_play_timer.setRepeats(false);
		_play_timer.start();
	}

	private void resetTime() {
		setTime(0);
	}

	private void setTime(int beat) {
		int so_far = 0;
		for (PlaybackEvent event : _playback_events) {
			so_far += event.getDuration();
			if (so_far > beat) {
				_events_index = _playback_events.indexOf(event);
				break;
			}
		}
		moveCursor();
	}

	private void moveCursor() {
		if (_playback_events.isEmpty()) {
			return;
		}

		if (_events_index < 0 || _events_index >= _playback_events.size()) {
			resetTime();
			return;
		}

		PlaybackEvent current_event = _playback_events.get(_events_index);
		Section current_section = current_event.getSection();
		Barline current_bar = current_event.getStart();

		JSection current_jsection = _jsections.get(_events_index / 4);
		System top_system = current_section.getStart().getParent();
		int image_top = top_system.getTop();

		int x = current_bar.getOffset();
		int y = current_bar.getParent().getBottom() - image_top
				+ current_jsection.getY();

		_cursor.setPosition(x, y);
		redraw();
	}

	private void start() {
		_playback_id++;
		fireNextEvent(_playback_id);
	}

	private void pause() {
		_playback_id++;
		if (_play_timer != null) {
			_play_timer.stop();
			_play_timer = null;
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
