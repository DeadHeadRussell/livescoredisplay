package edu.cmu.mat.scores;

import java.util.ArrayList;
import java.util.List;


public class Block {
	private List<System> _systems;
	private System _start_system = null;
	private System _end_system = null;
	
	private List<Barline> _jumps = new ArrayList<Barline>();
	private int _jump_index = 0;
	//private int _start_event_i;
	//private int _end_event_i;
	
	//private int _duration;
	
	private boolean _has_flipped_next_block = false;
	
	private java.awt.Image _image;
	
	public Block(List<System> systems) {
		_systems = systems;
		if (systems.size() > 0) {
			_start_system = systems.get(0);
			_end_system = systems.get(systems.size()-1);
		}
		_image = makeImage();
	}
	
	public void addJump(Barline from, Barline to) {
		_jumps.add(from);
		_jumps.add(to);
	}
	
	public Barline getNextJumpFrom() {
		if (_jump_index < _jumps.size()) {
			return _jumps.get(_jump_index);
		}
		else return null;
	}
	
	public Barline getNextJumpTo() {
		if (_jump_index < _jumps.size()) {
			return _jumps.get(_jump_index+1);
		}
		else return null;
	}
	
	public void makeJump(Barline from, Barline to) {
		if (from != _jumps.get(_jump_index) || to != _jumps.get(_jump_index + 1)) {
			java.lang.System.err.print("Err! Wrong jump\n");
		}
		else {
			_jump_index += 2;
		}
	}
	/*
	public void setDuration(int duration) {
		_duration = duration;
	}
	
	public int getDuration() {
		return _duration;
	}
	
	public void setStartEventIndex(int i) {
		_start_event_i = i;
	}
	
	public void setEndEventIndex(int i) {
		_end_event_i = i;
	}
	
	public int getStartEventIndex() {
		return _start_event_i;
	}
	
	public int getEndEventIndex() {
		return _end_event_i;
	}
	*/
	
	public void flippedNextBlock() {
		_has_flipped_next_block = true;
	}
	
	public boolean hasFlippedNextBlock() {
		return _has_flipped_next_block;
	}
	
	public int getYOffset(System current) {
		Page start_page = _start_system.getParent();
		Page current_page = current.getParent();
		
		if (start_page == current_page) {
			return current.getBottom() - _start_system.getTop();
		}
		
		int offset = start_page.getLastSystem().getBottom() - _start_system.getTop();
		List<Page> pages = start_page.getParent().getPages();
		int current_page_index = pages.indexOf(current_page);
		
		for (int i = pages.indexOf(start_page)+1; i < current_page_index; i++) {
			offset += pages.get(i).getLastSystem().getBottom() -
					pages.get(i).getFirstSystem().getTop();
		}
		
		offset += current.getBottom() - current_page.getFirstSystem().getTop();
		
		return offset;
	}
	
	private java.awt.Image makeImage() {
		Page start_page = _start_system.getParent();
		Page end_page = _end_system.getParent();

		int top = _start_system.getTop();
		int bottom = _end_system.getBottom();

		if (start_page == end_page) {
			return start_page.getImage().crop(top, bottom);
		} else {
			Score score = start_page.getParent();
			List<Page> pages = score.getPages();
			int start_index = pages.indexOf(start_page);
			int end_index = pages.indexOf(end_page);

			List<Page> sectionPages = pages.subList(start_index, end_index + 1);
			return Image.MERGE(sectionPages, top, bottom);
		}
	}

	public java.awt.Image getImage() {
		
		return _image;
	}
	
	public int getWidth() {
		return _image.getWidth(null);
	}
	
	public int getHeight() {
		return _image.getHeight(null);
	}
	
	public System getStartSystem() {
		return _start_system;
	}
	
	public System getEndSystem() {
		return _end_system;
	}
	
	public List<System> getSystems() {
		return _systems;
	}
}
