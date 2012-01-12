package com.awesome.test;

import static org.junit.Assert.*;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class PrivateAccessorTest {

	private static final int I = 9;
	private static final float F = 0.023f;
	private static final String STR = "string";
	private static final String METHOD1 = "method1";
	private static final String METHOD2 = "method2";
	private static final String METHOD3 = "method3";
	private static final String METHOD4 = "method4";

	private PrivateAccessor pa;

	@Before
	public void setUp() throws Exception {
		pa = new PrivateAccessor(TestClass.class);
	}

	@After
	public void tearDown() throws Exception {
	}

	@Test
	public void testField() {
		TestClass tc = new TestClass();
		assertEquals(pa.field(tc, "i"), I);
		assertEquals(pa.field(tc, "str"), STR);
		assertEquals(pa.field(tc, "strnull"), null);
//		assertEquals(pa.field(tc, "f"), F);		// Ç±ÇÍÇÕñ≥óù
	}

	@Test
	public void testMethod() {
		TestClass tc = new TestClass();
		assertEquals(pa.method(tc, "method1", null), METHOD1);
		assertEquals(pa.method(tc, "method2", METHOD2), METHOD2 + METHOD2);
		assertEquals(pa.method(tc, "method3", I, METHOD3), METHOD3 + 1 + I + METHOD3);
		assertEquals(pa.method(tc, "method3", I), METHOD3 + 2 + I);
		assertEquals(pa.method(tc, "method3", METHOD3), METHOD3 + 3 + METHOD3);
//		assertEquals(pa.method(tc, "method3", I), METHOD3 + 4 + I);	// Ç±ÇÍÇ‡ñ≥óùÇ€
		assertEquals(pa.method(tc, "method4", METHOD4, F), METHOD4 + 1 + METHOD4 + F);
//		assertEquals(pa.method(tc, "method4", METHOD4, I), METHOD4 + 2 + METHOD4 + I);	// Ç±Ç§Ç¢Ç§ÇÃÇ‡ñ≥óùÇ€ÅBÇ∆Ç¢Ç§Ç©åæåÍédólÇ∆ÇµÇƒÇ«Ç§Ç»Ç¡ÇƒÇÈÇ©ÇÌÇ©ÇËÇ‹ÇπÇÒÅ_(^o^)Å^
	}

	class TestSuperClass {
		private float f = F;
	}

	class TestClass extends TestSuperClass {
		private int i = I;
		private String str = STR;
		private String strnull = null;

		private String method1() {
			return METHOD1;
		}

		private String method2(String str) {
			return METHOD2 + str;
		}

		private String method3(Object obj, String str) {
			return METHOD3 + 1 + obj + str;
		}

		private Object method3(Object obj) {
			return METHOD3 + 2 + obj.toString();
		}

		private String method3(String str) {
			return METHOD3 + 3 + str;
		}

		private String method3(int i) {
			return METHOD3 + 4 + i;
		}

		private Object method4(Object obj, Number num) {
			return METHOD4 + 1 + obj + num;
		}

		private String method4(String str, Integer i) {
			return METHOD4 + 2 + str + i;
		}
	}
}
