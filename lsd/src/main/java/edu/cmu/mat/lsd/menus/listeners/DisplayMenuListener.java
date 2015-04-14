package edu.cmu.mat.lsd.menus.listeners;

import java.io.File;
import java.io.IOException;

import edu.cmu.mat.parsers.exceptions.CompilerException;

public interface DisplayMenuListener {

	void onNewScore(String score_name, File[] images) throws IOException,
			CompilerException;

	void onSetPath(File file) throws IOException;

	void quit();
}
