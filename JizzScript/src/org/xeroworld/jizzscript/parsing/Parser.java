package org.xeroworld.jizzscript.parsing;

import java.util.ArrayList;

class Sequence implements Cloneable {
	private String from;
	private String to;
	private Character escape;
	private int maxCount;
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
		this.escape = escape;
		this.maxCount = maxCount;
		this.visible = visible;
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
	
	public boolean incInsideCount(Integer insideCount) {
		if (maxCount >= 0) {
			if (insideCount + 1 > maxCount) {
				return false;
			}
		}
		return true;
	}
	
	public boolean decInsideCount(Integer insideCount) {
		return true;
	}
	
	public boolean isInside(Integer insideCount) {
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

		containers.add(new Sequence("\"", "\"", 1, '\\', true));	// Text container "..." with \ as escape character.
		containers.add(new Sequence("{", "}", true));			// Code block
		containers.add(new Sequence("(", ")", true));			// Expression
		containers.add(new Sequence("[", "]", true));			// Regular list
		
		//[Invisible Splitters]
		splitters.add(new Splitter(" ", false));	// Space
		splitters.add(new Splitter("\t", false));	// Tab
		splitters.add(new Splitter("\n", false));	// New line
		
		//[Splitters]
		splitters.add(new Splitter(".", true));		// Decimal point / child of
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
		
		//Increase/Decrease
		splitters.add(new Splitter("++", true));	// increase
		splitters.add(new Splitter("--", true));	// decrease
		
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
	
	private ArrayList<Codeblock> split(Codeblock codeblock) {
		int[] insideCounts = new int[containers.size()];
		ArrayList<Codeblock> splitted = new ArrayList<Codeblock>();
		StringBuilder current = new StringBuilder();
		boolean stepToNext;
		String code = codeblock.getCode();
		int lineNumber = codeblock.getLine();
		int column = codeblock.getColumn();
		int lastLineShift = -codeblock.getColumn();
		boolean isInside;
		for (int i = 0; i < code.length(); i++) {
			if (code.charAt(i) == '\n') {
				lineNumber++;
				lastLineShift = i;
			}
			stepToNext = false;
			isInside = false;
			for (int k = 0; k < containers.size(); k++) {
				Sequence c = containers.get(k);
				if (c.isInside(insideCounts[k])) {
					isInside = true;
				}
			}
			for (int k = 0; k < containers.size(); k++) {
				Sequence c = containers.get(k);
				if (c.isInside(insideCounts[k])) {
					if (c.getEscape() != null && code.charAt(i) == c.getEscape()) {
						System.out.println("asdfadf");
						i += 1;
						current.append(code.charAt(i));
					}
					else if (code.startsWith(c.getTo(), i)) {
						if (c.decInsideCount(insideCounts[k]))
							insideCounts[k]--;
						if (current.length() > 0)
							current.append(c.getTo());
						i += c.getTo().length() - 1;
					}
					else if (code.startsWith(c.getFrom(), i)) {
						if (c.incInsideCount(insideCounts[k]))
							insideCounts[k]++;
						if (current.length() > 0)
							current.append(c.getFrom());
						i += c.getFrom().length() - 1;
					}
					else {
						current.append(code.charAt(i));
					}
					if (!c.isInside(insideCounts[k])) {
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
				else if (!isInside) {
					if (code.startsWith(c.getFrom(), i)) {
						if (current.length() > 0) {
							splitted.add(new Codeblock(codeblock.getMetadata(), current.toString(), lineNumber, column));
						}
						if (c.incInsideCount(insideCounts[k]))
							insideCounts[k]++;
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
					if (s.getValue().equals(".") && current.length() > 0) {
						try {
							Integer.parseInt(current.toString());
							break;
						}
						catch (NumberFormatException ex) {
						}
					}
					if (current.length() > 0) {
						splitted.add(new Codeblock(codeblock.getMetadata(), current.toString(), lineNumber, column));
					}
					column = i - lastLineShift;
					if (s.isVisible()) {
						splitted.add(new Codeblock(codeblock.getMetadata(), s.getValue(), lineNumber, column));
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
	
	private ArrayList<Codeblock> arrange(ArrayList<Codeblock> codeBlocks) {
		ArrayList<ArrayList<Codeblock>> ret = new ArrayList<ArrayList<Codeblock>>();
		if (codeBlocks.size() > 0 && codeBlocks.get(0).getCode().equals("-")) {
			ArrayList<Codeblock> newArr = new ArrayList<Codeblock>();
			Codeblock c = (Codeblock)codeBlocks.get(0).clone();
			c.setGenerated(true);
			c.setCode("0");
			newArr.add(c);
			ret.add(newArr);
		}
		for (int i = 0; i < codeBlocks.size(); i++) {
			Codeblock c = codeBlocks.get(i);
			if (c.getCode().startsWith("(") && c.getCode().endsWith(")")) {
				c = (Codeblock)c.clone();
				c.setColumn(c.getColumn() + 1);
				c.setCode(c.getCode().substring(1, c.getCode().length() - 1));
				ret.add(arrange(split(c)));
			}
			else {
				ArrayList<Codeblock> newArr = new ArrayList<Codeblock>();
				newArr.add(c);
				ret.add(newArr);
			}
		}
		for (int i = 1; i < ret.size() - 1; i++) {
			ArrayList<Codeblock> curr = ret.get(i);
			if (curr.size() == 1) {
				if (curr.get(0).getCode().equals(".")) {
					ret.get(i - 1).add(0, curr.get(0));
					ret.get(i - 1).addAll(ret.get(i + 1));
					ret.remove(i + 1);
					ret.remove(i);
					i--;
				}
			}
		}
		for (int i = ret.size() - 2; i >= 0 ; i--) {
			ArrayList<Codeblock> curr = ret.get(i);
			if (curr.size() == 1) {
				if (curr.get(0).getCode().equals("@")) {
					ret.get(i + 1).add(0, curr.get(0));
					ret.remove(i);
				}
			}
		}
		for (int i = ret.size() - 1; i >= 1 ; i--) {
			ArrayList<Codeblock> curr = ret.get(i);
			if (curr.size() == 1) {
				if (curr.get(0).getCode().equals("++") || curr.get(0).getCode().equals("--")) {
					ret.get(i - 1).add(0, curr.get(0));
					ret.remove(i);
				}
			}
		}
		for (int i = ret.size() - 2; i >= 1 ; i--) {
			ArrayList<Codeblock> curr = ret.get(i);
			if (curr.size() == 1) {
				if (curr.get(0).getCode().equals("*") || curr.get(0).getCode().equals("/")) {
					ret.get(i - 1).add(0, curr.get(0));
					ret.get(i - 1).addAll(ret.get(i + 1));
					ret.remove(i + 1);
					ret.remove(i);
				}
			}
		}
		for (int i = ret.size() - 2; i >= 1 ; i--) {
			ArrayList<Codeblock> curr = ret.get(i);
			if (curr.size() == 1) {
				if (curr.get(0).getCode().equals("+") || curr.get(0).getCode().equals("-")) {
					ret.get(i - 1).add(0, curr.get(0));
					ret.get(i - 1).addAll(ret.get(i + 1));
					ret.remove(i + 1);
					ret.remove(i);
				}
			}
		}
//		for (int i = ret.size() - 2; i >= 1 ; i--) {
//		ArrayList<Codeblock> curr = ret.get(i);
//		if (curr.size() == 1) {
//			if (curr.get(0).getCode().equals("|") || curr.get(0).getCode().equals("&") || curr.get(0).getCode().equals("^")) {
//				ret.get(i - 1).add(0, curr.get(0));
//				ret.get(i - 1).addAll(ret.get(i + 1));
//				ret.remove(i + 1);
//				ret.remove(i);
//			}
//		}
//	}
//	for (int i = ret.size() - 2; i >= 1 ; i--) {
//		ArrayList<Codeblock> curr = ret.get(i);
//		if (curr.size() == 1) {
//			if (curr.get(0).getCode().equals("%")) {
//				ret.get(i - 1).add(0, curr.get(0));
//				ret.get(i - 1).addAll(ret.get(i + 1));
//				ret.remove(i + 1);
//				ret.remove(i);
//			}
//		}
//	}
		for (int i = ret.size() - 2; i >= 1 ; i--) {
			ArrayList<Codeblock> curr = ret.get(i);
			if (curr.size() == 1) {
				if (curr.get(0).getCode().equals("=") || curr.get(0).getCode().equals("+=") || curr.get(0).getCode().equals("-=")
						|| curr.get(0).getCode().equals("*=") || curr.get(0).getCode().equals("/=") || curr.get(0).getCode().equals("&=")
						|| curr.get(0).getCode().equals("|=") || curr.get(0).getCode().equals("^=") || curr.get(0).getCode().equals("%=")) {
					ret.get(i - 1).add(0, curr.get(0));
					ret.get(i - 1).addAll(ret.get(i + 1));
					ret.remove(i + 1);
					ret.remove(i);
				}
			}
		}
		for (int i = ret.size() - 2; i >= 1 ; i--) {
			ArrayList<Codeblock> curr = ret.get(i);
			if (curr.size() == 1) {
				if (curr.get(0).getCode().equals("==") || curr.get(0).getCode().equals("!=") || curr.get(0).getCode().equals(">=") || curr.get(0).getCode().equals("<=") || curr.get(0).getCode().equals(">") || curr.get(0).getCode().equals("<")) {
					ret.get(i - 1).add(0, curr.get(0));
					ret.get(i - 1).addAll(ret.get(i + 1));
					ret.remove(i + 1);
					ret.remove(i);
				}
			}
		}
		for (int i = ret.size() - 2; i >= 1 ; i--) {
			ArrayList<Codeblock> curr = ret.get(i);
			if (curr.size() == 1) {
				if (curr.get(0).getCode().equals("&&") || curr.get(0).getCode().equals("||")) {
					ret.get(i - 1).add(0, curr.get(0));
					ret.get(i - 1).addAll(ret.get(i + 1));
					ret.remove(i + 1);
					ret.remove(i);
				}
			}
		}
		ArrayList<Codeblock> result = new ArrayList<Codeblock>();
		for (ArrayList<Codeblock> arr : ret) {
			result.addAll(arr);
		}
		return result;
	}
	
	public ArrayList<Codeblock> parse(Codeblock code) {
		return arrange(split(code));
	}
	
	public ArrayList<Codeblock> parse(String code) {
		return arrange(split(new Codeblock(code)));
	}
}
