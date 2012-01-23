package com.awesome.script.macro;

import java.util.LinkedList;
import java.util.List;

import com.awesome.script.StaticScript;
import com.awesome.srpg.GameLogic;

public class PerfomMeasure {

	class Num {
		static final int RUN = 50;
		static final int BATTLE = LearningMacroAction.BATTLE_COUNT;
		static final int WON_RECORD = 100;
	}

	public static void main(String... args) {
		UnitIDLM.useGdx(false);

		List<Thread> ths = new LinkedList<Thread>();
		List<EpochRecorder> ers = new LinkedList<EpochRecorder>();

		for(int i = 0; i < 10; i++) {
			UnitIDLM.WIZ0.load();

			EpochRecorder er = new EpochRecorder(Num.WON_RECORD, UnitIDLM.WIZ0, new StaticScript(StaticScript.RULE_WIZARD));
			ers.add(er);

			GameLogic logic = new GameLogic(0, Num.BATTLE, er);

			Thread thread = new Thread(logic);
			thread.start();
			ths.add(thread);

			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}

			System.out.println("turning point : " + er.aveTurningPoint() +
					"\nD = " + er.distances() + "\n");
		}
	}

}