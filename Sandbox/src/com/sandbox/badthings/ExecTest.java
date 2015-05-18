package com.sandbox.badthings;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Scanner;

public class ExecTest
{
	public static void main(String[] args) throws Exception
	{
		Runtime rt = Runtime.getRuntime();
		rt.exec("open /Users/ladmin/Applications/Utilities/Terminal.app");
		System.out.print("Command: ");
		while (true)
		{
			Process proc = rt.exec(new Scanner(System.in).nextLine());
			InputStream stderr = proc.getErrorStream();
			InputStream stdin = proc.getInputStream();

			InputStreamReader isrStderr = new InputStreamReader(stderr);
			InputStreamReader isrStdin = new InputStreamReader(stdin);

			BufferedReader brErr = new BufferedReader(isrStderr);
			BufferedReader brIn = new BufferedReader(isrStdin);

			String line = null;

			System.out.println("<IN>");
			while ((line = brIn.readLine()) != null)
				System.out.println(line);
			System.out.println("</IN>");

			System.out.println("<ERROR>");
			while ((line = brErr.readLine()) != null)
				System.out.println(line);
			System.out.println("</ERROR>");

			int exitVal = proc.waitFor();
			System.out.println("Process exitValue: " + exitVal);
		}
	}
}
