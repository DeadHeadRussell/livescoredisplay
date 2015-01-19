package edu.cmu.mat.lsd.hcmp;

import edu.cmu.mat.scores.Score;

public interface HcmpMessenger {
	public void join();
	public void synchronize();
	public void sendArrangement(Score score);
}
