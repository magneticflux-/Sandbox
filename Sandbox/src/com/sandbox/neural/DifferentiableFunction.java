package com.sandbox.neural;

public interface DifferentiableFunction {
	double compute(double x);

	double computeDerivative(double x);
}