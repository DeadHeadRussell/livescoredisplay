package edu.cmu.mat.scores;

import java.awt.Point;
import java.util.Map.Entry;

import edu.cmu.mat.scores.events.RepeatEndEvent;
import edu.cmu.mat.scores.events.RepeatStartEvent;

public class Repeat {

	public Repeat(Score score, Repeat repeat) {
		// TODO Auto-generated constructor stub
	}

	public Repeat(Score score, Barline start, Barline end) {
		// TODO Auto-generated constructor stub
	}

	public void normalize() {
		// TODO Auto-generated method stub
		
	}

	public void move(Object object, Barline intersect) {
		// TODO Auto-generated method stub
		
	}

	public ScoreObject getEndEvent() {
		// TODO Auto-generated method stub
		return null;
	}

	public void setActive(Point location) {
		// TODO Auto-generated method stub
		
	}

	public boolean setInactive() {
		// TODO Auto-generated method stub
		return false;
	}

	public void delete() {
		// TODO Auto-generated method stub
		
	}

	public boolean isActive() {
		// TODO Auto-generated method stub
		return false;
	}

/*
	if (other._repeats != null) {
		for (Entry<Integer, Integer> set : other._repeats.entrySet()) {
			_repeats.put(set.getKey(), set.getValue());
			Barline start_barline = getStartBarlines().get(set.getKey());
			RepeatStartEvent start_event = new RepeatStartEvent(
					start_barline);
			start_barline.addEvent(start_event);

			Barline end_barline = getEndBarlines().get(set.getValue());
			RepeatEndEvent end_event = new RepeatEndEvent(end_barline,
					start_event);
			end_barline.addEvent(end_event);

			start_event.setEnd(end_event);
		}
	}
	*/
}
