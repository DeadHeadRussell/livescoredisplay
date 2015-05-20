package edu.cmu.mat.lsd.components;

import java.awt.Color;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.util.List;

import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.Border;

import edu.cmu.mat.lsd.Model;
import edu.cmu.mat.lsd.tools.Tool;
import edu.cmu.mat.scores.Barline;
import edu.cmu.mat.scores.Image;
import edu.cmu.mat.scores.Page;
import edu.cmu.mat.scores.System;
import edu.cmu.mat.scores.events.Event;
import edu.cmu.mat.scores.events.SectionStartEvent;

public class JPage extends JPanel {
	private static final long serialVersionUID = 4193873080878056943L;

	private static Border PAGE_BORDER = BorderFactory.createCompoundBorder(
			BorderFactory.createRaisedBevelBorder(),
			BorderFactory.createLoweredBevelBorder());

	private static int IMAGE_HEIGHT = 800;
	private static int IMAGE_WIDTH = 600;
	private static float IDEAL_ASPECT = IMAGE_HEIGHT / IMAGE_WIDTH;

	private static int BOX_HEIGHT = 16;
	private static int PAGE_LEFT = 8;
	private static int PAGE_RIGHT = 15;

	private static Color COLOR_DARK = new Color(100, 10, 140, 100);
	private static Color COLOR_MID = COLOR_DARK.brighter();
	private static Color COLOR_LIGHT = new Color(100, 10, 255, 30);
	private static Color COLOR_ACTIVE = new Color(10, 10, 140, 100);
	private static Color COLOR_BARLINE = Color.BLUE;
	private static Color COLOR_BAR_ACTIVE = COLOR_LIGHT;
	private static Color COLOR_BARLINE_ACTIVE = new Color(0, 0, 220, 80);
	private static Color COLOR_EVENT = new Color(200, 200, 200, 250);

	private Model _model;
	private JPage _jpage;
	private Page _page;

	public static FontMetrics FONT_METRICS = null;

	public JPage(Model model, Page page) {
		_model = model;
		_jpage = this;
		_page = page;

		Image image = page.getImage();
		float aspectRatio = image.getImage().getHeight(null)
				/ image.getImage().getWidth(null);
		if (aspectRatio >= IDEAL_ASPECT) {
			image.resize(IMAGE_HEIGHT, Image.DIMENSION_HEIGHT);
		} else {
			image.resize(IMAGE_WIDTH, Image.DIMENSION_WIDTH);
		}

		ImageIcon icon = new ImageIcon(image.getImage());
		JLabel imageLabel = new JLabel("", icon, JLabel.CENTER);
		imageLabel.setBorder(PAGE_BORDER);
		imageLabel.setVerticalAlignment(JLabel.TOP);

		addMouseListener(new PageMouseListener(page));
		addMouseMotionListener(new PageMouseMotionListener(page));
		add(imageLabel);
	}

	@Override
	public void paint(Graphics graphics) {
		super.paint(graphics);

		if (FONT_METRICS == null) {
			FONT_METRICS = graphics.getFontMetrics();
		}

		List<System> systems = _page.getSystems();
		for (System system : systems) {
			drawSystem(graphics, system);

			List<Barline> barlines = system.getBarlines();
			for (int i = 0; i < barlines.size(); i++) {
				Barline barline = barlines.get(i);
				Barline next = null;
				if (i < barlines.size() - 1) {
					next = barlines.get(i + 1);
				}
				drawBarline(graphics, system, barline, next);

				List<Event> events = barline.getEvents();
				int offset = -5;

				for (Event event : events) {
					String label = "";

					switch (event.getType()) {
					case SECTION_START:
						SectionStartEvent sectionStart = ((SectionStartEvent) event);
						label = sectionStart.getSection().getName() + " (";
						break;

					case SECTION_END:
						label = ")";
						break;

					case REPEAT_START:
						label = "|:";
						break;

					case REPEAT_END:
						label = ":|";
						break;

					default:
						break;
					}

					offset = drawEvent(graphics, system, barline, label,
							event.isActive(), offset);
				}
			}
		}
	}

