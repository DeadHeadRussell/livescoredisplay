package edu.cmu.mat.scores;

import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultListModel;

public class Arrangement {
	private Score _score;

	private List<Section> _list = new ArrayList<Section>();
	// @Expose
	private List<Integer> _order;

	public Arrangement(Score score) {
		_score = score;
	}

	public Arrangement(Score score, Arrangement other) {
		_score = score;
		_order = other._order;
		initList();
	}

	public List<Section> getList() {
		return _list;
	}

	public void save(DefaultListModel<String> list_model) {
		_list = new ArrayList<Section>(list_model.size());
		_order = new ArrayList<Integer>(list_model.size());
		for (int i = 0; i < list_model.size(); i++) {
			String section_name = list_model.get(i);
			Section section = null;
			for (Section s : _score.getSections()) {
				if (s.getName().equals(section_name)) {
					section = s;
					break;
				}
			}
			if (section != null) {
				_list.add(section);
				_order.add(_score.getSections().indexOf(section));
			} else {
				java.lang.System.out.println("Could not find section: "
						+ section_name);
			}
		}
	}

	public void load(DefaultListModel<String> list_model) {
		list_model.removeAllElements();
		initList();

		List<Section> list = _list;
		if (_list.size() == 0) {
			list = _score.getSections();
		}

		for (Section section : list) {
			list_model.addElement(section.getName());
		}
	}

	public double getNextBarlineBeat(double beat) {
		// XXX: All time signatures are 4/4 for now.
		return beat + 4 - (beat % 4);
		/*
		 * Section current_section = getSection(beat); if (current_section ==
		 * null) { return -1; } double section_beat = getSectionBeat(beat);
		 * double barline_beat = findNextBarlineBeat(current_section,
		 * section_beat); return (beat - section_beat) + barline_beat;
		 */
	}

	public Barline getBarline(double beat) {
		Section current_section = getSection(beat);
		if (current_section == null) {
			return null;
		}
		double section_beat = getSectionBeat(beat);
		return getBarline(current_section, section_beat);
	}

	private double findNextBarlineBeat(Section section, double section_beat) {
		int beat_tally = 0;
		return 0;
		// TODO when there are different time signatures.
	}

	private Barline getBarline(Section section, double beat) {
		// XXX: This is assuming 4/4 time signatures.
		int bar = (int) (beat / 4);
		int bar_tally = 0;
		System first_system = section.getStart().getParent();
		int first_index = first_system.getBarlines()
				.indexOf(section.getStart());

		bar_tally += first_system.getBarlines().size() - 1 - first_index;

		if (bar_tally > bar) {
			return first_system.getBarlines().get(first_index + bar);
		}

		Page first_page = first_system.getParent();
		int system_index = first_page.getSystems().indexOf(first_system);
		for (int i = system_index + 1; i < first_page.getSystems().size(); i++) {
			System system = first_page.getSystems().get(i);
			int last_tally = bar_tally;
			bar_tally += system.getBarlines().size() - 1;
			if (bar_tally > bar) {
				return system.getBarlines().get(bar - last_tally);
			}
		}

		int page_index = _score.getPages().indexOf(first_page);
		for (int i = page_index + 1; i < _score.getNumberPages(); i++) {
			Page page = _score.getPage(i);
			for (System system : page.getSystems()) {
				int last_tally = bar_tally;
				bar_tally += system.getBarlines().size() - 1;
				if (bar_tally > bar) {
					return system.getBarlines().get(last_tally + bar);
				}
			}
		}

		return null;
	}

	public Section getSection(double beat) {
		int beat_tally = 0;
		for (Section section : _list) {
			int next_beat = beat_tally + getTotalBeats(section);
			if (next_beat >= beat) {
				return section;
			}
			beat_tally = next_beat;
		}
		return null;
	}

	public int getSectionNumber(double beat) {
		int beat_tally = 0;
		for (int i = 0; i < _list.size(); i++) {
			int next_beat = beat_tally + getTotalBeats(_list.get(i));
			if (next_beat >= beat) {
				return i;
			}
			beat_tally = next_beat;
		}
		return -1;
	}

	private double getSectionBeat(double beat) {
		int beat_tally = 0;
		for (Section section : _list) {
			int next_beat = beat_tally + getTotalBeats(section);
			if (next_beat >= beat) {
				return beat - beat_tally;
			}
			beat_tally = next_beat;
		}
		return -1;
	}

	private int getTotalBeats(Section section) {
		List<Section> sections = _score.getSections();
		Section next = null;
		if (sections.indexOf(section) + 1 < sections.size()) {
			next = sections.get(sections.indexOf(section) + 1);
		}

		Barline first_barline = section.getStart();
		System first_system = first_barline.getParent();

		System last_system;
		Barline last_barline;

		if (next == null) {
			List<System> systems;
			int last_page = 0;
			do {
				last_page++;
				Page page = _score.getPage(_score.getNumberPages() - last_page);
				systems = page.getSystems();
			} while (systems.size() == 0);
			last_system = systems.get(systems.size() - 1);
			last_barline = last_system.getBarlines().get(
					last_system.getBarlines().size() - 1);
		} else {
			last_barline = next.getStart();
			last_system = last_barline.getParent();
		}

		int bar_count = 0;

		List<Barline> barlines = first_system.getBarlines();
		if (first_system == last_system) {
			bar_count += barlines.indexOf(last_barline)
					- barlines.indexOf(first_barline);
		} else {
			bar_count += barlines.size() - 1 - barlines.indexOf(first_barline);
			bar_count += last_system.getBarlines().indexOf(last_barline);

			Page first_page = first_system.getParent();
			Page last_page = last_system.getParent();

			List<System> systems = first_page.getSystems();
			int first_index = systems.indexOf(first_system) + 1;
			if (first_page == last_page) {
				int last_index = systems.indexOf(last_system);
				for (int i = first_index; i < last_index; i++) {
					bar_count += systems.get(i).getBarlines().size() - 1;
				}
			} else {
				for (int i = first_index; i < systems.size(); i++) {
					bar_count += systems.get(i).getBarlines().size() - 1;
				}

				List<Page> pages = _score.getPages().subList(
						_score.getPages().indexOf(first_page),
						_score.getPages().indexOf(last_page));

				for (Page page : pages) {
					for (System system : page.getSystems()) {
						bar_count += system.getBarlines().size() - 1;
					}
				}
			}
		}
		// XXX: This assumes all time signatures are 4/4.
		return bar_count * 4;
	}

	private void initList() {
		List<Section> sections = _score.getSections();
		if (_list.size() == 0 && _order != null && sections.size() != 0) {
			_list = new ArrayList<Section>(_order.size());
			for (Integer i : _order) {
				_list.add(_score.getSections().get(i));
			}
		}
	}
}
