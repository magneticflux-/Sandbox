package com.sandbox.neural;

import java.util.ArrayList;
import java.util.List;

public class NeuronNode extends AbstractNode
{
	public static double sigmoid(double x)
	{
		return 1 / (1 + Math.exp(-x));
	}

	public static double tanh(double x)
	{
		return Math.tanh(x);
	}

	private List<Double>	weights;

	public NeuronNode(ArrayList<AbstractNode> parents)
	{
		this.parents = parents;
		this.weights = new ArrayList<Double>(parents.size());

		for (int i = 0; i < parents.size(); i++)
		{
			weights.add(1d);
		}
	}

	public List<Double> getWeights()
	{
		return weights;
	}

	public void setWeights(List<Double> weights)
	{
		this.weights = weights;
	}

	@Override
	public double getOutput()
	{
		double sum = 0;
		for (int i = 0; i < parents.size(); i++)
		{
			sum += parents.get(i).getOutput() * weights.get(i);
		}
		return NeuronNode.tanh(sum);
	}

}
