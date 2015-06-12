package com.sandbox.neural;

import org.apache.commons.math3.util.FastMath;

public class SigmoidActivation implements DifferentiableFunction
{
	public static final SigmoidActivation	d	= new SigmoidActivation();

	@Override
	public double compute(double x)
	{
		return 1 / (1 + FastMath.exp(-x));
	}

	@Override
	public double computeDerivative(double x)
	{
		return 0;
	}
}
