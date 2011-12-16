package com.awesome.srpg.operation;

import com.awesome.srpg.strategy.Skill;

public interface AttackOperation extends Operation{

	int getTargetX();

	int getTargetY();

	Skill getSkill();

}
