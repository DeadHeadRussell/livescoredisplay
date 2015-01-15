package edu.cmu.mat.scores.events;

import edu.cmu.mat.scores.ScoreObject;

public abstract class Event implements ScoreObject {
	public enum Type {
		SECTION_START, SECTION_END, TIME_SIGNATURE, START_REPEAT, END_REPEAT
	};

	public abstract Type getType();
}
