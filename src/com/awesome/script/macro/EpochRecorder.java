package com.awesome.script.macro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Deque;
import java.util.LinkedList;
import java.util.List;

import com.awesome.script.ActionScript;
import com.awesome.script.ConfigedLine;
import com.awesome.script.Line;
import com.awesome.script.Recorder;
import com.awesome.script.dynamic.Rule;
import com.awesome.srpg.operation.ScriptOperator;

public class EpochRecorder implements Recorder {

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

	public ScriptOperator getLearnerOp() {
		return new LearnMacroOp(learner);
	}

	public ScriptOperator getStatOp() {
		return new ScriptOperator(stat);
	}

	public void nextEpoch() {
		epochs.add(new Epoch());
	}

	public void addLearnerVic(boolean b) {
		epochs.getLast().addLVic(b);
	}

	public void setRuleApplicationFreq(double[] p) {
		epochs.getLast().setRuleFreq(p);
	}

	public void setLog(String str) {
		epochs.getLast().setLog(str);
	}

	public void setMacro(Rule[] m) {
		epochs.getLast().setMacro(m);
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

	// DOING ‹¤‹N•p“x‚ð•]‰¿‚·‚é
//	public double collocation() {
//
//	}

	public String collectLog() {
		StringBuilder sb = new StringBuilder();
		for(Epoch e : epochs) {
			sb.append("\n------------------------------\n");
			sb.append(e.getLog());
			sb.append("turning point : " + e.turningPoint() + "\n");
		}
		sb.append("\n---------------ave---------------\n");
		sb.append("turning point : " + aveTurningPoint() + "\n");
		sb.append("D = " + distances());

		return sb.toString();
	}

	public List<Line[]> collectMacro() {
		ArrayList<Line[]> ms = new ArrayList<Line[]>(epochs.size());
		for(Epoch e : epochs) {
			List<Line> ls = new LinkedList<Line>();
			for(Rule r : e.getMacro())
				ls.add(r.getLine());

			ls.add(ConfigedLine.PASS);
			ms.add(ls.toArray(new Line[0]));
		}

		return ms;
	}

}

class Epoch {

	private static final int DEF_TP = 500;

	private List<Boolean> lVicLog = new ArrayList<Boolean>(500);

	private double[] p;

	private String log;

	private Rule[] macro;

	void addLVic(boolean b) {
		lVicLog.add(b);
	}

	void setRuleFreq(double[] p2) {
		this.p = p2;
	}

	int turningPoint() {
		int point = DEF_TP;
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

	void setLog(String str) {
		log = str;
	}

	String getLog() {
		return log;
	}

	void setMacro(Rule[] m) {
		macro = m;
	}

	Rule[] getMacro() {
		return macro;
	}

}
