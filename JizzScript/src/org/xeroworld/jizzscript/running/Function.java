package org.xeroworld.jizzscript.running;

import java.util.ArrayList;

import org.xeroworld.jizzscript.instructions.Instruction;

public abstract class Function {
	private Runner instance;
	
	public Function(Runner instance) {
		this.instance = instance;
	}
	
	public Runner getInstance() {
		return instance;
	}
	
	public abstract Variable run(Runner master) throws ScriptException, JizzException;
	
	public String toString() {
		return "func {}";
	}
}
