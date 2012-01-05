package com.awesome.srpg.operation;

import java.util.List;

import com.awesome.srpg.BattleRecord;
import com.awesome.srpg.logic.Coord;
import com.awesome.srpg.object.Unit;

public class SimpleMoveOperator implements UnitOperator {

	Unit actor;

	List<Unit> units;

	Unit[][] unitMap;

	@Override
	public void destruct(BattleRecord result) {
		// TODO 自動生成されたメソッド・スタブ

	}
	public SimpleMoveOperator(Unit actor) {
		this.actor = actor;
	}

	@Override
	public void updateEnvironment(Unit actor, List<Unit> units, Unit[][] unitMap) {
		this.units = units;
		this.unitMap = unitMap;
	}

	@Override
	public MoveOperation operateMove() {
		return new MoveOperation(){

			@Override
			public int getTargetX() {
				return actor.getPosX();
			}

			@Override
			public int getTargetY() {
				return actor.getPosY() + 1;
			}

			@Override
			public Coord<Integer>[] getPath() {
				// TODO 自動生成されたメソッド・スタブ
				return null;
			}

		};
	}

	@Override
	public AttackOperation operateAttack() {
		// TODO 自動生成されたメソッド・スタブ
		return null;
	}


}
