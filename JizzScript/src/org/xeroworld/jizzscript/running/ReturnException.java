package org.xeroworld.jizzscript.running;

public class ReturnException extends Exception {
	private Object value;
	
	public ReturnException(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}
}
