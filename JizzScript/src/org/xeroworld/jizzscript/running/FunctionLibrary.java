package org.xeroworld.jizzscript.running;

public interface FunctionLibrary {
	public boolean hasFunction(String name);
	public int getFunction(String name);
	public Variable runFunction(int id, Runner runner, boolean isFirst) throws ReturnException;
}
