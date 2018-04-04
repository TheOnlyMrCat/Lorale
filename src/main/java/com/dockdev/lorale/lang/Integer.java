package com.dockdev.lorale.lang;

public class Integer extends LoraleObject {

	private int value;

	public Integer() {
		super("lorale.lang.integer");
	}

	public Integer(int value) {
		this();
		this.value = value;
	}

	public java.lang.String toString() {
		return java.lang.String.valueOf(value);
	}
}
