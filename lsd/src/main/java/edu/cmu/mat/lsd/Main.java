package edu.cmu.mat.lsd;

import edu.cmu.mat.lsd.frames.Window;

public class Main {
	public static void main(String[] args) {
		Controller controller = new Controller();
		Model model = new Model(controller);
		new Window(model, controller);
	}
}
