package com.sandbox.neural;

import java.util.Random;

public class InputNode extends AbstractNode
{
	private double	output;

	public InputNode()
	{
	}

	@Override
	public double getOutput()
	{
		return this.output;
	}

	public void setOutput(final double output)
	{
		this.output = output;
	}

	@Override
	public String toString()
	{
		return "InputNode value: " + this.output;
	}

	@Override
	public void randomizeWeights(Random rng, double range)
	{
	}

}
