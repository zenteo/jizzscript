package org.xeroworld.jizzscript.running;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

class ListComparator implements Comparator<Variable> {
	private Runner master;
	
	public ListComparator(Runner master) {
		this.master = master;
	}
	
	@Override
	public int compare(final Variable o1, final Variable o2) {
		if (o1.getValue() instanceof Instance) {
			Instance ins = (Instance)o1.getValue();
			Variable func = ins.getField("compareTo");
			if (func.getValue() instanceof Function) {
				Runner runner = new Runner(master.getFunctionLibrary(), master, null) {
					@Override
					public Variable runNext(boolean first) {
						return o2;
					}
				};
				try {
					Variable ret = runner.runVariable(func);
					if (ret.getValue() instanceof Double) {
						double val = (Double)ret.getValue();
						return (int)val;
					}
				} catch (ReturnException e) {
					e.printStackTrace();
				} catch (ScriptException e) {
					e.printStackTrace();
				} catch (JizzException e) {
					e.printStackTrace();
				}
			}
		}
		if (o2.getValue() instanceof Instance) {
			Instance ins = (Instance)o2.getValue();
			Variable func = ins.getField("compareTo");
			if (func.getValue() instanceof Function) {
				Runner runner = new Runner(master.getFunctionLibrary(), master, null) {
					@Override
					public Variable runNext(boolean first) {
						return o1;
					}
				};
				try {
					Variable ret = runner.runVariable(func);
					if (ret.getValue() instanceof Double) {
						double val = (Double)ret.getValue();
						return -(int)val;
					}
				} catch (ReturnException e) {
					e.printStackTrace();
				} catch (ScriptException e) {
					e.printStackTrace();
				} catch (JizzException e) {
					e.printStackTrace();
				}
			}
		}
		return o1.compareTo(o2);
	}
	
}

public class ListInstance extends Instance {
	private ArrayList<Variable> data;
	
	public ListInstance(final Instance parent) {
		super(parent);
		data = new ArrayList<Variable>();
		Function cloneFunc = new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				ListInstance ret = new ListInstance(parent);
				for (Variable v : data) {
					ret.data.add(new Variable(v.getValue()));
				}
				return new Variable(ret);
			}
		};
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
		Function sortFunc = new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				ListComparator comparator = new ListComparator(master);
				Collections.sort(data, comparator);
				return new Variable();
			}
		};
		Function addFunc = new Function(null) {
			@Override
			public Variable run(Runner master) throws ScriptException,
					JizzException {
				try {
					Variable v = master.runNext();
					data.add(new Variable(v.getValue()));	
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
						data.add((int)dat, new Variable(v.getValue()));	
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
<<<<<<< HEAD
					for (int i = data.size() - 1; i >= 0; i--) {
=======
					for (int i = data.size()-1; i >= 0; i--) {
>>>>>>> branch 'master' of https://github.com/zenteo/jizzscript.git
						if (data.get(i).getValue().equals(v.getValue())) {
							data.remove(i);
						}
					}
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
		getField("clone").setValue(cloneFunc);
		getField("sort").setValue(sortFunc);
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
