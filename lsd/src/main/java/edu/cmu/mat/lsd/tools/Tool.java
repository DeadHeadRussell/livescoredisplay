package edu.cmu.mat.lsd.tools;

import java.awt.FontMetrics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import edu.cmu.mat.lsd.components.JPage;
import edu.cmu.mat.scores.Barline;
import edu.cmu.mat.scores.Page;
import edu.cmu.mat.scores.ScoreObject;
import edu.cmu.mat.scores.Section;
import edu.cmu.mat.scores.System;
import edu.cmu.mat.scores.events.Event;
import edu.cmu.mat.scores.events.SectionStartEvent;

public abstract class Tool {

	public enum State {
		IDLE, HOVERING, DRAGGING, READY
	};

	public abstract boolean mouseClicked(Page page, MouseEvent event);

	public abstract boolean mousePressed(Page page, MouseEvent event);

	public abstract boolean mouseReleased(Page page, MouseEvent event);

	public abstract boolean mouseDragged(Page page, MouseEvent event);

	public abstract boolean mouseMoved(Page page, MouseEvent event);

	public abstract boolean mouseEntered(Page page, MouseEvent event);

	public abstract boolean mouseExited(Page page, MouseEvent event);

	public static ScoreObject GetIntersectedScoreObject(Page page,
			Point mouse_point) {
		System system = GetIntersectedSystem(page, mouse_point);
		Barline barline = GetIntersectedBarline(system, mouse_point);
		if (barline != null) {
			return barline;
		}
		if (system != null) {
			return system;
		}

		return GetIntersectedEvent(page, mouse_point);
	}

	public static System GetIntersectedSystem(Page page, Point mouse_point) {
		if (page == null) {
			return null;
		}

		for (System system : page.getSystems()) {
			if (system.intersects(mouse_point.y)) {
				return system;
			}
		}
		return null;
	}

	public static Barline GetIntersectedBarline(System system, Point mouse_point) {
		if (system == null) {
			return null;
		}

		for (Barline barline : system.getBarlines()) {
			if (barline.intersects(mouse_point.x)) {
				return barline;
			}
		}
		return null;
	}

	public static Barline GetIntersectedBarline(Page page, Point mouse_point) {
		System system = GetIntersectedSystem(page, mouse_point);
		return GetIntersectedBarline(system, mouse_point);
	}

	public static Barline GetLeftBarline(Page page, Point mouse_point) {
		System system = GetIntersectedSystem(page, mouse_point);
		if (system == null) {
			return null;
		}

		for (int i = system.getBarlines().size() - 1; i >= 0; i--) {
			Barline barline = system.getBarlines().get(i);
			if (barline.isLeft(mouse_point.x)) {
				return barline;
			}
		}
		return null;
	}

	public static Barline GetRightBarline(Page page, Point mouse_point) {
		System system = GetIntersectedSystem(page, mouse_point);
		if (system == null) {
			return null;
		}

		for (Barline barline : system.getBarlines()) {
			if (barline.isRight(mouse_point.x)) {
				return barline;
			}
		}
		return null;
	}

	public static Event GetIntersectedEvent(Page page, Point mouse_point) {
		if (JPage.FONT_METRICS == null) {
			return null;
		}

		for (System system : page.getSystems()) {
			for (Barline barline : system.getBarlines()) {
				int offset = -5;

				for (Event event : barline.getEvents()) {
					String text = "";

					switch (event.getType()) {
					case SECTION_START:
						Section section = ((SectionStartEvent) event)
								.getSection();
						text = section.getName() + " (";
						break;

					case SECTION_END:
						text = ")";
						break;

					case REPEAT_START:
						text = "|:";
						break;

					case REPEAT_END:
						text = ":|";
						break;

					default:
						continue;
					}

					FontMetrics metrics = JPage.FONT_METRICS;
					int string_width = metrics.stringWidth(text);
					int string_height = metrics.getHeight();

					int width = string_width + 6;
					int height = string_height + 6;

					int x = barline.getOffset() + offset + 5;
					int y = system.getTop() - height - 5;

					Rectangle rect = new Rectangle(x, y, width, height);
					if (rect.contains(mouse_point)) {
						return event;
					}

					offset += width + 5;
				}
			}
		}

		return null;
	}
}
