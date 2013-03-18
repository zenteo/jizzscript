package org.xeroworld.jizzscript.instructions;

import org.xeroworld.jizzscript.parsing.Codeblock;

public abstract class Instruction {
	private Codeblock codeblock;
	
	public Instruction() {
		this.codeblock = null;
	}
	
	public Instruction(Codeblock codeblock) {
		this.codeblock = codeblock;
	}
	
	public Codeblock getCodeblock() {
		return codeblock;
	}
}
