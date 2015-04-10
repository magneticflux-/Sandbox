package com.sandbox.neural;

import java.util.ArrayList;

public class Population
{
	ArrayList<Network>	networks;
	double[]			actual;

	public Population(int size, String layout, double[] input, double[] actual)
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
			System.out.println("Fitness of network: " + compareOutput(n.getOutputs(), this.actual));
			System.out.println();
		}
	}

	public static double compareOutput(double[] output, double[] actual)
	{
		double difference = 0;

		for (int i = 0; i < output.length; i++)
		{
			difference += Math.abs(output[i] - actual[i]);
		}

		return 1 / (0.1 + difference);
	}
}
