package com.awesome.script.dynamic;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;

import com.awesome.script.ConfigedLine;

public class RulebaseInitializer {

	static final Rule[] SOLDIER = {
//		new Rule(ConfigedLine.DIRECT_ATTACK_CLOSEST_ENEMY),
		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_ENEMY),
		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_WIZARD),
//		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_SOLDIER),
//		new Rule(ConfigedLine.DIRECT_ATTACK_CLOSEST_SOLDIER),
		new Rule(ConfigedLine.DIRECT_ATTACK_CLOSEST_WIZARD),
	},
	WIZARD = {
//		new Rule(ConfigedLine.DIRECT_ATTACK_CLOSEST_ENEMY),
		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_ENEMY),
		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_WIZARD),
//		new Rule(ConfigedLine.DIRECT_ATTACK_WEAKEST_SOLDIER),
//		new Rule(ConfigedLine.MAGIC_ATTACK_CLOSEST_ENEMY),
		new Rule(ConfigedLine.MAGIC_ATTACK_WEAKEST_ENEMY),
		new Rule(ConfigedLine.MAGIC_ATTACK_WEAKEST_WIZARD),
		new Rule(ConfigedLine.MAGIC_ATTACK_WEAKEST_SOLDIER),
		new Rule(ConfigedLine.MAGIC_HEAL_WEAKEST_FRIEND),
	};

	/**
	 * @param args
	 */
	public static void main(String[] args) {
			fileOutput(SOLDIER, "res/dynamicscripting/rulebase_sol0.txt");
			fileOutput(SOLDIER, "res/dynamicscripting/rulebase_sol1.txt");
			fileOutput(WIZARD, "res/dynamicscripting/rulebase_wiz0.txt");
			fileOutput(WIZARD, "res/dynamicscripting/rulebase_wiz1.txt");

			System.out.println("initialized");
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
