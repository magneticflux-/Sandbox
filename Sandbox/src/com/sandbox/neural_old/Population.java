package com.sandbox.neural_old;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Random;
import java.util.TreeMap;

public class Population
{
	public static double compareOutput(final double[] output, final double[] actual)
	{
		double difference = 0;

		for (int i = 0; i < output.length; i++)
			difference += Math.abs(output[i] - actual[i]);

		return 1 / (0.001 + difference);
	}

	double[]			actual;
	ArrayList<Network>	networks;

	Random				r;

	public Population(final int size, final String layout, final InputValue[] input, final double[] actual, final Random r)
	{
		this.r = r;
		this.networks = new ArrayList<Network>(size);
		this.actual = actual;

		for (int i = 0; i < size; i++)
			this.networks.add(new Network(layout, input, r));
	}

	public void addNetwork(final Network n)
	{
		n.setRandom(this.r);
		this.networks.add(n);
	}

	public Network[] select(final int num)
	{
		// this.updateFitness();
		final Network[] victors = new Network[num];

		double sum = 0;
		final TreeMap<Double, Network> wheel = new TreeMap<Double, Network>();

		for (final Network n : this.networks)
		{
			wheel.put(sum, n);
			sum += n.getFitness();
		}

		for (int i = 0; i < victors.length; i++)
			victors[i] = wheel.floorEntry(this.r.nextDouble() * sum).getValue();

		return victors;
	}

	public Network[] selectBest(final int num)
	{
		// this.updateFitness();
		final Network[] victors = new Network[num];

		final ArrayList<Network> sorted = new ArrayList<Network>(this.networks);
		Collections.sort(sorted, new Comparator<Network>()
		{
			@Override
			public int compare(final Network o1, final Network o2)
			{
				return -new Double(o1.getFitness()).compareTo(new Double(o2.getFitness()));
			}
		});

		for (int i = 0; i < victors.length; i++)
			victors[i] = sorted.get(i);

		return victors;
	}

	public void updateFitness()
	{
		final double[][] cases = new double[Config.FITNESS_SAMPLE_SIZE][2];
		final double[][] solutions = new double[Config.FITNESS_SAMPLE_SIZE][1];
		for (int i = 0; i < cases.length; i++)
		{
			cases[i] = new double[] { Config.FITNESS_SAMPLE_RANGE * (this.r.nextDouble() - 0.5), Config.FITNESS_SAMPLE_RANGE * (this.r.nextDouble() - 0.5) };
			solutions[i] = new double[] { Config.operation(cases[i][0], cases[i][1]) };
		}

		for (final Network n : this.networks)
		{
			double sum = 0;
			for (int i = 0; i < cases.length; i++)
			{
				n.setEnvironment(cases[i]);
				sum += Population.compareOutput(n.getOutputs(), solutions[i]);
			}
			n.setFitness(sum / Config.FITNESS_SAMPLE_SIZE);
		}
	}

	public void viewStatus()
	{
		this.updateFitness();
		for (final Network n : this.networks)
		{
			n.evalNet();
			System.out.println("Fitness of network: " + n.getFitness());
			System.out.println();
		}
	}
}
