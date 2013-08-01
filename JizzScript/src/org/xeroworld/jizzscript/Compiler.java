package org.xeroworld.jizzscript;

import java.util.ArrayList;
import java.util.HashMap;

import org.xeroworld.jizzscript.instructions.CodeInstruction;
import org.xeroworld.jizzscript.instructions.FunctionInstruction;
import org.xeroworld.jizzscript.instructions.Instruction;
import org.xeroworld.jizzscript.instructions.ListInstruction;
import org.xeroworld.jizzscript.instructions.NameInstruction;
import org.xeroworld.jizzscript.instructions.NumberInstruction;
import org.xeroworld.jizzscript.instructions.StringInstruction;
import org.xeroworld.jizzscript.parsing.Codeblock;
import org.xeroworld.jizzscript.parsing.Parser;
import org.xeroworld.jizzscript.running.FunctionLibrary;

public class Compiler {
	private Parser parser = new Parser();
	private FunctionLibrary funclib;
	private ArrayList<Instruction> instructions;
	private HashMap<String, ArrayList<Instruction>> definitions;
	
	public Compiler(FunctionLibrary funclib) {
		this.instructions = new ArrayList<Instruction>();
		this.definitions = new HashMap<String, ArrayList<Instruction>>();
		this.funclib = funclib;
	}
	
	public void clear() {
		instructions.clear();
	}
	
	public ArrayList<Instruction> getInstructions() {
		return instructions;
	}
	
	public void addCode(String code) {
		instructions.addAll(compile(code));
	}
	
	public void addCode(Codeblock code) {
		instructions.addAll(compile(code));
	}
	
	public ArrayList<Instruction> compile(String code) {
		return compile(new Codeblock(code));
	}
	
	public ArrayList<Instruction> compile(Codeblock code) {
		ArrayList<Codeblock> codeblocks = parser.parse(code);
		//System.out.println(codeblocks);
		return compile(codeblocks);
	}
	
	private ArrayList<Instruction> compile(ArrayList<Codeblock> parsed) {
		ArrayList<Instruction> ret = new ArrayList<Instruction>();
		for (int i = 0; i < parsed.size(); i++) {
			Codeblock c = parsed.get(i);
			if (c.getCode().startsWith("{") && c.getCode().endsWith("}")) {
				c = (Codeblock)c.clone();
				c.setCode(c.getCode().substring(1, c.getCode().length() - 1));
				c.setColumn(c.getColumn() + 1);
				ret.add(new CodeInstruction(c, compile(c.getCode())));
				continue;
			}
			else if (c.getCode().startsWith("[") && c.getCode().endsWith("]")) {
				c = (Codeblock)c.clone();
				c.setCode(c.getCode().substring(1, c.getCode().length() - 1));
				c.setColumn(c.getColumn() + 1);
				ret.add(new ListInstruction(c, compile(c.getCode())));
				continue;
			}
			else if (c.getCode().startsWith("\"") && c.getCode().endsWith("\"")) {
				ret.add(new StringInstruction(c, c.getCode().substring(1, c.getCode().length()-1)));
				continue;
			}
			if (funclib.hasFunction(c.getCode())) {
				ret.add(new FunctionInstruction(c, funclib.getFunction(c.getCode())));
				continue;
			}
			if (c.getCode().equals("define") && i+2 <= parsed.size()) {
				Codeblock name = parsed.get(i+1);
				Codeblock definition = parsed.get(i+2);
				if (name.getCode().startsWith("\"") && name.getCode().endsWith("\"")) {
					if (definition.getCode().startsWith("\"") && definition.getCode().endsWith("\"")) {
						
						Codeblock b = (Codeblock)definition.clone();
						b.setColumn(b.getColumn()+1);
						b.setCode(definition.getCode().substring(1, definition.getCode().length() - 1));
						String nm = name.getCode().substring(1, name.getCode().length() - 1);
						definitions.put(nm, compile(parser.parse(b)));
						i += 2;
						continue;
					}	
				}
			}
			if (definitions.containsKey(c.getCode())) {
				ret.addAll(definitions.get(c.getCode()));
				continue;
			}
			try {
				double value = Double.parseDouble(c.getCode());
				ret.add(new NumberInstruction(c, value));
				continue;
			}
			catch (NumberFormatException ex) {
			}
			ret.add(new NameInstruction(c, c.getCode()));
		}
		return ret;
	}
}