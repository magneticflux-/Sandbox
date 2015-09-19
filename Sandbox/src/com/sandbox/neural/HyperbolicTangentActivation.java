package com.sandbox.neural;

import org.apache.commons.math3.util.FastMath;

public class HyperbolicTangentActivation implements DifferentiableFunction {

	@Override
	public double compute(double x) {
		return FastMath.tanh(x);
	}

	@Override
	public double computeDerivative(double x) {
		return 1 / FastMath.pow(FastMath.cosh(x), 2);
	}
}