package com.awesome.script.macro;

import static org.junit.Assert.*;

import java.util.ArrayList;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.awesome.script.ConfigedLine;
import com.awesome.script.dynamic.Rule;
import com.awesome.test.PrivateAccessor;

public class LearningMacroActionTest {

	private static final Rule[] SOLDIER = {
		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_ENEMY),
		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_WIZARD),
		new Rule(ConfigedLine.DIRECT_ATTACK_CLOSEST_WIZARD),
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
		fail("‚Ü‚¾ŽÀ‘•‚³‚ê‚Ä‚¢‚Ü‚¹‚ñ");
	}

	@Test
	public void testUpdateProbabilities() {
		fail("‚Ü‚¾ŽÀ‘•‚³‚ê‚Ä‚¢‚Ü‚¹‚ñ");
	}

	@Test
	public void testSortLast() {
		LearningMacroAction lma = new LearningMacroAction(SOLDIER);

		class SimpleMS extends MacroScript {

			private double f;

			public SimpleMS(double f) {
				super(new ArrayList<Integer>(), new Rule[0], 0);

				this.f = f;
			}

			@Override
			public double getF() {
				return f;
			}

		}

		SimpleMS e1 = new SimpleMS(6);
		SimpleMS e2 = new SimpleMS(5);
		SimpleMS e3 = new SimpleMS(2);

		MacroScript[] mss = new MacroScript[] {
				e3,
				e1,
				new SimpleMS(0),
				new SimpleMS(1),
				e2,
		};

		assertEquals(pa.method(lma, "sortLast", mss, 3), new MacroScript[] {e1, e2, e3});

	}

	@Test
	public void testGetFitness() {
		fail("‚Ü‚¾ŽÀ‘•‚³‚ê‚Ä‚¢‚Ü‚¹‚ñ");
	}

	@Test
	public void testRouletteSelect() {
		fail("‚Ü‚¾ŽÀ‘•‚³‚ê‚Ä‚¢‚Ü‚¹‚ñ");
	}

	@Test
	public void testEval() {
		fail("‚Ü‚¾ŽÀ‘•‚³‚ê‚Ä‚¢‚Ü‚¹‚ñ");
	}

	@Test
	public void testGenerateScript() {
		fail("‚Ü‚¾ŽÀ‘•‚³‚ê‚Ä‚¢‚Ü‚¹‚ñ");
	}

}
