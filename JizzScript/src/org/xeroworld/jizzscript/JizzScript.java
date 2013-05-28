package org.xeroworld.jizzscript;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

import org.xeroworld.jizzscript.parsing.Codeblock;
import org.xeroworld.jizzscript.running.DefaultLibrary;
import org.xeroworld.jizzscript.running.Function;
import org.xeroworld.jizzscript.running.FunctionLibrary;
import org.xeroworld.jizzscript.running.FunctionPointer;
import org.xeroworld.jizzscript.running.Instance;
import org.xeroworld.jizzscript.running.JizzException;
import org.xeroworld.jizzscript.running.ListInstance;
import org.xeroworld.jizzscript.running.ReturnException;
import org.xeroworld.jizzscript.running.Runner;
import org.xeroworld.jizzscript.running.ScriptException;
import org.xeroworld.jizzscript.running.Variable;

public class JizzScript {
	private FunctionLibrary functionLibrary;
	private Compiler compiler;
	
	public JizzScript() {
		functionLibrary = new DefaultLibrary();
		compiler = new Compiler(functionLibrary);
	}
	
	public void addCode(String code) {
		compiler.addCode(code);
	}
	
	public void addCode(Codeblock code) {
		compiler.addCode(code);
	}
	
	public void clear() {
		compiler.clear();
	}
	
	public void run() {
		Runner r = new Runner(functionLibrary, compiler.getInstructions());
		functionLibrary.addDefaults(r);
		try {
			r.run();
		}
		catch (ScriptException ex) {
			System.out.println(ex.getMessage());
		} catch (JizzException ex) {
			System.out.println("Exception thrown from within script: " + ex.getValue());
		}
		Variable jizzVar = r.getField("jizz");
		if (jizzVar != null && jizzVar.getValue() instanceof Instance) {
			Instance jizz = (Instance)jizzVar.getValue();
			Variable mainsVar = jizz.getField("mains");
			if (mainsVar != null && mainsVar.getValue() instanceof ListInstance) {
				ListInstance listIns = (ListInstance)mainsVar.getValue();
				for (Variable v : listIns.getData()) {
					if (v != null && v.getValue() instanceof FunctionPointer) {
						Function fp = ((FunctionPointer)v.getValue()).getValue();
						try {
							r.runVariable(new Variable(fp));
						}
						catch (ScriptException ex) {
							System.out.println(ex.getMessage());
						} catch (JizzException ex) {
							System.out.println("Exception thrown from within script: " + ex.getValue());
						} catch (ReturnException e) {
							System.out.println("A main returned " + e.getValue());
						}
					}
				}
			}
		}
	}
	
	public static void main(String[] args) {
		JizzScript js = new JizzScript();
	
		if (args.length < 1) {
			System.out.println("Usage: jizzscript filenames ...");
			return;
		}
		
		StringBuilder builder = new StringBuilder();
		for (int i = 0; i < args.length; i++) {
			try (FileReader fr = new FileReader(args[i])) {
				try (BufferedReader br = new BufferedReader(fr)) {
					String line;
					while ((line = br.readLine()) != null) {
						builder.append(line);
						builder.append('\n');
					}
				}
			} catch (IOException e2) {
				e2.printStackTrace();
			}
		}
		js.addCode(builder.toString());
		js.run();
		
	}
}
