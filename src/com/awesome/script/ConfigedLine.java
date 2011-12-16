package com.awesome.script;

import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import com.awesome.srpg.object.Unit;
import com.awesome.srpg.strategy.Skill;
import com.awesome.srpg.strategy.UnusualStatus;

public class ConfigedLine implements Line {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	public static final Line
	DIRECT_ATTACK_CLOSEST_ENEMY = 		new ConfigedLine(0, new Condition.Always(), new Target[] {Target.ENEMY, Target.CLOSEST}, 					Skill.NORMAL, 0),
	DIRECT_ATTACK_WEAKEST_ENEMY = 		new ConfigedLine(1, new Condition.Always(), new Target[] {Target.ENEMY, Target.WAKEST, Target.CLOSEST},	Skill.NORMAL, 0.5),
	DIRECT_ATTACK_CLOSEST_SOLDIER = 	new ConfigedLine(2, new Condition.Always(), new Target[] {Target.ENEMY, Target.SOLDIER, Target.CLOSEST},	Skill.NORMAL, 0.8),
	DIRECT_ATTACK_CLOSEST_WIZARD = 		new ConfigedLine(3, new Condition.Always(), new Target[] {Target.ENEMY, Target.WIZARD, Target.CLOSEST},	Skill.NORMAL, 0.9),
	DIRECT_ATTACK_WEAKEST_SOLDIER = 	new ConfigedLine(4, new Condition.Always(), new Target[] {Target.ENEMY, Target.SOLDIER, Target.WAKEST, Target.CLOSEST}, Skill.NORMAL, 1),
	DIRECT_ATTACK_WEAKEST_WIZARD = 		new ConfigedLine(5, new Condition.Always(), new Target[] {Target.ENEMY, Target.WIZARD, Target.WAKEST, Target.CLOSEST}, Skill.NORMAL, 1.2),
	TACKLE_CLOSEST_ENEMY = 				new ConfigedLine(6, new Condition.Always(), new Target[] {Target.ENEMY, Target.CLOSEST},					Skill.TACKLE, 1.3),
	MAGICAL_SMASH_CLOSEST_ENEMY = 		new ConfigedLine(7, new Condition.IsStatus(UnusualStatus.INTENCE_STR),	new Target[] {Target.ENEMY, Target.CLOSEST}, Skill.MAGICAL_SMASH, 1.4),

	MAGIC_ATTACK_CLOSEST_ENEMY = 		new ConfigedLine(50, new Condition.Always(), new Target[] {Target.ENEMY, Target.CLOSEST},	Skill.FIRE, 1),
	MAGIC_ATTACK_WEAKEST_ENEMY = 		new ConfigedLine(51, new Condition.Always(), new Target[] {Target.ENEMY, Target.WAKEST, Target.CLOSEST},	Skill.FIRE, 1.5),
	MAGIC_ATTACK_WEAKEST_WIZARD = 		new ConfigedLine(52, new Condition.Always(), new Target[] {Target.ENEMY, Target.WIZARD, Target.WAKEST, Target.CLOSEST},	Skill.FIRE, 1.6),
	MAGIC_ATTACK_WEAKEST_SOLDIER = 		new ConfigedLine(53, new Condition.Always(), new Target[] {Target.ENEMY, Target.SOLDIER, Target.WAKEST, Target.CLOSEST},	Skill.FIRE, 1.7),
	FIRE_BALL_CLOSEST_ENEMY = 			new ConfigedLine(54, new Condition.Always(), new Target[] {Target.ENEMY, Target.CLOSEST},	Skill.FIRE_BALL, 1.8),
	MAGIC_HEAL_WEAKEST_FRIEND = 		new ConfigedLine(55, new Condition.Less(Condition.StatusType.HP, 50),	new Target[] {Target.FRIEND, Target.WAKEST, Target.CLOSEST},	Skill.HEAL, 16),
	INTENSE_INT_CLOSEST_FRIEND_WIZARD = new ConfigedLine(56, new Condition.IsStatus(UnusualStatus.NONE),	new Target[] {Target.FRIEND, Target.WIZARD, Target.CLOSEST},	Skill.INTENSE_INT, 2.1),
	INTENSE_INT_CLOSEST_FRIEND_WIZARD_2 = new ConfigedLine(56.1, new Condition.IsStatus(UnusualStatus.NONE),	new Target[] {Target.FRIEND, Target.WIZARD, Target.CLOSEST},	Skill.INTENSE_INT_2, 2.1),
	INTENSE_INT_CLOSEST_FRIEND_WIZARD_3 = new ConfigedLine(56.2, new Condition.IsStatus(UnusualStatus.NONE),	new Target[] {Target.FRIEND, Target.WIZARD, Target.CLOSEST},	Skill.INTENSE_INT_3, 2.1),
	INTENSE_STR_CLOSEST_FRIEND = 		new ConfigedLine(57, new Condition.IsStatus(UnusualStatus.NONE),	new Target[] {Target.FRIEND, Target.CLOSEST},	Skill.INTENSE_STR, 2);

	private final double ID;
	private Condition cond;
	private Target[] targetRules;
	private Skill skill;
	private double priority;

	transient Unit targetUnit;

	public ConfigedLine(double id, Condition cond, Target[] targetRules, Skill skill, double priority) {
		this.ID = id;
		this.cond = cond;
		this.targetRules = targetRules;
		this.skill = skill;
		this.priority = priority;
	}

	@Override
	public double getPriority() { return priority; }

	@Override
	public boolean isUnderCondition(Unit actor, List<Unit> units,
			Unit[][] UnitMap) {
		targetUnit = null;

		// MP‚ª‘«‚è‚Ä‚é‚©
		if(!actor.getStatus().isUsable(skill))
			return false;

		List<Unit> targets = new LinkedList<Unit>(units);
		for(Target tr : targetRules)
			targets = tr.match(actor,targets);

		if(targets.isEmpty())
			return false;

		for(Unit t : targets) {
			if(cond.underCondition(t.getStatus())) {
				targetUnit = t;
				break;
			}
		}

		return targetUnit != null;
	}

	@Override
	public int getTargetX() {
		return targetUnit.getPosX();
	}

	@Override
	public int getTargetY() {
		return targetUnit.getPosY();
	}

	@Override
	public Skill getSkill() {
		return skill;
	}

	@Override
	public int compareTo(Line o) {
		int comp;
		if(priority < o.getPriority())
			comp = 1;
		else if(priority == o.getPriority())
			comp = 0;
		else
			comp = -1;

		return comp;
	}

	@Override
	public boolean equals(Object obj) {
		Line l = (Line)obj;
		return ID == l.getID();
	}

	@Override
	public String toString() {
		return "(ðŒ:" + cond + "/‘ÎÛ:" + Arrays.toString(targetRules) + "/s“®:" + skill + ")";
	}

	@Override
	public Condition getCond() {
		return cond;
	}

	@Override
	public double getID() {
		return ID;
	}

}
