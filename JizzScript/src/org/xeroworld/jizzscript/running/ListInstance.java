package org.xeroworld.jizzscript.running;

import java.util.ArrayList;

public class ListInstance extends Instance {
	private ArrayList<Variable> data;
	
	public ListInstance(Instance parent) {
		super(parent);
		data = new ArrayList<Variable>();
		Function getFunc = new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable at = master.runNext();
					if (at != null && at.getValue() instanceof Double) {
						double dat = (Double)at.getValue();
						return data.get((int)dat);
					}
				}
				catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		};
		Function addFunc = new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable v = master.runNext();
					data.add(v);	
				}
				catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		};
		Function addAtFunc = new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable at = master.runNext();
					Variable v = master.runNext();
					if (at.getValue() instanceof Double) {
						double dat = (Double)at.getValue();
						data.add((int)dat, v);	
					}
				}
				catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		};
		Function firstFunc = new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				return data.get(0);
			}
		};
		Function removeAtFunc = new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable at = master.runNext();
					if (at.getValue() instanceof Double) {
						double dat = (Double)at.getValue();
						data.remove((int)dat);
					}
				}
				catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		};
		Function removeFunc = new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable v = master.runNext();
					data.remove(v);
				}
				catch (ReturnException e) {
					e.printStackTrace();
				}
				return new Variable();
			}
		};
		Function peekFunc = new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				return data.get(data.size()-1);
			}
		};
		Function sizeFunc = new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				return new Variable((double)data.size());
			}
		};
		Function isEmptyFunc = new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				return new Variable(data.isEmpty());
			}
		};
		Function popFunc = new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				Variable v = data.get(data.size()-1);
				data.remove(data.size() - 1);
				return v;
			}
		};
		getField("get").setValue(getFunc);
		getField("add").setValue(addFunc);
		getField("addAt").setValue(addAtFunc);
		getField("remove").setValue(removeFunc);
		getField("removeAt").setValue(removeAtFunc);
		getField("push").setValue(addFunc);
		getField("pop").setValue(popFunc);
		getField("first").setValue(firstFunc);
		getField("peek").setValue(peekFunc);
		getField("last").setValue(peekFunc);
		getField("size").setValue(sizeFunc);
		getField("isEmpty").setValue(isEmptyFunc);
	}
	
	public ArrayList<Variable> getData() {
		return data;
	}

	public void setData(ArrayList<Variable> data) {
		this.data = data;
	}
	
	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < data.size(); i++) {
			if (i != 0) {
				builder.append("\t");
			}
			builder.append(data.get(i));
		}
		return builder.toString();
	}
}
