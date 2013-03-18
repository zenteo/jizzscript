package org.xeroworld.jizzscript.running;

interface RunnerFunction {
	public Variable run(Runner runner, boolean isFirst) throws ReturnException;
}
