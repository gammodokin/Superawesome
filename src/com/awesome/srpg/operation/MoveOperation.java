package com.awesome.srpg.operation;

import com.awesome.srpg.logic.Coord;

public interface MoveOperation extends Operation{

	int getTargetX();

	int getTargetY();

	Coord<Integer>[] getPath();

}
