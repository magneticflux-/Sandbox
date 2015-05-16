package com.sandbox.neural;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

import org.apache.commons.math3.util.FastMath;

public class NeuronNode extends AbstractNode
{
	public static double sigmoid(final double x)
	{
		return 2 / (1 + FastMath.exp(-2 * x)) - 1;
	}

	private List<Double>	weights;

	public NeuronNode(final ArrayList<AbstractNode> parents)
	{
		this.parents = new ArrayList<AbstractNode>(parents);
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
		double sum = 0;
		for (int i = 0; i < this.parents.size(); i++)
			sum += this.parents.get(i).getOutput() * this.weights.get(i);
		return NeuronNode.sigmoid(sum);
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
		return "Neuron";
	}

	public String detailedToString()
	{
		return "Neuron has weights of " + this.weights;
	}
}
