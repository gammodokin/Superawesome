package com.awesome.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PrivateAccessor {

	private HashMap<String, Field> fields;

	private HashMap<String, HashMap<List<Class<?>>, Method>> methods;

	public <T> PrivateAccessor(Class<T> clas) {

		fields = new HashMap<String, Field>();
		for(Field f : clas.getDeclaredFields()) {
			fields.put(f.getName(), f);
			f.setAccessible(true);
		}

		methods = new HashMap<String, HashMap<List<Class<?>>, Method>>();
		for(Method m : clas.getDeclaredMethods()) {
			HashMap<List<Class<?>>, Method> map = methods.containsKey(m.getName()) ? methods.get(m.getName()) : new HashMap<List<Class<?>>, Method>();
			map.put(Arrays.asList(m.getParameterTypes()), m);
			methods.put(m.getName(), map);
			m.setAccessible(true);
		}

		// プライベートプロパティを取得
//		field = c.getDeclaredField("privateField");
		// アクセス可能にする
//		field.setAccessible(true);
		// 値を取得して、テスト
//		assertEquals(0, field.getInt(privateOnlyClass));

		// プライベートメソッドを取得
//		Class[] params = new Class[] {};
//		method = c.getDeclaredMethod("privateMethod", params);
		// アクセス可能にする
//		method.setAccessible(true);
		// メソッドコール
//		method.invoke(privateOnlyClass, null);
	}

	@SuppressWarnings("unchecked")
	public <C, F> F field(C c, String n) {
		Field field = fields.get(n);
		F f = null;
		try {
			Object o = field.get(c);
			f = o == null ? null : (F)o;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return f;
	}

	@SuppressWarnings("unchecked")
	public <C, M> M method(C c, String n, Object... args) {
		List<Class<?>> cs;
		if(args == null) {
			cs = new ArrayList<Class<?>>();
		} else {
			cs = new ArrayList<Class<?>>(args.length);
			for(Object o : args) {
				cs.add(o.getClass());
			}
		}

		List<Class<?>> matchParams = null;
		HashMap<List<Class<?>>,Method> map = methods.get(n);
		Set<List<Class<?>>> keySet = map.keySet();
		for(List<Class<?>> params : keySet) {
			boolean match = true;
			Iterator<Class<?>> argitr = cs.iterator();
			Iterator<Class<?>> prmitr = params.iterator();
			while(prmitr.hasNext() && argitr.hasNext()) {
				Class<?> argc = argitr.next();
				Class<?> prmc = prmitr.next();
				if(!prmc.isAssignableFrom(argc)) {
					match = false;
					break;
				}
			}

			if(prmitr.hasNext() || argitr.hasNext())
				match = false;

			if(match) {
				matchParams = params;
				break;
			}
		}

		Method method = map.get(matchParams);
		M m = null;
		try {
			Object o = method.invoke(c, args);
			m = o == null ? null : (M)o;
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		} catch (InvocationTargetException e) {
			e.printStackTrace();
		}

		return m;
	}

}
