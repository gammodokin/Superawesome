package com.awesome.script.macro;

import java.util.List;

import com.awesome.srpg.BattleRecord;
import com.awesome.srpg.object.Unit;
import com.awesome.srpg.operation.ScriptOperator;

public class LearnMacroOp extends ScriptOperator {

	private LearningMacroAction lma;
	private MacroScript script;


	public LearnMacroOp(UnitIDLM unitId) {
//		super(null);
		this(unitId.getLearningMacroAction());

//		this.lma = unitId.getLearningMacroAction();
//		lma.initActivate();
//		script = lma.generateScript();
//		setScript(script);
	}

	public LearnMacroOp(LearningMacroAction lma) {
		super(null);

		this.lma = lma;
		lma.initActivate();
		script = lma.generateScript();
		setScript(script);
	}

	private int turn = 0;
	@Override
	public void updateEnvironment(Unit actor, List<Unit> units, Unit[][] unitMap) {
		super.updateEnvironment(actor, units, unitMap);

		if(!skillPassed())	// 前のターンにスキル実行していたらターン進める
			turn++;

		rule = script.selectRule(actor, units, unitMap, turn);

	}

	@Override
	public void destruct(BattleRecord result) {

		LearningMacroAction.Record rec = lma.new Record(result.getAgent(actor), result.getNotLearners().get(0));
		lma.eval(script, rec);

		super.destruct(result);
	}

}
