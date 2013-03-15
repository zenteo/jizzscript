package org.xeroworld.jizzscript.parsing;

public class Metadata {
	private String filename;
	private boolean runtimeCode;
	
	public Metadata() {
		filename = null;
		runtimeCode = false;
	}
	
	public boolean isRuntimeCode() {
		return runtimeCode;
	}

	public void setRuntimeCode(boolean runtimeCode) {
		this.runtimeCode = runtimeCode;
	}

	public String getFilename() {
		return filename;
	}

	public void setFilename(String filename) {
		this.filename = filename;
	}
	
	public String toString() {
		String ret = "";
		if (filename != null) {
			ret += filename;
		}
		if (isRuntimeCode()) {
			if (!ret.equals("")) {
				ret += ";";
			}
			ret += "runtime";
		}
		return "[" + ret + "]";
	}
}
