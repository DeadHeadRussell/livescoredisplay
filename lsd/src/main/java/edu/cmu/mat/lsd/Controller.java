package edu.cmu.mat.lsd;

import java.util.concurrent.LinkedBlockingQueue;

public class Controller {
	private LinkedBlockingQueue<ControllerListener> _listeners = new LinkedBlockingQueue<ControllerListener>();

	public void addListener(ControllerListener listener) {
		_listeners.add(listener);
	}

	public void removeListener(ControllerListener listener) {
		_listeners.remove(listener);
	}

	public void libraryPathUpdated() {
		for (ControllerListener listener : _listeners) {
			listener.onUpdateLibraryPath();
		}
	}

	public void modelUpdated() {
		for (ControllerListener listener : _listeners) {
			listener.onUpdateModel();
		}
	}

	public void scoreUpdated() {
		for (ControllerListener listener : _listeners) {
			listener.onUpdateScore();
		}
	}

	public void viewUpdated() {
		for (ControllerListener listener : _listeners) {
			listener.onUpdateView();
		}
	}

	public void toolUpdated() {
		for (ControllerListener listener : _listeners) {
			listener.onUpdateTool();
		}
	}

	public void programQuit() {
		for (ControllerListener listener : _listeners) {
			listener.onProgramQuit();
		}
	}
}
