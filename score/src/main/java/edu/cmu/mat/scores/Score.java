package edu.cmu.mat.scores;

import java.awt.Point;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import javax.imageio.ImageIO;
import javax.swing.DefaultListModel;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import edu.cmu.mat.parsers.JsonParser;
import edu.cmu.mat.parsers.exceptions.CompilerException;
import edu.cmu.mat.scores.events.Event;
import edu.cmu.mat.scores.events.EventTypeAdapter;

public class Score implements ScoreObject {
	private static JsonParser PARSER = new JsonParser();
	public static Gson GSON = new GsonBuilder()
			.excludeFieldsWithoutExposeAnnotation()
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
		// Initializing arrangements has to come after initializing the pages
		// since arrangements relies on _sections.
		_arrangement = new Arrangement(this, other.getArrangement());
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
		java.lang.System.out.println("Write: " + json);
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
			images = Arrays.asList(new Image[imageFiles.length]);
			for (File image : imageFiles) {
				String name = image.getName();
				int index = Integer.parseInt(name.substring(0,
						name.indexOf('.'))) - 1;
				images.set(index, new Image(ImageIO.read(image)));
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

	public void saveArrangment(DefaultListModel<String> list_model) {
		_arrangement.save(list_model);
	}

	public void loadArrangment(DefaultListModel<String> list_model) {
		_arrangement.load(list_model);
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

	public void deleteChild(ScoreObject child) {
		// Does nothing.
	}

	public List<Barline> getStartBarlines() {
		List<Barline> barlines = new LinkedList<Barline>();
		for (Page page : getPages()) {
			for (System system : page.getSystems()) {
				List<Barline> system_barlines = system.getBarlines();
				for (int i = 0; i < system_barlines.size() - 1; i++) {
					barlines.add(system_barlines.get(i));
				}
			}
		}
		return barlines;
	}

	public List<Barline> getEndBarlines() {
		List<Barline> barlines = new LinkedList<Barline>();
		for (Page page : getPages()) {
			for (System system : page.getSystems()) {
				List<Barline> system_barlines = system.getBarlines();
				for (int i = 1; i < system_barlines.size(); i++) {
					barlines.add(system_barlines.get(i));
				}
			}
		}
		return barlines;
	}
}
