package com.sandbox.neural;

import org.apache.commons.math3.util.FastMath;

public class RectifierActivation implements DifferentiableFunction
{
	@Override
	public double compute(double x)
	{
		return FastMath.max(0, x);
	}

	@Override
	public double computeDerivative(double x)
	{
		return x > 0 ? 1 : 0;
	}
}
