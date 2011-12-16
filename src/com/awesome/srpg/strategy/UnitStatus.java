package com.awesome.srpg.strategy;

import java.util.Arrays;
import java.util.List;


public class UnitStatus implements Status {

	public static Status createOwnSoldier() {
		return new UnitStatus("ソルジャー", "unit_own_soldier.png",			UnitType.SOLDIER,	UnitCorner.LEARNER,	100,	1,		10,	0,	10,	5,	10, 3, new Skill[]{Skill.PASS, Skill.NORMAL});
	}
	public static Status createOwnMagician() {
		return new UnitStatus("ウィザード", "unit_own_magician.png",		UnitType.WIZARD,	UnitCorner.LEARNER,	100,	100,	5,	10,	5,	10,	10, 3, new Skill[]{Skill.PASS, Skill.NORMAL, Skill.FIRE, Skill.HEAL});
	}
	public static Status createEnemySoldier() {
		return new UnitStatus("敵ソルジャー", "unit_enemy_soldier.png",		UnitType.SOLDIER,	UnitCorner.ENEMY,	100,	1,		10,	0,	10,	5,	10, 3, new Skill[]{Skill.PASS, Skill.NORMAL});
	}
	public static Status createEnemyMagician() {
		return new UnitStatus("敵ウィザード", "unit_enemy_magician.png",	UnitType.WIZARD,	UnitCorner.ENEMY,	100,	100,	10,	10,	5,	10,	10, 3, new Skill[]{Skill.PASS, Skill.NORMAL, Skill.FIRE, Skill.HEAL});
	}

	private String name, texName;

	private UnitType uType;

	private UnitCorner corner;

	private int hp, mp, maxHp, maxMp;

	private int str, inte, def, reg, spd, mov;

	private List<Skill> skills;

	private double charge, maxCharge;

	private UnusualStatus us = UnusualStatus.NONE;

	public UnitStatus(String name, String texName, UnitType uType, UnitCorner corner, int maxHp, int maxMp,
			int str, int inte, int def, int reg, int spd, int mov, Skill[] skills) {
		this.name = name;
		this.texName = texName;
		this.uType = uType;
		this.corner = corner;
		this.maxHp = maxHp;
		this.maxMp = maxMp;
		this.str = str;
		this.inte = inte;
		this.def = def;
		this.reg = reg;
		this.spd = spd;
		this.mov = mov;

		this.skills = Arrays.asList(skills);

		hp = maxHp;
		mp = maxMp;

		charge = 0;
		maxCharge = 100;
	}

	@Override
	public String getName() { return name; }

	@Override
	public String getTexName() { return texName; }

	@Override
	public void damage(Damage d) {
		hp += d.getHp();
		mp += d.getMp();

		// TODO マイナス効果はプラス効果で打ち消せるとか、いろいろやろう
		if(!d.getUnusualStatus().equals(UnusualStatus.NONE))
			us = d.getUnusualStatus();
	}

	@Override
	public UnitType getUnitType() { return uType; }

	@Override
	public int getCurrentHp() { return hp < 0 ? 0 : hp; }

	@Override
	public int getCurrentMp() { return mp < 0 ? 0 : mp; }

	@Override
	public int getMaxHp() { return maxHp; }

	@Override
	public int getMaxMp() { return maxMp; }

	@Override
	public int getStr() { return us.calcEffect(str, UnusualStatus.TargetStatus.STR); }

	@Override
	public int getInt() { return us.calcEffect(inte, UnusualStatus.TargetStatus.INT); }

	@Override
	public int getDef() { return us.calcEffect(def, UnusualStatus.TargetStatus.DEF); }

	@Override
	public int getReg() { return us.calcEffect(reg, UnusualStatus.TargetStatus.REG); }

	@Override
	public int getSpd() { return us.calcEffect(spd, UnusualStatus.TargetStatus.SPD); }

	@Override
	public int getMov() { return us.calcEffect(mov, UnusualStatus.TargetStatus.MOV); }

	@Override
	public List<Skill> getSkills() { return skills; }

	@Override
	public void setCharge(double c) {
		charge = c;
	}

	@Override
	public boolean isDead() {
		return hp <= 0;
	}

	@Override
	public boolean isUsable(Skill skill) {
		return mp + skill.getCost().getMp() >= 0;
	}

	@Override
	public UnitCorner getCorner() { return corner; }
	@Override
	public void update(float delta) {
		if(isStandby())
			charge = 0;
		charge += maxCharge / 1;// delta;
	}
	@Override
	public boolean isStandby() {
		return charge >= maxCharge;
	}
	@Override
	public UnusualStatus getUnusualStatus() {
		return us;
	}

}
