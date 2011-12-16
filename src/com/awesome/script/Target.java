package com.awesome.script;

import java.io.Serializable;
import java.util.LinkedList;
import java.util.List;

import com.awesome.srpg.object.Unit;
import com.awesome.srpg.strategy.MapSearch;
import com.awesome.srpg.strategy.UnitCorner;
import com.awesome.srpg.strategy.UnitType;

public enum Target implements Serializable {
	CLOSEST {
		@Override
		List<Unit> match(Unit actor, List<Unit> units) {
			if(units.isEmpty())
				return units;

			int minDist = Integer.MAX_VALUE;
			List<Unit> closests = new LinkedList<Unit>();

			for(Unit u : units) {
				int dist = MapSearch.distanse(actor.getPosX(), actor.getPosY(), u.getPosX(), u.getPosY());
				if(dist < minDist)
					minDist = dist;
			}

			for(Unit u : units) {
				int dist = MapSearch.distanse(actor.getPosX(), actor.getPosY(), u.getPosX(), u.getPosY());
				if(dist == minDist)
					closests.add(u);
			}

			assert !closests.isEmpty();
			return closests;
		}
	}, WAKEST {
		@Override
		List<Unit> match(Unit actor, List<Unit> units) {
			if(units.isEmpty())
				return units;

			int minHp = Integer.MAX_VALUE;
			List<Unit> wakests = new LinkedList<Unit>();

			for(Unit u : units) {
				int hp = u.getStatus().getCurrentHp();
				if(hp < minHp)
					minHp = hp;
			}

			for(Unit u : units) {
				int hp = u.getStatus().getCurrentHp();
				if(hp == minHp)
					wakests.add(u);
			}

			assert !wakests.isEmpty();
			return wakests;
		}
	}, FRIEND {
		@Override
		List<Unit> match(Unit actor, List<Unit> units) {
			UnitCorner corner = actor.getStatus().getCorner().friend();
			List<Unit> friends = new LinkedList<Unit>();

			for(Unit u : units)
				if(u.getStatus().getCorner() == corner)
					friends.add(u);

			assert !friends.isEmpty();
			return friends;
		}
	}, ENEMY {
		@Override
		List<Unit> match(Unit actor, List<Unit> units) {
			UnitCorner corner = actor.getStatus().getCorner().enemy();
			List<Unit> enemys = new LinkedList<Unit>();

			for(Unit u : units)
				if(u.getStatus().getCorner() == corner)
					enemys.add(u);

			assert !enemys.isEmpty();
			return enemys;
		}
	}, SOLDIER {
		@Override
		List<Unit> match(Unit actor, List<Unit> units) {
			UnitType type = UnitType.SOLDIER;
			List<Unit> soldiers = new LinkedList<Unit>();
			for(Unit u : units)
				if(u.getStatus().getUnitType() == type)
					soldiers.add(u);

			return soldiers;
		}
	}, WIZARD {
		@Override
		List<Unit> match(Unit actor, List<Unit> units) {
			UnitType type = UnitType.WIZARD;
			List<Unit> wizards = new LinkedList<Unit>();
			for(Unit u : units)
				if(u.getStatus().getUnitType() == type)
					wizards.add(u);

			return wizards;
		}
	};

	abstract List<Unit> match(Unit actor, List<Unit> units);
}
