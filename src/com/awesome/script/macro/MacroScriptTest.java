package com.awesome.script.macro;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.awesome.game.base.Screen;
import com.awesome.script.ConfigedLine;
import com.awesome.script.dynamic.Rule;
import com.awesome.srpg.logic.Status;
import com.awesome.srpg.object.Stage;
import com.awesome.srpg.object.Unit;
import com.awesome.srpg.operation.UnitOperator;
import com.awesome.test.PrivateAccessor;

public class MacroScriptTest {

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

	@Before
	public void setUp() throws Exception {
		pa = new PrivateAccessor(MacroScript.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testFiredInPhase() {
		fail("‚Ü‚¾ŽÀ‘•‚³‚ê‚Ä‚¢‚Ü‚¹‚ñ");
	}

	@Test
	public void testIsInLearningPhase() {
		List<Rule> rules = new ArrayList<Rule>();
		MacroScript script = new MacroScript(rules, 0);

		int turn = 0;
		int opening = 3;
		int learning = 0;

		boolean b = pa.method(script, "isInLearningPhase", turn, opening, learning);
		assertEquals(b, true);

		turn = 2;
		b = pa.method(script, "isInLearningPhase", turn, opening, learning);
		assertEquals(b, true);

		turn = 3;
		b = pa.method(script, "isInLearningPhase", turn, opening, learning);
		assertEquals(b, false);

		turn = 0;
		learning = 1;
		b = pa.method(script, "isInLearningPhase", turn, opening, learning);
		assertEquals(b, false);

		turn = 2;
		b = pa.method(script, "isInLearningPhase", turn, opening, learning);
		assertEquals(b, false);

		turn = 3;
		b = pa.method(script, "isInLearningPhase", turn, opening, learning);
		assertEquals(b, true);
	}

}
