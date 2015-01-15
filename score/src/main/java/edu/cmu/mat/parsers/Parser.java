package edu.cmu.mat.parsers;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.List;

import edu.cmu.mat.parsers.exceptions.CompilerException;
import edu.cmu.mat.scores.Image;
import edu.cmu.mat.scores.Score;

public interface Parser {
	public Score parse(String name, File score, List<Image> images)
			throws CompilerException, FileNotFoundException, IOException;
}
