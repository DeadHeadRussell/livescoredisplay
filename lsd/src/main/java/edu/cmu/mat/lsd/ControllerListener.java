package edu.cmu.mat.lsd;

public interface ControllerListener {
	public void onUpdateLibraryPath();

	public void onUpdateModel();
	
	public void onUpdateScore();

	public void onUpdateView();

	public void onUpdateTool();

	public void onProgramQuit();
}
