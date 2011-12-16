package com.awesome.script.dynamic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.awesome.script.ActionScript;
import com.awesome.script.ConfigedLine;
import com.awesome.script.Line;
import com.awesome.srpg.SRPG;
import com.awesome.srpg.object.Unit;

public class DynamicScript implements ActionScript {

//	private Line[] rules;
	private Set<Line> lineSet = new LinkedHashSet<Line>();
	private List<Line> lines;
	protected Map<Line, Rule> ruleMap = new HashMap<Line, Rule>();

	public DynamicScript() {
//		Arrays.sort(rules);
//		System.out.println(Arrays.toString(rules));
	}

	boolean insertLine(Rule rule) {
		if(lineSet.contains(rule.getLine()))
			return false;
		lineSet.add(rule.getLine());

		ruleMap.put(rule.getLine(), rule);

		return true;
	}

	void finishScript() {
		lineSet.add(ConfigedLine.DIRECT_ATTACK_CLOSEST_ENEMY);
		lines = new ArrayList<Line>(lineSet);
		Collections.sort(lines);

		if(SRPG.CONSOLE_VIEW)
			System.out.println(lines);
	}

	@Override
	public Line selectRule(Unit actor, List<Unit> units, Unit[][] UnitMap) {
		for(Line l : lineSet) {	// TODO linesÇ∂Ç·Ç»Ç≠ÇƒÇ¢Ç¢ÇÃÇ©ÅH
			if(l.isUnderCondition(actor, units, UnitMap)) {
				if(l != ConfigedLine.DIRECT_ATTACK_CLOSEST_ENEMY)	// Ç±ÇÃçsìÆÇ…ÇÕï]âøÇ™Ç¢ÇÁÇ»Ç¢
					ruleMap.get(l).activate();
				return l;
			}
		}

		// Ç±Ç±Ç…ÇÕìûíBÇµÇ»Ç¢ÇÕÇ∏
		assert false;
		return null;
	}

	public boolean contains(Rule rule) {
		return ruleMap.containsValue(rule);
	}

}
