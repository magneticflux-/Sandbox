package com.sandbox.neural;

public class InputValue implements HasOutput
{
	private Double	output;

	public InputValue(Double output)
	{
		this.output = output;
	}

	@Override
	public Double getOutput()
	{
		return output;
	}

	@Override
	public String toString()
	{
		return "Input: " + output;
	}

	public void setValue(double d)
	{
		output = d;
	}
}
