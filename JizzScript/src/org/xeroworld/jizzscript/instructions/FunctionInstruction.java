package org.xeroworld.jizzscript.instructions;

import org.xeroworld.jizzscript.parsing.Codeblock;

public class FunctionInstruction extends Instruction {
	private int functionId;
	
	public FunctionInstruction(int functionId) {
		super();
		this.functionId = functionId;
	}
	
	public FunctionInstruction(Codeblock codeblock, int functionId) {
		super(codeblock);
		this.functionId = functionId;
	}
	
	public int getFunctionId() {
		return functionId;
	}
}
