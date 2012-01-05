package com.awesome.srpg.logic;

import java.io.Serializable;

public class UnusualStatus implements Serializable {

	public static final UnusualStatus
		NONE = new UnusualStatus("�ُ�Ȃ�", TargetStatus.STR, 1.0f),
		INTENCE_INT = new UnusualStatus("���͋������", TargetStatus.INT, 1.8f),
		INTENCE_STR = new UnusualStatus("�͋������", TargetStatus.STR, 4.0f);

	enum TargetStatus {
		STR, INT, DEF, REG, SPD, MOV;
	}

	private String name;
	private TargetStatus target;
	private float effect;
	// TODO ���ʊ�������

	public UnusualStatus(String name, TargetStatus target, float effect) {
		super();
		this.name = name;
		this.target = target;
		this.effect = effect;
	}

	public int calcEffect(int s, TargetStatus t) {
		if(t == target)
			return (int)(s * effect);
		else
			return s;
	}

	@Override
	public boolean equals(Object obj) {
		return ((UnusualStatus)obj).name.equals(name);
	}

	@Override
	public String toString() {
		return name;
	}

}