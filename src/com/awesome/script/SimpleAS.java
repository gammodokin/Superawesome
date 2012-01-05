package com.awesome.script;

import java.util.List;

import com.awesome.srpg.logic.MapSearch;
import com.awesome.srpg.logic.Skill;
import com.awesome.srpg.object.Unit;

public class SimpleAS implements ActionScript {

	@Override
	public Line selectRule(Unit actor, List<Unit> units, Unit[][] UnitMap) {
		Line rule = new SimpleRule();

		if(rule.isUnderCondition(actor, units, UnitMap))
			return rule;
		else
			return null;
	}

	class SimpleRule implements Line {

		Unit actor;
		Unit target;

		@Override
		public double getPriority() {
			// TODO 自動生成されたメソッド・スタブ
			return 0;
		}

		@Override
		public boolean isUnderCondition(Unit actor, List<Unit> units,
				Unit[][] UnitMap) {
			this.actor = actor;

			int minDist = Integer.MAX_VALUE;
			Unit closest = null;
			for(Unit u : units) {
				if(u == actor)
					continue;
				int dist = MapSearch.distanse(actor.getPosX(), actor.getPosY(), u.getPosX(), u.getPosY());
				if(dist < minDist) {
					minDist = dist;
					closest = u;
				}
			}

			target = closest;

			return true;
		}

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
			return actor.getStatus().getSkills().get(1);
		}

		@Override
		public int compareTo(Line o) {
			// TODO 自動生成されたメソッド・スタブ
			return 0;
		}

		@Override
		public double getID() {
			// TODO 自動生成されたメソッド・スタブ
			return 0;
		}

		@Override
		public Condition getCond() {
			// TODO 自動生成されたメソッド・スタブ
			return null;
		}


	}

}
