package org.xeroworld.jizzscript.running;

public class JizzException extends Exception {
	private Object value;
	
	public JizzException(Object value) {
		this.setValue(value);
	}

	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
}
