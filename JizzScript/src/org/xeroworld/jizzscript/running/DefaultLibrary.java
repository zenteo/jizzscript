package org.xeroworld.jizzscript.running;

import java.util.ArrayList;
import java.util.HashMap;

import org.xeroworld.jizzscript.instructions.CodeInstruction;
import org.xeroworld.jizzscript.instructions.FunctionInstruction;
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
	
	public void addDefaults(Runner runner) {
		Variable jizzVar = runner.getVariable("jizz");
		if (jizzVar.getValue() == null || !(jizzVar.getValue() instanceof Instance)) {
			jizzVar.setValue(new Instance(null));
		}
		Variable mathVar = runner.getVariable("math");
		if (mathVar.getValue() == null || !(mathVar.getValue() instanceof Instance)) {
			mathVar.setValue(new Instance(null));
		}
		Instance jizz = (Instance)jizzVar.getValue();
		Instance math = (Instance)mathVar.getValue();
		jizz.getVariable("print").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException {
				try {
					Variable next;
					next = master.runNext();
					System.out.print(next.getValue());
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		jizz.getVariable("println").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException {
				try {
					Variable next;
					next = master.runNext();
					System.out.println(next.getValue());
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getVariable("pow").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException {
				try {
					Variable a = master.runNext();
					Variable b = master.runNext();
					return new Variable(Math.pow((Double)a.getValue(), (Double)b.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getVariable("cos").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.cos((Double)a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getVariable("sin").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.sin((Double)a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getVariable("abs").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.abs((Double)a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getVariable("sqrt").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.sqrt((Double)a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getVariable("exp").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.exp((Double)a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getVariable("floor").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.floor((Double)a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getVariable("ceil").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.ceil((Double)a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
	}
	
	private void addDefaultFunctions() {
		addFunction(new RunnerFunction("@") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.next();
				if (a.getValue() != null && a.getValue() instanceof Function) {
					return new Variable(new FunctionPointer((Function)a.getValue()));
				}
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("null") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("true") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				return new Variable(true);
			}
		});
		addFunction(new RunnerFunction("false") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				return new Variable(false);
			}
		});
		addFunction(new RunnerFunction("if") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.next();
				if (a.getValue() != null && a.getValue() instanceof Boolean) {
					if ((Boolean)a.getValue()) {
						runner.runVariable(b);
					}
					return new Variable(a.getValue());
				}
				return new Variable(false);
			}
		});
		addFunction(new RunnerFunction("while") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				int start = runner.getPosition();
				Variable a = runner.runNext();
				Variable b = runner.next();
				int end = runner.getPosition();
				runner.setPosition(start);
				while ((Boolean)runner.runNext().getValue()) {
					runner.runVariable(b);
					runner.setPosition(start);
				}
				runner.setPosition(end);
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("var") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException {
				Instruction ins = runner.getNextInstruction();
				runner.incPosition();
				if (ins != null && ins instanceof NameInstruction) {
					return runner.getVariable(((NameInstruction)ins).getName());
				}
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("contains") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Instruction ins = runner.getNextInstruction();
				runner.incPosition();
				if (a.getValue() == null || !(a.getValue() instanceof Instance)) {
					return new Variable(false);
				}
				if (ins != null && ins instanceof NameInstruction) {
					return new Variable(((Instance)a.getValue()).getVariables().containsKey(((NameInstruction)ins).getName()));
				}
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("pack") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = null;
				if (isFirst) {
					a = runner.runNext();
				}
				if (a != null && a.getValue() instanceof Instance) {
					Variable b = runner.next();
					if (b.getValue() != null && b.getValue() instanceof CodeInstruction) {
						Runner r = ((Instance)a.getValue()).derive(runner.getFunctionLibrary(), ((CodeInstruction)b.getValue()).getInstructions());
						r.setMaster(runner.getMaster());
						return r.runAndThrow();
					}
				}
				Variable b = runner.runNext();
				if (a != null) {
					a.setValue(b.getValue());
					return a;
				}
				return b;
			}
		});
		addFunction(new RunnerFunction("return") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				throw new ReturnException(a.getValue());
			}
		});
		addFunction(new RunnerFunction("=") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (b.getValue() != null && b.getValue() instanceof FunctionPointer) {
					a.setValue(((FunctionPointer)b.getValue()).getValue());
					return b;
				}
				else {
					a.setValue(b.getValue());
				}
				return a;
			}
		});
		addFunction(new RunnerFunction("||") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() != null && b.getValue() != null) {
					if (a.getValue() instanceof Boolean && b.getValue() instanceof Boolean) {
						return new Variable(((Boolean)a.getValue()) || ((Boolean)b.getValue()));
					}
				}
				//Throw Error!
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("&&") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() != null && b.getValue() != null) {
					if (a.getValue() instanceof Boolean && b.getValue() instanceof Boolean) {
						return new Variable(((Boolean)a.getValue()) && ((Boolean)b.getValue()));
					}
				}
				//Throw Error!
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("==") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() == null && b.getValue() == null)
					return new Variable(true);
				return new Variable(a.getValue().equals(b.getValue()));
			}
		});
		addFunction(new RunnerFunction("!=") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() == null && b.getValue() == null)
					return new Variable(false);
				return new Variable(!a.getValue().equals(b.getValue()));
			}
		});
		addFunction(new RunnerFunction(">=") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable((Double)a.getValue() >= (Double)b.getValue());
			}
		});
		addFunction(new RunnerFunction("<=") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable((Double)a.getValue() <= (Double)b.getValue());
			}
		});
		addFunction(new RunnerFunction(">") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable((Double)a.getValue() > (Double)b.getValue());
			}
		});
		addFunction(new RunnerFunction("<") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable((Double)a.getValue() < (Double)b.getValue());
			}
		});
		addFunction(new RunnerFunction("+=") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() instanceof String || b.getValue() instanceof String) {
					a.setValue(String.valueOf(a.getValue()) + String.valueOf(b.getValue()));
				}
				a.setValue((Double)a.getValue() + (Double)b.getValue());
				return a;
			}
		});
		addFunction(new RunnerFunction("-=") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				a.setValue((Double)a.getValue() - (Double)b.getValue());
				return a;
			}
		});
		addFunction(new RunnerFunction("*=") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				a.setValue((Double)a.getValue() * (Double)b.getValue());
				return a;
			}
		});
		addFunction(new RunnerFunction("/=") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				a.setValue((Double)a.getValue() / (Double)b.getValue());
				return a;
			}
		});
		addFunction(new RunnerFunction("++") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				a.setValue(((Double)a.getValue()) + 1.0);
				return a;
			}
		});
		addFunction(new RunnerFunction("--") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				a.setValue(((Double)a.getValue()) - 1.0);
				return a;
			}
		});
		addFunction(new RunnerFunction("+") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() instanceof String || b.getValue() instanceof String) {
					return new Variable(String.valueOf(a.getValue()) + String.valueOf(b.getValue()));
				}
				return new Variable((Double)a.getValue() + (Double)b.getValue());
			}
		});
		addFunction(new RunnerFunction("-") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable((Double)a.getValue() - (Double)b.getValue());
			}
		});
		addFunction(new RunnerFunction("*") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable((Double)a.getValue() * (Double)b.getValue());
			}
		});
		addFunction(new RunnerFunction("/") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable((Double)a.getValue() / (Double)b.getValue());
			}
		});
		addFunction(new RunnerFunction("$") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
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
		addFunction(new RunnerFunction("this") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				return new Variable(runner);
			}
		});
		addFunction(new RunnerFunction("super") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
				return new Variable(runner.getParent());
			}
		});
		addFunction(new RunnerFunction("func") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
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
				throw new ScriptException("Should be: func name {...} or ... func {...}");
			}
		});
		addFunction(new RunnerFunction(".") {
			public Variable run(Runner runner, boolean isFirst) throws ReturnException, ScriptException {
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
				else if (ins instanceof FunctionInstruction) {
					int id = ((FunctionInstruction)ins).getFunctionId();
					varName = functions.get(id).getName();
				}
				else {
					return new Variable();
				}
				Instance instance = ((Instance)a.getValue());
				return instance.getField(varName);
			}
		});
	}
	
	public void addFunction(RunnerFunction function) {
		mapping.put(function.getName(), functions.size());
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
	public Variable runFunction(int id, Runner runner, boolean isFirst) throws ReturnException, ScriptException {
		return functions.get(id).run(runner, isFirst);
	}

}
