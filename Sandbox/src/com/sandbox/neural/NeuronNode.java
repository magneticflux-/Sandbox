package com.sandbox.neural;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.util.FastMath;

public class NeuronNode extends AbstractNode
{
	public static double sigmoid(final double x)
	{
		return x / (1 + FastMath.abs(x));
		// return 2 / (1 + FastMath.exp(-2 * x)) - 1;
	}

	private List<Double>	weights;
	private boolean			localValidate	= false;
	private double			cache;

	public NeuronNode(final ArrayList<AbstractNode> parents, int layerNumber, NeuralNet network)
	{
		super(new ArrayList<AbstractNode>(parents), layerNumber, network);
		this.weights = new ArrayList<Double>(parents.size());

		for (int i = 0; i < parents.size(); i++)
			this.weights.add(1d);
	}

	@Override
	public void addParent(final AbstractNode n)
	{
		this.parents.add(n);
		this.weights.add(1d);
	}

	@Override
	public double getOutput()
	{
		if (network.getGlobalValidate() == this.localValidate)
		{
			double sum = 0;
			for (int i = 0; i < this.parents.size(); i++)
				sum += this.parents.get(i).getOutput() * this.weights.get(i);
			this.cache = NeuronNode.sigmoid(sum);
			this.localValidate = !this.localValidate;
		}
		return this.cache;
	}

	public List<Double> getWeights()
	{
		return this.weights;
	}

	@Override
	public void randomizeWeights(final Random rng, final double range)
	{
		for (int i = 0; i < this.weights.size(); i++)
			this.weights.set(i, range * 2 * (rng.nextDouble() - 0.5));
	}

	public void setWeights(final List<Double> weights)
	{
		this.weights = weights;
	}

	@Override
	public String toString()
	{
		return "Neuron @ " + this.layerNumber;
	}

	public String detailedToString()
	{
		return this.toString() + " has weights of " + this.weights;
	}
}
