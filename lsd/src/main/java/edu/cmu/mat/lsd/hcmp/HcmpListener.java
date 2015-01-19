package edu.cmu.mat.lsd.hcmp;

public interface HcmpListener {
	public Boolean handleNewPosition(int position);
	public Boolean handleNewTime(TimeMap time_map);
	public Boolean handlePlay();
	public Boolean handlePause();
	public Boolean handleStop();
	public Boolean handleNewArrangement(String[] arrangement_string);
}
