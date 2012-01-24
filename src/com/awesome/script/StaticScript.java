package com.awesome.script;

import java.util.Arrays;
import java.util.List;

import com.awesome.srpg.object.Unit;

public class StaticScript implements ActionScript {

	public static final Line[] RULE_SOLDIER = new Line[] {
		ConfigedLine.DIRECT_ATTACK_CLOSEST_ENEMY,
		ConfigedLine.DIRECT_ATTACK_WEAKEST_SOLDIER,
	}, RULE_WIZARD =
		new Line[] {
		ConfigedLine.DIRECT_ATTACK_CLOSEST_ENEMY,
		ConfigedLine.MAGIC_ATTACK_CLOSEST_ENEMY,
		ConfigedLine.MAGIC_HEAL_WEAKEST_FRIEND,
	};

	private Line[] rules;

	public StaticScript(Line[] rules) {
		this.rules = rules;
		Arrays.sort(rules);
	}

	public StaticScript(StaticScript script) {
		Line[] rs = new Line[script.rules.length];
		for(int i = 0; i < script.rules.length; i++) {
			rs[i] = new ConfigedLine((ConfigedLine)script.rules[i]);
		}

		rules = rs;
	}

	@Override
	public Line selectRule(Unit actor, List<Unit> units, Unit[][] UnitMap) {
		for(Line r : rules) {
			if(r.isUnderCondition(actor, units, UnitMap))
				return r;
		}

		// ‚±‚±‚É‚Í“ž’B‚µ‚È‚¢‚Í‚¸
		assert false;
		return null;
	}

}
