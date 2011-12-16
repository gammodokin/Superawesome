package com.awesome.script.dynamic;

import com.awesome.srpg.BattleRecord;
import com.awesome.srpg.operation.ScriptOperator;

public class DynamicScriptOp extends ScriptOperator {

	private DynamicScripting ds;
	private int lastTime = 0;

	public DynamicScriptOp(DynamicScripting ds) {
		super(null);

		this.ds = ds;
		ds.initActivate();

		setScript(ds.scriptGeneration());
	}



	@Override
	public void destruct(BattleRecord result) {

		ds.weightAdjustment(result.getAgent(actor), result.getLearnTeam());
//		new DynamicScripting.Team(won, N, cs, notg);
//		DynamicScripting.Agent a = new DynamicScripting.Agent(actor.getStatus().getMaxHp(), actor.getStatus().getCurrentHp(), lastTime);
//		ds.weightAdjustment(a, g);
//		super.destruct(won, time);
		super.destruct(result);
	}

}
