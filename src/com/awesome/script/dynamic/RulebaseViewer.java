package com.awesome.script.dynamic;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;

import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.files.FileHandle;

public class RulebaseViewer {

	/**
	 * @param args
	 */
	public static void main(String[] args) {
//		viewRulebases(true);

//		java.awt.Toolkit.getDefaultToolkit().beep();
//		System.out.println('\u0007');
	}

	public static void viewRulebases(boolean load) {
		System.out.println(rulebasesToString(load));
//		for(UnitID uid : UnitID.values()) {
//			if(load)
//				uid.load();
//			System.out.println(Arrays.toString(uid.getDynamicScripting().getRulebase()) + "\n");
//		}
	}

	public static String rulebasesToString(boolean load) {
		String result = "";
		for(UnitID uid : UnitID.values()) {
			if(load)
				uid.load();
			result += Arrays.toString(uid.getDynamicScripting().getRulebase()) + "\n\n";
		}

		return result;
	}

}
