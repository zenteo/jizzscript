package org.xeroworld.jizzscript.instructions;

import org.xeroworld.jizzscript.parsing.Codeblock;

public class NameInstruction extends Instruction {
	private String name;
	
	public NameInstruction(String name) {
		super();
		this.name = name;
	}
	
	public NameInstruction(Codeblock codeblock, String name) {
		super(codeblock);
		this.name = name;
	}
	
	public String getName() {
		return name;
	}
}
