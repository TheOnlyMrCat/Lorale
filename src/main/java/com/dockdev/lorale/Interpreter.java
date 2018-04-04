package com.dockdev.lorale;

import com.dockdev.lorale.exeption.InitializationError;
import com.dockdev.lorale.exeption.LoraleException;
import com.dockdev.lorale.lang.LoraleObject;
import org.jetbrains.annotations.Contract;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.PrintStream;
import java.util.Map;
import java.util.Scanner;

public class Interpreter {

	public static PrintStream console = System.out;

	public static void main(String[] args) {
		Interpreter interpreter = new Interpreter();
		String input = args[0];
		if (input.charAt(0) == ';') {
			interpreter.object = new LoraleObject(input.substring(1, input.indexOf('{', 1)));
			interpreter.interpret(input.substring(input.indexOf('{')+1), null);
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
				String line = scanner.nextLine();
				interpreter.object = new LoraleObject(line.substring(1, line.indexOf('{', 1)));

				interpreter.interpret(line, null);
				while (scanner.hasNextLine()) {
					interpreter.interpret(scanner.nextLine(), null);
				}
			}
		}
	}

	private LoraleObject object;

	public <R extends LoraleObject> R interpret(String code, LoraleObject.Method<R> method) {
		if (code.charAt(0) == ';') {
			code = code.substring(1);
		}
		LoraleObject previousObject;
		int endOfInstruction;
		for (int carat = 0; carat < code.length(); carat = endOfInstruction + 1) {
			endOfInstruction = code.indexOf(';', carat);
			switch (code.charAt(carat)) {
				case '§': handleVariable(code.substring(carat+1, endOfInstruction)); carat = endOfInstruction+1; break;
				case '₤': previousObject = object.getVariable(code.substring(carat+1, endOfInstruction));
				case '¶': handleMethod("print", code.substring(carat+1, endOfInstruction), LoraleObject.loraleConsole); break;
				case '}':
					try {
						return (R) object.getVariable(code.substring(carat + 1, endOfInstruction));
					} catch (ClassCastException e) {
						throw new LoraleException("return type not respected");
					}
				default: throw new LoraleException(String.format("Unknown instruction: %s", code.charAt(carat)));
			}
		}
		if (method != null) throw new LoraleException("No return statement found in method " + method);
		return null;
	}

	@Contract("_, _, null -> fail")
	private LoraleObject handleMethod(String name, String body, LoraleObject invokedUpon) {
		if (invokedUpon == null) throw new LoraleException("");
		String[] args = body.split(",");
		LoraleObject[] argobjects = new LoraleObject[args.length];
		for (int i = 0; i < args.length; i++) {
			//TODO implement constants
			argobjects[i] = object.getVariable(args[i]);
		}
		return invokedUpon.invokeMethod(name, argobjects);
	}

	private  void handleVariable(String extract) {
		String[] split = extract.split("=");
		Map<String, LoraleObject> variables = object.getAllVariables();
		char type = split[0].charAt(0);
		try {
			switch (type) {
				case 'i':
					variables.put(split[0], LoraleObject.asInteger(split[1]));
//				case 'l':
//					variables.put(split[0], LoraleObject.asLong(split[1]));
//				case 'd':
//					variables.put(split[0], LoraleObject.asDouble(split[1]));
//				case 'f':
//					variables.put(split[0], LoraleObject.asFloat(split[1]));
//				case 'b':
//					variables.put(split[0], LoraleObject.asBoolean(split[1]));
//				case 'c':
//					variables.put(split[0], LoraleObject.asCharacter(split[1]));
				case 's':
					variables.put(split[0], LoraleObject.asString(split[1]));
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
