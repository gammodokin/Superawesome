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
// DOING �}���`�X���b�h�A�G�t�F�N�gHPMP�o�[�̕`�悪�ł��ĂȂ�

// TODO ���G
// TODO ���G�ȃ~�b�V�������[��
// DOING �}�N���A�N�V����
// TODO �S�[���w��

public class SRPG implements ApplicationListener {

	public static final boolean VIEW = false;
	public static final boolean CONSOLE_VIEW = false;
	final static int BATTLE = LearningMacroAction.BATTLE_COUNT;
	private static final boolean DONE_NOTICE = false;

	static final int FPS = 30;

	private static boolean initial = false;

	private final int Wmax = DynamicScripting.maxweight;

	private static Renderer ren;

	private Thread logicThread;

	public static Renderer getRenderer() {
		return ren;
	}

	public static boolean initial() {
		return initial;
	}

	public static String learnerWinLog = "";

	Screen screen;

	private final String ENEMY = "weak";

	@Override
	public void create() {
		initial = true;

		ren = Renderer.getInstance(Gdx.app.getGraphics().getGL10());

		for(UnitID uid : UnitID.values())
			uid.load();

		for(UnitIDLM uid : UnitIDLM.values())
			uid.load();

		logicThread = new Thread(new GameLogic());
		logicThread.start();
//		screen = new BattleScreen();
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
		if(VIEW)
			ren.render(Gdx.gl10);
	}

	@Override
	public void resize(int width, int height) {
		ren.resize(width, height);
	}

	@Override
	public void resume() {
		// TODO �����������ꂽ���\�b�h�E�X�^�u

	}

	static void exit() {
//		for(UnitID uid : UnitID.values())
//			uid.saveRulebase();

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

		if(DONE_NOTICE)
			CDUtils.open("G:\\");

//		System.exit(0);
	}

}
