package com.awesome.srpg.logic;

public class Range {
	static final Range ZERO = new Range(RangeType.ARCH, 0, 0, 0, 0);

	final RangeType at;

	public final int min;
	public final int max;
	public final int minH;
	public final int maxH;

	Range(RangeType at, int min, int max, int minH, int maxH) {
		this.at = at;
		this.min = min;
		this.max = max;
		this.minH = minH;
		this.maxH = maxH;
	}

	Range(RangeType at, int max, int maxH) {
		this.at = at;
		this.min = 0;
		this.max = max;
		this.minH = 0;
		this.maxH = maxH;
	}

}
