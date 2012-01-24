package com.awesome.srpg;

import com.awesome.game.base.Screen;
import com.awesome.script.macro.EpochRecorder;

public class GameLogic implements Runnable {

	private final float DELTA_MIN;

	private final int BATTLE;

	private Screen screen;

	private EpochRecorder recorder;

	public GameLogic() {
		screen = new BattleScreen();

		DELTA_MIN = 1000.0f/SRPG.FPS;
		BATTLE = SRPG.BATTLE;
	}

	public GameLogic(float delta, int battle, EpochRecorder recorder) {
		screen = new BattleScreen(recorder);

		DELTA_MIN = delta;
		BATTLE = battle;
		this.recorder = recorder;
	}

	private int battleCount;

	@Override
	public void run() {

		float delta = 0;

		while(true) {
			long startTime = System.currentTimeMillis();

			screen.update(delta / 1000);

			Screen next = screen.nextScreen();
			if(next != screen) {
				if(SRPG.CONSOLE_VIEW)
					System.out.println("battle : " + battleCount);
				battleCount++;

				if(battleCount == BATTLE) {
//					SRPG.exit();
					break;
				}
			}
			screen = next;

			delta = System.currentTimeMillis() - startTime;

			if(delta < DELTA_MIN) {
				try {
					Thread.sleep((long) (DELTA_MIN - delta));
				} catch (InterruptedException e) {
					// TODO Ž©“®¶¬‚³‚ê‚½ catch ƒuƒƒbƒN
					e.printStackTrace();
				}

				delta = DELTA_MIN;
			}

//			System.out.println("delta time : " + (System.currentTimeMillis() - startTime));
		}

	}

}