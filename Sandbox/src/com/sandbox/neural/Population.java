package com.sandbox.neural;

import java.util.ArrayList;
import java.util.TreeMap;

public class Population
{
	ArrayList<Network>	networks;
	double[]			actual;

	public Population(int size, String layout, InputValue[] input, double[] actual)
	{
		networks = new ArrayList<Network>(size);
		this.actual = actual;

		for (int i = 0; i < size; i++)
		{
			networks.add(new Network(layout, input));
		}
	}

	public void viewStatus()
	{
		for (Network n : networks)
		{
			n.evalNet();
			System.out.println("Fitness of network: " + compareOutput(n.getOutputs()));
			System.out.println();
		}
	}

	public double compareOutput(double[] output)
	{
		double difference = 0;

		for (int i = 0; i < output.length; i++)
		{
			difference += Math.abs(output[i] - actual[i]);
		}

		return 1 / (0.001 + difference);
	}

	public double compareAndChangeOutput(Network n)
	{
		double sum = 0;
		
		
		
		return sum;
	}

	public Network[] select(int num)
	{
		Network[] victors = new Network[num];

		double sum = 0;
		TreeMap<Double, Network> wheel = new TreeMap<Double, Network>();

		for (Network n : networks)
		{
			wheel.put(sum, n);
			sum += this.compareOutput(n.getOutputs());
		}

		for (int i = 0; i < victors.length; i++)
		{
			victors[i] = wheel.floorEntry(Math.random() * sum).getValue();
		}

		return victors;
	}
}
