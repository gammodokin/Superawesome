package com.awesome.srpg.operation;

import com.awesome.srpg.logic.Skill;

public interface AttackOperation extends Operation{

	int getTargetX();

	int getTargetY();

	Skill getSkill();

}
