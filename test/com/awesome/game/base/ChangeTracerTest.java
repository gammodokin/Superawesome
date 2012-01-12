package com.awesome.game.base;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ChangeTracerTest {

	private ChangeTracer<String> ct;

	@Before
	public void setUp() throws Exception {
		ct = new ChangeTracer<String>("test1");
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testIsChanged() {
		assertEquals(ct.isChanged("test1"), false);
		assertEquals(ct.isChanged("test2"), true);
		assertEquals(ct.isChanged(null), true);
		assertEquals(ct.isChanged(null), false);
		assertEquals(ct.isChanged("test3"), true);
	}

}
