package org.xeroworld.jizzscript.running;

import java.util.ArrayList;

import org.xeroworld.jizzscript.instructions.Instruction;

public class ScriptFunction extends Function {
	private ArrayList<Instruction> instructions;
	
	public ScriptFunction(Runner instance, ArrayList<Instruction> instructions) {
		super(instance);
		this.instructions = instructions;
	}
	
	@Override
	public Variable run(Runner master) throws ScriptException {
		Runner r = new Runner(getInstance().getFunctionLibrary(), getInstance(), instructions);
		r.setMaster(master);
		return r.run();
	}
}
