package com.awesome.test;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

public class PrivateAccessor {

	private static Map<Class<?>, Class<?>> unboxing;

	static {
		unboxing = new HashMap<Class<?>, Class<?>>();
		unboxing.put(Boolean.class, boolean.class);
		unboxing.put(Byte.class, byte.class);
		unboxing.put(Character.class, char.class);
		unboxing.put(Short.class, short.class);
		unboxing.put(Integer.class, int.class);
		unboxing.put(Long.class, long.class);
		unboxing.put(Float.class, float.class);
		unboxing.put(Double.class, double.class);
		unboxing.put(Void.class, void.class);
	}

	private Map<String, Field> fields;

	private Map<String, Map<List<Class<?>>, Method>> methods;

	public <T> PrivateAccessor(Class<T> clas) {


		fields = new HashMap<String, Field>();
		for(Field f : clas.getDeclaredFields()) {
			fields.put(f.getName(), f);
			f.setAccessible(true);
		}

		methods = new HashMap<String, Map<List<Class<?>>, Method>>();
		for(Method m : clas.getDeclaredMethods()) {
			Map<List<Class<?>>, Method> map = methods.containsKey(m.getName()) ? methods.get(m.getName()) : new HashMap<List<Class<?>>, Method>();
			map.put(Arrays.asList(m.getParameterTypes()), m);
			methods.put(m.getName(), map);
			m.setAccessible(true);
		}
	}

	@SuppressWarnings("unchecked")
	public <C, F> F field(C c, String n) {
		Field field = fields.get(n);
		F f = null;
		try {
			f = (F)field.get(c);
		} catch (IllegalArgumentException e) {
			e.printStackTrace();
		} catch (IllegalAccessException e) {
			e.printStackTrace();
		}

		return f;
	}

	public <C, M> M method(C c, String n) {
		return method(c, n, (Object[])null);
	}

	@SuppressWarnings("unchecked")
	public <C, M> M method(C c, String n, Object... args) {
		List<Class<?>> cs;
		if(args == null) {
			cs = new ArrayList<Class<?>>();
		} else {
			cs = new ArrayList<Class<?>>(args.length);
			for(Object o : args)
				if(o == null)
					cs.add(null);
				else
					cs.add(o.getClass());
		}

		List<Class<?>> matchParams = null;
		Map<List<Class<?>>,Method> map = methods.get(n);
		Set<List<Class<?>>> keySet = map.keySet();

		for(List<Class<?>> params : keySet) {
			boolean match = true;
			Iterator<Class<?>> argitr = cs.iterator();
			Iterator<Class<?>> prmitr = params.iterator();

			while(prmitr.hasNext() && argitr.hasNext()) {
				Class<?> argc = argitr.next();
				Class<?> prmc = prmitr.next();

				if(argc == null)
					continue;

				if(prmc.isPrimitive())
					if(unboxing.containsKey(argc))
						argc = unboxing.get(argc);
					else {
						match = false;
						break;
					}

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

		if(matchParams == null) {
			System.err.println("args type : " + cs);
			System.err.println("params types : " + keySet);
			System.err.println("didn't match.");
		}

		Method method = map.get(matchParams);

		M m = null;
		try {
			m = (M)method.invoke(c, args);
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
