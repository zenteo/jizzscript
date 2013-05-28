package org.xeroworld.jizzscript.running;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import org.xeroworld.jizzscript.Compiler;
import org.xeroworld.jizzscript.instructions.CodeInstruction;
import org.xeroworld.jizzscript.instructions.FunctionInstruction;
import org.xeroworld.jizzscript.instructions.Instruction;
import org.xeroworld.jizzscript.instructions.NameInstruction;
import org.xeroworld.jizzscript.instructions.NumberInstruction;
import org.xeroworld.jizzscript.parsing.Codeblock;
import org.xeroworld.jizzscript.parsing.Metadata;

public class DefaultLibrary implements FunctionLibrary {
	private ArrayList<RunnerFunction> functions;
	private HashMap<String, Integer> mapping;

	public DefaultLibrary() {
		functions = new ArrayList<RunnerFunction>();
		mapping = new HashMap<String, Integer>();
		addDefaultFunctions();
	}

	public void addDefaults(Runner runner) {
		Variable jizzVar = runner.getField("jizz");
		if (jizzVar.getValue() == null
				|| !(jizzVar.getValue() instanceof Instance)) {
			jizzVar.setValue(new Instance(runner));
		}
		Variable mathVar = runner.getField("math");
		if (mathVar.getValue() == null
				|| !(mathVar.getValue() instanceof Instance)) {
			mathVar.setValue(new Instance(runner));
		}
		Instance jizz = (Instance) jizzVar.getValue();
		Instance math = (Instance) mathVar.getValue();
		jizz.getField("mains").setValue(new ListInstance(jizz));
		jizz.getField("include").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable next;
					next = master.runNext();
					if (next != null && next.getValue() instanceof String) {
						String filename = (String)next.getValue();
						StringBuilder builder = new StringBuilder();
						try (FileReader fr = new FileReader(filename)) {
							try (BufferedReader br = new BufferedReader(fr)) {
								String line;
								while ((line = br.readLine()) != null) {
									builder.append(line);
									builder.append('\n');
								}
							}
						} catch (IOException e) {
							e.printStackTrace();
						}
						Compiler compiler = new Compiler(master.getFunctionLibrary());
						Metadata m = new Metadata();
						m.setFilename(filename);
						compiler.addCode(new Codeblock(m, builder.toString(), 0, 0));
						Runner r = master.derive(master.getFunctionLibrary(), compiler.getInstructions());
						return r.run();
					}
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		jizz.getField("execute").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable next;
					next = master.runNext();
					if (next != null && next.getValue() instanceof String) {
						
						Compiler compiler = new Compiler(master.getFunctionLibrary());
						compiler.addCode((String)next.getValue());
						Runner r = master.derive(master.getFunctionLibrary(), compiler.getInstructions());
						return r.run();
					}
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		jizz.getField("print").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
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
		jizz.getField("println").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
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
		math.getField("pow").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable a = master.runNext();
					Variable b = master.runNext();
					return new Variable(Math.pow((Double) a.getValue(),
							(Double) b.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getField("cos").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.cos((Double) a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getField("sin").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.sin((Double) a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getField("abs").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.abs((Double) a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getField("sqrt").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.sqrt((Double) a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getField("exp").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.exp((Double) a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getField("floor").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.floor((Double) a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
		math.getField("ceil").setValue(new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable a = master.runNext();
					return new Variable(Math.ceil((Double) a.getValue()));
				} catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		});
	}

	private void addDefaultFunctions() {
		addFunction(new RunnerFunction("@") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.next();
				if (a.getValue() != null && a.getValue() instanceof Function) {
					return new Variable(new FunctionPointer(
							(Function) a.getValue()));
				}
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("null") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException {
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("true") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException {
				return new Variable(true);
			}
		});
		addFunction(new RunnerFunction(";") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException {
				return new Variable(new Stop());
			}
		});
		addFunction(new RunnerFunction("false") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException {
				return new Variable(false);
			}
		});
		addFunction(new RunnerFunction("if") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.next();
				runner.setTestFlag(false);
				if (a.getValue() != null && a.getValue() instanceof Boolean) {
					if ((Boolean) a.getValue()) {
						runner.setTestFlag(true);
						runner.runVariable(b);
					}
					return new Variable(a.getValue());
				}
				return new Variable(false);
			}
		});
		addFunction(new RunnerFunction("else") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.next();
				boolean doElse = runner.isTestFlag();
				if (!doElse) {
					runner.setTestFlag(true);
					runner.runVariable(a);
					return new Variable(true);
				}
				return new Variable(false);
			}
		});
		addFunction(new RunnerFunction("elseif") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.next();
				boolean doElse = runner.isTestFlag();
				if (!doElse) {
					if (a.getValue() != null && a.getValue() instanceof Boolean) {
						if ((Boolean) a.getValue()) {
							runner.setTestFlag(true);
							runner.runVariable(b);
						}
						return new Variable(a.getValue());
					}
				}
				return new Variable(false);
			}
		});
		addFunction(new RunnerFunction("while") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				int start = runner.getPosition();
				Variable a = runner.runNext();
				Variable b = runner.next();
				int end = runner.getPosition();
				runner.setPosition(start);
				while ((Boolean) a.getValue()) {
					runner.runVariable(b);
					runner.setPosition(start);
					a = runner.runNext();
				}
				runner.setPosition(end);
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("for") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				runner.runNext(); // Init
				int start = runner.getPosition();
				Variable a = runner.runNext(); // Condition
				Variable b = runner.next();
				Variable c = runner.next();
				int end = runner.getPosition();
				runner.setPosition(start);
				while ((Boolean) a.getValue()) {
					runner.runVariable(c);
					runner.runVariable(b);
					runner.setPosition(start);
					a = runner.runNext();
				}
				runner.setPosition(end);
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("var") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException {
				Instruction ins = runner.getNextInstruction();
				runner.incPosition();
				if (ins != null && ins instanceof NameInstruction) {
					return runner
							.getVariable(((NameInstruction) ins).getName());
				}
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("isNumber") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable v = runner.runNext();
				if (v != null && v.getValue() instanceof Double) {
					return new Variable(true);
				}
				return new Variable(false);
			}
		});
		addFunction(new RunnerFunction("isString") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable v = runner.runNext();
				if (v != null && v.getValue() instanceof String) {
					return new Variable(true);
				}
				return new Variable(false);
			}
		});
		addFunction(new RunnerFunction("isInstance") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable v = runner.runNext();
				if (v != null && v.getValue() instanceof Instance) {
					return new Variable(true);
				}
				return new Variable(false);
			}
		});
		addFunction(new RunnerFunction("isList") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable v = runner.runNext();
				if (v != null && v.getValue() instanceof ListInstance) {
					return new Variable(true);
				}
				return new Variable(false);
			}
		});
		addFunction(new RunnerFunction("contains") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Instruction ins = runner.getNextInstruction();
				runner.incPosition();
				if (ins == null || a.getValue() == null
						|| !(a.getValue() instanceof Instance)) {
					return new Variable(false);
				}
				Instance ains = (Instance) a.getValue();
				if (ins instanceof CodeInstruction) {
					CodeInstruction ci = (CodeInstruction) ins;
					for (Instruction ciIns : ci.getInstructions()) {
						if (ciIns != null && ciIns instanceof NameInstruction) {
							if (!ains.getVariables().containsKey(
									((NameInstruction) ciIns).getName())) {
								return new Variable(false);
							}
						}
					}
					return new Variable(true);
				}
				if (ins != null && ins instanceof NameInstruction) {
					return new Variable(((Instance) a.getValue())
							.getVariables().containsKey(
									((NameInstruction) ins).getName()));
				}
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("interfaceOf") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Interface ret = new Interface();
				if (a.getValue() != null && a.getValue() instanceof Instance) {
					Instance ins = (Instance) a.getValue();
					Iterator<Entry<String, Variable>> it = ins.getVariables()
							.entrySet().iterator();
					while (it.hasNext()) {
						Entry<String, Variable> entry = it.next();
						ret.getNames().add(entry.getKey());
					}
				}
				return new Variable(ret);
			}
		});
		addFunction(new RunnerFunction("interface") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable holder = null;
				if (isFirst) {
					holder = runner.runNext();
				}
				Instruction ins = runner.getNextInstruction();
				runner.incPosition();
				Interface ret = new Interface();
				if (ins instanceof CodeInstruction) {
					for (Instruction ciIns : ((CodeInstruction) ins)
							.getInstructions()) {
						if (ciIns != null && ciIns instanceof NameInstruction) {
							ret.getNames().add(
									((NameInstruction) ciIns).getName());
						}
					}
				}
				if (ins != null && ins instanceof NameInstruction) {
					ret.getNames().add(((NameInstruction) ins).getName());
				}
				if (holder != null) {
					holder.setValue(ret);
					return holder;
				}
				return new Variable(ret);
			}
		});
		addFunction(new RunnerFunction("matches") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() != null && a.getValue() instanceof Instance) {
					if (b.getValue() != null) {
						if (b.getValue() instanceof Interface) {
							for (String name : ((Interface) b.getValue())
									.getNames()) {
								if (!((Instance) a.getValue()).getVariables()
										.containsKey(name)) {
									return new Variable(false);
								}
							}
							return new Variable(true);
						}
					}
				}
				return new Variable(false);
			}
		});
		addFunction(new RunnerFunction("pack") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = null;
				if (isFirst) {
					a = runner.runNext();
				}
				if (a != null && a.getValue() instanceof Instance) {
					Variable b = runner.next();
					if (b.getValue() != null
							&& b.getValue() instanceof CodeInstruction) {
						Runner r = ((Instance) a.getValue()).derive(runner
								.getFunctionLibrary(), ((CodeInstruction) b
								.getValue()).getInstructions());
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
		addFunction(new RunnerFunction("throw") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				throw new JizzException(a.getValue());
			}
		});
		addFunction(new RunnerFunction("try") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.next();
				Variable b = runner.runNext();
				Variable c = runner.next();
				try {
					runner.runVariable(a);
				} catch (JizzException ex) {
					b.setValue(ex.getValue());
					runner.runVariable(c);
					return new Variable(false);
				}
				return new Variable(true);
			}
		});
		addFunction(new RunnerFunction("return") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				throw new ReturnException(a.getValue());
			}
		});
		addFunction(new RunnerFunction("=") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (b.getValue() != null
						&& b.getValue() instanceof FunctionPointer) {
					a.setValue(((FunctionPointer) b.getValue()).getValue());
					return b;
				} else {
					a.setValue(b.getValue());
				}
				return a;
			}
		});
		addFunction(new RunnerFunction("||") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() != null && b.getValue() != null) {
					if (a.getValue() instanceof Boolean
							&& b.getValue() instanceof Boolean) {
						return new Variable(((Boolean) a.getValue())
								|| ((Boolean) b.getValue()));
					}
				}
				// Throw Error!
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("&&") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() != null && b.getValue() != null) {
					if (a.getValue() instanceof Boolean
							&& b.getValue() instanceof Boolean) {
						return new Variable(((Boolean) a.getValue())
								&& ((Boolean) b.getValue()));
					}
				}
				// Throw Error!
				return new Variable();
			}
		});
		addFunction(new RunnerFunction("!") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				if (a.getValue() == null)
					return new Variable(true);
				if (a.getValue() instanceof Boolean) {
					return new Variable(!(boolean) a.getValue());
				}
				if (a.getValue() instanceof Double) {
					return new Variable((double) a.getValue() > 0.0);
				}
				return new Variable(false);
			}
		});
		addFunction(new RunnerFunction("==") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() == null && b.getValue() == null)
					return new Variable(true);
				if (a.getValue() == null)
					return new Variable(false);
				return new Variable(a.getValue().equals(b.getValue()));
			}
		});
		addFunction(new RunnerFunction("!=") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() == null && b.getValue() == null)
					return new Variable(false);
				if (a.getValue() == null)
					return new Variable(true);
				return new Variable(!a.getValue().equals(b.getValue()));
			}
		});
		addFunction(new RunnerFunction(">=") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable(
						(Double) a.getValue() >= (Double) b.getValue());
			}
		});
		addFunction(new RunnerFunction("<=") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable(
						(Double) a.getValue() <= (Double) b.getValue());
			}
		});
		addFunction(new RunnerFunction(">") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable(
						(Double) a.getValue() > (Double) b.getValue());
			}
		});
		addFunction(new RunnerFunction("<") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable(
						(Double) a.getValue() < (Double) b.getValue());
			}
		});
		addFunction(new RunnerFunction("+=") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() instanceof String
						|| b.getValue() instanceof String) {
					a.setValue(String.valueOf(a.getValue())
							+ String.valueOf(b.getValue()));
				}
				a.setValue((Double) a.getValue() + (Double) b.getValue());
				return a;
			}
		});
		addFunction(new RunnerFunction("-=") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				a.setValue((Double) a.getValue() - (Double) b.getValue());
				return a;
			}
		});
		addFunction(new RunnerFunction("*=") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				a.setValue((Double) a.getValue() * (Double) b.getValue());
				return a;
			}
		});
		addFunction(new RunnerFunction("/=") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				a.setValue((Double) a.getValue() / (Double) b.getValue());
				return a;
			}
		});
		addFunction(new RunnerFunction("++") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				a.setValue(((Double) a.getValue()) + 1.0);
				return a;
			}
		});
		addFunction(new RunnerFunction("--") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				a.setValue(((Double) a.getValue()) - 1.0);
				return a;
			}
		});
		addFunction(new RunnerFunction("+") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				if (a.getValue() instanceof String
						|| b.getValue() instanceof String) {
					return new Variable(String.valueOf(a.getValue())
							+ String.valueOf(b.getValue()));
				}
				return new Variable((Double) a.getValue()
						+ (Double) b.getValue());
			}
		});
		addFunction(new RunnerFunction("-") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable((Double) a.getValue()
						- (Double) b.getValue());
			}
		});
		addFunction(new RunnerFunction("*") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable((Double) a.getValue()
						* (Double) b.getValue());
			}
		});
		addFunction(new RunnerFunction("/") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Variable b = runner.runNext();
				return new Variable((Double) a.getValue()
						/ (Double) b.getValue());
			}
		});
		addFunction(new RunnerFunction("$") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
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
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				return new Variable(runner);
			}
		});
		addFunction(new RunnerFunction("super") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				return new Variable(runner.getParent());
			}
		});
		addFunction(new RunnerFunction("func") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable holder = null;
				if (isFirst) {
					holder = runner.runNext();
				}
				Variable code = runner.next();
				if (code.getValue() instanceof CodeInstruction) {
					ScriptFunction ret = new ScriptFunction(runner,
							((CodeInstruction) code.getValue())
									.getInstructions());
					if (holder != null) {
						holder.setValue(ret);
					}
					return new Variable(new FunctionPointer(ret));
				}
				throw new ScriptException(
						"Should be: func name {...} or ... func {...}");
			}
		});
		addFunction(new RunnerFunction("do") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable code = runner.runNext();
				if (code.getValue() instanceof FunctionPointer) {
					FunctionPointer pointer = (FunctionPointer) code.getValue();
					return pointer.getValue().run(runner);
				}
				throw new ScriptException(
						"Should be: do function");
			}
		});
		addFunction(new RunnerFunction(".") {
			public Variable run(Runner runner, boolean isFirst)
					throws ReturnException, ScriptException, JizzException {
				Variable a = runner.runNext();
				Instruction ins = runner.getNextInstruction();
				String varName = null;
				runner.incPosition();
				if (a.getValue() == null || !(a.getValue() instanceof Instance)) {
					a.setValue(new Instance(null));
				}
				if (ins instanceof NameInstruction) {
					varName = ((NameInstruction) ins).getName();
				} else if (ins instanceof NumberInstruction) {
					varName = String.valueOf((int) ((NumberInstruction) ins)
							.getValue());
				} else if (ins instanceof FunctionInstruction) {
					int id = ((FunctionInstruction) ins).getFunctionId();
					varName = functions.get(id).getName();
				} else {
					return new Variable();
				}
				Instance instance = ((Instance) a.getValue());
				if (varName.equals("super")) {
					return new Variable(instance.getParent());
				}
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
	public Variable runFunction(int id, Runner runner, boolean isFirst)
			throws ReturnException, ScriptException, JizzException {
		return functions.get(id).run(runner, isFirst);
	}

}
