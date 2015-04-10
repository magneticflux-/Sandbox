package com.sandbox.neural;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Neuron implements HasOutput
{
	private ArrayList<Double>		weights	= new ArrayList<Double>();
	private ArrayList<HasOutput>	inputs	= new ArrayList<HasOutput>();
	private Double					bias;
	private final Random			r		= new Random();

	public Neuron(List<HasOutput> inputs)
	{
		for (HasOutput n : inputs)
		{
			this.inputs.add(n);
			weights.add(1d); // Default initializer
		}
		bias = new Double(1);
	}

	public void randomizeWeights(double min, double max)
	{
		for (int i = 0; i < weights.size(); i++)
		{
			weights.set(i, (r.nextDouble() * (max - min)) + min);
		}
	}

	public void randomizeBias(double min, double max)
	{
		bias = (r.nextDouble() * (max - min)) + min;
	}

	public List<HasOutput> getInputs()
	{
		return inputs;
	}

	public List<Double> getWeights()
	{
		return weights;
	}

	public Double getThreshold()
	{
		return bias;
	}

	public Double getOutput()
	{
		double sum = 0;
		Iterator<HasOutput> i1 = inputs.iterator();
		Iterator<Double> i2 = weights.iterator();

		while (i1.hasNext() && i2.hasNext())
		{
			sum += i1.next().getOutput() * i2.next();
		}

		// return 1 / (Math.pow(10, -1) + Math.exp(-sum - bias));
		return sum * bias;
	}

	public double getBias()
	{
		return bias;
	}

	public void setBias(double bias)
	{
		this.bias = bias;
	}

	@Override
	public String toString()
	{
		return "This neuron has weights of " + this.weights;
	}

	public void setWeight(int index, double d)
	{
		weights.set(index, d);
	}

	public void setAllWeight(double d)
	{
		for (int i = 0; i < weights.size(); i++)
		{
			weights.set(i, d);
		}
	}

	public double getWeight(int index)
	{
		return weights.get(index);
	}
}