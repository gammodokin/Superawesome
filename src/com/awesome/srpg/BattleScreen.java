package com.awesome.srpg;

import java.util.LinkedList;
import java.util.List;

import com.awesome.game.base.Actor;
import com.awesome.game.base.Screen;
import com.awesome.script.Recorder;
import com.awesome.script.StaticScript;
import com.awesome.script.macro.EpochRecorder;
import com.awesome.script.macro.LearnMacroOp;
import com.awesome.script.macro.UnitIDLM;
import com.awesome.srpg.logic.StageCell;
import com.awesome.srpg.logic.UnitCorner;
import com.awesome.srpg.logic.UnitManager;
import com.awesome.srpg.logic.UnitStatus;
import com.awesome.srpg.object.Stage;
import com.awesome.srpg.object.Unit;
import com.awesome.srpg.object.UnitCursor;
import com.awesome.srpg.operation.ScriptOperator;
import com.badlogic.gdx.Application;

public class BattleScreen implements Screen {

	List<Actor> actorList = new LinkedList<Actor>();

	private final int SCALE = 2;
	private final int LINES = 8;

	StageCell[][] cells;
	Stage stage;
	UnitManager manager;

	private UnitCursor cursor;

	boolean isDone;

	Screen nextScreen;

	private Recorder recorder;

	public BattleScreen() {

		nextScreen = this;

		cells = new StageCell[LINES][LINES];
		for(int x = 0; x < cells.length; x++)
			for(int y = 0; y < cells.length; y++)
				cells[x][y] = StageCell.Normal;

		manager = new UnitManager(cells);


		stage = new Stage(this, LINES, cells);

		cursor = new UnitCursor(this, stage.SPAN);

		double r = 12.5;
//		manager.addUnit(new Unit(this, stage, 0, 1, UnitStatus.createOwnSoldier(), new DynamicScriptOp(UnitID.SOL0.getDynamicScripting()), r*0));
//		manager.addUnit(new Unit(this, stage, 1, 1, UnitStatus.createOwnSoldier(), new DynamicScriptOp(UnitID.SOL1.getDynamicScripting()), r*0));
//
		manager.addUnit(new Unit(this, stage, 0, 0, UnitStatus.createOwnMagician(), new LearnMacroOp(UnitIDLM.WIZ0), r*0));
//		manager.addUnit(new Unit(this, stage, 1, 0, UnitStatus.createOwnMagician(), new DynamicScriptOp(UnitID.WIZ1.getDynamicScripting()), r*0));

//		manager.addUnit(new Unit(this, stage, 0, 1, UnitStatus.createOwnSoldier(), new ScriptOperator(new StaticScript(StaticScript.RULE_SOLDIER)), r*0));
//		manager.addUnit(new Unit(this, stage, 1, 1, UnitStatus.createOwnSoldier(), new ScriptOperator(new StaticScript(StaticScript.RULE_SOLDIER)), r*0));
//
//		manager.addUnit(new Unit(this, stage, 0, 0, UnitStatus.createOwnMagician(), new ScriptOperator(new StaticScript(StaticScript.RULE_WIZARD)), r*0));
//		manager.addUnit(new Unit(this, stage, 1, 0, UnitStatus.createOwnMagician(), new ScriptOperator(new StaticScript(StaticScript.RULE_WIZARD)), r*0));
//
//		manager.addUnit(new Unit(this, stage, LINES-1, LINES-2, UnitStatus.createEnemySoldier(), new ScriptOperator(new StaticScript(StaticScript.RULE_SOLDIER)), r*0));
//		manager.addUnit(new Unit(this, stage, LINES-2, LINES-2, UnitStatus.createEnemySoldier(), new ScriptOperator(new StaticScript(StaticScript.RULE_SOLDIER)), r*0));
//
		manager.addUnit(new Unit(this, stage, LINES-1, LINES-1, UnitStatus.createEnemyMagician(), new ScriptOperator(new StaticScript(StaticScript.RULE_WIZARD)), r*0));
//		manager.addUnit(new Unit(this, stage, LINES-2, LINES-1, UnitStatus.createEnemyMagician(), new ScriptOperator(new StaticScript(StaticScript.RULE_WIZARD)), r*0));
	}

	public BattleScreen(Recorder recorder) {

		nextScreen = this;

		this.recorder = recorder;

		cells = new StageCell[LINES][LINES];
		for(int x = 0; x < cells.length; x++)
			for(int y = 0; y < cells.length; y++)
				cells[x][y] = StageCell.Normal;

		manager = new UnitManager(cells);


		stage = new Stage(this, LINES, cells);

		cursor = new UnitCursor(this, stage.SPAN);

		double r = 12.5;
		manager.addUnit(new Unit(this, stage, 0, 0, UnitStatus.createOwnMagician(), recorder.getLearnerOp(), r*0));

		manager.addUnit(new Unit(this, stage, LINES-1, LINES-1, UnitStatus.createEnemyMagician(), recorder.getStatOp(), r*0));
	}

	@Override
	public void update(float delta) {
//		float delta = app.getGraphics().getDeltaTime();
		List<Actor> actorListClone = new LinkedList<Actor>(actorList);
		for(Actor act : actorListClone)
			if(act != null)	// ”ñ“¯Šú‚ÌŠÖŒW
				act.update(delta);

		manager.scanStandbyUnit(delta);

		cursor.setUnit(manager.getCurrentUnit());
	}

	@Override
	public void dispose() {
		stage.dispose();
		manager.dispose();
		cursor.dispose();
	}

	@Override
	public void addActor(Actor act) {
		actorList.add(act);
	}

	@Override
	public void removeActor(Actor act) {
		actorList.remove(act);
	}

	@Override
	public Screen nextScreen() {
		if(manager.isDone()) {
			recorder.addLearnerVic(manager.wonCorder().equals(UnitCorner.LEARNER));

			dispose();
			if(recorder == null)
				nextScreen = new BattleScreen();
			else
				nextScreen = new BattleScreen(recorder);
		}
		return nextScreen;
	}

}
