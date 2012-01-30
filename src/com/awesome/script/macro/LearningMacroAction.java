package com.awesome.script.macro;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.LinkedList;
import java.util.List;
import java.util.Random;

import com.awesome.script.dynamic.Agent;
import com.awesome.script.dynamic.Rule;
import com.awesome.srpg.SRPG;

public class LearningMacroAction {

	private static final boolean TEIAN22 = true;
	private static final boolean TEIAN21 = false;
	private static final boolean TEIAN2 = true;
	private static final boolean TEIAN1 = false;
	private static final boolean MUTATION = false;

	private static final int SCRIPT_LEN = 3;
	private static final int MACRO_LEN = 3;

	private Rule[] rule;
	// CEM�ŗL�p�����[�^
	private final int N = 100;			// �̐�
	private final int M;
	private final double rho = 0.1;	// �I��
	private final double alpha = 0.7;	// �X�e�b�v�T�C�Y

	public static final int K = 15;	// �w�K����}�N���̐�
	public static final int T = 500;	// �g���[�j���O��񂠂���̐퓬��

	public static final int BATTLE_COUNT = K * T;

	private int[][] w;

	private EpochRecorder recorder;

	public LearningMacroAction(Rule[] rule) {
		this.rule = rule;
		M = rule.length;
		w = new int[N][M];
	}

	public void setRecorder(EpochRecorder r) {
		recorder = r;
	}

	private List<Macro> L = new LinkedList<Macro>();
	private int macroCount = 0;
	private int phase = 0;	// �ŏ���0
	private ProbV p = null;

	private ProbV po = new ProbV(K/2, 1/(K/2.0));	// TODO �Ƃ肠�����}�N���̊m���x�N�g���͈�l���z��

	private MacroScript[] S;
	private Record[] G;

	public void initLearn() {
//		phase = macroCount / (K / 2);	// 0 = opening, 1 = midgame
		phase = 0;

		if(MUTATION) {
			if(p == null)
				p = initProbabilities(M);
			else {
				ProbV c = p.comp();
				p = initProbabilities(M);
				p.blend(c, alpha);
			}
		} else
			p = initProbabilities(M);

		S = new MacroScript[T];
		G = new Record[T];

//		macroCount++;
		trainingCount = 0;

		if(recorder != null)
			recorder.nextEpoch();
	}

	public void initActivate() {
		for(Rule r : rule)
			r.initActivated();
	}

	private int trainingCount = 0;

	public void eval(MacroScript s, Record record) {
		G[trainingCount] = record;
		s.setF(getFitness(s, record, L));
		S[trainingCount] = s;

//		if(recorder != null)
//			recorder.addLearnerVic(record.A().ht > 0);

		p = updateProbabilities(p, trainingCount + 1, S);	// S��F��0����i�܂�

//		System.out.println("F : " + s.getF());

		if(++trainingCount >= T) {
			Macro M = extractMacro(p);
			L.add(M);	// ���X�g�ɐV�����}�N����ǉ�����

			if(recorder != null) {
				recorder.setRuleApplicationFreq(ruleFreq(w, this.M, N));
				StringBuilder sb = new StringBuilder();
				sb.append("teian : " + TEIAN2);
				sb.append("\nteian2 : " + TEIAN21);
				sb.append("\nteian3 : " + TEIAN22);
//				for(int i = 0; i < 100; i++)
//					sb.append(S[S.length - 1 - i]);
				sb.append("\n" + M + "\n");
				recorder.setLog(sb.toString());

				recorder.setMacro(M.getRules().toArray(new Rule[0]));
			}

			if(++macroCount < K) {
				initLearn();
			} else {
//				SRPG.learnerWinLog += "\n\n---------------\n\n" + L + "\n\n-----------------\n\n";
//				if(SRPG.CONSOLE_VIEW)
//					System.out.println("macros : " + L);
			}
		}

	}

	private Macro extractMacro(ProbV p) {
		return extractMacro(p, w, M, N, MACRO_LEN, TEIAN2);
	}

