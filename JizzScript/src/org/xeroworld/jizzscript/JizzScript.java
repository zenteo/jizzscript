package org.xeroworld.jizzscript;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.xeroworld.jizzscript.parsing.Codeblock;
import org.xeroworld.jizzscript.running.DefaultLibrary;
import org.xeroworld.jizzscript.running.FunctionLibrary;
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
	
	public Object run() {
		Runner r = new Runner(functionLibrary, compiler.getInstructions());
		functionLibrary.addDefaults(r);
		try {
			Variable var = r.run();
			if (var != null) {
				return var.getValue();
			}
		}
		catch (ScriptException ex) {
			System.out.println(ex.getMessage());
		}
		return null;
	}
	
	public static void main(String[] args) {
		JizzScript js = new JizzScript();
	
		if (args.length < 1) {
			System.out.println("Usage: jizzscript filename");
			return;
		}
		
		StringBuilder builder = new StringBuilder();
		File file = new File(args[0]);
		try (InputStream is = new FileInputStream(file)) {
			try (InputStreamReader isr = new InputStreamReader(is)) {
				try (BufferedReader br = new BufferedReader(isr)) {
					String line;
					while ((line = br.readLine()) != null) {
						builder.append(line);
						builder.append('\n');
					}
				}
			}
		} catch (IOException e2) {
			e2.printStackTrace();
		}
		
		js.addCode(builder.toString());
		
		js.run();
	}
}
