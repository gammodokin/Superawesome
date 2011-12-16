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


	private static final int SCRIPT_LEN = 3;

	private Rule[] rule;
	// CEM固有パラメータ
	private final int N = 30;			// 個体数
	private final int M;
	private final double rho = 0.1;	// 選択率
	private final double alpha = 0.3;	// ステップサイズ

	public static final int K = 3;	// 学習するマクロの数
	public static final int T = 60;	// トレーニング一回あたりの戦闘数

	public static final int BATTLE_COUNT = K * T;

	private int[][] w;

	public LearningMacroAction(Rule[] rule) {
		this.rule = rule;
		M = rule.length;
		w = new int[N][M];
	}

	private List<Macro> L = new LinkedList<Macro>();
	private int macroCount = 0;
	private int phase = 0;	// 最初は0
	private ProbV p;

	private ProbV po = new ProbV(K/2, 1/(K/2.0));	// TODO とりあえずマクロの確率ベクトルは一様分布で

	private MacroScript[] S;
	private Record[] G;

	public void initLearn() {
//		phase = macroCount / (K / 2);	// 0 = opening, 1 = midgame
		phase = 0;
		p = initProbabilities(M);

		S = new MacroScript[T];
		G = new Record[T];

//		macroCount++;
		trainingCount = 1;

	}

	public void initActivate() {
		for(Rule r : rule)
			r.initActivated();
	}

	private int trainingCount = 1;

	public void eval(MacroScript s, Record record) {
		G[trainingCount - 1] = record;
		s.setF(getFitness(s, record, L));
		S[trainingCount - 1] = s;
		p = updateProbabilities(p, trainingCount, S);	// SとFは0からiまで

		System.out.println("F : " + s.getF());

		if(++trainingCount >= T + 1) {
			Macro M = extractMacro(p);
			L.add(M);	// リストに新しいマクロを追加する

			if(++macroCount < K) {
				initLearn();
			} else {
				SRPG.learnerWinLog += "\n\n---------------\n\n" + L + "\n\n-----------------\n\n";
				System.out.println("macros : " + L);
			}
		}

	}

//	void listing1() {
//		List<Macro> L = new LinkedList<Macro>();
//
//		for(int k = 0; k < K; k++) {
//			int phase = k / (K / 2);	// 0 = opening, 1 = midgame
//			ProbV p = initProbabilities();
//
//			Script[] S = new Script[T];
//			BattleRecord[] G = new BattleRecord[T];
////			double[] F = new double[T];
//			for(int i = 0; i < T; i++) {
//				// pに従ってランダムなスクリプトを引く
//				S[i] = generateScript(p, phase);
//				// S[i]を使って戦闘して、ゲームの記録を取る
//				G[i] = evaluateScript(S[i]);
//				// スクリプトの適応度を計算する
//				S[i].setF(getFitness(S[i], G[i], L));
//				p = updateProbabilities(p, i, S);	// SとFは0からiまで
//			}
//			Macro M = extractMacro(p);
//			L.add(M);	// リストに新しいマクロを追加する
//		}
//	}

	private Macro extractMacro(ProbV p) {
		double[] v = new double[M];
		for(int j = 0; j < M; j++)
			for(int i = 0; i < N; i++)
				v[j] += w[i][j];

		for(int j = 0; j < M; j++)
			v[j] /= N;

//		for(int[] ds : w)
//			System.out.println(Arrays.toString(ds));

		System.out.println("v : " + Arrays.toString(v));

		int[] top = new int[3];
		for(int rank = 0; rank < 3; rank++) {
			double largest = Integer.MIN_VALUE;
			for(int j = 0; j < M; j++)
				if(v[j] > largest) {
					largest = v[j];
					top[rank] = j;
				}
			v[top[rank]] = -1;	// これ以降選ばれないため
		}

		Macro macro = new Macro(rule[top[0]], rule[top[1]], rule[top[2]]);

		System.out.println("extracted macro : \n" + macro);

//		for(int i = 0; i < M; i++) {
//			System.out.println(rule[i] + " // " + v[i]);
//		}

		return macro;
	}


	private ProbV updateProbabilities(ProbV p, int n, MacroScript[] scriptList) {
		//int M = p.length();	// ルールベース中のルールの数
		if(n % N == 0) {	// 全個体の更新
			// 適応度に従って直近N個のサンプルをソートする。ベストなのが最初
			scriptList = sortLast(scriptList, N);
			int Ne = (int)(rho * N);	// エリートサンプルの数
			// エリートサンプル内のルールの頻度を計算する
			ProbV pd = new ProbV(0);	// p' : 新規確率ベクトル

			for(int j = 0; j < M; j++) {
				pd.set(j, 0);
				for(int i = 0; i < Ne; i++)
					if(scriptList[i].contains(rule[j])) {
						w[i][j] = scriptList[i].firedInPhase(rule[j]) ? 1 : 0;	// 対応するphase中に実行されたかどうか
						pd.set(j, pd.get(j) + 1);
					}
				pd.set(j, pd.get(j) / Ne);
			}
			// 確率ベクトルを更新する
			for(int j = 0; j < M; j++)
				p.set(j, (1 - alpha)*p.get(j) + alpha*pd.get(j));

			// この時点で合計1.0になっててもらわなきゃ困る！
			p.probNor();

//			System.out.println("elite samples : ");
//			for(int i = 0; i < Ne; i++)
//				System.out.println(scriptList[i]);
			System.out.println("p' : " + pd);
			System.out.println("p updated : " + p);
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
		Arrays.sort(scriptList, 0, n, SCRIPT_COM);
		return scriptList;
	}


	static final double c = 1.0;//0.25;
	private double getFitness(MacroScript script, Record gameRecord, List<Macro> l) {
		// A : 学習エージェント、S : Staticエージェント
		Agent A = gameRecord.A();
		Agent Static = gameRecord.S();
		double Fdiv = 0;
		for(Macro m : l)
			for(int j = 0; j < M; j++)
				Fdiv += (!m.contains(rule[j]) && script.contains(rule[j]))
						|| (m.contains(rule[j]) && !script.contains(rule[j])) ? 1 : 0;
		Fdiv /= K;

		double Fstr = WeightUpdateOneOnOne.Fstr(A, Static);

		return Fstr + c * Fdiv;
	}

//	private BattleRecord evaluateScript(Script script) {
//		// TODO ゲームの実行
//		return null;
//	}

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
			assert false;	// ここには来ない
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
		return new ProbV(1.0 / count);	// TODO これでいいのか？
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

//		public ProbV nor() {
//			double len = len();
//			for(int i = 0; i < p.length; i++)
//				p[i] /= len;
//
//			return this;
//		}
//
//		double len2() {
//			double sum = 0;
//			for(double d : p)
//				sum += d * d;
//			return sum;
//		}
//
//		double len() {
//			return Math.sqrt(len2());
//		}

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

//	class Rule {
//
//	}

}
