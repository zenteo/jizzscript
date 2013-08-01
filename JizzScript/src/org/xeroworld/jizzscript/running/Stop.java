package org.xeroworld.jizzscript.running;

public class Stop {
	
	@Override
	public boolean equals(Object other) {
		return other instanceof Stop;
	}
	
	@Override
	public String toString() {
		return ";";
	}
}
