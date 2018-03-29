package com.dockdev.lorale;

import com.dockdev.lorale.exeption.InitializationError;
import com.dockdev.lorale.exeption.LoraleException;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;

public class Interpreter {

	public PrintStream console = System.out;

	private HashMap<String, LoraleObject> variables = new HashMap<>();
	
	public static void main(String[] args) {
		Interpreter interpreter = new Interpreter();
		String input = args[0];
		if (input.charAt(0) == ';') {
			interpreter.interpret(input, null);
		} else {
			File file = new File(input);
			if (file.exists()) {
				Scanner scanner = null;
				try {
					scanner = new Scanner(file);
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}
				assert scanner != null;
				while (scanner.hasNextLine()) {
					interpreter.interpret(scanner.nextLine(), null);
				}
			}
		}
	}

	<R extends LoraleObject> R interpret(String lorale, LoraleObject.Method<R> method) {
		if (lorale.charAt(0) == ';') {
			lorale = lorale.substring(1);
		}
		LoraleObject previousObject;
		for (int carat = 0; carat < lorale.length(); carat++) {
			int endOfInstruction = lorale.indexOf(';', carat);
			switch (lorale.charAt(carat)) {
				case '§': handleVariable(lorale.substring(carat+1, endOfInstruction)); carat = endOfInstruction+1; break;
				case '₤': previousObject = variables.get(lorale.substring(carat+1, endOfInstruction));
				case '¶': handleMethod(lorale.substring(carat+1, endOfInstruction > lorale.indexOf('.', carat) ? lorale.indexOf('.', carat) : endOfInstruction), previousObject); break;
				default: throw new LoraleException(String.format("Unknown instruction: %s", lorale.charAt(carat)));
			}
		}
	}

	private Object handleMethod(String name, String body, LoraleObject invokedUpon) {
		if (invokedUpon == null) throw new LoraleException("");
		String[] args = body.split(",");
		LoraleObject[] argobjects = new LoraleObject[args.length];
		for (int i = 0; i < args.length; i++) {
			if (canParseInt(args[i]) || (args[i].charAt(0) == '"' && args[0].charAt(1) == '"')) argobjects[i] = args[i];
			else argobjects[i] = variables.get(args[i]);
		}
		return invokedUpon.invokeMethod()
	}

	public void addVariable(String name, LoraleObject arg) {
		variables.put(name, arg);
	}

	private  void handleVariable(String extract) {
		String[] split = extract.split("=");
		Object value = split[1];
		char type = split[0].charAt(0);
		try {
			switch (type) {
				case 'i':
					variables.put(split[0], (Integer) value);
				case 'l':
					variables.put(split[0], (Long) value);
				case 'd':
					variables.put(split[0], (Double) value);
				case 'f':
					variables.put(split[0], (Float) value);
				case 'b':
					variables.put(split[0], (Boolean) value);
				case 'c':
					variables.put(split[0], (Character) value);
			}
		} catch (ClassCastException e) {
			throw new InitializationError("could not initialize variable: " + split[0], e);
		}
	}

	private static boolean canParseInt(String string) {
		try {
			Integer.parseInt(string);
			return true;
		} catch (NumberFormatException e) {
			return false;
		}
	}
}
