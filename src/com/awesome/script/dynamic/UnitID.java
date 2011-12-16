package com.awesome.script.dynamic;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

import com.awesome.script.macro.LearningMacroAction;

public enum UnitID {
	SOL0("rulebase_sol0.txt", 2), SOL1("rulebase_sol1.txt", 2), WIZ0("rulebase_wiz0.txt", 3), WIZ1("rulebase_wiz1.txt", 3);

	private static final String PATH = "res/dynamicscripting/";
	private final String fileName;

	private final int SCRIPT_SIZE;

	private DynamicScripting ds;
//	private LearningMacroAction lma;

	private UnitID(String fileName, int scriptSize) {
		this.fileName = fileName;
		this.SCRIPT_SIZE = scriptSize;

//		ds = new DynamicScripting(loadRulebase(), SCRIPT_SIZE);
	}

	int getScriptSize() {
		return SCRIPT_SIZE;
	}

	public void load() {
		Rule[] ruleBase = loadRulebase();
		ds = new DynamicScripting(ruleBase, SCRIPT_SIZE);
//		lma = new LearningMacroAction(ruleBase);
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

	public DynamicScripting getDynamicScripting() { return ds; }

	public void saveRulebase() {
		Rule[] rulebase = ds.getRulebase();
		ObjectOutputStream oos = null;
		try {
			oos = new ObjectOutputStream(new FileOutputStream(PATH + fileName));
			oos.writeObject(rulebase);
		} catch(IOException ie){
			ie.printStackTrace();
		} finally {
			try {
				oos.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}
	}
}
