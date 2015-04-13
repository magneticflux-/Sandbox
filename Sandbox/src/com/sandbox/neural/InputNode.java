package com.sandbox.neural;

public class InputNode extends AbstractNode
{
	private double	output;

	public InputNode()
	{
	}

	@Override
	public double getOutput()
	{
		return output;
	}

	public void setOutput(double output)
	{
		this.output = output;
	}
}
