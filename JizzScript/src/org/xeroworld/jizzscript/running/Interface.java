package org.xeroworld.jizzscript.running;

import java.util.ArrayList;

public class Interface {
	private ArrayList<String> names;
	
	public Interface() {
		names = new ArrayList<String>();
	}
	
	public ArrayList<String> getNames() {
		return names;
	}
	
	public String toString() {
		String ret = "interface {";
		boolean first = true;
		for (String name : names) {
			if (!first)
				ret += " ";
			first = false;
			ret += name;
		}
		ret += "}";
		return ret;
	}
}
