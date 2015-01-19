package edu.cmu.mat.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import edu.cmu.mat.parsers.exceptions.CompilerException;
import edu.cmu.mat.scores.Image;
import edu.cmu.mat.scores.Score;

public class JsonParser implements Parser {
	public Score parse(String name, File score_file, List<Image> images)
			throws CompilerException {
		if (score_file.exists()) {
			Score json = Score.GSON.fromJson(GET_TEXT(score_file), Score.class);

			if (json.getNumberPages() != images.size()) {
				// XXX: Number of pages in the saved score differs from number
				// of image files.
				// XXX: Show dialog box asking user to either quit and deal with
				// images, or create a new score.
			} else {
				return new Score(json, images);
			}
		}

		return new Score(name, images);
	}

	private static String GET_TEXT(File score_file) {
		String json_text = "";

		try {
			FileReader reader = new FileReader(score_file);
			int c;
			while ((c = reader.read()) != -1) {
				json_text += (char) c;
			}

			reader.close();
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}

		return json_text;
	}
}
