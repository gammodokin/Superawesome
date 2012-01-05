package com.awesome.srpg;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.awesome.game.base.RenderUtil;
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
import com.badlogic.gdx.graphics.Texture;

// TODO 中盤マクロで魔力強化スキルが採用されてる
// TODO 移動可能領域表示、同時にバグも見つかるはず
// TODO 1vs1ならいらないルールがあるので消す
// TODO 人間が操作

// TODO 一時停止機能

// TODO 索敵
// TODO 複雑なミッションルール
// DOING マクロアクション
// TODO ゴール指向

public class SRPG implements ApplicationListener {

	public static final boolean VIEW = true;
	public static final boolean CONSOLE_VIEW = false;
	final static int BATTLE = LearningMacroAction.BATTLE_COUNT;
	private static final boolean DONE_NOTICE = true;

	static final int FPS = 30;

	private final int Wmax = DynamicScripting.maxweight;

	private static Renderer ren;

	// DOING Multi Thread にする
	private Thread logicThread;

	public static Renderer getRenderer() {
		return ren;
	}

	public static String learnerWinLog = "";

	Screen screen;

	private final String ENEMY = "weak";

//	private int battleCount;

	@Override
	public void create() {
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
		// TODO 自動生成されたメソッド・スタブ

	}

	@Override
	public void render() {
//		screen.update(Gdx.app);
		if(VIEW)
			ren.render(Gdx.gl10);
//
//		Screen next = screen.nextScreen();
//		if(next != screen) {
//			System.out.println("battle : " + battleCount);
//			battleCount++;
//
////			RulebaseViewer.viewRulebases(false);
//
//			if(battleCount == BATTLE)
//				exit();
//		}
//		screen = next;
	}

	@Override
	public void resize(int width, int height) {
		ren.resize(width, height);
	}

	@Override
	public void resume() {
		// TODO 自動生成されたメソッド・スタブ

	}

	static void exit() {
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

		if(DONE_NOTICE)
			CDUtils.open("G:\\");

		System.exit(0);
	}

}

class GameLogic implements Runnable {

	private final float DELTA_MIN;

	Screen screen;

	public GameLogic() {
		screen = new BattleScreen();

		DELTA_MIN = 1.0f/30;
	}

	private int battleCount;

	@Override
	public void run() {

		float delta = 0;

		while(true) {
			long startTime = System.currentTimeMillis();

			screen.update(delta / 1000);

			Screen next = screen.nextScreen();
			if(next != screen) {
				System.out.println("battle : " + battleCount);
				battleCount++;

				if(battleCount == SRPG.BATTLE)
					SRPG.exit();
			}
			screen = next;

			delta = System.currentTimeMillis() - startTime;

			if(delta < DELTA_MIN) {
				try {
					Thread.sleep((long) (DELTA_MIN - delta));
				} catch (InterruptedException e) {
					// TODO 自動生成された catch ブロック
					e.printStackTrace();
				}

				delta = DELTA_MIN;
			}
		}

	}

}
