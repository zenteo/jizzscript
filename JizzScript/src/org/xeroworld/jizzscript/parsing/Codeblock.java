package org.xeroworld.jizzscript.parsing;


public class Codeblock implements Cloneable {
	private Metadata metadata;
	private String code;
	private boolean generated;
	private int line, column;
	
	public Codeblock(Metadata metadata, String code, int line, int column) {
		this.metadata = metadata;
		this.code = code;
		this.line = line;
		this.column = column;
		this.generated = false;
	}
	
	public Codeblock(String code, int line, int column) {
		this(new Metadata(), code, line, column);
		this.metadata.setRuntimeCode(true);
	}
	
	public Codeblock(String code) {
		this(new Metadata(), code, 1, 1);
		this.metadata.setRuntimeCode(true);
	}
	
	@Override
	public Object clone() {
		try {
			return super.clone();
		} catch (CloneNotSupportedException e) {
			e.printStackTrace();
		}
		return null;
	}
	

	public Metadata getMetadata() {
		return metadata;
	}

	public void setMetadata(Metadata metadata) {
		this.metadata = metadata;
	}

	public String getCode() {
		return code;
	}

	public void setCode(String code) {
		this.code = code;
	}
	
	public boolean isGenerated() {
		return generated;
	}
	
	public void setGenerated(boolean generated) {
		this.generated = generated;
	}

	public int getLine() {
		return line;
	}

	public void setLine(int line) {
		this.line = line;
	}

	public int getColumn() {
		return column;
	}

	public void setColumn(int column) {
		this.column = column;
	}
	
	public String toString() {
		return "[" + metadata.toString() + ";" + line + ";" + column + ":" + code + "]";
	}
}
