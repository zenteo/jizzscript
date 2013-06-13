package org.xeroworld.jizzscript.running;

public class Variable implements Comparable<Variable> {
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
		if (value == null) {
			return "null";
		}
		return value.toString();
	}

	@Override
	public int compareTo(Variable var) {
		if (value instanceof String && var.getValue() instanceof String) {
			return ((String)value).compareTo((String)var.getValue());
		}
		if (value instanceof Double && var.getValue() instanceof Double) {
			return ((Double)value).compareTo((Double)var.getValue());
		}
		return 0;
	}
	
	@Override
	public Object clone() {
		return new Variable(value);
	}
}
