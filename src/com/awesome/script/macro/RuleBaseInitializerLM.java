package com.awesome.script.macro;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.awesome.script.ConfigedLine;
import com.awesome.script.dynamic.Rule;

public class RuleBaseInitializerLM {

	private static final Rule[] SOLDIER = {
		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_ENEMY),
		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_WIZARD),
		new Rule(ConfigedLine.DIRECT_ATTACK_CLOSEST_WIZARD),
	},
	WIZARD = {
		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_ENEMY),
		new Rule(ConfigedLine.TACKLE_CLOSEST_ENEMY),
		new Rule(ConfigedLine.MAGICAL_SMASH_CLOSEST_ENEMY),
//		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_WIZARD),
		new Rule(ConfigedLine.MAGIC_ATTACK_WEAKEST_ENEMY),
		new Rule(ConfigedLine.FIRE_BALL_CLOSEST_ENEMY),
//		new Rule(ConfigedLine.MAGIC_ATTACK_WEAKEST_WIZARD),
//		new Rule(ConfigedLine.MAGIC_ATTACK_WEAKEST_SOLDIER),
		new Rule(ConfigedLine.MAGIC_HEAL_WEAKEST_FRIEND),
		new Rule(ConfigedLine.INTENSE_INT_CLOSEST_FRIEND_WIZARD),
		new Rule(ConfigedLine.INTENSE_INT_CLOSEST_FRIEND_WIZARD_2),
		new Rule(ConfigedLine.INTENSE_INT_CLOSEST_FRIEND_WIZARD_3),
		new Rule(ConfigedLine.INTENSE_STR_CLOSEST_FRIEND),
	};

	private static final String PATH = "res/learningmacro/";
	/**
	 * @param args
	 */
	public static void main(String[] args) {
			fileOutput(SOLDIER, PATH + "rulebase_sol0.txt");
			fileOutput(SOLDIER, PATH + "rulebase_sol1.txt");
			fileOutput(WIZARD, PATH + "rulebase_wiz0.txt");
			fileOutput(WIZARD, PATH + "rulebase_wiz1.txt");

			System.out.println("initialized.");
	}

	private static void fileOutput(Serializable o, String fileName) {
		try {
			ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(fileName));
			oos.writeObject(o);
		} catch(IOException ie){
			ie.printStackTrace();
		}
	}

}