	private Macro extractMacro(ProbV p, int[][] w, int M, int N, int MACRO_LEN, boolean collocation) {
		double[] v = ruleFreq(w, M, N);

		double[][] col = null;
		if(collocation)
			col = collocationRate(w);

		// v�͈�퓬������Ɏg����m��
//		System.out.println("v : " + Arrays.toString(v));

		int[] top = new int[MACRO_LEN];
		for(int rank = 0; rank < MACRO_LEN; rank++) {
			double largest = -Double.MAX_VALUE;
			for(int j = 0; j < M; j++) {
				double e = v[j];

				if(collocation) {
					if(rank > 0) {

						if(TEIAN21 && e >= 0)
							e = 1;

						double c = rankCol(rank, top, col, j);

						if(TEIAN22) {
							if(c > 0)
								e = e * c + 1; // col[top[0]][j];
						} else
							e *= c;
					}
				}

				if(e > largest) {
					largest = e;
					top[rank] = j;
				}
			}
			v[top[rank]] = -Double.MAX_VALUE;	// ����ȍ~�I�΂�Ȃ�����
		}

		List<Rule> rules = new ArrayList<Rule>(MACRO_LEN);
		for(int i : top)
			rules.add(rule[i]);

		Macro macro = new Macro(rules.toArray(new Rule[0]));

		return macro;
	}

	private double[] ruleFreq(int[][] w, int M, int N) {
		double[] v = new double[M];
		for(int j = 0; j < M; j++)
			for(int i = 0; i < N; i++)
				v[j] += w[i][j];

		for(int j = 0; j < M; j++)
			v[j] /= N;

		return v;
	}

	private double rankCol(int fixed, int[] top, double[][] col, int rule) {
		double c = 1;
		for(int i = 0; i < fixed; i++)
			c *= col[top[i]][rule];

		return c;
	}

	private ProbV updateProbabilities(ProbV p, int n, MacroScript[] scriptList) {
		return updateProbabilities(p, n, scriptList, N, rho);
	}
	private ProbV updateProbabilities(ProbV p, int n, MacroScript[] scriptList, int N, double rho) {
		if(n % N == 0) {	// �S�̂̍X�V
			for(int i = 1; i <= N; i++) {
				for(int j = 0; j < M; j++)
					w[i - 1][j] = scriptList[n - i].firedInPhase(rule[j]) ? 1 : 0;	// �Ή�����phase���Ɏ��s���ꂽ���ǂ���

//				System.out.println(scriptList[n - i]);
			}

//			for(int i = 0; i < N; i++)
//				System.out.println(Arrays.toString(w[i]));

			// �K���x�ɏ]���Ē���N�̃T���v�����\�[�g����B�x�X�g�Ȃ̂��ŏ�
			scriptList = sortLast(scriptList, N);
			int Ne = (int)(rho * N);	// �G���[�g�T���v���̐�
			// �G���[�g�T���v�����̃��[���̕p�x���v�Z����
			ProbV pd = new ProbV(0);	// p' : �V�K�m���x�N�g��

			int[] fired = new int[M];
			ProbV contain = new ProbV(0);

			for(int j = 0; j < M; j++) {
				pd.set(j, 0);
				for(int i = 0; i < Ne; i++) {
					if(scriptList[i].contains(rule[j])) {
						if(TEIAN1)
							fired[j] += scriptList[i].firedInPhase(rule[j]) ? 1 : 0;
						pd.set(j, pd.get(j) + 1);
					}
				}
				if(TEIAN1)
					contain.set(j, pd.get(j));

				pd.set(j, pd.get(j) / Ne);
			}

			pd.probNor();

//			System.out.println(pd);

			// �m���x�N�g�����X�V����
			if(TEIAN1) {
				for(int j = 0; j < p.length(); j++) {
					// TEIAN �A���t�@�l�Ɏ��s�m���������āA���s���ĂȂ���Εω��Ȃ��A���s���Ă���Ίw�K
					double c = fired[j] > 0 ? fired[j] / contain.get(j) : 0;
					double alpha = this.alpha * c;
					p.set(j, (1 - alpha)*p.get(j) + alpha*pd.get(j));
				}
			} else
				p.blend(pd, alpha);

			p.probNor();

//			System.out.println("p' : " + pd);
//			System.out.println("p updated : " + p);
		}

		return p;
	}

	private static final Comparator<MacroScript> SCRIPT_COM = new Comparator<MacroScript>(){
		@Override
		public int compare(MacroScript o1, MacroScript o2) {
			return (int)Math.signum(o2.getF() - o1.getF());
		}
	};

	private MacroScript[] sortLast(MacroScript[] scriptList, int n) {
		int last = scriptList.length;
		for(int i = 0; i < scriptList.length; i++)
			if(scriptList[i] == null) {
				last = i;
				break;
			}
		MacroScript[] copy = Arrays.copyOfRange(scriptList, last - n, last);
		Arrays.sort(copy, SCRIPT_COM);
		return copy;
	}


	static final double c = 0.25;
	private double getFitness(MacroScript script, Record gameRecord, List<Macro> l) {
		// A : �w�K�G�[�W�F���g�AS : Static�G�[�W�F���g
		Agent A = gameRecord.A();
		Agent Static = gameRecord.S();
		double Fdiv = 0;
		Fdiv = getFdiv(script, l);

		double Fstr = WeightUpdateOneOnOne.Fstr(A, Static);

		return Fstr + c * Fdiv;
	}

