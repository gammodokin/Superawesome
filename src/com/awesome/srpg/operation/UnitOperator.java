package com.awesome.srpg.operation;

import java.util.List;

import com.awesome.srpg.BattleRecord;
import com.awesome.srpg.object.Unit;

public interface UnitOperator {

	void updateEnvironment(Unit actor, List<Unit> units, Unit[][] unitMap);

	MoveOperation operateMove();

	AttackOperation operateAttack();

	void destruct(BattleRecord result);

}
