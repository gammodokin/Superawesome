package com.awesome.script.macro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.awesome.script.ActionScript;

public class EpochRecorder {

	private final int N;

	private LearningMacroAction learner;

	private ActionScript stat;

	private Deque<Epoch> epochs;

	public EpochRecorder(int N, LearningMacroAction learner, ActionScript stat) {
		super();
		this.N = N;

		this.learner = learner;
		this.stat = stat;

		epochs = new LinkedList<Epoch>();

		nextEpoch();

//		LearningMacroAction lma = learner.getLearningMacroAction();
		LearningMacroAction lma = learner;
		lma.setRecorder(this);
	}

//	public int getN() {
//		return N;
//	}

	public LearningMacroAction getLearner() {
		return learner;
	}

	public ActionScript getStat() {
		return stat;
	}

	public void nextEpoch() {
		epochs.add(new Epoch());
	}

	public void addLeanerVic(boolean b) {
		epochs.getLast().addLVic(b);
	}

	public void setRuleApplicationFreq(double[] p) {
		epochs.getLast().setRuleFreq(p);
	}

	public double aveTurningPoint() {
		int buf = 0;
		for(Epoch e : epochs)
			buf += e.turningPoint();

		double ave = (double)buf / epochs.size();
		return ave;
	}

	public double distances() {
		Epoch[] es = epochs.toArray(new Epoch[0]);
		int N = es.length;
		double sum = 0;
		for(int i = 0; i < N; i++)
			for(int j = i; j < N; j++) {
				sum += vdist(es[i].getRuleFreq(), es[j].getRuleFreq());
			}

		double D = sum / ((double)N * (N - 1) / 2);
		return D;
	}

	private double vdist(double[] v1, double[] v2) {
		double buf = 0;
		for(int i = 0; i < v1.length; i++) {
			buf += Math.pow(v2[i] - v1[i], 2);
		}

		double d = Math.sqrt(buf);
		return d;
	}

//	public void addW(int[][] w) {
//		List<List<Integer>> ruleList = new ArrayList<List<Integer>>(w.length);
//		wLog.add(ruleList);
//
//		for(int i = 0; i < w.length; i++) {
//			ArrayList<Integer> applys = new ArrayList<Integer>(w[i].length);
//			ruleList.add(applys);
//			for(int j = 0; j < w[i].length; j++) {
//				applys.add(w[i][j]);
//			}
//		}
//	}

}

class Epoch {

	private List<Boolean> lVicLog = new ArrayList<Boolean>(500);

	private double[] p;

	void addLVic(boolean b) {
		lVicLog.add(b);
	}

	void setRuleFreq(double[] p2) {
		this.p = p2;
	}

	int turningPoint() {
		int point = -1;
		for(int i = 9; i < lVicLog.size(); i++) {
			if(last10Ave(i) >= 0.5) {
				point = i;
				break;
			}
		}

		return point;
	}

	private double last10Ave(int now) {
		int vic = 0;
		for(int i = 0; i < 10; i++) {
			vic += lVicLog.get(now - i) ? 1 : 0;
		}

		double ave = (double)vic / 10;
		return ave;
	}

	double[] getRuleFreq() {
		return p;
	}

}
