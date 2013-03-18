package org.xeroworld.jizzscript.running;

import java.util.ArrayList;

import org.xeroworld.jizzscript.instructions.Instruction;

public class ScriptFunction {
	private Runner instance;
	private ArrayList<Instruction> instructions;
	
	public ScriptFunction(Runner instance, ArrayList<Instruction> instructions) {
		this.instance = instance;
		this.instructions = instructions;
	}
	
	public Variable run(Runner master) {
		Runner r = new Runner(instance.getFunctionLibrary(), instance, instructions);
		r.setMaster(master);
		return r.run();
	}
}
