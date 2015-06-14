package com.sandbox.neural;

import org.apache.commons.math3.util.FastMath;

public class SoftplusActivation implements DifferentiableFunction
{
	@Override
	public double compute(double x)
	{
		return FastMath.log(1 + FastMath.exp(x));
	}

	@Override
	public double computeDerivative(double x)
	{
		return 1 / (1 + FastMath.exp(-x));
	}
}
