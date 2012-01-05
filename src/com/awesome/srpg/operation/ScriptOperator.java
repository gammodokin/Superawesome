package com.awesome.srpg.operation;

import java.util.List;

import com.awesome.script.ActionScript;
import com.awesome.script.Line;
import com.awesome.srpg.BattleRecord;
import com.awesome.srpg.logic.Coord;
import com.awesome.srpg.logic.MapSearch;
import com.awesome.srpg.logic.Skill;
import com.awesome.srpg.logic.Status;
import com.awesome.srpg.object.Unit;

public class ScriptOperator implements UnitOperator {

	protected Unit actor;
	private Status status;
	private ActionScript script;

	private List<Unit> units;

	private Unit[][] unitMap;

	protected Line rule;

	private MapSearch ms;

	private boolean passed = true;

	public ScriptOperator(ActionScript script) {
		this.script = script;
	}

	@Override
	public void destruct(BattleRecord result) {}

	private void init(Unit actor) {
		this.actor = actor;
		this.status = actor.getStatus();
	}

	protected void setScript(ActionScript script) {
		this.script = script;
	}

	protected boolean skillPassed() {
		return passed;
	}

	@Override
	public void updateEnvironment(Unit actor, List<Unit> units, Unit[][] unitMap) {
		init(actor);

		this.units = units;
		this.unitMap = unitMap;

		rule = script.selectRule(actor, units, unitMap);

		ms = new MapSearch(unitMap.length, unitMap[0].length);
		ms.setCurrentPos(actor.getPosX(), actor.getPosY());

		for(Unit u : units) {
			ms.plotObject(u.getPosX(), u.getPosY());
			if(u.getStatus().getCorner() == status.getCorner().enemy())
				ms.plotZOC(u.getPosX(), u.getPosY());
		}
	}

	@Override
	public MoveOperation operateMove() {
		int dist = MapSearch.distanse(actor.getPosX(), actor.getPosY(), rule.getTargetX(), rule.getTargetY());

		class DistPoint {

			int dist;
			int x;
			int y;

			DistPoint(int dist, int x, int y) {
				super();
				this.dist = dist;
				this.x = x;
				this.y = y;
			}

		}
//		int minDist = Integer.MAX_VALUE;
//		int minDistX = -1;
//		int minDistY = -1;
		DistPoint closestT = new DistPoint(Integer.MAX_VALUE, -1, -1);
		DistPoint closestA = new DistPoint(Integer.MAX_VALUE, -1, -1);

		Coord<Integer>[] cs = null;
		// ターゲットが攻撃射程内になければ
		if(dist > rule.getSkill().getRange().max) {
			// 最もターゲットに接近可能な位置を探して
			boolean movables[][] = new boolean[unitMap.length][unitMap[0].length];
			for(int x = 0; x < movables.length; x++)
				for(int y = 0; y < movables[x].length; y++) {
					movables[x][y] = ms.search(x, y, Integer.MAX_VALUE, Integer.MAX_VALUE);
					if(!movables[x][y])
						continue;

					int d = MapSearch.distanse(rule.getTargetX(), rule.getTargetY(), x, y);
					if(d < closestT.dist) {
						closestT.dist = d;
					}
				}

			if(closestT.dist != Integer.MAX_VALUE) {
				// そのなかで最も現在地から近い位置をピックアップ
				for(int x = 0; x < movables.length; x++)
					for(int y = 0; y < movables[x].length; y++) {
						if(!movables[x][y])
							continue;

						int dt = MapSearch.distanse(rule.getTargetX(), rule.getTargetY(), x, y);
						int da = MapSearch.distanse(actor.getPosX(), actor.getPosY(), x, y);
						if(dt == closestT.dist && da < closestA.dist) {
							closestA.dist = da;
							closestA.x = x;
							closestA.y = y;
						}
					}

				// 移動ルートの中で射程に入るまでできるだけ進む
				cs = ms.getWay(closestA.x, closestA.y);
				Coord<Integer> c = cs.length < status.getMov() ? cs[cs.length - 1] : cs[status.getMov() - 1];
				////			MapSearch tms = new MapSearch(unitMap.length, unitMap[0].length);
				//			for(int i = 0; i < cs.length && i < status.getMov(); i++) {
				////				tms.setCurrentPos(cs[i]);
				//				int d = MapSearch.distanse(rule.getTargetX(), rule.getTargetY(), cs[i].x, cs[i].y);
				//				// 射程に入るなら
				//				if(rule.getSkill().getRange().max <= d)
				////				if(tms.search(rule.getSkill().getRange(), null))
				//					c = cs[i];
				//			}
				closestT.x = c.x;
				closestT.y = c.y;
			}

//			for(MapSearch.Coord<Integer> coord : ms.getWay(minDistX, minDistY)) {
//				if(!ms.search(coord.x, coord.y, status.getMov(), Integer.MAX_VALUE))
//					continue;
//
//				int d = MapSearch.distanse(rule.getTargetX(), rule.getTargetY(), coord.x, coord.y);
//				if(d < moveMinDist) {
//					moveMinDist = d;
//					moveMinDistX = coord.x;
//					moveMinDistY = coord.y;
//				}
//			}
		}

		int cx, cy;
		if(closestT.x >= 0) {
			cx = closestT.x;
			cy = closestT.y;
		} else {
			cx = actor.getPosX();
			cy = actor.getPosY();
//			System.out.println("not Move");
		}

		assert !(closestT.x >= 0 && cs == null);

		return new SimpleMoveOp(cx, cy, cs);
	}

	@Override
	public AttackOperation operateAttack() {
		Skill skill;
		if(MapSearch.distanse(actor.getPosX(), actor.getPosY(), rule.getTargetX(), rule.getTargetY()) <= rule.getSkill().getRange().max) {
			skill = rule.getSkill();
			passed = false;
		} else {
			skill = Skill.PASS;
			passed = true;
		}

		return new SimpleAttackOp(rule.getTargetX(), rule.getTargetY(), skill);
	}

	private class SimpleMoveOp implements MoveOperation {

		private int cx, cy;
		private Coord<Integer>[] path;

		SimpleMoveOp(int cx, int cy, Coord<Integer>[] path) {
			this.cx = cx;
			this.cy = cy;
			this.path = path;
		}

		@Override
		public int getTargetX() { return cx; }

		@Override
		public int getTargetY() { return cy; }

		@Override
		public Coord<Integer>[] getPath() {
			return path;
		}

	}

	private class SimpleAttackOp implements AttackOperation {

		private int cx, cy;

		private Skill skill;

		SimpleAttackOp(int cx, int cy, Skill skill) {
			this.cx = cx;
			this.cy = cy;
			this.skill = skill;
		}

		@Override
		public int getTargetX() { return cx; }

		@Override
		public int getTargetY() { return cy; }

		@Override
		public Skill getSkill() { return skill; }

	}

}
