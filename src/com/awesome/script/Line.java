package com.awesome.script;

import java.io.Serializable;
import java.util.List;

import com.awesome.srpg.logic.Skill;
import com.awesome.srpg.object.Unit;

public interface Line extends Comparable<Line>, Serializable{

	double getPriority();

	boolean isUnderCondition(Unit actor, List<Unit> units, Unit[][] UnitMap);

	double getID();

	int getTargetX();
	int getTargetY();

	Skill getSkill();

	Condition getCond();
}