	private void drawSystem(Graphics graphics, System system) {
		int top = system.getTop();
		int bottom = system.getBottom();
		int height = bottom - top;

		int page_width = getWidth() - PAGE_RIGHT;

		graphics.setColor(COLOR_DARK);
		if (height > BOX_HEIGHT) {
			graphics.drawRect(PAGE_LEFT, top, page_width, height);
			if (system.getState() == System.ALL_ACTIVE) {
				graphics.setColor(COLOR_LIGHT);
				graphics.fillRect(PAGE_LEFT, top, page_width, height);
			}
		} else {
			graphics.drawRect(PAGE_LEFT, top, page_width, BOX_HEIGHT);
		}

		if (system.getState() == System.TOP_ACTIVE) {
			graphics.setColor(COLOR_ACTIVE);
		} else {
			graphics.setColor(COLOR_LIGHT);
		}
		graphics.fillRect(PAGE_LEFT, top, page_width, BOX_HEIGHT);
		graphics.setColor(COLOR_MID);
		graphics.drawLine(PAGE_LEFT, top + BOX_HEIGHT, page_width, top
				+ BOX_HEIGHT);

		if (system.getState() == System.BOTTOM_ACTIVE) {
			graphics.setColor(COLOR_ACTIVE);
			graphics.fillRect(PAGE_LEFT, bottom - 3, page_width, 6);
		}
	}

	private void drawBarline(Graphics graphics, System system, Barline barline,
			Barline next) {
		int top = system.getTop();
		int bottom = system.getBottom();
		int height = bottom - top;

		int offset = barline.getOffset();

		if (barline.getState() == Barline.ACTIVE) {
			if (next != null) {
				graphics.setColor(COLOR_BAR_ACTIVE);
				graphics.fillRect(offset + 2, top + 16, next.getOffset()
						- offset - 3, height - BOX_HEIGHT);
			}

			graphics.setColor(COLOR_BARLINE_ACTIVE);
			graphics.fillRect(offset - 1, top + 16, 3, height - BOX_HEIGHT);
		}
		graphics.setColor(COLOR_BARLINE);
		graphics.drawLine(offset, top + 16, offset, bottom);

	}

	private int drawEvent(Graphics graphics, System system, Barline barline,
			String text, boolean isActive, int offset) {
		int stringWidth = FONT_METRICS.stringWidth(text);
		int stringHeight = FONT_METRICS.getHeight();
		int width = stringWidth + 6;
		int height = stringHeight + 6;

		int x = barline.getOffset() + offset + 5;
		int y = system.getTop() - height - 5;
		offset += width + 5;

		graphics.setColor(COLOR_EVENT);
		graphics.fillRect(x, y, width, height);

		graphics.setColor(Color.BLACK);
		if (isActive) {
			graphics.setColor(Color.WHITE);
		}
		graphics.drawRect(x, y, width, height);
		graphics.drawString(text, x + 3, y + stringHeight);

		return offset;
	}

	private class PageMouseListener implements MouseListener {
		private Page _page = null;

		public PageMouseListener(Page page) {
			_page = page;
		}

		public void mouseClicked(MouseEvent event) {
			Tool tool = _model.getCurrentTool();
			if (tool == null) {
				return;
			}
			if (tool.mouseClicked(_page, event)) {
				_jpage.repaint();
			}
		}

		public void mouseEntered(MouseEvent event) {
			Tool tool = _model.getCurrentTool();
			if (tool == null) {
				return;
			}
			if (tool.mouseEntered(_page, event)) {
				_jpage.repaint();
			}
		}

		public void mouseExited(MouseEvent event) {
			Tool tool = _model.getCurrentTool();
			if (tool == null) {
				return;
			}
			if (tool.mouseExited(_page, event)) {
				_jpage.repaint();
			}
		}

		public void mousePressed(MouseEvent event) {
			Tool tool = _model.getCurrentTool();
			if (tool == null) {
				return;
			}
			if (tool.mousePressed(_page, event)) {
				_jpage.repaint();
			}
		}

		public void mouseReleased(MouseEvent event) {
			Tool tool = _model.getCurrentTool();
			if (tool == null) {
				return;
			}
			if (tool.mouseReleased(_page, event)) {
				_jpage.repaint();
			}
		}
	}

	private class PageMouseMotionListener implements MouseMotionListener {
		private Page _page = null;

		public PageMouseMotionListener(Page page) {
			_page = page;
		}

		public void mouseDragged(MouseEvent event) {
			Tool tool = _model.getCurrentTool();
			if (tool == null) {
				return;
			}
			if (tool.mouseDragged(_page, event)) {
				_jpage.repaint();
			}
		}

		public void mouseMoved(MouseEvent event) {
			Tool tool = _model.getCurrentTool();
			if (tool == null) {
				return;
			}
			if (tool.mouseMoved(_page, event)) {
				_jpage.repaint();
			}
		}
	}

}
