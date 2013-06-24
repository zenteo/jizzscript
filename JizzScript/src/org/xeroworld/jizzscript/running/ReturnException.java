package org.xeroworld.jizzscript.running;

public class ReturnException extends Exception {
	private Variable variable;
	
	public ReturnException(Variable variable) {
		this.variable = variable;
	}
	
	public Variable getVariable() {
		return variable;
	}
}
