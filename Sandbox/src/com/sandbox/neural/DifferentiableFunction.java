package com.sandbox.neural;

public interface DifferentiableFunction
{
	public abstract double compute(double x);

	public abstract double computeDerivative(double x);
}