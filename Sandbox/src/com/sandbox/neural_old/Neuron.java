package com.sandbox.neural_old;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Random;

public class Neuron implements HasOutput
{
	private Double						bias;
	private final ArrayList<HasOutput>	inputs	= new ArrayList<HasOutput>();
	private final Random				r;
	private final ArrayList<Double>		weights	= new ArrayList<Double>();

	public Neuron(final List<HasOutput> inputs, final Random r)
	{
		this.r = r;
		for (final HasOutput n : inputs)
		{
			this.inputs.add(n);
			this.weights.add(1d); // Default initializer
		}
		this.bias = new Double(1);
	}

	public double getBias()
	{
		return this.bias;
	}

	public List<HasOutput> getInputs()
	{
		return this.inputs;
	}

	@Override
	public Double getOutput()
	{
		double sum = 0;
		final Iterator<HasOutput> i1 = this.inputs.iterator();
		final Iterator<Double> i2 = this.weights.iterator();

		while (i1.hasNext() && i2.hasNext())
			sum += i1.next().getOutput() * i2.next();

		// return 1 / (Math.pow(10, -1) + Math.exp(-sum - bias));
		// return 2 / (1 + Math.exp(sum + this.bias)) - 1;
		return Math.tanh(sum - this.bias);
		// return sum + bias;
	}

	public Double getThreshold()
	{
		return this.bias;
	}

	public double getWeight(final int index)
	{
		return this.weights.get(index);
	}

	public List<Double> getWeights()
	{
		return this.weights;
	}

	public void mutateWeight(final int index)
	{
		this.weights.set(index, this.weights.get(index) * (1 + Config.MUTATION_SIZE * 2 * (this.r.nextDouble() - 0.5)) + (this.r.nextDouble() - 0.5) * 2);
	}

	public void randomizeBias(final double min, final double max)
	{
		this.bias = this.r.nextDouble() * (max - min) + min;
	}

	public void randomizeWeights(final double min, final double max)
	{
		for (int i = 0; i < this.weights.size(); i++)
			this.weights.set(i, this.r.nextDouble() * (max - min) + min);
	}

	public void setAllWeight(final double d)
	{
		for (int i = 0; i < this.weights.size(); i++)
			this.weights.set(i, d);
	}

	public void setBias(final double bias)
	{
		this.bias = bias;
	}

	public void setWeight(final int index, final double d)
	{
		this.weights.set(index, d);
	}

	@Override
	public String toString()
	{
		return "[Weights=" + this.weights + "; Bias=" + this.bias + "]";
	}
}