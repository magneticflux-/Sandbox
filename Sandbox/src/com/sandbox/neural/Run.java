package com.sandbox.neural;

public class Run
{
	public static final boolean	DEBUG	= false;

	public static void main(String[] args)
	{
		double var1 = 3;
		double var2 = 4;
		Population p = new Population(10, "2 1", new double[] { var1, var2 }, new double[] { var1 * var2 });
		p.viewStatus();
	}
}
