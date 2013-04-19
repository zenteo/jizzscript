package org.xeroworld.jizzscript.running;

import java.util.ArrayList;
import java.util.HashMap;

import org.xeroworld.jizzscript.instructions.Instruction;

public class Instance {
	private Instance parent = null;
	private HashMap<String, Variable> variables = new HashMap<String, Variable>();
	
	public Instance(Instance parent) {
		this.parent = parent;
	}
	
	public Runner derive(FunctionLibrary funclib, ArrayList<Instruction> instructions) {
		Runner ret = new Runner(funclib, getParent(), instructions);
		ret.setVariables(getVariables());
		return ret;
	}
	
	public Instance getParent() {
		return parent;
	}
	
	public void setParent(Instance parent) {
		this.parent = parent;
	}
	
	public HashMap<String, Variable> getVariables() {
		return variables;
	}
	
	public void setVariables(HashMap<String, Variable> variables) {
		this.variables = variables;
	}
	
	public Variable getField(String name) {
		if (variables.containsKey(name)) {
			return variables.get(name);
		}
		Variable ret = new Variable();
		variables.put(name, ret);
		return ret;
	}
	
	public Variable getVariable(String name) {
		if (variables.containsKey(name)) {
			return variables.get(name);
		}
		Variable ret = null;
		if (parent != null) {
			ret = parent.getVariableSafely(name);
		}
		if (ret == null) {
			ret = new Variable();
			variables.put(name, ret);
		}
		return ret;
	}
	
	public Variable getVariableSafely(String name) {
		if (variables.containsKey(name)) {
			return variables.get(name);
		}
		if (parent != null) {
			return parent.getVariableSafely(name);
		}
		return null;
	}
}
