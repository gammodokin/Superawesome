package com.awesome.script.macro;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import com.awesome.script.ActionScript;
import com.awesome.script.ConfigedLine;
import com.awesome.script.Line;
import com.awesome.script.dynamic.Rule;
import com.awesome.script.macro.LearningMacroAction.Macro;
import com.awesome.srpg.SRPG;
import com.awesome.srpg.object.Unit;

class MacroScript implements ActionScript{

	private static final int OPENING_TURN = 3;

	//		private DynamicScript ds;
	private Macro opening = null;
	private List<Rule> rules;
	private double F;

	private Set<Line> openingSet = new LinkedHashSet<Line>();
	private Set<Line> lineSet = new LinkedHashSet<Line>();
	private List<Line> openingLines = new ArrayList<Line>();
	private List<Line> lines = new ArrayList<Line>();

	private Map<Line, Rule> ruleMap = new HashMap<Line, Rule>();

	private Map<Rule, Boolean> activatedInPhase = new HashMap<Rule, Boolean>();

	private final int LEARNING_PHASE;

	private List<Line> usedLineLog = new LinkedList<Line>();

	//		Script(DynamicScript ds) {
	//			super();
	//			this.ds = ds;
	//		}

	MacroScript(List<Integer> rulei, Rule[] rule, int phase) {
		this(null, rulei, rule, phase);
	}

	MacroScript(Macro opening, List<Integer> rulei, Rule[] rule, int phase) {
		this.opening = opening;

		List<Rule> rules = new ArrayList<Rule>(rulei.size());
		for(int i : rulei)
			rules.add(rule[i]);

		this.rules = rules;

		LEARNING_PHASE = phase;

		finishScript();
	}

	MacroScript(List<Rule> rules, int phase) {
		this.rules = rules;

		LEARNING_PHASE = phase;

		finishScript();
	}

	private void finishScript() {
		if(opening != null)
			initLine(opening.getRules(), openingSet, openingLines);

		initLine(rules, lineSet, lines);
	}

	private void initLine(List<Rule> rules, Set<Line> lineSet, List<Line> lines) {
		for(Rule r : rules) {
			lineSet.add(r.getLine());
			ruleMap.put(r.getLine(), r);
		}

		lineSet.add(ConfigedLine.PASS);
		lines.addAll(lineSet);

		Collections.sort(lines);

		if(SRPG.CONSOLE_VIEW)
			System.out.println(lines);
	}

	public boolean contains(Rule rule) {
		return rules.contains(rule);
		//			return ds.contains(rule);
	}

	public boolean firedInPhase(Rule rule) {
		if(!activatedInPhase.containsKey(rule))
			return false;

		return activatedInPhase.get(rule);
	}

	public double getF() {
		return F;
	}

	public void setF(double fitness) {
		F = fitness;

//		System.out.println(activatedInPhase);
	}

	// â∫ÇÃä÷êîÇåƒÇ‘Ç◊Çµ
	@Override
	public Line selectRule(Unit actor, List<Unit> units, Unit[][] UnitMap) {
		return null;
	}

	// TODO Ç±ÇÃÇ÷ÇÒàÍÇ¬ÇÃBattleEnvironmentìIÇ»ÉNÉâÉXÇ≈Ç‹Ç∆ÇﬂÇÈ
	public Line selectRule(Unit actor, List<Unit> units, Unit[][] UnitMap, int turn) {
		Line resultL = null;
		if(opening != null && turn < OPENING_TURN)
			for(Line l : openingLines) {
				if(l.isUnderCondition(actor, units, UnitMap)) {
					resultL = l;
					break;
				}
			}
		else
			for(Line l : lines) {
				if(l.isUnderCondition(actor, units, UnitMap)) {
					if(isInLearningPhase(turn, OPENING_TURN, LEARNING_PHASE))
						activatedInPhase.put(ruleMap.get(l), true);
					resultL = l;
					break;
				}
			}

		assert resultL != null;
		return resultL;
	}

	private static boolean isInLearningPhase(int turn, int OPENING, final int LEARNING) {
		return (LEARNING == 0 && turn < OPENING)
			|| (LEARNING == 1 && turn >= OPENING);
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		for(Line l : lines)
			sb.append(firedInPhase(ruleMap.get(l)) + ", ");

		String str = "macro	: " + openingLines + "\n lines : " + lines + "\n fired : " + sb + "\n";
		return str;
	}

}