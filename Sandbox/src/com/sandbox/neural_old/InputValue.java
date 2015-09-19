package com.sandbox.neural_old;

public class InputValue implements HasOutput {
	private Double output;

	public InputValue(final Double output) {
		this.output = output;
	}

	@Override
	public Double getOutput() {
		return this.output;
	}

	public void setValue(final double d) {
		this.output = d;
	}

	@Override
	public String toString() {
		return "Input: " + this.output;
	}
}
