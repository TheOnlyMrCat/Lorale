package com.dockdev.lorale;

import java.util.ArrayList;

import com.dockdev.lorale.exeption.LoraleException;

public class LoraleObject {

	public static final LoraleObject loraleConsole = new LoraleObject("com.dockdev.lorale.console").asConsole();

	private LoraleObject asConsole() {
		methods.add(new Method("print"))
	}

	private final String type;

	private ArrayList<Method> methods = new ArrayList<>();

	LoraleObject(String type) {
		this.type = type;
	}

	/**
	 * Defines a method in a {@link com.dockdev.lorale.LoraleObject LoraleObject}
	 * @param <R> the return type of the method
	 */
	public class Method<R extends LoraleObject> {

		private final String name;
		private final String body;

		private final ArrayList<String> argnames = new ArrayList<>();
		private final ArrayList<String> argtypes = new ArrayList<>();

		private Method(String name) {
			this.name = name;
			body = "";
		}

		public Method(String name, String body, String... args) {
			this.name = name;
			this.body = body;
			for (String arg : args) {
				String[] split = arg.split(":");
				argnames.add(split[0]); argtypes.add(split[1]);
			}
		}

		public R invoke(LoraleObject... args) {
			Interpreter interpreter = new Interpreter();
			if (args.length < argnames.size()) throw new LoraleException(String.format("Not enough arguments for method %s", name));
			else if (args.length > argnames.size()) throw new LoraleException(String.format("Too many arguments for method %s", name));
			try {
				for (int i = 0; i < args.length; i++) {
					assert argtypes.get(i).equals(args[i].type);
					interpreter.addVariable(argnames.get(i), args[i]);
				}
			} catch (AssertionError e) {
				throw new LoraleException("Type of object has not been preserved in method args", e);
			}
			return interpreter.interpret(body, this);
		}
	}

	private class JavaMethod extends Method {

		private JavaMethod(String name) {
			super(name);
		}
	}
}
