package edu.cmu.mat.scores.events;

import edu.cmu.mat.scores.ScoreObject;

public abstract class Event implements ScoreObject {
	public enum Type {
		SECTION_START, SECTION_END, TIME_SIGNATURE, REPEAT_START, REPEAT_END
	};

	public abstract Type getType();

	public abstract boolean isActive();
}
