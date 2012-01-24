package com.awesome.script.macro;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import com.awesome.script.StaticScript;
import com.awesome.test.PrivateAccessor;

public class EpochRecorderTest {

	private PrivateAccessor pa;
	private EpochRecorder recorder;

	@Before
	public void setUp() throws Exception {
		pa = new PrivateAccessor(EpochRecorder.class);

		UnitIDLM.useGdx(false);
		UnitIDLM.WIZ0.load();
		recorder = new EpochRecorder(100, UnitIDLM.WIZ0.getLearningMacroAction(), new StaticScript(StaticScript.RULE_WIZARD));
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testVdist() {

		double[] v1 = {0.5, 0.5, 0, 0};
		double[] v2 = {2.0, 0, 1.5, 0};

		double d = pa.method(recorder, "vdist", v1, v2);
		assertEquals(d, 2.179, 0.01);
	}

}
