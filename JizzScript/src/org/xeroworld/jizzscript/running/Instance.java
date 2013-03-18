package org.xeroworld.jizzscript.running;

import java.util.HashMap;

public class Instance {
	private Instance parent = null;
	private HashMap<String, Variable> variables = new HashMap<String, Variable>();
	
	public Instance(Instance parent) {
		this.parent = parent;
	}
	
	public Instance getParent() {
		return parent;
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
