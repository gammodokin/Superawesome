package com.awesome.script.macro;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import com.awesome.script.Line;
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
		UnitIDLM.WIZ0.load();

		StaticScript stat = new StaticScript(StaticScript.RULE_WIZARD);

		List<Thread> ths = new LinkedList<Thread>();
		List<EpochRecorder> ers = new LinkedList<EpochRecorder>();

		for(int i = 0; i < 50; i++) {
			UnitIDLM.WIZ0.altLoad();

			EpochRecorder er = new EpochRecorder(Num.WON_RECORD, UnitIDLM.WIZ0.getLearningMacroAction(), new StaticScript(stat));
			ers.add(er);

			GameLogic logic = new GameLogic(0, Num.BATTLE, er);

			Thread thread = new Thread(logic);
			ths.add(thread);
		}

		for(Thread thread : ths)
			thread.start();

		for(Thread thread : ths) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		List<Thread> sths = new LinkedList<Thread>();
		List<StaticRecorder> srs = new LinkedList<StaticRecorder>();

		for(EpochRecorder er : ers) {
			for(Line[] rules : er.collectMacro()) {
				StaticRecorder sr = new StaticRecorder(new StaticScript(StaticScript.copyLines(rules)), new StaticScript(stat));
				srs.add(sr);

				GameLogic logic = new GameLogic(0, 1, sr);

				Thread thread = new Thread(logic);
				sths.add(thread);
			}
		}

		for(Thread thread : sths)
			thread.start();

		for(Thread thread : sths) {
			try {
				thread.join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		List<StaticScript> scripts = new LinkedList<StaticScript>();
		double sum = 0;
		for(StaticRecorder sr : srs) {
			sum += sr.getVic() ? 1 : 0;
			if(!sr.getVic())
				scripts.add((StaticScript)sr.getLearner());
		}

		sum /= srs.size();

		SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd_HHmmss");
		File file = new File("res/log/macro/macroLog-" + sfd.format(new Date()) + ".txt");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		List<Double> tps = new ArrayList<Double>(ers.size());
		List<Double> dists = new ArrayList<Double>(ers.size());

		for(EpochRecorder er : ers) {
			pw.println(er.collectLog());

			double tp = er.aveTurningPoint();
			double d = er.distances();

			tps.add(tp);
			dists.add(d);

//			System.out.println("turning point : " + tp +
//					"\nD = " + d + "\n");
		}

		pw.println("\n--------- all ave -----------");
		pw.println("ave turning point : " + ave(tps));
		pw.println("ave D : " + ave(dists));

		pw.println("win rate : " + sum);

		pw.println("\nlose scripts : ");
		for(StaticScript s : scripts)
			pw.println(Arrays.toString(s.getLines()));

		pw.close();

		System.out.println("ave turning point : " + ave(tps));
		System.out.println("ave D : " + ave(dists));
		System.out.println("win rate : " + sum);
	}

	static <N extends Number> double ave(List<N> ds) {
		double sum = 0;
		for(Number d : ds)
			sum += d.doubleValue();
		sum /= ds.size();

		return sum;
	}

}
