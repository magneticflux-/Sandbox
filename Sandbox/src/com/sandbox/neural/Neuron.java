package com.sandbox.neural;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Neuron implements HasOutput
{
	private List<Double>	weights	= new ArrayList<Double>();
	private List<Neuron>	inputs	= new ArrayList<Neuron>();
	private Double			threshold;

	public Neuron()
	{
	}

	public Neuron(List<Neuron> inputs)
	{
		for (Neuron n : inputs)
		{
			inputs.add(n);
			weights.add(1d); // Default initializer
		}
		threshold = new Double(-1);
	}

	public void randomizeWeights(double min, double max)
	{
		Random r = new Random();
		for (int i = 0; i < weights.size(); i++)
		{
			weights.set(i, (r.nextDouble() * (max - min)) + min);
		}
	}

	public List<Neuron> getInputs()
	{
		return inputs;
	}

	public List<Double> getWeights()
	{
		return weights;
	}

	public Double getThreshold()
	{
		return threshold;
	}

	public Double getOutput()
	{
		double sum = 0;
		Iterator<Neuron> i1 = inputs.iterator();
		Iterator<Double> i2 = weights.iterator();

		while (i1.hasNext() && i2.hasNext())
		{
			sum += i1.next().getOutput() * i2.next();
		}
		sum -= threshold;

		return sum;
	}
}