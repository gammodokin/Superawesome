package com.awesome.game.base;

import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class AnimationManager {

	Queue<Animation> serialAnimes = new LinkedList<Animation>();
	Animation currentSAnime = null;

	List<Animation> parallelAnimes = new LinkedList<Animation>();

	public void startSerialAnimation(Animation anime) {
		serialAnimes.add(anime);
		nextSerialAnimation();
	}

	public void startParallelAnimation(Animation anime) {
		parallelAnimes.add(anime);
		anime.start();
	}

	public boolean allSerialAnimationFinished() {
		return currentSAnime == null && serialAnimes.isEmpty();
	}

	private void nextSerialAnimation() {
		if(!serialAnimes.isEmpty() && currentSAnime == null) {
			currentSAnime = serialAnimes.poll();
			currentSAnime.start();
		}
	}

	void update() {
		if(currentSAnime != null && currentSAnime.isDone()) {
			currentSAnime.dispose();
			currentSAnime = null;

			nextSerialAnimation();
		}
	}

}
