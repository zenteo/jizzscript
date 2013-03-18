package org.xeroworld.jizzscript.running;

public class FunctionPointer {
	private Function value;
	
	public FunctionPointer(Function value) {
		this.value = value;
	}
	
	public Function getValue() {
		return value;
	}
}
