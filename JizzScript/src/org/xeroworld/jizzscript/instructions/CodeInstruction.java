package org.xeroworld.jizzscript.instructions;

import java.util.ArrayList;

import org.xeroworld.jizzscript.parsing.Codeblock;

public class CodeInstruction extends Instruction {
	private ArrayList<Instruction> instructions;
	
	public CodeInstruction(Codeblock codeblock, ArrayList<Instruction> instructions) {
		super(codeblock);
		this.instructions = instructions;
	}
	
	public ArrayList<Instruction> getInstructions() {
		return instructions;
	}
}
