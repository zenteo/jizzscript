package org.xeroworld.jizzscript.instructions;

import org.xeroworld.jizzscript.parsing.Codeblock;

public class NumberInstruction extends Instruction {
	private double value;
	
	public NumberInstruction(double value) {
		super();
		this.value = value;
	}
	
	public NumberInstruction(Codeblock codeblock, double value) {
		super(codeblock);
		this.value = value;
	}
	
	public double getValue() {
		return value;
	}
}
