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
		rules = copyLines(script.rules);
		Arrays.sort(rules);
	}

	@Override
	public Line selectRule(Unit actor, List<Unit> units, Unit[][] UnitMap) {
		for(Line r : rules) {
			if(r.isUnderCondition(actor, units, UnitMap))
				return r;
		}

		// ここには到達しないはず
		assert false;
		return null;
	}

	public static Line[] copyLines(Line[] lines) {
		Line[] rs = new Line[lines.length];
		for(int i = 0; i < lines.length; i++) {
			rs[i] = new ConfigedLine((ConfigedLine)lines[i]);
		}

		return rs;
	}

	public Line[] getLines() {
		return rules;
	}

}
