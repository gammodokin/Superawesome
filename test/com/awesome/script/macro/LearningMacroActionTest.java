package com.awesome.script.macro;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.awesome.script.ConfigedLine;
import com.awesome.script.dynamic.Agent;
import com.awesome.script.dynamic.Rule;
import com.awesome.script.macro.LearningMacroAction.Macro;
import com.awesome.script.macro.LearningMacroAction.ProbV;
import com.awesome.script.macro.LearningMacroAction.Record;
import com.awesome.test.PrivateAccessor;

public class LearningMacroActionTest {

	private static final Rule[] SOLDIER = {
		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_ENEMY),
		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_WIZARD),
		new Rule(ConfigedLine.DIRECT_ATTACK_CLOSEST_WIZARD),
		new Rule(ConfigedLine.TACKLE_CLOSEST_ENEMY),
		new Rule(ConfigedLine.INTENSE_STR_CLOSEST_FRIEND),
	},
	WIZARD = {
		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_ENEMY),
		new Rule(ConfigedLine.TACKLE_CLOSEST_ENEMY),
		new Rule(ConfigedLine.MAGICAL_SMASH_CLOSEST_ENEMY),
		new Rule(ConfigedLine.MAGIC_ATTACK_WEAKEST_ENEMY),
		new Rule(ConfigedLine.FIRE_BALL_CLOSEST_ENEMY),
		new Rule(ConfigedLine.MAGIC_HEAL_WEAKEST_FRIEND),
		new Rule(ConfigedLine.INTENSE_INT_CLOSEST_FRIEND_WIZARD),
		new Rule(ConfigedLine.INTENSE_INT_CLOSEST_FRIEND_WIZARD_2),
		new Rule(ConfigedLine.INTENSE_INT_CLOSEST_FRIEND_WIZARD_3),
		new Rule(ConfigedLine.INTENSE_STR_CLOSEST_FRIEND),
	};

	private PrivateAccessor pa;

	private static final float DELTA = 0.001f;

	@Before
	public void setUp() throws Exception {
		pa = new PrivateAccessor(LearningMacroAction.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testInitLearn() {
		LearningMacroAction lma1 = new LearningMacroAction(SOLDIER);
		lma1.initLearn();
		LearningMacroAction.ProbV p1 = pa.field(lma1, "p");

		checkP(p1);

		LearningMacroAction lma2 = new LearningMacroAction(WIZARD);
		lma2.initLearn();
		LearningMacroAction.ProbV p2 = pa.field(lma2, "p");
		checkP(p2);
	}

	private void checkP(LearningMacroAction.ProbV p) {
		double ave = 1.0 / p.length();
		for(int i = 0; i < p.length(); i++)
			assertEquals(p.get(i), ave, DELTA);

		double sum = 0.0;
		for(int i = 0; i < p.length(); i++)
			sum += p.get(i);

		assertEquals(sum, 1.0, DELTA);

		System.out.println(p);
	}

//	@Test
//	public void testInitActivate() {
//		fail("‚Ü‚¾ŽÀ‘•‚³‚ê‚Ä‚¢‚Ü‚¹‚ñ");
//	}

	@Test
	public void testExtractMacro() {
		LearningMacroAction lma = new LearningMacroAction(SOLDIER);
		ProbV p = lma.new ProbV(SOLDIER.length, 0);
		int[][] w = new int[][] {
				{0, 1, 0, 0, 1},
				{0, 1, 1, 0, 0},
				{0, 0, 1, 0, 0},
				{0, 1, 0, 0, 1},
				{1, 1, 1, 0, 1},
				{1, 1, 1, 0, 0},
		};

		Macro m = pa.method(lma, "extractMacro", p, w, SOLDIER.length, w.length, 3);
		assertEquals(m.getRules(), Arrays.asList(new Rule[]{SOLDIER[1], SOLDIER[2], SOLDIER[4]}));

		m = pa.method(lma, "extractMacro", p, w, SOLDIER.length, w.length, 4);
		assertEquals(m.getRules(), Arrays.asList(new Rule[]{SOLDIER[1], SOLDIER[2], SOLDIER[4], SOLDIER[0]}));
	}

	@Test
	public void testUpdateProbabilities() {
		LearningMacroAction lma = new LearningMacroAction(SOLDIER);
		ProbV p = lma.new ProbV(5, 1.0/5);

		Rule[] s0 = new Rule[] {SOLDIER[0], SOLDIER[2], SOLDIER[4]};
		Rule[] s1 = new Rule[] {SOLDIER[1], SOLDIER[2], SOLDIER[4]};
		Rule[] s2 = new Rule[] {SOLDIER[0], SOLDIER[2], SOLDIER[3]};

		MacroScript[] mss = new MacroScript[] {
				new SimpleMS(Arrays.asList(s0), 0.0),
				new SimpleMS(Arrays.asList(s1), 0.5),
				new SimpleMS(Arrays.asList(s2), 1.5),
		};

		int n = 3;
		int N = 3;
		double rho = 1.0;
		double alpha = 0.7;
		int Ne = 3;//(int)(rho * N);

		pa.method(lma, "updateProbabilities", p, n, mss, N, rho);

		ProbV p0 = lma.new ProbV(5, 1.0/5);
		double pd0 = 2.0 / Ne;
		pd0 = pd0 / (9 / Ne);
		double ans0 = (1 - alpha) * p0.get(0) + alpha * pd0;

		assertEquals("pd0 = " + pd0 +
				"\np0 = " + p.get(0), p.get(0), ans0, 0.001);

		double sum = 0;
		for(int i = 0; i < p.length(); i++)
			sum += p.get(i);

		assertEquals(sum, 1.0, 0.001);
	}

	@Test
	public void testSortLast() {
		LearningMacroAction lma = new LearningMacroAction(SOLDIER);

		SimpleMS e1 = new SimpleMS(2);
		SimpleMS e2 = new SimpleMS(1);
		SimpleMS e3 = new SimpleMS(0.02);
		SimpleMS e4 = new SimpleMS(4);
		SimpleMS e5 = new SimpleMS(-3);

		MacroScript[] mss = new MacroScript[] {
				e4,
				e5,
				e3,
				e1,
				e2,
		};

		assertEquals(Arrays.equals((MacroScript[])pa.method(lma, "sortLast", mss, 3), new MacroScript[] {e1, e2, e3}), true);


		mss = new MacroScript[] {
				e4,
				e5,
				e3,
				e1,
				e2,
				null,
				null,
				null,
				null,
				null,
				null,
		};

		assertEquals(Arrays.equals((MacroScript[])pa.method(lma, "sortLast", mss, 3), new MacroScript[] {e1, e2, e3}), true);

	}

	@Test
	public void testGetFitness() {
		LearningMacroAction lma = new LearningMacroAction(WIZARD);

		Rule[] s0 = new Rule[] {WIZARD[0], WIZARD[2], WIZARD[6], WIZARD[7], WIZARD[9]};
		MacroScript ms = new MacroScript(Arrays.asList(s0), 0);

		int hta = 30, h0a = 100, hts = 0, h0s = 100, D = 20;
		Agent a = new Agent(hta, h0a, D);
		Agent s = new Agent(hts, h0s, D);
		Record record = lma.new Record(a, s);

		List<Macro> l = new ArrayList<Macro>();

		double F = pa.method(lma, "getFitness", ms, record, l);
		double c = pa.field(lma, "c");

		double Fstr = 0.55 + 0.35 * hta / h0a;
		double Fdiv = 0;

		double Fans = Fstr + c * Fdiv;

		assertEquals(F, Fans, 0.001);

		lma = new LearningMacroAction(WIZARD);

		hta = 0; h0a = 100; hts = 50; h0s = 100; D = 20;
		a = new Agent(hta, h0a, D);
		s = new Agent(hts, h0s, D);
		record = lma.new Record(a, s);

		F = pa.method(lma, "getFitness", ms, record, l);

		Fstr = 0.1 * D / Agent.D_MAX + 0.1 * (1 - (double)hts / h0s);
		Fdiv = 0;

		Fans = Fstr + c * Fdiv;

		assertEquals(F, Fans, 0.001);
	}

	@Test
	public void testRouletteSelect() {
		LearningMacroAction lma = new LearningMacroAction(SOLDIER);
		ProbV p1 = lma.new ProbV(5, 0);
		p1.set(0, 0.01);
		p1.set(1, 0.2);
		p1.set(2, 0.2);
		p1.set(3, 0.09);
		p1.set(4, 0.5);

		int contain = 0;
		for(int i = 0; i < 100; i++) {
			List<Integer> list = pa.method(lma, "rouletteSelect", p1, 3);
			if(list.contains(4))
				contain++;
		}

		assertEquals(contain / 100.0 , 0.9, 0.1);

		contain = 0;
		for(int i = 0; i < 100; i++) {
			List<Integer> list = pa.method(lma, "rouletteSelect", p1, 1);
			if(list.contains(4))
				contain++;
		}

		assertEquals(contain / 100.0 , 0.5, 0.2);
	}

	@Test
	public void testGetFdiv() {
		LearningMacroAction lma = new LearningMacroAction(WIZARD);
		int K = pa.field(lma, "K");
		Rule[] s0 = new Rule[] {WIZARD[0], WIZARD[2], WIZARD[6], WIZARD[7], WIZARD[9]};
		Rule[] s1 = new Rule[] {WIZARD[1], WIZARD[3], WIZARD[4]};
		Rule[] s2 = new Rule[] {WIZARD[0], WIZARD[2], WIZARD[6]};

		MacroScript ms = new MacroScript(Arrays.asList(s0), 0);
		Macro m1 = lma.new Macro(s1);
		Macro m2 = lma.new Macro(s2);

		double fdiv = pa.method(lma, "getFdiv", ms, Arrays.asList(new Macro[] {m1}));
		assertEquals(fdiv, 8.0 / K, 0.001);

		fdiv = pa.method(lma, "getFdiv", ms, Arrays.asList(new Macro[] {m2}));
		assertEquals(fdiv, 2.0 / K, 0.001);

		fdiv = pa.method(lma, "getFdiv", ms, Arrays.asList(new Macro[] {m1, m2}));
		assertEquals(fdiv, (8.0 + 2.0) / K, 0.001);

		fdiv = pa.method(lma, "getFdiv", ms, Arrays.asList(new Macro[] {}));
		assertEquals(fdiv, 0, 0.001);
	}

	@Test
	public void testEval() {
		fail("‚Ü‚¾ŽÀ‘•‚³‚ê‚Ä‚¢‚Ü‚¹‚ñ");
	}

	@Test
	public void testGenerateScript() {
		fail("‚Ü‚¾ŽÀ‘•‚³‚ê‚Ä‚¢‚Ü‚¹‚ñ");
	}

	class SimpleMS extends MacroScript {

		private double f;

		public SimpleMS(double f) {
			super(new ArrayList<Integer>(), new Rule[0], 0);

			this.f = f;
		}

		public SimpleMS(List<Rule> rules, double f) {
			super(rules, 0);

			this.f = f;
		}

		@Override
		public void setF(double fitness) {
			this.f = fitness;
		}

		@Override
		public double getF() {
			return f;
		}

		@Override
		public String toString() {
			return "F=" + f;
		}

	}

}
