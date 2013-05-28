package org.xeroworld.jizzscript.running;

public class Variable {
	private Object value;

	public Variable() {
		
	}
	
	public Variable(Object value) {
		this.value = value;
	}
	
	public Object getValue() {
		return value;
	}

	public void setValue(Object value) {
		this.value = value;
	}
	
	public String toString() {
		if (value instanceof String) {
			return "\"" + value.toString() + "\"";
		}
		else if (value instanceof ListInstance) {
			return "[" + value.toString() + "]";
		}
		else if (value instanceof Instance) {
			return "{" + value.toString() + "}";
		}
		return value.toString();
	}
}
