package com.sandbox.neural;

import java.util.ArrayList;
import java.util.Random;

public class InputNode extends AbstractNode
{
	private double	output;

	public InputNode(final double output, int layerNumber)
	{
		super(new ArrayList<AbstractNode>(), layerNumber, null);
		this.output = output;
	}

	@Override
	public double getOutput()
	{
		return this.output;
	}

	@Override
	public void randomizeWeights(final Random rng, final double range)
	{
	}

	public void setOutput(final double output)
	{
		this.output = output;
	}

	@Override
	public String toString()
	{
		return "InputNode @ " + this.layerNumber;
	}

	@Override
	public String detailedToString()
	{
		return this.toString() + ", value: " + this.output;
	}
}
