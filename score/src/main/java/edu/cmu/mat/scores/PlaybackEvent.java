package edu.cmu.mat.scores;

public class PlaybackEvent {
	private Section _section;
	private int _pos;
	private Barline _start;
	private Barline _end;
	private int _duration;
	private boolean _is_section_start;

	public PlaybackEvent(Section section, int pos, Barline start, Barline end,
			int duration, boolean is_section_start) {
		_section = section;
		_pos = pos;
		_start = start;
		_end = end;
		_duration = duration;
		_is_section_start = is_section_start;
	}

	public Section getSection() {
		return _section;
	}

	public int getPos() {
		return _pos;
	}

	public Barline getStart() {
		return _start;
	}

	public Barline getEnd() {
		return _end;
	}

	public int getDuration() {
		return _duration;
	}

	public boolean isSectionStart() {
		return _is_section_start;
	}
}
