package edu.cmu.mat.scores;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import javax.imageio.ImageIO;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import edu.cmu.mat.parsers.JsonParser;
import edu.cmu.mat.parsers.exceptions.CompilerException;
import edu.cmu.mat.scores.events.Event;
import edu.cmu.mat.scores.events.EventTypeAdapter;
import edu.cmu.mat.scores.events.SectionEndEvent;
import edu.cmu.mat.scores.events.SectionStartEvent;

public class Score implements ScoreObject {
	private static JsonParser PARSER = new JsonParser();
	public static Gson GSON = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation().setPrettyPrinting()
			.registerTypeAdapter(Event.class, new EventTypeAdapter()).create();

	// XXX: Sections will need some good way to be exported and imported with
	// GSON, since three locations, this score, bar events, and arrangements,
	// access them. We do not want imports to duplicate them.

	@Expose
	private String _name;
	@Expose
	private List<Section> _sections;
	@Expose
	private List<Page> _pages;

	@Expose
	private Arrangement _arrangement = new Arrangement(this);

	public Score(String name, List<Section> sections, List<Page> pages) {
		_name = name;
		_sections = sections;
		_pages = pages;
	}

	public Score(String name) {
		this(name, new LinkedList<Section>(), new ArrayList<Page>());
	}

	public Score(String name, List<Image> images) {
		this(name);
		for (Image image : images) {
			addPage(new Page(this, image));
		}
	}

	public Score(Score other, List<Image> images) {
		this(other.getName());
		for (int i = 0; i < other.getNumberPages(); i++) {
			addPage(new Page(this, other.getPage(i), images.get(i)));
		}

		for (Section section : other.getSections()) {
			Section new_section = new Section(this, section);
			addSection(new_section);
			Barline start = new_section.getStart();
			Barline end = new_section.getEnd();
			start.addEvent(new SectionStartEvent(start, new_section));
			end.addEvent(new SectionEndEvent(end, new_section));
		}

		// Initializing arrangements has to come after initializing the pages
		// and sections since arrangements relies on them to already exist.
		_arrangement = new Arrangement(this, other._arrangement);
	}

	public String getName() {
		return _name;
	}

	public int getNumberSections() {
		return _sections.size();
	}

	public Section getSection(int index) {
		return _sections.get(index);
	}

	public Section addSection(Barline start, Barline end) {
		if (start == null || end == null) {
			return null;
		}

		Section new_section = new Section(start, end);
		start.addEvent(new SectionStartEvent(start, new_section));
		end.addEvent(new SectionEndEvent(end, new_section));

		for (int i = 0; i < _sections.size(); i++) {
			Section section = _sections.get(i);
			if (compareLocation(section.getStart(), start) > 0) {
				_sections.add(i, new_section);
				return new_section;
			}
		}

		_sections.add(new_section);
		return new_section;
	}

	public void addSection(Section new_section) {
		for (int i = 0; i < _sections.size(); i++) {
			Section section = _sections.get(i);
			if (compareLocation(section.getStart(), new_section.getStart()) > 0) {
				_sections.add(i, new_section);
				return;
			}
		}

		_sections.add(new_section);
	}

	public List<Section> getSections() {
		return _sections;
	}

	public void removeSection(Section section) {
		if (section == null) {
			return;
		}
		_sections.remove(section);
		List<Event> events = new ArrayList<Event>();
		events.addAll(section.getStart().getEvents());
		events.addAll(section.getEnd().getEvents());

		for (int i = 0; i < events.size(); i++) {
			Event event = events.get(i);
			Section s = null;
			if (event.getType() == Event.Type.SECTION_START) {
				s = ((SectionStartEvent) event).getSection();
			} else if (event.getType() == Event.Type.SECTION_END) {
				s = ((SectionEndEvent) event).getSection();
			}

			if (s != null && s == section) {
				section.getStart().deleteChild(event);
				section.getEnd().deleteChild(event);
				break;
			}
		}
	}

	public void addPage(Page page) {
		_pages.add(page);
	}

	public int getNumberPages() {
		return _pages.size();
	}

	public Page getPage(int index) {
		return _pages.get(index);
	}

	public List<Page> getPages() {
		return _pages;
	}