	private double getFdiv(MacroScript script, List<Macro> l) {
		double Fdiv = 0;
		for(Macro m : l)
			for(int j = 0; j < M; j++)
				Fdiv += (!m.contains(rule[j]) && script.contains(rule[j]))
						|| (m.contains(rule[j]) && !script.contains(rule[j])) ? 1 : 0;
		Fdiv /= K;

		return Fdiv;
	}

	MacroScript generateScript() {
		return generateScript(p, phase);
	}

	private MacroScript generateScript(ProbV p, int phase) {
		MacroScript script = null;;
		switch(phase) {
		case 0:
			script = new MacroScript(rouletteSelect(p, SCRIPT_LEN), rule, phase);
			break;
		case 1:
			script = new MacroScript(L.get(rouletteSelect(po, 1).get(0)), rouletteSelect(p, SCRIPT_LEN), rule, phase);
			break;
		default:
			assert false;	// �����ɂ͗��Ȃ�
		}

		return script;
	}

	Random ran = new Random();
	private List<Integer> rouletteSelect(ProbV p, int count) {
		List<Integer> sel = new ArrayList<Integer>(count);

		assert p.length() >= count;

		while(sel.size() < count) {
			double fraction = ran.nextDouble();

			double sumw = 0;
			for(int i = 0; i < p.length(); i++) {
				sumw += p.get(i);
				if(sumw > fraction) {
					if(!sel.contains(i)) {
						sel.add(i);
						break;
					}
				}
			}
		}

		Collections.sort(sel);
		Collections.reverse(sel);

		return sel;
	}

	private ProbV initProbabilities(int count) {
		return new ProbV(1.0 / count);	// TODO ����ł����̂��H
	}

	class ProbV {

		double[] p;

		public ProbV(double d) {
			this(M, d);
		}

		public ProbV(int len, double d) {
			p = new double[len];
			for(int i = 0; i < p.length; i++)
				p[i] = d;
		}

		public ProbV(ProbV pd) {
			p = Arrays.copyOf(pd.p, pd.length());
		}

		public int length() {
			return p.length;
		}

		public double get(int j) {
			return p[j];
		}

		public void set(int j, double d) {
			p[j] = d;
		}

		public void probNor() {
			double sum = 0;
			for(double d : p)
				sum += d;

			for(int i = 0; i < p.length; i++)
				p[i] /= sum;
		}

		public void blend(ProbV pd, double alpha) {
			for(int j = 0; j < p.length; j++)
				set(j, (1 - alpha)*get(j) + alpha*pd.get(j));
		}

		public ProbV comp() {
			ProbV c = new ProbV(p.length, 0);
			for(int i = 0; i < c.length(); i++)
				c.set(i, 1 - p[i]);

			c.probNor();
			return c;
		}

		@Override
		public String toString() {
			return Arrays.toString(p);
		}

	}

	class Macro {

		private List<Rule> rules;

		Macro(Rule... rules) {
			this.rules = Arrays.asList(rules);
		}

		public boolean contains(Rule r) {
			return rules.contains(r);
		}

		public List<Rule> getRules() {
			return rules;
		}

		@Override
		public String toString() {
			StringBuilder sb = new StringBuilder();
			sb.append("\n");
			for(Rule r : rules)
				sb.append(r);

			return sb.toString();
		}

	}

	class Record {

		private Agent A, S;

		public Record(Agent a, Agent s) {
			super();
			A = a;
			S = s;
		}

		public Agent A() {
			return A;
		}

		public Agent S() {
			return S;
		}

	}

	public int getPhase() {
		return phase;
	}

	public Rule[] getRulebase() {
		return rule;
	}

	private double[][] collocationRate(int[][] w) {
		double[][] rate = new double[rule.length][rule.length];

		for(int j = 0; j < rule.length; j++) {
			int A = containRule(w, j);
			for(int k = j; k < rule.length; k++) {
				int B = containRule(w, k);
				int C = containColl(w, j, k);

				rate[j][k] = (double)C / (A + B - C);
				rate[k][j] = rate[j][k];
			}
		}

		return rate;
	}

	private int containRule(int[][] w, int j) {
		int sum = 0;
		for(int i = 0; i < w.length; i++)
			sum += w[i][j];

		return sum;
	}

	private int containColl(int[][] w, int j, int k) {
		int sum = 0;
		for(int i = 0; i < w.length; i++) {
			sum += w[i][j] * w[i][k] > 0 ? 1 : 0;
		}
		return sum;
	}

}
