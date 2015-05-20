package edu.cmu.mat.scores;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import com.google.gson.annotations.Expose;

public class Arrangement {
	private Score _score;

	private List<Section> _list = new ArrayList<Section>();
	@Expose
	private List<String> _order;

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

	public void save(String string) {
		String[] section_names = string.split("\\s+");
		_list = new ArrayList<Section>();
		_order = new ArrayList<String>();

		Set<Section> sections = _score.getSections();

		for (String section_name : section_names) {
			Section section = null;
			for (Section s : sections) {
				if (s.getName().equals(section_name)) {
					section = s;
					break;
				}
			}
			if (section != null) {
				_list.add(section);
				_order.add(section.getName());
			} else {
				java.lang.System.out.println("Could not find section: "
						+ section_name);
			}
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

	private void initList() {
		if (_list.size() == 0 && _order != null) {
			_list = new ArrayList<Section>(_order.size());
			for (String name : _order) {
				Section section = _score.getSectionByName(name);
				if (section != null) {
					_list.add(section);
				} else {
					java.lang.System.err
							.println("Could not find section named: " + name);
				}
			}
		}
	}
}
