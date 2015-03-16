package edu.cmu.mat.lsd.panels;

import java.awt.Dimension;
import java.awt.GridBagLayout;
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
import edu.cmu.mat.lsd.components.JArrow;
import edu.cmu.mat.lsd.components.JBlock;
import edu.cmu.mat.lsd.components.JCursor;
import edu.cmu.mat.lsd.hcmp.HcmpListener;
import edu.cmu.mat.lsd.hcmp.TimeMap;
import edu.cmu.mat.scores.Barline;
import edu.cmu.mat.scores.Block;
import edu.cmu.mat.scores.PlaybackEvent;
import edu.cmu.mat.scores.Score;

public class DisplayPanel implements Panel, HcmpListener {
	private Model _model;

	private JScrollPane _scroller;
	private JPanel _panel = new JPanel();
	private JPanel _centering = new JPanel();
	private JPanel _margin = new JPanel();
	private JLayeredPane _layers = new JLayeredPane();
	private JBlock _upper_block = new JBlock();
	private JBlock _lower_block = new JBlock();
	private JCursor _cursor;
	private JArrow _arrow;

	//List<JSection> _jsections;
	List<Block> _blocks;

	private TimeMap _time_map;
	private Timer _play_timer;
	
	private int _playback_id = 0;
	private List<PlaybackEvent> _playback_events = new ArrayList<PlaybackEvent>();
	private int _events_index = 0;
	//private int _current_jsection_index = -1;
	private int _current_block_index = 0;

