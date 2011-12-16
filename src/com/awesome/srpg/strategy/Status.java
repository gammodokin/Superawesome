package com.awesome.srpg.strategy;

import java.util.List;

import com.awesome.script.ActionScript;


public interface Status {

	String getName();
	String getTexName();

	UnitType getUnitType();
	UnitCorner getCorner();

	int getCurrentHp();
	int getCurrentMp();
	int getMaxHp();
	int getMaxMp();

	int getStr();
	int getInt();
	int getDef();
	int getReg();
	int getSpd();

	int getMov();

	List<Skill> getSkills();

	void setCharge(double c);

	boolean isDead();
	boolean isUsable(Skill skill);

	void damage(Damage d);
	void update(float delta);

	boolean isStandby();
	UnusualStatus getUnusualStatus();
}
