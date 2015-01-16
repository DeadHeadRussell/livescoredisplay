package edu.cmu.mat.scores;

import java.awt.Image;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;

import com.google.gson.annotations.Expose;

import edu.cmu.mat.scores.events.SectionEndEvent;
import edu.cmu.mat.scores.events.SectionStartEvent;

public class Section {
	@Expose
	private String _name = "  ";
	@Expose
	private int _start_index;
	@Expose
	private int _end_index;

	private Barline _start;
	private Barline _end;
	private int _state = NOT_ACTIVE;

	public static final int NOT_ACTIVE = 0;
	public static final int ACTIVE = 1;

	public Section(Barline start, Barline end) {
		_start = start;
		_end = end;

		Score score = _start.getParent().getParent().getParent();
		List<Barline> start_barlines = score.getStartBarlines();
		List<Barline> end_barlines = score.getEndBarlines();
		_start_index = start_barlines.indexOf(_start);
		_end_index = end_barlines.indexOf(_end);

		_start.addEvent(new SectionStartEvent(_start, this));
		_end.addEvent(new SectionEndEvent(_end));
	}

	public Section(Score score, Section other) {
		_name = other._name;
		_start_index = other._start_index;
		_end_index = other._end_index;

		List<Barline> start_barlines = score.getStartBarlines();
		List<Barline> end_barlines = score.getEndBarlines();
		_start = start_barlines.get(_start_index);
		_end = end_barlines.get(_end_index);
	}

	public void setName(String name) {
		_name = name;
	}

	public void setState(int state) {
		_state = state;
	}

	public void move(Point distance, ScoreObject intersect) {
		if (intersect != null && intersect.getClass() == Barline.class) {
			Barline barline = (Barline) intersect;
			Score score = _start.getParent().getParent().getParent();
			score.removeSection(this);
			_start = barline;
			score.addSection(this);
		}
	}

	public void setActive(Point location) {
		setState(ACTIVE);
	}

	public void setInactive() {
		setState(NOT_ACTIVE);
	}

	public Barline getStart() {
		return _start;
	}

	public Barline getEnd() {
		return _end;
	}

	public String getName() {
		return _name;
	}

	public int getState() {
		return _state;
	}

	public Image getSystemsImage() {
		Score score = _start.getParent().getParent().getParent();

		Page start_page = _start.getParent().getParent();
		Page end_page = _end.getParent().getParent();
		int start_page_number = score.getPages().indexOf(start_page);
		int end_page_number = score.getPages().indexOf(end_page);

		System start_system = _start.getParent();
		System end_system = _end.getParent();

		// XXX: Only support sections within one page for now.
		if (end_page_number == start_page_number) {
			Image image = start_page.getImage().getImage();
			return cropSingle(image, start_system.getTop(),
					end_system.getBottom());
		} else {
			return mergeMultiple(score, start_page_number, end_page_number,
					start_system.getTop(), end_system.getBottom());
		}
	}

	private Image cropSingle(Image image, int top, int bottom) {
		int width = image.getWidth(null);
		int height = bottom - top;

		BufferedImage cropped = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		// XXX: Minus 8 is a hack since the coordinates are off between the
		// image and the frame that the image is displayed in in notation mode.
		top = Math.max(top - 8, 0);
		cropped.getGraphics().drawImage(image, 0, 0, width, height, 0, top,
				width, top + height, null);
		return cropped;
	}

	private Image mergeMultiple(Score score, int start_page_number,
			int end_page_number, int system_top, int system_bottom) {
		List<Image> images = new ArrayList<Image>(end_page_number
				- start_page_number + 1);
		int height = 0;
		for (int i = start_page_number; i < end_page_number + 1; i++) {
			Image image = score.getPage(i).getImage().getImage();
			int top = 0;
			int bottom = image.getHeight(null);
			if (i == start_page_number) {
				top = system_top;
			} else if (i == end_page_number) {
				bottom = system_bottom;
			}
			height += (bottom - top);
			images.add(cropSingle(image, top, bottom));
		}
		int width = images.get(0).getWidth(null);
		BufferedImage merged = new BufferedImage(width, height,
				BufferedImage.TYPE_INT_ARGB);
		int y = 0;
		for (Image image : images) {
			int image_height = image.getHeight(null);
			merged.getGraphics().drawImage(image, 0, y, width, image_height, 0,
					0, width, image_height, null);
			y += height;
		}
		return merged;
	}

	public void normalize() {
		// Does nothing.
	}

	public void deleteChild(ScoreObject child) {
		// Does nothing.
	}

	public static Image GET_IMAGE(Section section, Section end) {
		// TODO Auto-generated method stub
		return null;
	}

	public Point getTopLeft() {
		// TODO Auto-generated method stub
		return null;
	}

	public Point getBottomRight() {
		// TODO Auto-generated method stub
		return null;
	}
}
