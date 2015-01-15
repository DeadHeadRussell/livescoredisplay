package edu.cmu.mat.scores;

import java.awt.Point;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import com.google.gson.annotations.Expose;

public class Page implements ScoreObject {
	private Score _parent;
	private Image _image;
	@Expose
	private List<System> _systems;

	public Page(Score parent, Image image) {
		this(parent, image, new ArrayList<System>());
	}

	public Page(Score parent, Image image, List<System> systems) {
		_parent = parent;
		_image = image;
		_systems = systems;
	}

	public Page(Score parent, Page other, Image image) {
		this(parent, image);

		for (System system : other.getSystems()) {
			addSystem(new System(this, system, _parent));
		}
	}

	public void addSystem(System system) {
		if (_systems == null) {
			_systems = new ArrayList<System>();
		}
		_systems.add(system);
	}

	public void deleteChild(ScoreObject child) {
		_systems.remove(child);
	}

	public void normalize() {
		Collections.sort(_systems, new Comparator<System>() {
			public int compare(System sys1, System sys2) {
				return sys1.getTop() - sys2.getTop();
			}
		});
	}

	public Image getImage() {
		return _image;
	}

	public List<System> getSystems() {
		return _systems;
	}

	public Score getParent() {
		return _parent;
	}

	public void move(Point distance, ScoreObject intersect) {
		// Does nothing.
	}

	public void setActive(Point location) {
		// Does nothing.
	}

	public void setInactive() {
		// Does nothing.
	}
}
