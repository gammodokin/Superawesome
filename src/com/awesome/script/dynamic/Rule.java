package com.awesome.script.dynamic;

import java.io.Serializable;

import com.awesome.script.Line;

public class Rule implements Serializable {

	/**
	 *
	 */
	private static final long serialVersionUID = 1L;

	private int weight = DynamicScripting.Winit;
	private Line line;
	transient private boolean activated = false;

	public Rule(Line line) {
		super();
		this.line = line;
	}

	int getWeight() { return weight; }

	int updateWeight(int update) {
		weight += update;
		return weight;
	}

	void setWeight(int w) {
		weight = w;
	}

	public Line getLine() { return line;}

	public void initActivated() {
		activated = false;
	}

	public void activate() {
		activated = true;
	}

	public boolean isActivated() { return activated; }

	@Override
	public String toString() {
		return "\t|W:" + weight + ",Line:" + line + "|\n";
	}

	@Override
	public boolean equals(Object obj) {
		Rule r = (Rule)obj;
		return line.equals(r.getLine());
	}

}
