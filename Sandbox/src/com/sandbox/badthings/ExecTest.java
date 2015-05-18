package com.sandbox.badthings;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ExecTest
{
	public static void main(String[] args) throws Exception
	{
		Runtime rt = Runtime.getRuntime();
		rt.exec("sudo open /Users/ladmin/Applications/Utilities/Terminal.app");

		Process proc = rt.exec("info");
		InputStream stderr = proc.getErrorStream();
		InputStreamReader isr = new InputStreamReader(stderr);
		BufferedReader br = new BufferedReader(isr);
		String line = null;
		System.out.println("<ERROR>");
		while ((line = br.readLine()) != null)
			System.out.println(line);
		System.out.println("</ERROR>");
		int exitVal = proc.waitFor();
		System.out.println("Process exitValue: " + exitVal);
	}
}
