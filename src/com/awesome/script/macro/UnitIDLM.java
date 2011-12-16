package com.awesome.script.macro;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.awesome.script.dynamic.DynamicScripting;
import com.awesome.script.dynamic.Rule;

public enum UnitIDLM {
	SOL0("rulebase_sol0.txt", 2), SOL1("rulebase_sol1.txt", 2), WIZ0("rulebase_wiz0.txt", 3), WIZ1("rulebase_wiz1.txt", 3);

	private static final String PATH = "res/learningmacro/";
	private final String fileName;

	private final int SCRIPT_SIZE;

//	private DynamicScripting[] ds;
	private LearningMacroAction lma;

	private UnitIDLM(String fileName, int scriptSize) {
		this.fileName = fileName;
		this.SCRIPT_SIZE = scriptSize;
	}

	int getScriptSize() {
		return SCRIPT_SIZE;
	}

	public void load() {
//		Rule[][] ruleBase = {loadRulebase(), loadRulebase()};
//		ds = new DynamicScripting[]{new DynamicScripting(ruleBase[0], SCRIPT_SIZE), new DynamicScripting(ruleBase[1], SCRIPT_SIZE),};
		lma = new LearningMacroAction(loadRulebase());
		lma.initLearn();
	}

	private Rule[] loadRulebase() {
		Rule[] r = null;
		ObjectInputStream ois = null;
		try {
			ois = new ObjectInputStream(new FileInputStream(PATH + fileName));
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
			ObjectOutputStream oos = null;
			try {
				oos = new ObjectOutputStream(new FileOutputStream(PATH + fileName));
				oos.writeObject(lma.getRulebase());
			} catch(IOException ie){
				ie.printStackTrace();
			} finally {
				try {
					oos.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
//		}
	}
}
