package org.xeroworld.jizzscript;

import java.util.ArrayList;

import org.xeroworld.jizzscript.parsing.Codeblock;
import org.xeroworld.jizzscript.parsing.Parser;
import org.xeroworld.jizzscript.running.DefaultLibrary;
import org.xeroworld.jizzscript.running.FunctionLibrary;
import org.xeroworld.jizzscript.running.Runner;
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
		Variable var = r.run();
		if (var != null) {
			return var.getValue();
		}
		return null;
	}
	
	public static void main(String[] args) {
		String code =	"vector = func { $x $y func echo { $scale print x*scale print y*scale} } \n" +
						"v = {$x $y} 4 3 \n" +
						"func v.echo {print v.x}" +
						"print \"lol\" + 2 + \"fa\"";
		
		JizzScript js = new JizzScript();
		js.addCode(code);
		js.run();
	}
}
