package com.dockdev.lorale.lang;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import com.dockdev.lorale.Interpreter;
import com.dockdev.lorale.exeption.LoraleException;
import org.jetbrains.annotations.Nullable;

/**
 * Defines an object declared in Lorale.
 * All such objects are subclasses
 */
public class LoraleObject {

	public static final LoraleObject loraleConsole = new LoraleObject("lorale.core.console").asConsole();

	public static LoraleObject asInteger(java.lang.String s) throws NumberFormatException {
		return new Integer(java.lang.Integer.parseInt(s));
	}

	public static LoraleObject asString(java.lang.String s) {
		return new String(s);
	}

	private LoraleObject asConsole() {
		methods.put("print", new JavaMethod("print"));
		return this;
	}

	private final java.lang.String type;

	Map<java.lang.String, Method> methods = new HashMap<>();
	Map<java.lang.String, LoraleObject> variables = new HashMap<>();

	public LoraleObject(java.lang.String type) {
		this.type = type;
	}

	public LoraleObject invokeMethod(java.lang.String name, LoraleObject[] argobjects) {
		return methods.get(name).invoke(this, argobjects);
	}

	public LoraleObject getVariable(java.lang.String name) {
		return variables.get(name);
	}

	public Map<java.lang.String, LoraleObject> getAllVariables() {
		return variables;
	}

	public void setVariable(java.lang.String name, LoraleObject to) {
		variables.put(name, to);
	}

	/**
	 * Defines a method in a {@link LoraleObject LoraleObject}
	 * @param <R> the return type of the method
	 */
	public class Method<R extends LoraleObject> {

		final java.lang.String name;
		private final java.lang.String body;

		private final ArrayList<java.lang.String> argnames = new ArrayList<>();
		private final ArrayList<java.lang.String> argtypes = new ArrayList<>();

		private Method(java.lang.String name) {
			this.name = name;
			body = "";
		}

		public Method(java.lang.String name, java.lang.String body, java.lang.String... args) {
			this.name = name;
			this.body = body;
			for (java.lang.String arg : args) {
				java.lang.String[] split = arg.split(":");
				argnames.add(split[0]); argtypes.add(split[1]);
			}
		}

		public R invoke(LoraleObject upon, LoraleObject... args) {
			Interpreter interpreter = new Interpreter();
			if (args.length < argnames.size()) throw new LoraleException(java.lang.String.format("not enough arguments for method %s", name));
			else if (args.length > argnames.size()) throw new LoraleException(java.lang.String.format("too many arguments for method %s", name));
			try {
				for (int i = 0; i < args.length; i++) {
					assert argtypes.get(i).equals(args[i].type);
					upon.setVariable(argnames.get(i), args[i]);
				}
			} catch (AssertionError e) {
				throw new LoraleException("type of object has not been preserved in method args", e);
			}
			return interpreter.interpret(body, this);
		}
	}

	private class JavaMethod<R extends LoraleObject> extends LoraleObject.Method<R> {

		private JavaMethod(java.lang.String name) {
			super(name);
		}

		@Override
		@Nullable
		public R invoke(LoraleObject upon, LoraleObject... args) {
			assert upon == null;
			switch (name) {
				case "print": for (LoraleObject object : args) Interpreter.console.print(object); Interpreter.console.println(); break;
			}
			return null;
		}
	}
}
