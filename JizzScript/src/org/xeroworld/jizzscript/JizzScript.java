package org.xeroworld.jizzscript;

import java.util.ArrayList;

import org.xeroworld.jizzscript.parsing.Codeblock;
import org.xeroworld.jizzscript.parsing.Parser;

public class JizzScript {
	public static void main(String[] args) {
		Parser parser = new Parser();
		String code = "func  lol/*adawdawd*/{return pack{a b c}}wef//hhawdawd\n Lordylord";
		ArrayList<Codeblock> splittet = parser.split(new Codeblock(code, 3, 24));
		System.out.println(splittet.toString());
	}
}
