package com.awesome.srpg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.awesome.game.base.Renderer;
import com.awesome.game.base.Screen;
import com.awesome.script.dynamic.CDUtils;
import com.awesome.script.dynamic.DynamicScripting;
import com.awesome.script.dynamic.RulebaseViewer;
import com.awesome.script.dynamic.UnitID;
import com.awesome.script.macro.LearningMacroAction;
import com.awesome.script.macro.UnitIDLM;
import com.badlogic.gdx.ApplicationListener;
import com.badlogic.gdx.Gdx;

// TODO ���Ճ}�N���Ŗ��͋����X�L�����̗p����Ă�
// TODO �ړ��\�̈�\���A�����Ƀo�O��������͂�
// TODO 1vs1�Ȃ炢��Ȃ����[��������̂ŏ���
// TODO �l�Ԃ�����

// TODO �ꎞ��~�@�\

// TODO ���G
// TODO ���G�ȃ~�b�V�������[��
// DOING �}�N���A�N�V����
// TODO �S�[���w��

public class SRPG implements ApplicationListener {

	public static final boolean VIEW = false;
	public static final boolean CONSOLE_VIEW = false;
	private final int BATTLE = LearningMacroAction.BATTLE_COUNT;
	private static final boolean DONE_NOTICE = true;

	private final int Wmax = DynamicScripting.maxweight;

	private static Renderer ren;

	public static Renderer getRenderer() {
		return ren;
	}

	public static String learnerWinLog = "";

	Screen screen;

	private final String ENEMY = "weak";

	private int battleCount;

	@Override
	public void create() {
		ren = Renderer.getInstance(Gdx.app.getGraphics().getGL10());

		for(UnitID uid : UnitID.values())
			uid.load();

		for(UnitIDLM uid : UnitIDLM.values())
			uid.load();

		screen = new BattleScreen();
	}

	@Override
	public void dispose() {
		ren.dispose();
	}

	@Override
	public void pause() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	@Override
	public void render() {
		screen.update(Gdx.app);
		if(VIEW)
			ren.render(Gdx.gl10);

		Screen next = screen.nextScreen();
		if(next != screen) {
			System.out.println("battle : " + battleCount);
			battleCount++;

//			RulebaseViewer.viewRulebases(false);

			if(battleCount == BATTLE)
				exit();
		}
		screen = next;
	}

	@Override
	public void resize(int width, int height) {
		ren.resize(width, height);
	}

	@Override
	public void resume() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	private void exit() {
		for(UnitID uid : UnitID.values())
			uid.saveRulebase();

		SimpleDateFormat sfd = new SimpleDateFormat("yyyy-MM-dd_HHmmss");

		File file = new File("res/log/macro/winLog-" + sfd.format(new Date()) + ".txt");
		PrintWriter pw = null;
		try {
			pw = new PrintWriter(new BufferedWriter(new FileWriter(file)));
		} catch (IOException e) {
			e.printStackTrace();
		}

		pw.println(learnerWinLog);
//		pw.println();
//		pw.println(RulebaseViewer.rulebasesToString(false));
		pw.close();

//		FileHandle handle = Gdx.files.internal("res/log/winLog-weak-" + BATTLE + ".txt");
//		OutputStream os = handle.write(false);
//		try {
//			os.write(learnerWinLog.getBytes());
//		} catch (IOException e) {
//			e.printStackTrace();
//		} finally {
//			try {
//				os.close();
//			} catch (IOException e) {
//				e.printStackTrace();
//			}
//		}

		if(DONE_NOTICE)
			CDUtils.open("G:\\");

		System.exit(0);
	}

}
