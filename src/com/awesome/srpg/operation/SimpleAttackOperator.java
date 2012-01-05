package com.awesome.srpg.operation;

import java.util.List;

import com.awesome.srpg.BattleRecord;
import com.awesome.srpg.logic.Coord;
import com.awesome.srpg.logic.Skill;
import com.awesome.srpg.logic.Status;
import com.awesome.srpg.object.Unit;

public class SimpleAttackOperator implements UnitOperator {

	private Unit actor;
	private Status status;

	private List<Unit> units;

	private Unit[][] unitMap;

	public SimpleAttackOperator(Unit actor) {
		this.actor = actor;
		this.status = actor.getStatus();
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
				return actor.getPosY();
			}

			@Override
			public Coord<Integer>[] getPath() {
				// TODO 自動生成されたメソッド・スタブ
				return null;
			}

		};
	}

	Unit target = null;

	@Override
	public AttackOperation operateAttack() {
		if(target == null)
			for(Unit u : units)
				if(u != actor) {
					target = u;
					break;
				}

		return new AttackOperation() {

			@Override
			public int getTargetX() {
				return target.getPosX();
			}

			@Override
			public int getTargetY() {
				return target.getPosY();
			}

			@Override
			public Skill getSkill() {
				return status.getSkills().get(1);
			}

		};
	}

	@Override
	public void destruct(BattleRecord result) {
		// TODO 自動生成されたメソッド・スタブ

	}


}
