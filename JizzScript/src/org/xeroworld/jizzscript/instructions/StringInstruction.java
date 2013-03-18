package org.xeroworld.jizzscript.instructions;

import org.xeroworld.jizzscript.parsing.Codeblock;

public class StringInstruction extends Instruction {
	private String value;
	
	public StringInstruction(String value) {
		super();
		this.value = value;
	}
	
	public StringInstruction(Codeblock codeblock, String value) {
		super(codeblock);
		this.value = value;
	}
	
	public String getValue() {
		return value;
	}
}
