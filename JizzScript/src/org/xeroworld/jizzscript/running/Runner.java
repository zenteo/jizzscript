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
	
	public Variable run() throws ScriptException {
		return run(false);
	}
	
	public Variable run(boolean makeList) throws ScriptException {
		int at = 0;
		try {
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
		}
		catch (ReturnException ex) {
			return new Variable(ex.getValue());
		}
		return new Variable(this);
	}

	public Variable runNext() throws ReturnException, ScriptException {
		return runNext(false);
	}
	
	public Variable runNext(boolean first) throws ReturnException, ScriptException {
		return run(next(first));
	}
	
	public Variable run(Variable var) throws ReturnException, ScriptException {
		if (var.getValue() instanceof Function) {
			return ((Function)var.getValue()).run(this);
		}
		else if (var.getValue() instanceof CodeInstruction) {
			Runner r = new Runner(funclib, this, ((CodeInstruction)var.getValue()).getInstructions());
			r.setMaster(getMaster());
			r.run();
			var = new Variable(r);
		}
		return var;
	}
	
	public Variable next() throws ReturnException, ScriptException {
		return next(false);
	}
	
	public Variable next(boolean first) throws ReturnException, ScriptException {
		Instruction ins = getNextInstruction();
		position++;
		if (ins == null) {
			String message;
			if (first) {
				message = "Something is wrong with the compilation!";
			}
			else {
				message = "Argument for function not found.";
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
			r.run(true);
			return new Variable(r);
		}
		if (ins instanceof NameInstruction) {
			return getVariable(((NameInstruction)ins).getName());
		}
		if (ins.getCodeblock() != null) {
			throw new ScriptException("Something went wrong around " + ins.toString());
		}
		throw new ScriptException("Please, kill me!");
	}
}
