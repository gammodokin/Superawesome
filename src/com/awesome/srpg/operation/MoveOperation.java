package com.awesome.srpg.operation;

import com.awesome.srpg.strategy.Coord;

public interface MoveOperation extends Operation{

	int getTargetX();

	int getTargetY();

	Coord<Integer>[] getPath();

}
