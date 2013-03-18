package org.xeroworld.jizzscript.running;

public class FunctionPointer {
	private ScriptFunction value;
	
	public FunctionPointer(ScriptFunction value) {
		this.value = value;
	}
	
	public ScriptFunction getValue() {
		return value;
	}
}
