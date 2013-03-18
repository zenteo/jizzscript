package org.xeroworld.jizzscript.running;

import java.util.ArrayList;
import java.util.HashMap;

import org.xeroworld.jizzscript.instructions.CodeInstruction;
import org.xeroworld.jizzscript.instructions.Instruction;
import org.xeroworld.jizzscript.instructions.NameInstruction;
import org.xeroworld.jizzscript.instructions.NumberInstruction;

public class DefaultLibrary implements FunctionLibrary {
	private ArrayList<RunnerFunction> functions;
	private HashMap<String, Integer> mapping;
	
	public DefaultLibrary() {
		functions = new ArrayList<RunnerFunction>();
		mapping = new HashMap<String, Integer>();
		addDefaultFunctions();
	}
	
	private void addDefaultFunctions() {
		addFunction("print", new RunnerFunction() {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException {
				Variable next = runner.runNext();
				System.out.print(next.getValue());
				return new Variable();
			}
		});
		addFunction("println", new RunnerFunction() {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException {
				Variable next = runner.runNext();
				System.out.println(next.getValue());
				return new Variable();
			}
		});
		addFunction("=", new RunnerFunction() {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (b.getValue() != null && b.getValue() instanceof FunctionPointer) {
					a.setValue(((FunctionPointer)b.getValue()).getValue());
				}
				else {
					a.setValue(b.getValue());
				}
				return a;
			}
		});
		addFunction("+", new RunnerFunction() {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() instanceof String || b.getValue() instanceof String) {
					return new Variable(String.valueOf(a.getValue()) + String.valueOf(b.getValue()));
				}
				return new Variable((Double)a.getValue() + (Double)b.getValue());
			}
		});
		addFunction("-", new RunnerFunction() {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable((Double)a.getValue() - (Double)b.getValue());
			}
		});
		addFunction("*", new RunnerFunction() {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable((Double)a.getValue() * (Double)b.getValue());
			}
		});
		addFunction("/", new RunnerFunction() {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable((Double)a.getValue() / (Double)b.getValue());
			}
		});
		addFunction("$", new RunnerFunction() {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException {
				if (runner.getMaster() == null) {
					return new Variable();
				}
				Variable ret = runner.getMaster().runNext();
				if (isFirst) {
					Variable holder = runner.runNext();
					holder.setValue(ret.getValue());
				}
				return ret;
			}
		});
		addFunction("func", new RunnerFunction() {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException {
				Variable holder = null;
				if (isFirst) {
					holder = runner.runNext();
				}
				Variable code = runner.next();
				if (code.getValue() instanceof CodeInstruction) {
					ScriptFunction ret = new ScriptFunction(runner, ((CodeInstruction)code.getValue()).getInstructions());
					if (holder != null) {
						holder.setValue(ret);
					}
					return new Variable(new FunctionPointer(ret));
				}
				return new Variable();
			}
		});
		addFunction(".", new RunnerFunction() {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException {
				Variable a = runner.runNext();
				Instruction ins = runner.getNextInstruction();
				String varName = null;
				runner.incPosition();
				if (a.getValue() == null || !(a.getValue() instanceof Instance)) {
					a.setValue(new Instance(null));
				}
				if (ins instanceof NameInstruction) {
					varName = ((NameInstruction) ins).getName();
				}
				else if (ins instanceof NumberInstruction) {
					varName = String.valueOf((int)((NumberInstruction) ins).getValue());
				}
				else {
					return new Variable();
				}
				Instance instance = ((Instance)a.getValue());
				return instance.getVariable(varName);
			}
		});
	}
	
	public void addFunction(String name, RunnerFunction function) {
		mapping.put(name, functions.size());
		functions.add(function);
	}
	
	@Override
	public boolean hasFunction(String name) {
		return mapping.containsKey(name);
	}

	@Override
	public int getFunction(String name) {
		return mapping.get(name);
	}

	@Override
	public Variable runFunction(int id, Runner runner, boolean isFirst) throws ReturnException {
		return functions.get(id).run(runner, isFirst);
	}

}
