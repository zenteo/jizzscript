package org.xeroworld.jizzscript.running;

import java.util.ArrayList;
import java.util.HashMap;

import org.xeroworld.jizzscript.instructions.CodeInstruction;
import org.xeroworld.jizzscript.instructions.FunctionInstruction;
import org.xeroworld.jizzscript.instructions.Instruction;
import org.xeroworld.jizzscript.instructions.ListInstruction;
import org.xeroworld.jizzscript.instructions.NameInstruction;
import org.xeroworld.jizzscript.instructions.NumberInstruction;
import org.xeroworld.jizzscript.instructions.StringInstruction;

public class Runner extends Instance {
	private FunctionLibrary funclib;
	private ArrayList<Instruction> instructions;
	private Runner master = null;
	private boolean testFlag = false;
	private int position = 0;
	
	public Runner(FunctionLibrary funclib, Instance parent, ArrayList<Instruction> instructions) {
		super(parent);
		this.funclib = funclib;
		this.instructions = instructions;
	}
	
	public Runner(FunctionLibrary funclib, ArrayList<Instruction> instructions) {
		super(null);
		this.funclib = funclib;
		this.instructions = instructions;
	}
	
	public FunctionLibrary getFunctionLibrary() {
		return funclib;
	}
	
	public ArrayList<Instruction> getInstructions() {
		return instructions;
	}
	
	public boolean hasNext() {
		return position < instructions.size();
	}
	
	public int getPosition() {
		return position;
	}
	
	public void setPosition(int position) {
		assert(position >= 0);
		this.position = position;
	}
	
	public int incPosition() {
		position += 1;
		return position;
	}
	
	public int decPosition() {
		position -= 1;
		return position;
	}
	
	public Runner getMaster() {
		return master;
	}

	public void setMaster(Runner master) {
		this.master = master;
	}
	
	public void resetPosition() {
		this.position = 0;
	}
	
	public Instruction getNextInstruction() {
		if (position < instructions.size()) {
			return instructions.get(position);
		}
		return null;
	}
	
	public Variable runAndThrow() throws ScriptException, ReturnException, JizzException {
		return runAndThrow(false);
	}
	
	public Variable runAndThrow(boolean makeList) throws ScriptException, ReturnException, JizzException {
		int at = 0;
		while (hasNext()) {
			Variable ret = runNext(true);
			if (ret == null) {
				throw new ScriptException("Please, kill me!");
			}
			if (makeList) {
				getVariable(String.valueOf(at)).setValue(ret.getValue());
				at++;
			}
		}
		return new Variable(this);
	}
	
	public Variable run() throws ScriptException, JizzException {
		try {
			while (hasNext()) {
				Variable ret = runNext(true);
				if (ret == null) {
					throw new ScriptException("Please, kill me!");
				}
			}
		}
		catch (ReturnException ex) {
			return ex.getVariable();
		}
		return new Variable(this);
	}
	
	public ListInstance makeList() throws ScriptException, ReturnException, JizzException {
		ListInstance ret = new ListInstance(this);
		while (hasNext()) {
			Variable var = runNext(true);
			if (var == null) {
				throw new ScriptException("Please, kill me!");
			}
			ret.getData().add(var);
		}
		return ret;
	}

	public Variable runNext() throws ReturnException, ScriptException, JizzException {
		return runNext(false);
	}
	
	public Variable runNext(boolean first) throws ReturnException, ScriptException, JizzException {
		return runVariable(next(first));
	}
	
	public Variable runVariable(Variable var) throws ReturnException, ScriptException, JizzException {
		if (var.getValue() instanceof Function) {
			return ((Function)var.getValue()).run(this);
		}
		else if (var.getValue() instanceof CodeInstruction) {
			Runner r = new Runner(funclib, this, ((CodeInstruction)var.getValue()).getInstructions());
			r.setMaster(getMaster());
			r.runAndThrow();
			var = new Variable(r);
		}
		return var;
	}
	
	public Variable next() throws ReturnException, ScriptException, JizzException {
		return next(false);
	}
	
	public Variable next(boolean first) throws ReturnException, ScriptException, JizzException {
		Instruction ins = getNextInstruction();
		position++;
		if (ins == null) {
			String message;
			if (first) {
				message = "Something is wrong with the compilation!";
			}
			else {
				message = "Argument for function not found.";
				ins = instructions.get(position-2);
				message += ins.getCodeblock().toString();
			}
			throw new ScriptException(message);
		}
		if (ins.getCodeblock() != null) {
			//System.out.println(ins.getCodeblock().toString());
		}
		if (ins instanceof StringInstruction) {
			return new Variable(((StringInstruction)ins).getValue());
		}
		if (ins instanceof NumberInstruction) {
			return new Variable(((NumberInstruction)ins).getValue());
		}
		if (ins instanceof FunctionInstruction) {
			return funclib.runFunction(((FunctionInstruction)ins).getFunctionId(), this, first);
		}
		if (ins instanceof CodeInstruction) {
			return new Variable(ins);
		}
		if (ins instanceof ListInstruction) {
			Runner r = new Runner(funclib, this, ((ListInstruction)ins).getInstructions());
			ListInstance list = r.makeList();
			return new Variable(list);
		}
		if (ins instanceof NameInstruction) {
			return getVariable(((NameInstruction)ins).getName());
		}
		if (ins.getCodeblock() != null) {
			throw new ScriptException("Something went wrong around " + ins.toString());
		}
		throw new ScriptException("Please, kill me!");
	}

	public boolean isTestFlag() {
		return testFlag;
	}

	public void setTestFlag(boolean testFlag) {
		this.testFlag = testFlag;
	}
}
