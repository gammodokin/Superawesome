package com.awesome.srpg.logic;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Queue;

import com.awesome.srpg.BattleRecord;
import com.awesome.srpg.SRPG;
import com.awesome.srpg.object.Unit;
import com.awesome.srpg.operation.AttackOperation;
import com.awesome.srpg.operation.MoveOperation;
import com.awesome.srpg.operation.UnitOperator;

public class UnitManager {

	private List<Unit> unitList = new LinkedList<Unit>();
	private List<Unit> allUnits = new LinkedList<Unit>();

	private StageCell[][] cells;

	private Unit[][] unitMap;

	private Unit currentUnit;

	// TODO 学習用ログを別のクラスにまとめる
	private Map<Unit, Integer> unitDeathTime = new HashMap<Unit, Integer>();

	private boolean isDone = false;

	public UnitManager(StageCell[][] cells) {
		this.cells = cells;
		unitMap = new Unit[cells.length][cells[0].length];
	}

	private void updateUnitMap(Unit unit) {
		unitMap[unit.getPosX()][unit.getPosY()] = unit;
	}

	public Unit getCurrentUnit() {
		return currentUnit;
	}

	public void addUnit(Unit unit) {
		unitList.add(unit);
		allUnits.add(unit);
		updateUnitMap(unit);
	}

	public boolean isMovable(int cx, int cy) {
		return unitMap[cx][cy] == null;
	}

	public boolean isDone() {
		return isDone;
	}

	private Queue<Unit> activatedUnits = new LinkedList<Unit>();
	private int timeInGame = 0;

	private static final float LIMIT = 0.0f;
	private float time = -1;
	public void scanStandbyUnit(float delta) {
		if(SRPG.VIEW) {
			if(!SRPG.getRenderer().getAnimationManager().allSerialAnimationFinished()) {
				//				System.out.println("animation");
				return;
			}

			if(time != -1) {
				time += delta;
				if(time < LIMIT)
					return;
				time = 0;
			} else {
				time = 0;
			}
		}

		do {
			timeInGame++;

			checkDeath(unitList);

			for(UnitCorner c : UnitCorner.values())
				if(isGameSet(c)) {
					isDone = true;
					return;
				}
//			List<Unit> unitListClone = new LinkedList<Unit>(unitList);

			while(activatedUnits.isEmpty()) {
				for(Unit unit : unitList) {
					if(unit.isDead())
						continue;

					unit.turnUpdate();

					if(unit.isStandby()) {
						activatedUnits.add(unit);
					}
				}
			}

			currentUnit = activatedUnits.poll();
			if(!currentUnit.isDead() && currentUnit.isStandby()) {

				if(SRPG.CONSOLE_VIEW)
					System.out.println("\n" + currentUnit + " Standby.");

				UnitOperator op = currentUnit.getOperater();

				op.updateEnvironment(currentUnit, unitList, unitMap);
				MoveOperation mop = op.operateMove();
				move(currentUnit, mop.getTargetX(), mop.getTargetY(), mop.getPath());

				op.updateEnvironment(currentUnit, unitList, unitMap);
				AttackOperation aop = op.operateAttack();
				attack(currentUnit, aop.getSkill(), aop.getTargetX(), aop.getTargetY());

				checkDeath(unitList);
			}

		} while(!SRPG.VIEW);

	}

	public void dispose() {
		for(Unit u : unitList)
			u.dispose();
	}

	private void removeUnit(Unit unit, Iterator<Unit> itr) {
		itr.remove();
		unitMap[unit.getPosX()][unit.getPosY()] = null;
	}

	private void checkDeath(List<Unit> units) {
		for(Iterator<Unit> itr = units.iterator(); itr.hasNext();)
			execDeath(itr.next(), itr);
	}

	private void execDeath(Unit unit, Iterator<Unit> itr) {
		if(unit.isDead()) {// && unitList.contains(unit)) {
			unitDeathTime.put(unit, timeInGame);
			removeUnit(unit, itr);
			unit.kill();
		}
	}

	private boolean isGameSet(UnitCorner corner) {
		for(Unit u : unitList)
			if(u.getStatus().getCorner() != corner)
				return false;

		BattleRecord result = new BattleRecord(corner, allUnits, unitDeathTime);
		for(Unit u : allUnits)
			u.getOperater().destruct(result);

		System.out.println("Win !! : " + corner + "\n");
		return true;
	}

	/*
	private void doAction(List<Unit> actorList, boolean update) {
		List<Unit> actorListClone = new LinkedList<Unit>(actorList);
		List<Unit> unitListClone = new LinkedList<Unit>(unitList);

		for(Unit unit : actorListClone) {
			if(unit.isDead())
				continue;

			if(update)
				unit.turnUpdate();

			if(unit.isStandby()) {
				//				unitActivated = true;
				if(update)
					activatedUnits.add(unit);
				else
					activatedUnits.remove(unit);

				if(SRPG.CONSOLE_VIEW)
					System.out.println("\n" + unit + " Standby.");

				UnitOperator op = unit.getOperater();
				op.updateEnvironment(unit, unitList, unitMap);

				MoveOperation mop = op.operateMove();
				AttackOperation aop = op.operateAttack();

				move(unit, mop.getTargetX(), mop.getTargetY(), mop.getPath());

				attack(unit, aop.getSkill(), aop.getTargetX(), aop.getTargetY());

				//				checkDeath(unit);
				for(Unit u : unitListClone)
					checkDeath(u);

				for(UnitCorner c : UnitCorner.values())
					if(isGameSet(c)) {
						isDone = true;
						return;
					}
			}
		}
	}
	/*

	/*
	 * 絶対座標
	 */
	private void move(Unit actor, int cx, int cy, Coord<Integer>[] path) {//, int cx, int cy) {
		assert 0 <= cx && cx < cells.length && 0 <= cy && cy < cells[cx].length : "cx = " + cx + ", cy = " + cy;

		unitMap[actor.getPosX()][actor.getPosY()] = null;

		assert unitMap[cx][cy] == null;

		actor.setMove(cx, cy, path);
		actor.setPos(cx, cy);
		updateUnitMap(actor);

		if(SRPG.CONSOLE_VIEW)
			System.out.println("Move to : (" + cx + ", " + cy + ")");
	}

	private void attack(Unit actor, Skill skill, int cx, int cy) {
		Unit target = unitMap[cx][cy];

		if(target == null)
			return;

		Damage damage = skill.getDamage(actor.getStatus(), target.getStatus());

		actor.setAttack(cx, cy, skill, target, damage);
		actor.exertDamage(skill.getCost());
//		target.exertDamage(damage);

		if(SRPG.CONSOLE_VIEW)
			System.out.println("Attack : "+ skill + " to " + target + ", " + damage);
	}

}
