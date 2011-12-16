package com.awesome.script;

import java.util.List;

import com.awesome.srpg.object.Unit;

public interface ActionScript {

	Line selectRule(Unit actor, List<Unit> units, Unit[][] UnitMap);

}