	public void saveTo(File score_directory) {
		// XXX: Save images if any new were added.
		String json = GSON.toJson(this);
		File init_file = new File(score_directory, "init.json");
		try {
			FileWriter writer = new FileWriter(init_file);
			writer.write(json);
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	public static Score fromDirectory(File score_directory) throws IOException,
			CompilerException {
		File imagesDir = new File(score_directory.getAbsolutePath()
				+ File.separator + "images");
		File[] imageFiles = imagesDir.listFiles();

		List<Image> images = new ArrayList<Image>();
		if (imageFiles != null) {
			int number = 0;
			for (File image : imageFiles) {
				String name = image.getName();
				try {
					Integer.parseInt(name.substring(0, name.indexOf('.')));
					number++;
				} catch (Exception e) {
				}
			}

			images = Arrays.asList(new Image[number]);
			for (File image : imageFiles) {
				String name = image.getName();
				try {
					int index = Integer.parseInt(name.substring(0,
							name.indexOf('.'))) - 1;
					images.set(index, new Image(ImageIO.read(image)));
				} catch (Exception e) {
					java.lang.System.err.println(e);
				}
			}
		}

		File init_file = new File(score_directory, "init.json");
		return PARSER.parse(score_directory.getName(), init_file, images);
	}

	public ScoreObject getParent() {
		return null;
	}

	public Arrangement getArrangement() {
		return _arrangement;
	}

	public List<Section> getArrangementList() {
		List<Section> arrangement = _arrangement.getList();
		if (arrangement.size() == 0) {
			return getSections();
		}
		return arrangement;
	}

	public void saveArrangment(String string) {
		_arrangement.save(string);
	}

	private int compareLocation(Page page1, Page page2) {
		return _pages.indexOf(page1) - _pages.indexOf(page2);
	}

	private int compareLocation(System sys1, System sys2) {
		int loc = compareLocation(sys1.getParent(), sys2.getParent());
		if (loc == 0) {
			return sys1.getTop() - sys2.getTop();
		}
		return loc;
	}

	private int compareLocation(Barline bar1, Barline bar2) {
		int loc = compareLocation(bar1.getParent(), bar2.getParent());
		if (loc == 0) {
			return bar1.getOffset() - bar2.getOffset();
		}
		return loc;
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

	public void normalize() {
		// Does nothing.
	}

	public void delete() {
		// Does nothing.
	}

	public void deleteChild(ScoreObject child) {
		// Does nothing.
	}

	public List<Barline> getStartBarlines() {
		List<Barline> barlines = new LinkedList<Barline>();

		List<Page> pages = getPages();
		for (int page = 0; page < pages.size(); page++) {
			List<System> systems = pages.get(page).getSystems();
			for (int system = 0; system < systems.size(); system++) {
				List<Barline> system_barlines = systems.get(system)
						.getBarlines();
				for (int i = 0; i < system_barlines.size() - 1; i++) {
					barlines.add(system_barlines.get(i));
				}
			}
		}
		return barlines;
	}

	public List<Barline> getEndBarlines() {
		List<Barline> barlines = new LinkedList<Barline>();
		barlines.add(null);

		List<Page> pages = getPages();
		for (int page = 0; page < pages.size(); page++) {
			List<System> systems = pages.get(page).getSystems();
			for (int system = 0; system < systems.size(); system++) {
				List<Barline> system_barlines = systems.get(system)
						.getBarlines();
				for (int i = 1; i < system_barlines.size(); i++) {
					barlines.add(system_barlines.get(i));
				}
			}
		}
		return barlines;
	}

	public List<PlaybackEvent> createPlaybackEvents(String[] arrangement_string) {
		try {
			List<PlaybackEvent> events = new LinkedList<PlaybackEvent>();

			Map<String, List<PlaybackEvent>> section_map = new HashMap<String, List<PlaybackEvent>>();
			List<Barline> start_barlines = getStartBarlines();
			List<Barline> end_barlines = getEndBarlines();

			for (String section_string : arrangement_string) {
				String[] parts = section_string.split(",");
				String name = parts[0];

				int start = Integer.parseInt(parts[1]) / 4;
				int end = Integer.parseInt(parts[2]) / 4 + start;

				if (!section_map.containsKey(name)) {
					Barline start_barline = start_barlines.get(start);
					Barline end_barline = end_barlines.get(end);
					Section section = new Section(start_barline, end_barline);
					section.setName(name);

					List<PlaybackEvent> section_events = new ArrayList<PlaybackEvent>();
					int duration = 4;
					boolean is_first = true;
					for (int i = start; i < end; i++) {
						section_events.add(new PlaybackEvent(section,
								start_barlines.get(i), end_barlines.get(i + 1),
								duration, is_first));
						is_first = false;
					}
					section_map.put(name, section_events);
				}
				events.addAll(section_map.get(name));
			}

			return events;
		} catch (Exception e) {
			return null;
		}
	}
}
