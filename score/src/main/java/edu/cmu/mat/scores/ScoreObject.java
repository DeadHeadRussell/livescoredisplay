package edu.cmu.mat.scores;

import java.awt.Point;

public interface ScoreObject {
	public void move(Point distance, ScoreObject intersect);

	public void delete();

	public void deleteChild(ScoreObject child);

	public void setActive(Point location);

	public void setInactive();

	public ScoreObject getParent();

	public void normalize();

}
