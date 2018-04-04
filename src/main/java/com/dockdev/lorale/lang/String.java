package com.dockdev.lorale.lang;

public class String extends LoraleObject {

	java.lang.String value;

	public String() {
		super("lorale.lang.string");
	}

	public String(java.lang.String value) {
		this();
		this.value = value;
	}

	public java.lang.String toString() {
		return value;
	}
}
