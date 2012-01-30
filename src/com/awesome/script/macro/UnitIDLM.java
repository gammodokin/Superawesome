package com.awesome.script.macro;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.awesome.script.ConfigedLine;
import com.awesome.script.dynamic.Rule;
import com.badlogic.gdx.Gdx;

public enum UnitIDLM {
	SOL0("rulebase_sol0.txt", 2), SOL1("rulebase_sol1.txt", 2), WIZ0("rulebase_wiz0.txt", 3), WIZ1("rulebase_wiz1.txt", 3);

	private static boolean useGdx = true;

	public static void useGdx(boolean b) {
		useGdx = b;
	}

	private static final String PATH = "res/learningmacro/";
	private final String fileName;

	private final int SCRIPT_SIZE;

//	private DynamicScripting[] ds;
	private LearningMacroAction lma;

	private Rule[] rules;

	private UnitIDLM(String fileName, int scriptSize) {
		this.fileName = fileName;
		this.SCRIPT_SIZE = scriptSize;
	}

	int getScriptSize() {
		return SCRIPT_SIZE;
	}

	public void load() {
		rules = loadRulebase();
		lma = new LearningMacroAction(rules);
		lma.initLearn();
	}

	public void altLoad() {
		Rule[] rs = new Rule[rules.length];
		for(int i = 0; i < rules.length; i++) {
			rs[i] = new Rule(new ConfigedLine((ConfigedLine)rules[i].getLine()));
		}

		lma = new LearningMacroAction(rs);
		lma.initLearn();
	}

	private Rule[] loadRulebase() {
		Rule[] r = null;
		ObjectInputStream ois = null;
		try {
			InputStream is;
			if(useGdx)
				is = Gdx.files.internal(PATH + fileName).read();
			else
				is = new FileInputStream(PATH + fileName);
			ois = new ObjectInputStream(is);
			r = (Rule[])ois.readObject();
		} catch(IOException ie){
			ie.printStackTrace();
		} catch(ClassNotFoundException cfe){
			cfe.printStackTrace();
		} finally {
			try {
				ois.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		assert r != null : "Rulebase load falure.";
		return r;
	}

//	public DynamicScripting[] getDynamicScripting() { return ds; }

	public LearningMacroAction getLearningMacroAction() {return lma; }

	public void saveRulebase() {
//		for(int i = 0; i < ds.length; i++) {
//			Rule[] rulebase = ds[i].getRulebase();
//			ObjectOutputStream oos = null;
//			try {
//				oos = new ObjectOutputStream(new FileOutputStream(PATH + fileName));
//				oos.writeObject(lma.getRulebase());
//			} catch(IOException ie){
//				ie.printStackTrace();
//			} finally {
//				try {
//					oos.close();
//				} catch (IOException e) {
//					e.printStackTrace();
//				}
//			}
//		}
	}
}
