package edu.cmu.mat.scores;

public class PlaybackEvent {
	private Section _section;
	private Barline _start;
	private Barline _end;
	private int _duration;
	private boolean _is_section_start;

	public PlaybackEvent(Section section, Barline start, Barline end,
			int duration, boolean is_section_start) {
		_section = section;
		_start = start;
		_end = end;
		_duration = duration;
		_is_section_start = is_section_start;
	}

	public Section getSection() {
		return _section;
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
