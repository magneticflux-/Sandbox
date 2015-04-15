package com.sandbox.neural;

import java.util.ArrayList;
import java.util.List;

public class NeuronNode extends AbstractNode
{
	public static double sigmoid(final double x)
	{
		return 1 / (1 + Math.exp(-x));
	}

	public static double tanh(final double x)
	{
		return Math.tanh(x);
	}

	private List<Double>	weights;

	public NeuronNode(final ArrayList<AbstractNode> parents)
	{
		this.parents = parents;
		this.weights = new ArrayList<Double>(parents.size());

		for (int i = 0; i < parents.size(); i++)
			this.weights.add(1d);
	}

	@Override
	public double getOutput()
	{
		double sum = 0;
		for (int i = 0; i < this.parents.size(); i++)
			sum += this.parents.get(i).getOutput() * this.weights.get(i);
		return NeuronNode.tanh(sum);
	}

	public List<Double> getWeights()
	{
		return this.weights;
	}

	public void setWeights(final List<Double> weights)
	{
		this.weights = weights;
	}

	@Override
	public String toString()
	{
		return "Neuron has weights of " + this.weights;
	}
}
