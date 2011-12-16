package com.awesome.script.dynamic;

import java.util.Random;

public class DynamicScripting {

	private final Rule[] rule;
	private final int scriptsize;
	private final int maxtries;

	static final int Winit = 100;
	static final int minweight = 0;
	public static final int maxweight = 500;

	private double carriedOver = 0;

	private Random ran = new Random();

	public DynamicScripting(Rule[] rulebase, int scriptsize) {
		this.rule = rulebase;
		this.scriptsize = scriptsize;

		maxtries = 1;//rule.length * 1;
	}

	public void initActivate() {
		for(Rule r : rule)
			r.initActivated();
	}

	public Rule[] getRulebase() {
		return rule;
	}

	public DynamicScript scriptGeneration() {

//		clearScript();
		DynamicScript script = new DynamicScript();

		int sumweights = 0;

		for(int i = 0; i < rule.length; i++) {
			sumweights += rule[i].getWeight();
		}

//		System.out.println("sumW=" + sumweights + ",carried=" + carriedOver);

		// åJÇËï‘ÇµÇÃÉãÅ[ÉåÉbÉgëIë
		for(int i = 0; i < scriptsize; i++) {
			int trying = 0;
			boolean lineadded = false;

			while(trying < maxtries && !lineadded) {
				int j = 0;
				int sum = 0;
				int selected = -1;
				int fraction = ran.nextInt(sumweights);

				while(selected < 0) {
					sum += rule[j].getWeight();

					if(sum > fraction)
						selected = j;
					else
						j++;
				}
				lineadded = script.insertLine(rule[selected]);
				trying++;
			}
		}
		script.finishScript();

		return script;
	}

	private int calculateAdjustment(Agent a, Team g) { return WeightUpdate.dW(a, g); }

	private void destributeReminder(double remainder) {
		remainder += carriedOver;
		carriedOver = 0;

		for(int i = 0; i < rule.length; i++) {
			int rem = (int) Math.round(remainder / (rule.length - i));
			remainder -= rem;
			rule[i].updateWeight(rem);
			if(rule[i].getWeight() < minweight) {
				carriedOver += rule[i].getWeight() - minweight;
				rule[i].setWeight(minweight);
			} else if(rule[i].getWeight() > maxweight) {
				carriedOver += rule[i].getWeight() - maxweight;
				rule[i].setWeight(maxweight);
			}
		}
	}

	public void weightAdjustment(Agent a, Team g) {
		int active = 0;

		for(int i = 0; i < rule.length; i++) {
			if(rule[i].isActivated())
				active++;
		}

		if(active <= 0 || active >= rule.length)
			return; // no updates are needed.

		int nonactive = rule.length - active;
		int adjustment = calculateAdjustment(a, g); // ïÒèV

		double comp = -(double)active * adjustment / nonactive;
		int compensation = (int) Math.round(comp);
//		int compensation = -active * adjustment / nonactive; // î±
		double remainder = 0;
		// Credit assignment
		for(int i = 0; i < rule.length; i++) {
			if(rule[i].isActivated())
				rule[i].updateWeight(adjustment);
			else {
				rule[i].updateWeight(compensation);
				remainder += comp - compensation;
			}

			if(rule[i].getWeight() < minweight) {
				remainder += rule[i].getWeight() - minweight;
				rule[i].setWeight(minweight);
			} else if(rule[i].getWeight() > maxweight) {
				remainder += rule[i].getWeight() - maxweight;
				rule[i].setWeight(maxweight);
			}
		}
		destributeReminder(remainder);
	}
}
