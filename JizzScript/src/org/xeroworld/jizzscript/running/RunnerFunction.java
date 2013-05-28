package org.xeroworld.jizzscript.running;

public abstract class RunnerFunction {
	private String name;
	
	public RunnerFunction(String name) {
		this.name = name;
	}
	
	public abstract Variable run(Runner runner, boolean isFirst)
			throws ReturnException, ScriptException, JizzException;

	public String getName() {
		return name;
	}
}