	public DisplayPanel(Model model) {
		_model = model;
		_model.getHcmp().setListener(this);

		_panel.setLayout(new BoxLayout(_panel, BoxLayout.Y_AXIS));
		_layers.add(_panel, -1);
		_panel.setLocation(0, 0);

		_arrow = new JArrow(_panel);
		_arrow.setOpaque(false);
		//_arrow.setSize(_layers.getPreferredSize());
		_layers.add(_arrow, 0);
		
		_cursor = new JCursor(_panel);
		_cursor.setOpaque(false);
		//_cursor.setSize(_layers.getPreferredSize());
		_layers.add(_cursor, 0);


		
		_scroller = new JScrollPane(_centering);
		_centering.setLayout(new GridBagLayout());
		_centering.add(_layers);
		_layers.setVisible(true);
		
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
			
			 handleNewArrangement(new String[] { "A,0,20","A,0,20","B,20,32","C,52,16","D,68,8","D,68,8","F,76,16"});
			 handleNewPosition(0); handleNewTime(TimeMap.Create(new
			 Date().getTime(), 0, 0.0072)); handlePlay();
			 
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
		_blocks = _model.getCurrentScore().createBlockList(_playback_events);
		_current_block_index = 0;
		
		_panel.removeAll();

		int width = 0;
		int height = 0;
		
		/*_jsections = new ArrayList<JSection>();
		//for (PlaybackEvent event : _playback_events) {
			if (event.isSectionStart()) {
				JSection jsection = new JSection(event.getSection());
				_jsections.add(jsection);
				//_panel.add(jsection);

				//width = Math.max(width, jsection.getWidth());
				//height += jsection.getHeight();
			}
		}*/
		
		//List<Page> pages = _model.getCurrentScore().getPages();
		
		for (Block block : _blocks) {
			/*
			Page start_page = block.getStartSystem().getParent();
			Page end_page = block.getEndSystem().getParent();
			java.lang.System.out.format("Block from page %d system %d to page %d system %d\n",
					pages.indexOf(start_page), start_page.getSystems().indexOf(block.getStartSystem()),
					pages.indexOf(end_page), end_page.getSystems().indexOf(block.getEndSystem()));
			JBlock jblock = new JBlock();
			jblock.setBlock(block);
			_panel.add(jblock);
			_panel.add(_margin);
			*/
			width = Math.max(width, block.getWidth());
			//height += jblock.getHeight() + 10;
		}
		
		_upper_block.setBlock(_blocks.get(0));
		if (_blocks.size() > 1) {
			_lower_block.setBlock(_blocks.get(1));
			height = _upper_block.getHeight() + _lower_block.getHeight() + 10;
			JPanel margin = new JPanel();
			_margin.setPreferredSize(new Dimension(width, 10));
			
			_panel.add(_upper_block);
			_panel.add(margin);
			_panel.add(_lower_block);
		}
		else {
			height = _upper_block.getHeight();
			_panel.add(_upper_block);
		}
		
	
		_layers.setPreferredSize(new Dimension(width, height));
		_panel.setSize(_layers.getPreferredSize());

		_cursor.setSize(_layers.getPreferredSize());
		//_cursor.setLocation(0, 0);
		
		_arrow.setSize(_layers.getPreferredSize());
		//_arrow.setPosition(50, 0, 0, 50);

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
				updateBlock();
				moveCursor();
				if (_events_index <= _playback_events.size()) {
					fireNextEvent(id);
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
		//_current_jsection_index = -2;
		for (PlaybackEvent event : _playback_events) {
			//if (event.isSectionStart()) {
				//_current_jsection_index++;
			//}

			so_far += event.getDuration();
			if (so_far > beat) {
				_events_index = _playback_events.indexOf(event);
				break;
			}
		}
		moveCursor();
	}

	private void updateBlock() {
		Block current_block = _blocks.get(_current_block_index);
		if (!current_block.hasFlippedNextBlock()) {
			if (_current_block_index < _blocks.size() - 1) {
				Block next_block = _blocks.get(_current_block_index + 1);
				
				if (_current_block_index % 2 == 0) _lower_block.setBlock(next_block);
				else _upper_block.setBlock(next_block);
				
				current_block.flippedNextBlock();
			}
			else current_block.flippedNextBlock();
		}
		
		if (_current_block_index < _blocks.size() - 1) {
			Score score = _model.getCurrentScore();
			if (score.outOfBlock(current_block, _playback_events.get(_events_index))) {
				_current_block_index += 1;
				if (score.outOfBlock(_blocks.get(_current_block_index), _playback_events.get(_events_index))) {
					java.lang.System.out.print("Error! Current event out of current and next block.\n");
				}
			}
		}
		redraw();
	}
	
	private void moveCursor() {
		if (_playback_events.isEmpty()) {
			return;
		}

		if (_events_index < 0) {
			resetTime();
			return;
		}
		if (_events_index >= _playback_events.size()) {
			PlaybackEvent end_event = _playback_events.get(_playback_events.size()-1);
			Barline end_bar = end_event.getEnd();
			
			Block end_block = _blocks.get(_current_block_index);
			//int image_top = end_block.getStartSystem().getTop();
			JBlock current_jblock;
			if (_current_block_index % 2 == 0) current_jblock = _upper_block;
			else current_jblock = _lower_block;

			int x = end_bar.getOffset();
			int y = end_block.getYOffset(end_bar.getParent())
					+ current_jblock.getY();

			_cursor.setPosition(x, y);
			
		}
		else {
			PlaybackEvent current_event = _playback_events.get(_events_index);
			//Section current_section = current_event.getSection();
			Barline current_bar = current_event.getStart();
	
			//if (current_event.isSectionStart()) {
			//	_current_jsection_index++;
			//}
	
			//JSection current_jsection = _jsections.get(_current_jsection_index);
			//System top_system = current_section.getStart().getParent();
			//int image_top = top_system.getTop();
			
			Block current_block = _blocks.get(_current_block_index);
			//int image_top = current_block.getStartSystem().getTop();
			JBlock current_jblock;
			if (_current_block_index % 2 == 0) current_jblock = _upper_block;
			else current_jblock = _lower_block;
	
			int x = current_bar.getOffset();
			int y = current_block.getYOffset(current_bar.getParent())
					+ current_jblock.getY();
	
			_cursor.setPosition(x, y);
			
			
			/*
			JScrollBar scroll_bar = _scroller.getVerticalScrollBar();
			if (y > (scroll_bar.getValue() + _scroller.getHeight()) - 100) {
				int system_top = current_bar.getParent().getTop() - image_top
						+ current_jsection.getY();
				scroll_bar.setValue(Math.min(system_top, scroll_bar.getMaximum()));
			}*/
		}
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
