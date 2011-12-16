package com.awesome.srpg.strategy;

import java.io.Serializable;

import com.awesome.srpg.effect.SkillEffect;


public enum Skill implements Serializable {
	PASS("ë“ã@", Damage.ZERO, Damage.ZERO, Range.ZERO, false, DamageType.SUPPORT, TargetType.FRIEND, null),
	NORMAL("ãﬂê⁄çUåÇ", Damage.ZERO, new Damage(-10, 0), new Range(RangeType.ARCH, 1, 1), false, DamageType.ATTACK, TargetType.OTHER, SkillEffect.ATTACK),
	TACKLE("éÃêgçUåÇ", new Damage(-5, 0), new Damage(-20, 0), new Range(RangeType.ARCH, 1, 1), false, DamageType.ATTACK, TargetType.OTHER, SkillEffect.ATTACK),
	MAGICAL_SMASH("ï®óùñÇñ@çUåÇ", new Damage(0, -15), new Damage(-40, 0), new Range(RangeType.ARCH, 1, 1), false, DamageType.ATTACK, TargetType.OTHER, SkillEffect.ATTACK),
	FIRE("ñÇñ@çUåÇ", new Damage(0, -10), new Damage(-20, 0), new Range(RangeType.ARCH, 3, 3), true, DamageType.ATTACK, TargetType.OTHER, SkillEffect.MAGIC),
	FIRE_BALL("ã≠ñÇñ@çUåÇ", new Damage(0, -15), new Damage(-30, 0), new Range(RangeType.ARCH, 3, 3), true, DamageType.ATTACK, TargetType.OTHER, SkillEffect.MAGIC),
	HEAL("âÒïúñÇñ@",  new Damage(0, -10), new Damage(30, 0), new Range(RangeType.ARCH, 3, 3), true, DamageType.SUPPORT, TargetType.FRIEND, SkillEffect.CURE),
	INTENSE_INT("ñÇóÕã≠âª", new Damage(0, -5), new Damage(0, 0, UnusualStatus.INTENCE_INT), new Range(RangeType.ARCH, 3, 3), true, DamageType.SUPPORT, TargetType.FRIEND, SkillEffect.CURE),
	INTENSE_INT_2("ñÇóÕã≠âª2", new Damage(0, -5), new Damage(0, 0, UnusualStatus.INTENCE_INT), new Range(RangeType.ARCH, 3, 3), true, DamageType.SUPPORT, TargetType.FRIEND, SkillEffect.CURE),
	INTENSE_INT_3("ñÇóÕã≠âª3", new Damage(0, -5), new Damage(0, 0, UnusualStatus.INTENCE_INT), new Range(RangeType.ARCH, 3, 3), true, DamageType.SUPPORT, TargetType.FRIEND, SkillEffect.CURE),
	INTENSE_STR("óÕã≠âª", new Damage(0, -5), new Damage(0, 0, UnusualStatus.INTENCE_STR), new Range(RangeType.ARCH, 3, 3), true, DamageType.SUPPORT, TargetType.FRIEND, SkillEffect.CURE);

	private final String name;

	private final Damage cost, dam;

	private Range range;

	private boolean isMagic;

	private DamageType dType;
	private TargetType tType;

	private SkillEffect effect;

	Skill(String name, Damage cost, Damage dam, Range range, boolean isMagic, DamageType dType, TargetType tType, SkillEffect effect) {
		this.name = name;
		this.cost = cost;
		this.dam = dam;
		this.range = range;
		this.isMagic = isMagic;
		this.dType = dType;
		this.tType = tType;
		this.effect = effect;
	}

	public Damage getCost() {
		return cost;
	}

	public Range getRange() { return range; }

	public Damage getDamage(Status actor, Status target) {
		Damage d;
		if(isMagic)
			d = dType.calcDamage(dam, actor.getInt(), target.getReg());
		else
			d = dType.calcDamage(dam, actor.getStr(), target.getDef());



		return d;
	}

	public UnitCorner getTargetCorner(UnitCorner corner) {
		return tType.getCorner(corner);
	}

	public SkillEffect getEffect() {
		return effect;
	}

	@Override
	public String toString() {
		return name;
	}



	private enum DamageType {
		ATTACK {
			@Override
			Damage calcDamage(Damage skillDam, int offense, int defense) {
				return skillDam.add(-offense + defense);
			}
		}, SUPPORT {
			@Override
			Damage calcDamage(Damage skillDam, int offense, int defense) {
				return skillDam.add(offense / 2);
			}
		};

		abstract Damage calcDamage(Damage skillDam, int offense, int defense);
	}

	private enum TargetType {
		FRIEND {
			@Override
			UnitCorner getCorner(UnitCorner corner) {
				return corner.friend();
			}
		}, OTHER {
			@Override
			UnitCorner getCorner(UnitCorner corner) {
				return corner.enemy();
			}
		};

		abstract UnitCorner getCorner(UnitCorner corner);
	}
}
