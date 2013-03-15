package org.xeroworld.jizzscript.parsing;

import java.util.ArrayList;

class Sequence {
	private String from;
	private String to;
	private Character escape;
	private int insideCount, maxCount;
	private boolean visible;
	
	public Sequence(String from, String to, boolean visible) {
		this(from, to, -1, null, visible);
	}
	
	public Sequence(String from, String to, Character escape, boolean visible) {
		this(from, to, -1, escape, visible);
	}
	
	public Sequence(String from, String to, int maxCount, boolean visible) {
		this(from, to, maxCount, null, visible);
	}
	
	public Sequence(String from, String to, int maxCount, Character escape, boolean visible) {
		this.from = from;
		this.to = to;
		this.setEscape(escape);
		this.maxCount = maxCount;
		this.setVisible(visible);
		this.insideCount = 0;
	}
	
	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}
	
	public void incInsideCount() {
		insideCount++;
		if (maxCount >= 0) {
			if (insideCount > maxCount) {
				insideCount = maxCount;
			}
		}
	}
	
	public void decInsideCount() {
		insideCount--;
	}
	
	public boolean isInside() {
		return insideCount > 0;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}

	public Character getEscape() {
		return escape;
	}

	public void setEscape(Character escape) {
		this.escape = escape;
	}
}

class Splitter {
	private String value;
	private boolean visible;
	
	public Splitter(String value, boolean visible) {
		this.setValue(value);
		this.setVisible(visible);
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public boolean isVisible() {
		return visible;
	}

	public void setVisible(boolean visible) {
		this.visible = visible;
	}
}

public class Parser {
	private ArrayList<Sequence> containers = new ArrayList<Sequence>();
	private ArrayList<Splitter> splitters = new ArrayList<Splitter>();
	
	public Parser() {
		//[Containers]
		containers.add(new Sequence("//", "\n", 1, false)); 		// Regular line comment
		containers.add(new Sequence("/*", "*/", false)); 			// Block comment

		containers.add(new Sequence("\"", "\"", '\\', true));	// Text container "..." with \ as escape character.
		containers.add(new Sequence("{", "}", true));			// Code block
		containers.add(new Sequence("[", "]", true));			// Regular list
		
		//[Invisible Splitters]
		splitters.add(new Splitter(" ", false));	// Space
		splitters.add(new Splitter("\t", false));	// Tab
		splitters.add(new Splitter("\n", false));	// New line
		
		//[Splitters]
		splitters.add(new Splitter("$", true));		// parameter fetcher
		splitters.add(new Splitter("@", true));		// address of operator
		
		// Logic operators
		splitters.add(new Splitter("||", true));	// || operator
		splitters.add(new Splitter("&&", true));	// && operator
		
		// Logic conditions
		splitters.add(new Splitter("==", true));	// == operator
		splitters.add(new Splitter("!=", true));	// != operator
		splitters.add(new Splitter(">=", true));	// >= operator
		splitters.add(new Splitter("<=", true));	// <= operator
		splitters.add(new Splitter(">", true));		// > operator
		splitters.add(new Splitter("<", true));		// < operator
		splitters.add(new Splitter("!", true));		// ! operator
		
		// Set-to arithmetic operators
		splitters.add(new Splitter("+=", true));	// += operator
		splitters.add(new Splitter("-=", true));	// -= operator
		splitters.add(new Splitter("*=", true));	// *= operator
		splitters.add(new Splitter("/=", true));	// /= operator
		
		// Arithmetic operators
		splitters.add(new Splitter("+", true));		// Addition operator
		splitters.add(new Splitter("-", true));		// Subtraction operator
		splitters.add(new Splitter("*", true));		// Multiplication operator
		splitters.add(new Splitter("/", true));		// Division operator
		splitters.add(new Splitter("%", true));		// Modulus operator
		
		// Set-to bitwise operators
		splitters.add(new Splitter("^=", true));	// ^= operator
		splitters.add(new Splitter("%=", true));	// %= operator
		splitters.add(new Splitter("|=", true));	// |= operator
		splitters.add(new Splitter("&=", true));	// &= operator
		
		// Bitwise operators
		splitters.add(new Splitter("^", true));		// Bitwise xor operator
		splitters.add(new Splitter("|", true));		// Bitwise or operator
		splitters.add(new Splitter("&", true));		// Bitwise and operator
		splitters.add(new Splitter("~", true));		// Bitwise not operator
	}
	
	public ArrayList<Codeblock> split(Codeblock codeblock) {
		ArrayList<Codeblock> splitted = new ArrayList<Codeblock>();
		StringBuilder current = new StringBuilder();
		boolean stepToNext;
		String code = codeblock.getCode();
		int lineNumber = codeblock.getLine();
		int column = codeblock.getColumn();
		int lastLineShift = -codeblock.getColumn();
		for (int i = 0; i < code.length(); i++) {
			if (code.charAt(i) == '\n') {
				lineNumber++;
				lastLineShift = i;
			}
			stepToNext = false;
			for (Sequence c : containers) {
				if (c.isInside()) {
					if (code.startsWith(c.getTo(), i)) {
						c.decInsideCount();
						if (current.length() > 0)
							current.append(c.getTo());
						i += c.getTo().length() - 1;
					}
					else if (code.startsWith(c.getFrom(), i)) {
						c.incInsideCount();
						if (current.length() > 0)
							current.append(c.getFrom());
						i += c.getFrom().length() - 1;
					}
					else {
						current.append(code.charAt(i));
					}
					if (!c.isInside()) {
						if (c.isVisible()) {
							if (current.length() > 0) {
								splitted.add(new Codeblock(codeblock.getMetadata(), current.toString(), lineNumber, column));
							}
						}
						current = new StringBuilder();
						column = i - lastLineShift + 1;
					}
					stepToNext = true;
					break;
				}
				else {
					if (code.startsWith(c.getFrom(), i)) {
						if (current.length() > 0) {
							splitted.add(new Codeblock(codeblock.getMetadata(), current.toString(), lineNumber, column));
						}
						c.incInsideCount();
						current = new StringBuilder();
						column = i - lastLineShift;
						i += c.getFrom().length() - 1;
						current.append(c.getFrom());
						stepToNext = true;
						break;
					}
				}
			}
			if (stepToNext)
				continue;
			for (Splitter s : splitters) {
				if (code.startsWith(s.getValue(), i)) {
					if (current.length() > 0) {
						splitted.add(new Codeblock(codeblock.getMetadata(), current.toString(), lineNumber, column));
					}
					column = i - lastLineShift;
					if (s.isVisible()) {
						current.append(new Codeblock(codeblock.getMetadata(), s.getValue(), lineNumber, column));
					}
					i += s.getValue().length() - 1;
					current = new StringBuilder();
					column = i - lastLineShift + 1;
					stepToNext = true;
					break;
				}
			}
			if (stepToNext)
				continue;
			current.append(code.charAt(i));
		}
		if (current.length() > 0) {
			splitted.add(new Codeblock(codeblock.getMetadata(), current.toString(), lineNumber, column));
		}
		return splitted;
	}
}
